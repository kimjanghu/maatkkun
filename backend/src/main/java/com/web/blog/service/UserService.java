package com.web.blog.service;

import java.util.List;

import com.web.blog.model.user.User;

public interface UserService {
    User login(String email,String passwd);
    int join(User user);
    int deleteUser(int uid);
    int updateUser(User user);
    List<User> findAllUser();
    User getUser(int uid);
}