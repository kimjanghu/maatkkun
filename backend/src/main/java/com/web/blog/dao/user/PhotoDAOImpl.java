package com.web.blog.dao.user;

import java.util.List;

import com.web.blog.model.user.Photo;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PhotoDAOImpl implements PhotoDAO {

    @Autowired
    SqlSession sqlsession;

    @Override
    public List<Photo> getList() {
        return sqlsession.selectList("photoMapper.getList");
    }

    @Override
    public int register(Photo photo) {
        return sqlsession.insert("photoMapper.register",photo);
    }

    @Override
    public int delete(Photo photo) {
        return sqlsession.delete("PhotoMapper.delte", photo);
    }

    @Override
    public int like(Photo photo) {
        return sqlsession.update("photoMapper.like", photo);
    }

    
}