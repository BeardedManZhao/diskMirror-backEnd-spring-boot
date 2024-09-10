package top.lingyuzhao.diskMirror.backEnd.springController;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.lingyuzhao.diskMirror.backEnd.conf.WebConf;
import top.lingyuzhao.diskMirror.backEnd.springConf.DiskMirrorBackEndProperties;
import top.lingyuzhao.diskMirror.backEnd.springConf.DiskMirrorMAIN;
import top.lingyuzhao.diskMirror.backEnd.utils.HttpUtils;
import top.lingyuzhao.diskMirror.conf.Config;
import top.lingyuzhao.diskMirror.core.Adapter;
import top.lingyuzhao.diskMirror.core.DiskMirror;
import top.lingyuzhao.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;


/**
 * 文件系统的增删操作接口
 *
 * @author zhao
 */
@Component("top.lingyuzhao.diskMirror.backEnd.springController.FsCrud")
@Controller
@RequestMapping(
        value = "FsCrud",
        // 告知前端页面，回复数据的解析方式
        produces = "text/html;charset=UTF-8",
        method = {RequestMethod.POST}
)
@ConditionalOnClass(value = {top.lingyuzhao.diskMirror.core.Adapter.class})
public class FsCrud implements CRUD {

    /**
     * 从配置类中获取到适配器对象
     */
    protected final Adapter adapter;

    protected final Config DISK_MIRROR_CONFIG;

    protected final DiskMirrorBackEndProperties diskMirrorBackEndProperties;

    /**
     * 直接使用在外部初始化好的适配器来进行初始化
     *
     * @param adapter    在外界实例化好的适配器对象
     * @param properties 配置类 通过 starter 获取到的配置类
     */
    public FsCrud(Adapter adapter, top.lingyuzhao.diskMirror.starter.conf.properties.DiskMirrorProperties properties, DiskMirrorBackEndProperties diskMirrorBackEndProperties) {
        this.adapter = adapter;
        this.DISK_MIRROR_CONFIG = adapter.getConfig();
        this.diskMirrorBackEndProperties = diskMirrorBackEndProperties;
        DiskMirrorMAIN.logger.info("diskMirror 明文密钥：\"{}\" 被解析为数字密钥：{}", properties.getSecureKey(), adapter.getConfig().getString(Config.SECURE_KEY));
        this.DISK_MIRROR_CONFIG.put(WebConf.IO_MODE, properties.getAdapterType());
    }

    /**
     * 获取文件加密的密钥
     *
     * @param httpServletRequest 来自前端的请求对象
     * @param defKey             默认的密钥值，如果请求对象中没有包含 请求对象 则会使用此参数！
     * @param jsonObject         需要用来存储 key 的 json对象
     */
    private static void getDiskMirrorXorSecureKey(HttpServletRequest httpServletRequest, int defKey, JSONObject jsonObject) {
        if (httpServletRequest == null || httpServletRequest.getCookies() == null) {
            jsonObject.put("secure.key", defKey);
            return;
        }
        for (Cookie cookie1 : httpServletRequest.getCookies()) {
            if ("diskMirror_xor_secure_key".equals(cookie1.getName())) {
                jsonObject.put("secure.key", HttpUtils.xorDecrypt(Integer.parseInt(cookie1.getValue())));
                break;
            }
        }
    }

    /**
     * 增加函数
     *
     * @param httpServletRequest 请求对象
     * @return 返回结果
     */
    @Override
    public String add(HttpServletRequest httpServletRequest, @RequestPart("params") MultipartFile params, @RequestPart("file") MultipartFile file) {
        try {
            // 校验数据
            if (file == null || params == null) {
                if (file == null) {
                    return HttpUtils.getResJsonStr(new JSONObject(), "您的文件数据为空，请确保您要上传的文件数据存储在 ”file“ 对应的请求数据包中!!!");
                } else {
                    return HttpUtils.getResJsonStr(new JSONObject(), "您的请求参数为空，请确保您的请求参数 json 字符串存储在 ”params“ 对应的请求数据包中!");
                }
            }
            try (
                    final InputStream inputStream0 = params.getInputStream();
                    final InputStream inputStream1 = file.getInputStream()
            ) {
                return adapter.upload(inputStream1, JSONObject.parseObject(IOUtils.getStringByStream(inputStream0, DISK_MIRROR_CONFIG.getString(Config.CHAR_SET)))).toString();
            }
        } catch (IOException | RuntimeException e) {
            WebConf.LOGGER.error("add 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    /**
     * 删除函数
     *
     * @param httpServletRequest 请求对象
     * @return 返回结果
     */
    @Override
    public String remove(HttpServletRequest httpServletRequest, @RequestPart("params") MultipartFile params) {
        try {
            if (params == null) {
                return HttpUtils.getResJsonStr(new JSONObject(), "您的请求参数为空，请确保您的请求参数 json 字符串存储在 ”params“ 对应的请求数据包中!");
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.remove(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DISK_MIRROR_CONFIG.getString(Config.CHAR_SET)))).toString();
                }
            }
        } catch (IOException | RuntimeException e) {
            WebConf.LOGGER.error("remove 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    /**
     * 重命名类的函数
     *
     * @param httpServletRequest 请求对象
     * @return 返回结果
     */
    @Override
    public String reName(HttpServletRequest httpServletRequest, @RequestPart("params") MultipartFile params) {
        try {
            if (params == null) {
                return HttpUtils.getResJsonStr(new JSONObject(), "您的请求参数为空，请确保您的请求参数 json 字符串存储在 ”params“ 对应的请求数据包中!");
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.reName(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DISK_MIRROR_CONFIG.getString(Config.CHAR_SET)))).toString();
                }
            }
        } catch (IOException | RuntimeException e) {
            WebConf.LOGGER.error("reName 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    /**
     * 获取相关操作的函数
     *
     * @param httpServletRequest 请求对象
     * @return 返回结果
     */
    @Override
    public String get(HttpServletRequest httpServletRequest, @RequestParam("params") MultipartFile params) {
        try {
            if (params == null) {
                return HttpUtils.getResJsonStr(new JSONObject(), "您的请求参数为空，请确保您的请求参数 json 字符串存储在 ”params“ 对应的请求数据包中!");
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.getUrls(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DISK_MIRROR_CONFIG.getString(Config.CHAR_SET)))).toString();
                }
            }
        } catch (IOException | RuntimeException e) {
            WebConf.LOGGER.error("get 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    @Override
    public void downLoad(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                         @PathVariable("userId") String userId, @PathVariable("type") String type,
                         String fileName, Integer sk) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", userId);
        jsonObject.put("fileName", fileName);
        jsonObject.put("type", type);
        // 解密 并 提取 sk
        getDiskMirrorXorSecureKey(httpServletRequest, sk == null ? 0 : sk, jsonObject);

        // 设置响应头部信息
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setHeader("Expires", "0");

        httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        InputStream fileInputStream = null;

        try {
            fileInputStream = adapter.downLoad(jsonObject);
            httpServletResponse.setHeader("Content-Length", String.valueOf(fileInputStream.available()));
        } catch (IOException | UnsupportedOperationException e) {
            WebConf.LOGGER.warn(e.toString());
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

        try (
                ServletOutputStream outputStream = httpServletResponse.getOutputStream()
        ) {
            IOUtils.copy(fileInputStream, outputStream, true);
        } catch (RuntimeException | IOException e) {
            WebConf.LOGGER.warn(e.toString());
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }


    @Override
    public String transferDeposit(HttpServletRequest httpServletRequest, @RequestPart("params") MultipartFile params) {
        try {
            if (params == null) {
                return HttpUtils.getResJsonStr(new JSONObject(), "您的请求参数为空，请确保您的请求参数 json 字符串存储在 ”params“ 对应的请求数据包中!");
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.transferDeposit(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DISK_MIRROR_CONFIG.getString(Config.CHAR_SET)))).toString();
                }
            }
        } catch (IOException | RuntimeException e) {
            WebConf.LOGGER.error("transferDeposit 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }


    @Override
    public String transferDepositStatus(HttpServletRequest httpServletRequest, @RequestPart("params") MultipartFile params) {
        try {
            if (params == null) {
                return HttpUtils.getResJsonStr(new JSONObject(), "您的请求参数为空，请确保您的请求参数 json 字符串存储在 ”params“ 对应的请求数据包中!");
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.transferDepositStatus(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DISK_MIRROR_CONFIG.getString(Config.CHAR_SET)))).toString();
                }
            }
        } catch (IOException | RuntimeException e) {
            WebConf.LOGGER.error("transferDeposit 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    @Override
    public String getAllProgressBar(String id) {
        return adapter.getAllProgressBar(id).toString();
    }


    /**
     * 创建一个文件目录的后端处理函数
     *
     * @param httpServletRequest 来自前端的请求对象
     * @return 操作成功之后的返回结果
     */
    @Override
    public String mkdirs(HttpServletRequest httpServletRequest, @RequestPart("params") MultipartFile params) {
        try {
            if (params == null) {
                return HttpUtils.getResJsonStr(new JSONObject(), "您的请求参数为空，请确保您的请求参数 json 字符串存储在 ”params“ 对应的请求数据包中!");
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.mkdirs(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DISK_MIRROR_CONFIG.getString(Config.CHAR_SET)))).toString();
                }
            }
        } catch (IOException | RuntimeException e) {
            WebConf.LOGGER.error("mkdirs 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    /**
     * 获取到指定空间的大小
     * 依赖 diskMirror 1.1.1 以及以上版本！！
     *
     * @param spaceId 指定的空间的id
     * @return 返回指定空间的大小 单位是字节
     */
    @Override
    public String getSpaceSize(String spaceId) {
        return HttpUtils.getResJsonStr(new JSONObject(), adapter.getConfig().getSpaceMaxSize(spaceId));
    }

    /**
     * 设置指定空间的大小，此操作需要提供安全密钥
     * 依赖 diskMirror 1.1.1 以及以上版本！！
     *
     * @param httpServletRequest 请求对象
     * @return 操作结果
     */
    @Override
    public String setSpaceSize(HttpServletRequest httpServletRequest, @RequestPart("params") MultipartFile params) {
        try {
            if (params == null) {
                return HttpUtils.getResJsonStr(new JSONObject(), "您的请求参数为空，请确保您的请求参数 json 字符串存储在 ”params“ 对应的请求数据包中!");
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    // 获取到 id 和 size 以及安全密钥
                    final JSONObject jsonObject = JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DISK_MIRROR_CONFIG.getString(Config.CHAR_SET)));
                    final Object userId = jsonObject.get("userId");
                    final Long newSize = jsonObject.getLong("newSize");
                    if (userId == null || newSize == null) {
                        throw new UnsupportedOperationException("请求参数不合规，请确保您在调用 setSpaceSize 函数的参数中设置了 userId and newSize， error:" + jsonObject);
                    }
                    adapter.setSpaceMaxSize(userId.toString(), newSize);
                    return HttpUtils.getResJsonStr(jsonObject, this.adapter.getConfig().getString(Config.OK_VALUE));
                }
            }
        } catch (IOException | RuntimeException e) {
            WebConf.LOGGER.error("mkdirs 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    /**
     * 获取 盘镜 后端系统 版本号
     * 依赖 diskMirror 1.1.1 以及以上版本！！
     *
     * @return 操作成功之后返回的结果
     */
    public String getVersion() {
        final DiskMirror orDefault = (DiskMirror) DISK_MIRROR_CONFIG.getOrDefault(WebConf.IO_MODE, DiskMirror.LocalFSAdapter);
        return orDefault.getVersion() + '\n' + adapter.version();
    }

    @Override
    public String getUseSize(HttpServletRequest httpServletRequest, @RequestPart("params") MultipartFile params) {
        try {
            if (params == null) {
                return HttpUtils.getResJsonStr(new JSONObject(), "您的请求参数为空，请确保您的请求参数 json 字符串存储在 ”params“ 对应的请求数据包中!");
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    final JSONObject jsonObject = JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DISK_MIRROR_CONFIG.getString(Config.CHAR_SET)));
                    jsonObject.put("useSize", adapter.getUseSize(jsonObject));
                    return HttpUtils.getResJsonStr(jsonObject, this.adapter.getConfig().getString(Config.OK_VALUE));
                }
            }
        } catch (IOException | RuntimeException e) {
            WebConf.LOGGER.error("getUseSize 函数调用错误!!!", e);
            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    @Override
    public String setSpaceSk(HttpServletRequest httpServletRequest, @RequestPart("params") MultipartFile params) {
//        try {
//            final Part params = httpServletRequest.getPart("params");
//            if (params == null) {
//                return HttpUtils.getResJsonStr(new JSONObject(), "您的请求参数为空，请确保您的请求参数 json 字符串存储在 ”params“ 对应的请求数据包中!");
//            } else {
//                try (
//                        final InputStream inputStream = params.getInputStream()
//                ) {
//                    final JSONObject jsonObject = JSONObject.parseObject(
//                            IOUtils.getStringByStream(inputStream, DISK_MIRROR_CONFIG.getString(Config.CHAR_SET))
//                    );
//                    final int i = this.adapter.setSpaceSk(jsonObject.getString("userId"));
//                    jsonObject.clear();
//                    jsonObject.put("sk", i);
//                    return HttpUtils.getResJsonStr(jsonObject, this.adapter.getConfig().getString(Config.OK_VALUE));
//                }
//            }
//        } catch (IOException | RuntimeException | ServletException e) {
//            WebConf.LOGGER.error("getUseSize 函数调用错误!!!", e);
//            return HttpUtils.getResJsonStr(new JSONObject(), e.toString());
//        }
        // 此服务如果可以随意被别人调用 则不安全，因此暂时不提供此服务了
        return "还未集成此服务!";
    }

    @Override
    public String shutdown(String password) {
        final DiskMirrorBackEndProperties.ShutdownController shutdownController = diskMirrorBackEndProperties.getShutdownController();
        if (shutdownController.isEnable()) {
            if (shutdownController.getPassword().equals(password)) {
                WebConf.LOGGER.info("shutdown 函数调用成功，{} 毫秒之后关闭 diskMirror 服务!", shutdownController.getTimeout());
                new Thread(() -> {
                    try {
                        Thread.sleep(shutdownController.getTimeout());
                    } catch (InterruptedException e) {
                        throw new RuntimeException("shutdown时，延迟功能出现错误！", e);
                    }
                    WebConf.LOGGER.info("shutdown 函数: 开始关闭 diskMirror 服务!");
                    DiskMirrorMAIN.run.stop();
                }).start();
                return "服务器已尝试关机！";
            }
            return "密码有误 验证失败，无法关机！";
        }
        return "关机功能未启用~";
    }
}
