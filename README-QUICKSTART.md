**基于[Onebot](https://github.com/howmanybots/onebot)和[onebot-kotlin](https://github.com/yyuueexxiinngg/onebot-kotlin)实现的QQ聊天机器人极简框架。**
---

[![License](https://img.shields.io/github/license/AcceNoi/dmzjbot)](https://img.shields.io/github/license/AcceNoi/dmzjbot) [![Size](https://img.shields.io/github/repo-size/AcceNoi/dmzjbot)](https://img.shields.io/github/repo-size/AcceNoi/dmzjbot)

| Author | Accen/クロノス    |
| ------ | ----------------- |
| Email  | 1339liu@gmail.com |

## Quick Start

### 1.Clone this Project

```shell
git clone https://github.com/AcceNoi/accenbot.git yourproject
```

### 2.Import to your IDE like Eclipse or IDEA

### 3.Menu & Menu Group

> ```org.accen.dmzj.core.handler.cmd```这个包下为本项目此前已写好的功能，你可以选择保留或者删除。你可以看到这些实现了[CmdAdapter](https://github.com/AcceNoi/accenbot/blob/master/src/main/java/org/accen/dmzj/core/handler/cmd/CmdAdapter.java)接口的若干个类（这是老式的功能编写方式，后续可能会移除），例如实现了P站榜单功能的[PixivRank](https://github.com/AcceNoi/accenbot/blob/master/src/main/java/org/accen/dmzj/core/handler/cmd/PixivRank.java)，或者实现了菜单展示功能的[Shower](https://github.com/AcceNoi/accenbot/blob/master/src/main/java/org/accen/dmzj/core/handler/cmd/Shower.java)，这些接口有些是被注解了@FuncSwitch，标识该功能将作为菜单展示，然而需要注意的是，菜单与功能实际并不是绑定的，@FuncSwitch标识了一个菜单，而[实现功能有很多方式](#user-content-5coding-with-accenbot-framework )
```java
@FuncSwitch(groupClass = Image.class,showMenu = true, title = "p站榜单",format = "p站本月榜[1-9]")
```
> groupClass标识它所属的菜单组，showMenu标识是否需要显示（默认为否），title标识菜单名字，format标识菜单的提示格式，order为排序。

>
> ```org.accen.dmzj.core.handler.group```这个包下为本项目此前已写好的功能组，可以选择保留或者删除（除了[```Default```](https://github.com/AcceNoi/accenbot/blob/master/src/main/java/org/accen/dmzj/core/handler/group/Default.java)，也可以编写自己的功能组，像这样：

```java
@FuncSwitchGroup(title = "音乐",
				showMenu = true,
				order = 3)
public class Audio {

}
```

> title标识功能/菜单组的名字，showMenu标识是否需要显示（默认为否），order标识它的展示顺序，matchSigns标识匹配到这个菜单的文本，默认是title。
>
> 

### 4.Configuration

> Accenbot的配置见application.yml

```yaml
cq: 
  push-url: http://localhost:5700 #对应onebot中http模块
  token: XXX #对应onebot中auth.access_token
  bot-id: 123456 #对应当前bot的qq（当前版本暂不支持多qq）
  admin-id: 123456,654321 #标识管理员的qq，此管理员的权限未来将高于group role为owner、admin的权限
pixivc: #pixivc功能模块，不需要可以去除
  login-retry: 3 #重试次数
  account: XXX #pixivc账号
  password: 123456 #pixivc密码
  #authLocation: pixivc.auth #本地存储的auth文件，默认pixivc.auth
  #vcUrl: https://pix.ipv4.host/verificationCode #verificationCode获取链接
  #authHolderClass: org.accen.dmzj.core.task.api.pixivc.LocalAuthHolder
  #authFresherClass: org.accen.dmzj.core.task.api.pixivc.HttpAuthFresher
```

### 5.Coding with Accenbot framework

> 按照Onebot标准中对[Event的定义](https://github.com/howmanybots/onebot/blob/master/v11/specs/event/README.md)，你可以针对：
>
> - 消息事件，包括私聊消息、群消息等
> - 通知事件，包括群成员变动、好友变动等
> - 请求事件，包括加群请求、加好友请求等
> - 元事件，包括 OneBot 生命周期、心跳等
>
> 编写相应的反馈代码，CmdMessage，CmdNotice，CmdRequest，CmdMeta，它们可以标识在类或者方法上。
>
> #### 5.1 @CmdMessage，标识这是一个面向消息事件的功能
>
> ```java
> @CmdMessage(value="message",messageType = MessageType.GROUP)
> 	@GeneralMessage
> 	public String message(@AutowiredParam(".sender.nickname")String nickname) {
> 		return nickname;
> 	}
> ```
>
> 例如这个message方法，它被@CmdMessage标识为面向“消息事件”的一个功能，可以看到它通过messageType限制了只对group（也就是群聊）消息做反馈，当然你可以使用messageType={MessageType.GROUP,MessageType.PRIVATE}或者messageType=MessageType._ALL来标识它对群聊和私聊消息都反馈。
>
> 它通过@AutowiredParam注入了nickname这个参数，你还可以注入更多的参数甚至event根对象，参考，同时通过@GeneralMessage将方法的返回值定义为一个反馈任务，它将在来源群里面回复nickname。
>
> #### 5.2 @CmdNotice，标识这是一个面向通知事件的功能
>
> ```java
> @CmdNotice(value="recall",noticeType = NoticeType.GROUP_RECALL)
> 	@GeneralMessage
> 	public String recall(@AutowiredParam long groupId,@AutowiredParam long userId) {
> 		return "%s撤回了一条消息".formatted(userId);
> 	}
> ```
>
> 例如这个recall方法，它被@CmdNotice标识为面向“通知事件”的一个功能，同时，只对group_recall（群消息消息撤回）通知事件反馈，这里@AutowiredParam使用了缺省注入，即按照方法参数名进行匹配（支持驼峰明明和下划线命名），但是注意，缺省注入暂时不支持递归注入，例如5.1中的nickname参数是sender下面的nickname，你现在必须显式地标明@AutowiredParam(".sender.nickname")，否则注入失败。
>
> #### 5.3 @CmdRequest，标识这是一个面向请求事件的功能
>
> ```java
> @CmdRequest(requestType = {RequestType.FRIEND,RequestType.GROUP},subType = {RequestSubType.ADD,RequestSubType.INVITE})
> 	public void request(@AutowiredParam String request_type) {
> 		System.out.println("收到了%s请求".formatted("friend".equals(request_type)?"加好友":"加群"));
> 	}
> ```
>
> 例如这个request方法，它被@CmdRequest标识为面向“请求事件”的一个功能，它对加群、加好友请求事件反馈。当你不需要回复消息时，可以不使用注解@GeneralMessage。
>
> #### 5.4 @CmdMeta，标识这是一个面向元事件的功能
>
> ```java
> @CmdMeta(metaEventType = MetaEventType.HEARTBEAT)
> 	public void heartbeat(@AutowiredParam long time) {
> 		System.out.println("收到心跳：%d".formatted(time));
> 	}
> ```
>
> 例如这个heartbeat方法，它对心跳事件做反馈
>
> #### 5.5 @CmdRegular，标识这是一个群消息文本匹配反馈功能
>
> CmdRegular是CmdMessage的一种特殊类型，它使用正则匹配群消息达到反馈的目的
>
> ```java
> @CmdRegular(name="engine",expression = "^用(.+)引擎检索(\\d+)$")
> 	@GeneralMessage(targetId = "123456")
> 	public String search(Qmessage qmassage
> 						,@AutowiredParam("message") String msg
> 						,@AutowiredParam Date sendTime
> 						,int pid
> 						,@AutowiredRegular(1) String engine) {
> 		//TODO your code
> 		return "检索结果...";
> 	}
> ```
>
> 它可以实现诸如此类更为复杂的识别，例如这个search方法，当群消息匹配expression中的正则式时，则进入此方法，使用@AutowiredParam可以注入event中的对象，使用@AutowiredRegular或缺省可以注入expression中匹配抽取出的串，甚至可以随心所欲地改变你形参的位置。

### 6.Setup your Bot

本项目依赖于以Onebot为标准的HTTP AIP，例如[onebot-kotlin](https://github.com/yyuueexxiinngg/onebot-kotlin)，你需要先配置好你的HTTP API，然后按照[4.Configuration](#user-content-4configuration )中配置你的accenbot，打包。

```shell
java -jar accenbot.jar
```

另外，如果你使用了Pixivc的功能，请关注日志中需要你填写登录验证码。

现在，开始愉快地享受极简框架Accenbot吧！

### 7.Notice

我在集成onebot-kotlin时，发现作者提供的包是使用openjdk编译的，所以为了后续新版本的计划，建议使用openjdk编译和运行。