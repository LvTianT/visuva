package com.example.picturemaster.manager.auth;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.json.JSONUtil;
import com.example.picturemaster.exception.BusinessException;
import com.example.picturemaster.exception.ErrorCode;
import com.example.picturemaster.manager.auth.model.SpaceUserPermissionConstant;
import com.example.picturemaster.model.entity.Picture;
import com.example.picturemaster.model.entity.Space;
import com.example.picturemaster.model.entity.SpaceUser;
import com.example.picturemaster.model.entity.User;
import com.example.picturemaster.model.enums.SpaceRoleEnum;
import com.example.picturemaster.model.enums.SpaceTypeEnum;
import com.example.picturemaster.service.PictureService;
import com.example.picturemaster.service.SpaceService;
import com.example.picturemaster.service.SpaceUserService;
import com.example.picturemaster.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.example.picturemaster.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 自定义权限加载接口实现类，通过url的接口找到对应的id  api/picture对应pictureId
 */
@Component    // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {

    // 设置contextPath值默认是/api
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Resource
    private UserService userService;

    @Resource
    private SpaceService spaceService;

    @Resource
    private SpaceUserService spaceUserService;

    @Resource
    private PictureService pictureService;

    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;

    /**
     * 返回一个账号所拥有的权限码集合，涉及多个查询，
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 判断 loginType，仅对类型为 "space" 进行权限校验
        if (!StpKit.SPACE_TYPE.equals(loginType)) {
            return new ArrayList<>();
        }
        // 管理员权限，表示权限校验通过
        List<String> ADMIN_PERMISSIONS = spaceUserAuthManager.getPermissionsByRole(SpaceRoleEnum.ADMIN.getValue());
        // 获取上下文对象
        SpaceUserAuthContext authContext = getAuthContextByRequest();
        // 如果所有字段都为空，表示查询公共图库，可以通过
        if (isAllFieldsNull(authContext)) {
            return ADMIN_PERMISSIONS;
        }
        // 获取 userId
        User loginUser = (User) StpKit.SPACE.getSessionByLoginId(loginId).get(USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "用户未登录");
        }
        Long userId = loginUser.getId();
        // 优先从上下文中获取 SpaceUser 对象
        SpaceUser spaceUser = authContext.getSpaceUser();
        if (spaceUser != null) {
            return spaceUserAuthManager.getPermissionsByRole(spaceUser.getSpaceRole());
        }
        // 如果有 spaceUserId，必然是团队空间，通过数据库查询 SpaceUser 对象
        Long spaceUserId = authContext.getSpaceUserId();
        if (spaceUserId != null) {
            spaceUser = spaceUserService.getById(spaceUserId);
            if (spaceUser == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到空间用户信息");
            }
            // 取出当前登录用户对应的 spaceUser
            SpaceUser loginSpaceUser = spaceUserService.lambdaQuery()
                    .eq(SpaceUser::getSpaceId, spaceUser.getSpaceId())
                    .eq(SpaceUser::getUserId, userId)
                    .one();
            if (loginSpaceUser == null) {
                return new ArrayList<>();
            }
            // 这里会导致管理员在私有空间没有权限，可以再查一次库处理
            return spaceUserAuthManager.getPermissionsByRole(loginSpaceUser.getSpaceRole());
        }
        // 如果没有 spaceUserId，尝试通过 spaceId 或 pictureId 获取 Space 对象并处理
        Long spaceId = authContext.getSpaceId();
        if (spaceId == null) {
            // 如果没有 spaceId，通过 pictureId 获取 Picture 对象和 Space 对象
            Long pictureId = authContext.getPictureId();
            // 图片 id 也没有，则默认通过权限校验
            if (pictureId == null) {
                return ADMIN_PERMISSIONS;
            }
            Picture picture = pictureService.lambdaQuery()
                    .eq(Picture::getId, pictureId)
                    .select(Picture::getId, Picture::getSpaceId, Picture::getUserId)
                    .one();
            if (picture == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到图片信息");
            }
            spaceId = picture.getSpaceId();
            // 公共图库，仅本人或管理员可操作
            if (spaceId == null) {
                if (picture.getUserId().equals(userId) || userService.isAdmin(loginUser)) {
                    return ADMIN_PERMISSIONS;
                } else {
                    // 不是自己的图片，仅可查看
                    return Collections.singletonList(SpaceUserPermissionConstant.PICTURE_VIEW);
                }
            }
        }
        // 获取 Space 对象
        Space space = spaceService.getById(spaceId);
        if (space == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到空间信息");
        }
        // 根据 Space 类型判断权限
        if (space.getSpaceType() == SpaceTypeEnum.PRIVATE.getValue()) {
            // 私有空间，仅本人或管理员有权限
            if (space.getUserId().equals(userId) || userService.isAdmin(loginUser)) {
                return ADMIN_PERMISSIONS;
            } else {
                return new ArrayList<>();
            }
        } else {
            // 团队空间，查询 SpaceUser 并获取角色和权限
            spaceUser = spaceUserService.lambdaQuery()
                    .eq(SpaceUser::getSpaceId, spaceId)
                    .eq(SpaceUser::getUserId, userId)
                    .one();
            if (spaceUser == null) {
                return new ArrayList<>();
            }
            return spaceUserAuthManager.getPermissionsByRole(spaceUser.getSpaceRole());
        }
    }

    /**
     * 本项目中不使用。返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return new ArrayList<>();
    }

    /**
     * 从请求中获取上下文对象
     */
    private SpaceUserAuthContext getAuthContextByRequest() {
        // HTTP 请求进入 Spring 的 DispatcherServlet 时，Spring 会将当前请求的 ServletRequestAttributes 对象（它包装了 HttpServletRequest 和 HttpServletResponse）存储到 RequestContextHolder 内部的 ThreadLocal 中
        // Controller、Service、Repository 等所有层都可以通过 RequestContextHolder 随时获取到这个绑定的请求对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        //从请求头中拿到请求的类别：GET、POST、PUT、DELETE等，Content-Type 请求头的值可能是 "application/json"
        String contentType = request.getHeader(Header.CONTENT_TYPE.getValue());
        //我们需要的请求上下文对象
        SpaceUserAuthContext authRequest;
        // 获取请求参数 ，判断请求类型是否是JSON对象，即application/json，ContentType 是一个枚举类（Enum），有个常量名为JSON("application/json")
        if (ContentType.JSON.getValue().equals(contentType)) {
            //hutool 工具类，直接根据request获取前端请求体body，拿到JSON字符串
            String body = ServletUtil.getBody(request);
            // 将 JSON 字符串转换为需要的 SpaceUserAuthContext 对象，JSON键值对自动赋值给类里面有的属性，没有的属性忽略
            authRequest = JSONUtil.toBean(body, SpaceUserAuthContext.class);
        } else {
            //get请求一般接在url的后面，多个参数用&符号隔开，xxx.com/user?id=123&name=张三，使用hutool工具类的getParamMap()
            //解析所有参数成一个格式为(Key,Value)的Map集合
            Map<String, String> paramMap = ServletUtil.getParamMap(request);
            //把Map也转化为SpaceUserAuthContext 对象
            authRequest = BeanUtil.toBean(paramMap, SpaceUserAuthContext.class);
        }
        // 根据请求路径区分 id 字段的含义
        Long id = authRequest.getId();
        // 先判断请求对象是否有id
        if (ObjUtil.isNotNull(id)) {
            // 获取到请求路径的业务前缀，获取到完整的URI，即/api/picture/aaa?a=1
            String requestURI = request.getRequestURI();
            // 先用空替换掉上下文/api，剩下的就是前缀：picture/aaa?a=1
            String partURI = requestURI.replace(contextPath + "/", "");
            // 获取前缀的第一个斜杠前的字符串picture
            String moduleName = StrUtil.subBefore(partURI, "/", false);
            //可以根据不同的id类型，判断是哪个模块的id，并赋值给SpaceUserAuthContext对象，用于公共图库、私有图库和团队空间权限校验
            switch (moduleName) {
                case "picture":
                    authRequest.setPictureId(id);
                    break;
                case "spaceUser":
                    authRequest.setSpaceUserId(id);
                    break;
                case "space":
                    authRequest.setSpaceId(id);
                    break;
                default:
            }
        }
        return authRequest;
    }

    /**
     * 判断对象的所有字段是否为空
     *
     * @param object
     * @return
     */
    private boolean isAllFieldsNull(Object object) {
        if (object == null) {
            return true; // 对象本身为空
        }
        // 获取所有字段并判断是否所有字段都为空
        return Arrays.stream(ReflectUtil.getFields(object.getClass()))
                // 获取字段值
                .map(field -> ReflectUtil.getFieldValue(object, field))
                // 检查是否所有字段都为空
                .allMatch(ObjectUtil::isEmpty);
    }
}
