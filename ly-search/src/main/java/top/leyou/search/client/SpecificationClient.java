package top.leyou.search.client;

import org.springframework.cloud.openfeign.FeignClient;
import top.leyou.item.api.SpecificationApi;

@FeignClient(name = "item-service", contextId = "SpecificationClient")
public interface SpecificationClient extends SpecificationApi {
}
