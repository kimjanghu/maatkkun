package com.web.blog.dao.user;

import java.util.List;

import com.web.blog.model.user.User;

public interface UserDAO {
    User login(String email,String passwd);
    int join(User user);
    int deleteUser(int uid);
    int updateUser(User user);
    List<User> findAllUser();
    User getUser(int uid);
}