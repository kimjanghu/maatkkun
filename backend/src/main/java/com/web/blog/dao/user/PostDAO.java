package com.web.blog.dao.user;

import java.util.List;

import com.web.blog.model.user.Post;

public interface PostDAO {
    Post getPost(int postId);
    List<Post> getList();
    int register(Post post);
    int modify(Post post);
    int deletePost(int postId);
    Post showArticle(int postId);  
    int getTopNum();
    int updateLikes(int postID, int status);
    List<Post> searchArticle(String keyword);
    List<Post> getPostedList(int userId);
}