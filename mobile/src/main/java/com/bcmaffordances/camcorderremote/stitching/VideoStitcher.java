package com.bcmaffordances.camcorderremote.stitching;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;

/**
 * This class is responsible for stitching video files together.
 * It delegates to a stitching service to do the work on a
 * background thread.
 */
public class VideoStitcher {

    private final String TAG = "VideoStitcher";
    private Context mCtx;
    private File mBaseFile;

    /**
     * Create a VideoStitcher
     * @param ctx The application context
     * @param baseFile The file which all other files will be appended to.
     */
    public VideoStitcher(Context ctx, File baseFile) {
        if (null == ctx) throw new IllegalArgumentException();
        if (null == baseFile) throw new IllegalArgumentException();
        this.mCtx = ctx;
        this.mBaseFile = baseFile;
    }

    /**
     * Append a video file.
     * @param fileToAppend A file referencing a video file
     */
    public void appendFile(File fileToAppend) {
        if (null == fileToAppend) throw new IllegalArgumentException();
        if (fileToAppend.getAbsolutePath() != mBaseFile.getAbsolutePath()) {
            Log.d(TAG, "Invoking service to append " + fileToAppend.getName() + " to " + mBaseFile.getName());
            Intent i = new Intent(mCtx, VideoStitchingService.class);
            i.putExtra(VideoStitchingService.INTENT_EXTRA_BASE_FILE, mBaseFile);
            i.putExtra(VideoStitchingService.INTENT_EXTRA_FILE_TO_APPEND, fileToAppend);
            mCtx.startService(i);
        }
    }
}
