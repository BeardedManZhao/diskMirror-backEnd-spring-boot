# diskMirror-backEnd-spring-boot

diskMirror 后端服务器的 SpringBoot 版本，此版本中拓展了 DiskMirrorBackEnd，是一个完全的SpringBoot项目！

## 我如何使用？

### 部署与配置

您只需要将此项目源码克隆，然后修改配置文件即可，下面是配置文件的模板。

```yaml
disk-mirror:
  # 此配置项目代表的就是是否启用 diskMirror 如果设置为 false 则代表不启用，diskMirror 的starter 将不会被加载，需要您手动设置此参数
  enable-feature: true
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
```

配置完毕之后，您只需要将 MAIN 方法启动即可。

当然，您也可以直接在启动参数中设置配置文件的使用，下面展示的就是使用 Java 命令启动 SpringBoot
包的语法，其中包含两个路径，第一个是配置文件的路径，第二个是 SpringBoot 包的路径，这样就可以实现让
SpringBoot 自动加载您写好的配置文件。

至于需要使用的包和配置文件模板，您可以亲自编译，也可以在 [历史版本存储库](https://github.com/BeardedManZhao/diskMirror-backEnd-spring-boot/releases)
中进行下载!!!!

```
# java 【-Dspring.config.location=要使用的yaml文件的路径】 -jar 【jar包的路径 data(目前代表的是数据存储者的标识，直接输入就好)】【跨域允许列表】
java -Dspring.config.location=file:/xxx/xxx/xxx/application.yaml -jar /xxx/xxx/xxx/diskMirror-backEnd-spring-boot-1.0-SNAPSHOT.jar data 跨域主机1,跨域主机2,...
```

### 我如何使用其中的服务？

此项目是继承于 diskMirrorBackEnd 项目的，因此所有的服务使用方法与 DiskMirrorBackEnd
中是一样的，您可以 [点击这里前往 diskMirrorBackEnd](https://www.lingyuzhao.top/?/linkController=/articleController&link=88968287)
的文档进行查看。

您还可以直接使用此项目的前端界面来进行操作！

## 更多说明

### 前端组件集成方式

> 这里的说明是针对开发者/想要修改 diskMirror前端界面的用户所写的，若无此类需求，可以直接跳过这里哦！

[diskMirror-front](https://github.com/BeardedManZhao/diskMirror-front.git) 项目做为此项目的前端组件，融合方式如下所示
```
直接将前端项目源码中的 web 目录粘贴到项目 static 目录中，并使用项目 /conf/indexConfig.js 覆盖 static/conf/indexConfig.js 中的配置文件即可
```

## 更新日志

### 2024.04.13 开始开发

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
- diskMirror Java API 版本：https://github.com/BeardedManZhao/DiskMirror.git