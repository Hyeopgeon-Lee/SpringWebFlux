package kopo.poly.test;

import reactor.core.publisher.Flux;

public class FluxExample {

    public static void main(String[] args) {
        Flux<String> flux = Flux.just("A", "B", "C");

        // 구독(subscribe)하여 데이터 출력
        flux.subscribe(System.out::println);
    }
}
