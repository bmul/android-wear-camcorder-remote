package com.bcmaffordances.camcorderremote.video;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * VideoFileFactory class.
 */
public class VideoFileFactory {

    private static final String TAG = "VideoFileFactory";
    private static final String SUBDIR = "com.bcmaffordances.camcorderremote.video";

    /**
     * Create a video file object.
     * Specifies an mp4 file within the external movies directory with a unique filename.
     * This does not actually create a file on the file system.
     * @return File object
     * @throws VideoFileException if unable to create file object.
     */
    public static File createFile() throws VideoFileException {

        File videoFile = null;

        File dir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
                SUBDIR);

        if (!dir.exists() && !dir.mkdirs()) {
            Log.e(TAG, "Failed to create video storage directory: " + dir.getAbsolutePath());
            throw new VideoFileException("Failed to create video file directory");
        }

        videoFile = new File(
                dir.getAbsolutePath(),
                new SimpleDateFormat("'BCM_'yyyy_MM_dd_HH_mm_ss'.mp4'").format(new Date()));

        if (null == videoFile) {
            Log.e(TAG, "Failed to create video file object");
            throw new VideoFileException("Failed to create video file object");
        }

        return videoFile;
    }
}
