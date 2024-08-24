package com.leguan.content.service.impl;

import com.leguan.content.model.po.TbSeckillVoucher;
import com.leguan.content.mapper.TbSeckillVoucherMapper;
import com.leguan.content.service.TbSeckillVoucherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * 秒杀优惠券表，与优惠券是一对一关系 服务实现类
 * </p>
 *
 * @author leguan
 */
@Slf4j
@Service
public class TbSeckillVoucherServiceImpl extends ServiceImpl<TbSeckillVoucherMapper, TbSeckillVoucher> implements TbSeckillVoucherService {

}
