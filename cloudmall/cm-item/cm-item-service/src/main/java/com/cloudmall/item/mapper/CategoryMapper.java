package com.cloudmall.item.mapper;

import com.cloudmall.item.pojo.Category;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

//想要在mapper中根据id查询或删除,PK是主键类型 IdListMapper继承了SelectByIdListMapper<T, PK>, DeleteByIdListMapper<T, PK>
public interface CategoryMapper extends Mapper<Category>,IdListMapper<Category,Long> {
}
