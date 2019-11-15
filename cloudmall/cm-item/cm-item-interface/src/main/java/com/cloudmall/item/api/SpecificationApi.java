package com.cloudmall.item.api;

import com.cloudmall.item.pojo.SpecGroup;
import com.cloudmall.item.pojo.SpecParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface SpecificationApi {
    /**
     * 通过cid(类别id)和gid(组id) 都能查询到，也就是两个参数传递一个就行，所以改造上一个接口在参数上加required=false
     * @param gid 组id
     * @Param cid 分类id
     * @Param searching 是否搜索
     * @return
     */
    @GetMapping("spec/params")                                //RequestParam是加在路径后的问号上的参数
    List<SpecParam> queryParamList(@RequestParam(value = "gid",required = false)Long gid,
                                                          @RequestParam(value = "cid",required = false)Long cid,
                                                          @RequestParam(value = "searching",required = false)Boolean searching);

    //一个分类下多个规格组
    @GetMapping("spec/group")
    List<SpecGroup> queryGroupListByCid(@RequestParam("cid") Long cid );
}
