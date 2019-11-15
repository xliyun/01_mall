package com.cmcloud.sms.mq;

import com.cloudmall.common.utils.JsonUtils;
import com.cmcloud.sms.config.SmsProperties;
import com.cmcloud.sms.utils.SmsUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * @description:
 * @author: xiaoliyu
 * @date: 2019-04-11 14:41
 */
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsListener {
    @Autowired
    private SmsProperties prop;

    @Autowired
    private SmsUtils smsUtils;

    /**
     * @Author xiaoliyu
     * @Description 发送短信验证码
     * @Date 14:49 2019/4/11
     **/
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "sms.verify.code.queue",durable = "true"),//声明队列
            exchange = @Exchange(name = "cm.sms.exchange",type = ExchangeTypes.TOPIC),//声明交换机，TOPIC模式
            key = {"sms.verify.code"}//绑定insert 和update事件
    ))
    public void listenInsertOrUpdate(Map<String,String> msg){
        if(CollectionUtils.isEmpty(msg)){
            return;
        }
        String phone=msg.remove("phone");//可以不用get，remove是删除并且返回元素，拿走了phone Map就只剩下了code
        if(StringUtils.isBlank(phone)){
            return;
        }
        smsUtils.sendSms(phone,prop.getSignName(),prop.getVerifyCodeTemplate(), JsonUtils.serialize(msg));
    }
}
