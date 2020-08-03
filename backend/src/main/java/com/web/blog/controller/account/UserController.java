package com.web.blog.controller.account;



import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.mail.internet.MimeMessage;
import javax.validation.Valid;

import com.web.blog.model.BasicResponse;
import com.web.blog.model.user.Photo;
import com.web.blog.model.user.User;

import com.web.blog.service.UserServiceImpl;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.beans.factory.annotation.Autowired;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@ApiResponses(value = { @ApiResponse(code = 401, message = "Unauthorized", response = BasicResponse.class),
        @ApiResponse(code = 403, message = "Forbidden", response = BasicResponse.class),
        @ApiResponse(code = 404, message = "Not Found", response = BasicResponse.class),
        @ApiResponse(code = 500, message = "Failure", response = BasicResponse.class) })


@CrossOrigin(origins = { "*" },maxAge = 6000)
@RestController

public class UserController {

    @Autowired
    UserServiceImpl userService;

    @Autowired
    JavaMailSender mailSender;

    @PostMapping("/accounts/login")
    @ApiOperation(value = "로그인", notes = "로그인 기능입니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "이메일", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "password", value = "비밀번호", required = true, dataType = "string", paramType = "path"),
    })
    public ResponseEntity<User> login(@Valid @RequestBody User request) {

        User user = userService.login(request.getEmail(), request.getPassword());
        Optional<User> userOpt = Optional.of(user);

        ResponseEntity response = null;

        if (userOpt.isPresent()) {
            final BasicResponse result = new BasicResponse();
            result.status = true;
            response = new ResponseEntity<User>(user, HttpStatus.OK);

        } else {
            response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        return response;
    }
    
    @PostMapping("/accounts/signup")
    @ApiOperation(value = "가입하기", notes = "회원가입 API")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "식별자", dataType = "string" ),
            @ApiImplicitParam(name = "password", value = "비밀번호", required = true, dataType = "string"),
            @ApiImplicitParam(name = "email", value = "이메일", required = true, dataType = "string"),
            @ApiImplicitParam(name = "nickname", value = "별칭", required = true, dataType = "string"),
            @ApiImplicitParam(name = "createDate", value = "생성일", dataType = "Date", defaultValue = "현재시간"),
    })
    public Object signup(@Valid @RequestBody User request) {
        System.out.println(request);

        String password = request.getPassword();
        String passwordConfirm = request.getPasswordConfirm();
        if(!password.equals(passwordConfirm))
            return new ResponseEntity<>("fail", HttpStatus.BAD_REQUEST);

        if(userService.join(request) > 0){
            final BasicResponse result = new BasicResponse();
            result.status = true;
            result.data = "success";

            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        else{
            final BasicResponse result = new BasicResponse();
            result.status = true;
            result.data = "fail";

            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    

    @PutMapping("/accounts/update")
    @ApiOperation(value = "업데이트", notes = "회원정보 업데이트 API")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "식별자", required = true, dataType = "string" ),
            @ApiImplicitParam(name = "password", value = "비밀번호", dataType = "string"),
            @ApiImplicitParam(name = "email", value = "이메일" , dataType = "string"),
            @ApiImplicitParam(name = "nickname", value = "별칭", dataType = "string"),
            @ApiImplicitParam(name = "content", value = "자기소개", dataType = "string"),
            @ApiImplicitParam(name = "createDate", value = "생성일", dataType = "Date", defaultValue = "현재시간"),
    })
    public Object userUpdate(@Valid @RequestBody User request) {
        
        System.out.println("userupdate");
        System.out.println(request);
        if(userService.updateUser(request) > 0){
            final BasicResponse result = new BasicResponse();
            result.status = true;
            result.data = "success";

            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        else{
            final BasicResponse result = new BasicResponse();
            result.status = true;
            result.data = "fail";

            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);

        }
    }

    @GetMapping("/accounts/emailCheck")
    @ApiOperation(value = "이메일 중복" ,notes="이메일 중복을 확인하는 API")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "이메일", required = true, dataType = "string"),
    })
    public ResponseEntity<String> emailCheck(@RequestParam(required = true) final String email){
        System.out.println("이메일 중복 확인");
        List<User> list = userService.findAllUser();
        for(User u : list){
            if(u.getEmail().equals(email)){
                return new ResponseEntity<>("아이디중복",HttpStatus.OK);
            }
        }
        System.out.println("끝2");
        return new ResponseEntity<>("아이디사용가능",HttpStatus.OK);
    }

    @GetMapping("/accounts/nicknameCheck")
    @ApiOperation(value = "닉네임 중복" ,notes="닉네임 중복을 확인하는 API")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nickname", value = "별칭", required = true, dataType = "string"),
    })
    public ResponseEntity<String> nicknameCheck(@RequestParam(required = true) final String nickname){
        System.out.println("닉네임 중복 확인");
        List<User> list = userService.findAllUser();

        for(User u : list){
            if(u.getNickname().equals(nickname)){
                return new ResponseEntity<>("닉네임중복",HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("닉네임사용가능",HttpStatus.OK);
    }

    

    @GetMapping("/accounts/emailAuth")
    @ApiOperation(value = "이메일 인증", notes = "이메일 인증 API")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "이메일", required = true, dataType = "string",paramType = "path"),
    })
    public ResponseEntity<Object> getEmail(@RequestParam(required = true) final String email){ 

        Random r = new Random();
        int dice = r.nextInt(4589362) + 49311; //이메일로 받는 인증코드 부분 (난수)
        
        String setfrom = "admin@gamil.com";
        String tomail = email; // 받는 사람 이메일
        String title = "회원가입 인증 이메일 입니다."; // 제목
        String content =
        
        System.getProperty("line.separator")+ //한줄씩 줄간격을 두기위해 작성
        System.getProperty("line.separator")+
        "안녕하세요 회원님 저희 홈페이지를 찾아주셔서 감사합니다"
        +System.getProperty("line.separator")+
        System.getProperty("line.separator")+
        " 인증번호는 " +dice+ " 입니다. "
        +System.getProperty("line.separator")+
        System.getProperty("line.separator")+
        "받으신 인증번호를 홈페이지에 입력해 주시면 다음으로 넘어갑니다."; // 내용
        
        try {
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message,true, "UTF-8");

            messageHelper.setFrom(setfrom); // 보내는사람 생략하면 정상작동을 안함
            messageHelper.setTo(tomail); // 받는사람 이메일
            messageHelper.setSubject(title); // 메일제목은 생략이 가능하다
            messageHelper.setText(content); // 메일 내용
            mailSender.send(message);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ResponseEntity<>(dice,HttpStatus.OK);
    }

    @GetMapping("/accounts/list")
    @ApiOperation(value = "회원 목록", notes = "회원목록 API")
    public ResponseEntity<List<User>> getList(){
        return new ResponseEntity<>(userService.findAllUser(),HttpStatus.OK);
    }


    
    @DeleteMapping("/accounts/dropUser")
    @ApiOperation(value = "회원탈퇴" ,notes="회원탈퇴 API")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "식별자", required = true, dataType = "String"),
    })
    public Object delete(@RequestParam(required = true) final String uid){
      
        userService.deleteUser(Integer.parseInt(uid));
        
        final BasicResponse result = new BasicResponse();
        result.data= "success";
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping("/accounts/userDetail")
    @ApiOperation(value = "회원 정보", notes = "회원 정보 API")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "사용자 아이디", required = true, dataType = "string", paramType = "path"),
    })
    public ResponseEntity<User> userDetail(@Valid @RequestBody User request) {
        int userId = Integer.parseInt(request.getUid());
        User user = userService.getUser(userId);
        Optional<User> userOpt = Optional.of(user);
        ResponseEntity response = null;

        if (userOpt.isPresent()) {
            final BasicResponse result = new BasicResponse();
            result.status = true;
            response = new ResponseEntity<User>(user, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>("fail", HttpStatus.BAD_REQUEST);
        }
        return response;
    }
}
// 로그아웃
// POST accounts/signout
