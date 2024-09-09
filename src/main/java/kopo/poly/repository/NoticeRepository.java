package kopo.poly.repository;

import kopo.poly.domain.Notice;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface NoticeRepository extends ReactiveCrudRepository<Notice, Long> {

    /**
     * 공지사항 리스트를 공지사항 시퀀스 역순으로 조회
     */
    Flux<Notice> findAllByOrderByNoticeYnDescNoticeSeqDesc();

    /**
     * 공지사항 상세 조회
     *
     * @param noticeSeq 공지사항 PK
     */
    Mono<Notice> findByNoticeSeq(Long noticeSeq);

    /**
     * 공지사항 상세 보기할 때, 조회수 증가하기
     *
     * @param noticeSeq 공지사항 PK
     */
    @Query("UPDATE NOTICE SET read_cnt = IFNULL(read_cnt, 0) + 1 WHERE notice_seq = :noticeSeq")
    Mono<Void> updateReadCnt(Long noticeSeq);
}
