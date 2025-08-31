package com.acc.global.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PrintUtil {
	/**
	 * Map<String, Object>를 JSON과 유사한 형태로 예쁘게 출력합니다.
	 * @param data 출력할 Map 데이터
	 */
	public static void printMap(Map<String, ?> data) {
		if (data == null) {
			System.out.println("null");
			return;
		}
		// 초기 깊이 0으로 재귀 함수 호출 시작
		printMapRecursive(data, 0);
	}

	/**
	 * Map 데이터를 재귀적으로 출력하는 내부 메서드
	 * @param map 현재 출력할 Map
	 * @param depth 현재 데이터의 깊이 (들여쓰기에 사용)
	 */
	private static void printMapRecursive(Map<String, ?> map, int depth) {
		System.out.println("{");

		// 마지막 요소 뒤에 콤마(,)를 찍지 않기 위해 Iterator 사용
		Iterator<? extends Map.Entry<String, ?>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>)iterator.next();

			// 현재 깊이 * 2만큼 들여쓰기
			System.out.print(getIndent(depth + 1));

			// Key 출력
			System.out.print("\"" + entry.getKey() + "\": ");

			// Value 출력
			printValue(entry.getValue(), depth + 1);

			// 마지막 요소가 아니면 콤마 추가
			if (iterator.hasNext()) {
				System.out.println(",");
			} else {
				System.out.println(); // 마지막 요소는 줄바꿈만
			}
		}

		System.out.print(getIndent(depth) + "}");
	}

	/**
	 * List 데이터를 재귀적으로 출력
	 * @param list 현재 출력할 List
	 * @param depth 현재 데이터의 깊이
	 */
	private static void printListRecursive(List<?> list, int depth) {
		System.out.println("[");

		Iterator<?> iterator = list.iterator();
		while (iterator.hasNext()) {
			Object item = iterator.next();
			System.out.print(getIndent(depth + 1));
			printValue(item, depth + 1);

			if (iterator.hasNext()) {
				System.out.println(",");
			} else {
				System.out.println();
			}
		}

		System.out.print(getIndent(depth) + "]");
	}

	/**
	 * Object의 타입에 따라 적절한 형태로 값을 출력
	 * @param value 출력할 값
	 * @param depth 현재 깊이
	 */
	@SuppressWarnings("unchecked")
	private static void printValue(Object value, int depth) {
		if (value == null) {
			System.out.print("null");
		} else if (value instanceof String) {
			System.out.print("\"" + value + "\"");
		} else if (value instanceof Map) {
			// 값이 Map인 경우, 재귀 호출
			printMapRecursive((Map<String, Object>) value, depth);
		} else if (value instanceof List) {
			// 값이 List인 경우, List 출력 메서드 호출
			printListRecursive((List<?>) value, depth);
		} else {
			// Primitive 타입 또는 그 외 Object는 toString()으로 출력
			System.out.print(value.toString());
		}
	}

	/**
	 * 깊이에 따른 들여쓰기 공백 문자열을 생성
	 * @param depth 깊이
	 * @return "  " * depth 만큼의 공백 문자열
	 */
	private static String getIndent(int depth) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < depth * 2; i++) {
			sb.append(" ");
		}
		return sb.toString();
	}
}
