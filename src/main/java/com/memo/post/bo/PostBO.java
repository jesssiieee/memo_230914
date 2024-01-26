package com.memo.post.bo;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.memo.common.FileManagerService;
import com.memo.post.domain.Post;
import com.memo.post.mapper.PostMapper;
import com.mysql.cj.x.protobuf.MysqlxCrud.Collection;

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

	// 페이징 관련 필드 
	private static final int POST_MAX_SIZE = 3;
	
	// userId 기준으로 글 목록 + 페이징
	// input: userId(로그인 된 사람), output: List<Post>
	public List<Post> getPostListByUserId(int userId, Integer prevId, Integer nextId) {
		
		// 게시글 번호 => 10 9 8 | 7 6 5 | 4 3 2 | 1
		// 만약 | 4 3 2 | 페이지에 있을 때
		// 1) 다음 => 2보다 작은 3개를 "DESC" 정렬
		// 2) 이전 => 4보다 큰 3개를 "ASC" 정렬 => list.reverse(); => 7 6 5
		// 3) 페이징 정보 없음(= 처음 들어온 경우) 최신순 3개 DESC
		
		// 사용될 id는 하나만 들어오기 때문에 prevId, nextId를 기준삼은 아이디 한 개 선언
		Integer standardId = null; // 기준이 되는 postId
		String direction = null;   // 방향
		
		if (prevId != null) { // 2) 이전
			standardId = prevId;
			direction = "prev";
			
			List<Post> postList = postmapper.selectPostListByuserId(userId, standardId, direction, POST_MAX_SIZE);
			
			// reverse list = 5 6 7 = > 7 6 5
			Collections.reverse(postList); // 뒤집고 결과를 저장까지
			return postList;
			
		} else if (nextId != null) { //  1) 다음
			standardId = nextId;
			direction = "next";
		} 
		
		// 페이징 정보 없음, 1) 다음
		return postmapper.selectPostListByuserId(userId, standardId, direction, POST_MAX_SIZE);
	}
	
	// 이전 페이지의 마지막 인가?
	public boolean isPrevLastPageByUserId(int userId, int prevId) {
		int postId = postmapper.selectPostIdByUserIdSort(userId, "DESC");
		return postId == prevId; // 같으면 끝, 마지막이다.
	}
	
	// 다음 페이지의 마지막 인가?
	public boolean isNextLastPageByUserId(int userId, int nextId) {
		int postId = postmapper.selectPostIdByUserIdSort(userId, "ASC");
		return postId == nextId;
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
	
	public void deletePostByIdUserId(int postId, int userId) {
		
		// 기존글이 있는지 확인
		Post post = postmapper.selectPostByPostIdUserId(postId, userId);
		if (post == null) {
			log.info("[글 삭제] post is null. postId: {}, userId: {}", postId, userId);
			return;
		}
		
		// DB 삭제
		// 삭제된 행의 개수
		int deleteRowCount = postmapper.deletePostById(postId);
		
		// 이미지가 존재하고 DB 삭제도 성공했다면
		if (deleteRowCount > 0 && post.getImagePath() != null) {
			// 사진 삭제
			fileManagerService.deleteFile(post.getImagePath());
		}
		
		
	}
	
}
