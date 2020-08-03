package com.web.blog.service;

import java.util.List;

import com.web.blog.model.user.Photo;

public interface PhotoService {
    List<Photo> getList();
    int register(Photo photo);
    int delete(Photo photo);
    int like(Photo photo);
    
}