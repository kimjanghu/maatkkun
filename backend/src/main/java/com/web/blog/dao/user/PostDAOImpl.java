package com.web.blog.dao.user;

import java.util.HashMap;
import java.util.List;


import com.web.blog.model.user.Post;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class PostDAOImpl implements PostDAO {
    @Autowired
    SqlSession sqlSession;

    @Override
    public Post getPost(int postId) {
        return sqlSession.selectOne("postMapper.getPost", postId);
    }

    @Override
    public List<Post> getList() {
        return sqlSession.selectList("postMapper.getList");
    }

    @Override
    public int register(Post post) {
        return sqlSession.insert("postMapper.register",post);
    }

    @Override
    public int deletePost(int postId) {
        return sqlSession.delete("postMapper.delete",postId);
    }

    @Override
    public Post showArticle(int postId) {
        return sqlSession.selectOne("postMapper.selectOne",postId);
    }

    @Override
    public int updateLikes(int postId, int status) {
        HashMap<String, Integer> hm = new HashMap<String, Integer>();
        hm.put("postId", postId);
        hm.put("status", status);
        return sqlSession.update("postMapper.updateLikes", hm);
    }

    @Override
    public int modify(Post post) {
        return sqlSession.update("postMapper.modifyPost",post);
    }
    
    @Override
    public int getTopNum() {
        return sqlSession.selectOne("postMapper.getTopNum");
    }

    @Override
    public List<Post> searchArticle(String keyword) {
        return sqlSession.selectList("postMapper.searchArticle","%"+keyword+"%");
    }

    @Override
    public List<Post> getPostedList(int userId) {
        return sqlSession.selectList("postMapper.getPostedList", userId);
    }

    @Override
    public List<Post> getPostedListByLikes() {
        return sqlSession.selectList("postMapper.getPostedListByLikes");
    }

    @Override
    public List<Post> getPostedListByHits() {
        return sqlSession.selectList("postMapper.getPostedListByHits");

    }

    @Override
    public void upHit(int postId) {
        sqlSession.update("postMapper.upHit",postId);
    }

    
}