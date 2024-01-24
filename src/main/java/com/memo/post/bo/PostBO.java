package com.memo.post.bo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.memo.common.FileManagerService;
import com.memo.post.domain.Post;
import com.memo.post.mapper.PostMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j // lombok으로 import == 객체를 생성하는 것과 같은 것
public class PostBO {

	// mybatis가 아닌 slf4j로 import
//	private Logger logger = LoggerFactory.getLogger(PostBO.class);
//	private Logger logger = LoggerFactory.getLogger(this.getClass()); // 지금 이 클래스
	
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
	
	// 글 목록 가져오기 
	public Post getPostByPostIdUserId(int postId, int userId) {
		return postmapper.selectPostByPostIdUserId(postId, userId);
	}
	
	// input: 파라미터들 				output: X
	public void updatePostById(
			int userId, String userLoginId, 
			int postId, String subject, String content, MultipartFile file) {
		
		// 기존 글을 가져온다. 
		// (1. 이미지 교체시 기존 이미지를 삭제하기 위해서 )
		// (2. 업데이트 대상이 있는지 다시 확인하기 위해서 )
		Post post = postmapper.selectPostByPostIdUserId(postId, userId);
		if (post == null) {
			log.info("[글 수정] post is null. postId: {}, userId: {}", postId, userId);
			return;
		}
		
		// 파일이 있다면
		// 1) 새 이미지를 업로드한다. 
		// 2) 1번 단계가 성공한다면, 기존 이미지를 제거한다(기존이미지가 있다면).
		String imagePath = null;
		if (file != null) {
			// 업로드
			imagePath = fileManagerService.saveFile(userLoginId, file); // (폴더이름으로 사용할 로그인 아이디, 이미지)
			
			// 업로드 성공 시, 기존 이미지가 있다면 제거 (메모리 삭제)
			if (imagePath != null && post.getImagePath() != null) {
				// 업로드를 성공하고, 기존 이미지가 있다면, 서버에서의 파일 제거
				fileManagerService.deleteFile(post.getImagePath()); // 기존의 post의 getImagePath를 타고 들어가서 삭제하라.
			}
		}
		
		// db 업데이트
		postmapper.updatePostByPostId(postId, subject, content, imagePath);
		
	}
	
	public void deletePostById(int postId, int userId) {
		
		// 삭제할 사진이 있는지 없는지 판단
		// 1) 기존 글 가져오기
		Post post = postmapper.selectPostByPostIdUserId(postId, userId);
		if (post == null) {
			log.info("[글 수정] post is null. postId: {}", postId);
			return;
		}
		
		// 만약 사진이 존재한다면. 
		if (post.getImagePath() != null) {
			// 사진 삭제
			fileManagerService.deleteFile(post.getImagePath());
		}
		
		postmapper.deletePostById(postId, userId);
		
	}
	
}
