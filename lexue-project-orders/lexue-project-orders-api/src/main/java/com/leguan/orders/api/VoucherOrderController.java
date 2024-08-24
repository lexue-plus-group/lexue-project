package com.leguan.orders.api;

import com.leguan.base.model.RestResponse;
import com.leguan.orders.service.VoucherOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VoucherOrderController {

    @Autowired
    private VoucherOrderService voucherOrderService;

    @PostMapping("/seckill/{id}")
    public RestResponse secKillVoucher(@PathVariable("id") Long voucherId) {

        //String id = SecurityUtil.getUser().getId();
        //Long userId = Long.parseLong(id);
        Long userId = 52L;
        return voucherOrderService.secKillVoucher(voucherId, userId);
    }
}
