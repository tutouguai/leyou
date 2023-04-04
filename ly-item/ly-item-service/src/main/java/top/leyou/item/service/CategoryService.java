package top.leyou.item.service;

import top.leyou.item.pojo.Category;

import java.util.List;

public interface CategoryService {
    public List<Category> queryCategoryListByPid(Long pid);

    public Integer addCategory(Category category);

    void editCategory(Category category);

    void deleteCategory(Long id);

    List<Category> queryByBrandId(Long bid);

    List<Category> queryByIds(List<Long> ids);
}
