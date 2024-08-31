package top.lingyuzhao.diskMirror.backEnd.springConf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.lingyuzhao.utils.StrUtils;

import java.util.Arrays;

/**
 * diskMirror 的后端服务器主类
 *
 * @author zhao
 */
@SpringBootApplication(scanBasePackages = {
        "top.lingyuzhao.diskMirror.starter",
        "top.lingyuzhao.diskMirror.backEnd.springController"
})
public class DiskMirrorMAIN implements WebMvcConfigurer {
    public final static Logger logger = LoggerFactory.getLogger(DiskMirrorMAIN.class);

    /**
     * 跨域允许列表
     */
    public static String[] corsAllowOrigin;

    public static void main(String[] args) {

        // 首先解析参数 1 位置 是否是
        // 首先解析参数 2 位置 是否有配置跨域允许资源
        if (args.length >= 2) {
            corsAllowOrigin = StrUtils.splitBy(args[1], ',');
        } else {
            corsAllowOrigin = new String[]{};
        }

        logger.info("允许跨域列表：{}", Arrays.toString(corsAllowOrigin));

        final ConfigurableApplicationContext run;
        try {
            run = SpringApplication.run(DiskMirrorMAIN.class);
        } catch (RuntimeException r) {
            logger.error("""
                    （x_x）!!!
                        ↘
                         +-----------------------------------------------------+
                         |糟糕！springBoot 容器启动失败，请按照spring提供的错误来检查!!|
                         +-----------------------------------------------------+
                    """, r);
            return;
        }

        final ConfigurableListableBeanFactory beanFactory = run.getBeanFactory();
        // 检查 diskMirror
        if ((
                beanFactory.containsBean("getAdapter") || beanFactory.containsBean("top.lingyuzhao.diskMirror.core.Adapter")) &&
                beanFactory.containsBean("top.lingyuzhao.diskMirror.backEnd.springController.FsCrud")) {
            logger.info("diskMirror-backEnd-spring-boot 已经就绪!!!");
        } else {
            logger.error("""
                    （x_x）!!!
                        ↘
                         +-------------------------------------------------------+
                         |糟糕！虽然 springBoot 容器启动了，但 diskMirror 没有启动成功!!|
                         +-------------------------------------------------------+
                    
                    # 请您检查是否在 maven 中包含了下面的依赖，版本可以按照您的需求来进行配置
                    ```
                            <!--  设置 SpringBoot3 做为父项目 所有的 SpringBoot 项目都应该以此为父项目  -->
                            <parent>
                                <groupId>org.springframework.boot</groupId>
                                <artifactId>spring-boot-starter-parent</artifactId>
                                <version>3.0.5</version>
                            </parent>
                    
                            <dependencies>
                    
                                <!-- 在 SpringBoot 项目中 有着许多已经实现好的 starter -->
                                <!-- 在这里我们引用的就是 web 中的 starter -->
                                <dependency>
                                    <groupId>org.springframework.boot</groupId>
                                    <artifactId>spring-boot-starter-web</artifactId>
                                </dependency>
                    
                                <!-- diskMirror 的 starter 依赖 帮助您获取到适配器-->
                                <dependency>
                                    <groupId>io.github.BeardedManZhao</groupId>
                                    <artifactId>diskMirror-spring-boot-starter</artifactId>
                                    <version>1.0.2</version>
                                </dependency>
                    
                                <!-- diskMirror 的后端服务器依赖 会自动的使用 diskMirror starter 获取到的适配器 -->
                                <dependency>
                                    <groupId>io.github.BeardedManZhao</groupId>
                                    <artifactId>DiskMirrorBackEnd</artifactId>
                                    <version>2024.02.21</version>
                                </dependency>
                    
                                <!-- diskMirror 后端服务器需要的依赖 -->
                                <dependency>
                                    <groupId>javax.servlet</groupId>
                                    <artifactId>javax.servlet-api</artifactId>
                                    <version>4.0.1</version>
                                </dependency>
                    
                            </dependencies>
                    ```
                    # 如果您确定依赖没有问题，请您检查您的配置文件中是否启用了 diskMirror starter 下面就是配置文件中的配置项
                    ```
                    disk-mirror:
                      # 此配置项目代表的就是是否启用 diskMirror 如果设置为 false 则代表不启用，diskMirror 的starter 将不会被加载，需要您手动设置此参数
                      enable-feature: true
                    ```
                    
                    # 如果您确定配置项目启动，则请您确定您的配置文件中是否缺少某些配置，下面是一个配置模板，一般来说，您的配置文件中应该是基于下面的模板来进行修改的
                    ```
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
                    
                    - 如果还是具有此问题 请您在 https://github.com/BeardedManZhao 中联系作者！
                    """);
            run.stop();
        }
    }

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        if (corsAllowOrigin.length > 0) {
            registry.addMapping("/**")
                    .allowedHeaders("*")
                    .allowedOrigins(corsAllowOrigin)
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowCredentials(true)
                    .maxAge(3600L);
        }
    }
}
