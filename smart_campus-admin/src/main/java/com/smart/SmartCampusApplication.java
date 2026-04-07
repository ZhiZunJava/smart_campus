package com.smart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author can
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class SmartCampusApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(SmartCampusApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  校园智学 - AI 赋能智慧校园自主学习生态系统 启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}
