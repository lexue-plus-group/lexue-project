package com.leguan.content.api;

import com.leguan.content.model.dto.BindTeachPlanMediaDto;
import com.leguan.content.model.dto.SaveTeachPlanDto;
import com.leguan.content.model.dto.TeachPlanDto;
import com.leguan.content.model.po.Teachplan;
import com.leguan.content.service.TeachPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description 课程计划管理相关的接口
 */

@Api(value = "课程计划管理接口", tags = "课程计划信息管理接口")
@RestController
public class TeachPlanController {

    @Autowired
    TeachPlanService teachPlanService;

    //查询课程计划
    @ApiOperation("查询课程计划树形结构")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachPlanDto> getTreeNodes(@PathVariable Long courseId) {
        List<TeachPlanDto> teachPlanTree = teachPlanService.findTeachPlanTree(courseId);
        return teachPlanTree;
    }

    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachPlan(@RequestBody SaveTeachPlanDto teachPlan) {
        teachPlanService.saveTeachPlan(teachPlan);
    }

    @ApiOperation("删除课程计划")
    @DeleteMapping("/teachplan/{teachPlanId}")
    public void deleteTeachPlan(@PathVariable Long teachPlanId) {
        teachPlanService.deleteTeachPlan(teachPlanId);
    }

    @ApiOperation("课程计划排序(下移)")
    @PostMapping("/teachplan/movedown/{teachPlanId}")
    public void moveDown(@PathVariable Long teachPlanId) {
        teachPlanService.moveDownPlan(teachPlanId);
    }

    @ApiOperation("课程计划排序(上移)")
    @PostMapping("/teachplan/moveup/{teachPlanId}")
    public void moveUp(@PathVariable Long teachPlanId) {
        teachPlanService.moveUpPlan(teachPlanId);
    }

    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachPlanMediaDto bindTeachPlanMediaDto){
        teachPlanService.associationMedia(bindTeachPlanMediaDto);
    }

    @ApiOperation("课程计划查询")
    @PostMapping("/teachplan/{teachplanId}")
    public Teachplan getTeachplan(@PathVariable Long teachplanId) {
        return teachPlanService.getTeachPlan(teachplanId);
    }
}
