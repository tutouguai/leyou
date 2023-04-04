package top.leyou.item.service;

import top.leyou.item.pojo.SpecGroup;
import top.leyou.item.pojo.SpecParam;

import java.util.List;

public interface SpecificationService {
    List<SpecGroup> queryGroupByCid(Long cid);

    List<SpecParam> queryParamByGid(Long gid);

    List<SpecParam> queryParamList(Long gid, Long cid, Boolean searching);
}
