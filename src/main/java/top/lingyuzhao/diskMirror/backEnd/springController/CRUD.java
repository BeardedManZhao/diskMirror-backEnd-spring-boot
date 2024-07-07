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
     * 将一个 url 地址指向的数据进行转存操作，将 url 指向的文件转存到空间中的指定位置！
     * <p>
     * Transfer the data pointed to by a URL address to a specified location in the space!
     *
     * @param httpServletRequest 请求对象
     * @return 转存后的结果
     * <p>
     * Result after transfer
     */
    @RequestMapping("/transferDeposit")
    @ResponseBody
    String transferDeposit(HttpServletRequest httpServletRequest);


    /**
     * 将一个 url 地址指向的数据进行转存操作，将 url 指向的文件转存到空间中的指定位置！
     * <p>
     * Transfer the data pointed to by a URL address to a specified location in the space!
     *
     * @param httpServletRequest 请求对象
     * @return 转存后的结果
     * <p>
     * Result after transfer
     */
    @RequestMapping("/transferDepositStatus")
    @ResponseBody
    String transferDepositStatus(HttpServletRequest httpServletRequest);

    /**
     * 获取某个空间的所有进度条，这些进度条代表的往往都是正在处于上传状态的文件的操作进度对象
     * Retrieves all progress bars for a given space, typically representing ongoing upload operations for files within that space.
     *
     * @param id 指定的空间的 id
     *           A JSON collection of progress bar objects, where the keys correspond to progress scales.
     * @return 进度条对象的 json 集合 其中 key 是进度id value 是进度对象
     * The unique identifier of the specified space.
     */
    @RequestMapping("/getAllProgressBar")
    @ResponseBody
    String getAllProgressBar(String id);

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

    /**
     * 获取 盘镜 后端系统 已经使用的所有的空间大小。
     *
     * @param httpServletRequest http 的请求对象
     * @return 操作成功之后返回的结果
     */
    @RequestMapping("/getUseSize")
    @ResponseBody
    String getUseSize(HttpServletRequest httpServletRequest);

    /**
     * 将指定的 userId 生成一个新的密钥
     *
     * @param httpServletRequest http 的请求对象
     * @return 操作成功之后返回的结果
     */
    @RequestMapping("/setSpaceSk")
    @ResponseBody
    String setSpaceSk(HttpServletRequest httpServletRequest);
}
