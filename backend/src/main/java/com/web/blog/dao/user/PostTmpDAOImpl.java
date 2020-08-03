package com.web.blog.dao.user;

import java.util.List;

import com.web.blog.model.user.Post;
import com.web.blog.model.user.PostTmp;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PostTmpDAOImpl implements PostTmpDAO {
    @Autowired
    SqlSession sqlsession;

    @Override
    public List<Post> getList(int userid) {
        return sqlsession.selectList("posttmpMapper.getList",userid);
    }

    @Override
    public int delete(int postId) {
        return sqlsession.delete("posttmpMapper.delete",postId);
    }

    @Override
    public Post showArticle(int postId) {
        return sqlsession.selectOne("posttmpMapper.selectOne", postId);
    }

    @Override
    public int getTopNum() {
        return sqlsession.selectOne("posttmpMapper.getTopNum");
    }

    @Override
    public int register(Post post) {
        return sqlsession.insert("posttmpMapper.register",post);
    }
}