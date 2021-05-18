**基于Mirai和[CqHTTP Mirai](https://github.com/yyuueexxiinngg/cqhttp-mirai)实现的QQ群聊天机器人。**
---

[![License](https://img.shields.io/github/license/AcceNoi/dmzjbot)](https://img.shields.io/github/license/AcceNoi/dmzjbot) [![Size](https://img.shields.io/github/repo-size/AcceNoi/dmzjbot)](https://img.shields.io/github/repo-size/AcceNoi/dmzjbot)

| Author | Accen/クロノス    |
| ------ | ----------------- |
| Email  | 1339liu@gmail.com |

---

##  <font color="red">注意</font>
>本项目依赖[onebot-kotlin](https://github.com/yyuueexxiinngg/onebot-kotlin)
>
>本项目使用了preview特性，请确保使用JDK15+（可能会持续到17发布）进行编译和运行，并添加--enable-preview参数。
>
>当前最新版本[V2.0-Agito](https://github.com/AcceNoi/dmzjbot/releases/tag/V2.0-Agito)，新增了极简正则匹配、自动注入等功能。
>

### V2.0+待填的坑
- 支持解析分P的B站视频
- 集成ffmpeg（当前是外挂）
- 整理各个功能的配置（基于Springboot-configuration-processor2.4+）
- 隔离每个功能的工作空间
- 集成[onebot-kotlin](https://github.com/yyuueexxiinngg/onebot-kotlin)

## 模块介绍

### 1. 菜单生成器

将FuncSwitch配置在相应的类上，可以自动生成菜单，详见[FuncSwitch](https://github.com/AcceNoi/accenbot/blob/master/src/main/java/org/accen/dmzj/core/annotation/FuncSwitch.java)、[FuncSwitchGroup](https://github.com/AcceNoi/accenbot/blob/master/src/main/java/org/accen/dmzj/core/annotation/FuncSwitchGroup.java)、[CmdShower](https://github.com/AcceNoi/accenbot/blob/master/src/main/java/org/accen/dmzj/core/handler/CmdShower.java)(核心)

```java
/**
 * 配置在Cmd上面，标识其对应的功能点
 * @author <a href="1339liu@gmail.com">Accen</a>
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FuncSwitch {
	/**
	 * 功能key,默认cmd_类名
	 * @return
	 */
	String name() default "";
	/**
	 * 是否要在菜单中展示出来
	 * @return
	 */
	boolean showMenu() default false;
	/**
	 * 功能名
	 * @return
	 */
	String title();
	/**
	 * 排序
	 * @return
	 */
	int order() default 99;
	/**
	 * 格式
	 * @return
	 */
	String format() default "";
	/**
	 * 所属分组
	 * @return
	 */
	Class<?> groupClass() default Default.class;
}
```

### 2. 极简正则匹配

使用@CmdRegular和@GeneralMessage，可以快速、无侵入、去耦合地实现一个匹配型的功能，详见[Demo](https://github.com/AcceNoi/accenbot/blob/master/src/test/java/dmzjbot/Demo.java)、[GeneralMessage](https://github.com/AcceNoi/accenbot/blob/master/src/main/java/org/accen/dmzj/core/annotation/GeneralMessage.java)、[CmdRegular](https://github.com/AcceNoi/accenbot/blob/master/src/main/java/org/accen/dmzj/core/annotation/CmdRegular.java)、[ AutowiredParam](https://github.com/AcceNoi/accenbot/blob/master/src/main/java/org/accen/dmzj/core/annotation/AutowiredParam.java)、[AutowiredRegular](https://github.com/AcceNoi/accenbot/blob/master/src/main/java/org/accen/dmzj/core/annotation/AutowiredRegular.java)、[CmdRegularManager](https://github.com/AcceNoi/accenbot/blob/master/src/main/java/org/accen/dmzj/core/handler/CmdRegularManager.java)(核心)

```java
@Component
public class Demo {
	@CmdRegular(expression = "^检索(.+)$",enableAutowiredParam = false)
	@GeneralMessage
	public String search(String key) {
		//TODO your code
		return "检索结果...";
	}
	
	@CmdRegular(name="engine",expression = "^用(.+)引擎检索(\\d+)$")
	@GeneralMessage(targetId = "123456")
	public String search(Qmessage qmassage
						,@AutowiredParam("message") String msg
						,@AutowiredParam Date sendTime
						,int pid
						,@AutowiredRegular(1) String engine) {
		//TODO your code
		return "检索结果...";
	}
}
```

### 3.消息处理链

[EventHandler](https://github.com/AcceNoi/accenbot/blob/master/src/main/java/org/accen/dmzj/core/handler/EventHandler.java)重构中...

## [已实现的功能](https://github.com/AcceNoi/accenbot/blob/master/README-FUNCTION.md)

## 感谢

  - ~~感谢酷Q项目，各位有兴趣可移步[酷Q社区]( https://cqp.cc/ )~~
  - 感谢大佬[richardchien](https://github.com/richardchien)的**[coolq-http-api](https://github.com/richardchien/coolq-http-api)**项目，提供了一个第三方公共调用的接口
  - 感觉大佬[Henryhaohao](https://github.com/Henryhaohao)的**[Bilibili_video_download](https://github.com/Henryhaohao/Bilibili_video_download)**项目的B站接口调用指导
  - 感谢**[cqhttp mirai](https://github.com/yyuueexxiinngg/cqhttp-mirai)**项目基本保持cqhttp风格，保证了项目的迁移

