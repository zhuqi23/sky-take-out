# 苍穹外卖项目

#### 前端环境搭建: nginx运行, `localhost:90` 进入

### 项目结构

1. sky-take-out: maven父工程，统一管理依赖版本, 聚合其他子模块
2. sky-common: 子模块, 存放公共类, eg. 工具类, 常量类, 异常类等
3. sky-server: 子模块, 后端服务, 存放: 配置文件, 配置类, 拦截器, 启动类, Controller, Service, Mapper等
4. sky-pojo: 子模块, 存放: 实体类, VO, DTO等

- Entity: 实体, 与数据库表对应
- DTO: 接收数据, 数据传输对象, 程序各层间传递数据
- VO: 发送数据, 视图对象, 为前端展示数据提供对象
- POJO: 普通Java对象, 只有属性和对应的getter和setter (包含以上3个)

## 开发过程所学知识点

1. 前后端联调: nginx反向代理, 将前端发送的请求有nginx转发到后端服务器
   - 浏览器 -> `http://localhost/api/employee/login` -> nginx -> `http://localhost:8080/admin/employee/login` -> 服务器tomcat
   - 好处:
     - 提高访问速度: nginx有缓存, 同样接口无需访问服务器, 直接返回缓存数据
     - 进行负载均衡: 有大量请求时, 均匀地将其发送个各个服务器
     - 保证后端服务安全: 防止直接暴露服务器ip地址
   - 配置nginx反向代理/负载均衡: nginx.conf文件中server
2. MyBatis 的 `<where>` 标签会自动去除第一个多余的 AND 或 OR，但不会在条件之间自动添加连接符, 反正在标签中有 AND/OR 都放即可
3. 公共字段填充(AOP):
   - 问题: 很多业务表中有公共字段(update_time等), 在代码中很多相同的操作对其修改, 不便于后期修改
   - 解决: 使用 AOP 技术在所需方法运行前加入通知
   - 创建 annotation 自定义注解, 用以标识要 AOP 的方法和不同的方法要执行的不同通知(使用注解的属性指定是update/insert方法)(使用注解是因为要同时拦截update和insert, 只用execution比较麻烦)
   - 创建 aspect 切面类, 统一拦截有注解的方法(execution/@annotation)
   - 在 mapper 中对应方法增加注解
   - 完善 aspect 通知:
     - 获取数据库操作类型(update/insert)
     - 获取被拦截方法的参数: 实体对象(约定好第一个参数是对象, 其余的不管)
     - 通过反射获取对象方法并赋值(update_time...)


## 开发过程小技巧

1. TODO 用来标识后续要改进的部分
2. 前后端分离开发流程: 定制接口 -> 前端开发/后端开发自测 -> 联调校验格式 -> 自动化测试
3. 在 Swagger/ApiFox 中修改数据, 会跳过各种前端检测使得数据不合理

## 开发流程

### 后端环境搭建

#### 导入代码

- 导入代码 -> 设置模块 -> 数据库搭建 -> 前后端联调 Nginx

#### 完善登录功能

- 问题: 员工表中密码明文存储, 安全性太低
- 解决: 密码加密后存储: MD5加密对明文密码加密后再存入数据库

#### 导入接口文档

- 接口文档已提供: Apifox/YApi 中导入接口文档
- 开发过程自动生成接口: Swagger: 生成接口文档以及在线接口调试页面
  - 导入 knife4j 的 maven
  - 配置类中加入 knife4j 相关配置, docket
  - 设置静态资源映射, 否则接口文档无法访问, addResourceHandlers
- Swagger 注解, 使接口文档更具可读性
  - @Api: 类上, controller类上对类的说明
  - @ApiModel: 类上, pojo类用途的说明
  - @ApiModelProperty: 属性上, 描述属性信息
  - @ApiOperation: 方法上, controller方法上说明方法用途

### 代码正式开发

#### 代码完善(程序常见存在问题)

- 录入内容已存在, 抛出异常后没有处理

#### 员工管理

- 新增员工
  - handler中处理: 员工已存在, 返回 SQL 异常
  - 获取记录人id: 拦截器set ThreadLocal, 新增员工get
- 员工分页查询
  - 使用PageHelper, mapper.xml进行sql查询
  - 日期LocalDateTime前端展示为数组
    - 类属性上加注解 `@JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss"`)
    - WebMvcConfiguration扩展MVC消息转换器, 统一对日期类型格式化
- 启用禁用员工账号: 通过id修改账户
- 修改员工信息
  - 回显: 通过id查询信息
  - 通过id修改员工信息

#### 分类管理

- 新增分类
- 分类分页查询
- 根据id删除分类
  - 需要查询当前分类是否关联菜品/套餐
- 修改分类
- 启用禁用分类
- 根据类型查询分类
- 公共字段填充
  - create_time/create_user/update_time/update_user在insert/update时与其他表操作相同, AOP统一处理

#### 菜品管理



