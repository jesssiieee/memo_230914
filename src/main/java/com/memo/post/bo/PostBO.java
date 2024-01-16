package com.memo.post.bo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.memo.post.domain.Post;
import com.memo.post.mapper.PostMapper;

@Service
public class PostBO {

	@Autowired
	private PostMapper postmapper;
	
	// input: userId(로그인 된 사람), output: List<Post>
	public List<Post> getPostListByuserId(int userId) {
		return postmapper.selectPostListByuserId(userId);
	}
	
}
