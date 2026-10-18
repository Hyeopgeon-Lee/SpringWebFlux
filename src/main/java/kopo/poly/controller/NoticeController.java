package kopo.poly.controller;

import java.nio.charset.StandardCharsets;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.service.INoticeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/notice")
public class NoticeController {

    private final INoticeService noticeService;
    private final String demoUserId;

    // 로그인 기능이 없는 공개 CRUD 실습용 공용 사용자 (실제 인증이 아님).
    public NoticeController(INoticeService noticeService,
                            @Value("${app.demo-user-id:USER01}") String demoUserId) {
        this.noticeService = noticeService;
        this.demoUserId = demoUserId;
    }

    @GetMapping("/noticeList")
    public Mono<String> noticeList(Model model) {
        return noticeService.getNoticeList().collectList()
                .map(notices -> {
                    model.addAttribute("rList", notices);
                    return "notice/noticeList";
                });
    }

    @GetMapping("/noticeReg")
    public Mono<String> noticeReg() {
        return Mono.just("notice/noticeReg");
    }

    @ResponseBody
    @PostMapping("/noticeInsert")
    public Mono<MsgDTO> noticeInsert(ServerWebExchange exchange) {
        return exchange.getFormData()
                .map(form -> toNotice(form, false))
                .flatMap(noticeService::insertNoticeInfo)
                .thenReturn(success("등록되었습니다."));
    }

    @GetMapping("/noticeInfo")
    public Mono<String> noticeInfo(@RequestParam("nSeq") String nSeq, Model model) {
        return noticeService.getNoticeInfo(lookup(nSeq), true)
                .map(notice -> {
                    model.addAttribute("rDTO", notice);
                    return "notice/noticeInfo";
                });
    }

    @GetMapping("/noticeEditInfo")
    public Mono<String> noticeEditInfo(@RequestParam("nSeq") String nSeq, Model model) {
        return noticeService.getNoticeInfo(lookup(nSeq), false)
                .map(notice -> {
                    model.addAttribute("rDTO", notice);
                    return "notice/noticeEditInfo";
                });
    }

    @ResponseBody
    @PostMapping("/noticeUpdate")
    public Mono<MsgDTO> noticeUpdate(ServerWebExchange exchange) {
        return exchange.getFormData()
                .map(form -> toNotice(form, true))
                .flatMap(noticeService::updateNoticeInfo)
                .thenReturn(success("수정되었습니다."));
    }

    @ResponseBody
    @PostMapping("/noticeDelete")
    public Mono<MsgDTO> noticeDelete(ServerWebExchange exchange) {
        return exchange.getFormData()
                .map(form -> lookup(form.getFirst("nSeq")))
                .flatMap(noticeService::deleteNoticeInfo)
                .thenReturn(success("삭제되었습니다."));
    }

    private NoticeDTO lookup(String value) {
        return NoticeDTO.builder().noticeSeq(parseNoticeSeq(value)).build();
    }

    private NoticeDTO toNotice(MultiValueMap<String, String> form, boolean update) {
        String title = requireText(form.getFirst("title"), "제목", 200);
        String contents = requireText(form.getFirst("contents"), "내용", 4000);
        String noticeYn = form.getFirst("noticeYn");
        if (!"Y".equals(noticeYn) && !"N".equals(noticeYn)) {
            throw badRequest("공지글 여부는 Y 또는 N이어야 합니다.");
        }
        return NoticeDTO.builder()
                .noticeSeq(update ? parseNoticeSeq(form.getFirst("nSeq")) : null)
                .title(title)
                .contents(contents)
                .noticeYn(noticeYn)
                .userId(demoUserId)
                .build();
    }

    private static Long parseNoticeSeq(String value) {
        try {
            long id = Long.parseLong(value);
            if (id > 0) {
                return id;
            }
        } catch (NumberFormatException | NullPointerException ignored) {
            // 유효하지 않은 값은 공통 400 응답으로 처리한다.
        }
        throw badRequest("유효한 공지사항 번호가 필요합니다.");
    }

    private static String requireText(String value, String field, int maxUtf8Bytes) {
        if (value == null || value.isBlank()) {
            throw badRequest(field + "을(를) 입력해 주세요.");
        }
        String normalized = value.trim();
        if (normalized.getBytes(StandardCharsets.UTF_8).length > maxUtf8Bytes) {
            throw badRequest(field + "은(는) UTF-8 기준 " + maxUtf8Bytes + "바이트 이하여야 합니다.");
        }
        return normalized;
    }

    private static ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private static MsgDTO success(String message) {
        return MsgDTO.builder().result(1).msg(message).build();
    }
}
