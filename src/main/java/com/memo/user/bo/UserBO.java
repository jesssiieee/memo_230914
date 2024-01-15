package com.memo.user.bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.memo.user.entity.UserEntity;
import com.memo.user.repository.UserRepository;

@Service
public class UserBO {

	@Autowired
	private UserRepository userRepository;
	
	// 로그인 id가 중복인지 DB에서 확인필요.
	// input: loginId	output: UserEntity(있거나 or null)
	public UserEntity getUserEntityByLoginId(String loginId) {
		return userRepository.findByLoginId(loginId);
	}
	
	// input: 파라미터 4개
	// output: Integer id(pk) <- insert가 실패한다면 null일 수 도 있기 때문에
	public Integer addUser(String loginId, String password, String name, String email) {
		
		// 기본 제공 메소드 save()
		UserEntity userEntity = userRepository.save(
					UserEntity.builder()
						.loginId(loginId)
						.password(password)
						.name(name)
						.email(email)
						.build()
				);
		
		return userEntity == null ? null : userEntity.getId();
		
	}
	
	// input: loginId, password		output: UserEntity
	public UserEntity getUserEntityByLoginIdPassword(String loginId, String password) {
		return userRepository.findByLoginIdAndPassword(loginId, password);
	}
	
	
}
