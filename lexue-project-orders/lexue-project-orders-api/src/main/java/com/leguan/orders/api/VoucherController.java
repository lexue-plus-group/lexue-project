package com.leguan.orders.api;

import com.leguan.orders.model.dto.VoucherDto;
import com.leguan.orders.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VoucherController {

    @Autowired
    VoucherService voucherService;

    /**
     * 新增秒杀券
     */
    @PostMapping("/seckill")
    public void addSecKillVoucher(@RequestBody VoucherDto voucher) {
        voucherService.addSecKillVoucher(voucher);
    }
}
