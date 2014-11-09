package com.bcmaffordances.camcorderremote.video;

import android.util.Log;

import java.io.File;

/**
 * Class to encapsulate the creation and management of video files.
 */
public class VideoFile {

    private static final String TAG = "VideoFile";
    private File mFile;

    /**
     * Constructor
     * @throws VideoFileException if unable to create a new VideoFile.
     */
    public VideoFile() throws VideoFileException {
        mFile = VideoFileFactory.createFile();
    }

    /**
     * Get the file.
     * @return File object
     */
    public File getFile() {
        return mFile;
    }

    /**
     * Delete the file.
     * @throws VideoFileException if unable to delete the file.
     */
    public void deleteFile() throws VideoFileException {
        if (mFile != null && mFile.exists()) {
            if (mFile.delete()) {
                Log.d(TAG, "Deleted " + mFile.getAbsolutePath());
            } else {
                Log.d(TAG, "Failed to delete: " + mFile.getAbsolutePath());
                throw new VideoFileException("Failed to delete file: " + mFile.getAbsolutePath());
            }
        }
    }
}
