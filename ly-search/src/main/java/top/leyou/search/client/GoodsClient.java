package top.leyou.search.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.leyou.item.api.GoodsApi;
import top.leyou.item.pojo.Sku;
import top.leyou.item.pojo.Spu;
import top.leyou.item.pojo.SpuDetail;

import java.util.List;

@FeignClient(name = "item-service", contextId = "GoodsClient")
public interface GoodsClient extends GoodsApi {
}