package org.accen.dmzj.web.dao;

import java.util.List;

import org.accen.dmzj.web.vo.CmdGame;
import org.accen.dmzj.web.vo.CmdGameNode;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

public interface CmdGameMapper {
	@Results(id = "cmdGameMapper",value = {
			@Result(property = "id",column = "id"),
			@Result(property = "gameName",column = "game_name"),
			@Result(property = "coinConsum",column = "coin_consum"),
			@Result(property = "favLimit",column = "fav_limit"),
			@Result(property = "status",column = "status")
	})
	@Select("select * from cmd_game where id = #{id}")
	public CmdGame selectGameById(@Param("id")long id);
	
	@Results(id = "cmdGameNodeMapper",value = {
			@Result(property = "id",column = "id"),
			@Result(property = "gameName",column = "game_name"),
			@Result(property = "coinConsum",column = "coin_consum"),
			@Result(property = "favLimit",column = "fav_limit"),
			@Result(property = "status",column = "status")
	})
	@Select("select * from cmd_game_node where id = #{id}")
	public CmdGameNode selectGameNodeById(@Param("id")long id);
	
	@ResultMap("cmdGameNodeMapper")
	@Select("select * from cmd_game_node where game_id = #{gameId} and ")
	public CmdGameNode findStartNodeByGame(@Param("gameId")long gameId);
}
