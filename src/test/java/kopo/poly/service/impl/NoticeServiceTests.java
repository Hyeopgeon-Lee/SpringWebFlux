package kopo.poly.service.impl;

import java.time.LocalDateTime;
import kopo.poly.domain.Notice;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.repository.NoticeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NoticeServiceTests {

    private final NoticeRepository repository = mock(NoticeRepository.class);
    private final NoticeService service = new NoticeService(repository);

    @Test
    void detailIncrementsReadCountBeforeReturningData() {
        Notice first = notice(3L);
        Notice updated = notice(4L);
        when(repository.findById(7L)).thenReturn(Mono.just(first), Mono.just(updated));
        when(repository.increaseReadCount(7L)).thenReturn(Mono.just(1));

        StepVerifier.create(service.getNoticeInfo(id(7L), true))
                .assertNext(dto -> assertEquals(4L, dto.readCnt()))
                .verifyComplete();

        verify(repository).increaseReadCount(7L);
        verify(repository, times(2)).findById(7L);
    }

    @Test
    void detailWithoutViewIncrementDoesNotWrite() {
        when(repository.findById(7L)).thenReturn(Mono.just(notice(3L)));

        StepVerifier.create(service.getNoticeInfo(id(7L), false))
                .assertNext(dto -> assertEquals(3L, dto.readCnt()))
                .verifyComplete();

        verify(repository, never()).increaseReadCount(any());
    }

    @Test
    void updatePreservesOriginalAuditAndReadCount() {
        Notice original = notice(8L);
        when(repository.findById(7L)).thenReturn(Mono.just(original));
        when(repository.save(any(Notice.class))).thenAnswer(invocation ->
                Mono.just(invocation.getArgument(0, Notice.class)));

        NoticeDTO changes = NoticeDTO.builder()
                .noticeSeq(7L).title("수정 제목").contents("수정 내용")
                .noticeYn("Y").userId("EDITOR").build();
        StepVerifier.create(service.updateNoticeInfo(changes)).verifyComplete();

        var captor = org.mockito.ArgumentCaptor.forClass(Notice.class);
        verify(repository).save(captor.capture());
        Notice saved = captor.getValue();
        assertEquals("AUTHOR", saved.getUserId());
        assertEquals("CREATOR", saved.getRegId());
        assertEquals(original.getRegDt(), saved.getRegDt());
        assertEquals(8L, saved.getReadCnt());
        assertEquals("EDITOR", saved.getChgId());
        assertNotNull(saved.getChgDt());
        assertEquals("수정 제목", saved.getTitle());
    }

    @Test
    void missingDetailReturns404() {
        when(repository.findById(42L)).thenReturn(Mono.empty());

        StepVerifier.create(service.getNoticeInfo(id(42L), true))
                .expectErrorSatisfies(this::assertNotFound)
                .verify();
    }

    @Test
    void missingUpdateReturns404() {
        when(repository.findById(42L)).thenReturn(Mono.empty());

        StepVerifier.create(service.updateNoticeInfo(id(42L)))
                .expectErrorSatisfies(this::assertNotFound)
                .verify();

        verify(repository, never()).save(any(Notice.class));
    }

    @Test
    void missingDeleteReturns404() {
        when(repository.deleteByNoticeSeq(42L)).thenReturn(Mono.just(0));

        StepVerifier.create(service.deleteNoticeInfo(id(42L)))
                .expectErrorSatisfies(this::assertNotFound)
                .verify();
    }

    @Test
    void existingDeleteCompletes() {
        when(repository.deleteByNoticeSeq(7L)).thenReturn(Mono.just(1));
        StepVerifier.create(service.deleteNoticeInfo(id(7L))).verifyComplete();
    }

    @Test
    void insertPopulatesAuditFields() {
        when(repository.save(any(Notice.class))).thenAnswer(invocation ->
                Mono.just(invocation.getArgument(0, Notice.class)));

        NoticeDTO input = NoticeDTO.builder()
                .title("공지").contents("내용").noticeYn("N").userId("USER01").build();
        StepVerifier.create(service.insertNoticeInfo(input)).verifyComplete();

        var captor = org.mockito.ArgumentCaptor.forClass(Notice.class);
        verify(repository).save(captor.capture());
        assertNull(captor.getValue().getNoticeSeq());
        assertEquals(0L, captor.getValue().getReadCnt());
        assertEquals("USER01", captor.getValue().getRegId());
        assertNotNull(captor.getValue().getRegDt());
    }

    private void assertNotFound(Throwable error) {
        assertInstanceOf(ResponseStatusException.class, error);
        assertEquals(404, ((ResponseStatusException) error).getStatusCode().value());
    }

    private static NoticeDTO id(long seq) {
        return NoticeDTO.builder().noticeSeq(seq).build();
    }

    private static Notice notice(long views) {
        return Notice.builder()
                .noticeSeq(7L).title("원래 제목").contents("원래 내용").noticeYn("N")
                .userId("AUTHOR").readCnt(views).regId("CREATOR")
                .regDt(LocalDateTime.of(2025, 1, 2, 3, 4)).build();
    }
}
