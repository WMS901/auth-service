package com.wms.authService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableRedisRepositories  // ✅ Redis Repository 사용 가능하게 설정
public class RedisConfig {

    @Value("${spring.redis.cluster.nodes}") // ✅ 클러스터 노드 리스트
    private String redisClusterNodes;

    @Value("${spring.redis.password}")
    private String redisPassword;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        List<String> nodes = Arrays.asList(redisClusterNodes.split(",")); // 노드 리스트 변환
        RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration(nodes);
        clusterConfig.setPassword(redisPassword);

        System.out.println("🔍 Redis 클러스터 연결 확인: " + redisClusterNodes);
        System.out.println("🔑 Redis 비밀번호 확인: " + (redisPassword.isEmpty() ? "비밀번호 없음" : "설정됨"));

        return new LettuceConnectionFactory(clusterConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}
