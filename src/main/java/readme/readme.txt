
Session控制器基本实现
备注：由于我周六加班；工作较忙，只有周日一天可以抽出时间在家实现此功能，时间有限，考虑不全，望面试官可以指点，不吝赐教；实现如下：
一、采用socket连接，在本地模拟客户端和服务端；
1）客户端：在客户端，为了避免对每一个请求都分配线程而带来的资源开销，服务会预先分配一个固定大小的线程池SessionEventPool实现异步请求;
2)服务端；可以通过修改SessionEventPool中线程池中线程数目动态调整并发参数；为了防止OOM，本项目中没有采用Executors工具类去生成线程池，而是采用了ThreadPoolExecutor；
3)并发及并发数目: 本项目中没有采用Executors工具类去生成线程池，而是采用了ThreadPoolExecutor实现多线程；可以通过修改SessionEventPool中线程池中线程数目动态调整并发参数；
4)异步：JDK1.8中则新增的lambda表达式和CompletableFuture技术实现异步请求   服务端：采用多线程的方式，每接收到一个Socket就建立一个新的线程来处理它；
5)超时：为了控制每次请求的超时时间，项目中引用了redis，每次请求时都设置session的超时时间，key为DeliverySessionId，value为StopTime；当过期时服务端断开连接；
6)日志: 在请求结束后，会将session 状态 (发送时间,发送url,和body,结果,连接信息)，写入日志；
7）操作说明 先启动服务端，再启动客户端，在真实的操作环境中，通过输入javaBean的文件路径，动态的加载请求；
二、Schema文件的处理
将Schema文件转换为Java文件；
可通过xjc命令完成将schema文件转换为java文件。
打开命令控制台，切换至项目中xsd文件所在目录，如E:\Eclipse\webservice\03_schema\src\schema
输入命令：xjc -d <导出的java文件存放目录> -verbose <需要转换的xsd文件>
如：xjc -d E:\Eclipse\webserviceimport\02 -verbose classroom.xsd；通过客户端输入生成javaBean文件的路径；实现加载请求参数；
三、扩展
根据过期时间发送；可以将所有的任务添加在一个任务队列中，开启线程进行遍历，如果过期了就删除；


