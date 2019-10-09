package org.accen.dmzj.web.dao;

import java.util.List;

import org.accen.dmzj.web.vo.CfgQuickReply;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CfgQuickReplyMapper {
	@Results(id = "cfgQuickReplyResultMapper",value = {
			@Result(property = "id",column = "id"),
			@Result(property = "matchType",column = "match_type"),
			@Result(property = "pattern",column = "pattern"),
			@Result(property = "applyType",column = "apply_type"),
			@Result(property = "applyTarget",column = "apply_target"),
			@Result(property = "needAt",column = "need_at"),
			@Result(property = "reply",column = "reply"),
			@Result(property = "createUserId",column = "create_user_id"),
			@Result(property = "createTime",column = "create_time"),
			@Result(property = "status",column = "status")
	})
	@Select("select * from cfg_quick_relpy where id = #{id}")
	public CfgQuickReply selectById(@Param("id") long id);
	
	@ResultMap("cfgQuickReplyResultMapper")
	@Select("select * from cfg_quick_reply where status = 1 and apply_type=#{applyType} and apply_target=#{applyTarget} order by create_time desc")
	public List<CfgQuickReply> queryByApply(@Param("applyType") int applyType,@Param("applyTarget") String applyTarget);
	
	@Insert("insert into cfg_quick_reply(match_type,pattern,apply_type,apply_target,need_at,reply,create_user_id,createTime,status) "
			+ " values(#{matchType},#{pattern},#{applyType},#{applyTarget},#{needAt},#{reply},#{createUserId},#{status})") 
	@Options(useGeneratedKeys = true,keyProperty = "id")
	public long insert(CfgQuickReply cfgQuickReply);
}
