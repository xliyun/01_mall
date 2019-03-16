package com.cloudmall.common.mapper;

import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;
//InsertListMapper有两个 一个必须有属性名是id并且是自增的 tk.mybatis.mapper.common.special.InsertListMapper
//一个只要设置@id就行
public interface BaseListMapper<T> extends Mapper<T>, IdListMapper<T,Long>, InsertListMapper<T> {
}
