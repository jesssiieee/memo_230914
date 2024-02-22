package com.memo.user.bo;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LambdaTest {

	// @Test
	void 람다테스트1() {
		List<String> fruits = List.of("apple", "banana", "bbbb");
		fruits
		.stream() // 계속 줄줄이 이어진다.
		.filter(fruit -> fruit.startsWith("b"))
		.forEach(fruit -> log.info("### {}", fruit));
		
	}
	
	@Test
	void 람다테스트2() {
		List<String> fruits = List.of("apple", "banana", "bbbb");
		fruits = fruits
		.stream()
		.map(fruit -> fruit.toUpperCase())
		.collect(Collectors.toList()); // stream to list
		
		log.info(fruits.toString());
	}
	
}
