package top.leyou.item.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import top.leyou.common.enums.ExceptionEnum;
import top.leyou.common.exception.LyException;
import top.leyou.item.mapper.CategoryMapper;
import top.leyou.item.pojo.Category;
import top.leyou.item.service.CategoryService;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Resource
    private CategoryMapper categoryMapper;
    @Override
    public List<Category> queryCategoryListByPid(Long pid) {
        Category category = new Category();
        category.setParentId(pid);
        List<Category> resultList = categoryMapper.select(category);
        if(CollectionUtils.isEmpty(resultList)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOND);
        }
        return resultList;
    }

    @Transactional
    public Integer addCategory(Category category) {
//        category.setId(null);
        return categoryMapper.insert(category);

    }

    @Transactional
    @Override
    public void editCategory(Category category) {
        Example example = new Example(Category.class);
        example.createCriteria().orEqualTo("id", category.getId());
        Category category1 = categoryMapper.selectByPrimaryKey(category.getId());
        category1.setName(category.getName());
        categoryMapper.updateByExample(category1, example);
    }

    @Transactional
    @Override
    public void deleteCategory(Long id) {
        categoryMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Category> queryByBrandId(Long bid) {
        List<Category> categories = categoryMapper.queryByBrandId(bid);
        if(CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOND);
        }
        return categories;
    }
    @Override
    public List<Category> queryByIds(List<Long> ids){
        List<Category> categories = categoryMapper.selectByIdList(ids);
        if(CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOND);
        }
        return categories;
    }
}
