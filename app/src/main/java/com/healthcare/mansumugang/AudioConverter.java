package com.healthcare.mansumugang;

import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;

public class AudioConverter {

    // 인터페이스를 정의하여 콜백 방식으로 변환 결과를 전달
    public interface ConversionCallback {
        void onSuccess(File convertedFile);

        void onFailure(Exception error);
    }

    public void convert3gpToMp3(String inputPath, String outputPath, ConversionCallback callback) {
        // FFmpeg 명령어 정의
        String command = String.format("-i \"%s\" -vn -acodec libmp3lame \"%s\"", inputPath, outputPath);

        // FFmpeg 비동기 실행
        FFmpeg.executeAsync(command, (executionId, returnCode) -> {
            if (returnCode == 0) {
                // 변환 성공
                File converted = new File(outputPath);
                if (converted.exists()) {
                    callback.onSuccess(converted);
                } else {
                    callback.onFailure(new Exception("File conversion failed: File not found after conversion."));
                }
            } else {
                // 변환 실패
                callback.onFailure(new Exception("Conversion failed with return code: " + returnCode));
            }
        });
    }
}
