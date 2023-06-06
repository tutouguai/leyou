package top.leyou.search.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.stereotype.Repository;
import top.leyou.search.pojo.Goods;


@Repository
public interface GoodsRepository extends ElasticsearchRepository<Goods, Long> {
}
