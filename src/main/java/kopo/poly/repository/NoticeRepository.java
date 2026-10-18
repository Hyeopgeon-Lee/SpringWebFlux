package kopo.poly.repository;

import kopo.poly.domain.Notice;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NoticeRepository extends ReactiveCrudRepository<Notice, Long> {

    Flux<Notice> findAllByOrderByNoticeYnDescNoticeSeqDesc();

    @Modifying
    @Query("UPDATE notice SET read_cnt = COALESCE(read_cnt, 0) + 1 WHERE notice_seq = :noticeSeq")
    Mono<Integer> increaseReadCount(Long noticeSeq);

    @Modifying
    @Query("DELETE FROM notice WHERE notice_seq = :noticeSeq")
    Mono<Integer> deleteByNoticeSeq(Long noticeSeq);
}
