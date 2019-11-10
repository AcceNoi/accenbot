package org.accen.dmzj.web.dao;

import org.accen.dmzj.web.vo.CfgConfigValue;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CfgConfigValueMapper {
	@Results(id = "cfgConfigMapper",value = {
			@Result(property = "id",column = "id"),
			@Result(property = "targetType",column = "target_type"),
			@Result(property = "target",column = "target"),
			@Result(property = "configKey",column = "config_key"),
			@Result(property = "configValue",column = "config_value"),
			@Result(property = "updateUserId",column = "update_user_id"),
			@Result(property = "updateTime",column = "update_time")
	})
	@Select("select * from cfg_config_value where id = #{id} ")
	public CfgConfigValue selectById(@Param("id")long id);
	
	@ResultMap("cfgConfigMapper")
	@Select("select * from cfg_config_value where target_type = #{targetType} and target = #{target}  and config_key = #{configKey} ")
	public CfgConfigValue selectByTargetAndKey(@Param("targetType")String targetType
			,@Param("target")String target
			,@Param("configKey")String configKey);
	
	@Insert("insert into cfg_config_value(target_type,target,config_key,config_value,update_user_id,update_time) "
			+ " values(#{targetType},#{target},#{configKey},#{configValue},#{updateUserId},#{updateTime})")
	@Options(useGeneratedKeys = true,keyProperty = "id")
	public long insert(CfgConfigValue config);
	
	@Update("update cfg_config_value set config_value = #{configValue},update_user_id = #{updateUserId},update_time=#{updateTime} "
			+ " where target_type = #{targetType} and target = #{target}  and config_key = #{configKey}  ")
	public int updateValue(CfgConfigValue config);
}
