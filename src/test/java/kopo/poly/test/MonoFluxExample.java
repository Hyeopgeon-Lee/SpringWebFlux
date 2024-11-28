package kopo.poly.test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MonoFluxExample {

    public static void main(String[] args) {
        // 단일 데이터 생성 (Mono)
        Mono<String> mono = Mono.just("User ID: 12345");

        // 다수의 데이터 생성 (Flux)
        Flux<String> flux = Flux.just("Order1", "Order2", "Order3");

        // Mono와 Flux 조합
        flux.flatMap(order -> mono.map(user -> user + " - " + order))
                .subscribe(System.out::println);
    }

}
