package top.leyou.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import top.leyou.common.enums.ExceptionEnum;
import top.leyou.common.exception.LyException;
import top.leyou.common.vo.PageResult;
import top.leyou.item.mapper.BrandMapper;
import top.leyou.item.pojo.Brand;
import top.leyou.item.pojo.Category;
import top.leyou.item.service.BrandService;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BrandServiceImpl implements BrandService {
    @Resource
    private BrandMapper brandMapper;

    @Override
    public PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String sortBy, boolean desc, String key) {
        //分页
        PageHelper.startPage(page,rows);//通过拦截器实现，在实现sql前拼接一段分页的sql语句
        //条件过滤
        Example example = new Example(Brand.class);
        if(StringUtils.isNoneBlank(key)){
            example.createCriteria().orLike("name", "%"+key+"%")
                    .orEqualTo("letter",key.toUpperCase());
        }
        //排序
        if(StringUtils.isNoneBlank(sortBy)){
            String orderByClause = sortBy + (desc?" DESC":" ASC");
            example.setOrderByClause(orderByClause);
        }
        //查询
        List<Brand> brands = brandMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOND);
        }
        //计算总条数，分页助手完成
        PageInfo<Brand> brandPageInfo = new PageInfo<>(brands);

        return new PageResult<Brand>(brandPageInfo.getTotal(), brands);
    }

    @Transactional
    @Override
    public void saveBrand(Brand brand, List<Long> cids) {
        int count = brandMapper.insert(brand);
        if(count !=1 ){
            throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
        }
        //新增中间表
        Long bid = brand.getId();//新增完成自动回显数据
        for (Long cid : cids) {
            int i = brandMapper.insertCategoryBrand(cid, bid);
            if(i != 1){
                throw  new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
            }
        }
    }

    @Transactional
    @Override
    public void editBrand(Brand brand, List<Long> cids) {
//        Example example = new Example(Brand.class);
//        example.createCriteria().orEqualTo("name",brand.getName());
//        //修改tb_brand表
//        int count = brandMapper.updateByExample(brand, example);
        int count = brandMapper.updateByPrimaryKey(brand);
        if(count !=1 ){
            throw new LyException(ExceptionEnum.BRAND_EDIT_ERROR);
        }
        Long bid = brand.getId();//新增完成自动回显数据

        //修改中间表数据中的cid
        //1. 查询中间表数据
        List<Long> lastCid = brandMapper.selectCategoryBrand(bid);
        //2. 比对数据，进行新增或删除，设计一个比对算法
        // 1:删除,2:不变:,7:新增
        HashMap<Long, Integer> cidMap = new HashMap<Long, Integer>();
        for (Long aLong : lastCid) {
            cidMap.put(aLong, 1);
        }
        for (Long cid : cids) {
            cidMap.put(cid, cidMap.containsKey(cid)?cidMap.get(cid)+1:7);
        }
        Iterator<Long> iterator = cidMap.keySet().iterator();
        while(iterator.hasNext()){
            Long next = iterator.next();
            Integer cidNum = cidMap.get(next);
            if(cidNum==1){
                brandMapper.deleteCategoryBrand(next, bid);
            }
            else if(cidNum==7){
                brandMapper.insertCategoryBrand(next, bid);
            }
        }
    }

    @Transactional
    @Override
    public void deleteBrand(Long id) {
        int status = brandMapper.deleteByPrimaryKey(id);
        int count = brandMapper.deleteCategoryBrandByBrandId(id);
        if(status!=1 || count!=1){
            throw new LyException(ExceptionEnum.DELETE_BRAND_ERROR);
        }
    }

    @Override
    public Brand queryById(Long id){
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if(brand==null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOND);
        }
        return brand;
    }

    @Override
    public List<Brand> queryBrandByCid(Long cid) {
        List<Brand> brands = brandMapper.selectBrandByCategoryId(cid);
        if(CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOND);
        }
        return brands;
    }
}
