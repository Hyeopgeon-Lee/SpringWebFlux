package kopo.poly.util;

public class CmmUtil {

    /**
     * 문자열이 null 또는 비어있으면 대체 문자열 반환
     *
     * @param str    원본 문자열
     * @param chgStr 대체 문자열
     * @return 원본 문자열 또는 대체 문자열
     */
    public static String nvl(String str, String chgStr) {
        return (str == null || str.isEmpty()) ? chgStr : str;
    }

    /**
     * 문자열이 null 또는 비어있으면 빈 문자열("") 반환
     *
     * @param str 원본 문자열
     * @return 원본 문자열 또는 빈 문자열
     */
    public static String nvl(String str) {
        return nvl(str, "");
    }

    /**
     * 숫자가 null이면 기본값 반환
     *
     * @param number       원본 숫자
     * @param defaultValue 대체 숫자
     * @param <T>          Number의 서브타입
     * @return 원본 숫자 또는 기본값
     */
    public static <T extends Number> T nvl(T number, T defaultValue) {
        return (number == null) ? defaultValue : number;
    }
}
