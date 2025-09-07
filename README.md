# diskMirror-backEnd-spring-boot

diskMirror 后端服务器的 SpringBoot 版本，此版本中拓展了 DiskMirrorBackEnd，是一个完全的SpringBoot项目！

## 我如何部署与配置

> 可以联系微信（微信号 CH-liming02240898）进行定制修改或代替部署哦！

### docker 方式部署 diskMirror【省去了 JDK 和系统相关的操作只需要修改配置，过程中需要等待程序自动编译】

![20240425173711](https://github.com/BeardedManZhao/diskMirror-backEnd-spring-boot/assets/113756063/93b519e7-357e-4621-9127-d4edbfd47b3a)

您可以访问 [diskMirror-docker](https://github.com/BeardedManZhao/diskMirror-docker.git) 项目来了解有关 docker
部署的操作，此操作更加简单！

### Linux/Windows 方式直接部署【省去了 docker 镜像编译，需要单独的配置服务器环境】

![image](https://github.com/BeardedManZhao/diskMirror-backEnd-spring-boot/assets/113756063/e1455546-a602-48d4-99ef-c85234ae4f93)

您只需要从 包仓库 中下载 jar 并修改配置文件，然后根据此页面的引导进行安装和启动即可，下面是配置文件的模板。

```yaml
disk-mirror:
  # 此配置项目代表的就是是否启用 diskMirror 如果设置为 false 则代表不启用，diskMirror 的starter 将不会被加载
  enabled-feature: true
  # 要使用的盘镜适配器类型 在这里默认数值是本地盘镜适配器，具体的适配器 您可以查阅 top.lingyuzhao.diskMirror.core.DiskMirror 类
  adapter-type: "LocalFSAdapter"
  # 要被盘镜管理的目录 用于存储数据的目录 此目录是真实目录
  root-dir: "/DiskMirror"
  # 一般来说 如果对接带第三方文件系统 而非本次文件系统 则此参数则会派上用场，其代表的就是第三方文件系统的地址
  fs-default-fs: "hdfs://localhost:8020/"
  # 当处理之后，如果处理无错误会返回一个结果状态，此数值代表的就是是否正确处理
  ok-value: "ok!!!!"
  # 返回结果的key 返回结果中 结果状态的字段名字
  res-key: "res"
  # 协议前缀，默认为http 不同协议前缀有不同的意义，用于拼接 url
  protocol-prefix: "http://localhost:80/"
  # 参数 可能会派上用场，在不同的适配器中有不同的实现
  params: { }
  # 用户磁盘配额 每个盘镜空间的磁盘最大空间数值，单位是字节
  user-disk-mirror-space-quota: 134217728
  # 安全密钥
  secure-key: ""
  # 指定的几个用户的空间对应的容量
  space-max-size: { }
  # 指定后端服务器相关的配置
  backend:
    # 指定后端服务器的关机控制器
    shutdown-controller:
      # 这里代表是否要启用关机控制器
      enable: false
      # 这里代表的是关机控制器的操作密钥 此密钥独立于 diskMirror 可防止被其它用户关机
      password: "zhao"
      # 这里代表的是在关机之前预留的时间，一般是用来将关机的信息返回给客户端的 单位是毫秒
      timeout: 5000
  # 设置要使用的空间配置方式 目前支持 HashMapper 和 JedisMapper 两种，用于将不同空间的配置信息放到第三方平台
  use-space-config-mode: "HashMapper"
  # redis 配置
  redis-host-port-db: "127.0.0.1:6379:0"
  # redis 密码
  redis-password: "0000"
  # 图像文件压缩模块配置
  image-compress-module:
    # 设置位 true 代表启用~ 反之则不启用 不启用的将不会被加载到 diskMirror 中
    enable: false
    # 设置 png 调色板模式 默认是 RGB_8 代表 8 位压缩
    palette-png: "RGB_8"
    # 设置 调色板生成器，默认是 X255
    palette-generator: "X255"
    # 设置是否支持透明 默认是 false
    transparent: false
  # 设置校验模块
  verifications:
    # 设置读取操作中的 sk 校验 这样所有的读取操作都需要经过这个模块了
    - "SkCheckModule$read"
    # 设置写入操作中的 sk 校验 这样所有的写入操作都需要经过这个模块了
    - "SkCheckModule$writer"

# Spring Boot 配置文件
spring:
  # mvc 配置
  mvc:
    resources:
      cache:
        # 设置缓存时间 这里是以秒为单位 代表的 1 小时
        period: 3600
        # cachecontrol 具有更多的配置，其中也包括 period 但是叫做 max-age 并且 通过 cachecontrol 配置的优先级更高
        # 也就是说 在这里配置最后会直接覆盖掉 cache.period
        # 一般来说 需要更详细的配置就需要在这里操作
        cachecontrol:
          # 设置缓存的过期时间 这是告诉浏览器此资源缓存 7200 秒
          max-age: 7200
        # 配置资源的对比策略，这里是使用的最后一次修改的时间
        # 这个配置项在 spring 5.2.0 之后才支持
        # 如果不配置此选项 默认的策略是 Last-Modified
        # Last-Modified 的策略是根据资源的最后修改时间来判断资源是否被修改过
        # 如果资源被修改过 则会返回 304 状态码
        # 如果资源没有被修改过 则会返回 200 状态码
        # 但是 Last-Modified 的策略存在一个问题，就是如果资源的最后修改时间被修改过，那么资源的最后修改时间也会被修改，这样就会导致资源的最后修改时间永远是当前时间。
        # 这个配置项默认是 true
        use-last-modified: true
  # 配置 HTTP 多部分文件上传
  servlet:
    # 文件上传相关配置
    multipart:
      # 指定临时文件的存储位置
      # 注意：此目录必须存在，并且应用程序应具有写入权限
      location: /opt/app/diskMirror-spring-boot/temp
      # 指定临时文件的位置（与location配置相同）
      # 这个配置在较新版本的Spring中可能不是必需的
      # temp-location: /opt/app/diskMirror-spring-boot/temp
      # 单个文件的最大大小
      max-file-size: 1024MB
      # 整个请求中所有文件的总大小的最大值，默认值为多部分数据的最大大小
      max-request-size: 1030MB
      # 是否启用文件大小检查，默认为true
      enabled: true
      # 当文件大小超过这个阈值时，才会使用临时文件存储
      file-size-threshold: 4MB
```

配置完毕之后，您只需要将 MAIN 方法启动。

当然，您也可以直接在启动参数中设置配置文件的使用，下面展示的就是使用 Java 命令启动 SpringBoot
包的语法，其中包含两个路径，第一个是配置文件的路径，第二个是 SpringBoot 包的路径，这样就可以实现让
SpringBoot 自动加载您写好的配置文件。

至于需要使用的包和配置文件模板，您可以亲自编译，也可以在 [历史版本存储库](https://github.com/BeardedManZhao/diskMirror-backEnd-spring-boot/releases)
中进行下载!!!!

```
# java 【-Dspring.config.location=要使用的yaml文件的路径】 -jar 【jar包的路径 data(目前代表的是数据存储者的标识，直接输入就好)】【跨域允许列表】
java -Dspring.config.location=file:/xxx/xxx/xxx/application.yaml -jar /xxx/xxx/xxx/diskMirror-backEnd-spring-boot-1.0-SNAPSHOT.jar data 跨域主机1,跨域主机2,...
```

## 我如何使用其中的服务？

此项目是继承于 diskMirrorBackEnd 项目的，因此所有的服务使用方法与 DiskMirrorBackEnd
中是一样的，项目中的 diskMirror 脚本可以帮助您直接操作服务器，在 `2024.04.12` 之后发布的版本中可以使用 WEBUI 操作

有关更多详细信息，您可以 [点击这里前往 diskMirrorBackEnd](https://www.lingyuzhao.top/?/linkController=/articleController&link=88968287)
的文档进行查看。

## WEBUI 示例

![image](https://github.com/BeardedManZhao/diskMirror-backEnd-spring-boot/assets/113756063/07f0975c-e544-4298-a7be-c7eba8983f0b)

![image](https://github.com/user-attachments/assets/44d6b078-6d76-4800-9374-db7b908bbf9e)

![image](https://github.com/BeardedManZhao/diskMirror-backEnd-spring-boot/assets/113756063/24dd3537-3e4d-43b1-b1ec-d86790a9c277)

## 更多说明

### 前端组件集成方式

> 这里的说明是针对开发者/想要修改 diskMirror前端界面的用户所写的，若无此类需求，可以直接跳过这里哦！

[diskMirror-front](https://github.com/BeardedManZhao/diskMirror-front.git) 项目做为此项目的前端组件，融合方式如下所示

```
直接将前端项目源码中的 web 目录粘贴到项目 static 目录中，并使用项目 /js/conf/indexConfig.js 覆盖 static/js/conf/indexConfig.js 中的配置文件即可
```

## 更新日志

### 2025.09.06

- 修复了分享功能问题，移除了冗余日志

### 2025.07.15

- 优化了视频类文件下载的速度！让其支持分块传输！

### 2025.07.13

- 前端页面升级为 2.1.2 版本
- 文件下载功能支持文件名称
- 支持了文件分享功能

### 2025.05.25

- 为 DiskMirror 核心组件版本升级到 1.5.3
- 对 downLoad 第一代也优化，其和第二代性能无异，且兼容性更好
- 对 js 文件优化，默认使用第一代下载

### 2025.05.20

- 为 DiskMirror 核心组件版本升级到 1.5.2
- 为 download 操作添加了新的第二代，支持客户端缓存的下载方式，多次下载的情况下速度更快。

### 2025.04.15

- 为 `getUrlsNoRecursion` 进行了支持！
- 为 DiskMirror 核心组件版本升级到 1.4.9
- 为参数错误的情况的异常返回的json 做了启动时加载，避免了返回信息时候的频繁的json对象序列化操作，可以增强性能！

### 2025.02.04

- 为 DiskMirror 核心组件版本升级到 1.4.4
- 支持使用 jedisMapper 存储空间配置数据！

### 2024.12.05

- 为 DiskMirror 核心组件版本升级到 1.3.5
- 移除了 MVC 后端模块 进行单独设计与实现！

### 2024.11.06

- 为 DiskMirror 核心组件版本升级到 1.3.2
- 下载文件时，对于文件大小的显示做了优化，如果被下载的文件数据流中无文件字节数显示，则不会强制设置为 0，这能够让文件的下载模块支持多种适配器！
- 解决了下载 预览 文件时，sk 校验不论如何都不通过的问题，问题的发生是因为在 springBoot
  的版本中，废弃了 `top.lingyuzhao.diskMirror.backEnd.conf.DiskMirrorConfig` 的配置，但下载和预览的sk校验模块需要使用到它，本次更新中，我们将配置类进行同步！

### 2024.11.02

- 集成了 较新版本的 [diskMirror starter 1.0.3版本](https://github.com/BeardedManZhao/diskMirror-spring-boot-starter.git)
- 启动错误信息的展示优化

### 2024.09.10

- 启用了关机服务的支持
- 为 DiskMirror-Front 版本升级到 2.1.1

### 2024.09.08

- 为 DiskMirror 核心组件版本升级到 1.2.7
- 这启用了对于密钥的支持！
- 启动服务的时候，会打印出密钥的具体数值，不再需要额外的计算出可用的数字密钥！

### 2024.09.02

- 为 DiskMirror-Front 版本升级到 2.1.0
- 为 DiskMirror 核心组件版本升级到 1.2.5

### 2024.08.31

- 优化了文件上传逻辑，可避免由于文件数据上传导致的内存溢出！
- 集成了新版本的 DiskMirror-Front

### 2024.07.07

- 尝试集成 `setSpaceSk` 服务，此服务在 1.2.4 版本中不具备安全性，将在后续尝试继续集成此服务！

### 2024.04.26

- 集成 文件进度条 API 支持 `getAllProgressBar` 服务，能够获取到指定空间的所有的进度条信息。
- 集成 diskMirror 1.2.2 版本

### 2024.04.25

- 集成 1.0.5 的前端代码，能够支持文本数据的在线编辑！

### 2024.04.13

- 优化了前端代码
- 优化默认配置，加上了缓存和表单限设置

### 2024.04.12

- 集成了前端项目
- 删除了不必要的类
- 新增转存功能支持！

### 2024.03.28

- 新增 `public String getUseSize(HttpServletRequest httpServletRequest);` 函数，能够显式的获取到磁盘的使用情况。
- 依赖优化！将所有需要的依赖都导入了进来
- 版本打印优化，支持更加详细的版本打印操作！

### 2024.03.26

- 更新了其中的一些组件
- 修复了 某些 API 报 空指针的错误
- 修复了 SpringBoot3 中 使用 某些API会 报错的问题，报错内容如下（新版本中已修复！）

```
2024-03-26T20:19:02.342+08:00 ERROR 26376 --- [nio-8080-exec-5] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed: java.lang.IllegalStateException: No primary or single unique constructor found for interface javax.servlet.http.HttpServletRequest] with root cause

java.lang.IllegalStateException: No primary or single unique constructor found for interface javax.servlet.http.HttpServletRequest
	at org.springframework.beans.BeanUtils.getResolvableConstructor(BeanUtils.java:268) ~[spring-beans-6.0.7.jar:6.0.7]
	at org.springframework.web.method.annotation.ModelAttributeMethodProcessor.createAttribute(ModelAttributeMethodProcessor.java:221) ~[spring-web-6.0.7.jar:6.0.7]
	at org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor.createAttribute(ServletModelAttributeMethodProcessor.java:85) ~[spring-webmvc-6.0.7.jar:6.0.7]
	at org.springframework.web.method.annotation.ModelAttributeMethodProcessor.resolveArgument(ModelAttributeMethodProcessor.java:149) ~[spring-web-6.0.7.jar:6.0.7]
	at org.springframework.web.method.support.HandlerMethodArgumentResolverComposite.resolveArgument(HandlerMethodArgumentResolverComposite.java:122) ~[spring-web-6.0.7.jar:6.0.7]
	......
```

### 2024.02.23

diskMirror SpringBoot 版本首次发布，详情请查看：https://github.com/BeardedManZhao/diskMirror-backEnd-spring-boot.git

## 更多

----

- diskMirror starter SpringBoot：https://github.com/BeardedManZhao/diskMirror-spring-boot-starter.git
- diskMirror 后端服务器版本（MVC）：https://github.com/BeardedManZhao/DiskMirrorBackEnd.git
- diskMirror 后端服务器版本（SpringBoot）：https://github.com/BeardedManZhao/diskMirror-backEnd-spring-boot.git
- diskMirror 后端服务器版本（SpringBoot-docker）：https://github.com/BeardedManZhao/diskMirror-docker.git
- diskMirror Java API 版本：https://github.com/BeardedManZhao/DiskMirror.git
