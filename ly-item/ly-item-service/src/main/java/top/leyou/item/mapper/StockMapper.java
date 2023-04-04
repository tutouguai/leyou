package top.leyou.item.mapper;

import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;
import top.leyou.common.mapper.BaseMapper;
import top.leyou.item.pojo.Stock;

public interface StockMapper extends BaseMapper<Stock, Long> {
}
