-- 최종수정
-- email에 Unique 속성 넣음
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `uid` INT NOT NULL auto_increment, 
  `email` varchar(128) DEFAULT NULL UNIQUE,
  `password` varchar(128) DEFAULT NULL,
  `nickname` varchar(128) DEFAULT NULL, 
  `content` varchar(128) DEFAULT NULL,
  `create_date` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`uid`),
  UNIQUE KEY `user_idx_unique_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- 200722 add sql

-- add add user table photo col 
alter table user add photo varchar(100) default null;

-- post table
create table post(
  postId int auto_increment,
  userid int,
  title varchar(100),
  lat varchar(30),
  lon  varchar(30),
  content varchar(1000),
  hashtag varchar(200),
  address varchar(50),
  likes int(10),
  create_data datetime DEFAULT current_timestamp(),
  PRIMARY KEY (postId),
  FOREIGN key(userid)
  REFERENCES user(uid) on delete cascade
);

--posttmp table
create table posttmp(
  postId int auto_increment,
  userid int,
  title varchar(100),
  lat varchar(30),
  lon  varchar(30),
  content varchar(1000),
  hashtag varchar(200),
  address varchar(50),
  likes int(10),
  create_data datetime DEFAULT current_timestamp(),
  PRIMARY KEY (postId),
  FOREIGN key(userid)
  REFERENCES user(uid) on delete cascade
);

--photo table
create table photo(
  photoid int auto_increment,
  postid int,
  src varchar(200),
  likes int,
  PRIMARY KEY(photoid),
  FOREIGN KEY(postid) 
  REFERENCES post(postid) on delete CASCADE
);

--comment table
create table comment(
  commentId int auto_increment,
  postid int,
  content varchar(1000),
  create_data datetime DEFAULT current_timestamp(),
  userid int,
  parent int,
  PRIMARY KEY(commentId),
  FOREIGN KEY(postid)
  REFERENCES post(postid) on delete CASCADE
);

-- 200727 add sql
--  add user table likedpost col 
alter table user add likedpost varchar(100) default null;

-- 200728 add sql
-- add comment table nickname col
alter table comment add nickname varchar(100) default null;

--200729 modify sql
-- comment parentid >> parent
alter table comment change parentid parent int;

--200729 modify sql
-- post add post,posttmp column nickname
alter table post add nickname varchar(100) default null;
alter table posttmp add nickname varchar(100) default null;

--200803 modify sql
-- post add hits
alter table post add hits int default 0;

--200805
alter table post add url varchar(200) default null;
alter table post add starpoint varchar(10) default null;


