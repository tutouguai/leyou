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
import top.leyou.item.mapper.SkuMapper;
import top.leyou.item.mapper.SpuDetailMapper;
import top.leyou.item.mapper.SpuMapper;
import top.leyou.item.mapper.StockMapper;
import top.leyou.item.pojo.*;
import top.leyou.item.service.BrandService;
import top.leyou.item.service.CategoryService;
import top.leyou.item.service.GoodsService;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoodsServiceImpl implements GoodsService {
    @Resource
    private SpuMapper spuMapper;
    @Resource
    private SpuDetailMapper spuDetailMapper;
    @Resource
    private CategoryService categoryService;
    @Resource
    private BrandService brandService;
    @Resource
    private SkuMapper skuMapper;
    @Resource
    private StockMapper stockMapper;

    @Override
    public PageResult<Spu> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows) {
        // 分页
        PageHelper.startPage(page, rows);
        //过滤
        Example example = new Example(Spu.class);
        //搜索字段过滤
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNoneBlank(key)){
            criteria.andLike("title", "%"+key+"%");
        }
        //上下架过滤
        if(saleable !=null){
            criteria.andEqualTo("saleable", saleable);
        }
        //默认排序
        example.setOrderByClause("last_update_time DESC");

        //查询
        List<Spu> spus = spuMapper.selectByExample(example);

        //判断
        if(CollectionUtils.isEmpty(spus)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }

        //解析分类与品牌名称
        loadCategoryAndBrandName(spus);

        //解析分页结果
        PageInfo<Spu> spuPageInfo = new PageInfo<>(spus);

        return new PageResult<>(spuPageInfo.getTotal(), spus);
    }

    @Override
    @Transactional
    public void saveGoods(Spu spu) {
        //1.保存spu
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        spu.setId(null);
        spu.setSaleable(true);
        spu.setValid(false);
        int count = spuMapper.insert(spu);
        if(count !=1){
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
        //2.保存spkDetail
        //查询Spu的Id，填入SpuDetail中
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        count = spuDetailMapper.insert(spuDetail);
        if(count !=1){
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }


        saveSkuAndStock(spu);

    }

    @Transactional
    public void saveSkuAndStock(Spu spu) {
        //定义库存集合
        List<Stock> stockList = new ArrayList<>();

        //3.1保存sku, 3.2保存商品stock库存信息
        List<Sku> skus = spu.getSkus();
        for (Sku sku : skus) {
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            sku.setSpuId(spu.getId());
            int count = skuMapper.insert(sku);
            if(count !=1){
                throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
            }
            //查询刚插入的sku的Id,设置到Stock里面
//            sku.getId(), sku.getStock()
            Stock stock = new Stock(sku.getId(), sku.getStock());
            stockList.add(stock);
        }

        int count = stockMapper.insertList(stockList); //返回，修改行数
        if(count !=stockList.size()){
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
    }

    @Override
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(spuId);
        if(spuDetail == null){
            throw new LyException(ExceptionEnum.SPU_DETAIL_NOT_FOUND);
        }
        return spuDetail;
    }

    @Override
    public List<Sku> querySkuBySpuId(Long spuId) {
        //查询sku
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(sku);
        if(CollectionUtils.isEmpty(skus)){
            throw new LyException(ExceptionEnum.SKU_NOT_FOUND);
        }
        //查询库存
//        for (Sku s : skus) {
//            Stock stock = stockMapper.selectByPrimaryKey(s.getId());
//            if(stock == null){
//                throw new LyException(ExceptionEnum.SKU_STOCK_NOT_FOUND);
//            }
//            s.setStock(stock.getStock());
//        }
        List<Long> ids = skus.stream().map(Sku::getId).collect(Collectors.toList());
        List<Stock> stocks = stockMapper.selectByIdList(ids);
        Map<Long, Integer> stockMap = stocks.stream().collect(Collectors.toMap(Stock::getSkuId, Stock::getStock));
        skus.forEach(s -> s.setStock(stockMap.get(s.getId())));

        return skus;
    }

    @Transactional
    @Override
    public void updateGoods(Spu spu) {
        if(spu.getId() == null){
            throw new LyException(ExceptionEnum.GOODS_ID_CANNOT_BE_NULL);
        }
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        //查询sku
        List<Sku> skuList = skuMapper.select(sku);
        if(!CollectionUtils.isEmpty(skuList)){
            //删除sku
            skuMapper.delete(sku);
            //删除stock
            List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
            stockMapper.deleteByIdList(ids);
        }
        //修改spu
        spu.setValid(null);
        spu.setSaleable(null);
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);
        int count = spuMapper.updateByPrimaryKeySelective(spu);//根据主键更新不为null的值
        if(count != 1){
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }
        //修改detail
        count = spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
        if(count != 1){
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }
        //新增sku、stock
        saveSkuAndStock(spu);
    }

    @Override
    public Spu querySpuById(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu == null)
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        //查询sku
        spu.setSkus(querySkuBySpuId(id));
        //查询spuDetails
        spu.setSpuDetail(querySpuDetailBySpuId(id));
        return spu;
    }

    private void loadCategoryAndBrandName(List<Spu> spus){
        for (Spu spu : spus) {
            //处理分类名称
            List<String> name = categoryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                    .stream().map(c -> c.getName()).collect(Collectors.toList());
            spu.setCname(StringUtils.join(name, "/"));
            //处理品牌名称
            spu.setBname(brandService.queryById(spu.getBrandId()).getName());
//            Map<Integer, Integer> hashMap = new HashMap<>();
        }
    }
}
