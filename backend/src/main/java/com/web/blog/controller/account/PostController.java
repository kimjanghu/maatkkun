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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.Map.Entry;

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
import lombok.Getter;
import lombok.Setter;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import org.jsoup.Jsoup;
import org.jsoup.Connection.KeyVal;
import org.jsoup.nodes.Document; 
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;





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

        final HashMap<String,Object> hm = new HashMap<>();
        
        final List<Post> postList = service.getList();
        final List<Integer> commentList = new ArrayList<>();
        
        for(final Post post : postList){
            commentList.add(commentservice.countComment(post.getPostId()));


            final Document doc = Jsoup.parseBodyFragment(post.getContent());
            final Elements dd = doc.select("img");
            

            if(dd.size() > 0){
                final Element element = dd.get(0);
                final String id = element.attr("id");
                post.setContent(idParseImage(id));       
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
    public ResponseEntity<Object> register(@Valid @RequestBody final Post post){

        post.setStarpoint(crawling(post.getUrl()));

        post.setNickname(userService.getUser(post.getUserid()).getNickname());

        final ArrayList<String> srcAr = new ArrayList<String>();

        final Document doc = Jsoup.parseBodyFragment(post.getContent());
        final Element body = doc.body();
        final int topNum = service.getTopNum();
        

        final Elements dd = doc.select("img");
        for(int i = 0;i < dd.size() ; i++){
            final Element element = dd.get(i);
            final String filename = Integer.toString(topNum)+Integer.toString(post.getUserid())+Integer.toString(i);
            srcAr.add(element.attr("src"));
            element.attr("src","");
            element.attr("id",filename);

        }
        post.setContent(body.html());

        if(service.register(post)>0){
            for(int i = 0;i < srcAr.size() ; i++){

                final String filename = Integer.toString(topNum)+Integer.toString(post.getUserid())+Integer.toString(i);
                final String base64 = srcAr.get(i);
                final String data = base64.split(",")[1];
                

                final byte[] imageBytes = DatatypeConverter.parseBase64Binary(data);

                try {
                    
                    ImageIO.write(ImageIO.read(new ByteArrayInputStream(imageBytes)), "jpg", new File(path+filename+".jpg"));
                    
                    final Photo photo = new Photo();
                    photo.setPostid(topNum);
                    photo.setSrc(path+filename+".jpg");
                    photoservice.register(photo);

                } catch (final IOException e) {
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
    public ResponseEntity<Object> modify(@Valid @RequestBody final Post post){
        final int topNum = post.getPostId();
        final ArrayList<String> srcAr = new ArrayList<String>();
        final Document doc = Jsoup.parseBodyFragment(post.getContent());
        final Element body = doc.body();
        final Elements dd = doc.select("img");
        for(int i = 0;i < dd.size() ; i++){
            final Element element = dd.get(i);
            final String filename = Integer.toString(topNum)+Integer.toString(post.getUserid())+Integer.toString(i);
            srcAr.add(element.attr("src"));
            element.attr("src","");
            element.attr("id",filename);
        }
        post.setContent(body.html());

        if(service.modify(post)>0){
            for(int i = 0;i < srcAr.size() ; i++){
                final String filename = Integer.toString(topNum)+Integer.toString(post.getUserid())+Integer.toString(i);
                final String base64 = srcAr.get(i);
                final String data = base64.split(",")[1];
                final byte[] imageBytes = DatatypeConverter.parseBase64Binary(data);

                try {
                    
                    ImageIO.write(ImageIO.read(new ByteArrayInputStream(imageBytes)), "jpg", new File(path+filename+".jpg"));

                    final Photo photo = new Photo();
                    photo.setPostid(topNum);
                    photo.setSrc(path+filename+".jpg");
                    photoservice.register(photo);
                } catch (final IOException e) {
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
        final Post post = service.showArticle(postId);
        service.upHit(postId);

        final Document doc = Jsoup.parseBodyFragment(post.getContent());
        final Element body = doc.body();
        final Elements dd = doc.select("img");
        
        for(int i = 0;i < dd.size() ; i++){
            final Element element = dd.get(i);
            final String id = element.attr("id");
            
            final File imagePath = new File(path+id+".jpg");
            
            FileInputStream fis = null;
            
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            

            int len = 0;
            final byte[] buf = new byte[1024];

            try{
                fis = new FileInputStream(imagePath);

                while((len = fis.read(buf)) != -1){
                    baos.write(buf,0,len);
                }

                final byte[] fileArray = baos.toByteArray();
                final byte[] baseIncodingBytes = Base64.encodeBase64(fileArray);
                element.attr("src","data:image/jpeg;base64, "+new String(baseIncodingBytes));

                baos.close();
                fis.close();
            }
            catch(final Exception e){
                System.out.println(e.getMessage());
            }
        }
        post.setContent(body.html());
        final Optional<Post> optPost = Optional.of(post);

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
        final Post post = service.showArticle(postId);
        System.out.println("글 삭제");
        final Document doc = Jsoup.parseBodyFragment(post.getContent());
        final Element body = doc.body();
        final Elements dd = doc.select("img");
        
        for(int i = 0;i < dd.size() ; i++){
            final Element element = dd.get(i);
            final String id = element.attr("id");
            final File imagePath = new File(path+id+".jpg");
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
    public ResponseEntity<Object> likeArticle(@Valid @RequestBody final Post request){
        final int postId = request.getPostId();
        final int userId = request.getUserid();
        final User user = userService.getUser(userId);
        String likedPostList = user.getLikedpost();
        if(likedPostList == null) likedPostList = "";
        final String pid = Integer.toString(postId);
        final int idx = likedPostList.indexOf(pid);
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
    public ResponseEntity<Object> getLikedList(@Valid @RequestBody final User request){
        final User user = userService.getUser(Integer.parseInt(request.getUid()));
        String likedpost = user.getLikedpost();

        final String[] idList = likedpost.split(",");
        
        
        final List<Post> likedPostList = new ArrayList<Post>();
        for(final String s : idList){
            final Post post = service.getPost(Integer.parseInt(s));
            if(post == null){ // deleted post일 경우
                // user 데이터 업데이트
                final int idx = likedpost.indexOf(s);
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

    @ApiOperation(value = "사용자가 작성한 게시물 ", notes = "작성 게시물 리스트 API")
    @PostMapping(value="/articles/postedList")
    @ApiImplicitParams({
        // @ApiImplicitParam(name = "uid", value = "식별자", required = true, dataType = "string" ),
        // @ApiImplicitParam(name = "password", value = "비밀번호", dataType = "string"),
        // @ApiImplicitParam(name = "email", value = "이메일" , dataType = "string"),
        // @ApiImplicitParam(name = "nickname", value = "별칭", dataType = "string"),
        // @ApiImplicitParam(name = "content", value = "자기소개", dataType = "string"),
        // @ApiImplicitParam(name = "createDate", value = "생성일", dataType = "Date", defaultValue = "현재시간"),
    })
    public ResponseEntity<Object> getPostedList(@Valid @RequestBody final User request){
        final List<Post> postedList = service.getPostedList(Integer.parseInt(request.getUid()));
        if(postedList.isEmpty()) { // 비어있을때
            return new ResponseEntity<>(null, HttpStatus.OK);
        } else { // 비어있지 않을때 
            return new ResponseEntity<>(postedList, HttpStatus.OK);
        }
    }

    @ApiOperation(value = "작성 게시물 리스트 좋아요순", notes = "작성 게시물 리스트 API")
    @GetMapping(value="/articles/postedListByLikes")
    @ApiImplicitParams({
        // @ApiImplicitParam(name = "uid", value = "식별자", required = true, dataType = "string" ),
        // @ApiImplicitParam(name = "password", value = "비밀번호", dataType = "string"),
        // @ApiImplicitParam(name = "email", value = "이메일" , dataType = "string"),
        // @ApiImplicitParam(name = "nickname", value = "별칭", dataType = "string"),
        // @ApiImplicitParam(name = "content", value = "자기소개", dataType = "string"),
        // @ApiImplicitParam(name = "createDate", value = "생성일", dataType = "Date", defaultValue = "현재시간"),
    })
    public ResponseEntity<Object> getPostedListByLikes(){
        final HashMap<String,Object> hm = new HashMap<>();
        
        final List<Post> postList = service.getPostedListByLikes();
        final List<Integer> commentList = new ArrayList<>();
        
        for(final Post post : postList){
            commentList.add(commentservice.countComment(post.getPostId()));


            final Document doc = Jsoup.parseBodyFragment(post.getContent());
            final Element body = doc.body();
            final Elements dd = doc.select("img");
            

            if(dd.size() > 0){
                final Element element = dd.get(0);
                final String id = element.attr("id");
                post.setContent(idParseImage(id));
            }
            else{
                post.setContent(null);
            }
        }
        
        hm.put("comment", commentList);
        hm.put("list", postList);

        return new ResponseEntity<>(hm,HttpStatus.OK) ;
    }

    @ApiOperation(value = "작성 게시물 리스트 조회순", notes = "작성 게시물 리스트 API")
    @GetMapping(value="/articles/postedListByHits")
    @ApiImplicitParams({
        // @ApiImplicitParam(name = "uid", value = "식별자", required = true, dataType = "string" ),
        // @ApiImplicitParam(name = "password", value = "비밀번호", dataType = "string"),
        // @ApiImplicitParam(name = "email", value = "이메일" , dataType = "string"),
        // @ApiImplicitParam(name = "nickname", value = "별칭", dataType = "string"),
        // @ApiImplicitParam(name = "content", value = "자기소개", dataType = "string"),
        // @ApiImplicitParam(name = "createDate", value = "생성일", dataType = "Date", defaultValue = "현재시간"),
    })
    public ResponseEntity<Object> getPostedListByHits(){
        final HashMap<String,Object> hm = new HashMap<>();
        
        final List<Post> postList = service.getPostedListByLikes();
        final List<Integer> commentList = new ArrayList<>();
        
        for(final Post post : postList){
            commentList.add(commentservice.countComment(post.getPostId()));


            final Document doc = Jsoup.parseBodyFragment(post.getContent());
            final Element body = doc.body();
            final Elements dd = doc.select("img");
            

            if(dd.size() > 0){
                final Element element = dd.get(0);
                final String id = element.attr("id");
                
                
                post.setContent(idParseImage(id));
            }
            else{
                post.setContent(null);
            }
        }
        
        hm.put("comment", commentList);
        hm.put("list", postList);

        return new ResponseEntity<>(hm,HttpStatus.OK) ;
    }

    @ApiOperation(value = "기본 검색", notes = "기본 검색 API")
    @GetMapping(value="/articles/searchArticle/{keyword}")
    @ApiImplicitParams({
    })
    public ResponseEntity<Object> searchArticle(@PathVariable final String keyword){
        
        final HashMap<String,Object> hm = new HashMap<>();
        final List<Integer> commentList = new ArrayList<>();

        final List<Post> list = service.searchArticle(keyword);        
        if(!list.isEmpty()){
            for(final Post post : list){

                commentList.add(commentservice.countComment(post.getPostId()));

                final Document doc = Jsoup.parseBodyFragment(post.getContent());
                final Elements dd = doc.select("img");
                
                if(dd.size() > 0){
                    final Element element = dd.get(0);
                    final String id = element.attr("id");
                    post.setContent(idParseImage(id));
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

    public String idParseImage(final String id){
        
        final File imagePath = new File(path+id+".jpg");
        FileInputStream fis = null;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int len = 0;
        final byte[] buf = new byte[1024];

        try{
            fis = new FileInputStream(imagePath);

            while((len = fis.read(buf)) != -1){
                baos.write(buf,0,len);
            }

            final byte[] fileArray = baos.toByteArray();
            final byte[] baseIncodingBytes = Base64.encodeBase64(fileArray);
            

            baos.close();
            fis.close();

            return "data:image/jpeg;base64, "+new String(baseIncodingBytes);
        }
        catch(final Exception e){
            System.out.println(e.getMessage());
        }

        return null;
    }


    @ApiOperation(value = "추천 음식점", notes = "추천 음식점 API")
    @PostMapping(value="/articles/getRecommentList/")
    public ResponseEntity<Object> getRecommentList(@RequestBody String wantRecommend){

        System.out.println(wantRecommend);

        final Double std_lat = 37.5006744185994;
        final Double std_lon = 127.03646946847;
        
        
        final double hitScore = 20.0;   // 조회수 계수
        final double likeScore = 20.0;  // 좋아요 계수 
        final double starScore = 20.0;  // 별점 계수
        final double disScore = 20.0;   // 거리 계수

        final Map<Integer,Double> hm = new HashMap<Integer,Double>();

        //전체 리스트 받아오기
        final List<Post> list =  service.getList();
        System.out.println("pid\thits\tlikes\tstarpoint\tdistance");

        Double hitsAvg = 0.0;
        Double likesAvg = 0.0;
        Double starAvg = 0.0;
        for(final Post p : list){
            hitsAvg += p.getHits();
            likesAvg += p.getLikes();
            starAvg += Double.parseDouble(p.getStarpoint());
            
            // 미터(Meter) 단위
            final double distanceMeter = distance(std_lat, std_lon, Double.parseDouble(p.getLat()), Double.parseDouble(p.getLon()), "meter");

            System.out.println(p.getPostId()+"\t"+p.getHits()+"\t"+p.getLikes()+"\t"+p.getStarpoint()+"\t"+distanceMeter);
        }

        hitsAvg /= list.size();
        likesAvg /= list.size();
        starAvg /= list.size();

        final double hitUnit = (hitScore/2)/hitsAvg;
        final double likeUnit = (likeScore/2)/likesAvg;
        final double starUnit = (starScore/2)/starAvg;

        System.out.println("hitsAvg : "+hitsAvg);
        System.out.println("likesAvg : "+likesAvg);
        System.out.println("starAvg : "+starAvg);

        System.out.println();

        System.out.println("hitUnit : "+hitUnit);
        System.out.println("likeUnit : "+likeUnit);
        System.out.println("starUnit : "+starUnit);


        final List<KeyValue> li = new ArrayList<KeyValue>();
        for(final Post p : list){
            final Double score =  p.getHits() * hitUnit +
                            p.getLikes() * likeUnit +
                            Double.parseDouble(p.getStarpoint()) * starUnit;
            hm.put(p.getPostId(), score);
            
            
            li.add(new KeyValue(p.getPostId(), hm.get(p.getPostId())));
        }

        System.out.println("================================");
        Collections.sort(li,Collections.reverseOrder());
        for(final KeyValue kv : li){
            System.out.println(kv);
        }
        
        // System.out.println(x);
        // System.out.println(y);
        System.out.println("==========================================");

        final HashMap<String,Post> result = new HashMap<>();
        result.put("음식", service.getPost(li.get(0).getPostid()));
        result.put("카페", service.getPost(li.get(1).getPostid()));
        result.put("술집", service.getPost(li.get(2).getPostid()));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @Getter
    @Setter
    public class KeyValue implements Comparable<KeyValue>{
        int postid;
        double score;

        KeyValue(){

        }

        KeyValue(final int postid,final double score){
            this.postid = postid;
            this.score = score;
        }

        @Override
        public String toString(){
            return this.postid+"\t"+this.score;
        }

        @Override
        public int compareTo(final KeyValue o) {
            return (int)(this.score - o.score);
        }
    }

    public String crawling(final String url){
        final String WEB_DRIVER_ID = "webdriver.chrome.driver";
        final String WEB_DRIVER_PATH = "C:\\Users\\multicampus\\Desktop\\sel\\chromedriver.exe";
        // final String WEB_DRIVER_PATH = "/usr/local/bin/chromedriver";

        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        final ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("start-maximized");
        options.addArguments("disable-infobars");
        options.addArguments("--disable-extensions");

        final WebDriver driver = new ChromeDriver(options);
        double star = 0.0;
        

        
        try{
            driver.get(url);
            Thread.sleep(300);
            final Document doc = Jsoup.parse(driver.getPageSource());
            final Elements el = doc.body().getElementsByClass("link_evaluation");
            star = Double.parseDouble(el.text().split(" ")[1].substring(0,3));
        }
        catch(final Exception e){
            System.out.println(e.getMessage());
        }
        finally{
            driver.close();
        }
        System.out.println(star);
        return star+"";
    }

    private static double distance(final double lat1, final double lon1, final double lat2, final double lon2, final String unit) {
         
        final double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
         
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
         
        if(unit == "meter"){
            dist = dist * 1609.344;
        }
        return (dist);
    }
     
 
    private static double deg2rad(final double deg) {
        return (deg * Math.PI / 180.0);
    }
     
    private static double rad2deg(final double rad) {
        return (rad * 180 / Math.PI);
    }

}