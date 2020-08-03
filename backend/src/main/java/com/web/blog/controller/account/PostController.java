package com.web.blog.controller.account;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import javax.xml.bind.DatatypeConverter;

import com.web.blog.model.BasicResponse;
import com.web.blog.model.user.Photo;
import com.web.blog.model.user.Post;
import com.web.blog.model.user.User;
import com.web.blog.service.CommentServiceImpl;
import com.web.blog.service.PhotoServiceImpl;
import com.web.blog.service.PostServiceImpl;
import com.web.blog.service.UserServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import org.jsoup.Jsoup; 
import org.jsoup.nodes.Document; 
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;





@ApiResponses(value = { @ApiResponse(code = 401, message = "Unauthorized", response = BasicResponse.class),
        @ApiResponse(code = 403, message = "Forbidden", response = BasicResponse.class),
        @ApiResponse(code = 404, message = "Not Found", response = BasicResponse.class),
        @ApiResponse(code = 500, message = "Failure", response = BasicResponse.class) })


@CrossOrigin(origins = { "*" },maxAge = 6000)
@RestController
public class PostController {
    @Autowired
    PostServiceImpl service;

    @Autowired
    PhotoServiceImpl photoservice;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    CommentServiceImpl commentservice;

    //window 환경
    final String path = "C:\\Users\\images\\";
    //ec2 환경
    // final String path = "/home/ubuntu/images/";
    
    // @ApiOperation(value = "업데이트", notes = "회원정보 업데이트 API")
    // @ApiImplicitParams({
    //         @ApiImplicitParam(name = "uid", value = "식별자", required = true, dataType = "string" ),
    //         @ApiImplicitParam(name = "password", value = "비밀번호", dataType = "string"),
    //         @ApiImplicitParam(name = "email", value = "이메일" , dataType = "string"),
    //         @ApiImplicitParam(name = "nickname", value = "별칭", dataType = "string"),
    //         @ApiImplicitParam(name = "content", value = "자기소개", dataType = "string"),
    //         @ApiImplicitParam(name = "createDate", value = "생성일", dataType = "Date", defaultValue = "현재시간"),
    // })


    @ApiOperation(value = "글 리스트", notes = "글 리스트 API")
    @GetMapping(value="/articles/list")
    public ResponseEntity<Object> getList(){

        HashMap<String,Object> hm = new HashMap<>();
        
        List<Post> postList = service.getList();
        List<Integer> commentList = new ArrayList<>();
        
        for(Post post : postList){
            commentList.add(commentservice.countComment(post.getPostId()));


            Document doc = Jsoup.parseBodyFragment(post.getContent());
            Element body = doc.body();
            Elements dd = doc.select("img");
            

            if(dd.size() > 0){
                Element element = dd.get(0);
                String id = element.attr("id");
                
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
                    post.setContent("data:image/jpeg;base64, "+new String(baseIncodingBytes));

                    baos.close();
                    fis.close();
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                }
            }
            else{
                post.setContent(null);
            }
        }
        
        hm.put("comment", commentList);
        hm.put("list", postList);

        return new ResponseEntity<>(hm,HttpStatus.OK) ;
    }

    @ApiOperation(value = "글 작성", notes = "글 작성 API")
    @PostMapping(value="/articles/register")
    @ApiImplicitParams({
                // @ApiImplicitParam(name = "uid", value = "식별자", required = true, dataType = "string" ),
                // @ApiImplicitParam(name = "password", value = "비밀번호", dataType = "string"),
                // @ApiImplicitParam(name = "email", value = "이메일" , dataType = "string"),
                // @ApiImplicitParam(name = "nickname", value = "별칭", dataType = "string"),
                // @ApiImplicitParam(name = "content", value = "자기소개", dataType = "string"),
                // @ApiImplicitParam(name = "createDate", value = "생성일", dataType = "Date", defaultValue = "현재시간"),
        })
    public ResponseEntity<Object> register(@Valid @RequestBody Post post){

        post.setNickname(userService.getUser(post.getUserid()).getNickname());

        ArrayList<String> srcAr = new ArrayList<String>();

        Document doc = Jsoup.parseBodyFragment(post.getContent());
        Element body = doc.body();
        int topNum = service.getTopNum();
        

        Elements dd = doc.select("img");
        for(int i = 0;i < dd.size() ; i++){
            Element element = dd.get(i);
            String filename = Integer.toString(topNum)+Integer.toString(post.getUserid())+Integer.toString(i);
            srcAr.add(element.attr("src"));
            element.attr("src","");
            element.attr("id",filename);

        }
        post.setContent(body.html());

        if(service.register(post)>0){
            for(int i = 0;i < srcAr.size() ; i++){

                String filename = Integer.toString(topNum)+Integer.toString(post.getUserid())+Integer.toString(i);
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

    @ApiOperation(value = "글 수정", notes = "글 수정 API")
    @PutMapping(value="/articles/modify")
    @ApiImplicitParams({
        // @ApiImplicitParam(name = "uid", value = "식별자", required = true, dataType = "string" ),
        // @ApiImplicitParam(name = "password", value = "비밀번호", dataType = "string"),
        // @ApiImplicitParam(name = "email", value = "이메일" , dataType = "string"),
        // @ApiImplicitParam(name = "nickname", value = "별칭", dataType = "string"),
        // @ApiImplicitParam(name = "content", value = "자기소개", dataType = "string"),
        // @ApiImplicitParam(name = "createDate", value = "생성일", dataType = "Date", defaultValue = "현재시간"),
    })
    public ResponseEntity<Object> modify(@Valid @RequestBody Post post){
        int topNum = post.getPostId();
        ArrayList<String> srcAr = new ArrayList<String>();
        Document doc = Jsoup.parseBodyFragment(post.getContent());
        Element body = doc.body();
        Elements dd = doc.select("img");
        for(int i = 0;i < dd.size() ; i++){
            Element element = dd.get(i);
            String filename = Integer.toString(topNum)+Integer.toString(post.getUserid())+Integer.toString(i);
            srcAr.add(element.attr("src"));
            element.attr("src","");
            element.attr("id",filename);
        }
        post.setContent(body.html());

        if(service.modify(post)>0){
            for(int i = 0;i < srcAr.size() ; i++){
                String filename = Integer.toString(topNum)+Integer.toString(post.getUserid())+Integer.toString(i);
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
            return new ResponseEntity<>("success",HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("fail",HttpStatus.BAD_REQUEST);
        }
    }


    @ApiOperation(value = "글 상세 조회", notes = "글 상세 조회 API")
    @GetMapping(value="/articles/showArticle")
    @ApiImplicitParams({
    })
    public ResponseEntity<Object> showArticle(@RequestParam(required = true) final int postId){
        Post post = service.showArticle(postId);
        service.upHit(postId);

        Document doc = Jsoup.parseBodyFragment(post.getContent());
        Element body = doc.body();
        Elements dd = doc.select("img");
        
        for(int i = 0;i < dd.size() ; i++){
            Element element = dd.get(i);
            String id = element.attr("id");
            
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

                baos.close();
                fis.close();
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
            return new ResponseEntity<>("조회 실패",HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "글 삭제", notes = "글 삭제 API")
    @DeleteMapping(value="/articles/dropArticle")
    @ApiImplicitParams({
                // @ApiImplicitParam(name = "uid", value = "식별자", required = true, dataType = "string" ),
                // @ApiImplicitParam(name = "password", value = "비밀번호", dataType = "string"),
                // @ApiImplicitParam(name = "email", value = "이메일" , dataType = "string"),
                // @ApiImplicitParam(name = "nickname", value = "별칭", dataType = "string"),
                // @ApiImplicitParam(name = "content", value = "자기소개", dataType = "string"),
                // @ApiImplicitParam(name = "createDate", value = "생성일", dataType = "Date", defaultValue = "현재시간"),
        })
    public ResponseEntity<Object> dropArticle(@RequestParam(required = true) final int postId){
        Post post = service.showArticle(postId);
        System.out.println("글 삭제");
        Document doc = Jsoup.parseBodyFragment(post.getContent());
        Element body = doc.body();
        Elements dd = doc.select("img");
        
        for(int i = 0;i < dd.size() ; i++){
            Element element = dd.get(i);
            String id = element.attr("id");
            File imagePath = new File(path+id+".jpg");
            imagePath.setReadable(false);
            System.out.println(imagePath);
            System.out.println(imagePath.canWrite());
            
            if(imagePath.exists()){

                if(imagePath.isDirectory()){
                    System.out.println("디렉토리 입니다.");
                }
                else{
                    System.out.println("디렉토리가 아닙니다..");
                }

                System.out.println("파일이 존재합니다.");
                if(imagePath.delete()){
                    System.out.println("파일삭제 성공"); 
                }
                else{
                    System.out.println("파일삭제 실패"); 
                    } 
                }
            else
            {
                System.out.println("파일이 존재하지 않습니다."); 
            }
        }

        if(service.deletePost(postId)>0){
            return new ResponseEntity<>("삭제 성공",HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("삭제 실패",HttpStatus.NOT_FOUND);
        }

    }

    @ApiOperation(value = "글 좋아요", notes = "글 좋아요 API")
    @PostMapping(value="/articles/like")
    @ApiImplicitParams({
        // @ApiImplicitParam(name = "uid", value = "식별자", required = true, dataType = "string" ),
        // @ApiImplicitParam(name = "password", value = "비밀번호", dataType = "string"),
        // @ApiImplicitParam(name = "email", value = "이메일" , dataType = "string"),
        // @ApiImplicitParam(name = "nickname", value = "별칭", dataType = "string"),
        // @ApiImplicitParam(name = "content", value = "자기소개", dataType = "string"),
        // @ApiImplicitParam(name = "createDate", value = "생성일", dataType = "Date", defaultValue = "현재시간"),
    })
    public ResponseEntity<Object> likeArticle(@Valid @RequestBody Post request){
        int postId = request.getPostId();
        int userId = request.getUserid();
        User user = userService.getUser(userId);
        String likedPostList = user.getLikedpost();
        if(likedPostList == null) likedPostList = "";
        String pid = Integer.toString(postId);
        int idx = likedPostList.indexOf(pid);
        if(!likedPostList.contains(Integer.toString(postId))){ // 좋아요
            if(service.updateLikes(postId, 1)>0){
                likedPostList += pid + ",";
                user.setLikedpost(likedPostList);
                userService.updateUser(user);
                System.out.println("좋아요 추가");
                return new ResponseEntity<>("success",HttpStatus.OK);
            } else {
                return new ResponseEntity<>("fail",HttpStatus.BAD_REQUEST);
            }
        } else { // 좋아요 취소
            if(service.updateLikes(postId, -1)>0){
                likedPostList = likedPostList.substring(0, idx)
                            + likedPostList.substring(idx + pid.length() + 1, likedPostList.length());
                user.setLikedpost(likedPostList);
                userService.updateUser(user);
                System.out.println("좋아요 취소");
                return new ResponseEntity<>("success",HttpStatus.OK);
            } else {
                return new ResponseEntity<>("fail",HttpStatus.BAD_REQUEST);

            }
        }
        
}

    @ApiOperation(value = "좋아요 게시물 리스트", notes = "좋아요 게시물 리스트 API")
    @PostMapping(value="/articles/likedList")
    @ApiImplicitParams({
        // @ApiImplicitParam(name = "uid", value = "식별자", required = true, dataType = "string" ),
        // @ApiImplicitParam(name = "password", value = "비밀번호", dataType = "string"),
        // @ApiImplicitParam(name = "email", value = "이메일" , dataType = "string"),
        // @ApiImplicitParam(name = "nickname", value = "별칭", dataType = "string"),
        // @ApiImplicitParam(name = "content", value = "자기소개", dataType = "string"),
        // @ApiImplicitParam(name = "createDate", value = "생성일", dataType = "Date", defaultValue = "현재시간"),
    })
    public ResponseEntity<Object> getLikedList(@Valid @RequestBody User request){
        User user = userService.getUser(Integer.parseInt(request.getUid()));
        String likedpost = user.getLikedpost();

        String[] idList = likedpost.split(",");
        
        
        List<Post> likedPostList = new ArrayList<Post>();
        for(String s : idList){
            Post post = service.getPost(Integer.parseInt(s));
            if(post == null){ // deleted post일 경우
                // user 데이터 업데이트
                int idx = likedpost.indexOf(s);
                likedpost = likedpost.substring(0, idx)
                            + likedpost.substring(idx + s.length() + 1, likedpost.length());
                user.setLikedpost(likedpost);
                userService.updateUser(user);
            } else { // 아닐 경우
                likedPostList.add(post);
            }
        }

        if(likedPostList.isEmpty()) { // 비어있을때
            return new ResponseEntity<>(null, HttpStatus.OK);
        } else { // 비어있지 않을때 
            return new ResponseEntity<>(likedPostList, HttpStatus.OK);
        }
    }

    @ApiOperation(value = "작성 게시물 리스트", notes = "작성 게시물 리스트 API")
    @PostMapping(value="/articles/postedList")
    @ApiImplicitParams({
        // @ApiImplicitParam(name = "uid", value = "식별자", required = true, dataType = "string" ),
        // @ApiImplicitParam(name = "password", value = "비밀번호", dataType = "string"),
        // @ApiImplicitParam(name = "email", value = "이메일" , dataType = "string"),
        // @ApiImplicitParam(name = "nickname", value = "별칭", dataType = "string"),
        // @ApiImplicitParam(name = "content", value = "자기소개", dataType = "string"),
        // @ApiImplicitParam(name = "createDate", value = "생성일", dataType = "Date", defaultValue = "현재시간"),
    })
    public ResponseEntity<Object> getPostedList(@Valid @RequestBody User request){
        List<Post> postedList = service.getPostedList(Integer.parseInt(request.getUid()));
        if(postedList.isEmpty()) { // 비어있을때
            return new ResponseEntity<>(null, HttpStatus.OK);
        } else { // 비어있지 않을때 
            return new ResponseEntity<>(postedList, HttpStatus.OK);
        }
    }

    @ApiOperation(value = "기본 검색", notes = "기본 검색 API")
    @GetMapping(value="/articles/searchArticle/{keyword}")
    @ApiImplicitParams({
    })
    public ResponseEntity<Object> searchArticle(@PathVariable String keyword){
        
        HashMap<String,Object> hm = new HashMap<>();
        
        List<Integer> commentList = new ArrayList<>();

        List<Post> list = service.searchArticle(keyword);        
        if(!list.isEmpty()){
            for(Post post : list){

                commentList.add(commentservice.countComment(post.getPostId()));

                Document doc = Jsoup.parseBodyFragment(post.getContent());
                Element body = doc.body();
                Elements dd = doc.select("img");
                

                if(dd.size() > 0){
                    Element element = dd.get(0);
                    String id = element.attr("id");
                    
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
                        post.setContent("data:image/jpeg;base64, "+new String(baseIncodingBytes));

                        baos.close();
                        fis.close();
                    }
                    catch(Exception e){
                        System.out.println(e.getMessage());
                    }
                }
            }
            hm.put("comment", commentList);
            hm.put("list", list);
            return new ResponseEntity<>(hm, HttpStatus.OK);
        }   
        else{
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        
    }


}