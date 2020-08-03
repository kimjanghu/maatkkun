package com.web.blog.service;

import java.util.List;

import com.web.blog.dao.user.PostDAO;
import com.web.blog.model.user.Post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {


    @Autowired
    PostDAO postdao;

    @Override
    public Post getPost(int postId) {
        return postdao.getPost(postId);
    }

    @Override
    public List<Post> getList() {
        return postdao.getList();
    }

    @Override
    public int register(Post post) {
        return postdao.register(post);
    }

    @Override
    public int deletePost(int postId) {
        return postdao.deletePost(postId);
    }

    @Override
    public Post showArticle(int postId) {
        return postdao.showArticle(postId);
    }

    @Override
    public int updateLikes(int postId, int status) {
        return postdao.updateLikes(postId, status);
    }

    @Override
    public int modify(Post post) {
        return postdao.modify(post);
    }
    @Override
    public int getTopNum() {
        return postdao.getTopNum();
    }

    @Override
    public List<Post> searchArticle(String keyword) {
        return postdao.searchArticle(keyword);
    }

    @Override
    public List<Post> getPostedList(int userId) {
        return postdao.getPostedList(userId);
    }

    @Override
    public void upHit(int postId) {
        postdao.upHit(postId);
    }
}