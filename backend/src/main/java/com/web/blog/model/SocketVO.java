package com.web.blog.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SocketVO {
    // 유저의 이름을 저장하기 위한 변수
    private String postId;

    // 메시지의 내용을 저장하기 위한 변수
    private String status;
}