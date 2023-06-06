package top.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.TotalHitsRelation;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.leyou.common.utils.JsonUtils;
import top.leyou.common.utils.NumberUtils;
import top.leyou.common.vo.PageResult;
import top.leyou.item.pojo.*;
import top.leyou.search.client.BrandClient;
import top.leyou.search.client.CategoryClient;
import top.leyou.search.client.GoodsClient;
import top.leyou.search.client.SpecificationClient;
import top.leyou.search.pojo.Goods;
import top.leyou.search.pojo.SearchRequest;
import top.leyou.search.pojo.SearchResult;
import top.leyou.search.repository.GoodsRepository;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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
    @Resource
    private GoodsRepository repository;
    @Resource
    private ElasticsearchRestTemplate template;

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
//        Set<Long> prices = new HashSet<>();
        List<Long> prices = new ArrayList<>();
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
        goods.setPrice(prices); // 所有sku价格的集合
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


    public PageResult<Goods> search(SearchRequest searchRequest) {
        Integer page = searchRequest.getPage() - 1;
        Integer size = searchRequest.getSize();
        //创建查询构造器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "subTitle", "skus"}, null));
        //分页
        queryBuilder.withPageable(PageRequest.of(page, size));
        // 过滤
//        QueryBuilder baseQuery = QueryBuilders.matchQuery("all", searchRequest.getKey());
//        queryBuilder.withQuery(baseQuery);
        QueryBuilder baseQuery = buildBasicQuery(searchRequest);
        queryBuilder.withQuery(baseQuery);


        //聚合分类与品牌信息
        //聚合分类
        String categoryAggName = "category_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        //聚合品牌
        String brandAggName = "brand_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        //查询
        SearchHits<Goods> search = template.search(queryBuilder.build(), Goods.class);
        //解析结果， 构建PageResult对象
        PageResult<Goods> result = new PageResult<>();
        //总条数
        Long total = search.getTotalHits();
        //数据
        List<Goods> goodsList = search.stream().map(SearchHit::getContent).collect(Collectors.toList());
        //计算总页数
        Long totalPage = result.getTotal()/searchRequest.getSize() + (result.getTotal()/searchRequest.getSize()==0?0:1);
        //解析聚合结果
        Aggregations aggs = search.getAggregations();
        List<Category> categories = parseCategory(aggs.get(categoryAggName));
        List<Brand> brands = parseBrand(aggs.get(brandAggName));
        //完成规格参数聚合
        List<Map<String, Object>> specs = null;
        if(categories.size() ==1 && !CollectionUtils.isEmpty(categories)){
            //商品分类存在数量为1，可以聚合规格参数
            specs = buildSpecificationAgg(categories.get(0).getId(), baseQuery);
        }
        return new SearchResult(total, totalPage, goodsList, categories, brands, specs);

    }

    private QueryBuilder buildBasicQuery(SearchRequest request) {
        //创建bool查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()));
        //过滤条件
        Map<String, String> filter = request.getFilter();
        for (Map.Entry<String, String> entry : filter.entrySet()) {
            String key = entry.getKey();
            //处理key
            if(!"cid3".equals(key) && !"brandId".equals(key)){
                key  = "spec."+key+".keyword";
            }
            queryBuilder.filter(QueryBuilders.termQuery(key, entry.getValue()));
        }
        return queryBuilder;
    }

    private List<Map<String, Object>> buildSpecificationAgg(Long cid, QueryBuilder baseQuery) {
        List<Map<String, Object>> specs = new ArrayList<>();
        // 查询需要聚合的规格参数
        //聚合
        List<SpecParam> specParams = specificationClient.queryParamList(null, cid, true);
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //带上原查询条件
        queryBuilder.withQuery(baseQuery);
        for (SpecParam specParam : specParams) {
            String name = specParam.getName();
            queryBuilder.addAggregation(AggregationBuilders.terms(name).field("spec."+name+".keyword"));
        }
        //获取结果
        SearchHits<Goods> search = template.search(queryBuilder.build(), Goods.class);
        // 解析结果
        Aggregations aggregations = search.getAggregations();
        for (SpecParam specParam : specParams) {
            String name = specParam.getName();
            StringTerms terms = aggregations.get(name);
            List<String> options = terms.getBuckets()
                    .stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            //准备map
            Map<String, Object> map = new HashMap<>();
            map.put("k", name);
            map.put("options", options);
            specs.add(map);
        }
        return specs;
    }

    private List<Brand> parseBrand(LongTerms terms) {
        try {
            List<Long> ids = terms.getBuckets().stream()
                    .map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
            List<Brand> brands = brandClient.queryBrandByIds(ids);
            return brands;
        }catch (Exception e){
            log.error("[搜索服务]查询品牌异常:", e);
            return null;
        }

    }

    private List<Category> parseCategory(LongTerms terms) {
        try {
            List<Long> ids = terms.getBuckets().stream()
                    .map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
            List<Category> categories = categoryClient.queryCategoryByIds(ids);
            return categories;
        }catch (Exception e){
            log.error("[搜索服务]查询分类异常:", e);
            return null;
        }
    }
}
