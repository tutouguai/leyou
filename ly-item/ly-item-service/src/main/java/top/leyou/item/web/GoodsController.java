package top.leyou.item.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.leyou.common.vo.PageResult;
import top.leyou.item.pojo.Sku;
import top.leyou.item.pojo.Spu;
import top.leyou.item.pojo.SpuDetail;
import top.leyou.item.service.GoodsService;
import top.leyou.item.service.impl.GoodsServiceImpl;

import javax.annotation.Resource;
import javax.websocket.server.PathParam;
import java.util.List;

@RestController
public class GoodsController {
    @Resource
    private GoodsService goodsService;

    /**
     * 分页查询spu
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<Spu>> querySpuByPage(
        @RequestParam(value = "key", required = false) String key,
        @RequestParam(value = "saleable", required = false) Boolean saleable,
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        @RequestParam(value = "rows", defaultValue = "5") Integer rows
    ){
        return ResponseEntity.ok(goodsService.querySpuByPage(key, saleable, page, rows));
    }
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu){
        goodsService.saveGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/spu/detail/{id}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("id") Long spuId){
        return ResponseEntity.ok(goodsService.querySpuDetailBySpuId(spuId));
    }
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long spuId){
        return ResponseEntity.ok(goodsService.querySkuBySpuId(spuId));
    }
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody Spu spu){
        goodsService.updateGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

