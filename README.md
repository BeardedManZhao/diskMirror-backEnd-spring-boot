# diskMirror-backEnd-spring-boot

diskMirror 后端服务器的 SpringBoot 版本，此版本中拓展了 DiskMirrorBackEnd，是一个完全的SpringBoot项目！

## 我如何使用？

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

## 更新日志

### 2024.02.23

diskMirror SpringBoot 版本首次发布，详情请查看：https://github.com/BeardedManZhao/diskMirror-backEnd-spring-boot.git

## 更多

----

- diskMirror starter SpringBoot：https://github.com/BeardedManZhao/diskMirror-spring-boot-starter.git
- diskMirror 后端服务器版本（MVC）：https://github.com/BeardedManZhao/DiskMirrorBackEnd.git
- diskMirror 后端服务器版本（SpringBoot）：https://github.com/BeardedManZhao/diskMirror-backEnd-spring-boot.git
- diskMirror Java API 版本：https://github.com/BeardedManZhao/DiskMirror.git