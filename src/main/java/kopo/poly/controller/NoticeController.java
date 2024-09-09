package kopo.poly.controller;

import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.service.INoticeService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@RequestMapping(value = "/notice")
@RequiredArgsConstructor
@Controller
public class NoticeController {

    private final INoticeService noticeService;

    /**
     * 게시판 리스트 보여주기
     */
    @GetMapping(value = "noticeList")
    public Mono<String> noticeList(ServerWebExchange exchange, ModelMap model) {
        log.info(this.getClass().getName() + ".noticeList Start!");

        // 세션에 사용자 아이디 저장 (로그인을 구현했다고 가정)
        Mono<Void> sessionMono = exchange.getSession().flatMap(webSession -> {
            webSession.getAttributes().put("SESSION_USER_ID", "USER01");
            return Mono.empty();
        });

        return sessionMono.then(noticeService.getNoticeList()
                .collectList()
                .map(rList -> {
                    model.addAttribute("rList", rList);
                    log.info(this.getClass().getName() + ".noticeList End!");
                    return "notice/noticeList";
                }));
    }

    /**
     * 게시판 작성 페이지 이동
     */
    @GetMapping(value = "noticeReg")
    public Mono<String> noticeReg() {
        log.info(this.getClass().getName() + ".noticeReg Start!");
        log.info(this.getClass().getName() + ".noticeReg End!");
        return Mono.just("notice/noticeReg");
    }

    /**
     * 게시판 글 등록
     */
    @ResponseBody
    @PostMapping(value = "noticeInsert")
    public Mono<MsgDTO> noticeInsert(ServerWebExchange exchange) {
        log.info(this.getClass().getName() + ".noticeInsert Start!");

        return exchange.getFormData().flatMap(formData -> {
            String userId = CmmUtil.nvl(Objects.requireNonNull(exchange.getSession().block()).getAttribute("SESSION_USER_ID"));
            String title = CmmUtil.nvl(formData.getFirst("title"));
            String noticeYn = CmmUtil.nvl(formData.getFirst("noticeYn"));
            String contents = CmmUtil.nvl(formData.getFirst("contents"));

            log.info("userId: {}", userId);
            log.info("title: {}", title);
            log.info("noticeYn: {}", noticeYn);
            log.info("contents: {}", contents);

            NoticeDTO pDTO = NoticeDTO.builder()
                    .userId(userId)
                    .title(title)
                    .noticeYn(noticeYn)
                    .contents(contents)
                    .build();

            return noticeService.insertNoticeInfo(pDTO)
                    .thenReturn(MsgDTO.builder().msg("등록되었습니다.").build());
        }).doFinally(signalType -> log.info(this.getClass().getName() + ".noticeInsert End!"));
    }

    /**
     * 게시판 상세보기
     */
    @GetMapping(value = "noticeInfo")
    public Mono<String> noticeInfo(ServerWebExchange exchange, ModelMap model) {
        log.info(this.getClass().getName() + ".noticeInfo Start!");

        String nSeq = exchange.getRequest().getQueryParams().getFirst("nSeq");

        return Mono.just(CmmUtil.nvl(nSeq, "0"))
                .flatMap(nSeqValue -> {
                    log.info("nSeq: {}", nSeqValue);

                    NoticeDTO pDTO = NoticeDTO.builder().noticeSeq(Long.parseLong(nSeqValue)).build();

                    return noticeService.getNoticeInfo(pDTO, true)
                            .map(rDTO -> {
                                model.addAttribute("rDTO", rDTO);
                                return "notice/noticeInfo";
                            });
                })
                .doFinally(signalType -> log.info(this.getClass().getName() + ".noticeInfo End!"));
    }

    /**
     * 게시판 글 수정
     */
    @ResponseBody
    @PostMapping(value = "noticeUpdate")
    public Mono<MsgDTO> noticeUpdate(ServerWebExchange exchange) {
        log.info(this.getClass().getName() + ".noticeUpdate Start!");

        return exchange.getFormData().flatMap(formData -> {
            String userId = CmmUtil.nvl(Objects.requireNonNull(exchange.getSession().block()).getAttribute("SESSION_USER_ID"));
            String nSeq = CmmUtil.nvl(formData.getFirst("nSeq"));
            String title = CmmUtil.nvl(formData.getFirst("title"));
            String noticeYn = CmmUtil.nvl(formData.getFirst("noticeYn"));
            String contents = CmmUtil.nvl(formData.getFirst("contents"));

            log.info("userId: {}", userId);
            log.info("nSeq: {}", nSeq);
            log.info("title: {}", title);
            log.info("noticeYn: {}", noticeYn);
            log.info("contents: {}", contents);

            NoticeDTO pDTO = NoticeDTO.builder()
                    .userId(userId)
                    .noticeSeq(Long.parseLong(nSeq))
                    .title(title)
                    .noticeYn(noticeYn)
                    .contents(contents)
                    .build();

            return noticeService.updateNoticeInfo(pDTO)
                    .thenReturn(MsgDTO.builder().msg("수정되었습니다.").build());
        }).doFinally(signalType -> log.info(this.getClass().getName() + ".noticeUpdate End!"));
    }

    /**
     * 게시판 글 삭제
     */
    @ResponseBody
    @PostMapping(value = "noticeDelete")
    public Mono<MsgDTO> noticeDelete(ServerWebExchange exchange) {
        log.info(this.getClass().getName() + ".noticeDelete Start!");

        return exchange.getFormData().flatMap(formData -> {
            String nSeq = CmmUtil.nvl(formData.getFirst("nSeq"));

            log.info("nSeq: {}", nSeq);

            NoticeDTO pDTO = NoticeDTO.builder().noticeSeq(Long.parseLong(nSeq)).build();

            return noticeService.deleteNoticeInfo(pDTO)
                    .thenReturn(MsgDTO.builder().msg("삭제되었습니다.").build());
        }).doFinally(signalType -> log.info(this.getClass().getName() + ".noticeDelete End!"));
    }
}
