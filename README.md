android-wear-camcorder-remote
=============================

Android Wear video camera remote to play/pause/stop recordings.

This project allows users to control their video camera from their Android wearable device.
The mobile and wearable apps current support the following features:
  - Play, pause, resume, and stop functionality
  - Videos which are paused/resumed are automagically stitched into a single output
    mp4 file via a Stitching Service component
  - Non-adjustable recording settings
  
  
Architecturally this project consists of the following components:
  - Mobile app which is reponsible for displaying the camera preview and recording video
  - Wearable app which provides video recording controls
  - A common messaging component which is responsible for encapsulating the code which allows
    the mobile and wearable apps to communicate with one another
