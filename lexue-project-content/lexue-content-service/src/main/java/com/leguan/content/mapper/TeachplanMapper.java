package com.leguan.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leguan.content.model.dto.TeachPlanDto;
import com.leguan.content.model.po.Teachplan;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author leguan
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {

    //课程计划查询
    public List<TeachPlanDto> selectTreeNodes(Long courseId);
}
