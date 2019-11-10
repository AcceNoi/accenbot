package org.accen.dmzj.web.dao;

import java.util.List;

import org.accen.dmzj.web.vo.CfgResource;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
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
			@Result(property = "resourceType",column = "resource_type"),
			@Result(property = "title",column = "title"),
			@Result(property = "content",column = "content"),
			@Result(property = "image",column = "image"),
			@Result(property = "originResource",column = "origin_resource"),
			@Result(property = "createUserId",column = "create_user_id"),
			@Result(property = "createUserName",column = "create_user_name"),
			@Result(property = "createTime",column = "create_time")
	})
	@Select("select * from cfg_resource where id = #{id}")
	public CfgResource selectById(@Param("id")long id);
	@ResultMap("cfgResourceMapper")
	@Select("select * from cfg_resource where cfg_key = #{key} limit 1")
	public CfgResource selectByKey(@Param("key")String key);
	
	@ResultMap("cfgResourceMapper")
	@Select("select * from cfg_resource where cfg_key like 'audio_bilibiliG_%' escape 'G' and  title like concat('%',concat(#{key},'%')) ")
	public List<CfgResource> findByKey(@Param("preffix")String preffix ,@Param("key")String key);
	
	@Insert("insert into cfg_resource(cfg_key,cfg_resource,resource_type,title,content,image,origin_resource,create_user_id,create_user_name,create_time) "
			+ " values(#{cfgKey},#{cfgResource},#{resourceType},#{title},#{content},#{image},#{originResource},#{createUserId},#{createUserName},#{createTime})")
	@Options(useGeneratedKeys = true,keyProperty = "id")
	public long insert(CfgResource cr);
	@ResultMap("cfgResourceMapper")
	@Select("select * from cfg_resource where cfg_key like 'audio_bilibiliG_%' escape 'G' limit #{offset},#{pageSize} ")
	public List<CfgResource> findBMusicLimit(int offset,int pageSize);
	@Select("select count(1) from cfg_resource where cfg_key like 'audio_bilibiliG_%' escape 'G' ")
	public int countBMusic();
	
	@ResultMap("cfgResourceMapper")
	@Select("select * from cfg_resource where cfg_key = #{cfgKey} and resource_type = 'image' ")
	public List<CfgResource> findCollectByKey(@Param("cfgKey")String cfgKey);
	
	@ResultMap("cfgResourceMapper")
	@Select("select * from cfg_resource where cfg_key = #{cfgKey} and resource_type = 'image' order by rand() limit 1")
	public CfgResource selectRandomCollectByKey(@Param("cfgKey")String cfgKey);
	
}
