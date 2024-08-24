package com.leguan.orders.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leguan.base.exception.LexueException;
import com.leguan.base.model.RestResponse;
import com.leguan.base.utils.IdWorkerUtils;
import com.leguan.orders.mapper.TbSeckillVoucherMapper;
import com.leguan.orders.mapper.TbVoucherOrderMapper;
import com.leguan.orders.model.po.TbSeckillVoucher;
import com.leguan.orders.model.po.TbVoucherOrder;
import com.leguan.orders.service.VoucherOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author leguan
 */
@Slf4j
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<TbVoucherOrderMapper, TbVoucherOrder> implements VoucherOrderService {

    @Autowired
    TbSeckillVoucherMapper seckillVoucherMapper;

    @Autowired
    TbVoucherOrderMapper voucherOrderMapper;

    @Transactional
    @Override
    public RestResponse secKillVoucher(Long voucherId, Long userId) {

        //查询代金券
        LambdaQueryWrapper<TbSeckillVoucher> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TbSeckillVoucher::getVoucherId, voucherId);
        TbSeckillVoucher voucher = seckillVoucherMapper.selectOne(lambdaQueryWrapper);

        //判断秒杀是否开始
        if (voucher.getBeginTime().isAfter(LocalDateTime.now())) {
            return RestResponse.validfail("秒杀尚未开始！");
        }

        //判断秒杀是否已经结束
        if (voucher.getEndTime().isBefore(LocalDateTime.now())) {
            return RestResponse.validfail("秒杀已经结束！");
        }

        //判断库存是否充足
        if (voucher.getStock() < 1) {
            return RestResponse.validfail("库存不足！");
        }

        //扣减库存
        TbSeckillVoucher seckillVoucher = new TbSeckillVoucher();
        BeanUtils.copyProperties(voucher, seckillVoucher);
        seckillVoucher.setStock(seckillVoucher.getStock() - 1);
        LambdaQueryWrapper<TbSeckillVoucher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TbSeckillVoucher::getVoucherId, voucherId).gt(TbSeckillVoucher::getStock, 0);
        int i = seckillVoucherMapper.update(seckillVoucher, wrapper);
        if (i <= 0) {
            LexueException.cast("库存不足！");
        }

        //创建订单
        TbVoucherOrder voucherOrder = new TbVoucherOrder();
        long orderId = IdWorkerUtils.getInstance().nextId();
        voucherOrder.setId(orderId);
        voucherOrder.setUserId(userId);
        voucherOrder.setVoucherId(voucherId);
        int insert = voucherOrderMapper.insert(voucherOrder);
        if (insert <= 0) {
            LexueException.cast("订单生成失败");
        }

        //返回订单id
        return RestResponse.success(orderId);
    }

}
