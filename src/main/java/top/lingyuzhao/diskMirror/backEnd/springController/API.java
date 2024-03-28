package top.lingyuzhao.diskMirror.backEnd.springController;

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
    public API(top.lingyuzhao.diskMirror.core.Adapter adapter, top.lingyuzhao.diskMirror.starter.conf.properties.DiskMirrorProperties properties) {
        super(adapter, properties);
    }

}
