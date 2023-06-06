package top.leyou.item.web;

import org.apache.ibatis.annotations.Delete;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.leyou.common.vo.PageResult;
import top.leyou.item.pojo.Brand;
import top.leyou.item.service.impl.BrandServiceImpl;

import javax.annotation.Resource;
import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {

    @Resource
    private BrandServiceImpl brandService;

    @GetMapping("/page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
         @RequestParam(value = "page", defaultValue = "1") Integer page,
         @RequestParam(value = "rows", defaultValue = "5") Integer rows,
         @RequestParam(value = "sortBy", required = false) String sortBy,
         @RequestParam(value = "desc", defaultValue = "false") boolean desc,
         @RequestParam(value = "key", required = false) String key
    ){
        PageResult<Brand> result = brandService.queryBrandByPage(page,rows,sortBy,desc,key);
        return ResponseEntity.ok(result);
    }
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        this.brandService.saveBrand(brand, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Void> editBrand( Brand brand, @RequestParam("cids") List<Long> cids) {
        this.brandService.editBrand(brand, cids);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping
    public ResponseEntity<Void> deleteBrand(@PathParam("id") Long id) {
        this.brandService.deleteBrand(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable("cid") Long cid){
        return ResponseEntity.ok(brandService.queryBrandByCid(cid));
    }

    @GetMapping("{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id")Long id){
        return ResponseEntity.ok(brandService.queryById(id));
    }

    @GetMapping("brands")
    public ResponseEntity<List<Brand>> queryBrandByIds(@RequestParam("ids")List<Long> ids){
        return ResponseEntity.ok(brandService.queryByIds(ids));
    }
}
