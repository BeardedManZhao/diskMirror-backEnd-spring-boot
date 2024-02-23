package top.lingyuzhao.diskMirror.backEnd.springController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import top.lingyuzhao.diskMirror.core.Adapter;

/**
 * 控制器 此控制器直接继承了后端版本的控制器 能够直接使用!
 *
 * @author zhao
 */
@Controller
@RequestMapping(
        value = {"FsCrud"},
        produces = {"text/html;charset=UTF-8"},
        method = {RequestMethod.POST}
)
@ConditionalOnClass({Adapter.class})
@ConditionalOnBean(Adapter.class)
public class FsCrud extends top.lingyuzhao.diskMirror.backEnd.core.controller.FsCrud {

    @Autowired
    public FsCrud(Adapter adapter) {
        super(adapter);
    }
}
