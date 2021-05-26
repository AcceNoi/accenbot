<div align="center">
<img width="250" src="https://raw.githubusercontent.com/AcceNoi/accenbot/master/accenbot-logo-1.png" alt="logo">
<h2 id="quick-start"><a href="https://github.com/AcceNoi/accenbot/blob/master/README-QUICKSTART.md">Accenbot Framework</a></h2>
</div>

**基于[Onebot](https://github.com/howmanybots/onebot)和[onebot-kotlin](https://github.com/yyuueexxiinngg/onebot-kotlin)实现的QQ聊天机器人极简框架。**
---

[![License](https://img.shields.io/github/license/AcceNoi/dmzjbot)](https://img.shields.io/github/license/AcceNoi/dmzjbot) [![Size](https://img.shields.io/github/repo-size/AcceNoi/dmzjbot)](https://img.shields.io/github/repo-size/AcceNoi/dmzjbot)[![OneBot v11](https://img.shields.io/badge/OneBot-v11-black)](https://github.com/howmanybots/onebot/blob/master/v11/specs/README.md)

| Author | Accen/クロノス    |
| ------ | ----------------- |
| Email  | 1339liu@gmail.com |

---

##  <font color="red">注意</font>
>本项目依赖[onebot-kotlin](https://github.com/yyuueexxiinngg/onebot-kotlin)
>
>本项目使用了preview特性，请确保使用JDK15+（可能会持续到17发布）进行编译和运行，并添加--enable-preview参数。
>
>当前最新版本[V2.1-Tempest](https://github.com/AcceNoi/dmzjbot/releases/tag/V2.1-Tempest)，整理出第一版Accenbot极简配置框架。
>

### V2.0+待填的坑
- [x] 支持解析分P的B站视频 feat [0742](https://github.com/AcceNoi/accenbot/commit/0742012a269be069031d8c98dec8387a2f6b3e5e)
- [ ] 集成ffmpeg（当前是外挂）
- [ ] 整理各个功能的配置（基于Springboot-configuration-processor2.4+）
- [ ] 隔离每个功能的工作空间
- [x] 集成onebot-kotlin feat [onebot-kotlin-embedded](https://github.com/AcceNoi/accenbot/tree/onebot-kotlin-embedded)

## [Quick Start](https://github.com/AcceNoi/accenbot/blob/master/README-QUICKSTART.md)

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

### 3.极简配置框架Accenbot

除了2种描述的极简正则匹配用于最常用的文本匹配回复，框架将符合Onebot标准Event全部采用极简化配置的方式，同样配合@AutowiredParam和@GeneralMessage，实现功能的快速编写。

***其核心理念是将方法Method代理，将上报的Event的参数注入到该Method的参数中，执行后，若其符合发送消息的格式，则自动封装成消息发出。实现消息来源与反馈的去耦合。***

**@AutowiredParam**：自动注入Event参数，用于标识该Parameter。例如：你可以使用```.```来注入Event这个对象，或者```.post_type```来注入Event的post_type参数，或者```.sender.nickname```注入nickname参数（当然前提是存在这个参数）。另外，AutowiredParam允许你使用驼峰命名的风格就像```.postType```，但是我并不希望你这样做，最好是保持与Onebot协议中描述的一致；此外，AutowiredParam允许你以参数签名的方式来定义第一层的参数，例如@AutowiredParam String postType。

```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutowiredParam {
	String value() default "";
}
```

**@CmdMessage、@CmdMeta、@CmdNotice、@CmdRequest**：用来定义一个代理（我称之为AccenbotCmdProxy），它们分别匹配Onebot中的四种Event类型。它们可以被标识在Class或者Method上，标识在类上时，executeMethod将起作用，它会将类中的这些方法（默认为execute）注册为AccenbotCmdProxy。而标识在方法上，则为此方法。value参数为该AccenbotCmdProxy的唯一标识，请确保此value为唯一的，否则将抛出CmdRegisterDuplicateException异常，或者使用默认值，框架会为你生成一个唯一的value（但是这很蠢，如果你希望在运行时注销一个AccenbotCmdProxy，建议你显式地为它命名）。

```java
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CmdMessage {
	String value() default "";
	String[] executeMethod() default {"execute"};
	int order() default 999;
	MessageType[] messageType() default MessageType._ALL;
	MessageSubType[] subType() default MessageSubType._ALL;
}
```

除了上面四种，@CmdRegular（也就是第2点描述的）也是相同的原理，只是因为常用（现在实现的80%功能都是属于这种类型）所以单列出来了。

## [已实现的功能](https://github.com/AcceNoi/accenbot/blob/master/README-FUNCTION.md)

## 感谢

  - ~~感谢酷Q项目，各位有兴趣可移步[酷Q社区]( https://cqp.cc/ )~~
  - 感谢大佬[richardchien](https://github.com/richardchien)的**[coolq-http-api](https://github.com/richardchien/coolq-http-api)**项目，提供了一个第三方公共调用的接口
  - 感觉大佬[Henryhaohao](https://github.com/Henryhaohao)的**[Bilibili_video_download](https://github.com/Henryhaohao/Bilibili_video_download)**项目的B站接口调用指导
  - 感谢**[cqhttp mirai](https://github.com/yyuueexxiinngg/cqhttp-mirai)**项目基本保持cqhttp风格，保证了项目的迁移

