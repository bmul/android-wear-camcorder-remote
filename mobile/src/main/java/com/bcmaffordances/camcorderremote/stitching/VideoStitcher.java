package com.bcmaffordances.camcorderremote.stitching;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bcmaffordances.camcorderremote.video.VideoFile;
import com.bcmaffordances.camcorderremote.video.VideoFileException;

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
    private VideoFile mOutputFile;

    /**
     * Create a VideoStitcher
     * @param ctx The application context
     */
    public VideoStitcher(Context ctx) {
        if (null == ctx) throw new IllegalArgumentException();
        this.mCtx = ctx;
        mBaseFile = null;
        mOutputFile = null;
    }

    /**
     * Append a video file.
     * @param fileToAppend A file referencing a video file
     * @throws VideoFileException if unable to create output VideoFile object
     */
    public void appendFile(File fileToAppend) throws VideoFileException {

        if (null == fileToAppend) throw new IllegalArgumentException();
        if (null == mBaseFile) {
            // If first pass, set fileToAppend as the base file
            mBaseFile = fileToAppend;
            return;
        }
        if (null != mOutputFile) {
            // If nth pass, set previous output file as the base file
            mBaseFile = mOutputFile.getFile();
        }

        if (fileToAppend.getAbsolutePath() != mBaseFile.getAbsolutePath()) {

            mOutputFile = new VideoFile();
            Log.d(TAG, "Invoking service to append " + fileToAppend.getName() + " to " + mBaseFile.getName());
            Log.d(TAG, "Service to write composite file to: " + mOutputFile.getFile().getAbsolutePath());

            Intent i = new Intent(mCtx, VideoStitchingService.class);
            i.putExtra(VideoStitchingService.INTENT_EXTRA_BASE_FILE, mBaseFile);
            i.putExtra(VideoStitchingService.INTENT_EXTRA_FILE_TO_APPEND, fileToAppend);
            i.putExtra(VideoStitchingService.INTENT_EXTRA_OUTPUT_FILE, mOutputFile.getFile());
            mCtx.startService(i);
        }
    }
}
