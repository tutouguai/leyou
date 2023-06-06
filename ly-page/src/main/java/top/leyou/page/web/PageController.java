package top.leyou.page.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.leyou.page.service.PageService;

import javax.annotation.Resource;
import java.util.*;

@Controller
public class PageController {

    @Resource
    private PageService pageService;

    @GetMapping("item/{id}.html")
    public String toItemPage(@PathVariable("id") Long spuId, Model model){
        //查询模型数据
        Map<String, Object> attributes = pageService.loadModel(spuId);
        //准备模型数据
        model.addAllAttributes(attributes);
        //返回视图
        return "item";
    }
}
