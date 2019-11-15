package com.cloudmall.item.web;

import com.cloudmall.item.pojo.SpecGroup;
import com.cloudmall.item.pojo.SpecParam;
import com.cloudmall.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecificationController {
    @Autowired
    private SpecificationService specificationService;

    /**
     * 根据分类id查询规格组
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")                           //PathVariable是加在路径上的参数
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid") Long cid){

        return ResponseEntity.ok(specificationService.queryGroupById(cid));
    }

/*    *//**
     * 根据id查询规格组参数
     * @param gid
     * @return
     *//*
    @GetMapping("params")                                //RequestParam是加在路径后的问号上的参数
    public ResponseEntity<List<SpecParam>> queryParamByGid(@RequestParam("gid")Long gid){

        return ResponseEntity.ok(specificationService.queryParamByGid(gid));
    }*/
    /**
     * 通过cid(类别id)和gid(组id) 都能查询到，也就是两个参数传递一个就行，所以改造上一个接口在参数上加required=false
     * @param gid 组id
     * @Param cid 分类id
     * @Param searching 是否搜索
     * @return
     */
    @GetMapping("params")                                //RequestParam是加在路径后的问号上的参数
    public ResponseEntity<List<SpecParam>> queryParamList(@RequestParam(value = "gid",required = false)Long gid,
                                                          @RequestParam(value = "cid",required = false)Long cid,
                                                          @RequestParam(value = "searching",required = false)Boolean searching){

        return ResponseEntity.ok(specificationService.queryParamList(gid,cid,searching));
    }

    /**
     * 新增规格组
     * @param specGroup
     * @return
     */
    @PostMapping(value="group")//RequestBody是为了获取到post请求中body体的参数，Postman中body row的参数，如果是form-data可以不加RequestBody
    public ResponseEntity<Void> saveGroup(@RequestBody SpecGroup specGroup){
        specificationService.saveGroup(specGroup);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
/*    @DeleteMapping
    public RequestEntity<Void> deleteGroup(@RequestBody )*/

    @PostMapping(value="param")//RequestBody是为了获取到post请求中body体的参数，Postman中body row的参数，如果是form-data可以不加RequestBody
    public ResponseEntity<Void> saveGroupParam(@RequestBody SpecParam specParam){
        specificationService.saveGroupParam(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //一个分类下多个规格组
    @GetMapping("group")
    public ResponseEntity<List<SpecGroup>> queryGroupListByCid(@RequestParam("cid") Long cid ){
        return ResponseEntity.ok(specificationService.queryListByCid(cid));
    }

}
