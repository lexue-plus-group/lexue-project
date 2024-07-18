package com.leguan.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.leguan.base.exception.LexueException;
import com.leguan.content.mapper.TeachplanMapper;
import com.leguan.content.mapper.TeachplanMediaMapper;
import com.leguan.content.model.dto.SaveTeachPlanDto;
import com.leguan.content.model.dto.TeachPlanDto;
import com.leguan.content.model.po.Teachplan;
import com.leguan.content.service.TeachPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TeachPlanServiceImpl implements TeachPlanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachPlanDto> findTeachPlanTree(Long courseId) {
        List<TeachPlanDto> teachPlanDtos = teachplanMapper.selectTreeNodes(courseId);
        return teachPlanDtos;
    }

    public int getTeachPlanCount(Long courseId, Long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper = queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count + 1;
    }

    @Override
    public void saveTeachPlan(SaveTeachPlanDto saveTeachPlanDto) {
        //通过课程计划id判断是新增还是修改
        Long teachPlanId = saveTeachPlanDto.getId();
        if (teachPlanId == null) {
            //新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachPlanDto, teachplan);
            //确定排序字段，找到它的同级节点个数，排序字段就是个数加1
            Long parentid = saveTeachPlanDto.getParentid();
            Long courseId = saveTeachPlanDto.getCourseId();
            int teachPlanCount = getTeachPlanCount(courseId, parentid);
            teachplan.setOrderby(teachPlanCount);

            teachplanMapper.insert(teachplan);
        } else {
            //修改
            Teachplan teachplan = teachplanMapper.selectById(teachPlanId);
            BeanUtils.copyProperties(saveTeachPlanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }

    @Override
    public void deleteTeachPlan(Long teachPlanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachPlanId);
        Integer grade = teachplan.getGrade();
        if (grade == 2) {
            teachplanMapper.deleteById(teachPlanId);
            teachplanMediaMapper.deleteById(teachPlanId);
        } else {
            Long courseId = teachplan.getCourseId();
            List<TeachPlanDto> teachPlanDtoList = teachplanMapper.selectTreeNodes(courseId);
            if (teachPlanDtoList != null) {
                LexueException.cast("课程计划信息还有子级信息，无法操作");
            }
            teachplanMapper.deleteById(teachPlanId);
        }
    }
}
