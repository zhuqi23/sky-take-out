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

## 开发过程小技巧

1. TODO 用来标识后续要改进的部分
2. 前后端分离开发流程: 定制接口 -> 前端开发/后端开发自测 -> 联调校验格式 -> 自动化测试

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

#### 分类管理



