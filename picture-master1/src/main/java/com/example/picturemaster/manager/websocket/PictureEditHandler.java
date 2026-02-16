package com.example.picturemaster.manager.websocket;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.example.picturemaster.manager.websocket.disruptor.PictureEditEventProducer;
import com.example.picturemaster.manager.websocket.model.PictureEditActionEnum;
import com.example.picturemaster.manager.websocket.model.PictureEditMessageTypeEnum;
import com.example.picturemaster.manager.websocket.model.PictureEditRequestMessage;
import com.example.picturemaster.manager.websocket.model.PictureEditResponseMessage;
import com.example.picturemaster.model.entity.User;
import com.example.picturemaster.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**2.
 * 图片编辑 WebSocket 处理器
 * 业务：前端给服务器发送请求，后端根据这些消息，通知给在同一房间的其他客户端
 */
@Component
@Slf4j
public class PictureEditHandler extends TextWebSocketHandler{

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private PictureEditEventProducer pictureEditEventProducer;


    // 保存当前图片已经在编辑用户的信息： 图片1 ，用户A  图片2，用户B  也是使用ConcurrentHashMap键值对，保证线程安全
    // 每张图片的编辑状态，key: pictureId, value: 当前正在编辑的用户 ID
    private final Map<Long, Long> pictureEditingUsers = new ConcurrentHashMap<>();

    // 保存所有连接的会话，key: pictureId, value: 用户会话集合
    // 注：无论多少个连接，都是调用PictureEditHandler这个类里面相同的以下3个方法，因此一定要使用并发的HashMap，以保证线程安全
    private final Map<Long, Set<WebSocketSession>> pictureSessions = new ConcurrentHashMap<>();

    // 建立连接后
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        // 保存会话到集合中
        User user = (User) session.getAttributes().get("user");
        Long pictureId = (Long) session.getAttributes().get("pictureId");
        // 如果是第一次加入会话，初始化一个空的集合
        pictureSessions.putIfAbsent(pictureId, ConcurrentHashMap.newKeySet());
        pictureSessions.get(pictureId).add(session);
        // 构造响应，发送加入编辑的消息通知
        PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
        pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.INFO.getValue());
        String message = String.format("%s加入协作", user.getUserName());
        pictureEditResponseMessage.setMessage(message);
        pictureEditResponseMessage.setUser(userService.getUserVO(user));
        // 广播给所有用户
        broadcastToPicture(pictureId, pictureEditResponseMessage);
    }

    // 异步处理：收到客户端的TextMessage消息后要把消息放入Disruptor队列中，然后立即返回请求响应，WebSocket可以继续接收更多的请求
    // 一个线程用于接收请求，一个线程用于处理消息（事件），后台的消费者PictureEditEventWorkHandler会使用额外的线程默默处理任务
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        // 获取消息内容，将JSON转换为PictureEditRequestMessage
        PictureEditRequestMessage pictureEditRequestMessage = JSONUtil.toBean(message.getPayload(), PictureEditRequestMessage.class);
        // 从消息的枚举类转化成对应的消息类型
        PictureEditMessageTypeEnum pictureEditMessageTypeEnum = PictureEditMessageTypeEnum.getEnumByValue(pictureEditRequestMessage.getType());

        // 从WebSocketSession会话中获取存入的user信息和图片Id
        User user = (User) session.getAttributes().get("user");
        Long pictureId = (Long) session.getAttributes().get("pictureId");

        // 立刻提交事件到Disruptor中，请求就返回了
        pictureEditEventProducer.publishEvent(pictureEditRequestMessage, session, user, pictureId);

    }


    /**
     * 进入编辑状态
     *
     * @param pictureEditRequestMessage
     * @param session
     * @param user
     * @param pictureId
     */
    public void handleEnterEditMessage(PictureEditRequestMessage pictureEditRequestMessage, WebSocketSession session, User user, Long pictureId) throws IOException {
        // 检查当前图片编辑列表是否存在该图片Id：有，则表示已经有人在编辑；没有，才能进入该图片进行编辑
        // pictureEditingUsers集合类型： key: pictureId, value: 当前正在编辑的用户 ID
        if (!pictureEditingUsers.containsKey(pictureId)) {
            // 存储当前正在编辑图片的PictureId和UserId键值对
            pictureEditingUsers.put(pictureId, user.getId());
            // 构造响应，发送加入编辑的消息通知
            PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
            pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.ENTER_EDIT.getValue());
            String message = String.format("%s开始编辑图片", user.getUserName());
            pictureEditResponseMessage.setMessage(message);
            pictureEditResponseMessage.setUser(userService.getUserVO(user));
            // 广播给所有用户编辑图片的消息
            broadcastToPicture(pictureId, pictureEditResponseMessage);
        }
    }
    /**
     * 处理编辑操作
     *
     *
     * @param pictureEditRequestMessage
     * @param session
     * @param user
     * @param pictureId
     */
    public void handleEditActionMessage(PictureEditRequestMessage pictureEditRequestMessage, WebSocketSession session, User user, Long pictureId) throws IOException {
        // 判断是否是当前图片Id正在编辑的用户
        Long editingUserId = pictureEditingUsers.get(pictureId);
        // 获取编辑动作
        String editAction = pictureEditRequestMessage.getEditAction();
        PictureEditActionEnum actionEnum = PictureEditActionEnum.getEnumByValue(editAction);
        if (actionEnum == null) {
            log.error("无效的编辑动作");
            return;
        }
        // 确认是当前的编辑者
        if (editingUserId != null && editingUserId.equals(user.getId())) {
            // 构造响应，发送具体操作的通知
            PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
            pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.EDIT_ACTION.getValue());
            String message = String.format("%s 执行 %s", user.getUserName(), actionEnum.getText());
            pictureEditResponseMessage.setMessage(message);
            pictureEditResponseMessage.setEditAction(editAction);
            pictureEditResponseMessage.setUser(userService.getUserVO(user));
            // 广播给除了当前客户端之外的其他用户，否则会造成重复编辑
            broadcastToPicture(pictureId, pictureEditResponseMessage, session);
        }
    }

    /**
     * 退出编辑状态
     *
     * @param pictureEditRequestMessage
     * @param session
     * @param user
     * @param pictureId
     */
    public void handleExitEditMessage(PictureEditRequestMessage pictureEditRequestMessage, WebSocketSession session, User user, Long pictureId) throws IOException {
        // 判断是否是当前图片Id正在编辑的用户
        Long editingUserId = pictureEditingUsers.get(pictureId);
        // 确认是当前的编辑者
        if (editingUserId != null && editingUserId.equals(user.getId())) {
            // 移除用户正在编辑该图片
            pictureEditingUsers.remove(pictureId);
            // 构造响应，发送退出编辑的消息通知
            PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
            pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.EXIT_EDIT.getValue());
            String message = String.format("%s退出编辑图片", user.getUserName());
            pictureEditResponseMessage.setMessage(message);
            pictureEditResponseMessage.setUser(userService.getUserVO(user));
            broadcastToPicture(pictureId, pictureEditResponseMessage);
        }
    }


    /**
     * 关闭连接
     *
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 1. 调用父类的方法，完成一些标准的关闭操作（必须要有的一步）
        super.afterConnectionClosed(session, status);

        // 2. 从会话的“背包”（属性）里拿出之前握手时存进去的数据
        // 拿出当前是哪个用户在操作
        User user = (User) session.getAttributes().get("user");
        // 拿出当前正在编辑哪张图片的 ID
        Long pictureId = (Long) session.getAttributes().get("pictureId");

        // 3. 让用户退出该图的编辑
        // 调用专门的方法来更新状态，比如告诉系统“这个用户不再编辑这张图了”
        handleExitEditMessage(null, session, user, pictureId);

        // 4. 清理会话列表（核心清理步骤）
        // 找到这张图片对应的所有在线会话集合
        Set<WebSocketSession> sessionSet = pictureSessions.get(pictureId);

        // 如果会话集合存在（确实有人在编辑这张图）
        if (sessionSet != null) {
            // 从会话集合中把刚才断开连接的那个会话删掉
            sessionSet.remove(session);

            // 如果删完之后，发现这张图没人在线了（集合空了）
            if (sessionSet.isEmpty()) {
                // 就把这张图片的记录也从大列表里删掉，节省内存
                pictureSessions.remove(pictureId);
            }
        }

        // 5. 广播通知其他人（消息推送步骤）
        // 创建一个消息对象，准备发给还在编辑这张图的其他人
        PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();

        // 设置消息类型为“提示信息”（比如前端收到后会弹个小窗或显示一行字）
        pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.INFO.getValue());

        // 拼接提示语的内容：例如 "用户 张三 离开编辑"
        String message = String.format("%s离开编辑", user.getUserName());
        pictureEditResponseMessage.setMessage(message);

        // 把用户信息也塞进消息里（比如头像、昵称），方便前端显示是谁走了
        pictureEditResponseMessage.setUser(userService.getUserVO(user));

        // 调用广播方法，把这条消息发给所有还在看这张图的人
        broadcastToPicture(pictureId, pictureEditResponseMessage);
    }



    /**
     * 广播给该图片的所有用户（支持排除掉某个 Session）
     *
     * @param pictureId
     * @param pictureEditResponseMessage
     * @param excludeSession
     */
    private void broadcastToPicture(Long pictureId, PictureEditResponseMessage pictureEditResponseMessage, WebSocketSession excludeSession) throws IOException {
         // 根据图片Id拿到当前图片的所有会话集合
         Set<WebSocketSession> sessionSet = pictureSessions.get(pictureId);
         // 集合不为空，才能广播
         if (CollUtil.isNotEmpty(sessionSet)) {
             // 创建 ObjectMapper
             ObjectMapper objectMapper = new ObjectMapper();
             // 配置序列化：将 Long 类型转为 String，解决丢失精度问题
             SimpleModule module = new SimpleModule();
             module.addSerializer(Long.class, ToStringSerializer.instance);
             module.addSerializer(Long.TYPE, ToStringSerializer.instance); // 支持 long 基本类型
             objectMapper.registerModule(module);
             // 序列化为 JSON 字符串
             String message = objectMapper.writeValueAsString(pictureEditResponseMessage);
             TextMessage textMessage = new TextMessage(message);

             // 遍历会话集合的每个Session
             for (WebSocketSession session : sessionSet) {
                 // 排除掉的 session 不发送
                 if (excludeSession != null && session.equals(excludeSession)) {
                     continue;
                 }
                 if (session.isOpen()) {
                     session.sendMessage(textMessage);
                 }
             }
         }
     }

    /**
     * 广播给该图片的所有用户
     *
     * @param pictureId
     * @param pictureEditResponseMessage
     */
    private void broadcastToPicture(Long pictureId, PictureEditResponseMessage pictureEditResponseMessage) throws IOException{
        broadcastToPicture(pictureId, pictureEditResponseMessage, null);
    }

}
