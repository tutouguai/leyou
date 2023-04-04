package top.leyou.search.client;

import org.springframework.cloud.openfeign.FeignClient;
import top.leyou.item.api.CategoryApi;

@FeignClient(name = "item-service", contextId = "CategoryClient")
public interface CategoryClient extends CategoryApi {
}
