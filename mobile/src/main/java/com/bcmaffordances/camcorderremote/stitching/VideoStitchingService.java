package com.bcmaffordances.camcorderremote.stitching;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;

/**
 * The VideoStitchingService is responsible for combining
 * two video files using a background thread. The
 * service queues up calls and executes them sequentially
 * in the order they were received.
 */
public class VideoStitchingService extends IntentService {

    public static final String INTENT_EXTRA_BASE_FILE = "com.bcmaffordances.camcorderremote.stitching.baseFile";
    public static final String INTENT_EXTRA_FILE_TO_APPEND = "com.bcmaffordances.camcorderremote.stitching.fileToAppend";
    public static final String INTENT_EXTRA_OUTPUT_FILE = "com.bcmaffordances.camcorderremote.stitching.outputFile";

    private final String TAG = "VideoStitchingService";


    public VideoStitchingService() {
        super("VideoStitchingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "onHandleIntent() invoked");

        // Input validation
        File baseFile = (File) intent.getExtras().get(INTENT_EXTRA_BASE_FILE);
        File fileToAppend = (File) intent.getExtras().get(INTENT_EXTRA_FILE_TO_APPEND);
        File outputFile = (File) intent.getExtras().get(INTENT_EXTRA_OUTPUT_FILE);
        if (!isIntentValid(baseFile, fileToAppend, outputFile)) {
            return;
        }

        Log.d(TAG, "Appending file " + baseFile.getAbsolutePath() + " to " + fileToAppend.getName());
        try {
            Movie[] inputMovies = new Movie[]{
                    MovieCreator.build(baseFile.getAbsolutePath()),
                    MovieCreator.build(fileToAppend.getAbsolutePath())};

            List<Track> videoTracks = new LinkedList<Track>();
            List<Track> audioTracks = new LinkedList<Track>();
            for (Movie m : inputMovies) {
                for (Track t : m.getTracks()) {
                    if (t.getHandler().equals("soun")) {
                        audioTracks.add(t);
                    }
                    if (t.getHandler().equals("vide")) {
                        videoTracks.add(t);
                    }
                }
            }

            Movie result = new Movie();
            int numAudioTracks = audioTracks.size();
            if (numAudioTracks > 0) {
                result.addTrack(new AppendTrack(audioTracks.toArray(new Track[numAudioTracks])));
            }
            int numVideoTracks = videoTracks.size();
            if (numVideoTracks > 0) {
                result.addTrack(new AppendTrack(videoTracks.toArray(new Track[numVideoTracks])));
            }

            Container out = new DefaultMp4Builder().build(result);
            Log.d(TAG, "Writing combined video files to: " + outputFile.getAbsolutePath());
            FileChannel fc = new RandomAccessFile(outputFile.getAbsolutePath(), "rw").getChannel();
            out.writeContainer(fc);
            fc.close();

            // Delete input files after stitching has completed
            boolean baseFileDeleted = baseFile.delete();
            boolean fileToAppendDeleted = fileToAppend.delete();
            if (!baseFileDeleted) {
                Log.w(TAG, "Failed to delete baseFile: " + baseFile.getAbsolutePath());
            }
            if (!fileToAppendDeleted) {
                Log.w(TAG, "Failed to delete fileToAppend: " + fileToAppend.getAbsolutePath());
            }

        } catch(IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * Perform simple input validation
     * @param baseFile The base video file
     * @param fileToAppend The video file to append to the base file
     * @param outputFile The output video file
     * @return Returns true if inputs are valid
     */
    private boolean isIntentValid(File baseFile, File fileToAppend, File outputFile) {
        if (null == baseFile || null == fileToAppend || null == outputFile) {
            Log.e(TAG, "Input parameter(s) is null");
            return false;
        }
        if (baseFile.getAbsolutePath() == fileToAppend.getAbsolutePath()) {
            Log.w(TAG, "Base file is the same as the file-to-append");
            return false;
        }
        return true;
    }
}
