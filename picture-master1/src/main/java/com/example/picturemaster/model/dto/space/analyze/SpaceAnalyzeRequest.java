package com.example.picturemaster.model.dto.space.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用空间分析请求  查询公共图库和全空间分析都需要管理员权限，要进行权限校验
 */
@Data
public class SpaceAnalyzeRequest implements Serializable {

    /**
     * 空间 ID
     */
    private Long spaceId;

    /**
     * 是否查询公共图库
     */
    private boolean queryPublic;

    /**
     * 全空间分析
     */
    private boolean queryAll;

    private static final long serialVersionUID = 1L;
}
