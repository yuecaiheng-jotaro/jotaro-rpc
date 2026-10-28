package com.jotaro.pojo.conn;

import lombok.Builder;
import lombok.Data;

/**
 * @description: ip:port包装类
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/24 21:43
 */
@Data
@Builder
public class ConnAddress {

    private String host;

    private int port;

}
