package com.cloudmall.item.pojo;

import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="tb_category")
public class Category {
	@Id
	@KeySql(useGeneratedKeys=true)
	private Long id;
	private String name;
	private Long parentId;
	private Boolean isParent;
	private Integer sort;
	// getter和setter略
    // 注意isParent的get和set方法


	public Category() {
	}

	public Category(Long id, String name, Long parentId, Boolean isParent, Integer sort) {
		this.id = id;
		this.name = name;
		this.parentId = parentId;
		this.isParent = isParent;
		this.sort = sort;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(Boolean parent) {
		isParent = parent;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

}