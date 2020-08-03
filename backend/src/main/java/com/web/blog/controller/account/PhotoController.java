package com.web.blog.controller.account;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import javax.xml.bind.DatatypeConverter;

import com.web.blog.model.BasicResponse;
import com.web.blog.model.user.Photo;
import com.web.blog.service.PhotoServiceImpl;


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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
public class PhotoController {
    

    @Autowired
    PhotoServiceImpl service;

    @PostMapping("photos/register")
    @ApiOperation(value = "사진 등록", notes = "사진 등록 기능입니다.")
    @ApiImplicitParams({
            // @ApiImplicitParam(name = "email", value = "이메일", required = true, dataType = "string", paramType = "path"),
            // @ApiImplicitParam(name = "password", value = "비밀번호", required = true, dataType = "string", paramType = "path"),
    })
    public ResponseEntity<Object> register(@Valid @RequestBody Photo photo){

        if(service.register(photo)>0){
            return new ResponseEntity<>("사진 등록 성공", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("사진 등록 실패", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value="/photos/list")
    public ResponseEntity<List<Photo>> getList(){
        return new ResponseEntity<>(service.getList(),HttpStatus.OK) ;
    }  

    @DeleteMapping("photos/dropPhoto")
    @ApiOperation(value = "사진 삭제", notes = "사진 삭제 기능입니다.")
    @ApiImplicitParams({
            // @ApiImplicitParam(name = "email", value = "이메일", required = true, dataType = "string", paramType = "path"),
            // @ApiImplicitParam(name = "password", value = "비밀번호", required = true, dataType = "string", paramType = "path"),
    })
    public ResponseEntity<Object> delete(@Valid @RequestBody Photo photo){
        if(service.delete(photo)>0){
            return new ResponseEntity<>("사진 삭제 성공", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("사진 삭제 실패", HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("photos/like")
    @ApiOperation(value = "사진 좋아요", notes = "사진 좋아요 기능입니다.")
    @ApiImplicitParams({
            // @ApiImplicitParam(name = "email", value = "이메일", required = true, dataType = "string", paramType = "path"),
            // @ApiImplicitParam(name = "password", value = "비밀번호", required = true, dataType = "string", paramType = "path"),
    })
    public ResponseEntity<Object> like(@Valid @RequestBody Photo photo){
        if(service.like(photo)>0){
            return new ResponseEntity<>("사진 좋아요 성공", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("사진 좋아요 실패", HttpStatus.NOT_FOUND);
        }
    }
}