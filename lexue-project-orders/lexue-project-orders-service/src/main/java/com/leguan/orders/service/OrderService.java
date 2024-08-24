package com.leguan.orders.service;

import com.leguan.messagesdk.model.po.MqMessage;
import com.leguan.orders.model.dto.AddOrderDto;
import com.leguan.orders.model.dto.PayRecordDto;
import com.leguan.orders.model.dto.PayStatusDto;
import com.leguan.orders.model.po.XcPayRecord;

public interface OrderService {

    /**
     * @description 创建商品订单
     * @param addOrderDto 订单信息
     * @return PayRecordDto 支付记录(包括二维码)
     */
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto);

    /**
     * @description 查询支付记录
     * @param payNo  交易记录号
     * @return com.leguan.orders.model.po.XcPayRecord
     */
    public XcPayRecord getPayRecordByPayno(String payNo);

    /**
     * 请求支付宝查询支付结果
     * @param payNo 支付记录id
     * @return 支付记录信息
     */
    public PayRecordDto queryPayResult(String payNo);

    public PayStatusDto queryPayResultFromAlipay(String payNo);

    /**
     * @description 保存支付宝支付结果
     * @param payStatusDto  支付结果信息
     * @return void
     */
    public void saveAliPayStatus(PayStatusDto payStatusDto);

    /**
     * 发送通知结果
     * @param message
     */
    public void notifyPayResult(MqMessage message);

}
