//package com.xialiu.elemei.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import org.springframework.cache.annotation.CachingConfigurerSupport;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.cache.RedisCacheConfiguration;
//import org.springframework.data.redis.cache.RedisCacheManager;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializationContext;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//@Configuration
//public class RedisConfig extends CachingConfigurerSupport {
//
//    @Bean
//    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
//
//        //默认的Key序列化器为：JdkSerializationRedisSerializer
//        redisTemplate.setKeySerializer(new StringRedisSerializer()); // key序列化
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());// value序列化
//
//        redisTemplate.setConnectionFactory(connectionFactory);
//        return redisTemplate;
//    }
//
//    @Bean
//    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
//        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
//                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
//
//        return RedisCacheManager.builder(redisConnectionFactory)
//                .cacheDefaults(config)
//                .build();
//    }
//    @Bean
//    public ObjectMapper customObjectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//        return objectMapper;
//    }
//}
//
//
