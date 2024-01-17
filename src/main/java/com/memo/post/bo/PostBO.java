package com.memo.post.bo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.memo.common.FileManagerService;
import com.memo.post.domain.Post;
import com.memo.post.mapper.PostMapper;

@Service
public class PostBO {

	@Autowired
	private PostMapper postmapper;
	
	@Autowired
	private FileManagerService fileManagerService;

	// userId 기준으로 글 목록 
	// input: userId(로그인 된 사람), output: List<Post>
	public List<Post> getPostListByuserId(int userId) {
		return postmapper.selectPostListByuserId(userId);
	}
	
	// 글쓰기 - DB insert
	// input: params 
	// output: X
	public void addPost(int userId, String userLoginId, String subject, String content, MultipartFile file) {
		
		String imagePath = null;
		
		// 업로드 할 이미지가 있을 때 업로드
		if (file != null) {
			imagePath = fileManagerService.saveFile(userLoginId, file);
		}
		
		postmapper.insertPost(userId, subject, content, imagePath);
	}
	
}
