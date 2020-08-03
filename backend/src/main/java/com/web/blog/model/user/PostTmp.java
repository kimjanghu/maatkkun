package com.web.blog.model.user;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostTmp {
    private int postId;
    private int userid;
    private String title;
    private String lat;
    private String lon;
    private String content;
    private String hashtag;
    private String address;
    private int likes;
    private LocalDateTime createDate; 
}