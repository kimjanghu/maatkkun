package com.web.blog.dao.user;

import java.util.HashMap;
import java.util.List;

import com.web.blog.model.user.User;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAOImpl implements UserDAO {

	@Autowired
	SqlSession sqlSession;

	@Override
	public User login(String email, String passwd) {

		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("email", email);
		hm.put("passwd", passwd);

		return sqlSession.selectOne("mapper.userlogin", hm);
	}

	@Override
	public List<User> findAllUser() {
		return sqlSession.selectList("mapper.findAlluser");
	}

	@Override
	public int join(User user) {
		return sqlSession.insert("mapper.userjoin",user);
	}

	@Override
	public int deleteUser(int uid) {
		return sqlSession.delete("mapper.deleteUser",uid);
	}

	@Override
	public int updateUser(User user) {
		return sqlSession.update("mapper.updateUser",user);
	}

	@Override
	public User getUser(int uid) {
		return sqlSession.selectOne("mapper.deatilUser",uid);
	}


    
}