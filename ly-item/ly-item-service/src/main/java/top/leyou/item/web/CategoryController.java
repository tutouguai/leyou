package top.leyou.item.web;


import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import top.leyou.item.pojo.Brand;
import top.leyou.item.pojo.Category;

import top.leyou.item.service.impl.CategoryServiceImpl;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryServiceImpl categoryService;

    /**
     * 根据父节点id查询商品分类
     * @param pid
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoryListByPid(@RequestParam("pid")Long pid){

        return ResponseEntity.ok(categoryService.queryCategoryListByPid(pid));
    }
    @PostMapping("add")
    public Integer addCategory(@RequestBody Category category){
        return categoryService.addCategory(category);
    }
    @PutMapping("edit")
    public void editCategory(@RequestBody Category category){
        categoryService.editCategory(category);
    }

    @DeleteMapping("delete")
    public void deleteCategory(@RequestParam Long id){
        categoryService.deleteCategory(id);
    }

    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> queryCategoryByBrandId(@PathVariable("bid") Long bid){
        List<Category> list = this.categoryService.queryByBrandId(bid);
        return ResponseEntity.ok(list);
    }

    /**
     * 根据id查询商品分类
     * @param ids
     * @return
     */
    @GetMapping("list/ids")
    public ResponseEntity<List<Category>> queryCategoryByIds(@RequestParam("ids") List<Long> ids){
        return ResponseEntity.ok(categoryService.queryByIds(ids));
    }

}
