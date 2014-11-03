package com.bcmaffordances.wearableconnector;

/**
 * Constants common to the camcorderRemote handheld & wearable apps.
 */
public class CamcorderRemoteConstants {

    // TODO Move this out of this project into its own.

    /** Specify endpoint at receiving node */
    public static final String MESSAGE_PATH = "/camcorderRemote";

    /** Intent extra that holds a message */
    public static final String MESSAGE_INTENT_EXTRA = "message";

    /**
     * Request messages from wearable to handheld.
     */

    /** Request to start a new recording **/
    public static final String REQUEST_RECORD = "record-request";
    /** Request to resume a recording **/
    public static final String REQUEST_RESUME = "request-resume";
    /** Request to pause a recording **/
    public static final String REQUEST_PAUSE = "request-pause";
    /** Request to stop a recording **/
    public static final String REQUEST_STOP = "request-stop";

    /**
     * Response messages from handheld to wearable.
     */

    /** Response that a new recording was started **/
    public static final String RESPONSE_RECORDING = "response-recording";
    /** Response that a recording was resumed **/
    public static final String RESPONSE_RESUMED = "response-resumed";
    /** Resposne that a recording was paused **/
    public static final String RESPONSE_PAUSED = "response-paused";
    /** Response that a recording was stopped **/
    public static final String RESPONSE_STOPPED = "response-stopped";
}
