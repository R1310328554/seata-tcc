
2020-12-15 23:53:33,923 ERROR [http-nio-8084-exec-35] o.a.c.c.C.[.[.[.[dispatcherServlet] [DirectJDKLog.java : 175] Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is org.apache.dubbo.rpc.RpcException: Failed to invoke the method prepareMinus in the service io.seata.samples.common.FirstTccAction. Tried 3 times of the providers [192.168.56.1:20881] (1/1) from the registry 127.0.0.1:2181 on the consumer 192.168.56.1 using the dubbo version 2.7.5. Last error is: Failed to invoke remote method: prepareMinus, provider: dubbo://192.168.56.1:20881/io.seata.samples.common.FirstTccAction?anyhost=true&application=tcc-sample&check=false&deprecated=false&dubbo=2.0.2&dynamic=true&generic=false&init=false&interface=io.seata.samples.common.FirstTccAction&lazy=true&loadbalance=roundrobin&methods=rollback,prepareMinus,commit&pid=23880&qos.enable=false&register.ip=192.168.56.1&release=2.7.5&remote.application=tcc-sample-p1&side=consumer&sticky=false&timeout=10000&timestamp=1608044488568, cause: org.apache.dubbo.remoting.RemotingException: Server side(192.168.56.1,20881) thread pool is exhausted, detail msg:Thread pool is EXHAUSTED! Thread Name: DubboServerHandler-192.168.56.1:20881, Pool Size: 10 (active: 10, core: 10, max: 10, largest: 10), Task: 15629 (completed: 15619), Executor status:(isShutdown:false, isTerminated:false, isTerminating:false), in dubbo://192.168.56.1:20881!] with root cause
org.apache.dubbo.remoting.RemotingException: Server side(192.168.56.1,20881) thread pool is exhausted, detail msg:Thread pool is EXHAUSTED! Thread Name: DubboServerHandler-192.168.56.1:20881, Pool Size: 10 (active: 10, core: 10, max: 10, largest: 10), Task: 15629 (completed: 15619), Executor status:(isShutdown:false, isTerminated:false, isTerminating:false), in dubbo://192.168.56.1:20881!
	at org.apache.dubbo.remoting.exchange.support.DefaultFuture.doReceived(DefaultFuture.java:212)
	at org.apache.dubbo.remoting.exchange.support.DefaultFuture.received(DefaultFuture.java:175)
	at org.apache.dubbo.remoting.exchange.support.DefaultFuture.received(DefaultFuture.java:163)
	at org.apache.dubbo.remoting.exchange.support.header.HeaderExchangeHandler.handleResponse(HeaderExchangeHandler.java:60)
	at org.apache.dubbo.remoting.exchange.support.header.HeaderExchangeHandler.received(HeaderExchangeHandler.java:181)
	at org.apache.dubbo.remoting.transport.DecodeHandler.received(DecodeHandler.java:51)
	at org.apache.dubbo.remoting.transport.dispatcher.ChannelEventRunnable.run(ChannelEventRunnable.java:57)
	at org.apache.dubbo.common.threadpool.ThreadlessExecutor.waitAndDrain(ThreadlessExecutor.java:77)
	at org.apache.dubbo.rpc.AsyncRpcResult.get(AsyncRpcResult.java:175)
	at org.apache.dubbo.rpc.protocol.AsyncToSyncInvoker.invoke(AsyncToSyncInvoker.java:61)
	at org.apache.dubbo.rpc.listener.ListenerInvokerWrapper.invoke(ListenerInvokerWrapper.java:78)
	at com.alibaba.dubbo.rpc.Invoker$CompatibleInvoker.invoke(Invoker.java:55)
	at io.seata.integration.dubbo.alibaba.AlibabaDubboTransactionPropagationFilter.invoke(AlibabaDubboTransactionPropagationFilter.java:45)
	at com.alibaba.dubbo.rpc.Filter.invoke(Filter.java:29)
	at org.apache.dubbo.rpc.protocol.ProtocolFilterWrapper$1.invoke(ProtocolFilterWrapper.java:81)
	at io.seata.integration.dubbo.ApacheDubboTransactionPropagationFilter.invoke(ApacheDubboTransactionPropagationFilter.java:69)
	at org.apache.dubbo.rpc.protocol.ProtocolFilterWrapper$1.invoke(ProtocolFilterWrapper.java:81)
	at org.apache.dubbo.monitor.support.MonitorFilter.invoke(MonitorFilter.java:89)
	at org.apache.dubbo.rpc.protocol.ProtocolFilterWrapper$1.invoke(ProtocolFilterWrapper.java:81)
	at org.apache.dubbo.rpc.protocol.dubbo.filter.FutureFilter.invoke(FutureFilter.java:49)
	at org.apache.dubbo.rpc.protocol.ProtocolFilterWrapper$1.invoke(ProtocolFilterWrapper.java:81)
	at org.apache.dubbo.rpc.filter.ConsumerContextFilter.invoke(ConsumerContextFilter.java:55)
	at org.apache.dubbo.rpc.protocol.ProtocolFilterWrapper$1.invoke(ProtocolFilterWrapper.java:81)
	at org.apache.dubbo.rpc.protocol.InvokerWrapper.invoke(InvokerWrapper.java:56)
	at org.apache.dubbo.rpc.cluster.support.FailoverClusterInvoker.doInvoke(FailoverClusterInvoker.java:82)
	at org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker.invoke(AbstractClusterInvoker.java:255)
	at org.apache.dubbo.rpc.cluster.interceptor.ClusterInterceptor.intercept(ClusterInterceptor.java:47)
	at org.apache.dubbo.rpc.cluster.support.wrapper.AbstractCluster$InterceptorInvokerNode.invoke(AbstractCluster.java:92)
	at org.apache.dubbo.rpc.cluster.support.wrapper.MockClusterInvoker.invoke(MockClusterInvoker.java:78)
	at org.apache.dubbo.rpc.proxy.InvokerInvocationHandler.invoke(InvokerInvocationHandler.java:60)
	at org.apache.dubbo.common.bytecode.proxy0.prepareMinus(proxy0.java)
	at org.apache.dubbo.common.bytecode.proxy0$$FastClassBySpringCGLIB$$c197671e.invoke(<generated>)
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218)
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)
	at io.seata.rm.tcc.interceptor.ActionInterceptorHandler.proceed(ActionInterceptorHandler.java:84)
	at io.seata.spring.tcc.TccActionInterceptor.invoke(TccActionInterceptor.java:95)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688)
	at org.apache.dubbo.common.bytecode.proxy0$$EnhancerBySpringCGLIB$$5f06f677.prepareMinus(<generated>)
	at io.seata.samples.business.service.TransferServiceImpl.transfer(TransferServiceImpl.java:39)
	at io.seata.samples.business.service.TransferServiceImpl$$FastClassBySpringCGLIB$$45d035c2.invoke(<generated>)
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218)
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)
	at io.seata.spring.annotation.GlobalTransactionalInterceptor$2.execute(GlobalTransactionalInterceptor.java:184)
	at io.seata.tm.api.TransactionalTemplate.execute(TransactionalTemplate.java:127)
	at io.seata.spring.annotation.GlobalTransactionalInterceptor.handleGlobalTransaction(GlobalTransactionalInterceptor.java:181)
	at io.seata.spring.annotation.GlobalTransactionalInterceptor.invoke(GlobalTransactionalInterceptor.java:150)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688)
	at io.seata.samples.business.service.TransferServiceImpl$$EnhancerBySpringCGLIB$$d73be3ed.transfer(<generated>)
	at io.seata.samples.business.controller.BusinessController.doTransfer(BusinessController.java:59)
	at io.seata.samples.business.controller.BusinessController.doTransferSuccess(BusinessController.java:44)
	at io.seata.samples.business.controller.BusinessController.purchaseCommit(BusinessController.java:33)
	at sun.reflect.GeneratedMethodAccessor51.invoke(Unknown Source)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:215)
	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:142)
	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:102)
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:895)
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:800)
	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1038)
	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:942)
	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1005)
	at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:897)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:634)
	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:882)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:741)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:231)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:99)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:92)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
	at org.springframework.web.filter.HiddenHttpMethodFilter.doFilterInternal(HiddenHttpMethodFilter.java:93)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:200)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:199)
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:96)
	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:490)
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:139)
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92)
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:343)
	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:408)
	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:66)
	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:791)
	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1417)
	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
	at java.lang.Thread.run(Thread.java:748)
2020-12-15 23:53:33,982 ERROR [http-nio-8084-exec-45] o.a.c.c.C.[.[.[.[dispatcherServlet] [DirectJDKLog.java : 175] Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is org.apache.dubbo.rpc.RpcException: Failed to invoke the method prepareMinus in the service io.seata.samples.common.FirstTccAction. Tried 3 times of the providers [192.168.56.1:20881] (1/1) from the registry 127.0.0.1:2181 on the consumer 192.168.56.1 using the dubbo version 2.7.5. Last error is: Failed to invoke remote method: prepareMinus, provider: dubbo://192.168.56.1:20881/io.seata.samples.common.FirstTccAction?anyhost=true&application=tcc-sample&check=false&deprecated=false&dubbo=2.0.2&dynamic=true&generic=false&init=false&interface=io.seata.samples.common.FirstTccAction&lazy=true&loadbalance=roundrobin&methods=rollback,prepareMinus,commit&pid=23880&qos.enable=false&register.ip=192.168.56.1&release=2.7.5&remote.application=tcc-sample-p1&side=consumer&sticky=false&timeout=10000&timestamp=1608044488568, cause: org.apache.dubbo.remoting.RemotingException: Server side(192.168.56.1,20881) thread pool is exhausted, detail msg:Thread pool is EXHAUSTED! Thread Name: DubboServerHandler-192.168.56.1:20881, Pool Size: 10 (active: 10, core: 10, max: 10, largest: 10), Task: 15630 (completed: 15620), Executor status:(isShutdown:false, isTerminated:false, isTerminating:false), in dubbo://192.168.56.1:20881!] with root cause
org.apache.dubbo.remoting.RemotingException: Server side(192.168.56.1,20881) thread pool is exhausted, detail msg:Thread pool is EXHAUSTED! Thread Name: DubboServerHandler-192.168.56.1:20881, Pool Size: 10 (active: 10, core: 10, max: 10, largest: 10), Task: 15630 (completed: 15620), Executor status:(isShutdown:false, isTerminated:false, isTerminating:false), in dubbo://192.168.56.1:20881!
	at org.apache.dubbo.remoting.exchange.support.DefaultFuture.doReceived(DefaultFuture.java:212)
	at org.apache.dubbo.remoting.exchange.support.DefaultFuture.received(DefaultFuture.java:175)
	at org.apache.dubbo.remoting.exchange.support.DefaultFuture.received(DefaultFuture.java:163)
	at org.apache.dubbo.remoting.exchange.support.header.HeaderExchangeHandler.handleResponse(HeaderExchangeHandler.java:60)
	at org.apache.dubbo.remoting.exchange.support.header.HeaderExchangeHandler.received(HeaderExchangeHandler.java:181)
	at org.apache.dubbo.remoting.transport.DecodeHandler.received(DecodeHandler.java:51)
	at org.apache.dubbo.remoting.transport.dispatcher.ChannelEventRunnable.run(ChannelEventRunnable.java:57)
	at org.apache.dubbo.common.threadpool.ThreadlessExecutor.waitAndDrain(ThreadlessExecutor.java:77)
	at org.apache.dubbo.rpc.AsyncRpcResult.get(AsyncRpcResult.java:175)
	
	
	
D:\d\xampp\apache\bin>ab -n 100000 -c 50 http://127.0.0.1:8084/tcc/commit/1
This is ApacheBench, Version 2.3 <$Revision: 1843412 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking 127.0.0.1 (be patient)
Completed 10000 requests


Server Software:
Server Hostname:        127.0.0.1
Server Port:            8084

Document Path:          /tcc/commit/1
Document Length:        4 bytes

Concurrency Level:      50
Time taken for tests:   547.427 seconds
Complete requests:      17904
Failed requests:        12886
   (Connect: 0, Receive: 0, Length: 12886, Exceptions: 0)
Non-2xx responses:      12886
Total transferred:      18979753 bytes
HTML transferred:       16849177 bytes
Requests per second:    32.71 [#/sec] (mean)
Time per request:       1528.699 [ms] (mean)
Time per request:       30.574 [ms] (mean, across all concurrent requests)
Transfer rate:          33.86 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        1    5   2.9      5      44
Processing:   211 1519 594.0   1182    9015
Waiting:      153 1511 593.2   1174    8962
Total:        213 1524 594.0   1187    9016

Percentage of the requests served within a certain time (ms)
  50%   1187
  66%   1302
  75%   2218
  80%   2273
  90%   2374
  95%   2454
  98%   2577
  
  
  101005041	98994959	0	0	-12915
  
  
  2020-12-16 00:17:54,858 ERROR [http-nio-8084-exec-66] i.s.c.r.n.AbstractNettyRemotingClient [AbstractNettyRemotingClient.java : 171] wait response error:cost 30001 ms,ip:${SEATA_IP:10.100.254.144}:${SEATA_PORT:31609},request:xid=192.168.56.1:8091:82266420372574208,extraData=null
  2020-12-16 00:17:54,858 ERROR [http-nio-8084-exec-66] i.s.tm.api.DefaultGlobalTransaction [DefaultGlobalTransaction.java : 130] Failed to report global commit [192.168.56.1:8091:82266420372574208],Retry Countdown: 5, reason: RPC timeout
  2020-12-16 00:17:54,897 ERROR [http-nio-8084-exec-67] i.s.c.r.n.AbstractNettyRemotingClient [AbstractNettyRemotingClient.java : 171] wait response error:cost 30001 ms,ip:${SEATA_IP:10.100.254.144}:${SEATA_PORT:31609},request:xid=192.168.56.1:8091:82266420414517248,extraData=null
  2020-12-16 00:17:54,897 ERROR [http-nio-8084-exec-67] i.s.tm.api.DefaultGlobalTransaction [DefaultGlobalTransaction.java : 130] Failed to report global commit [192.168.56.1:8091:82266420414517248],Retry Countdown: 5, reason: RPC timeout
  2020-12-16 00:17:54,941 ERROR [http-nio-8084-exec-68] i.s.c.r.n.AbstractNettyRemotingClient [AbstractNettyRemotingClient.java : 171] wait response error:cost 30001 ms,ip:${SEATA_IP:10.100.254.144}:${SEATA_PORT:31609},request:xid=192.168.56.1:8091:82266420477431808,extraData=null
  2020-12-16 00:17:54,941 ERROR [http-nio-8084-exec-68] i.s.tm.api.DefaultGlobalTransaction [DefaultGlobalTransaction.java : 130] Failed to report global commit [192.168.56.1:8091:82266420477431808],Retry Countdown: 5, reason: RPC timeout
  2020-12-16 00:17:54,993 ERROR [http-nio-8084-exec-69] i.s.c.r.n.AbstractNettyRemotingClient [AbstractNettyRemotingClient.java : 171] wait response error:cost 30001 ms,ip:${SEATA_IP:10.100.254.144}:${SEATA_PORT:31609},request:xid=192.168.56.1:8091:82266420515180544,extraData=null
  2020-12-16 00:17:54,993 ERROR [http-nio-8084-exec-69] i.s.tm.api.DefaultGlobalTransaction [DefaultGlobalTransaction.java : 130] Failed to report global commit [192.168.56.1:8091:82266420515180544],Retry Countdown: 5, reason: RPC timeout