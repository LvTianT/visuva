package com.example.picturemaster.manager.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 前端传来message的JSON，先转换成PictureEditRequestMessage，获取消息的类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PictureEditRequestMessage {

    /**
     * 消息类型，例如 "ENTER_EDIT", "EXIT_EDIT", "EDIT_ACTION" 进入编辑状态、退出编辑状态、执行编辑操作
     */
    private String type;

    /**
     * 执行的编辑动作（放大、缩小）
     */
    private String editAction;
}