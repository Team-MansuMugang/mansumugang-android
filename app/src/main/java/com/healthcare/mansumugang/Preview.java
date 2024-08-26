package com.healthcare.mansumugang;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.*;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Preview 클래스는 카메라의 실시간 미리보기와 사진 촬영을 관리합니다.
 * 이 클래스는 카메라 프리뷰를 설정하고, 사진을 촬영하며, 촬영된 사진을 저장하고 서버에 업로드하는 기능을 포함합니다.
 */
public class Preview extends Thread {

    private Size mPreviewSize; // 프리뷰의 크기
    private Context mContext; // 애플리케이션 컨텍스트
    private CameraDevice mCameraDevice; // 카메라 디바이스
    private CaptureRequest.Builder mPreviewBuilder; // 카메라 프리뷰 설정 빌더
    private CameraCaptureSession mPreviewSession; // 카메라 캡처 세션
    private TextureView mTextureView; // 텍스처 뷰
    private String mCameraId = "0"; // 카메라 ID (기본값: 0)
    private Button mCameraCaptureButton; // 카메라 촬영 버튼
    private HandlerThread mBackgroundThread; // 백그라운드 작업을 위한 핸들러 스레드
    private Handler mBackgroundHandler; // 백그라운드 핸들러
    private File imageFile; // 저장할 이미지 파일

    // 카메라의 회전 방향에 따른 각도 매핑
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray(4);

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /**
     * Preview 클래스의 생성자
     *
     * @param context       애플리케이션 컨텍스트
     * @param textureView   카메라 프리뷰를 표시할 텍스처 뷰
     * @param captureButton 사진 촬영 버튼
     *                      생성자에서는 텍스처 뷰의 서피스 텍스처 리스너를 설정하고, 사진 촬영 버튼의 클릭 리스너를 설정합니다.
     */
    public Preview(Context context, TextureView textureView, Button captureButton) {
        mContext = context;
        mTextureView = textureView;
        mCameraCaptureButton = captureButton;
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);

        mCameraCaptureButton.setOnClickListener(v -> takePicture());
        startBackgroundThread();
    }

    /**
     * 백그라운드 스레드를 시작합니다.
     * 백그라운드 스레드에서 카메라 관련 작업을 처리합니다.
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * 백그라운드 스레드를 중지합니다.
     * 백그라운드 스레드를 안전하게 종료하고, 관련 핸들러를 null로 설정합니다.
     */
    private void stopBackgroundThread() {
        if (mBackgroundThread != null) {
            mBackgroundThread.quitSafely();
            try {
                mBackgroundThread.join();
                mBackgroundThread = null;
                mBackgroundHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 후면 카메라의 ID를 가져옵니다.
     *
     * @param cManager 카메라 관리자
     * @return 후면 카메라의 ID 또는 null (후면 카메라가 없을 경우)
     * 카메라 관리자에서 후면 카메라를 검색하고, 후면 카메라의 ID를 반환합니다.
     */
    private String getBackFacingCameraId(CameraManager cManager) {
        try {
            for (String cameraId : cManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cManager.getCameraCharacteristics(cameraId);
                int lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    return cameraId;
                }
            }
        } catch (CameraAccessException e) {
            Log.e(Constants.PREVIEW, "Error accessing camera", e);
        }
        return null;
    }

    /**
     * 카메라를 열고 프리뷰를 시작합니다.
     * 카메라 ID를 설정하고 카메라 권한을 확인한 후 카메라를 열어 프리뷰를 시작합니다.
     */
    public void openCamera() {
        CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = getBackFacingCameraId(manager);
            if (mCameraId == null) {
                Log.e(Constants.PREVIEW, "No back-facing camera found");
                return;
            }

            CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[0];

            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CAMERA}, Constants.REQUEST_CAMERA);
            } else {
                manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
            }
        } catch (CameraAccessException e) {
            Log.e(Constants.PREVIEW, "Error opening camera", e);
        }
    }

    /**
     * 텍스처 뷰의 서피스 텍스처 리스너
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // 서피스 텍스처 크기 변경 시 처리 (필요할 경우)
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return true; // 서피스 텍스처가 파괴될 때 true를 반환
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            // 서피스 텍스처 업데이트 시 처리 (필요할 경우)
        }
    };

    /**
     * 카메라 디바이스 상태 콜백
     */
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            startPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            Log.e(Constants.PREVIEW, "Camera disconnected");
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            Log.e(Constants.PREVIEW, "Camera error: " + error);
        }
    };

    /**
     * 카메라 프리뷰를 시작합니다.
     * 카메라 디바이스와 텍스처 뷰가 준비되었는지 확인한 후, 프리뷰를 설정하고 시작합니다.
     */
    private void startPreview() {
        if (mCameraDevice == null || !mTextureView.isAvailable() || mPreviewSize == null) {
            Log.e(Constants.PREVIEW, "startPreview fail, return");
            return;
        }

        SurfaceTexture texture = mTextureView.getSurfaceTexture();
        if (texture == null) {
            Log.e(Constants.PREVIEW, "SurfaceTexture is null");
            return;
        }

        texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface surface = new Surface(texture);

        try {
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewBuilder.addTarget(surface);

            mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    mPreviewSession = session;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    Toast.makeText(mContext, "Failed to configure camera", Toast.LENGTH_LONG).show();
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(Constants.PREVIEW, "Error creating capture session", e);
        }
    }

    /**
     * 프리뷰를 업데이트합니다.
     * 카메라 프리뷰의 설정을 업데이트하여 실시간으로 프리뷰를 계속 보여줍니다.
     */
    private void updatePreview() {
        if (mCameraDevice == null) {
            Log.e(Constants.PREVIEW, "updatePreview error: CameraDevice is null");
            return;
        }

        mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

        HandlerThread thread = new HandlerThread("CameraPreview");
        thread.start();
        Handler backgroundHandler = new Handler(thread.getLooper());

        try {
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(Constants.PREVIEW, "Error updating preview", e);
        }
    }

    private final Runnable mDelayPreviewRunnable = this::startPreview;

    /**
     * 사진을 촬영합니다.
     * 카메라 디바이스가 준비된 상태에서 사진을 촬영하고, 촬영된 사진을 저장하고 서버에 업로드합니다.
     */
    private void takePicture() {
        if (mCameraDevice == null) {
            Log.e(Constants.PREVIEW, "CameraDevice is null");
            return;
        }

        try {
            CameraManager cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(mCameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            Size[] jpegSizes = null;
            if (map != null) {
                jpegSizes = map.getOutputSizes(ImageFormat.JPEG);
            }

            int width = 640;
            int height = 480;
            if (jpegSizes != null && jpegSizes.length > 0) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }

            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(mTextureView.getSurfaceTexture()));

            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());

            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(mContext));

            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);

            final HandlerThread captureThread = new HandlerThread("CameraCapture");
            captureThread.start();
            Handler captureHandler = new Handler(captureThread.getLooper());

            mCameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), new CameraCaptureSession.CaptureCallback() {
                            @Override
                            public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                                Toast.makeText(mContext, "Picture taken!", Toast.LENGTH_SHORT).show();
                                startPreview();

                                if (imageFile != null && imageFile.exists()) {
                                    ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
                                    String token = App.prefs.getToken();
                                    RequestBody requestFile = RequestBody.create(imageFile, MediaType.parse("image/jpeg"));
                                    MultipartBody.Part body = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);

                                    Call<Void> call = apiService.saveImage("Bearer " + token, body);
                                    call.enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            // 성공 응답 처리
                                            if (response.isSuccessful()) {
                                                System.out.println("성공");
                                                imageFile.delete();
                                            } else if (response.code() == 401) {
                                                Log.d(Constants.LOCATION_HELPER_TAG, "Token may be expired. Refreshing token.");
                                            } else {
                                                String errorMessage = "API 호출 실패";
                                                if (response.errorBody() != null) {
                                                    try {
                                                        String errorBody = response.errorBody().string();
                                                        JsonParser parser = new JsonParser();
                                                        JsonObject jsonObject = parser.parse(errorBody).getAsJsonObject();
                                                        errorMessage = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "서버 오류";
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                Toast.makeText(mContext, "오류 발생: " + errorMessage, Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            // 실패 응답 처리
                                        }
                                    });
                                } else {
                                    Log.e(Constants.PREVIEW, "Image file is null or does not exist");
                                }
                            }
                        }, captureHandler);
                    } catch (CameraAccessException e) {
                        Log.e(Constants.PREVIEW, "Error capturing picture", e);
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    Toast.makeText(mContext, "Failed to configure camera capture session", Toast.LENGTH_LONG).show();
                }
            }, captureHandler);
        } catch (CameraAccessException e) {
            Log.e(Constants.PREVIEW, "Error setting up image reader", e);
        }
    }

    /**
     * 이미지가 사용 가능할 때 호출됩니다.
     * 이미지를 읽어 파일로 저장합니다.
     */
    private final ImageReader.OnImageAvailableListener readerListener = reader -> {
        Image image = null;
        try {
            image = reader.acquireLatestImage();
            if (image == null) return;

            Image.Plane[] planes = image.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);

            save(bytes);
        } catch (Exception e) {
            Log.e(Constants.PREVIEW, "Error saving image", e);
        } finally {
            if (image != null) {
                image.close();
            }
        }
    };

    /**
     * 바이트 배열로부터 이미지를 저장합니다.
     *
     * @param bytes 이미지 데이터 바이트 배열
     *              바이트 배열을 사용하여 외부 저장소에 이미지를 저장합니다.
     */
    private void save(byte[] bytes) {
        imageFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg");
        try (OutputStream output = new FileOutputStream(imageFile)) {
            output.write(bytes);
        } catch (IOException e) {
            Log.e(Constants.PREVIEW, "Error saving picture", e);
        }
    }

    /**
     * 현재 화면의 회전 방향을 가져옵니다.
     *
     * @param context 애플리케이션 컨텍스트
     * @return 회전 방향에 따른 이미지 회전 각도
     * 현재 화면의 회전 방향에 따라 카메라 촬영 이미지의 회전 각도를 계산합니다.
     */
    private int getOrientation(Context context) {
        int rotation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
        return ORIENTATIONS.get(rotation);
    }
}
