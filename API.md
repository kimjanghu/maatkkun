### User 기능

* 로그인

  POST accounts/login

* 회원가입

  POST accounts/signup

* 정보 수정

  POST accounts/update

* 이메일 중복 체크

  GET accounts/emailCheck

* 닉네임 중복 체크

  GET accounts/nickNameCheck

* 이메일 인증

  GET accounts/emailAuth

* 회원 탈퇴

  DELETE accounts/dropUser

* 로그아웃

  POST accounts/signout

* 내 정보 조회

  GET accounts/userDetail

### 게시글 기능

* 글 작성

  POST articles/register

  request

  ```java
      private int userid;
      private String title;
      private String lat;
      private String lon;
      private String content;
      private String hashtag;
      private String address;
  ```

  response

  ```java
  “success”	httpStatus.OK
   “fail”		httpStatus.BAD_REQUEST
  ```

  

* 글 목록 조회

  GET articles/list

  response

  ```java
JSON<List<Post>>	httpStatus.OK
   “fail”				httpStatus.BAD_REQUEST
  ```
  
  

* 글 수정

  POST articles/modify

  request

  ```java
      private int postId;
      private int userid;
      private String title;
      private String lat;
      private String lon;
      private String content;
      private String hashtag;
      private String address;
      private int likes;
  ```

  response

  ```java
  “success”	httpStatus.OK
   “fail”		httpStatus.BAD_REQUEST
  ```

  

* 글 상세 조회

  GET articles/showArticle/

  request

  PathVariable GET articles/showArticle/{pid}

  response

  ```java
  “success”  httpStatus.OK
   “fail”  httpStatus.BAD_REQUEST
  ```

  

* 글 삭제

  DELETE articles/dropArticle

  request

  PathVariable GET articles/dropArticle/{pid}

  response

  ```java
  “success”  httpStatus.OK
   “fail”  httpStatus.BAD_REQUEST
  ```

  

* 글 좋아요

  POST articles/like

  request

  ```java
  private int postId;
  private int userid;
  ```

  response
  
* 글 좋아요 목록

  POST articles/likedList

  request
  ```java
  private int userid;
  ```

  response
  
  ```
  JSON<List<Post>>	httpStatus.OK
  “fail”  httpStatus.BAD_REQUEST
  ```
  
* 글 검색

  GET articles/searchArticle/{keyword}

  request

  ```
  PathVariable GET articles/searchArticle/{keyword}
  ```

  response

  ```
  JSON<List<Post>> httpStatus.OK
  “fail”  httpStatus.BAD_REQUEST
  ```

  


### 댓글 기능

* 댓글 등록

  POST comments/register

* 댓글 수정

  POST comments/modify

* 댓글 삭제

  DELETE comments/dropComment

* 댓글 조회

  GET comments/list

### 사진 기능

* 사진 등록

  POST photos/register

* 사진 조회

  GET photos/list

* 사진 삭제

  DELETE photos/dropPhoto

* 사진 좋아요

  POST photos/like

### 임시저장 기능

* 유저별 임시저장 게시글 목록 조회

  GET subarticles/list/{userid}

  request

  ```java
  PathVariable GET subarticles/list/{userid}
  ```

  response

  ```
  JSON<List<User>> httpStatus.OK
  “fail”  httpStatus.BAD_REQUEST
  ```

  

* 임시저장 게시글 저장

  POST subarticles/register

  request

  ```java
      private int userid;
      private String title;
      private String lat;
      private String lon;
      private String content;
      private String hashtag;
      private String address;
  ```

  response

  ```java
  “success”	httpStatus.OK
   “fail”		httpStatus.BAD_REQUEST
  ```

  

* 임시저장 게시글 삭제

  DELETE subarticles/dropSubarticle

* 임시저장 게시글 상세 조회

  GET subarticles/detail/{postid}

  request

  ```java
  PathVariable GET subarticles/detail/{postid}
  ```

  response

  ```
  JSON<Post> httpStatus.OK
  “fail”  httpStatus.BAD_REQUEST
  ```

  

  

  



