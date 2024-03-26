package top.lingyuzhao.diskMirror.backEnd.springController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author zhao
 */
@Component("top.lingyuzhao.diskMirror.backEnd.springController.API")
@Controller
@RequestMapping(
        value = "FsCrud",
        // 告知前端页面，回复数据的解析方式
        produces = "text/html;charset=UTF-8",
        method = {RequestMethod.POST}
)
@ConditionalOnClass(value = {top.lingyuzhao.diskMirror.core.Adapter.class})
public class API extends FsCrud {

    @Autowired
    public API(top.lingyuzhao.diskMirror.core.Adapter adapter) {
        super(adapter);
    }

    @Override
    public String add(HttpServletRequest httpServletRequest) {
        return super.add(httpServletRequest);
    }

    @Override
    public String remove(HttpServletRequest httpServletRequest) {
        return super.remove(httpServletRequest);
    }

    @Override
    public String reName(HttpServletRequest httpServletRequest) {
        return super.reName(httpServletRequest);
    }

    @Override
    public String get(HttpServletRequest httpServletRequest) {
        return super.get(httpServletRequest);
    }

    @Override
    public void downLoad(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String userId, String type, String fileName, Integer sk) {
        super.downLoad(httpServletRequest, httpServletResponse, userId, type, fileName, sk);
    }

    @Override
    public String mkdirs(HttpServletRequest httpServletRequest) {
        return super.mkdirs(httpServletRequest);
    }

    @Override
    public String setSpaceSize(HttpServletRequest httpServletRequest) {
        return super.setSpaceSize(httpServletRequest);
    }
}
