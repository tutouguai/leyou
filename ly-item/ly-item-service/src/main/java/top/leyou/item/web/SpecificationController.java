package top.leyou.item.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.leyou.item.pojo.SpecGroup;
import top.leyou.item.pojo.SpecParam;
import top.leyou.item.service.SpecificationService;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecificationController {

    @Resource
    private SpecificationService specService;

    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid")Long cid){
        return ResponseEntity.ok(specService.queryGroupByCid(cid));
    }

    /**
     * 查询参数集合
     * @param gid
     * @param cid
     * @param searching
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamList(@RequestParam(value = "gid", required = false) Long gid,
                                                          @RequestParam(value = "cid", required = false) Long cid,
                                                          @RequestParam(value = "searching", required = false) Boolean searching){
        return ResponseEntity.ok(specService.queryParamList(gid, cid, searching));
    }

}
