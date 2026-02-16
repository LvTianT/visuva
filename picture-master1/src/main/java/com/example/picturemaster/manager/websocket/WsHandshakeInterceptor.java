package com.example.picturemaster.manager.websocket;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.example.picturemaster.manager.auth.SpaceUserAuthManager;
import com.example.picturemaster.manager.auth.model.SpaceUserPermissionConstant;
import com.example.picturemaster.model.entity.Picture;
import com.example.picturemaster.model.entity.Space;
import com.example.picturemaster.model.entity.User;
import com.example.picturemaster.model.enums.SpaceTypeEnum;
import com.example.picturemaster.service.PictureService;
import com.example.picturemaster.service.SpaceService;
import com.example.picturemaster.service.SpaceUserService;
import com.example.picturemaster.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/** 1.
 * WsHandshakeInterceptor拦截器：用于拦截客户端的握手请求，执行鉴权。如果鉴权通过，握手继续
 * Websocket握手流程：客户端发起WebSocket握手请求，服务器端的DispatcherServlet接收到请求，WsHandshakeInterceptor拦截请求，进行鉴权，若鉴权通过，握手继续。
 */
@Slf4j
@Component
public class WsHandshakeInterceptor implements HandshakeInterceptor {

    @Resource
    UserService userService;

    @Resource
    private PictureService pictureService;

    @Resource
    private SpaceService spaceService;

    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;


    /**
     * 建立连接前要先鉴权
     *
     * @param request
     * @param response
     * @param wsHandler
     * @param attributes 属性的作用：给session会话设置一些用户信息，存放user信息和PictureId用于封装后续要返回的图片编辑响应消息pictureEditResponseMessage
     * @return
     * @throws Exception
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if(request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpServletRequest= servletRequest.getServletRequest();
            // getParameter主要用于获取 URL 问号后面的查询参数，多少个参数都统一用getParameter获取
            // 从请求中获取参数：图片Id，进行参数校验，图片ID必须不为空
            String pictureId = httpServletRequest.getParameter("pictureId");
            if(StrUtil.isBlank(pictureId)){
                log.error("缺少图片参数，拒绝握手");
                return false;
            }
            // 获取当前登录用户
            User loginUser = userService.getLoginUser(httpServletRequest);
            if(ObjUtil.isEmpty(loginUser)){
                log.error("未登录用户，拒绝握手");
                return false;
            }

            // 校验用户是否有当前空间编辑图片的权限
            Picture picture =   pictureService.getById(pictureId);
            if(ObjUtil.isEmpty(picture)){
                log.error("图片不存在，拒绝握手");
                return false;
            }

            Long spaceId = picture.getSpaceId();
            Space space = null;
            if(spaceId != null){
                space = spaceService.getById(spaceId);
                if(ObjUtil.isEmpty(space)){
                    log.error("空间不存在，拒绝握手");
                    return false;
                }
                // 判断空间类型是不是团队空间
                if(space.getSpaceType() != SpaceTypeEnum.TEAM.getValue()){
                    log.error("图片所在空间不是团队空间，拒绝握手");
                    return false;
                }
            }

            List<String>  permissionList =  spaceUserAuthManager.getPermissionList(space,loginUser);
            if(!permissionList.contains(SpaceUserPermissionConstant.PICTURE_EDIT)){
                log.error("用户没有编辑图片权限，拒绝握手");
                return false;
            }

            // 设置用户登录信息等属性到 WebSocket 会话中  =  Map<String, Object> attributes 插入键值对
            // 设置属性：用户、用户Id、图片Id
            attributes.put("user",loginUser);
            attributes.put("userId",loginUser.getId());
            //必须传图片Id，记得转化为long类型
            attributes.put("pictureId",Long.valueOf(pictureId));

        }

        return true;
    }


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
