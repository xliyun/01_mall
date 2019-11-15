package com.cmcloud.page.mq;


import com.cmcloud.service.PageService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: xiaoliyu
 * @date: 2019-04-09 15:25
 */
//@Component
public class itemListener {

    @Autowired
    private PageService pageService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "page.item.insert.querue",durable = "true"),//声明队列
            exchange = @Exchange(name = "cm.item.exchange",type = ExchangeTypes.TOPIC),//声明交换机，TOPIC模式
            key = {"item.insert","item.update"}//绑定insert 和update事件
    ))
    public void listenInsertOrUpdate(Long spuId){
        if(spuId==null){
            return;
        }
        //处理消息，创建静态页
        pageService.createhtml(spuId);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "page.item.delete.querue",durable = "true"),//声明队列
            exchange = @Exchange(name = "cm.item.exchange",type = ExchangeTypes.TOPIC),//声明交换机，TOPIC模式
            key = {"item.delete"}//绑定insert 和update事件
    ))
    public void listenDelete(Long spuId){
        if(spuId==null){
            return;
        }
        //处理消息，对静态页进行删除
        pageService.deleteHtml(spuId);
    }
}
