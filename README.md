# 苍穹外卖项目

#### 前端环境搭建: nginx运行, `localhost:90` 进入

### 项目结构

1. sky-take-out: maven父工程，统一管理依赖版本, 聚合其他子模块
2. sky-common: 子模块, 存放公共类, eg. 工具类, 常量类, 异常类等
3. sky-pojo: 子模块, 存放: 实体类, VO, DTO等
4. sky-server: 子模块, 后端服务, 存放: 配置文件, 配置类, 拦截器, 启动类, Controller, Service, Mapper等

##### sky-pojo

- Entity: 实体, 与数据库表对应
- DTO: 接收数据, 数据传输对象, 程序各层间传递数据
- VO: 发送数据, 视图对象, 为前端展示数据提供对象
- POJO: 普通Java对象, 只有属性和对应的getter和setter (包含以上3个)



## 开发过程所学知识点

1.
