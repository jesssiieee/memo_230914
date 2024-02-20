package com.memo;



import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class EnumTest {
	
	public enum Status { 	// Status.Y <- 사용 형식
		
		// 열거형 정의
		Y(1, true), 
		N(0, false)
		;
		
		// enum에 정의된 항목의 필드
		private int value1;
		private boolean value2;
		
		// 생성자
		Status(int value1, boolean value2) {
			this.value1 = value1;
			this.value2 = value2;
		}

		public int getValue1() {
			return value1;
		}

		public void setValue1(int value1) {
			this.value1 = value1;
		}

		public boolean isValue2() {
			return value2;
		}

		public void setValue2(boolean value2) {
			this.value2 = value2;
		}
		
		
		
	}
	
	@Test
	void status테스트() {
		
		// given(준비)
		Status status = Status.Y;
		
		// when(실행)
		int value1 = status.getValue1();
		boolean value2 = status.isValue2();
		
		// then(검증)
		assertEquals(status, Status.Y);
		assertEquals(1, value1);
		assertEquals(true, value2);
		
	}
	
	
}
