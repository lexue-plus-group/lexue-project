package com.leguan.content.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.leguan.content.service.TbSeckillVoucherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * 秒杀优惠券表，与优惠券是一对一关系 前端控制器
 * </p>
 *
 * @author leguan
 */
@Slf4j
@RestController
@RequestMapping("tbSeckillVoucher")
public class TbSeckillVoucherController {

    @Autowired
    private TbSeckillVoucherService  tbSeckillVoucherService;
}
