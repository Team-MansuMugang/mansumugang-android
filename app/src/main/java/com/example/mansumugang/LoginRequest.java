package com.example.mansumugang;

/**
 * LoginRequest 클래스는 로그인 요청 시 필요한 데이터 모델을 정의합니다.
 */
public class LoginRequest {
    private String username;
    private String password;

    /**
     * LoginRequest 생성자
     *
     * @param username 사용자의 아이디
     * @param password 사용자의 비밀번호
     */
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter 및 Setter 메서드

    /**
     * 사용자 아이디를 반환합니다.
     *
     * @return 사용자 아이디
     */
    public String getUsername() {
        return username;
    }

    /**
     * 사용자 아이디를 설정합니다.
     *
     * @param username 사용자 아이디
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 사용자 비밀번호를 반환합니다.
     *
     * @return 사용자 비밀번호
     */
    public String getPassword() {
        return password;
    }

    /**
     * 사용자 비밀번호를 설정합니다.
     *
     * @param password 사용자 비밀번호
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
