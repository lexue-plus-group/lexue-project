package com.leguan.content.service;

import com.leguan.content.model.dto.CourseCategoryTreeDto;
import java.util.List;

/**
 * @description 课程分类管理接口
 */

public interface CourseCategoryService {

    /**
     * @description 课程分类树形结构查询
     */
    public List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
