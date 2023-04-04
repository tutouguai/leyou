package top.leyou.item.mapper;

import org.apache.ibatis.annotations.Select;
import org.w3c.dom.stylesheets.LinkStyle;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;
import top.leyou.item.pojo.Category;

import java.util.List;

public interface CategoryMapper extends Mapper<Category>, IdListMapper<Category, Long> {
    @Select("SELECT * FROM tb_category WHERE id IN (SELECT category_id FROM tb_category_brand WHERE brand_id = #{bid})")
    List<Category> queryByBrandId(Long bid);
}
