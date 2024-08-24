package com.leguan.orders.model.dto;

import com.leguan.orders.model.po.TbVoucher;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VoucherDto extends TbVoucher {

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 生效时间
     */
    private LocalDateTime beginTime;

    /**
     * 失效时间
     */
    private LocalDateTime endTime;
}
