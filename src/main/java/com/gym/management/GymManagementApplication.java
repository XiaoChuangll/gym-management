package com.gym.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class GymManagementApplication {

    private static final Logger logger = LoggerFactory.getLogger(GymManagementApplication.class);

    public static void main(String[] args) {
        logger.info("================================================");
        logger.info("   健身房管理系统启动中...");
        logger.info("   初始化Spring Boot应用");
        logger.info("================================================");

        SpringApplication.run(GymManagementApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) {
        Environment environment = event.getApplicationContext().getEnvironment();

        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            String hostName = InetAddress.getLocalHost().getHostName();

            // 获取端口信息
            String port = environment.getProperty("local.server.port");
            if (port == null) {
                port = environment.getProperty("server.port", "8080");
            }

            // 获取数据库信息 - 修复属性名称
            String dbUrl = environment.getProperty("spring.datasource.url", "未配置");
            String dbUsername = environment.getProperty("spring.datasource.username", "未配置");

            // Spring Boot 2.x vs 3.x 驱动类名配置差异
            String dbDriver = environment.getProperty("spring.datasource.driver-class-name");
            if (dbDriver == null) {
                dbDriver = environment.getProperty("spring.datasource.driverClassName", "自动检测");
            }

            // 从URL推断数据库类型
            String dbType = "未知";
            if (dbUrl.contains("mysql")) {
                dbType = "MySQL";
                if ("自动检测".equals(dbDriver)) {
                    dbDriver = "com.mysql.cj.jdbc.Driver";
                }
            } else if (dbUrl.contains("postgresql")) {
                dbType = "PostgreSQL";
                if ("自动检测".equals(dbDriver)) {
                    dbDriver = "org.postgresql.Driver";
                }
            } else if (dbUrl.contains("oracle")) {
                dbType = "Oracle";
                if ("自动检测".equals(dbDriver)) {
                    dbDriver = "oracle.jdbc.OracleDriver";
                }
            } else if (dbUrl.contains("sqlserver")) {
                dbType = "SQL Server";
                if ("自动检测".equals(dbDriver)) {
                    dbDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                }
            } else if (dbUrl.contains("h2")) {
                dbType = "H2";
                if ("自动检测".equals(dbDriver)) {
                    dbDriver = "org.h2.Driver";
                }
            }

            // 打印启动成功信息
            logger.info("================================================");
            logger.info("   健身房管理系统启动成功！");
            logger.info("================================================");
            logger.info("   应用信息:");
            logger.info("   - 服务名称: {}", environment.getProperty("spring.application.name", "gym-management"));
            logger.info("   - 运行环境: {}", String.join(",", environment.getActiveProfiles().length == 0 ?
                    new String[]{"default"} : environment.getActiveProfiles()));
            logger.info("   - 主机地址: {} ({})", hostAddress, hostName);
            logger.info("   - 服务端口: {}", port);
            logger.info("   - 本地访问: http://localhost:{}", port);
            logger.info("   - 网络访问: http://{}:{}", hostAddress, port);
            logger.info("");
            logger.info("   数据库信息:");
            logger.info("   - 数据库类型: {}", dbType);
            logger.info("   - 驱动类型: {}", dbDriver);
            logger.info("   - 连接地址: {}", dbUrl);
            logger.info("   - 用户名: {}", dbUsername);
            logger.info("");
            logger.info("   健康检查:");
            logger.info("   - 应用状态: http://localhost:{}/actuator/health", port);
            logger.info("   - 应用信息: http://localhost:{}/actuator/info", port);
            logger.info("   - 监控端点: http://localhost:{}/actuator", port);
            logger.info("================================================");

            // 简洁的控制台输出
            System.out.println("\n================================================");
            System.out.println("          健身房管理系统启动成功！");
            System.out.println("================================================");
            System.out.println("服务地址: http://localhost:" + port);
            System.out.println("数据库: " + dbType + " (" + dbUrl + ")");
            System.out.println("================================================");

        } catch (UnknownHostException e) {
            logger.error("获取主机信息失败", e);

            // 即使获取主机信息失败，也显示基本信息
            String port = environment.getProperty("server.port", "8080");
            String dbUrl = environment.getProperty("spring.datasource.url", "未配置");

            System.out.println("\n✅ 健身房管理系统启动成功!");
            System.out.println("📍 本地访问: http://localhost:" + port);
            System.out.println("🗄️  数据库: " + dbUrl);
        }
    }
}