package com.pro.configserverservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ConfigServerServiceApplicationTests {
    @Autowired ApplicationContext ctx;
    @Test void hasEurekaServerBean() {
        assertThat(ctx.containsBean("eurekaAutoServiceRegistration")).isTrue();
    }

    @Test
    void contextLoads() {
    }

}
