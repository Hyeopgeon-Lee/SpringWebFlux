package kopo.poly.controller;

import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.service.INoticeService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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
    public Mono<String> noticeList(ServerWebExchange exchange, Model model) {
        log.info("{}.noticeList Start!", getClass().getName());

        return exchange.getSession()
                .doOnNext(session -> session.getAttributes().put("SESSION_USER_ID", "USER01"))
                .then(noticeService.getNoticeList().collectList())
                .map(rList -> {
                    model.addAttribute("rList", rList);
                    log.info("{}.noticeList End!", getClass().getName());
                    return "notice/noticeList";
                });
    }

    /**
     * 게시판 작성 페이지 이동
     */
    @GetMapping(value = "noticeReg")
    public Mono<String> noticeReg() {
        log.info("{}.noticeReg Start and End!", getClass().getName());
        return Mono.just("notice/noticeReg");
    }

    /**
     * 게시판 글 등록
     */
    @ResponseBody
    @PostMapping(value = "noticeInsert")
    public Mono<MsgDTO> noticeInsert(ServerWebExchange exchange) {
        log.info("{}.noticeInsert Start!", getClass().getName());

        return exchange.getSession()
                .flatMap(session -> exchange.getFormData().map(formData -> {
                    String userId = CmmUtil.nvl(session.getAttribute("SESSION_USER_ID"));
                    NoticeDTO pDTO = NoticeDTO.builder()
                            .userId(userId)
                            .title(CmmUtil.nvl(formData.getFirst("title")))
                            .noticeYn(CmmUtil.nvl(formData.getFirst("noticeYn")))
                            .contents(CmmUtil.nvl(formData.getFirst("contents")))
                            .build();
                    log.info("DTO: {}", pDTO);
                    return pDTO;
                }))
                .flatMap(noticeService::insertNoticeInfo)
                .thenReturn(MsgDTO.builder().msg("등록되었습니다.").build())
                .doFinally(signalType -> log.info("{}.noticeInsert End!", getClass().getName()));
    }

    /**
     * 게시판 상세보기
     */
    @GetMapping(value = "noticeInfo")
    public Mono<String> noticeInfo(ServerWebExchange exchange, Model model) {
        log.info("{}.noticeInfo Start!", getClass().getName());

        return Mono.justOrEmpty(exchange.getRequest().getQueryParams().getFirst("nSeq"))
                .map(nSeq -> CmmUtil.nvl(nSeq, "0"))
                .flatMap(nSeq -> {
                    NoticeDTO pDTO = NoticeDTO.builder().noticeSeq(Long.parseLong(nSeq)).build();
                    return noticeService.getNoticeInfo(pDTO, true)
                            .map(rDTO -> {
                                model.addAttribute("rDTO", rDTO);
                                return "notice/noticeInfo";
                            });
                })
                .doFinally(signalType -> log.info("{}.noticeInfo End!", getClass().getName()));
    }

    /**
     * 게시판 수정보기
     */
    @GetMapping(value = "noticeEditInfo")
    public Mono<String> noticeEditInfo(ServerWebExchange exchange, Model model) {
        log.info("{}.noticeEditInfo Start!", getClass().getName());

        return Mono.justOrEmpty(exchange.getRequest().getQueryParams().getFirst("nSeq"))
                .map(nSeq -> CmmUtil.nvl(nSeq, "0"))
                .flatMap(nSeq -> {
                    NoticeDTO pDTO = NoticeDTO.builder().noticeSeq(Long.parseLong(nSeq)).build();
                    return noticeService.getNoticeInfo(pDTO, false)
                            .map(rDTO -> {
                                model.addAttribute("rDTO", rDTO);
                                return "notice/noticeEditInfo";
                            });
                })
                .doFinally(signalType -> log.info("{}.noticeEditInfo End!", getClass().getName()));
    }

    /**
     * 게시판 글 수정
     */
    @ResponseBody
    @PostMapping(value = "noticeUpdate")
    public Mono<MsgDTO> noticeUpdate(ServerWebExchange exchange) {
        log.info("{}.noticeUpdate Start!", getClass().getName());

        return exchange.getSession()
                .flatMap(session -> exchange.getFormData().map(formData -> {
                    String userId = CmmUtil.nvl(session.getAttribute("SESSION_USER_ID"));
                    NoticeDTO pDTO = NoticeDTO.builder()
                            .userId(userId)
                            .noticeSeq(Long.parseLong(CmmUtil.nvl(formData.getFirst("nSeq"))))
                            .title(CmmUtil.nvl(formData.getFirst("title")))
                            .noticeYn(CmmUtil.nvl(formData.getFirst("noticeYn")))
                            .contents(CmmUtil.nvl(formData.getFirst("contents")))
                            .build();
                    log.info("DTO: {}", pDTO);
                    return pDTO;
                }))
                .flatMap(noticeService::updateNoticeInfo)
                .thenReturn(MsgDTO.builder().msg("수정되었습니다.").build())
                .doFinally(signalType -> log.info("{}.noticeUpdate End!", getClass().getName()));
    }

    /**
     * 게시판 글 삭제
     */
    @ResponseBody
    @PostMapping(value = "noticeDelete")
    public Mono<MsgDTO> noticeDelete(ServerWebExchange exchange) {
        log.info("{}.noticeDelete Start!", getClass().getName());

        return exchange.getFormData()
                .map(formData -> NoticeDTO.builder()
                        .noticeSeq(Long.parseLong(CmmUtil.nvl(formData.getFirst("nSeq"))))
                        .build())
                .flatMap(noticeService::deleteNoticeInfo)
                .thenReturn(MsgDTO.builder().msg("삭제되었습니다.").build())
                .doFinally(signalType -> log.info("{}.noticeDelete End!", getClass().getName()));
    }
}
