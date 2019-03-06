package com.cloudmall.item.service;

import com.cloudmall.common.enums.ExceptionEnum;
import com.cloudmall.common.exception.CmException;
import com.cloudmall.item.mapper.CategoryMapper;
import com.cloudmall.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> queryCategoryListByPid(Long pid){
        //
        Category c=new Category();
        c.setParentId(pid);
        List<Category> select = categoryMapper.select(c);
        //判断结果是否为空
        if(select==null || select.isEmpty()){//或者CollectionUtils.isEmpty()
            throw new CmException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return select;
    }
}
