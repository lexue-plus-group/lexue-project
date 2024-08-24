package com.leguan.orders.service.impl;

import com.leguan.orders.model.po.TbVoucherOrder;
import com.leguan.orders.mapper.TbVoucherOrderMapper;
import com.leguan.orders.service.TbVoucherOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author leguan
 */
@Slf4j
@Service
public class TbVoucherOrderServiceImpl extends ServiceImpl<TbVoucherOrderMapper, TbVoucherOrder> implements TbVoucherOrderService {

}
