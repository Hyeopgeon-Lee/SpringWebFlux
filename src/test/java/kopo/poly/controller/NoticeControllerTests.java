package kopo.poly.controller;

import kopo.poly.dto.NoticeDTO;
import kopo.poly.service.INoticeService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NoticeControllerTests {

    private final INoticeService service = mock(INoticeService.class);
    private final NoticeController controller = new NoticeController(service, "USER01");

    @Test
    void validInsertReturnsSuccessResultOne() {
        when(service.insertNoticeInfo(any(NoticeDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(controller.noticeInsert(form("title=Hello&contents=World&noticeYn=N")))
                .assertNext(message -> {
                    assertEquals(1, message.result());
                    assertEquals("등록되었습니다.", message.msg());
                })
                .verifyComplete();

        var captor = org.mockito.ArgumentCaptor.forClass(NoticeDTO.class);
        verify(service).insertNoticeInfo(captor.capture());
        assertEquals("USER01", captor.getValue().userId());
        assertNull(captor.getValue().noticeSeq());
    }

    @Test
    void tooLongUtf8TitleIsRejected() {
        String title = "a".repeat(201);
        StepVerifier.create(controller.noticeInsert(form("title=" + title + "&contents=World&noticeYn=N")))
                .expectErrorSatisfies(this::assertBadRequest)
                .verify();
        verifyNoInteractions(service);
    }

    @Test
    void invalidNoticeIdIsRejected() {
        StepVerifier.create(controller.noticeDelete(form("nSeq=not-a-number")))
                .expectErrorSatisfies(this::assertBadRequest)
                .verify();
        verifyNoInteractions(service);
    }

    @Test
    void unsupportedNoticeFlagIsRejected() {
        StepVerifier.create(controller.noticeInsert(form("title=Hello&contents=World&noticeYn=INVALID")))
                .expectErrorSatisfies(this::assertBadRequest)
                .verify();
        verifyNoInteractions(service);
    }

    private static MockServerWebExchange form(String payload) {
        return MockServerWebExchange.from(MockServerHttpRequest
                .post("/notice")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(payload));
    }

    private void assertBadRequest(Throwable error) {
        assertInstanceOf(ResponseStatusException.class, error);
        assertEquals(400, ((ResponseStatusException) error).getStatusCode().value());
    }
}
