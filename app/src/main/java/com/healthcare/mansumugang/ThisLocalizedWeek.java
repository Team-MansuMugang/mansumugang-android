package com.healthcare.mansumugang;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * ThisLocalizedWeek 클래스는 특정 로케일의 주의 첫날과 마지막 날을 계산합니다.
 */
public class ThisLocalizedWeek {

    // 항상 사용할 시간대를 서울로 지정합니다.
    private final static TimeZone TZ = TimeZone.getTimeZone("Asia/Seoul");

    private final Locale locale;             // 로케일 정보
    private final int firstDayOfWeek;        // 주의 첫날 (Calendar.DAY_OF_WEEK 값)
    private final int lastDayOfWeek;         // 주의 마지막 날 (Calendar.DAY_OF_WEEK 값)

    /**
     * ThisLocalizedWeek 생성자
     *
     * @param locale 로케일 정보
     */
    public ThisLocalizedWeek(final Locale locale) {
        this.locale = locale;
        this.firstDayOfWeek = Calendar.getInstance(locale).getFirstDayOfWeek();
        this.lastDayOfWeek = (this.firstDayOfWeek + 5) % 7 + 1;
    }

    /**
     * 주의 첫날을 반환합니다.
     *
     * @return 주의 첫날
     */
    public Calendar getFirstDay() {
        Calendar cal = new GregorianCalendar(TZ, locale);
        cal.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
        return cal;
    }

    /**
     * 주의 마지막 날을 반환합니다.
     *
     * @return 주의 마지막 날
     */
    public Calendar getLastDay() {
        Calendar cal = new GregorianCalendar(TZ, locale);
        cal.set(Calendar.DAY_OF_WEEK, lastDayOfWeek);
        return cal;
    }

    /**
     * 객체의 문자열 표현을 반환합니다.
     *
     * @return 로케일에 따른 주의 첫날과 마지막 날을 포함하는 문자열
     */
    @Override
    public String toString() {
        return String.format("The %s week starts on %s and ends on %s",
                this.locale.getDisplayName(),
                getFirstDay().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale),
                getLastDay().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale));
    }
}
