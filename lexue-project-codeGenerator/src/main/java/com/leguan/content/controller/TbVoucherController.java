package com.leguan.content.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.leguan.content.service.TbVoucherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author leguan
 */
@Slf4j
@RestController
@RequestMapping("tbVoucher")
public class TbVoucherController {

    @Autowired
    private TbVoucherService  tbVoucherService;
}
