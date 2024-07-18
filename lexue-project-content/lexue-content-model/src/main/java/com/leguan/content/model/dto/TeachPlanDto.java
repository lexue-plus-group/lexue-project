package com.leguan.content.model.dto;

import com.leguan.content.model.po.Teachplan;
import com.leguan.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class TeachPlanDto extends Teachplan {

    //与媒体资源关联的表
    private TeachplanMedia teachplanMedia;

    //小章节list（子节点）
    private List<TeachPlanDto> teachPlanTreeNodes;

}
