package org.accen.dmzj.web.dao;

import java.util.List;

import org.accen.dmzj.web.vo.SysRecordCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysRecordCountMapper {
	@Results(id = "sysRecordCountResultMap",value = {
			@Result(property = "recordType",column = "record_type"),
			@Result(property = "recordTarget",column = "record_target"),
			@Result(property = "recordValue",column = "record_value"),
			@Result(property = "attr1",column = "attr1"),
			@Result(property = "attr2",column = "attr2"),
			@Result(property = "createTime",column = "create_time"),
			@Result(property = "createUserId",column = "create_user_id"),
			@Result(property = "updateTime",column = "update_time"),
			@Result(property = "updateUserId",column = "update_user_id")
	})
	@Select("select * from sys_record_count where id = #{id}")
	public SysRecordCount selectById(@Param("id") long id);
	@ResultMap("sysRecordCountResultMap")
	@Select("select * from sys_record_count where record_target = #{recordTarget}")
	public List<SysRecordCount> findByTarget(@Param("recordTarget") String recordTarget);
	@ResultMap("sysRecordCountResultMap")
	@Select("select * from sys_record_count where record_type = #{record_type}")
	public List<SysRecordCount> findByType(@Param("recordType") String recordType);
}
