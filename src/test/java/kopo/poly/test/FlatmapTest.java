package kopo.poly.test;

import reactor.core.publisher.Mono;

public class FlatmapTest {

    public static void main(String[] args) {
        Mono<String> mono = Mono.just("Hello, WebFlux")
                .map(data -> data.toUpperCase()) // 데이터를 대문자로 변환
                .map(data -> "변환된 데이터: " + data); // 문자열 추가

        mono.subscribe(System.out::println);

    }
}
