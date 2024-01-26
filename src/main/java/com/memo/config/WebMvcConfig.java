package com.memo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.memo.common.FileManagerService;
import com.memo.interceptor.PermissionInterceptor;

@Configuration // 설정을 위한 spring bean
public class WebMvcConfig implements WebMvcConfigurer {
	
	@Autowired
	private PermissionInterceptor interceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry
			.addInterceptor(interceptor)
			.addPathPatterns("/**") // 모든 주소
			.excludePathPatterns("/static/**", "/error", "/user/sign-out") // 제외하고 싶은 주소
			;

	}
	
	// web 이미지 path와 서버에 업로드 된 실제 이미지와 매핑 설정
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		registry
			.addResourceHandler("/images/**") // web path: http://localhost/images/aaaa_1705483559044/cat-8361048_1280.jpg
			.addResourceLocations("file:///" + FileManagerService.FILE_UPLOAD_PATH); // 실제 이미지 파일 위치
//			.addResourceLocations("file://" + FileManagerService.FILE_UPLOAD_PATH); // 실제 이미지 파일 위치
									// mac os는 // 2개, window는 /// 3개
		
	}

}