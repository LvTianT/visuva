package com.example.picturemaster.model.enums;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

@Getter
public enum UserRoleEnum {
    /**
     * 普通用户
     */
    USER("用户", "user"),
    /**
     * 管理员
     */
    ADMIN("管理员", "admin");

    /**
     * 用户角色
     */
    private final String text;

    /**
     * 用户角色描述
     */
    private final String value;

    UserRoleEnum( String text,String value) {
        this.text = text;
        this.value = value;
    }


    /**
     * 根据value获取枚举
     * @param value
     * @return
     */
    public static UserRoleEnum getEnumByValue(String value) {
        if(ObjUtil.isEmpty(value)){
            return null;
        }
        for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
            if (userRoleEnum.getValue().equals(value)) {
                return userRoleEnum;
            }
        }
        return null;
    }
}
