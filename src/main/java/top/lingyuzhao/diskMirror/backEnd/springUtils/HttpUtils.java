package top.lingyuzhao.diskMirror.backEnd.springUtils;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.lingyuzhao.diskMirror.conf.Config;

/**
 * Http 协议处理工具类
 *
 * @author zhao
 */
@Component
public final class HttpUtils {

    private final Config config;
    private final String resK;

    @Autowired
    public HttpUtils(Config config) {
        this.config = config;
        this.resK = config.getString(Config.RES_KEY);
    }

    /**
     * 为 json 添加上结果并返回
     *
     * @param jsonObject       需要被添加结果的json对象
     * @param resReturnOkValue 返回数值
     * @return json的字符串
     */
    public String getResJsonStr(JSONObject jsonObject, Object resReturnOkValue) {
        return getResJson(jsonObject, resReturnOkValue).toString();
    }

    /**
     * 为 json 添加上结果并返回
     *
     * @param jsonObject       需要被添加结果的json对象
     * @param resReturnOkValue 返回数值
     * @return json的字符串
     */
    public JSONObject getResJson(JSONObject jsonObject, Object resReturnOkValue) {
        jsonObject.put(resK, resReturnOkValue);
        return jsonObject;
    }

    /**
     * 解密
     *
     * @param encrypted 需要被解密的值
     * @return 解密后的值
     */
    public int xorDecrypt(int encrypted) {
        int temp = encrypted;
        final String sk_str = String.valueOf(config.getSecureKey());
        for (int i = 0; i < sk_str.length(); ++i) {
            temp += sk_str.charAt(i) << 1;
        }
        return temp;
    }
}
