package kopo.poly.service.impl;

import java.time.LocalDateTime;
import kopo.poly.domain.Notice;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.repository.NoticeRepository;
import kopo.poly.service.INoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class NoticeService implements INoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    public Flux<NoticeDTO> getNoticeList() {
        return noticeRepository.findAllByOrderByNoticeYnDescNoticeSeqDesc()
                .map(NoticeDTO::from);
    }

    @Override
    public Mono<NoticeDTO> getNoticeInfo(NoticeDTO pDTO, boolean increaseViews) {
        Long noticeSeq = pDTO.noticeSeq();
        return noticeRepository.findById(noticeSeq)
                .switchIfEmpty(Mono.error(notFound()))
                .flatMap(notice -> {
                    if (!increaseViews) {
                        return Mono.just(notice);
                    }
                    // @Modifying 쿼리가 실제로 적용된 뒤 최신 조회수를 읽는다.
                    return noticeRepository.increaseReadCount(noticeSeq)
                            .flatMap(rows -> rows > 0
                                    ? noticeRepository.findById(noticeSeq)
                                    : Mono.empty());
                })
                .switchIfEmpty(Mono.error(notFound()))
                .map(NoticeDTO::from);
    }

    @Override
    public Mono<Void> updateNoticeInfo(NoticeDTO pDTO) {
        return noticeRepository.findById(pDTO.noticeSeq())
                .switchIfEmpty(Mono.error(notFound()))
                .flatMap(existing -> {
                    // 작성자/등록일/조회수는 유지하고 변경 이력만 갱신한다.
                    Notice updated = Notice.builder()
                            .noticeSeq(existing.getNoticeSeq())
                            .title(pDTO.title())
                            .noticeYn(pDTO.noticeYn())
                            .contents(pDTO.contents())
                            .userId(existing.getUserId())
                            .readCnt(existing.getReadCnt())
                            .regId(existing.getRegId())
                            .regDt(existing.getRegDt())
                            .chgId(pDTO.userId())
                            .chgDt(LocalDateTime.now())
                            .build();
                    return noticeRepository.save(updated);
                })
                .then();
    }

    @Override
    public Mono<Void> insertNoticeInfo(NoticeDTO pDTO) {
        return noticeRepository.save(NoticeDTO.to(pDTO)).then();
    }

    @Override
    public Mono<Void> deleteNoticeInfo(NoticeDTO pDTO) {
        return noticeRepository.deleteByNoticeSeq(pDTO.noticeSeq())
                .flatMap(rows -> rows > 0 ? Mono.<Void>empty() : Mono.error(notFound()));
    }

    private static ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "공지사항이 존재하지 않습니다.");
    }
}
