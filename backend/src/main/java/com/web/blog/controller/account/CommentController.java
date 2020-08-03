package com.web.blog.controller.account;

import java.util.List;

import javax.validation.Valid;

import com.web.blog.model.BasicResponse;
import com.web.blog.model.user.Comment;
import com.web.blog.service.CommentServiceImpl;
import com.web.blog.service.UserService;
import com.web.blog.service.UserServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@ApiResponses(value = { @ApiResponse(code = 401, message = "Unauthorized", response = BasicResponse.class),
        @ApiResponse(code = 403, message = "Forbidden", response = BasicResponse.class),
        @ApiResponse(code = 404, message = "Not Found", response = BasicResponse.class),
        @ApiResponse(code = 500, message = "Failure", response = BasicResponse.class) })


@CrossOrigin(origins = { "*" },maxAge = 6000)
@RestController
public class CommentController {
    @Autowired
    CommentServiceImpl service;

    @Autowired
    UserServiceImpl userService;

    @PostMapping("/comments/register")
    @ApiOperation(value = "댓글 작성", notes = "댓글 작성 기능입니다.")
    @ApiImplicitParams({
            // @ApiImplicitParam(name = "email", value = "이메일", required = true, dataType = "string", paramType = "path"),
            // @ApiImplicitParam(name = "password", value = "비밀번호", required = true, dataType = "string", paramType = "path"),
    })
    public ResponseEntity<Object> register(@Valid @RequestBody Comment comment){
        comment.setNickname(userService.getUser(comment.getUserid()).getNickname());
        if(service.register(comment)>0){
            return new ResponseEntity<>("success", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("fail", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/comments/modify")
    @ApiOperation(value = "댓글 수정", notes = "댓글 수정 기능입니다.")
    @ApiImplicitParams({
            // @ApiImplicitParam(name = "email", value = "이메일", required = true, dataType = "string", paramType = "path"),
            // @ApiImplicitParam(name = "password", value = "비밀번호", required = true, dataType = "string", paramType = "path"),
    })
    public ResponseEntity<Object> modifyComment(@Valid @RequestBody Comment comment){
        if(service.modifyComment(comment)>0){
            return new ResponseEntity<>("success", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("fail", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/comments/dropComment")
    @ApiOperation(value = "댓글 삭제", notes = "댓글 삭제 기능입니다.")
    @ApiImplicitParams({
            // @ApiImplicitParam(name = "email", value = "이메일", required = true, dataType = "string", paramType = "path"),
            // @ApiImplicitParam(name = "password", value = "비밀번호", required = true, dataType = "string", paramType = "path"),
    })
    public ResponseEntity<Object> deleteComment(@RequestParam(required = true) final int commentId){
        if(service.deleteComment(commentId)>0){
            return new ResponseEntity<>("success", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("fail", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value="/comments/list/{postid}")
    public ResponseEntity<List<Comment>> getList(@PathVariable("postid") int postid){
        return new ResponseEntity<>(service.getList(postid),HttpStatus.OK) ;
    }  
}