package org.accen.dmzj.core.handler.cmd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.accen.dmzj.core.annotation.AutowiredParam;
import org.accen.dmzj.core.annotation.CmdMessage;
import org.accen.dmzj.core.api.HitokotoApiClient;
import org.accen.dmzj.core.meta.MessageSubType;
import org.accen.dmzj.core.meta.MessageType;
import org.accen.dmzj.util.CQUtil;
import org.accen.dmzj.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@EnableConfigurationProperties(HitokotoContributeConfiguration.class)
public class HitokotoContribute {
	private HitokotoContributeConfiguration config;
	private ObjectMapper json;
	private Path localFilePath;
	public static final String SOURCE_KEY = "_SOURCE";
	@Autowired
	private HitokotoApiClient hitokotoApiClient;
	public HitokotoContribute(HitokotoContributeConfiguration config) {
		json = new ObjectMapper();
		this.config = config;
		localFilePath = new File(config.localPath()).toPath();
		if(!Files.exists(localFilePath)) {
			try {
				Files.createFile(localFilePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	@CmdMessage(value = "hitokoto-contri",messageType = MessageType.GROUP,subType = MessageSubType.NORMAL)
	public void contribute(@AutowiredParam String message,@AutowiredParam(".sender.nickname") String nickname,@AutowiredParam long groupId) {
		String fmtMessage = message.replaceAll(System.lineSeparator(), "").trim();
		if(fmtMessage.length()>=10&&!CQUtil.hasCq(message)&&RandomUtil.randomPass(config.contributeRatio())) {
			//加一条hitokoto
			Map<String, Object> aHitoko = Map.of("uuid",UUID.randomUUID().toString()
					,"hitokoto",message.replaceAll(System.lineSeparator(), "").trim()
					,"from",groupId
					,"from_who",nickname
					,"length",fmtMessage.length());
			try {
				Files.writeString(localFilePath,System.lineSeparator()+json.writeValueAsString(aHitoko),StandardOpenOption.APPEND);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 获取一言
	 * @return
	 */
	public Map<String,Object> catchHitokoto(){
		if(RandomUtil.randomPass(config.usageRatio())) {
			Map<String,Object> r = catchHitokotoFromLocal();
			if(r!=null) {
				return r;
			}
		}
		return catchHitokotoFromApi();
	}
	/**
	 * 从hitokoto api获取
	 * @return
	 */
	private Map<String,Object> catchHitokotoFromApi(){
		try{
			Map<String,Object> r = hitokotoApiClient.hitokoto();
			r.put(SOURCE_KEY, "api");
			return r;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	private Map<String, Object> catchHitokotoFromLocal(){
		List<String> lines;
		try {
			lines = Files.readAllLines(localFilePath);
			if(lines==null||lines.size()==0) {
				return null;
			}else {
				Map<String, Object> r =  json.readValue(lines.get(RandomUtil.randomInt(lines.size()))
														, Map.class);
				r.put(SOURCE_KEY, "local");
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
		
	}
	
	
}
@ConfigurationProperties("hitokoto")
@ConstructorBinding
record HitokotoContributeConfiguration(@DefaultValue("local-hitokoto.txt")String localPath
										,@DefaultValue("0.3")float contributeRatio
										,@DefaultValue("0.1")float usageRatio) {
}
