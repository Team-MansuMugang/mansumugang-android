package com.healthcare.mansumugang;

import java.util.Date;

/**
 * 날짜 선택 리스너 인터페이스
 * 이 인터페이스는 날짜가 선택되었을 때 호출되는 메서드를 정의합니다.
 * 날짜를 선택한 후 이 리스너를 사용하여 선택된 날짜에 대한 작업을 처리할 수 있습니다.
 */
public interface OnDateSelectedListener {
    /**
     * 날짜 선택 이벤트가 발생했을 때 호출되는 메서드
     *
     * @param date 선택된 날짜를 나타내는 Date 객체
     */
    void onDateSelected(Date date);
}
