package org.accen.dmzj.web.dao;

import java.util.List;

import org.accen.dmzj.web.vo.CmdBuSub;
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
public interface CmdBuSubMapper {
	@Results(id="cmdBuSubMapper",value= {
			@Result(property = "id",column = "id"),
			@Result(property = "type",column = "type"),
			@Result(property = "targetId",column = "target_id"),
			@Result(property = "subscriber",column = "subscriber"),
			@Result(property = "subTarget",column = "sub_target"),
			@Result(property = "subType",column = "sub_type"),
			@Result(property = "subObj",column = "sub_obj"),
			@Result(property = "subObjMark",column = "sub_obj_mark"),
			@Result(property = "subTime",column = "sub_time"),
			@Result(property = "status",column = "status"),
			@Result(property = "attr1",column = "attr1"),
			@Result(property = "attr2",column = "attr2")
	})
	@Select("select * from cmd_bu_sub where id = #{id}")
	public CmdBuSub selectById(@Param("id")long id);
	
	@ResultMap("cmdBuSubMapper")
	@Select("select * from cmd_bu_sub where type=#{type} and sub_target = #{subTarget} and sub_type=#{subType} and status = 1")
	public List<CmdBuSub> findBySubType(@Param("type")String type,@Param("subTarget") String subTarget,@Param("subType") String subType);	
	@ResultMap("cmdBuSubMapper")
	@Select("select * from cmd_bu_sub where type=#{type} and target_id=#{targetId} and subscriber=#{subscriber} and status = 1")
	public List<CmdBuSub> findBySubscriber(@Param("type")String type,@Param("targetId")String targetId,@Param("subscriber")String subscriber);
	
	@ResultMap("cmdBuSubMapper")
	@Select("select * from cmd_bu_sub where type=#{type} and target_id=#{targetId} and subscriber=#{subscriber} and sub_target=#{subTarget} and sub_type=#{subType} and sub_obj = #{subObj} and status = 1")
	public List<CmdBuSub> findBySubscriberAndObj(@Param("type")String type,@Param("targetId")String targetId,@Param("subscriber")String subscriber,@Param("subTarget")String subTarget, @Param("subType")String subType, @Param("subObj")String subObj);
	
	@ResultMap("cmdBuSubMapper")
	@Select("select * from cmd_bu_sub where type=#{type} and target_id=#{targetId} and subscriber=#{subscriber} and sub_target=#{subTarget} and sub_type=#{subType} and sub_obj_mark = #{subObjMark} and status = 1")
	public List<CmdBuSub> findBySubscriberAndObjMark(@Param("type")String type,@Param("targetId")String targetId,@Param("subscriber")String subscriber,@Param("subTarget")String subTarget, @Param("subType")String subType,@Param("subObjMark")String subObjMark);
	
	@Insert("insert into cmd_bu_sub(type,target_id,subscriber,sub_target,sub_type,sub_obj,sub_obj_mark,sub_time,status,attr1,attr2) values"
			+ " (#{type},#{targetId},#{subscriber},#{subTarget},#{subType},#{subObj},#{subObjMark},#{subTime},#{status},#{attr1},#{attr2}) ")
	@Options(useGeneratedKeys = true,keyProperty = "id")
	public long insert(CmdBuSub sub);
	
	@Update("update cmd_bu_sub set status = 2 where id = #{id} ")
	public void deleteById(@Param("id")long id);
	
	@Update("update cmd_bu_sub set attr1 = #{attr1} where id = #{id}")
	public void updateRoomStatus(@Param("attr1") String attr1,@Param("id")long id);
	
	@Update("update cmd_bu_sub set attr1 = #{attr1} where attr1 like #{roomId}||'#%' and status =1 ")
	public void updateRoomStatusByRoomId(@Param("attr1") String attr1,@Param("roomId")String roomId);
}
