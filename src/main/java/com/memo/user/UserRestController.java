package com.memo.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.memo.common.EncryptUtils;
import com.memo.user.bo.UserBO;
import com.memo.user.entity.UserEntity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class UserRestController {
	
	@Autowired
	private UserBO userBO;
	
	/*
	 *  아이디 중복확인 API
	 *  @param loginId
	 *  @return
	 * */

	@RequestMapping("/is-duplicated-id")
	public Map<String, Object> isDuplicatedId(
			@RequestParam("loginId") String loginId ) {
		
		// DB 조회 - select
		// loginId로 검색한 UserEntity를 가져온다.
		UserEntity user = userBO.getUserEntityByLoginId(loginId);
		
		Map<String, Object> result = new HashMap<>();
		
		if (user != null) { // user가 null 이지 않다면 = 중복
			result.put("code", 200);
			result.put("is_duplicated_id", true);
		} else { // 중복 아님 
			result.put("code", 200);
			result.put("is_duplicated_id", false);
		}
		
		
		return result;
		
	}
	
	/**
	 * 회원가입 API
	 * @param loginId
	 * @param password
	 * @param name
	 * @param email
	 * @return
	 */
	@PostMapping("/sign-up")
	public Map<String, Object> signUp(
			@RequestParam("loginId") String loginId, 
			@RequestParam("password") String password,
			@RequestParam("name") String name,
			@RequestParam("email") String email) {
		
		// md5 알고리즘 => password hashing
		// aaaa => 594f803b38a41396ed63dca39503542
		// aaaa => 594f803b38a41396ed63dca39503542
		String hashedPassword = EncryptUtils.md5(password);
		
		// db insert
		Integer userId = userBO.addUser(loginId, hashedPassword, name, email);
		
		// 응답값
		Map<String, Object> result = new HashMap<>();
		if (userId != null) {
			result.put("code", 200);
			result.put("result", "성공");
		} else {
			result.put("code", 500);
			result.put("error_message", "회원가입에 실패했습니다.");
		}
		
		return result;
		
	}
	
	@PostMapping("/sign-in")
	public Map<String, Object> signIn(
			@RequestParam("loginId")String loginId,
			@RequestParam("password")String password,
			HttpServletRequest request ) {
		
		// 비밀번호 hashing - md5
		// md5() returned	"74b8733745420d4d33f80c4663dc5e5" (id=155)	
		String hashedPassword = EncryptUtils.md5(password);
		
		// db 조회(loginId, hashedPassword) => UserEntity
		UserEntity user = userBO.getUserEntityByLoginIdPassword(loginId, hashedPassword);
		
		// 응답값
		Map<String, Object> result = new HashMap<>();
		if (user != null) { // 로그인 성공
			// 로그인 처리
			
			// 로그인 정보를 세션에 담는다.(사용자 마다)
			HttpSession session = request.getSession();
			session.setAttribute("userId", user.getId());
			session.setAttribute("userLoginId", user.getLoginId());
			session.setAttribute("userName", user.getName());
			
			// 응답
			result.put("code", 200);
			result.put("result", "성공");
		} else { // 로그인 불가
			// 에러
			result.put("code", 300);
			result.put("error_message", "존재하지 않는 사용자입니다.");
		}
		
		return result;
		
	}
 	
}
