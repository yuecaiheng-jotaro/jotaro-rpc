package com.jotaro.client.netty;

import com.alibaba.fastjson2.JSON;
import com.jotaro.api.User;
import com.jotaro.api.UserService;
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
        NettyClientProxy proxy = new NettyClientProxy();
        UserService userService = proxy.getProxy(UserService.class);
        User user = userService.getUserById(1);
        System.out.println("user:{}" + JSON.toJSONString(user));
        log.info("user:{}", JSON.toJSONString(user));
    }

}
