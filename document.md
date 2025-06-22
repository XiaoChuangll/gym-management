# 健身房管理系统设计与实现文档

## 1. 需求分析

### 1.1 项目背景

健身房管理系统是一款专为健身房、健身中心设计的综合管理软件，旨在提高健身房的运营效率，优化会员体验，帮助健身房实现数字化管理。

### 1.2 功能需求

1. **会员管理**
    - 添加、查询、修改、删除会员信息
    - 会员信息包括：会员编号、姓名、联系方式、入会日期、会员类型等

2. **教练管理**
    - 添加、查询、修改、删除教练信息
    - 教练信息包括：教练编号、姓名、联系方式、擅长领域等

3. **课程管理**
    - 添加、查询、修改、删除课程信息
    - 课程信息包括：课程编号、课程名称、教练编号、课程时间、课程容量等

4. **预约管理**
    - 添加、查询、修改、删除预约信息
    - 预约信息包括：预约编号、会员编号、课程编号、预约时间、状态等

5. **统计报表**
    - 查询会员的课程预约情况
    - 统计各课程的出勤率
    - 统计教练的工作量

6. **通知系统**
    - 发布、查看、管理系统通知和公告
    - 支持通知的归档功能
    - 支持Markdown格式的通知内容
    - 按时间顺序显示最新通知

### 1.3 非功能需求

1. **性能需求**
    - 系统响应时间不超过3秒
    - 支持多用户并发访问

2. **安全需求**
    - 用户认证和授权
    - 数据加密存储

3. **可用性需求**
    - 友好的用户界面
    - 简单易用的操作流程

## 2. 采用框架技术

### 2.1 后端技术

1. **Spring Boot 3.1.5**
    - 核心框架，提供自动配置、依赖注入等功能
    - 简化应用开发，提高开发效率

2. **Spring MVC**
    - Web应用框架，处理HTTP请求
    - 实现MVC架构，分离业务逻辑和视图

3. **Spring Data JPA**
    - 数据访问层框架，简化数据库操作
    - 提供Repository接口，减少样板代码

4. **Hibernate**
    - ORM框架，实现对象关系映射
    - 自动生成SQL语句，简化数据库操作

5. **MySQL**
    - 关系型数据库，存储系统数据
    - 支持事务处理，保证数据一致性

### 2.2 前端技术

1. **Thymeleaf**
    - 服务器端模板引擎
    - 与Spring Boot无缝集成

2. **Bootstrap 5**
    - 响应式前端框架
    - 提供丰富的UI组件

3. **Tailwind CSS**
    - 实用优先的CSS框架
    - 提供低级样式类，构建现代化界面

4. **Chart.js**
    - JavaScript图表库
    - 用于生成统计报表的可视化图表

5. **Font Awesome**
    - 图标库
    - 提供丰富的图标集合

6. **Marked.js**
    - Markdown解析器
    - 用于将Markdown格式转换为HTML显示

### 2.3 开发工具

1. **Maven**
    - 项目构建工具
    - 管理项目依赖

2. **JDK 17**
    - Java开发环境
    - 支持最新Java特性

3. **Lombok**
    - 减少样板代码
    - 自动生成getter/setter等方法

## 3. 总体结构设计

### 3.1 系统架构

采用经典的三层架构设计：

1. **表示层（Presentation Layer）**
    - 控制器（Controller）：处理用户请求，调用服务层方法
    - 视图（View）：展示数据，提供用户界面

2. **业务逻辑层（Business Logic Layer）**
    - 服务接口（Service Interface）：定义业务逻辑方法
    - 服务实现（Service Implementation）：实现业务逻辑

3. **数据访问层（Data Access Layer）**
    - 数据访问对象（Repository）：提供数据库操作方法
    - 实体类（Entity）：映射数据库表结构

### 3.2 包结构设计

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

### 3.3 数据库设计

1. **用户表（users）**
    - id：主键
    - username：用户名
    - password：密码
    - role：角色
    - created_at：创建时间
    - updated_at：更新时间

2. **会员表（member）**
    - member_id：会员编号（主键）
    - name：姓名
    - contact：联系方式
    - join_date：入会日期
    - member_type：会员类型

3. **教练表（coach）**
    - coach_id：教练编号（主键）
    - name：姓名
    - contact：联系方式
    - specialty：擅长领域

4. **课程表（course）**
    - course_id：课程编号（主键）
    - course_name：课程名称
    - coach_id：教练编号（外键）
    - course_time：课程时间
    - capacity：课程容量

5. **预约表（reservation）**
    - reservation_id：预约编号（主键）
    - member_id：会员编号（外键）
    - course_id：课程编号（外键）
    - reservation_time：预约时间
    - status：状态

6. **通知表（notification）**
    - notification_id：通知编号（主键）
    - title：通知标题
    - content：通知内容（支持Markdown格式）
    - status：通知状态（活动/归档）
    - created_at：创建时间
    - updated_at：更新时间
    - created_by：创建者

## 4. 详细设计与实现

### 4.1 实体类设计

#### 4.1.1 会员实体（Member）

```java
@Entity
@Table(name = "member")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "member_id", length = 36)
    private String memberId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "contact", nullable = false, length = 20)
    private String contact;

    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    @Column(name = "member_type", nullable = false, length = 20)
    private String memberType;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;
}
```

#### 4.1.2 教练实体（Coach）

```java
@Entity
@Table(name = "coach")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coach {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "coach_id", length = 36)
    private String coachId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "contact", nullable = false, length = 20)
    private String contact;

    @Column(name = "specialty", nullable = false, length = 100)
    private String specialty;

    @OneToMany(mappedBy = "coach", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Course> courses;
}
```

#### 4.1.3 课程实体（Course）

```java
@Entity
@Table(name = "course")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "course_id", length = 36)
    private String courseId;

    @Column(name = "course_name", nullable = false, length = 50)
    private String courseName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id", nullable = false)
    private Coach coach;

    @Column(name = "course_time", nullable = false)
    private LocalDateTime courseTime;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;
}
```

#### 4.1.4 预约实体（Reservation）

```java
@Entity
@Table(name = "reservation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "reservation_id", length = 36)
    private String reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "reservation_time", nullable = false)
    private LocalDateTime reservationTime;

    @Column(name = "status", nullable = false, length = 20)
    private String status;
}
```

#### 4.1.5 通知实体（Notification）

```java
@Entity
@Table(name = "notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "notification_id", length = 36)
    private String notificationId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;
}
```

### 4.2 数据传输对象（DTO）设计

#### 4.2.1 会员DTO（MemberDTO）

用于在控制器和视图之间传输会员数据，避免直接暴露实体类。

#### 4.2.2 教练DTO（CoachDTO）

用于在控制器和视图之间传输教练数据。

#### 4.2.3 课程DTO（CourseDTO）

用于在控制器和视图之间传输课程数据。

#### 4.2.4 预约DTO（ReservationDTO）

用于在控制器和视图之间传输预约数据。

#### 4.2.5 统计报表DTO

- **课程出勤率DTO（CourseAttendanceDTO）**：统计课程出勤率
- **教练工作量DTO（CoachWorkloadDTO）**：统计教练工作量
- **会员预约DTO（MemberReservationDTO）**：统计会员预约情况

#### 4.2.6 通知DTO（NotificationDTO）

用于在控制器和视图之间传输通知数据。

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private String notificationId;
    private String title;
    private String content;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
}
```

### 4.3 数据访问层（Repository）设计

采用Spring Data JPA的Repository接口，简化数据库操作。

#### 4.3.1 会员Repository

```java
public interface MemberRepository extends JpaRepository<Member, String> {
    List<Member> findByName(String name);
    List<Member> findByMemberType(String memberType);
}
```

#### 4.3.2 教练Repository

```java
public interface CoachRepository extends JpaRepository<Coach, String> {
    List<Coach> findByName(String name);
    List<Coach> findBySpecialty(String specialty);
}
```

#### 4.3.3 课程Repository

```java
public interface CourseRepository extends JpaRepository<Course, String> {
    List<Course> findByCoach(Coach coach);
    List<Course> findByCourseName(String courseName);
    List<Course> findByCourseTimeAfter(LocalDateTime dateTime);
}
```

#### 4.3.4 预约Repository

```java
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    List<Reservation> findByMember(Member member);
    List<Reservation> findByCourse(Course course);
    List<Reservation> findByStatus(String status);
}
```

#### 4.3.5 通知Repository

```java
public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByStatus(String status);
    List<Notification> findByStatusOrderByCreatedAtDesc(String status);
}
```

### 4.4 服务层（Service）设计

#### 4.4.1 会员服务

```java
public interface MemberService {
    List<MemberDTO> getAllMembers();
    MemberDTO getMemberById(String id);
    MemberDTO createMember(MemberDTO memberDTO);
    MemberDTO updateMember(String id, MemberDTO memberDTO);
    void deleteMember(String id);
    List<MemberDTO> searchMembers(String name);
}
```

#### 4.4.2 教练服务

```java
public interface CoachService {
    List<CoachDTO> getAllCoaches();
    CoachDTO getCoachById(String id);
    CoachDTO createCoach(CoachDTO coachDTO);
    CoachDTO updateCoach(String id, CoachDTO coachDTO);
    void deleteCoach(String id);
    List<CoachDTO> searchCoaches(String name);
}
```

#### 4.4.3 课程服务

```java
public interface CourseService {
    List<CourseDTO> getAllCourses();
    CourseDTO getCourseById(String id);
    CourseDTO createCourse(CourseDTO courseDTO);
    CourseDTO updateCourse(String id, CourseDTO courseDTO);
    void deleteCourse(String id);
    List<CourseDTO> searchCourses(String name);
    List<CourseDTO> getCoursesByCoach(String coachId);
    List<CourseDTO> getUpcomingCourses();
}
```

#### 4.4.4 预约服务

```java
public interface ReservationService {
    List<ReservationDTO> getAllReservations();
    ReservationDTO getReservationById(String id);
    ReservationDTO createReservation(ReservationDTO reservationDTO);
    ReservationDTO updateReservation(String id, ReservationDTO reservationDTO);
    void deleteReservation(String id);
    List<ReservationDTO> getReservationsByMember(String memberId);
    List<ReservationDTO> getReservationsByCourse(String courseId);
    List<ReservationDTO> getReservationsByStatus(String status);
}
```

#### 4.4.5 报表服务

```java
public interface ReportService {
    List<MemberReservationDTO> getMemberReservations(String memberId);
    List<CourseAttendanceDTO> getCourseAttendance();
    List<CoachWorkloadDTO> getCoachWorkload();
}
```

#### 4.4.6 通知服务

```java
public interface NotificationService {
    List<NotificationDTO> getAllNotifications();
    NotificationDTO getNotificationById(String id);
    NotificationDTO createNotification(NotificationDTO notificationDTO);
    NotificationDTO updateNotification(String id, NotificationDTO notificationDTO);
    NotificationDTO updateNotificationStatus(String id, String status);
    void deleteNotification(String id);
    List<NotificationDTO> getActiveNotifications();
    List<NotificationDTO> getArchivedNotifications();
}
```

### 4.5 控制器层（Controller）设计

#### 4.5.1 会员控制器

处理会员相关的HTTP请求，包括页面请求和API请求。

#### 4.5.2 教练控制器

处理教练相关的HTTP请求，包括页面请求和API请求。

#### 4.5.3 课程控制器

处理课程相关的HTTP请求，包括页面请求和API请求。

#### 4.5.4 预约控制器

处理预约相关的HTTP请求，包括页面请求和API请求。

#### 4.5.5 报表控制器

处理报表相关的HTTP请求，包括页面请求和API请求。

#### 4.5.6 通知控制器

```java
@Controller
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public String listNotifications(Model model) {
        List<NotificationDTO> notifications = notificationService.getAllNotifications();
        model.addAttribute("notifications", notifications);
        return "notification/list";
    }

    @GetMapping("/manage")
    public String manageNotifications(Model model) {
        List<NotificationDTO> notifications = notificationService.getAllNotifications();
        model.addAttribute("notifications", notifications);
        return "notification/manage";
    }

    @PostMapping("/create")
    public String createNotification(@ModelAttribute NotificationDTO notificationDTO) {
        notificationService.createNotification(notificationDTO);
        return "redirect:/notifications/manage";
    }

    @PostMapping("/{id}/update-status")
    @ResponseBody
    public Map<String, Object> updateNotificationStatus(@PathVariable("id") String id, @RequestParam("status") String status) {
        NotificationDTO updated = notificationService.updateNotificationStatus(id, status);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("notification", updated);
        return response;
    }
}
```

### 4.6 视图层设计

采用Thymeleaf模板引擎，结合Bootstrap和Tailwind CSS构建响应式用户界面。

#### 4.6.1 布局模板

使用Thymeleaf的布局功能，创建通用布局模板，包含导航栏、侧边栏和页脚。

#### 4.6.2 列表页面

展示数据列表，支持分页、排序和搜索功能。

#### 4.6.3 表单页面

用于添加和编辑数据，包含表单验证功能。

#### 4.6.4 详情页面

展示数据详情，包含相关联的数据。

#### 4.6.5 报表页面

展示统计报表，包含图表和表格。

### 4.7 安全设计

#### 4.7.1 认证与授权

使用拦截器实现用户认证和授权功能。

```java
@Configuration
public class WebSecurityConfig implements WebMvcConfigurer {
    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**") // 拦截所有请求
                .excludePathPatterns("/about","/login", "/logout", "/css/**", "/js/**", "/img/**"); // 不拦截登录相关和静态资源
    }
}
```

#### 4.7.2 数据校验

使用Spring Validation框架进行数据校验，确保数据的完整性和有效性。

## 5. 测试

### 5.1 单元测试

使用JUnit和Mockito进行单元测试，测试各个组件的功能。

#### 5.1.1 服务层测试

测试服务层的业务逻辑，包括正常情况和异常情况。

#### 5.1.2 控制器层测试

测试控制器层的请求处理，包括正常情况和异常情况。

### 5.2 集成测试

使用Spring Boot Test进行集成测试，测试各个组件的协作。

#### 5.2.1 数据访问层测试

测试数据访问层的数据库操作，包括增删改查操作。

#### 5.2.2 Web层测试

测试Web层的请求处理，包括页面请求和API请求。

### 5.3 系统测试

使用Selenium进行系统测试，测试整个系统的功能。

#### 5.3.1 功能测试

测试系统的各项功能，包括会员管理、教练管理、课程管理、预约管理和统计报表。

#### 5.3.2 性能测试

测试系统的性能，包括响应时间和并发处理能力。

## 6. 部署与维护

### 6.1 部署环境

- JDK 17+
- MySQL 8.0+
- Nginx（可选，用于反向代理）

### 6.2 部署步骤

1. 准备环境：安装JDK和MySQL
2. 配置数据库：创建数据库和用户
3. 配置应用：修改配置文件
4. 构建应用：使用Maven构建应用
5. 运行应用：使用Java命令或Spring Boot插件运行应用

### 6.3 维护计划

1. 定期备份数据库
2. 定期检查日志文件
3. 定期更新依赖库
4. 定期优化数据库

## 7. 总结与展望

### 7.1 项目总结

健身房管理系统采用Spring Boot框架开发，实现了会员管理、教练管理、课程管理、预约管理、统计报表和通知系统等功能，满足了健身房日常运营的需求。系统支持通知的发布、查看和归档管理，以及Markdown格式内容的展示，为用户提供了更好的信息交流体验。

### 7.2 未来展望

1. 增加移动端应用，方便会员和教练使用
2. 增加支付功能，支持在线支付
3. 增加会员积分系统，提高会员忠诚度
4. 增加数据分析功能，为经营决策提供支持
5. 增加多语言支持，适应国际化需求
6. 扩展通知系统，添加个人消息功能和推送通知能力
7. 增强Markdown编辑器，提供更丰富的编辑体验