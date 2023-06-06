package top.leyou.page.client;

import org.springframework.cloud.openfeign.FeignClient;
import top.leyou.item.api.GoodsApi;

@FeignClient(name = "item-service", contextId = "GoodsClient")
public interface GoodsClient extends GoodsApi {
}
