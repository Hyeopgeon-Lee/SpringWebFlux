package kopo.poly.dto;

import kopo.poly.domain.Notice;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import lombok.Builder;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Builder
public record NoticeDTO(

        Long noticeSeq, // 기본키, 순번
        String title, // 제목
        String noticeYn, // 공지글 여부
        String contents, // 글 내용
        String userId, // 작성자
        Long readCnt, // 조회수
        String regId, // 등록자 아이디
        String regDt, // 등록일
        String chgId, // 수정자 아이디
        String chgDt, // 수정일
        String userName // 등록자명
) {

    /**
     * 도메인을 DTO로 변환
     *
     * @param notice 공지사항 정보
     * @return NoticeDTO
     */
    public static NoticeDTO from(Notice notice) {

        return NoticeDTO.builder()
                .noticeSeq(notice.getNoticeSeq())
                .title(notice.getTitle())
                .noticeYn(notice.getNoticeYn())
                .contents(notice.getContents())
                .userId(notice.getUserId())
                .readCnt(notice.getReadCnt())
                .regId(notice.getRegId())
                .regDt(Optional.ofNullable(notice.getRegDt()).map(dt -> dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).orElse(null))
                .chgId(notice.getChgId())
                .chgDt(Optional.ofNullable(notice.getChgDt()).map(dt -> dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).orElse(null))
                .build();
    }


    /**
     * 도메인을 DTO로 변환
     *
     * @param dto 공지사항 정보
     * @return Notice
     */
    public static Notice to(NoticeDTO dto) {

        return Notice.builder()
                .noticeSeq(dto.noticeSeq())
                .title(dto.title())
                .noticeYn(dto.noticeYn())
                .contents(dto.contents())
                .userId(dto.userId())
                .readCnt(CmmUtil.nvl(dto.readCnt(), 0L))
                .regId(CmmUtil.nvl(dto.regId(), dto.userId))
                .regDt(DateUtil.stringToLocalDateTime(dto.regDt()))
                .chgId(CmmUtil.nvl(dto.chgId(), dto.userId))
                .chgDt(DateUtil.stringToLocalDateTime(dto.chgDt()))
                .build();
    }
}