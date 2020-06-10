/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fiberhome.ms.auth.init.mq;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.ms.auth.service.AuthDataNewServiceV3;
import javax.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

/**
 * Base {@link MessageListener} implementation for listening to Redis keyspace notifications.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
@Component
@Lazy
@Log4j2
public class ReceiveMsgListener implements MessageListener, DisposableBean {

    private final RedisMessageListenerContainer listenerContainer;

    public static final String AUTH_LOCAL_CACHE_TOPIC = "topic:auth:local_cache_update";

    private static final Topic AUTH_LOCAL_DATA_CACHE = new ChannelTopic(AUTH_LOCAL_CACHE_TOPIC);



    @Autowired
    private RedisConnectionFactory factory;

    @Autowired
    private AuthDataNewServiceV3 authDataNewServiceV3;


    @Autowired
    private RedisSerializer redisSerializer;

    /**
     * 更新   初始化容器
     *
     */
    public ReceiveMsgListener() {
        this.listenerContainer = new RedisMessageListenerContainer();
    }


    /**
     *  监听队列处理方法
     * @param message
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {

        if (message == null || message.getChannel() == null || message.getBody() == null) {
            return;
        }

        doHandleMessage(message);
    }

    /**
     *  auth    业务处理
     *
     * @param message never {@literal null}.
     */
    protected void doHandleMessage(Message message){
        // 消费 authData 队列信息
        authDataNewServiceV3.updateOrDeleteCacheWhenObejctData(
            (JSONObject) redisSerializer.deserialize(message.getBody()));
        log.debug("收到消息，处理结束, messageBody：" + new String(message.getBody()));
    }

    /**
     *  初始化本地监听 配置
     *      将 鉴权 队列加入到监听器
     */
    @PostConstruct
    public void init() {
        this.listenerContainer.setConnectionFactory(factory);
        this.listenerContainer.setErrorHandler(t -> {
            log.error("redis 队列 auth 消费失败", t);
        });
        this.listenerContainer.afterPropertiesSet();
        this.listenerContainer.start();
        listenerContainer.addMessageListener(this, AUTH_LOCAL_DATA_CACHE);
        log.debug("AUTH_LOCAL_DATA_CACHE 初始化了");
    }

    /**
     *  销毁 监听器
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        log.debug("AUTH_LOCAL_DATA_CACHE 销毁了");
        listenerContainer.removeMessageListener(this);
    }


}
