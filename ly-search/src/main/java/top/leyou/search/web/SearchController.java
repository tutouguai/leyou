package top.leyou.search.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.leyou.common.vo.PageResult;
import top.leyou.search.pojo.Goods;
import top.leyou.search.pojo.SearchRequest;
import top.leyou.search.service.SearchService;

import javax.annotation.Resource;
import javax.xml.ws.Response;

@RestController
public class SearchController {

    @Resource
    private SearchService searchService;
    @PostMapping("page")
    public ResponseEntity<PageResult<Goods>> search(@RequestBody SearchRequest searchRequest){
        return ResponseEntity.ok(searchService.search(searchRequest));
    }
}
