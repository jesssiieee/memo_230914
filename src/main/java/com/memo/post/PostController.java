package com.memo.post;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
	public String postListView(Model model, HttpSession session) {
		
		// 로그인 여부 권한 검사
		Integer userId = (Integer)session.getAttribute("userId");
		if (userId == null) {
			// 비로그인이면, 로그인 페이지로 이동.
			return "redirect:/user/sign-in-view";
		}
		
		// DB 글 목록 조회
		List<Post> postList = postBO.getPostListByuserId(userId);
		
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
	
}
