package top.leyou.item.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import top.leyou.common.enums.ExceptionEnum;
import top.leyou.common.exception.LyException;
import top.leyou.item.mapper.SpecGroupMapper;
import top.leyou.item.mapper.SpecParamMapper;
import top.leyou.item.pojo.SpecGroup;
import top.leyou.item.pojo.SpecParam;
import top.leyou.item.service.SpecificationService;

import javax.annotation.Resource;
import java.util.*;

@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Resource
    private SpecGroupMapper specGroupMapper;
    @Resource
    private SpecParamMapper specParamMapper;


    @Override
    public List<SpecGroup> queryGroupByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> specGroups = specGroupMapper.select(specGroup);
        if(CollectionUtils.isEmpty(specGroups)){
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        return specGroups;
    }

    @Override
    public List<SpecParam> queryParamByGid(Long gid) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        List<SpecParam> select = specParamMapper.select(specParam);
        if(CollectionUtils.isEmpty(select)){
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUNT);
        }
        return select;
    }

    @Override
    public List<SpecParam> queryParamList(Long gid, Long cid, Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        List<SpecParam> select = specParamMapper.select(specParam);
        if(CollectionUtils.isEmpty(select)){
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUNT);
        }
        return select;
    }
}
