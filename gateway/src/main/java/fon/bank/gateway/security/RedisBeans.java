package fon.bank.gateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

@Configuration
public class RedisBeans {

//    @Bean
//    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(
//            @Value("${spring.data.redis.host:localhost}") String host,
//            @Value("${spring.data.redis.port:6379}") int port) {
//        return new LettuceConnectionFactory(host, port);
//    }

    @Bean
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(
            ReactiveRedisConnectionFactory factory) {
        return new ReactiveStringRedisTemplate(factory);
    }
}
