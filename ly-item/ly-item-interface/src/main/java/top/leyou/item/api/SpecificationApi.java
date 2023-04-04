package top.leyou.item.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.leyou.item.pojo.SpecParam;

import java.util.List;

public interface SpecificationApi {
    @GetMapping("spec/params")
    List<SpecParam> queryParamList(@RequestParam(value = "gid", required = false) Long gid,
                                                          @RequestParam(value = "cid", required = false) Long cid,
                                                          @RequestParam(value = "searching", required = false) Boolean searching);
    }
