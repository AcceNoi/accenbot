package org.accen.dmzj.web.dao;

import org.accen.dmzj.web.vo.CfgResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CfgResourceMapper {
	@Results(id = "cfgResourceMapper",value = {
			@Result(property = "id",column = "id"),
			@Result(property = "cfgKey",column = "cfg_key"),
			@Result(property = "cfgResource",column = "cfg_resource"),
			@Result(property = "resourceType",column = "resource_type")
	})
	@Select("select * from cfg_resource where id = #{id}")
	public CfgResource selectById(@Param("id")long id);
	@ResultMap("cfgResourceMapper")
	@Select("select * from cfg_resource where cfg_key = #{key} limit 1")
	public CfgResource selectByKey(@Param("key")String key);
}
