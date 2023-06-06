package top.leyou.item.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import top.leyou.item.pojo.Brand;

import java.util.List;

public interface BrandApi {
    @GetMapping("brand/{id}")
    Brand queryBrandById(@PathVariable("id")Long id);

    @GetMapping("brand/brands")
    List<Brand> queryBrandByIds(@RequestParam("ids")List<Long> ids);
}
