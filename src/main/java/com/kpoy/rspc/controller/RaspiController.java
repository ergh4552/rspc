package com.kpoy.rspc.controller;

import com.kpoy.rspc.camera.CaptureStillImage;
import com.kpoy.rspc.model.GpioAccess;
import com.kpoy.rspc.service.ImageService;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
public class RaspiController {
    private final ImageService imageService;
    private GpioAccess gpioAccess;

    public RaspiController(ImageService imageService,
                           GpioAccess gpioAccess) {
        this.imageService = imageService;
        this.gpioAccess = gpioAccess;
    }

    @RequestMapping("/image")
    public ResponseEntity<byte[]> getImage() throws IOException {

        try {
            byte[] image = imageService.takePicture();

            return ResponseEntity.ok()
                    .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(image);
        } catch (Exception ex) {
            String errorMessage = "something went wrong: " + ex.getMessage();
            byte[] errorImage = errorMessage.getBytes();
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                    .contentType(MediaType.TEXT_HTML)
                    .body(errorImage);
        }
    }

    @RequestMapping("/stored")
    public ResponseEntity<byte[]> getStoredImage() throws IOException {
        try {
            byte[] image = imageService.getLatestImage();

            return ResponseEntity.ok()
                    .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(image);
        } catch (Exception ex) {
            String errorMessage = "something went wrong: " + ex.getMessage();
            byte[] errorImage = errorMessage.getBytes();
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                    .contentType(MediaType.TEXT_HTML)
                    .body(errorImage);
        }

    }
}
