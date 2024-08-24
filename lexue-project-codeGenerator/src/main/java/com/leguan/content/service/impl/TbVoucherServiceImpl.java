package com.leguan.content.service.impl;

import com.leguan.content.model.po.TbVoucher;
import com.leguan.content.mapper.TbVoucherMapper;
import com.leguan.content.service.TbVoucherService;
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
public class TbVoucherServiceImpl extends ServiceImpl<TbVoucherMapper, TbVoucher> implements TbVoucherService {

}
