package com.kpoy.rspc.camera;

public interface Camera {
    byte [] takePicture();
    byte [] getStoredPicture();
}