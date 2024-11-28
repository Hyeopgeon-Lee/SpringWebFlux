package kopo.poly.test;

import reactor.core.publisher.Mono;

public class MonoEaxmple {

    public static void main(String[] args) {
        Mono<String> mono = Mono.just("Hello, Mono!");

        // 구독(subscribe)하여 데이터 출력
        mono.subscribe(System.out::println);
    }

}
