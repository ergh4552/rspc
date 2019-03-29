package com.kpoy.rspc.service;

import com.kpoy.rspc.camera.Camera;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ImageServiceImpl implements ImageService {

    private final Camera camera;

    public ImageServiceImpl(Camera camera) {
        this.camera = camera;
    }
    @Override
    public byte[] getLatestImage() throws Exception {
        return camera.getStoredPicture();
    }

    @Override
    public byte[] takePicture() {
        log.debug("take picture using camera");
        return camera.takePicture();
    }

    @Override
    public String getCameraDetails() {
        return camera.getCameraDetails();
    }

    @Override
    public byte[] takePicture(int deviceId) {
        log.debug("take picture using camera");
        return camera.takePicture(deviceId);
    }
}
