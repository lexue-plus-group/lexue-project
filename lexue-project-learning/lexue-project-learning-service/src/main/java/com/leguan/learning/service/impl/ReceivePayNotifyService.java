package com.leguan.learning.service.impl;

import com.alibaba.fastjson.JSON;
import com.leguan.base.exception.LexueException;
import com.leguan.learning.config.PayNotifyConfig;
import com.leguan.learning.service.MyCourseTablesService;
import com.leguan.messagesdk.model.po.MqMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description 接收消息通知处理类
 */
@Slf4j
@Service
public class ReceivePayNotifyService {

    @Autowired
    MyCourseTablesService myCourseTablesService;

    //监听消息队列接收支付结果通知
    @RabbitListener(queues = PayNotifyConfig.PAYNOTIFY_QUEUE)
    public void receive(Message message) {

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //解析出消息
        byte[] body = message.getBody();
        String jsonString = new String(body);
        //转成对象
        MqMessage mqMessage = JSON.parseObject(jsonString, MqMessage.class);
        //解析消息的内容
        //选课id
        String chooseCourseId = mqMessage.getBusinessKey1();
        //订单类型
        String orderType = mqMessage.getBusinessKey2();
        //学习中心服务只要购买课程类的支付订单结果
        if (orderType.equals("60201")) {
            //根据消息内容，更新选课记录，向我的课程记录表插入记录
            boolean result = myCourseTablesService.saveChooseCourseSuccess(chooseCourseId);
            if (!result) {
                LexueException.cast("保存选课记录失败");
            }
        }
    }

}
