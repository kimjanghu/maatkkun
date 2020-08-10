package com.web.blog.controller.account;



import com.web.blog.model.SocketVO;
import com.web.blog.service.PostServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class SocketController {
    
    @Autowired
    RedisTemplate<String, String> redisTemplate;



    // receive를 메시지를 받을 endpoint로 설정합니다.
    @MessageMapping("/receive")
    
    // send로 메시지를 반환합니다.
    @SendTo("/send")

    


    
    // SocketHandler는 1) /receive에서 메시지를 받고, /send로 메시지를 보내줍니다.
    // 정의한 SocketVO를 1) 인자값, 2) 반환값으로 사용합니다.
    public SocketVO SocketHandler(SocketVO socketVO) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        


        // vo에서 getter로 userName을 가져옵니다.
        String userName = socketVO.getUserName();
        // vo에서 setter로 content를 가져옵니다.
        String content = socketVO.getContent();

        vop.set("userName", userName);
        System.out.println(vop.get("userName"));
        
        // 생성자로 반환값을 생성합니다.
        SocketVO result = new SocketVO(userName, content);
        // 반환
        return result;
    }
}