package com.leguan.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.leguan.base.exception.LexueException;
import com.leguan.content.mapper.TeachplanMapper;
import com.leguan.content.mapper.TeachplanMediaMapper;
import com.leguan.content.model.dto.BindTeachPlanMediaDto;
import com.leguan.content.model.dto.SaveTeachPlanDto;
import com.leguan.content.model.dto.TeachPlanDto;
import com.leguan.content.model.po.Teachplan;
import com.leguan.content.model.po.TeachplanMedia;
import com.leguan.content.service.TeachPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
            LambdaQueryWrapper<TeachplanMedia> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TeachplanMedia::getTeachplanId, teachPlanId);
            teachplanMediaMapper.delete(wrapper);
        } else {
            LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Teachplan::getParentid, teachPlanId);
            Integer count = teachplanMapper.selectCount(wrapper);
            if (count != 0) {
                LexueException.cast("课程计划信息还有子级信息，无法操作");
            }
            teachplanMapper.deleteById(teachPlanId);
        }
    }

    @Override
    public void swapTeachPlanOrderBy(Teachplan teachplan, Teachplan otherTeachplan) {
        Integer otherOrderBy = otherTeachplan.getOrderby();
        otherTeachplan.setOrderby(teachplan.getOrderby());
        teachplan.setOrderby(otherOrderBy);
        teachplanMapper.updateById(teachplan);
        teachplanMapper.updateById(otherTeachplan);
    }

    @Override
    public void moveDownPlan(Long teachPlanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachPlanId);
        Teachplan nextTeachplan = null;
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        Integer grade = teachplan.getGrade();
        Long parentId = teachplan.getParentid();
        Integer orderBy = teachplan.getOrderby();
        if (grade == 2) {
            wrapper = wrapper.eq(Teachplan::getParentid, parentId).eq(Teachplan::getOrderby, orderBy + 1);
            nextTeachplan = teachplanMapper.selectOne(wrapper);
        } else {
            Long courseId = teachplan.getCourseId();
            wrapper = wrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentId).eq(Teachplan::getOrderby, orderBy + 1);
            nextTeachplan = teachplanMapper.selectOne(wrapper);
        }
        if (nextTeachplan == null) {
            return;
        }
        swapTeachPlanOrderBy(teachplan, nextTeachplan);
    }

    @Override
    public void moveUpPlan(Long teachPlanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachPlanId);
        Teachplan preTeachplan = null;
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        Integer grade = teachplan.getGrade();
        Long parentId = teachplan.getParentid();
        Integer orderBy = teachplan.getOrderby();
        if (grade == 2) {
            wrapper = wrapper.eq(Teachplan::getParentid, parentId).eq(Teachplan::getOrderby, orderBy - 1);
            preTeachplan = teachplanMapper.selectOne(wrapper);
        } else {
            Long courseId = teachplan.getCourseId();
            wrapper = wrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentId).eq(Teachplan::getOrderby, orderBy - 1);
            preTeachplan = teachplanMapper.selectOne(wrapper);
        }
        if (preTeachplan == null) {
            return;
        }
        swapTeachPlanOrderBy(teachplan, preTeachplan);
    }

    @Override
    public List<Teachplan> getTeachPlanList(Long courseId) {
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getCourseId, courseId);
        List<Teachplan> teachPlans = teachplanMapper.selectList(wrapper);
        return teachPlans;
    }

    @Override
    public List<TeachplanMedia> getTeachPlanMediaList(Long courseId) {
        LambdaQueryWrapper<TeachplanMedia> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeachplanMedia::getCourseId, courseId);
        List<TeachplanMedia> teachplanMediaList = teachplanMediaMapper.selectList(wrapper);
        return teachplanMediaList;
    }

    @Override
    public void deleteTeachPlanMedia(Long courseId) {
        LambdaQueryWrapper<TeachplanMedia> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeachplanMedia::getCourseId, courseId);
        teachplanMediaMapper.delete(wrapper);
    }

    @Transactional
    @Override
    public TeachplanMedia associationMedia(BindTeachPlanMediaDto bindTeachPlanMediaDto) {
        //教学计划id
        Long teachplanId = bindTeachPlanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if (teachplan == null) {
            LexueException.cast("教学计划不存在");
        }
        Integer grade = teachplan.getGrade();
        if (grade != 2) {
            LexueException.cast("只允许第二级教学计划绑定媒资文件");
        }
        //课程id
        Long courseId = teachplan.getCourseId();

        //先删除原来该教学计划绑定的媒资
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId, teachplanId));

        //再添加教学计划与媒资的绑定关系
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setCourseId(courseId);
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaFilename(bindTeachPlanMediaDto.getFileName());
        teachplanMedia.setMediaId(bindTeachPlanMediaDto.getMediaId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);
        return teachplanMedia;

    }

    @Override
    public Teachplan getTeachPlan(Long teachplanId) {
        return teachplanMapper.selectById(teachplanId);
    }
}
