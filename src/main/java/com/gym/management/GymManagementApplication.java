package com.gym.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 健身房管理系统主应用类
 */
@SpringBootApplication
public class GymManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(GymManagementApplication.class, args);
        System.out.println("------------------------------------------------");
        System.out.println(" ");
        System.out.println("          服务已运行在：8080 端口");
        System.out.println("          点击下面URL进行访问");
        System.out.println("          http://localhost:8080/");
        System.out.println(" ");
        System.out.println("------------------------------------------------");

    }
} 