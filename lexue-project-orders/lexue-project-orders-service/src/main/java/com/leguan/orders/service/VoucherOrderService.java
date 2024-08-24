package com.leguan.orders.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leguan.base.model.RestResponse;
import com.leguan.orders.model.po.TbVoucherOrder;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author leguan
 * @since 2024-08-22
 */
public interface VoucherOrderService extends IService<TbVoucherOrder> {

    public RestResponse secKillVoucher(Long voucherId, Long userId);
}
