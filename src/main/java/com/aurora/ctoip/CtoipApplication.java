package com.aurora.ctoip;

import com.aurora.ctoip.controller.BaseController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ServletComponentScan("com.aurora.ctoip.common.filter")
@EnableTransactionManagement
@EnableConfigurationProperties(BaseController.class)
public class CtoipApplication {
    public static void main(String[] args) {
        SpringApplication.run(CtoipApplication.class, args);
    }

}
