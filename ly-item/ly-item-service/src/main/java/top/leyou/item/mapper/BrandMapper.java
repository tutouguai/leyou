package top.leyou.item.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import top.leyou.common.mapper.BaseMapper;
import top.leyou.item.pojo.Brand;

import java.util.List;

public interface BrandMapper extends BaseMapper<Brand, Long> {

    @Insert("INSERT INTO tb_category_brand (category_id, brand_id) VALUES (#{cid},#{bid})")
    int insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    @Select("SELECT category_id FROM tb_category_brand WHERE brand_id=#{bid}")
    List<Long> selectCategoryBrand(@Param("bid")Long bid);

    @Delete("DELETE FROM tb_category_brand WHERE category_id=#{cid} and brand_id=#{bid}")
    int deleteCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    @Delete("DELETE FROM tb_category_brand WHERE brand_id=#{bid}")
    int deleteCategoryBrandByBrandId(@Param("bid") Long bid);

    @Select("SELECT\tb.id, b.name, b.letter, b.image\n" +
            "FROM tb_brand b\n" +
            "INNER JOIN tb_category_brand cb ON b.id = cb.brand_id\n" +
            "WHERE cb.category_id = #{cid}")
    List<Brand> selectBrandByCategoryId(@Param("cid") Long cid);
}
