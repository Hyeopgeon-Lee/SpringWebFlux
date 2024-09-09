package kopo.poly.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class DateUtil {

    /**
     * 날짜, 시간 출력하기
     *
     * @param fm 날짜 출력 형식
     * @return date
     */
    public static String getDateTime(String fm) {

        Date today = new Date();
        SimpleDateFormat date = new SimpleDateFormat(fm);

        return date.format(today);
    }

    /**
     * Unix UTC 타입의 날짜, 시간 출력하기
     *
     * @param time 시간
     * @param fm   날짜 출력 형식
     * @return date
     */
    public static String getLongDateTime(Object time, String fm) {
        return getLongDateTime((Integer) time, fm);

    }

    /**
     * Unix UTC 타입의 날짜, 시간 출력하기
     *
     * @param time 시간
     * @param fm   날짜 출력 형식
     * @return date
     */
    public static String getLongDateTime(Integer time, String fm) {
        Instant instant = Instant.ofEpochSecond(time);
        return DateTimeFormatter.ofPattern(fm)
                .withZone(ZoneId.systemDefault())
                .format(instant);

    }

    /**
     * String을 LocalDateTime으로 변환하는 공통 함수
     *
     * @param dateTimeString 변환할 문자열 (예: "2024-09-06 15:45:30")
     * @return 변환된 LocalDateTime
     * @throws DateTimeParseException 유효하지 않은 포맷의 문자열이 들어올 경우 예외 발생
     */
    public static LocalDateTime stringToLocalDateTime(String dateTimeString, String format) {
        try {
            DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(format);

            return LocalDateTime.parse(dateTimeString, FORMATTER);
        } catch (DateTimeParseException e) {
            // 변환에 실패한 경우 예외 처리
            throw new IllegalArgumentException("Invalid date format, expected 'yyyy-MM-dd HH:mm:ss'", e);
        }
    }


    public static LocalDateTime stringToLocalDateTime(String s) {
        return stringToLocalDateTime(s, "yyyy-MM-dd HH:mm:ss");
    }
}
