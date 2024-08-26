package com.healthcare.mansumugang;

import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;

/**
 * AudioConverter 클래스는 오디오 파일 형식 변환 기능을 제공합니다.
 * 이 클래스는 FFmpeg 라이브러리를 사용하여 3GP 형식의 오디오 파일을 MP3 형식으로 변환합니다.
 */
public class AudioConverter {

    /**
     * ConversionCallback 인터페이스는 오디오 파일 변환 작업의 결과를 콜백 방식으로 전달합니다.
     */
    public interface ConversionCallback {
        /**
         * 변환이 성공적으로 완료되었을 때 호출됩니다.
         *
         * @param convertedFile 변환된 MP3 파일
         */
        void onSuccess(File convertedFile);

        /**
         * 변환이 실패했을 때 호출됩니다.
         *
         * @param error 변환 실패 원인을 설명하는 예외 객체
         */
        void onFailure(Exception error);
    }

    /**
     * 3GP 형식의 오디오 파일을 MP3 형식으로 변환합니다.
     *
     * @param inputPath  입력 3GP 파일의 경로
     * @param outputPath 변환된 MP3 파일의 저장 경로
     * @param callback   변환 작업의 결과를 처리할 콜백 인터페이스
     */
    public void convert3gpToMp3(String inputPath, String outputPath, ConversionCallback callback) {
        // FFmpeg 명령어를 정의합니다. -i는 입력 파일을 지정하며, -vn은 비디오 스트림을 제외하고, -acodec libmp3lame은 MP3 형식을 지정합니다.
        String command = String.format("-i \"%s\" -vn -acodec libmp3lame \"%s\"", inputPath, outputPath);

        // FFmpeg 명령어를 비동기적으로 실행합니다.
        FFmpeg.executeAsync(command, (executionId, returnCode) -> {
            if (returnCode == 0) {
                // 변환이 성공한 경우
                File converted = new File(outputPath);
                if (converted.exists()) {
                    // 변환된 파일이 존재하면 콜백의 onSuccess 메서드를 호출합니다.
                    callback.onSuccess(converted);
                } else {
                    // 변환된 파일이 존재하지 않으면 실패 콜백을 호출합니다.
                    callback.onFailure(new Exception("File conversion failed: File not found after conversion."));
                }
            } else {
                // 변환 실패 시, 실패 콜백을 호출합니다. returnCode는 FFmpeg 명령어의 반환 코드입니다.
                callback.onFailure(new Exception("Conversion failed with return code: " + returnCode));
            }
        });
    }
}
