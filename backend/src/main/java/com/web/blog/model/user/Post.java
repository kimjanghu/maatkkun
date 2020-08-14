package com.web.blog.model.user;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Post {
    private int postId;
    private int userid;
    private String title;
    private String lat;
    private String lon;
    private String content;
    private String hashtag;
    private String address;
    private int likes;
    private String createDate; 
    private String nickname;
    private int hits;
    private String url;
    private String starpoint;
    private String placename;
    private String price;
    private String taste;
    private String atmosphere;
    private String menu;
}