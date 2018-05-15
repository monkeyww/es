//package com.bestpay.insurance.cbs.common.redis;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
//
//@ConditionalOnProperty(prefix = "spring.redis", name = "enable",
//      matchIfMissing = true)
//@Configuration
//@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 7200)
//public class RedisSessionConfig {
//
//  @Value("${spring.redis.host}")
//  private String host;
//
//  @Value("${spring.redis.password}")
//  private String password;
//
//  @Value("${spring.redis.port}")
//  private String port;
//
//  @Value("${spring.redis.timeout}")
//  private String timeout;
//
//  @Value("${spring.redis.database}")
//  private String database;
//
//  @Bean
//  public JedisConnectionFactory redisConnectionFactory() {
//      JedisConnectionFactory factory = new JedisConnectionFactory();
//      factory.setHostName(host);
//      factory.setPassword(password);
//      factory.setPort(Integer.parseInt(port));
//      factory.setTimeout(Integer.parseInt(timeout)); // 设置连接超时时间
//      factory.setDatabase(Integer.parseInt(database));
//      return factory;
//  }
//}
