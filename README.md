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
4. 文件上传(oss)
   
   - 注册账号/充值 -> 参照官方sdk写入门程序 -> 集成
   - 开通oss -> 创建bucket(上传的文件存储到某个bucket, 按步骤即可) -> 创建密钥AccessKey(用以在代码中调用阿里云服务, 按步骤即可) -> 配置AccessKey环境
   - ```
     # 配置: 直接在同一个 cmd 中以管理员身份运行以下
     # 设置 id 和 secret
     set OSS_ACCESS_KEY_ID=xxxxxxxxx
     set OSS_ACCESS_KEY_SECRET=xxxxxxxxxx
     # 使以上设置生效
     setx OSS_ACCESS_KEY_ID "%OSS_ACCESS_KEY_ID%"
     setx OSS_ACCESS_KEY_SECRET "%OSS_ACCESS_KEY_SECRET%"
     # 验证是否成功
     echo "%OSS_ACCESS_KEY_ID%"
     echo "%OSS_ACCESS_KEY_SECRET%"
     ```
   - 完成程序并集成(依据oss官网)
     - pom.xml 中增加 oss 相关依赖
     - 在 application.yml 中配置 alioss 内容
     - 新建 AliOssProperties配置类 匹配 application.yml 的内容
     - 新建 AliOssUtil工具类 实现文件上传功能(依据oss示例)
     - 新建 OssConfiguration配置类 初始化 AliOssUtil类对象
     - 在 CommonController 中实现 /upload 路径对应的 upload 方法
       - 传入 MultipartFile文件类型, 获取文件后缀, 生成唯一文件名
       - 调用 aliOssUtile.upload 上传文件到 oss, 并返回 oss 中的路径
5. com.sky.properties 配置文件作用
   
   - springboot 的配置属性绑定类, 将 application.yml 配置项自动映射到 properties 的对象属性上
   - 注解说明
     - @Component: 将其交给 spring 管理
     - @ConfigurationProperties(prefix=sky.jwt): 与配置文件中以 sky.jwt 开头的属性匹配
     - @Data: Lombok注解, 自动生成 getter/setter 方法
   - 使用: 注入对象 -> 直接使用getter/setter方法
6. application.yml 讲解
   
   - springboot 的主配置文件, 开发环境和测试环境不同可能会有不同配置, 故用引用的方式将 dev(application-dev.yml) 作为开发环境文件具体配置, 在其他环境时提供其他环境配置文件(eg.prod), 修改 spring.profiles.active 中的内容即可更换
   - 使用 ${dev中的路径} 作为数据
   - 与配置属性类 properties 相对应获取, 用 '-' 分隔, spring 会自动转换为驼峰命名法
7. controller类 注解
   
   - @RequestBody: 将请求体中的 json 转为对象, 注意Get请求没有请求体
   - @RequestParam: 请求参数, 获取 url 中 ? 后面的同名参数值, eg. /employee/page?page=1
     - 不用@RequestParam: 传入的参数与接收的对象相匹配, spring会自动将同名参数值转为对象
     - 使用情况:
       - java参数是基本类型/string(可忽略)
       - 传入字符串1,2,3要自动转成List
       - 前后端参数名不一致
       - 设置默认值/可选参数
   - @PathVariable: Path参数, 获取 url 中 {} 中的参数值, eg. /employee/{id}/status
   - @RestController: @Controller + @ResponseBody, RESTful风格控制器; @RestController(spring类名) 防止同名类在 spring 中冲突
     - 方法返回值直接作为响应体（通常是将值转为 JSON），不经过视图解析器
   - @Controller: 返回页面，标识控制器类，处理 Web 请求，通常配合视图模板使用
   - @Component: 通用组件注解，spring扫描（大部分spring注解包含），将类标记为 Spring 容器管理的 Bean
8. 事务开启情况:
   
   - 多个写操作(增删改)
   - 先查询后修改
   - 多表级联操作(操作主子表)
   - 金融相关
   - 多个读操作(视情况而定, 开启 readOnly)

#### Redis

1. 简介:
   
   - 基于内存的 key-value 结构数据库
   - 内存存储, 读写性能高
   - 适合存储短期有大量用户的情况
2. 安装设置:
   
   - github 下载安装包, 解压缩即可
   - 密码: 在 redis.windows.conf 中设置 requirepass 密码, 即可
   - 服务端: cmd中 redis-server.exe redis.windows.conf
   - 客户端: cmd中 redis-cli -h localhost -p 6379 -a 密码
3. 数据类型: key 是字符串类型, value 有以下 5 种
   
   - 字符串 string:
   - 哈希 hash: 类似map, 可存对象
   - 列表 list:
   - 集合 set: 无重复
   - 有序集合 sorted set / zset: 每个元素关联一个分数(double), 按分数升序
4. 常用命令(不分大小写)
   
   1. 字符串操作命令
      
      - SET key value: 设置指定 key 的值, 已有 key 时会覆盖
      - GET key: 获取指定 key 的值
      - SETEX key seconds value: 设置指定 key 的值, 并将 key 的过期时间设为 seconds 秒, 过期后 key 自动被清理掉(短信验证码)
      - SETNX key value: 只有在 key 不存在时才设置 key 的值(分布式锁)
   2. Hash 操作命令
      
      - HSET key field value: 将哈希表key中的字段field的值设为value
      - HGET key field: 获取存储在哈希表中指定字段的值
      - HDEL key field: 删除存储在哈希表中的指定字段
      - HKEYS key: 获取哈希表中所有字段
      - HVALS key: 获取哈希表中所有值
   3. 列表操作命令: (第一个l/r代表左右首尾)
      
      - LPUSH/RPUSH key value1 value2...: 将一个/多个值插入到列表
      - LRANGE key start stop(0开始): 获取列表区间 [start, stop] 的元素
      - RPOP/LPOP key(返回移除的值): 移除并获取列表最后一个元素
      - LLEN key: 获取列表长度
   4. 集合操作命令
      
      - SADD key member1 member2 ...: 向集合添加一个/多个成员
      - SMEMBERS key: 返回集合中的所有成员
      - SCARD key: 获取集合的成员数
      - SINTER key1 key2 ...: 返回给定所有集合的交集(都有)
      - SUNION key1 key2 ...: 返回所有给定集合的并集(一方有即可)
      - SREM key member1 member2 ...: 删除集合中一个或多个成员
   5. 有序集合操作命令(score有丢失精度问题)
      
      - ZADD key score1 member1 score2 member2 ...: 向有序集合添加一个/多个成员
      - ZRANGE key start stop [WITHSCORES]: 返回区间 [start, stop] 内的成员, 带有 WITHSCORES 则一起返回分数
      - ZINCRBY key increment member: 有序集合中对指定成员的分数加上增量increment
      - ZREM key member1 member2 ...: 移除有序集合中的一个或多个成员
   6. 通用操作命令
      
      - KEYS pattern: 查找所有符合给定模式(pattern)的 key
      - EXISTS key: 检查是否存在指定 key
      - TYPE key: 返回 key 存储的值的类型
      - DEL key1 key2 ...: 删除 key
5. Java 中操作 Redis (Spring Data Redis 使用, 详见 Test.java)
   
   1. 导入 pom.xml 依赖, 配置 application redis数据源
   2. 编写配置类 RedisConfiguration, 创建 RedisTemplate 对象
   3. 通过注入 RedisTemplate 对象操作 Redis


## 开发过程小技巧

1. TODO 用来标识后续要改进的部分
2. 前后端分离开发流程: 定制接口 -> 前端开发/后端开发自测 -> 联调校验格式 -> 自动化测试
3. 在 Swagger/ApiFox 中修改数据, 会跳过各种前端检测使得数据不合理
4. application-dev.yml内包含敏感信息(eg.oss密钥...), 不能一起推送到GitHub, 在 .gitignore文件中添加 **/application-dev.yml
5. 本地提交 git 但远程推送失败, 修改后重新提交

- ```
  cd D:\Project_Sky\code\sky-take-out
  :: 1. 撤销上一次提交（保留代码修改）
  git reset --soft HEAD~1
  :: 2. 取消暂存所有文件
  git reset HEAD .
  :: 3. 重新添加文件（排除 application-dev.yml）
  git add .
  :: 4. 检查是否有敏感文件被添加
  git status
  :: 5. 如果看到 application-dev.yml 在列表中，移除它
  git rm --cached sky-server/src/main/resources/application-dev.yml
  :: 6. 重新提交
  git commit -m "xxx"
  :: 7. 推送到远程
  git push origin main
  ```

6. 不要在循环中使用 sql, 建议使用动态 sql 查询, 传 list 到 mapper, 在 xml 写 foreach
7. mysql 中 join 默认是 inner join

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

- 新增菜品
  - 根据类型查询分类(分类管理处已完成)
  - 文件上传(阿里云oss存储)
    - 返回文件上传路径, 用于回显图片
  - 新增菜品 @Transactional
    - 保存菜品及其口味
    - 插入一条菜品表: 将数据库生成的自增 ID 回填到传入对象的 id 属性中
    - 批量插入口味表: 获取菜品id, 遍历插入菜品id, insertBatch
    - insertBatch: 在 mapper.xml 中使用 foreach 批量插入
- 菜品分页查询
  - 返回值包含分类名称, 设计返回值类型 VO类, 多表查询获取名称
- 菜品批量删除 @Transactional
  - 起售的菜品不能删除, 套餐关联的菜品不能删除, 有一个菜品不能删则操作失败, 抛出异常
    - 分别判断是否有起售/关联套餐
  - 先删除对应口味, 再批量删除菜品(避免外键约束)
- 修改菜品
  - 查询菜品用于回显
    - 分开查询关联口味(多表一起查询需要映射且复用性较低)
  - 根据类型查询分类(分类管理处已完成): 用以选择菜品分类
  - 文件上传(新增菜品处已完成)
  - 修改菜品
    - 修改口味: 先删除原口味, 再新增口味(要设置菜品id)
- 根据分类查找菜品(用以后续套餐选择菜品)

#### 套餐管理

- 新增套餐 @Transactional
  - 插入一条套餐数据, 之后获取生成的 id, 遍历填充关系数据的套餐 id
  - 插入多条套餐-菜品关系数据
- 套餐分页查询
- 删除套餐 @Transactional
  - 起售中的套餐不能删除
  - 先删除套餐菜品关系, 再删除套餐
- 根据 id 查询套餐
  - 分别查询套餐表和套餐-菜品关系表, 再组合在一起
- 修改套餐 @Transactional
  - 修改套餐表
  - 修改套餐-菜品关系表, 先删再加
- 起售/停售
  - 起售时如果有停售菜品则失败

#### 营业状态管理

- 设置营业状态(使用 redis 存储)
  - 打烊: 客户无法下单点餐
  - 查询营业状态(管理/用户端)
  - 修改营业状态

## 改进课程内容

#### 菜品管理

1. 菜品批量删除
   - 课程使用 for 循环遍历 ids, sql 查询每个 id 是否起售, 获取所有 ids 对应的套餐 id 进行返回
   - 问题: for 循环内使用 sql, 获取所有 id 消耗性能
   - 改进: 使用 exist/in 进行查询, dish 表内存在 ids 包含的 id, 且起售则返回 true; setmeal_dish 表内存在包含 ids 对应的套餐 id 则返回
   - 优势: 不在循环内使用 sql, 使用 exist 只需找到一条符合即可返回, 提高性能
   - 进一步改进方案: 能删除的就删除, 不能的返回 ids 和原因(起售/关联套餐)
   - 进一步解决方案: 一开始就将 ids 参数分为三部分, 分别进行删除/返回原因

