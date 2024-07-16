package com.leguan.content.service;

import com.leguan.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

public interface CourseCategoryService {

    /**
     * 课程分类树形结构查询
     * @return
     */
    public List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
