package top.leyou.common.mapper;

import org.apache.catalina.Manager;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;


@tk.mybatis.mapper.annotation.RegisterMapper
public interface BaseMapper<T,PK> extends Mapper<T>, IdListMapper<T,PK>, InsertListMapper<T>{
}
