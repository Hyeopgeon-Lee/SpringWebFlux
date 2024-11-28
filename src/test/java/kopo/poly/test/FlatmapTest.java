package kopo.poly.test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FlatmapTest {

    public static void main(String[] args) {
        Flux<String> flux = Flux.just("User1", "User2", "User3")
                .flatMap(userId -> {
                    // 비동기 작업: 사용자 이름 가져오기
                    return Mono.just(userId + " - 학생들");
                })
                .map(userInfo -> {
                    // 데이터 변환: 대문자로 변경
                    return userInfo.toUpperCase();
                });

        flux.subscribe(System.out::println);

    }
}
