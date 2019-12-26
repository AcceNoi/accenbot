package org.accen.dmzj.web.dao;

import java.util.List;

import org.accen.dmzj.web.vo.SysGroupMember;
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
public interface SysGroupMemberMapper {
	@Results(id = "sysGroupMemberMapper",value={
		@Result(property = "id",column = "id"),
		@Result(property = "type",column = "type"),
		@Result(property = "targetId",column = "target_id"),
		@Result(property = "userId",column = "user_id"),
		@Result(property = "coin",column = "coin"),
		@Result(property = "checkinCount",column = "checkin_count"),
		@Result(property = "favorability",column = "favorability"),
		@Result(property = "createTime",column = "create_time"),
		@Result(property = "lastCheckinTime",column = "last_checkin_time"),
		@Result(property = "status",column = "status"),
		@Result(property = "repeatCount",column = "repeat_count"),
		@Result(property = "cardTicket",column = "card_ticket"),
		@Result(property = "remark",column = "remark")
	}) 
	@Select("select * from sys_group_member where id = #{id}")
	public SysGroupMember selectById(@Param("id")long id);
	
	@Insert("insert into sys_group_member(type,target_id,user_id,coin,checkin_count,favorability,create_time,last_checkin_time,status,repeat_count,remark)"
			+ "values(#{type},#{targetId},#{userId},#{coin},#{checkinCount},#{favorability},#{createTime},#{lastCheckinTime},#{status},#{repeatCount},#{remark}) ")
	@Options(useGeneratedKeys = true,keyProperty = "id")
	public long insert(SysGroupMember member);
	
	@Select("select * from sys_group_member where type=#{type} and target_id = #{targetId} and user_id = #{userId} and status = 1")
	@ResultMap("sysGroupMemberMapper")
	public List<SysGroupMember> selectByTarget(@Param("type")String type,@Param("targetId")String targetId,@Param("userId")String userId);
	
	@Update("update sys_group_member set coin = #{coin}"
			+ ",checkin_count=#{checkinCount}"
			+ ",favorability=#{favorability}"
			+ ",last_checkin_time=#{lastCheckinTime}"
			+ ",repeat_count=#{repeatCount}"
			+ ",card_ticket=#{cardTicket}"
			+ ",remark=#{remark} where id = #{id}")
	public long updateCheckin(SysGroupMember member);
	@Update("update sys_group_member set coin = #{coin} where type = #{type} and target_id = #{targetId} and user_id = #{userId} and status = 1")
	public long updateCoinByTarget(@Param("coin")int coin,@Param("type")String type,@Param("targetId")String targetId,@Param("userId")String userId);
	
	@Update("update sys_group_member set favorability = #{favorability} where type = #{type} and target_id = #{targetId} and user_id = #{userId} and status = 1")
	public long updateFavByTarget(@Param("favorability")int favorability,@Param("type")String type,@Param("targetId")String targetId,@Param("userId")String userId);

	@Update("update sys_group_member set repeat_count = #{repeatCount} where type = #{type} and target_id = #{targetId} and user_id = #{userId} and status = 1")
	public int updateRepeatByTarget(@Param("repeatCount")int repeatCount,@Param("type")String type,@Param("targetId")String targetId,@Param("userId")String userId);
}
