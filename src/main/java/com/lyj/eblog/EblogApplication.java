package com.lyj.eblog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@MapperScan("com.lyj.eblog.mapper")
public class EblogApplication {

    public static void main(String[] args) {
        SpringApplication.run(EblogApplication.class, args);
        System.out.println("Test");
    }

}
