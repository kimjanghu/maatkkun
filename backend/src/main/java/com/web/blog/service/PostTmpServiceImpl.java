package com.web.blog.service;

import java.util.List;

import com.web.blog.dao.user.PostTmpDAO;
import com.web.blog.model.user.Post;
import com.web.blog.model.user.PostTmp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostTmpServiceImpl implements PostTmpService {
    @Autowired
    PostTmpDAO posttmpdao;

    @Override
    public List<Post> getList(int userid) {
        return posttmpdao.getList(userid);
    }

    @Override
    public int delete(int postId) {
        return posttmpdao.delete(postId);
    }

    @Override
    public Post showArticle(int postId) {
        return posttmpdao.showArticle(postId);
    }

    @Override
    public int getTopNum() {
        return posttmpdao.getTopNum();
    }

    @Override
    public int register(Post post) {
        return posttmpdao.register(post);
    }
}