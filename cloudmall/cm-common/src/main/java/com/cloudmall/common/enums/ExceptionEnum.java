package com.cloudmall.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

//@Getter
//@NoArgsConstructor//自动生成无参数构造函数。
//@AllArgsConstructor//自动生成全参数构造函数。
public enum ExceptionEnum {

    //private  static final ExceptionEnum ff=new ExceptionEnum(400,"价格不能为空");//老写法
    //枚举必须定义在最前面
    PRICE_CANNOT_BE_NULL(400,"价格不能为空!"),
    PRODUCT_CANNOT_BE_NULL(400,"商品不能为空!"),
    CATEGORY_NOT_FOUND(404,"没有查询到商品分类"),
    BRAND_NOT_FOUND(404,"品牌不存在"),

    SPEC_GROUP_NOT_FOUND(500,"商品规格组不存在"),
    LACK_CID(500,"新增的规格组缺少cid"),
    BRAND_SAVE_GROUP(500,"新增规格组失败"),
    SPEC_PARAM_NOT_FOUND(500,"商品规格参数不存在"),
    LACK_PARAM(500,"新增的规格参数缺少数据"),
    BRAND_SAVE_GROUP_PARAM(500,"新增规格组参数失败"),

    BRAND_SAVE_EROOR(500,"新增品牌失败"),
    BRAND_INTERMEDIDATE_SAVE_EROOR(500,"新增品牌分类中间表失败"),
    UPLOAD_FILE_ERROR(500,"文件上传失败!"),
    INVALID_FILE_TYPE(500,"无效的文件类型"),

    GOODS_NOT_FOUND(404,"商品不存在"),
    GOODS_DETAIL_NOT_FOUND(404,"商品详情不存在"),
    GOODS_SKU_NOT_FOUND(404,"商品的SKU不存在"),
    GOODS_STOCK_NOT_FOUND(404,"商品库存不存在不存在"),
    GOODS_SAVE_ERROR(500,"新增商品失败"),
    GOODS_UPDATE_ERROR(500,"更新商品失败"),
    GOODS_ID_CANNOT_BE_NULL(400,"商品id不能为空"),
    ;
    private int code;
    private String msg;

    ExceptionEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
