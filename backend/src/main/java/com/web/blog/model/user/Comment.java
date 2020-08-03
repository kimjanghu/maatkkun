package com.web.blog.model.user;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Comment {
    private int commentid;
    private int postid;
    private String content;
    private String createDate; 
    private int userid;
    private int parentid;
    private String nickname;
}