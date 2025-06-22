# 健身房管理系统

健身房管理系统是一款专为健身房、健身中心设计的综合管理软件，旨在提高健身房的运营效率，优化会员体验，帮助健身房实现数字化管理。

## 功能特点

- **会员管理**：录入、查询、修改、删除会员信息（会员编号、姓名、联系方式、入会日期、会员类型等）
- **教练管理**：管理教练信息（教练编号、姓名、联系方式、擅长领域等）
- **课程管理**：录入、查询、修改、删除课程信息（课程编号、课程名称、教练编号、课程时间、课程容量等）
- **预约管理**：记录会员的课程预约信息
- **统计报表**：查询会员的课程预约情况，统计各课程的预约人数、教练的工作量等
- **通知系统**：发布、查看、管理系统通知和公告，支持Markdown格式，实现通知归档功能


## 🛠️ 技术栈

|      类别      |            技术实现            |
|:------------:|:--------------------------:|  
|     后端框架     |         Spring Boot 3.1.5       |  
|     数据库      |            MySQL            |  
|    ORM框架      |       Spring Data JPA        |  
|     前端框架     |         Bootstrap 5          |  
|    模板引擎      |         Thymeleaf           |  
|     图表库      |          Chart.js           |
|    Markdown渲染  |         Marked.js          |

## 系统要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

## 快速开始

### 1. 克隆代码库

```bash
git clone https://github.com/XiaoChuangll/gym-management.git
cd gym-management
```

### 2. 配置数据库

在 `src/main/resources/application.properties` 中配置您的数据库连接信息：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gym_management?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. 构建并运行

```bash
mvn clean package
java -jar target/management-0.2.0-SNAPSHOT.jar
```

或者使用Maven Spring Boot插件：

```bash
mvn spring-boot:run
```

### 4. 访问系统

打开浏览器，访问 [http://localhost:8080/](http://localhost:8080/)

## 数据库结构

系统包含以下主要数据表：

- **会员表（Member）**：存储会员信息
- **教练表（Coach）**：存储教练信息
- **课程表（Course）**：存储课程信息
- **预约表（Reservation）**：存储预约信息
- **通知表（Notification）**：存储系统通知和公告信息

## API文档

系统提供了完整的RESTful API，支持会员、教练、课程、预约和通知的增删改查操作。详细API文档请参考：

- 会员API：`/gym/api/members`
- 教练API：`/gym/api/coaches`
- 课程API：`/gym/api/courses`
- 预约API：`/gym/api/reservations`
- 报表API：`/gym/api/reports`
- 通知API：`/gym/api/notifications`

## 开发指南

### 项目结构

```
src/main/java/com/gym/management/
├── controller/    # 控制器层
├── service/       # 服务层
│   └── impl/      # 服务实现类
├── repository/    # 数据访问层
├── model/         # 实体类
├── dto/           # 数据传输对象
├── config/        # 配置类
├── exception/     # 异常处理
└── interceptor/   # 拦截器
```

### 添加新功能

1. 在 `model` 包中创建实体类
2. 在 `repository` 包中创建数据访问接口
3. 在 `service` 包中创建服务接口和实现类
4. 在 `controller` 包中创建控制器
5. 在 `templates` 目录下创建相应的视图模板

### 最近更新

- 修复了通知管理页面访问问题
- 实现了通知归档功能的持久化
- 优化了首页通知公告的排序，按创建时间倒序排列
- 移除了通知管理页面的批量选择功能
- 增加了对Markdown格式通知内容的支持

## 贡献指南

欢迎贡献代码！请遵循以下步骤：

1. Fork 本仓库
2. 创建您的特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交您的更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建一个 Pull Request

## 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件 