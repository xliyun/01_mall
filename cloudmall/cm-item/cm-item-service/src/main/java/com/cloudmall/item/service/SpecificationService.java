package com.cloudmall.item.service;

import com.cloudmall.common.enums.ExceptionEnum;
import com.cloudmall.common.exception.CmException;
import com.cloudmall.item.mapper.SpecGroupMapper;
import com.cloudmall.item.mapper.SpecParamMapper;
import com.cloudmall.item.pojo.SpecGroup;
import com.cloudmall.item.pojo.SpecParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

@Service
public class SpecificationService {

    @Autowired
    private SpecGroupMapper groupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    public List<SpecGroup> queryGroupById(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> list = groupMapper.select(specGroup);
        if(list.isEmpty()){
            //没查到
            throw new CmException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        return list;
    }

    public void saveGroup(SpecGroup specGroup) {
        specGroup.setId(null);
        if(specGroup.getCid()==null){
            throw  new CmException(ExceptionEnum.LACK_CID);
        }
        int count=groupMapper.insert(specGroup);
        if(count!=1){
            throw new CmException(ExceptionEnum.BRAND_SAVE_GROUP);
        }
    }

/*    public List<SpecParam> queryParamByGid(Long gid) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        List<SpecParam> list = specParamMapper.select(specParam);
        if(CollectionUtils.isEmpty(list)){
            throw new CmException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        return list;
    }*/

    public void saveGroupParam(SpecParam specParam) {
        specParam.setId(null);
        if(specParam.getCid()==null &&specParam.getGeneric()==null && specParam.getGroupId()==null && specParam.getName()==null){
            throw  new CmException(ExceptionEnum.LACK_PARAM);
        }
        int count=specParamMapper.insert(specParam);
        if(count!=1){
            throw new CmException(ExceptionEnum.BRAND_SAVE_GROUP_PARAM);
        }
    }

    public List<SpecParam> queryParamList(Long gid, Long cid, Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        //select是根据类中非空字段去查询，如果这三个字段有空的，查询的时候就不为条件
        List<SpecParam> list = specParamMapper.select(specParam);
        if(CollectionUtils.isEmpty(list)){
            throw new CmException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        return list;
    }
}
