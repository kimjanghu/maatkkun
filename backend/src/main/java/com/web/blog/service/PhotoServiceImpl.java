package com.web.blog.service;

import java.util.List;

import com.web.blog.dao.user.PhotoDAO;
import com.web.blog.model.user.Photo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhotoServiceImpl implements PhotoService {

    @Autowired
    PhotoDAO dao;

    @Override
    public List<Photo> getList() {
        return dao.getList();
    }

    @Override
    public int register(Photo photo) {
        return dao.register(photo);
    }

    @Override
    public int delete(Photo photo) {
        return dao.delete(photo);
    }

    @Override
    public int like(Photo photo) {
        return dao.like(photo);
    }

    
    
}