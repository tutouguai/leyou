package top.leyou;


import org.elasticsearch.client.ElasticsearchClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;
import top.leyou.common.vo.PageResult;
import top.leyou.item.pojo.Spu;
import top.leyou.search.client.GoodsClient;
import top.leyou.search.pojo.Goods;
import top.leyou.search.repository.GoodsRepository;
import top.leyou.search.service.SearchService;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class testDataLoad {

    @Resource
    private GoodsRepository goodsRepository;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private GoodsClient goodsClient;

    @Resource
    private SearchService searchService;

    @Test
    public void loadData(){

        int page = 1;
        int rows = 100;
        int size = 0;
        do {
            //批量查询spu信息
            PageResult<Spu> result = goodsClient.querySpuByPage(page, rows, true, null);
            List<Spu> SpuList = result.getItems();
            if(CollectionUtils.isEmpty(SpuList)) {
                break;
            }
            //构建goods

            List<Goods> goodsList = SpuList.stream()
                    .map(searchService::buildGoods).collect(Collectors.toList());

            //存入索引库
            goodsRepository.saveAll(goodsList);

            //翻页
            page++;
            size = SpuList.size();
        }while(size == 100);
    }
}
