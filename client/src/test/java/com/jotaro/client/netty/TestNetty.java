package com.jotaro.client.netty;

import com.alibaba.fastjson2.JSON;
import com.jotaro.client.netty.testservice.User;
import com.jotaro.client.netty.testservice.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @description:
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/23 01:47
 */
@Slf4j
public class TestNetty {

    @Test
    public void testNetty(){
        NettyClientProxy proxy = new NettyClientProxy("127.0.0.1",9999);
        UserService userService = proxy.getProxy(UserService.class);
        User user = userService.getUserById(1);
        log.info("user:{}", JSON.toJSONString(user));
    }

}
