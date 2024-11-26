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

@Slf4j
@RequiredArgsConstructor
@Service
public class NoticeService implements INoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    public Flux<NoticeDTO> getNoticeList() {
        log.info("getNoticeList Start!");

        return noticeRepository.findAllByOrderByNoticeYnDescNoticeSeqDesc()
                .switchIfEmpty(Flux.defer(() -> {
                    log.info("No data found!"); // 빈 결과일 때 로그 출력
                    return Flux.empty(); // 빈 Flux 반환
                }))
                .map(NoticeDTO::from)
                .doFinally(signalType -> log.info("getNoticeList End!"));
    }


    @Override
    public Mono<NoticeDTO> getNoticeInfo(NoticeDTO pDTO, boolean type) {
        log.info("getNoticeInfo Start!");

        return noticeRepository.findByNoticeSeq(pDTO.noticeSeq())
                .flatMap(rEntity -> {
                    if (type) {
                        // 조회수 증가 후 다시 데이터 조회
                        return noticeRepository.updateReadCnt(pDTO.noticeSeq())
                                .then(noticeRepository.findByNoticeSeq(pDTO.noticeSeq()));
                    } else {
                        // 조회수 증가 없이 바로 반환
                        return Mono.just(rEntity);
                    }
                })
                .map(NoticeDTO::from) // 명시적 캐스팅 제거
                .doFinally(signalType -> log.info("getNoticeInfo End!"));
    }


    @Override
    public Mono<Void> updateNoticeInfo(NoticeDTO pDTO) {
        log.info("updateNoticeInfo Start!");

        return noticeRepository.findByNoticeSeq(pDTO.noticeSeq())
                .flatMap(rEntity -> {
                    Notice updatedEntity = Notice.builder()
                            .noticeSeq(pDTO.noticeSeq())
                            .title(CmmUtil.nvl(pDTO.title()))
                            .noticeYn(CmmUtil.nvl(pDTO.noticeYn()))
                            .contents(CmmUtil.nvl(pDTO.contents()))
                            .userId(CmmUtil.nvl(pDTO.userId()))
                            .readCnt(rEntity.getReadCnt())  // 기존 조회수 유지
                            .build();
                    return noticeRepository.save(updatedEntity).then(); // Mono<Void> 반환
                })
                .doFinally(signalType -> log.info("updateNoticeInfo End!"));
    }


    @Override
    public Mono<Void> insertNoticeInfo(NoticeDTO pDTO) {
        log.info("insertNoticeInfo Start!");

        return noticeRepository.save(NoticeDTO.to(pDTO))
                .then() // Mono<Notice>를 Mono<Void>로 변환
                .doFinally(signalType -> log.info("insertNoticeInfo End!"));
    }

    @Override
    public Mono<Void> deleteNoticeInfo(NoticeDTO pDTO) {
        log.info("deleteNoticeInfo Start!");

        return noticeRepository.deleteById(pDTO.noticeSeq())
                .doFinally(signalType -> log.info("deleteNoticeInfo End!"));
    }


}
