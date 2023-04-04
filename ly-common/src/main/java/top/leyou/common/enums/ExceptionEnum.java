package top.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {
    PRICE_CANNOT_BE_NULL(400, "价格不能为空!"),
    CATEGORY_NOT_FOND(404, "商品分类未查到"),
    BRAND_NOT_FOND(404, "品牌未查到"),
    BRAND_SAVE_ERROR(500, "新增品牌失败"),
    UPLOAD_ERROR(500, "上传文件失败"),
    INVALID_FILE_TYPE(400, "非法的文件类型"),
    BRAND_EDIT_ERROR(500, "修改品牌失败"),
    DELETE_BRAND_ERROR(500, "删除品牌失败"),
    SPEC_GROUP_NOT_FOUND(404, "商品规格组不存在"),
    SPEC_PARAM_NOT_FOUNT(404, "商品规格参数不存在"),
    GOODS_NOT_FOUND(404, "商品不存在"),
    GOODS_SAVE_ERROR(500, "商品新增失败"),
    SPU_DETAIL_NOT_FOUND(404, "商品详情不存在"),
    SKU_NOT_FOUND(404,"商品SKU不存在"),
    SKU_STOCK_NOT_FOUND(404, "商品库存不存在"),
    GOODS_UPDATE_ERROR(500, "商品信息更新失败"),
    GOODS_ID_CANNOT_BE_NULL(400, "商品id不能为空")

    ;
    private Integer code;
    private String msg;

}
