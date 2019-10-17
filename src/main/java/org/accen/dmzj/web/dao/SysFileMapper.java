package org.accen.dmzj.web.dao;

import org.accen.dmzj.web.vo.SysFile;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

public interface SysFileMapper {
	@Results(id = "sysFileMapper",value = {
		@Result(property = "id",column = "id"),
		@Result(property = "fileName",column = "file_name"),
		@Result(property = "fileType",column = "file_type"),
		@Result(property = "sha",column = "sha"),
		@Result(property = "ftpPath",column = "ftp_path"),
		@Result(property = "createTime",column = "create_time"),
		@Result(property = "createUserId",column = "create_user_id")
	})
	@Select("select * from sys_file where id = #{id}")
	public SysFile selectById(@Param("id")String id);
	
	@Insert("insert into sys_file(id,file_name,file_type,sha,ftp_path,create_time,create_user_id) "
			+ "values (#{id},#{fileName},#{fileType},#{sha},#{ftpPath},#{createTime},#{createUserId}) ")
	public long insert(SysFile file);
	
}
