package top.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.leyou.common.utils.JsonUtils;
import top.leyou.common.utils.NumberUtils;
import top.leyou.item.pojo.*;
import top.leyou.search.client.BrandClient;
import top.leyou.search.client.CategoryClient;
import top.leyou.search.client.GoodsClient;
import top.leyou.search.client.SpecificationClient;
import top.leyou.search.pojo.Goods;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Resource
    private CategoryClient categoryClient;
    @Resource
    private BrandClient brandClient;
    @Resource
    private SpecificationClient specificationClient;
    @Resource
    private GoodsClient goodsClient;

    public Goods buildGoods(Spu spu){

        Long spuId = spu.getId();

        //1. 搜索字段

        //查询分类
        List<Category> categories = categoryClient.queryCategoryByIds(
                Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());
        //查询，品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());

        String all = spu.getTitle() + StringUtils.join(names) + brand.getName();

        //查询sku
        List<Sku> skuList = goodsClient.querySkuBySpuId(spuId);
        //对sku进行处理
        List<Map<String, Object>> skus = new ArrayList<>();
        //价格集合
        Set<Long> prices = new HashSet<>();
        for (Sku sku : skuList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("title", sku.getTitle());
            map.put("price", sku.getPrice());
            map.put("image", sku.getImages());
            skus.add(map);
            prices.add(sku.getPrice());
        }
        //查询规格参数
        List<SpecParam> specParams = specificationClient.queryParamList(null, spu.getCid3(), true);
        //查询商品详情
        SpuDetail spuDetail = goodsClient.querySpuDetailById(spuId);
        //获取通用规格参数
        Map<Long, String> genericSpec = JsonUtils.toMap(spuDetail.getGenericSpec(), Long.class, String.class);
        //获取特有规格参数
        Map<Long, List<String>> specialSpec = JsonUtils
                .nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {});

        //规格参数, key是规格参数的名字，值是规格参数的值
        Map<String, Object> specs = new HashMap<>();
        for (SpecParam specParam : specParams) {

            String key = specParam.getName();
            Object value = "";
            //判断是否是通用规格参数
            if(specParam.getGeneric()){
                value = genericSpec.get(specParam.getId());
                //判断是否是数值类型
                if(specParam.getNumeric()){
                    //处理成段（0-500）
                    value = chooseSegment(value.toString(), specParam);
                }
            }else{
                value = specialSpec.get(specParam.getId());
            }
            specs.put(key, value);
        }

        // 构建goods对象
        Goods goods = new Goods();
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setId(spuId);
        goods.setSubTitle(spu.getSubTitle());
        goods.setAll(all); // 搜索字段,包含标题，分类，品牌，规格
        goods.setPrice((List<Long>) prices); // 所有sku价格的集合
        goods.setSkus(JsonUtils.toString(skus));// 所有sku集合的json格式
        goods.setSpecs(specs);//所有的可搜索的规格参数

        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

}
