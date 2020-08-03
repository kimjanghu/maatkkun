package com.web.blog.service;

import java.util.List;


import com.web.blog.model.user.Post;

public interface PostService {
    Post getPost(int postId);
    List<Post> getList();
    int register(Post post);
    int modify(Post post);
    int deletePost(int postId);
    Post showArticle(int postId);
    int getTopNum();
    int updateLikes(int postId, int status);
    List<Post> searchArticle(String keyword);
    List<Post> getPostedList(int userId);
}   