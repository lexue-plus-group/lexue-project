package com.leguan.content;

import com.leguan.content.mapper.CourseCategoryMapper;
import com.leguan.content.mapper.TeachplanMapper;
import com.leguan.content.model.dto.CourseCategoryTreeDto;
import com.leguan.content.model.dto.TeachPlanDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
public class TeachPlanMapperTests {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Test
    public void testSelectTreeNodes() {
        List<TeachPlanDto> teachPlanDtos = teachplanMapper.selectTreeNodes(117L);
        System.out.println(teachPlanDtos);
    }

}
