package com.web.blog.service;

import java.util.List;
import java.util.Optional;

import com.web.blog.dao.user.UserDAO;
import com.web.blog.model.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDAO userdao;

    @Override
    public User login(String email, String passwd) {
        return userdao.login(email, passwd);
    }

    @Override
    public List<User> findAllUser() {
        return userdao.findAllUser();
    }

    @Override
    public int join(User user) {
        return userdao.join(user);
    }

    @Override
    public int deleteUser(int uid) {
        return userdao.deleteUser(uid);
    }

    @Override
    public int updateUser(User user) {
        return userdao.updateUser(user);
    }

    @Override
    public User getUser(int uid) {
        return userdao.getUser(uid);
    }
}