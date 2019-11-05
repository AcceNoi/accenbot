package org.accen.dmzj.util;

import java.util.List;

import org.accen.dmzj.web.vo.CmdTips;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CmdTipsMapper {
	@Results(id = "cmdTipsMapper",value = {
			@Result(property = "id",column = "id"),
			@Result(property = "moduleCode",column = "module_code"),
			@Result(property = "moduleName",column = "module_name"),
			@Result(property = "cmdFunc",column = "cmd_func"),
			@Result(property = "content",column = "content"),
			@Result(property = "status",column = "status")
	})
	@Select("select * from cmd_tips where id = #{id}")
	public CmdTips selectById(@Param("id")long id);
	
	@ResultMap("cmdTipsMapper")
	@Select("select * from cmd_tips where module_code = #{moduleCode} and status = 1 ")
	public List<CmdTips> findByModule(@Param("moduleCode")String moduleCode);
	
	@ResultMap("cmdTipsMapper")
	@Select("select * from cmd_tips where module_code = #{moduleCode} and status = 1 order by random() limit 1 ")
	public CmdTips selectByModuleRandom(@Param("moduleCode")String moduleCode);
	
	@ResultMap("cmdTipsMapper")
	@Select("select * from cmd_tips where cmd_func = #{cmdFunc} and status = 1 ")
	public CmdTips selectByCmdFunc(@Param("cmdFunc")String cmdFunc);
}
