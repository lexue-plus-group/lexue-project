package com.leguan.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @description 分页查询的参数
 */

@Data
@ToString
public class PageParams {

    //当前页码
    @ApiModelProperty(value = "页码")
    private Long pageNo = 1L;
    //每页显示记录数
    @ApiModelProperty(value = "每页显示记录数")
    private Long pageSize = 30L;

    public PageParams() {
    }

    public PageParams(Long pageNo, Long pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}
