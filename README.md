**基于[酷Q机器人](https://cqp.cc/) ![酷Q](https://sr.cqp.me/template/dightnew/ex/logo-common.png "酷Q")和[CoolQ HTTP API 插件](https://github.com/richardchien/coolq-http-api/)实现的QQ群聊天机器人。**
---

[![License](https://img.shields.io/github/license/AcceNoi/dmzjbot)](https://img.shields.io/github/license/AcceNoi/dmzjbot) [![Size](https://img.shields.io/github/repo-size/AcceNoi/dmzjbot)](https://img.shields.io/github/repo-size/AcceNoi/dmzjbot)

## 主页（建设中） - [bot.accen.wiki](http://bot.accen.wiki)

| Author | Accen/クロノス    |
| ------ | ----------------- |
| Email  | 1339liu@gmail.com |

---

## 功能介绍

### 1. 图片

- **搜图** 借助[Saucenao](http://saucenao.com/)![saucenao](http://saucenao.com/images/static/banner.gif)实现的搜图功能，用过这个网站的人应该都明白它的强大之处，另外其他诸如我们所更了解的百度识图、谷歌识图等理论上都是可以集成上去的。
  搜图功能支持两种用法：

  | 发送搜图指令+图片                      | 直接出结果       | 难适应手机端（其实手机端可以做到，但是操作并没那么友好） |
  | -------------------------------------- | ---------------- | -------------------------------------------------------- |
  | 发送搜图指定等待提示后，再单独发送图片 | 分段式*回调监听* | 适合手机端使用习惯                                       |

- **随机图片** 借助[随机图片API](https://api.lolicon.app)实现的随机图片功能，该API是由网络上提供的随机（P站）图片API，使用[pixiv.cat](https://pixiv.cat/)作为图片代理。

- **收藏** 在1.2的基础上，获得图片后会触发一次*常驻回调监听*，在指定时间内发送对应指定可保存此图片的索引，并通过“随机收藏”重新展现出来。

### 2. 词条

- **新增词条**
- **删除词条**
- **查看词条**
- **查询词条**
- **我的词条**

>实际上就是对词条的CURD，但是相比其他固定的功能，这个具有一定的自由度，沙盒性质。对用户的输入做出对应的反应，精确或模糊，是否需要At，以及后面有一个相对复杂的词条功能，都给与用户足够的创作空间。

### 3. 音乐

- **点歌** 借助[网易云音乐的搜索API](http://music.163.com)实现的点歌功能，酷Q的CQ码支持分享QQ音乐、网易云音乐和虾米音乐，有兴趣的话可已看看其他两个搜索API，酷Q社区上有非常多的例子。

- **B站点歌与投稿** 这个是一个相对复杂的功能，能够截取[B站](https://www.bilibili.com)![Bilibili](https://raw.githubusercontent.com/Henryhaohao/Bilibili_video_download/master/Pic/logo.png)的视频，下载到本地，然后使用ffmpeg对视频进行处理
  我这里的处理就是简单的截取某段音频并保存下来。保存后以两种形式呈现：

- | 语音 | 限制在120秒以内 | 以词条的形式触发       | 展示为语音     |
  | ---- | --------------- | ---------------------- | -------------- |
  | 音乐 | 无时长限制      | 以B站点歌XXX的形式触发 | 展示为音乐分享 |

- 这个是自定义的音乐格式，酷Q也支持这种写法。其投稿点歌也是自由度相对比较高的功能，可以继续在
  这上面拓展，但是这对服务器的性能要求会比较高，酌情考虑取舍。

### 4. 抽卡

- **东方卡包**
- **影之诗卡包**

>抽卡功能即是设置卡片的抽取概率，再通过一定的指定随机出来，并保留抽取结果。

### 5. 提醒&复读

- **定时提醒** 设定定时任务，做好时间的解析即可。
- **复读** 让bot重复你说的话（或者语音说出来），或者启用*复读模式*，当群内的复读次数达到一定次数时，则参与一次复读。（这些都是比较闲的小功能）
- **翻译** 调用[Google翻译](https://translate.google.cn/)![Google](https://www.gstatic.com/images/branding/googlelogo/svg/googlelogo_clr_74x24px.svg)接口进行翻译。注意Google翻译有个token的生成比较麻烦，网上有现成的例子可供使用，其他的翻译API如百度翻译、有道翻译有兴趣可以尝试。

### 6. 百科

>解析百度百科的页面，但是要注意*同义词*的处理。其他的如互动百科、维基百科、萌娘百科都可以尝试

### 7.订阅

- **B站up订阅** 支持对B站up的用户名称和uid的检索，检索成功后允许订阅。订阅以*群组-个人*的形式存储，也就是说在两个群组的同一个人的订阅是互不影响的。订阅成功后会推送该up的动态、转发动态、视频、专栏。当群组中多个人订阅了同一个up时，会同时at，也就是每个up更新一个动态时，只会在每个群组推送一次。

  > - 现在是统一的使用bilibili动态接口，b站up的行为绝大部分会以动态的形式呈现，而且这个接口是不需要登录的。
  >
  > - | 接口调用地址   | https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/space_history |
  >   | -------------- | ------------------------------------------------------------ |
  >   | 请求方式       | **GET**                                                      |
  >   | 参数           | visitor_uid：参观者uid（基本不需要）<br />host_uid：up的uid<br />offset_dynamic_id：offset（不含）<br />（暂时没找到控制size和sort的字段，默认是时间倒序） |
  >   | 主要返回值说明 | 具体的动态都放在data-cards下面<br />desc-timestamp：动态的时间戳，这个时间戳没有记录毫秒，转成UNIX时间戳时要*1000再使用<br />desc-type：动态的类型，1-转发动态，2-普通动态，8-视频，64-专栏。看样子应该至少还有4，16，32，估计是音频啥的，欢迎各位补充<br />card：具体的动态，是一个json格式的字符串，再额外去解析即可。但是要注意的是，某些type的card的字符串是用unicode编码的，要记得解码。<br />其他重要字段欢迎补充 |
  >
  > - 现在的扫描策略是0 */2 * * * * 

## 部分名词说明

- **回调监听** 回调监听是针对后续步骤处理的一种方式，酷Q不支持对消息的标记*（版本为酷Q on Docker 2.0/酷Q Pro 5.15.4）*，所以要产生互动必须要保留上一次有意义的消息记录并监听下一次对应的记录，这个过程就叫做回调监听了，具体可以查看CallbackManager.java的实现和说明。

- **常驻回调监听** 常驻回调监听是回调监听的一种，回调监听分为两种模式：

- | 一般回调监听 | 生命周期由CallbackManager保管 | 以上一次的消息为标识 |
  | ------------ | ----------------------------- | -------------------- |
  | 常驻回调监听 | 生命周期由自己保管            | 自定义标识           |

- **风纪模式** 风纪模式调用了百度AI的[图像审核API]( https://ai.baidu.com/tech/imagecensoring )![百度AI]( https://ai.bdstatic.com/file/03D0F32FE36C4E3A893D1AD60E797F5B )，根据结果的强弱程度划分成了四个强度：

- | 强度       | 级别 | 说明                      | 备注                                                         |
  | ---------- | ---- | ------------------------- | ------------------------------------------------------------ |
  | prohibited | ★★★★ | 不允许图片的传送          |                                                              |
  | strong     | ★★★☆ | 审核策略比较严格          | coolq.judge.maxpornpro:0.3最高能容忍的（不含）               |
  | normal     | ★★☆☆ | 一般审核策略              | coolq.judge.minnormalpro:0.85最低能接受的（含），以上两者加起来必须大于1，否则就没意义 |
  | allow      | ★☆☆☆ | 不审核                    |                                                              |
  | invalid    | ☆☆☆☆ | 无效，也就是审核的api挂了 | 比如调用次数用光了（现在用的免费次数）                       |

- **功能开关** 功能开关用来对bot的某些功能进行限制，现在的功能主要包含：词条回复、系统功能、回调监听、监听。能够被功能开关所限制的是系统功能。通过@FuncSwitch标识这个功能，在CFG_CONFIG_VALUE中对其进行控制，粒度暂时只能精确到群组，详细可看FuncSwitchUtil.java。

  ## 感谢

  - 感谢酷Q项目，各位有兴趣可移步[酷Q社区]( https://cqp.cc/ )
  - 感谢大佬[richardchien](https://github.com/richardchien)的**[coolq-http-api](https://github.com/richardchien/coolq-http-api)**项目，提供了一个第三方公共调用的接口
  - 感觉大佬[Henryhaohao](https://github.com/Henryhaohao)的**[Bilibili_video_download](https://github.com/Henryhaohao/Bilibili_video_download)**项目的B站接口调用指导

