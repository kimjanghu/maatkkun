package com.web.blog.controller.account;



import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

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
    RedisTemplate<String, Integer> redisTemplate;



    // receive를 메시지를 받을 endpoint로 설정합니다.
    @MessageMapping("/receive")
    
    // send로 메시지를 반환합니다.
    @SendTo("/send")

    


    
    // SocketHandler는 1) /receive에서 메시지를 받고, /send로 메시지를 보내줍니다.
    // 정의한 SocketVO를 1) 인자값, 2) 반환값으로 사용합니다.
    public HashMap<String,Integer> SocketHandler(SocketVO socketVO) {
        ValueOperations<String, Integer> vop = redisTemplate.opsForValue();
        // vop.set("test", value);

        

        String postid = socketVO.getPostId();
        String status = socketVO.getStatus();

        if(status.equals("in")){
            System.out.println("in");
            vop.set(postid, vop.get(postid) + 1);
        }
        else if(status.equals("out")){
            System.out.println("out");
            if(vop.get(postid) > 0){
                vop.set(postid, vop.get(postid) - 1);
            }
        }
        else if(status.equals("list")){
            System.out.println("list");
        }
        HashMap<String,Integer> hm = new HashMap<>();

        Set<String> set = redisTemplate.keys("*");
        Iterator<String> it = set.iterator();
        while(it.hasNext()){
            String id = it.next();
            hm.put(id, vop.get(id));
        }

        
        return hm;
    }
}