package com.web.blog.model.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Photo {
    private int photoid;
    private int postid;
    private String src;
    private int likes;
}