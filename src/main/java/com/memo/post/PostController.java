package com.memo.post;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.memo.post.bo.PostBO;
import com.memo.post.domain.Post;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/post")
public class PostController {
	
	@Autowired
	private PostBO postBO;

	// url : http://localhost/post/post-list-view
	@GetMapping("/post-list-view")
	public String postListView(
			@RequestParam(value = "prevId", required = false) Integer prevIdParam,
			@RequestParam(value = "nextId", required = false) Integer nextIdParam,
			Model model, HttpSession session) {
		
		// 로그인 여부 권한 검사
		Integer userId = (Integer)session.getAttribute("userId");
		if (userId == null) {
			// 비로그인이면, 로그인 페이지로 이동.
			return "redirect:/user/sign-in-view";
		}
		
		// DB 글 목록 조회
		List<Post> postList = postBO.getPostListByUserId(userId, prevIdParam, nextIdParam);
		
		int nextId = 0;
		int prevId = 0;
		// postList가 비어있을 때, [] 오류를 방지하기 위함
		if (postList.isEmpty() == false) {
			prevId = postList.get(0).getId(); // 리스트에서 꺼내진 첫번째 글의 글 번호
			nextId = postList.get(postList.size() - 1).getId(); // 리스트의 마지막 글 번호
			
			// 이전 방향의 끝 인가?
			// 내가 쓴 글들 중 id가 가장 큰 것 == select한 아이디와 같다면
			// prevId와 post 테이블의 가장 큰 id값과 같다면, 이전 페이지 없음
			if (postBO.isPrevLastPageByUserId(userId, prevId)) {
				prevId = 0;
			}
			
			// 다음 방향의 끝 인가?
			// nextId와 post 테이블의 가장 작은 id값이 같으면 다음 페이지 없음
			if (postBO.isNextLastPageByUserId(userId, nextId)) {
				nextId = 0;
			}
						
		}
		
		
		model.addAttribute("nextId", nextId);
		model.addAttribute("prevId", prevId);
		
		model.addAttribute("postList", postList);
		model.addAttribute("viewName", "post/postList");
		return "template/layout";
	}

	/**
	 * 글쓰기 화면
	 * @param model
	 * @return
	 */
	// url : http://localhost/post/post-create-view
	@GetMapping("/post-create-view")
	public String postCreateView(Model model) {
		model.addAttribute("viewName", "post/postCreate");
		return "template/layout";
	}
	
	// url : http://localhost/post/post-detail-view
	@GetMapping("/post-detail-view")
	public String postDetailVeiw(
			@RequestParam("postId") int postId,
			HttpSession session,
			Model model) {
		
		int userId = (int)session.getAttribute("userId");
		
		// DB 조회 - postId + userId
		Post post = postBO.getPostByPostIdUserId(postId, userId);
		
		model.addAttribute("post", post);
		model.addAttribute("viewName", "post/postDetail");
		return "template/layout";
	}
	
}
