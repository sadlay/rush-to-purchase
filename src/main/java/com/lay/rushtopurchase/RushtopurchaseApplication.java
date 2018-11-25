package com.lay.rushtopurchase;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan(annotationClass = Mapper.class, basePackages = "com.lay.rushtopurchase.dao")
//启动定时任务
@EnableScheduling
public class RushtopurchaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(RushtopurchaseApplication.class, args);
    }
}
