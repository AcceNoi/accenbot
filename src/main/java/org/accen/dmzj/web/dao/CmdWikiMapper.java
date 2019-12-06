package org.accen.dmzj.web.dao;

import java.util.List;

import org.accen.dmzj.web.vo.CmdWiki;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import feign.Param;

public interface CmdWikiMapper {
	@Results(id = "cmdWikiMapper",value = {
			@Result(property = "id",column = "id"),
			@Result(property = "article",column = "article"),
			@Result(property = "wikiName",column = "wiki_name"),
			@Result(property = "keywords",column = "keywords"),
			@Result(property = "content",column = "content"),
			@Result(property = "image",column = "image"),
			@Result(property = "status",column = "status")
	})
	public CmdWiki selectById(@Param("id")long id);
	
	@ResultMap("cmdWikiMapper")
	@Select("select * from cmd_wiki where wiki_name = #{name} and status = 3 ")
	public CmdWiki selectByName(@Param("name")String wikiName);
	
	@ResultMap("cmdWikiMapper")
	@Select("select * from cmd_wiki where keywords like concat('%',concat(#{keyword},'%')) and status = 3 ")
	public List<CmdWiki> findByKeyword(String keyword);
}
