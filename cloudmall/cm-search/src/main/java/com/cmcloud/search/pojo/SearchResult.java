package com.cmcloud.search.pojo;

import com.cloudmall.common.vo.PageResult;
import com.cloudmall.item.pojo.Brand;
import com.cloudmall.item.pojo.Category;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

/*@Data
@NoArgsConstructor
@AllArgsConstructor*/
public class SearchResult extends PageResult<Goods> {

    private List<Category> categories; //分类待选项

    private List<Brand> brands; //品牌待选项

    private List<Map<String,Object>> specs;//规格参数过滤条件


    public SearchResult() {
    }

    //构造函数比较长，可以用工厂模式 TODO
    public SearchResult(Long total, Long totalPage, List<Goods> items, List<Category> categories, List<Brand> brands) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
    }

    public SearchResult(List<Category> categories, List<Brand> brands, List<Map<String, Object>> specs) {
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }

    public SearchResult(Long total, List<Goods> items, List<Category> categories, List<Brand> brands, List<Map<String, Object>> specs) {
        super(total, items);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }

    public SearchResult(Long total, Long totalPage, List<Goods> items, List<Category> categories, List<Brand> brands, List<Map<String, Object>> specs) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }

    public List<Map<String, Object>> getSpecs() {
        return specs;
    }

    public void setSpecs(List<Map<String, Object>> specs) {
        this.specs = specs;
    }
}