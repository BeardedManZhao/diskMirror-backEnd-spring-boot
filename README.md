# diskMirror-backEnd-spring-boot

diskMirror 后端服务器的 SpringBoot 版本，此版本中拓展了 DiskMirrorBackEnd，是一个完全的SpringBoot项目！

## 我如何部署与配置

### docker 方式部署 diskMirror【省去了 JDK 和系统相关的操作只需要修改配置，过程中需要等待程序自动编译】

![20240425173711](https://github.com/BeardedManZhao/diskMirror-backEnd-spring-boot/assets/113756063/93b519e7-357e-4621-9127-d4edbfd47b3a)

您可以访问 [diskMirror-docker](https://github.com/BeardedManZhao/diskMirror-docker.git) 项目来了解有关 docker
部署的操作，此操作更加简单！

### Linux/Windows 方式直接部署【省去了 docker 镜像编译，需要单独的配置服务器环境】

![image](https://github.com/BeardedManZhao/diskMirror-backEnd-spring-boot/assets/113756063/e1455546-a602-48d4-99ef-c85234ae4f93)

您只需要从 包仓库 中下载 jar 并修改配置文件，然后根据此页面的引导进行安装和启动即可，下面是配置文件的模板。

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

![image](https://private-user-images.githubusercontent.com/113756063/325888391-d1f4c15a-193c-40d0-ae0c-9c3fb3a499bb.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MTQxMTczNzgsIm5iZiI6MTcxNDExNzA3OCwicGF0aCI6Ii8xMTM3NTYwNjMvMzI1ODg4MzkxLWQxZjRjMTVhLTE5M2MtNDBkMC1hZTBjLTljM2ZiM2E0OTliYi5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjQwNDI2JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI0MDQyNlQwNzM3NThaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1mODc5NjBjN2QwYmI3YmYwNGVjNmE1Mjg4MmI5ZjQ4NjA2MTRjMmUwOTc4ZTgzZDg2NWNmM2MzM2MzOTU0NzQ0JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCZhY3Rvcl9pZD0wJmtleV9pZD0wJnJlcG9faWQ9MCJ9.tHkpSkdSkNQLBW-Ih6Sr5nsRVlZo61NiRRAdB6PSxVM)

![image](https://github.com/BeardedManZhao/diskMirror-backEnd-spring-boot/assets/113756063/24dd3537-3e4d-43b1-b1ec-d86790a9c277)

## 更多说明

### 前端组件集成方式

> 这里的说明是针对开发者/想要修改 diskMirror前端界面的用户所写的，若无此类需求，可以直接跳过这里哦！

[diskMirror-front](https://github.com/BeardedManZhao/diskMirror-front.git) 项目做为此项目的前端组件，融合方式如下所示

```
直接将前端项目源码中的 web 目录粘贴到项目 static 目录中，并使用项目 /conf/indexConfig.js 覆盖 static/conf/indexConfig.js 中的配置文件即可
```

## 更新日志

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
