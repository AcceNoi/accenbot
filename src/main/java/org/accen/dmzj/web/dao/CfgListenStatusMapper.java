package org.accen.dmzj.web.dao;

import java.util.List;

import org.accen.dmzj.web.vo.CfgListenStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CfgListenStatusMapper {
	@Results(id = "cfgListenStatusResultMap",value = {
			@Result(property = "id",column = "id"),
			@Result(property = "applyType",column = "apply_type"),
			@Result(property = "applyTarget",column = "apply_target"),
			@Result(property = "listenerCode",column = "listener_code"),
			@Result(property = "listenerStatus",column = "listener_status"),
			@Result(property = "listenerStatus2",column = "listener_status2"),
			@Result(property = "createUserId",column = "create_user_id"),
			@Result(property = "createTime",column = "create_time"),
			@Result(property = "updateUserId",column = "update_user_id"),
			@Result(property = "updateTime",column = "update_time")
	})
	@Select("select * from cfg_listen_status where id = #{id}")
	public CfgListenStatus selectById(@Param("id") long id);
	
	@ResultMap("cfgListenStatusResultMap")
	@Select("select * from cfg_listen_status where apply_type = #{applyType} and apply_target=#{applyTarget}")
	public List<CfgListenStatus> findByApply(@Param("applyTarget") String applyTarget,@Param("applyType") String applyType);
	
	@ResultMap("cfgListenStatusResultMap")
	@Select("select * from cfg_listen_status where apply_type = #{applyType} and apply_target=#{applyTarget} and listener_code = #{listenerCode}")
	public CfgListenStatus selectByApplyAndCode(@Param("applyTarget") String applyTarget,@Param("applyType") String applyType,@Param("listenerCode") String listenerCode);
	
}
