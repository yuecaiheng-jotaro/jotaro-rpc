package com.jotaro.api;

/**
 * @description:
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/23 13:30
 */
public class UserServiceImpl implements UserService{

    @Override
    public User getUserById(final Integer id) {
        return User.builder().id(1).name("test").build();
    }
}
