package org.accen.dmzj.web.dao;

import org.accen.dmzj.web.vo.Qmessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface QmessageMapper {
	@Results(id="qmessageResultMap",value= {
			@Result(property = "id",column = "id"),
			@Result(property = "messageType",column = "message_type"),
			@Result(property = "subType",column = "sub_type"),
			@Result(property = "messageId",column = "message_id"),
			@Result(property = "groupId",column = "group_id"),
			@Result(property = "userId",column = "user_id"),
			@Result(property = "message",column = "message"),
			@Result(property = "rawMessage",column = "raw_message"),
			@Result(property = "sendTime",column = "send_time"),
			@Result(property = "font",column = "font")
	})
	@Select("select * from qmessage where id = #{id}")
	public Qmessage selectById(@Param("id") long id);
	@Insert("insert into qmessage(message_type,sub_type,message_id,group_id,user_id,message,raw_message,send_time,font)"
			+ " values(#{messageType},#{subType},#{messageId},#{groupId},#{userId},#{message},#{rawMessage},#{sendTime},#{font})")
	@Options(useGeneratedKeys = true,keyProperty = "id")
	public long insert(Qmessage qmessage);
}
