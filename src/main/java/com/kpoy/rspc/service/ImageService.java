package com.kpoy.rspc.service;

public interface ImageService {

    byte[] getLatestImage() throws Exception;

    byte[] takePicture();

    byte[] takePicture(int deviceId);

    String getCameraDetails();
}
