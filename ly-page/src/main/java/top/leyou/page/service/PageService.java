package top.leyou.page.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import top.leyou.item.pojo.*;
import top.leyou.page.client.BrandClient;
import top.leyou.page.client.CategoryClient;
import top.leyou.page.client.GoodsClient;
import top.leyou.page.client.SpecificationClient;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PageService {

    @Resource
    private BrandClient brandClient;

    @Resource
    private CategoryClient categoryClient;

    @Resource
    private GoodsClient goodsClient;

    @Resource
    private SpecificationClient specClient;

    @Resource
    private TemplateEngine templateEngine;

    public Map<String, Object> loadModel(Long spuId) {
        Map<String, Object> model = new HashMap<>();
        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        //查询skus
        List<Sku> skus = spu.getSkus();
        //查询详情
        SpuDetail detail = spu.getSpuDetail();
        //查询brand
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        //查询商品分类
        List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //查询规格参数
        List<SpecGroup> specs = specClient.queryGroupByCid(spu.getCid3());

        model.put("title", spu.getTitle());
        model.put("subTitle", spu.getSubTitle());
        model.put("skus", skus);
        model.put("detail", detail);
        model.put("brand", brand);
        model.put("categories", categories);
        model.put("specs", specs);
        return model;
    }

    public void createHtml(Long spuId){
       //准备上下文
        Context context = new Context();
        context.setVariables(loadModel(spuId));
        //准备输出流

        //生成html
        templateEngine.process("item", context, writer);
    }
}
