package top.lingyuzhao.diskMirror.backEnd.springConf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * diskMirror 的后端服务配置 这里是一些自动配置的配置
 *
 * @author zhao
 */
@Configuration
@ConfigurationProperties(prefix = "disk-mirror.backend")
public class DiskMirrorBackEndProperties {

    /**
     * shutdownController 配置
     */
    ShutdownController shutdownController = new ShutdownController(false, "");

    public ShutdownController getShutdownController() {
        return shutdownController;
    }

    public void setShutdownController(ShutdownController shutdownController) {
        this.shutdownController = shutdownController;
    }

    public static class ShutdownController {

        /**
         * 是否·启用 shutdownController 如果为 true 则会启用 shutdownController 我们可以直接通过 shutdown 服务关掉 diskMirror 服务器
         */
        boolean enable;

        /**
         * shutdownController 的密码 如果密码不正确 则不允许关机
         */
        String password;

        /**
         * 接收到关机指令之后 需要延迟多久才能关机
         */
        long timeout = 5000;

        public ShutdownController(boolean enable, String password) {
            this.enable = enable;
            this.password = password;
        }

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public long getTimeout() {
            return timeout;
        }

        public void setTimeout(long timeout) {
            this.timeout = timeout;
        }
    }
}
