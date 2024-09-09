package kopo.poly.service;

import kopo.poly.dto.NoticeDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface INoticeService {

    /**
     * 공지사항 전체 가져오기
     */
    Flux<NoticeDTO> getNoticeList();

    /**
     * 공지사항 상세 정보 가져오기
     *
     * @param pDTO 공지사항 상세 가져오기 위한 정보
     * @param type 조회수 증가 여부 (true: 증가, false: 증가 안함)
     */
    Mono<NoticeDTO> getNoticeInfo(NoticeDTO pDTO, boolean type);

    /**
     * 해당 공지사항 수정하기
     *
     * @param pDTO 공지사항 수정하기 위한 정보
     */
    Mono<Void> updateNoticeInfo(NoticeDTO pDTO);

    /**
     * 해당 공지사항 삭제하기
     *
     * @param pDTO 공지사항 삭제하기 위한 정보
     */
    Mono<Void> deleteNoticeInfo(NoticeDTO pDTO);

    /**
     * 해당 공지사항 저장하기
     *
     * @param pDTO 공지사항 저장하기 위한 정보
     */
    Mono<Void> insertNoticeInfo(NoticeDTO pDTO);
}
