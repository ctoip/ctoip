package com.aurora.ctoip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ServletComponentScan("com.aurora.ctoip.common.filter")
@EnableTransactionManagement
public class CtoipApplication {
    public static void main(String[] args) {
        SpringApplication.run(CtoipApplication.class, args);
    }

}
