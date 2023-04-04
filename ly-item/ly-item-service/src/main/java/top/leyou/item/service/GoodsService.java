package top.leyou.item.service;

import top.leyou.common.vo.PageResult;
import top.leyou.item.pojo.Sku;
import top.leyou.item.pojo.Spu;
import top.leyou.item.pojo.SpuDetail;

import java.util.List;

public interface GoodsService {
    PageResult<Spu> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows);

    void saveGoods(Spu spu);

    SpuDetail querySpuDetailBySpuId(Long spuId);

    List<Sku> querySkuBySpuId(Long spuId);

    void updateGoods(Spu spu);
}
