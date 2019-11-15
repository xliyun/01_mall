package com.cloudmall.item.vo;

import com.cloudmall.item.pojo.Spu;

public class SpuVo extends Spu {
    private String cname;
    private String bname;

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    @Override
    public String toString() {
        return super.toString()+"   SpuVo{" +
                "cname='" + cname + '\'' +
                ", bname='" + bname + '\'' +
                '}';
    }
}
