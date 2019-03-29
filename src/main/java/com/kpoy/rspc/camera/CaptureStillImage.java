package com.kpoy.rspc.camera;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;

// fswebcam --fps 15 -S 8 --device /dev/video1 -r 1280x720    /tmp/image.jpg

@Slf4j
@Component
public class CaptureStillImage implements Camera {
    private static final String FSWEBCAM_COMMAND = "fswebcam";
    private static final String FSWEBCAM_DEVICE = "--device";
    private static final String FSWEBCAM_FRAMESPERSEC="--fps";
    private static final String FSWEBCAM_SKIPFRAMES="-S";
    private static final String FSWEBCAM_RESOLUTION="-r";
    private static final String FSWEBCAM_STDOUT = "-";

    private static final String DEFAULT_DEVICE = "/dev/video1";

    private static final String DEFAULT_FILEPATH = "/tmp/image.jpg";

    @Override
    public byte[] takePicture() {
        // TODO: make used device configurable, now using Microsoft cam as the second USB cam...
        // TODO: make the frames per sec, skip frames and resolution configurable
        ProcessBuilder processBuilder = new ProcessBuilder(
                FSWEBCAM_COMMAND,
                FSWEBCAM_FRAMESPERSEC, "15",
                FSWEBCAM_SKIPFRAMES, "2",
                FSWEBCAM_RESOLUTION, "1280x720",
                FSWEBCAM_DEVICE, "/dev/video1",
                FSWEBCAM_STDOUT);

        byte[] image;
        try {

            Process process = processBuilder.start();
            log.debug("Started Process, wait for ending");
            ByteStreamReader bsr = new ByteStreamReader(process.getInputStream());
            bsr.start();
            int exitValue = process.waitFor();
            if (exitValue != 0) {
                log.error(getFsWebcamErrors(process.getErrorStream()));
                throw new CameraException("Error Occurred");
            }
            bsr.join();
            image = bsr.getBytes();
            process.destroy();
        } catch (IOException ex) {
            log.error("IOException: " + ex.getMessage(), ex);
            throw new CameraException(ex.getMessage(), ex);
        } catch (InterruptedException ex) {
            log.error("InterruptedException: " + ex.getMessage(), ex);
            throw new CameraException(ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("Exception: " + ex.getMessage(), ex);
            throw new CameraException(ex.getMessage(), ex);
        }
        return image;
    }

    private static String getFsWebcamErrors(InputStream errStream) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(errStream));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + System.getProperty("line.separator"));
            }
        } catch (IOException ex) {
            log.error(ex.getMessage());
        } finally {
            try {
                br.close();
            } catch (Throwable ignore) {
            }
        }
        return sb.toString();
    }

    /**
     * @return the image from
     */
    public byte[] getStoredPicture() {
        Resource res;
        res = new FileSystemResource(DEFAULT_FILEPATH);
        try {
            InputStream is = res.getInputStream();
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len = 0;
            while ((len = is.read(bytes)) > 0) {
                bos.write(bytes, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException ex) {
            log.error("Error reading last image {}", ex);
            captureImage(); // try to capture the image
            return ex.getMessage().getBytes();
        }

    }

    public void captureImage() {

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    FSWEBCAM_COMMAND,
                    FSWEBCAM_FRAMESPERSEC, "15",
                    FSWEBCAM_SKIPFRAMES, "2",
                    FSWEBCAM_RESOLUTION, "1280x720",
                    FSWEBCAM_DEVICE, DEFAULT_DEVICE,
                    DEFAULT_FILEPATH);
            processBuilder.start();

        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    class ByteStreamReader extends Thread {
        private InputStream inputStream;
        private byte[] bytes;

        ByteStreamReader(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public void run() {
            try {
                byte[] bytes = new byte[1024];
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int len = 0;

                log.debug("Start reading...");
                while ((len = inputStream.read(bytes)) > 0) {
                    byteArrayOutputStream.write(bytes, 0, len);
                }
                log.debug("Reading complete");
                this.bytes = byteArrayOutputStream.toByteArray();

            } catch (IOException ex) {
                log.error("Failure to read {}", ex);
            }
        }

        public byte[] getBytes() {
            return this.bytes;
        }
    }
}
