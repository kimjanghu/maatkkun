package com.web.blog.dao.user;

import java.util.List;

import com.web.blog.model.user.Photo;

public interface PhotoDAO {
    List<Photo> getList();
    int register(Photo photo);
    int delete(Photo Photo);    
    int like(Photo photo);
}