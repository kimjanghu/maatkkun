package com.web.blog.dao.user;

import java.util.List;

import com.web.blog.model.user.Comment;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class CommentDAOImpl implements CommentDAO {

    @Autowired
    SqlSession sqlsession;

    @Override
    public List<Comment> getList(int postid) {
        return sqlsession.selectList("commentMapper.getList",postid);
    }

    @Override
    public int register(Comment comment) {
        return sqlsession.insert("commentMapper.register", comment);
    }

    @Override
    public int modifyComment(Comment comment) {
        return sqlsession.update("commentMapper.modifyComment", comment);
    }

    @Override
    public int deleteComment(int commentId) {
        return sqlsession.delete("commentMapper.deleteComment", commentId);
    }

    @Override
    public int countComment(int commentid) {
        return sqlsession.selectOne("commentMapper.countComment",commentid);
    }
    
}