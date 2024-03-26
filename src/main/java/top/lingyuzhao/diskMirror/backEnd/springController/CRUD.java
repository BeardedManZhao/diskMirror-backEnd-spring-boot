package top.lingyuzhao.diskMirror.backEnd.springController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 具有增删改查功能的控制器接口
 */
public interface CRUD {

    /**
     * 增加函数
     *
     * @param httpServletRequest 请求对象
     * @return 返回结果
     */
    @RequestMapping("/add")
    @ResponseBody
    String add(HttpServletRequest httpServletRequest);

    /**
     * 删除函数
     *
     * @param httpServletRequest 请求对象
     * @return 返回结果
     */
    @RequestMapping("/remove")
    @ResponseBody
    String remove(HttpServletRequest httpServletRequest);

    /**
     * 重命名类的函数
     *
     * @param httpServletRequest 请求对象
     * @return 返回结果
     */
    @RequestMapping("/reName")
    @ResponseBody
    String reName(HttpServletRequest httpServletRequest);

    /**
     * 获取相关操作的函数
     *
     * @param httpServletRequest 请求对象
     * @return 返回结果
     */
    @RequestMapping("/getUrls")
    @ResponseBody
    String get(HttpServletRequest httpServletRequest);

    /**
     * 下载文件的后端处理函数
     *
     * @param httpServletRequest  来自前端的请求对象
     * @param httpServletResponse 对接前端的数据回复对象
     * @param fileName            需要下载的文件对应的文件名
     * @param type                需要下载的文件对应的文件类型
     * @param userId              需要下载的文件所属的空间id
     * @param sk                  操作的时候需要的密钥，此密钥可以不进行加密，当cookie获取不到的时候，才会调用此密钥！
     */
    @RequestMapping(
            value = "/downLoad/{userId:\\d+}/{type:[a-zA-Z]+}",
            method = {RequestMethod.GET, RequestMethod.POST},
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    @ResponseBody
    void downLoad(HttpServletRequest httpServletRequest,
                  HttpServletResponse httpServletResponse,
                  @PathVariable("userId") String userId, @PathVariable("type") String type,
                  String fileName, Integer sk
    );

    /**
     * 创建一个文件目录的后端处理函数
     *
     * @param httpServletRequest 来自前端的请求对象
     * @return 操作成功之后的返回结果
     */
    @RequestMapping("/mkdirs")
    @ResponseBody
    String mkdirs(HttpServletRequest httpServletRequest);

    /**
     * 获取到指定空间的大小
     *
     * @param spaceId 指定的空间的id
     * @return 返回指定空间的大小 单位是字节
     */
    @RequestMapping("/getSpaceSize")
    @ResponseBody
    String getSpaceSize(String spaceId);

    /**
     * 设置指定空间的大小，此操作需要提供安全密钥
     *
     * @param httpServletRequest 请求对象
     * @return 操作结果
     */
    @RequestMapping("/setSpaceSize")
    @ResponseBody
    String setSpaceSize(HttpServletRequest httpServletRequest);

    /**
     * 获取 盘镜 后端系统 版本号
     *
     * @return 操作成功之后返回的结果
     */
    @RequestMapping("/getVersion")
    @ResponseBody
    String getVersion();
}
