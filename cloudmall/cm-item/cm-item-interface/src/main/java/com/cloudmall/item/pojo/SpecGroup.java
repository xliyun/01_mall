package com.cloudmall.item.pojo;

import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Table(name="tb_spec_group")
public class SpecGroup {
    @Id
    @KeySql(useGeneratedKeys=true)
    private Long id;

    private Long cid;

    private String name;

    //规格组对应的规格参数
    @Transient
    private List<SpecParam> params;

    public List<SpecParam> getParams() {
        return params;
    }

    public void setParams(List<SpecParam> params) {
        this.params = params;
    }

    public SpecGroup() {
    }

    public SpecGroup(Long id, Long cid, String name) {
        this.id = id;
        this.cid = cid;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SpecGroup{" +
                "id=" + id +
                ", cid=" + cid +
                ", name='" + name + '\'' +
                ", params=" + params.toString() +
                '}';
    }
}
