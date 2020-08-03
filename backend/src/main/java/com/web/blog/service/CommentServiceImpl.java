package com.web.blog.service;

import java.util.List;

import com.web.blog.dao.user.CommentDAO;
import com.web.blog.model.user.Comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentDAO dao;


    @Override
    public List<Comment> getList(int postid) {
        return dao.getList(postid);
    }

    @Override
    public int register(Comment comment) {
        return dao.register(comment);
    }

    @Override
    public int modifyComment(Comment comment) {
        return dao.modifyComment(comment);
    }

    @Override
    public int deleteComment(int commentId) {
        return dao.deleteComment(commentId);
    }

    @Override
    public int countComment(int commentid) {
        return dao.countComment(commentid);
    }
    
}