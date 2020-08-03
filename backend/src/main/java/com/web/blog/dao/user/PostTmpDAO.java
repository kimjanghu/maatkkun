package com.web.blog.dao.user;

import java.util.List;

import com.web.blog.model.user.Post;
import com.web.blog.model.user.PostTmp;

public interface PostTmpDAO {
    List<Post> getList(int userid);
    int delete(int postId);
    Post showArticle(int postId);
    int getTopNum();
    int register(Post post);
}