package com.kpoy.rspc.camera;

public interface Camera {
    byte [] takePicture();
    byte [] takePicture(int deviceId);
    byte [] getStoredPicture();
    String getCameraDetails();
}
