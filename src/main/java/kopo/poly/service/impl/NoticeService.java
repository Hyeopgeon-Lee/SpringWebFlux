package kopo.poly.service.impl;

import kopo.poly.domain.Notice;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.repository.NoticeRepository;
import kopo.poly.service.INoticeService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 공지사항 서비스 구현 클래스
 * Reactive 프로그래밍을 사용하여 공지사항 데이터를 처리
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class NoticeService implements INoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * 공지사항 목록 조회
     *
     * @return 공지사항 리스트(Flux<NoticeDTO>)
     */
    @Override
    public Flux<NoticeDTO> getNoticeList() {
        log.info("getNoticeList Start!");

        return noticeRepository.findAllByOrderByNoticeYnDescNoticeSeqDesc()
                // 공지사항이 없을 경우 빈 Flux 반환
                .switchIfEmpty(Flux.defer(() -> {
                    log.info("No data found!"); // 빈 결과일 때 로그 출력
                    return Flux.empty();
                }))
                // Notice 엔티티를 NoticeDTO로 변환
                .map(NoticeDTO::from)
                // 작업 완료 후 로그 출력
                .doFinally(signalType -> log.info("getNoticeList End!"));
    }

    /**
     * 공지사항 상세 조회
     *
     * @param pDTO 조회할 공지사항의 DTO
     * @param type 조회수 증가 여부
     * @return 공지사항 상세 정보(Mono<NoticeDTO>)
     */
    @Override
    public Mono<NoticeDTO> getNoticeInfo(NoticeDTO pDTO, boolean type) {
        log.info("getNoticeInfo Start!");

        return noticeRepository.findByNoticeSeq(pDTO.noticeSeq())
                .flatMap(rEntity -> {
                    if (type) {
                        // 조회수를 증가시키고 다시 데이터를 조회
                        return noticeRepository.updateReadCnt(pDTO.noticeSeq())
                                .then(noticeRepository.findByNoticeSeq(pDTO.noticeSeq()));
                    } else {
                        // 조회수 증가 없이 데이터 반환
                        return Mono.just(rEntity);
                    }
                })
                // Notice 엔티티를 NoticeDTO로 변환
                .map(NoticeDTO::from)
                // 작업 완료 후 로그 출력
                .doFinally(signalType -> log.info("getNoticeInfo End!"));
    }

    /**
     * 공지사항 수정
     *
     * @param pDTO 수정할 공지사항의 DTO
     * @return 작업 완료(Mono<Void>)
     */
    @Override
    public Mono<Void> updateNoticeInfo(NoticeDTO pDTO) {
        log.info("updateNoticeInfo Start!");

        return noticeRepository.findByNoticeSeq(pDTO.noticeSeq())
                .flatMap(rEntity -> {
                    // 기존 데이터와 수정 데이터를 합쳐 새로운 엔티티 생성
                    Notice updatedEntity = Notice.builder()
                            .noticeSeq(pDTO.noticeSeq())
                            .title(CmmUtil.nvl(pDTO.title())) // Null 값 방지
                            .noticeYn(CmmUtil.nvl(pDTO.noticeYn()))
                            .contents(CmmUtil.nvl(pDTO.contents()))
                            .userId(CmmUtil.nvl(pDTO.userId()))
                            .readCnt(rEntity.getReadCnt()) // 기존 조회수 유지
                            .build();
                    // 수정된 엔티티 저장
                    return noticeRepository.save(updatedEntity).then(); // Mono<Void> 반환
                })
                // 작업 완료 후 로그 출력
                .doFinally(signalType -> log.info("updateNoticeInfo End!"));
    }

    /**
     * 공지사항 추가
     *
     * @param pDTO 추가할 공지사항의 DTO
     * @return 작업 완료(Mono<Void>)
     */
    @Override
    public Mono<Void> insertNoticeInfo(NoticeDTO pDTO) {
        log.info("insertNoticeInfo Start!");

        // DTO를 엔티티로 변환 후 저장
        return noticeRepository.save(NoticeDTO.to(pDTO))
                .then() // Mono<Notice>를 Mono<Void>로 변환
                // 작업 완료 후 로그 출력
                .doFinally(signalType -> log.info("insertNoticeInfo End!"));
    }

    /**
     * 공지사항 삭제
     *
     * @param pDTO 삭제할 공지사항의 DTO
     * @return 작업 완료(Mono<Void>)
     */
    @Override
    public Mono<Void> deleteNoticeInfo(NoticeDTO pDTO) {
        log.info("deleteNoticeInfo Start!");

        // 공지사항 ID로 데이터 삭제
        return noticeRepository.deleteById(pDTO.noticeSeq())
                // 작업 완료 후 로그 출력
                .doFinally(signalType -> log.info("deleteNoticeInfo End!"));
    }
}
