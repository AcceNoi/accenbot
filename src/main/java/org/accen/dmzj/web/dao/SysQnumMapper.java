package org.accen.dmzj.web.dao;

import java.util.List;

import org.accen.dmzj.web.vo.SysQnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysQnumMapper {
	@Results(id="qnumResultMap",value= {
			@Result(property = "qnum",column = "qnum"),
			@Result(property = "secret",column = "secret")
	})
	@Select("select * from sys_qnum where qnum = #{qnum}")
	public SysQnum getByQnum(@Param("qnum") String qnum);
	
	@ResultMap("qnumResultMap")
	@Select("select * from sys_qnum ")
	public List<SysQnum> getAllQnum();
}
