package com.example.picturemaster.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 已登录用户视图脱敏
 */
@TableName(value ="user")
@Data
@NoArgsConstructor          //  生成无参构造函数
@AllArgsConstructor       //  生成全参构造函数
public class LoginUserVO implements Serializable {

    // 必须有这个构造函数！
    public LoginUserVO(String token, Long id, String username) {
        this.token = token;
        this.id = id;
        this.userName = username;
    }
    private String token;
    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}