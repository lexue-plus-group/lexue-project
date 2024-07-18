package com.leguan.content.service;


import com.leguan.content.model.dto.SaveTeachPlanDto;
import com.leguan.content.model.dto.TeachPlanDto;

import java.util.List;

/**
 * @description 课程计划管理接口
 */

public interface TeachPlanService {

    /**
     * 根据课程id查询课程计划
     * @param courseId 课程计划id
     * @return
     */
    public List<TeachPlanDto> findTeachPlanTree(Long courseId);

    /**
     * 获取该章节已有多少个节点
     * @return
     */
    public int getTeachPlanCount(Long courseId, Long parentId);

    /**
     * 新增/修改/保存课程计划
     * @param saveTeachPlanDto
     */
    public void saveTeachPlan(SaveTeachPlanDto saveTeachPlanDto);

    public void deleteTeachPlan(Long teachPlanId);
}
