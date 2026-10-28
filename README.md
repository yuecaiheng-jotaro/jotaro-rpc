# JRpc

基于 Netty 的轻量 Java RPC 框架。服务端把接口实现注册到 ZooKeeper，客户端通过 JDK 动态代理发起远程调用；传输层使用自定义二进制协议，并内置负载均衡、本地服务缓存、令牌桶限流和失败重试。

当前版本：`1.0.6-SNAPSHOT`

## 架构

```
┌─────────────┐          ┌────────────────┐          ┌─────────────┐
│   Client    │  服务发现  │   ZooKeeper    │  服务注册  │   Server    │
│ JDK Proxy   │ ───────▶ │  /JotaroRpc    │ ◀─────── │ ServerProvider│
│ NettyRpcClient│        │  临时节点实例    │          │ NettyRpcServer│
└──────┬──────┘          └────────────────┘          └──────▲──────┘
       │  自定义协议（长度 + 消息类型 + 序列化类型 + 报文）      │
       └──────────────────── Netty TCP ─────────────────────┘
```

调用链路：

1. 服务端 `ServerProvider` 扫描实现类接口，写入本地表，并以 **临时节点** 注册到 ZK。
2. 客户端 `NettyClientProxy` 拦截接口方法，组装 `RpcRequest`。
3. `ZKServiceCenter` 从本地缓存 / ZK 拉取实例列表，经负载均衡选出地址。
4. Netty 编码发送；服务端反射调用，按接口+方法做令牌桶限流后返回 `RpcResponse`。

## 模块

| 模块 | 职责 |
| --- | --- |
| `common` | 协议对象、序列化编解码、令牌桶限流、示例 API（`UserService`） |
| `register` | ZooKeeper 注册 / 发现、Watcher 缓存、负载均衡 |
| `server` | Netty 服务端、本地服务表、请求分发与限流 |
| `client` | Netty 客户端、JDK 动态代理、Guava 重试 |

## 特性

- **自定义协议**：4 字节长度前缀解决粘包；消息类型（请求 / 响应）+ 序列化类型 + 报文。
- **序列化**：JSON（Fastjson2，当前默认）与 Java 原生序列化。
- **服务治理**：ZK 永久服务路径 + 临时实例节点；服务下线自动摘除。
- **服务发现缓存**：`CuratorCache` 监听节点变更，默认写入本地内存缓存。
- **负载均衡**：轮询（默认）、随机、一致性哈希（10 个虚拟节点）。
- **限流**：按 `接口名#方法名` 独立令牌桶，默认容量 10、补充间隔 100ms。
- **重试**：`GuavaRetryHandler` 异常或非成功码时最多 3 次，间隔 2 秒。

## 协议格式

外层由 Netty `LengthFieldPrepender` / `LengthFieldBasedFrameDecoder` 处理长度：

```
+----------------+----------------+----------------+------------------+
| length (4B)    | msgType (2B)   | serialize (2B) | payload (N B)    |
+----------------+----------------+----------------+------------------+
```

| 字段 | 说明 |
| --- | --- |
| `msgType` | `0` 请求，`1` 响应 |
| `serialize` | `0` Java 原生，`1` JSON |
| `payload` | `RpcRequest` / `RpcResponse` 序列化结果 |

## 快速开始

### 环境

- JDK 8+
- Maven 3.6+
- ZooKeeper（默认连接 `10.211.55.6:2181`）

### 编译

```bash
mvn clean install
```

### 启动 ZooKeeper

本地或远程均可。连接地址从 `register/src/main/resources/zk.propertis` 读取：

```properties
jrpc.zk.address=10.211.55.6:2181
```

ZK 命名空间为 `JotaroRpc`，实例节点形如：

```
/JotaroRpc/{接口全限定名}/{host:port}
```

### 发布服务

```java
UserService userService = new UserServiceImpl();
ServerProvider serverProvider = new ServerProvider("127.0.0.1", 9999);
serverProvider.provideServiceInterface(userService);

RpcServer rpcServer = new NettyRpcServer(serverProvider);
rpcServer.start(9999);
```

`provideServiceInterface` 会把实现类的全部接口注册到 ZK，并写入服务端本地映射。

### 发起调用

```java
NettyClientProxy proxy = new NettyClientProxy();
UserService userService = proxy.getProxy(UserService.class);
User user = userService.getUserById(1);
```

仓库内已有对应测试：

- 服务端：`server/src/test/java/com/jotaro/server/TestRpcServer.java`
- 客户端：`client/src/test/java/com/jotaro/client/netty/TestNetty.java`

先启动服务端测试，再启动客户端测试。

## 配置与扩展点

### ZooKeeper

连接串从 classpath 上的 `zk.propertis` 读取 `jrpc.zk.address`；文件缺失或配置为空时回退到 `10.211.55.6:2181`。会话超时 40s，连接超时 15s，指数退避重试 3 次。

实现见 `ZKClientFactory`。

### 负载均衡

客户端发现侧默认使用 `RoundLoadBalancer`。可替换为：

- `RandomLoadBalancer`
- `ConsistentHashLoadBalancer`（可自定义哈希函数）

修改位置：`ZKServiceCenter` 构造函数中的 `this.loadBalancer`。

### 序列化

Pipeline 当前使用 `JsonSerializer`。编解码器为 `JotaroEncoder` / `JotaroDecoder`，按报文头中的类型动态选择反序列化器。

### 限流

`RateLimitProvider` 按方法维度懒创建 `TokenBucketRateLimitImpl(rate=100, capacity=10)`。令牌不足时返回失败响应「服务限流」。

### 重试

```java
GuavaRetryHandler retryHandler = new GuavaRetryHandler(/* RpcClient */);
RpcResponse response = retryHandler.sendWithRetry(rpcRequest);
```

## 技术栈

- Netty 4.1
- Apache Curator 5.2
- Fastjson2
- Guava / guava-retrying
- Hutool、Lombok、SLF4J
- JUnit 4 / JUnit 5

## 目录结构

```
JRpc
├── common      # 协议、序列化、限流、示例 API
├── register    # ZK 注册发现、缓存、负载均衡
├── server      # Netty 服务端
└── client      # Netty 客户端、代理、重试
```
