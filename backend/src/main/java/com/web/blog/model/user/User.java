package com.web.blog.model.user;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User {
    private String uid;
    private String password;
    private String passwordConfirm;
    private String email;
    private String nickname;
    private String content;
    private LocalDateTime createDate; 
    private String likedpost;
}
