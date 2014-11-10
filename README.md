WestlakstudentXmppClient
========================

消息推送客户端

XMPP消息推送整理
注：XMPP客户端与服务器连接流程（asmack，mina）openfire


以下为具体流程：
1.客户端发起请求连接，发送一个请求包
<stream:stream 	to="192.168.8.190"xmlns="jabber:client"xmlns:stream="http://etherx.jabber.org/streams" 	version="1.0"> 给服务器；


2.服务器接收到上面的请求后，发送回复包
<?xmlversion='1.0'encoding='UTF-8'?><stream:stream xmlns:stream="http://etherx.jabber.org/streams"xmlns="jabber:client" from="127.0.0.1" id="8695062c" xml:lang="en" version="1.0">
给客户端，从此建立连接，并维持着一个心跳；然后再发送一个请求认证和注册的包
<stream:features><starttls xmlns="urn:ietf:params:xml:ns:xmpp-tls"></starttls><auth xmlns="http://jabber.org/features/iq-auth"/><register xmlns="http://jabber.org/features/iq-register"/></stream:features>
给客户端要求其注册并认证。

3.若客户端不注册也无妨，链接是一直在的；

4.若客户端发送注册请求包set为注册
<iqid="WoMGt-0"type="set"><query xmlns="jabber:iq:register"><password>88888888</password><username>westlakestudent</username><imei>862186021303189</imei></query></iq>
此处服务器端可以自己定义，判断客户端是否为注册过的，是则返回一个PacketError.Condition.conflict的error（即为重复注册）
下图为一般情况下的状态及错误代码：

注册成功则服务器返回<iq type="result" id="WoMGt-0" to="127.0.0.1/8695062c"/>

5.客户端请求认证发送请求认证包get为请求认证（相当于客户端的登录）
<iqid="WoMGt-1"type="get"><query xmlns="jabber:iq:auth"><username>westlakestudent</username></query></iq>

6.服务端接收到此请求认证包之后发送认证要求包（认证所需的参数）
<iqtype="result"id="WoMGt-1"to="127.0.0.1/8695062c"><query xmlns="jabber:iq:auth"><username>westlakestudent</username><password/><digest/><resource/></query></iq>

7.客户端收到服务端发的要求包之后根据条件发送相应的参数set为认证
<iqid="WoMGt-2"type="set"><query xmlns="jabber:iq:auth"><username>westlakestudent</username><digest>d3249e513ce8ff08fd93b1fc36803b863bcccfde</digest><resource>WESTLAKESTUDENT</resource></query></iq>

8.服务器发送认证over包
<iq type="result" id="WoMGt-2" to="westlakestudent@127.0.0.1/WESTLAKESTUDENT"/>
此时两者之间的连接为已认证（STATUS_AUTHENTICATED），在这之前都为STATUS_CONNECTED
服务端流程：（通过阿帕奇的mina框架）


1.通过这个org.apache.mina.transport.socket.nio.NioSocketAcceptor类类侦听客户端的请求连接，通过实现这个org.apache.mina.core.service.IoHandler接口来作为NioSocketAcceptor的处理类，通过绑定InetSocketAddress（端口）

2.         acceptor = new NioSocketAcceptor();  
3.         SocketSessionConfig sessionConfig = acceptor.getSessionConfig();  
4.         sessionConfig.setReadBufferSize(BUFFER);  
5.         DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();  
6.         chain.addLast("androidpn", newProtocolCodecFilter(new TextLineCodecFactory()));  
7.         acceptor.setHandler(handler);  
8.         address = new InetSocketAddress(SERVER_PORT);
9.         try {  
10.             acceptor.bind(address);  
11.         } catch (IOException e) {  
12.             Log.error(e.getMessage());
13.         }  

到此处服务端侦听结束

客户端连接过来先到IoHandler的一个实现类中的sessionCreated-->sessionOpened
一般我们在sessionOpened方法中初始化一些东西,通过sessionManager来管理session，在这个manager中有两个map对象，pre这个是验证前的session存储区域，另一个则是认证后的。即sessionmanager中对应connection的两种状态。在messageReceived方法中处理message
根据客户端过来请求消息的类型，分为 IQ消息处理，客户端状态处理，其他处理进入IQ处理后通过不同的namespace路由，分别到相应的出来handler（IQRegisterHandler等）。















客户端流程：（通过开源android段smack扩展的asmack框架）
心跳维持为asmack自动维持	
1.通过XMPPConnection 类建立与服务器的连接；
2.通过产生的connection向服务段发送Registration进行注册
3.通过connection的login方法进行登录
4.Connection通过PacketListener接口的回调方法processPacket获得服务端发过来的消息



