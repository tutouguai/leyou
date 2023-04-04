package top.leyou.item.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.leyou.item.pojo.Brand;

public interface BrandApi {
    @GetMapping("brand/{id}")
    Brand queryBrandById(@PathVariable("id")Long id);
}
