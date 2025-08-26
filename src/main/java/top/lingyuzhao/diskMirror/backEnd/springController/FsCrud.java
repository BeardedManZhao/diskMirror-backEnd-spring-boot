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
import top.lingyuzhao.diskMirror.backEnd.springConf.DiskMirrorBackEndProperties;
import top.lingyuzhao.diskMirror.backEnd.springConf.DiskMirrorMAIN;
import top.lingyuzhao.diskMirror.backEnd.springUtils.HttpUtils;
import top.lingyuzhao.diskMirror.conf.Config;
import top.lingyuzhao.diskMirror.core.Adapter;
import top.lingyuzhao.diskMirror.core.DiskMirror;
import top.lingyuzhao.utils.IOUtils;
import top.lingyuzhao.utils.StrUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;


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
    private final HttpUtils httpUtils;

    private final String errorParamsIsNull, errorFileIsNull;

    private final static IOException fileNotExist = new IOException("文件不存在");

    private final static HashSet<String> VIDEO_TYPES = new HashSet<>();

    static {
        VIDEO_TYPES.add("mp4");
        VIDEO_TYPES.add("webm");
        VIDEO_TYPES.add("ogv");
        VIDEO_TYPES.add("ogg");
    }

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
        this.DISK_MIRROR_CONFIG.put("WebConf.IO_MODE", properties.getAdapterType());
        this.httpUtils = new HttpUtils(DISK_MIRROR_CONFIG);
        this.errorFileIsNull  = httpUtils.getResJsonStr(new JSONObject(), "您的文件数据为空，请确保您要上传的文件数据存储在 ”file“ 对应的请求数据包中!!!");
        this.errorParamsIsNull = httpUtils.getResJsonStr(new JSONObject(), "您的请求参数为空，请确保您的请求参数 json 字符串存储在 ”params“ 对应的请求数据包中!");
    }

    /**
     * 获取文件加密的密钥
     *
     * @param httpServletRequest 来自前端的请求对象
     * @param defKey             默认的密钥值，如果请求对象中没有包含 请求对象 则会使用此参数！
     * @param jsonObject         需要用来存储 key 的 json对象
     */
    private void getDiskMirrorXorSecureKey(HttpServletRequest httpServletRequest, int defKey, JSONObject jsonObject) {
        if (httpServletRequest == null || httpServletRequest.getCookies() == null) {
            jsonObject.put("secure.key", defKey);
            return;
        }
        for (Cookie cookie1 : httpServletRequest.getCookies()) {
            if ("diskMirror_xor_secure_key".equals(cookie1.getName())) {
                jsonObject.put("secure.key", httpUtils.xorDecrypt(Integer.parseInt(cookie1.getValue())));
                return;
            }
        }
        jsonObject.put("secure.key", defKey);
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
                    return this.errorFileIsNull;
                } else {
                    return this.errorParamsIsNull;
                }
            }
            try (
                    final InputStream inputStream0 = params.getInputStream();
                    final InputStream inputStream1 = file.getInputStream()
            ) {
                return adapter.upload(inputStream1, JSONObject.parseObject(IOUtils.getStringByStream(inputStream0, DISK_MIRROR_CONFIG.getString(Config.CHAR_SET)))).toString();
            }
        } catch (IOException | RuntimeException e) {
            DiskMirrorMAIN.logger.error("add 函数调用错误!!!", e);
            return httpUtils.getResJsonStr(new JSONObject(), e.toString());
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
                return this.errorParamsIsNull;
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.remove(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DISK_MIRROR_CONFIG.getString(Config.CHAR_SET)))).toString();
                }
            }
        } catch (IOException | RuntimeException e) {
            DiskMirrorMAIN.logger.error("remove 函数调用错误!!!", e);
            return httpUtils.getResJsonStr(new JSONObject(), e.toString());
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
                return this.errorParamsIsNull;
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.reName(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DISK_MIRROR_CONFIG.getString(Config.CHAR_SET)))).toString();
                }
            }
        } catch (IOException | RuntimeException e) {
            DiskMirrorMAIN.logger.error("reName 函数调用错误!!!", e);
            return httpUtils.getResJsonStr(new JSONObject(), e.toString());
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
                return this.errorParamsIsNull;
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.getUrls(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DISK_MIRROR_CONFIG.getString(Config.CHAR_SET)))).toString();
                }
            }
        } catch (IOException | RuntimeException e) {
            DiskMirrorMAIN.logger.error("get 函数调用错误!!!", e);
            return httpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    /**
     * 获取相关操作的函数
     *
     * @param httpServletRequest 请求对象
     * @return 返回结果
     */
    @Override
    public String getUrlsNoRecursion(HttpServletRequest httpServletRequest, @RequestParam("params") MultipartFile params) {
        try {
            if (params == null) {
                return this.errorParamsIsNull;
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.getUrlsNoRecursion(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DISK_MIRROR_CONFIG.getString(Config.CHAR_SET)))).toString();
                }
            }
        } catch (IOException | RuntimeException e) {
            DiskMirrorMAIN.logger.error("getUrlsNoRecursion 函数调用错误!!!", e);
            return httpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    @Override
    public void downLoad(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                         @RequestHeader(name = "Range", required = false) String rangeHeader,
                         @PathVariable("userId") String userId, @PathVariable("type") String type,
                         @RequestParam("fileName") String fileName,  @RequestParam(value = "sk", defaultValue = "0", required = false) Integer sk) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", userId);
        jsonObject.put("fileName", fileName);
        jsonObject.put("type", type);
        // 获取到后缀
        final String substring = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        // 判断是否是视频
        final boolean isVideo = VIDEO_TYPES.contains(substring);
        // 解密并提取 sk
        getDiskMirrorXorSecureKey(httpServletRequest, sk == null ? 0 : sk, jsonObject);
        // 获取文件元数据及最后修改时间
        final JSONObject urlsNoRecursion;
        try {
            urlsNoRecursion = adapter.getUrlsNoRecursion(jsonObject.clone());
            if (urlsNoRecursion == null) {
                throw fileNotExist;
            }
        } catch (IOException e) {
            DiskMirrorMAIN.logger.warn("文件不存在: " + fileName);
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        long lastModified = urlsNoRecursion.getLongValue("lastModified");
        if (lastModified <= 0) {
            lastModified = System.currentTimeMillis(); // 默认当前时间
        }

        // 设置 Last-Modified 响应头
        httpServletResponse.setDateHeader("Last-Modified", lastModified);
        // 设置 Content-Length 响应头
        final String fSize = urlsNoRecursion.getString("size");
        httpServletResponse.setHeader("Content-Length", fSize);

        // 检查 If-Modified-Since 请求头
        long ifModifiedSince = httpServletRequest.getDateHeader("If-Modified-Since");
        if (ifModifiedSince >= lastModified) {
            // 文件未修改，返回 304 Not Modified
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        final String s = URLEncoder.encode(StrUtils.splitByLast(fileName, '/', 2)[0], StandardCharsets.UTF_8);
        // 设置其他响应头
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"" + s + "\"; filename*=UTF-8''" + s);
        if (isVideo) {
            httpServletResponse.setContentType("video/" + substring);
            httpServletResponse.setHeader("Accept-Ranges", "bytes");
        } else {
            httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        }

        try (InputStream fileInputStream = adapter.downLoad(jsonObject)) {
            if (fileInputStream == null) {
                httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            long[] range = null;
            if (isVideo) {
                if (rangeHeader != null) {
                    // 解析 Range 头，如：bytes=0-1023
                    String[] ranges = rangeHeader.substring(6).split("-");
                    long start = Long.parseLong(ranges[0]);
                    long end = ranges.length > 1 ? Long.parseLong(ranges[1]) : Long.parseLong(fSize) - 1;
                    range = new long[]{start, end};
                }
            }

            try (ServletOutputStream outputStream = httpServletResponse.getOutputStream()) {
                if (range != null) {
                    // 返回 206 Partial Content
                    httpServletResponse.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                    httpServletResponse.setHeader("Content-Range", "bytes " + range[0] + "-" + range[1] + "/" + fSize);
                    httpServletResponse.setContentLengthLong(range[1] - range[0] + 1);
                    byte[] buffer = new byte[4096];
                    fileInputStream.skipNBytes(range[0]);
                    long remaining = range[1] - range[0] + 1;
                    int len;
                    while ((len = fileInputStream.read(buffer, 0, (int) Math.min(buffer.length, remaining))) > 0) {
                        outputStream.write(buffer, 0, len);
                        remaining -= len;
                    }
                } else {
                    IOUtils.copy(fileInputStream, outputStream, true);
                }
            }
        } catch (IOException | RuntimeException e) {
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }


    @Override
    public String transferDeposit(HttpServletRequest httpServletRequest, @RequestPart("params") MultipartFile params) {
        try {
            if (params == null) {
                return this.errorParamsIsNull;
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.transferDeposit(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DISK_MIRROR_CONFIG.getString(Config.CHAR_SET)))).toString();
                }
            }
        } catch (IOException | RuntimeException e) {
            DiskMirrorMAIN.logger.error("transferDeposit 函数调用错误!!!", e);
            return httpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }


    @Override
    public String transferDepositStatus(HttpServletRequest httpServletRequest, @RequestPart("params") MultipartFile params) {
        try {
            if (params == null) {
                return this.errorParamsIsNull;
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.transferDepositStatus(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DISK_MIRROR_CONFIG.getString(Config.CHAR_SET)))).toString();
                }
            }
        } catch (IOException | RuntimeException e) {
            DiskMirrorMAIN.logger.error("transferDeposit 函数调用错误!!!", e);
            return httpUtils.getResJsonStr(new JSONObject(), e.toString());
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
                return this.errorParamsIsNull;
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    return adapter.mkdirs(JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DISK_MIRROR_CONFIG.getString(Config.CHAR_SET)))).toString();
                }
            }
        } catch (IOException | RuntimeException e) {
            DiskMirrorMAIN.logger.error("mkdirs 函数调用错误!!!", e);
            return httpUtils.getResJsonStr(new JSONObject(), e.toString());
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
        return httpUtils.getResJsonStr(new JSONObject(), adapter.getConfig().getSpaceMaxSize(spaceId));
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
                return this.errorParamsIsNull;
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    // 获取到 id 和 size 以及安全密钥
                    final JSONObject jsonObject = JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DISK_MIRROR_CONFIG.getString(Config.CHAR_SET)));
                    final Object userId = jsonObject.get("userId");
                    final Long newSize = jsonObject.getLong("newSize");
                    int sk = jsonObject.getIntValue(Config.SECURE_KEY);
                    if (userId == null || newSize == null) {
                        throw new UnsupportedOperationException("请求参数不合规，请确保您在调用 setSpaceSize 函数的参数中设置了 userId and newSize， error:" + jsonObject);
                    }
                    adapter.setSpaceMaxSize(userId.toString(), newSize, sk);
                    return httpUtils.getResJsonStr(jsonObject, this.adapter.getConfig().getString(Config.OK_VALUE));
                }
            }
        } catch (IOException | RuntimeException e) {
            DiskMirrorMAIN.logger.error("setSpaceSize 函数调用错误!!!", e);
            return httpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    /**
     * 获取 盘镜 后端系统 版本号
     * 依赖 diskMirror 1.1.1 以及以上版本！！
     *
     * @return 操作成功之后返回的结果
     */
    public String getVersion() {
        final DiskMirror orDefault = (DiskMirror) DISK_MIRROR_CONFIG.getOrDefault("WebConf.IO_MODE", DiskMirror.LocalFSAdapter);
        return orDefault.getVersion() + '\n' + adapter.version();
    }

    @Override
    public String getUseSize(HttpServletRequest httpServletRequest, @RequestPart("params") MultipartFile params) {
        try {
            if (params == null) {
                return this.errorParamsIsNull;
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    final JSONObject jsonObject = JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DISK_MIRROR_CONFIG.getString(Config.CHAR_SET)));
                    jsonObject.put("useSize", adapter.getUseSize(jsonObject));
                    return httpUtils.getResJsonStr(jsonObject, this.adapter.getConfig().getString(Config.OK_VALUE));
                }
            }
        } catch (IOException | RuntimeException e) {
            DiskMirrorMAIN.logger.error("getUseSize 函数调用错误!!!", e);
            return httpUtils.getResJsonStr(new JSONObject(), e.toString());
        }
    }

    @Override
    public String setSpaceSk(HttpServletRequest httpServletRequest, @RequestPart("params") MultipartFile params) {
        try {
            if (params == null) {
                return this.errorParamsIsNull;
            } else {
                try (
                        final InputStream inputStream = params.getInputStream()
                ) {
                    final JSONObject jsonObject = JSONObject.parseObject(IOUtils.getStringByStream(inputStream, DISK_MIRROR_CONFIG.getString(Config.CHAR_SET)));
                    final int i = this.adapter.setSpaceSk(jsonObject.getString("userId"), jsonObject.getIntValue(Config.SECURE_KEY));
                    jsonObject.put(Config.SECURE_KEY, i);
                    final String resJsonStr = httpUtils.getResJsonStr(jsonObject, this.adapter.getConfig().getString(Config.OK_VALUE));
                    DiskMirrorMAIN.logger.info("setSpaceSk 函数调用成功，返回值:{}", resJsonStr);
                    return resJsonStr;
                }
            }
        } catch (IOException | RuntimeException e) {
            DiskMirrorMAIN.logger.error("setSpaceSk 函数调用错误!!!", e);
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(Config.SECURE_KEY, e.toString());
            return httpUtils.getResJsonStr(jsonObject, e.toString());
        }
    }

    @Override
    public String shutdown(String password) {
        final DiskMirrorBackEndProperties.ShutdownController shutdownController = diskMirrorBackEndProperties.getShutdownController();
        if (shutdownController.isEnable()) {
            if (shutdownController.getPassword().equals(password)) {
                DiskMirrorMAIN.logger.info("shutdown 函数调用成功，{} 毫秒之后关闭 diskMirror 服务!", shutdownController.getTimeout());
                new Thread(() -> {
                    try {
                        Thread.sleep(shutdownController.getTimeout());
                    } catch (InterruptedException e) {
                        throw new RuntimeException("shutdown时，延迟功能出现错误！", e);
                    }
                    DiskMirrorMAIN.logger.info("shutdown 函数: 开始关闭 diskMirror 服务!");
                    DiskMirrorMAIN.run.stop();
                }).start();
                return "服务器已尝试关机！";
            }
            return "密码有误 验证失败，无法关机！";
        }
        return "关机功能未启用~";
    }
}
