package com.web.blog.controller.account;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.validation.Valid;

import com.web.blog.model.BasicResponse;
import com.web.blog.model.user.Photo;
import com.web.blog.model.user.Post;
import com.web.blog.model.user.PostTmp;
import com.web.blog.service.PhotoServiceImpl;
import com.web.blog.service.PostTmpServiceImpl;
import com.web.blog.service.UserServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup; 
import org.jsoup.nodes.Document; 
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import javax.xml.bind.DatatypeConverter;


@ApiResponses(value = { @ApiResponse(code = 401, message = "Unauthorized", response = BasicResponse.class),
        @ApiResponse(code = 403, message = "Forbidden", response = BasicResponse.class),
        @ApiResponse(code = 404, message = "Not Found", response = BasicResponse.class),
        @ApiResponse(code = 500, message = "Failure", response = BasicResponse.class) })


@CrossOrigin(origins = { "*" },maxAge = 6000)
@RestController
public class PostTmpController {
    
    //window 환경
    // final String path = "C:\\Users\\images\\";
    //ec2 환경
    final String path = "/home/ubuntu/images/";

    @Autowired
    PostTmpServiceImpl service;

    @Autowired
    PhotoServiceImpl photoservice;

    @Autowired
    UserServiceImpl userService;


    @ApiOperation(value = "임시 글 작성", notes = "임시 글 작성 API")
    @PostMapping(value="/subarticles/register")
    @ApiImplicitParams({
        })
    public ResponseEntity<Object> register(@Valid @RequestBody Post post){
        System.out.println("임시 글 작성");
        post.setNickname(userService.getUser(post.getUserid()).getNickname());

        ArrayList<String> srcAr = new ArrayList<String>();

        Document doc = Jsoup.parseBodyFragment(post.getContent());
        Element body = doc.body();
        int topNum = service.getTopNum();
        System.out.println(topNum);

        Elements dd = doc.select("img");
        for(int i = 0;i < dd.size() ; i++){
            Element element = dd.get(i);
            String filename = "tmp"+Integer.toString(topNum)+Integer.toString(post.getUserid())+Integer.toString(i);
            srcAr.add(element.attr("src"));
            element.attr("src","");
            element.attr("id",filename);

        }
        post.setContent(body.html());

        if(service.register(post)>0){
            for(int i = 0;i < srcAr.size() ; i++){

                String filename = "tmp"+Integer.toString(topNum)+Integer.toString(post.getUserid())+Integer.toString(i);
                String base64 = srcAr.get(i);
                String data = base64.split(",")[1];
                

                byte[] imageBytes = DatatypeConverter.parseBase64Binary(data);

                try {
                    
                    ImageIO.write(ImageIO.read(new ByteArrayInputStream(imageBytes)), "jpg", new File(path+filename+".jpg"));

                    Photo photo = new Photo();
                    photo.setPostid(topNum);
                    photo.setSrc(path+filename+".jpg");
                    photoservice.register(photo);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
                return new ResponseEntity<>("글작성 성공",HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("글작성 실패",HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/subarticles/list/{userid}")
    @ApiOperation(value = "유저별 임시저장 글 목록", notes = "유저별 임시저장 글 목록 API")
    public ResponseEntity<Object> getList(@PathVariable int userid){
        List<Post> list = service.getList(userid);
        if(!list.isEmpty()){
            return new ResponseEntity<>(list, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("fail", HttpStatus.OK);
        }
        
    }



    @ApiOperation(value = "임시저장 글 삭제", notes = "임시저장 글 삭제 API")
    @DeleteMapping(value="/subarticles/dropSubarticle")
    @ApiImplicitParams({
        })
    public ResponseEntity<Object> dropArticle(@RequestParam(required = true) final int postId){
        if(service.delete(postId)>0){
            return new ResponseEntity<>("임시저장 글 삭제 성공",HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("임시저장 글 삭제 실패",HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "임시저장 글 상세 조회", notes = "임시저장 글 상세 조회 API")
    @GetMapping(value="/subarticles/detail/{postid}")
    @ApiImplicitParams({
    })
    public ResponseEntity<Object> showArticle(@PathVariable int postid){


        System.out.println("임시 글 상세조회");
        System.out.println(path);
        Post post = service.showArticle(postid);
        System.out.println(post);

        Document doc = Jsoup.parseBodyFragment(post.getContent());
        Element body = doc.body();
        Elements dd = doc.select("img");
        
        for(int i = 0;i < dd.size() ; i++){
            Element element = dd.get(i);
            String id = element.attr("id");
            System.out.println(id);
            
            File imagePath = new File(path+id+".jpg");
            
            FileInputStream fis = null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len = 0;
            byte[] buf = new byte[1024];

            try{
                fis = new FileInputStream(imagePath);

                while((len = fis.read(buf)) != -1){
                    baos.write(buf,0,len);
                }

                byte[] fileArray = baos.toByteArray();
                byte[] baseIncodingBytes = Base64.encodeBase64(fileArray);
                element.attr("src","data:image/jpeg;base64, "+new String(baseIncodingBytes));
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
        post.setContent(body.html());
        Optional<Post> optPost = Optional.of(post);

        if(optPost.isPresent()){
            return new ResponseEntity<>(post,HttpStatus.OK);            
        }
        else{
            return new ResponseEntity<>("fail",HttpStatus.NOT_FOUND);
        }
    }
}