package com.leguan.orders.service.impl;

import com.leguan.base.exception.LexueException;
import com.leguan.orders.mapper.TbSeckillVoucherMapper;
import com.leguan.orders.mapper.TbVoucherMapper;
import com.leguan.orders.model.dto.VoucherDto;
import com.leguan.orders.model.po.TbSeckillVoucher;
import com.leguan.orders.service.VoucherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class VoucherServiceImpl implements VoucherService {

    @Autowired
    TbVoucherMapper voucherMapper;

    @Autowired
    TbSeckillVoucherMapper secKillVoucherMapper;

    @Override
    @Transactional
    public void addSecKillVoucher(VoucherDto voucher) {

        if (voucher == null) {
            return ;
        }
        int insert1 = voucherMapper.insert(voucher);//新增普通代金券
        if (insert1 <= 0) {
            LexueException.cast("新增代金券失败");
        }
        TbSeckillVoucher secKillVoucher = new TbSeckillVoucher();
        secKillVoucher.setVoucherId(voucher.getId());
        secKillVoucher.setStock(voucher.getStock());
        secKillVoucher.setBeginTime(voucher.getBeginTime());
        secKillVoucher.setEndTime(voucher.getEndTime());
        int insert2 = secKillVoucherMapper.insert(secKillVoucher);
        if (insert2 <= 0) {
            LexueException.cast("新增代金券失败");
        }
    }


}
