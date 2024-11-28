package kopo.poly.test;

import reactor.core.publisher.Mono;

public class LazyEvaluation {

    public static void main(String[] args) {

        // Mono 정의 (데이터 생성 로직)
        Mono<String> mono = Mono.defer(() -> {
            System.out.println("데이터 생성 중...");
            return Mono.just("Hello, WebFlux!");
        });

        System.out.println("Mono 정의 완료");

        // 구독 시점 (이 시점에만 데이터 생성 실행)
        mono.subscribe(data -> System.out.println("데이터 수신: " + data));
    }
}
