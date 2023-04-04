package top.leyou.search.client;

import org.springframework.cloud.openfeign.FeignClient;
import top.leyou.item.api.BrandApi;

@FeignClient(name = "item-service", contextId = "BrandClient")
public interface BrandClient extends BrandApi {
}
