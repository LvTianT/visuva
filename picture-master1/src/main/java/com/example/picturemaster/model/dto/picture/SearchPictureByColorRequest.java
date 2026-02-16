package com.example.picturemaster.model.dto.picture;

import lombok.Data;

/**
 * 颜色搜图请求
 */
@Data
public class SearchPictureByColorRequest {
    /**
     * 图片主色调
     */
    private String picColor;

    /**
     * 空间id
     */
    private Long spaceId;

    private static final long serialVersionUID = 1L;
}
