package com.web.blog.dao.user;

import java.util.List;

import com.web.blog.model.user.Comment;

public interface CommentDAO {
    List<Comment> getList(int postid);
    int register(Comment comment);
    int modifyComment(Comment comment);
    int deleteComment(int commentId);
    int countComment(int commentid);
}