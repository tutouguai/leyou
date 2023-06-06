package top.leyou.item.service;

import top.leyou.common.vo.PageResult;
import top.leyou.item.pojo.Brand;
import top.leyou.item.pojo.Category;

import java.util.List;

public interface BrandService {
    PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String sortBy, boolean desc, String key);

    void saveBrand(Brand brand, List<Long> cids);

    void editBrand(Brand brand, List<Long> cids);

    void deleteBrand(Long id);

    Brand queryById(Long id);

    List<Brand> queryBrandByCid(Long cid);

    List<Brand> queryByIds(List<Long> ids);
}
