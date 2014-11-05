package com.bcmaffordances.camcorderremote.video;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class to encapsulate the creation and management of video files.
 */
public class VideoFile {

    private static final String TAG = "VideoFile";
    private File mVideoFile;

    /**
     * Create a VideoFile.
     */
    public VideoFile() {
        // TODO pull out factory class
        File dir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
                this.getClass().getPackage().getName());

        if (!dir.exists() && !dir.mkdirs()) {
            Log.wtf(TAG, "Failed to create storage directory: " + dir.getAbsolutePath());
            mVideoFile = null;
        } else {
            mVideoFile = new File(
                    dir.getAbsolutePath(),
                    new SimpleDateFormat("'BCM_'yyyy_MM_dd_HH_mm_ss'.mp4'").format(new Date()));
        }
    }

    /**
     * Get the underlying video File object.
     * @return File
     */
    public File getFile() {
        return mVideoFile;
    }

    /**
     * Delete the underlying video file.
     */
    public void deleteFile() {
        if (mVideoFile != null && mVideoFile.exists() && mVideoFile.delete()) {
            Log.d(TAG, "Deleted " + mVideoFile.getAbsolutePath());
        }
    }
}
