package com.cmcloud.search.mq;

import com.cloudmall.common.enums.ExceptionEnum;
import com.cmcloud.search.service.SearchService;
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
@Component
public class itemListener {

    @Autowired
    SearchService searchService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.item.insert.querue",durable = "true"),//声明队列
            exchange = @Exchange(name = "cm.item.exchange",type = ExchangeTypes.TOPIC),//声明交换机，TOPIC模式
            key = {"item.insert","item.update"}//绑定insert 和update事件
    ))
    public void listenInsertOrUpdate(Long spuId){
        if(spuId==null){
            return;
        }
        //处理消息，对索引库进行新增或者修改
        searchService.createOrUpdateIndex(spuId);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.item.delete.querue",durable = "true"),//声明队列
            exchange = @Exchange(name = "cm.item.exchange",type = ExchangeTypes.TOPIC),//声明交换机，TOPIC模式
            key = {"item.delete"}//绑定insert 和update事件
    ))
    public void listenDelete(Long spuId){
        if(spuId==null){
            return;
        }
        //处理消息，对索引库进行新增或者修改
        searchService.deleteIndex(spuId);
    }
}
