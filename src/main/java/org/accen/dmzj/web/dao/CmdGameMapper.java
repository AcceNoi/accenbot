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
	@ResultMap("cmdGameMapper")
	@Select("select * from cmd_game where game_name = #{gameName} and status = 1 ")
	public CmdGame selectGameByName(@Param("gameName")String gameName);
	@ResultMap("cmdGameMapper")
	@Select("select * from cmd_game where status = 1 ")
	public List<CmdGame> findAllGame();
	
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
	@Select("select * from cmd_game_node where game_id = #{gameId} and node_type = 'First'}")
	public CmdGameNode findFirstNodeByGame(@Param("gameId")long gameId);
	@ResultMap("cmdGameNodeMapper")
	@Select("select cgn.* from cmd_game_node cgn left join cmd_node_relation cnr on cnr.c_node_id = cgn.id "
			+ " where cnr.p_node_id = #{parentId} ")
	public List<CmdGameNode> findNextNode(@Param("parentId")long parentId);
	@ResultMap("cmdGameNodeMapper")
	@Select("select cgn.* from cmd_game_node cgn left join cmd_node_relation cnr on cnr.c_node_id = cgn.id "
			+ " where cnr.p_node_id = #{parentId} and cnr.check_no = #{checkNo}")
	public CmdGameNode selectNextNodeByNo(@Param("parentId")long parentId,String checkNo);
}
