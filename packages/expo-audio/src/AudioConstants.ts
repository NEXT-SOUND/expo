// @needsAudit
/**
 * Options for configuring which playback controls should be displayed on the lock screen.
 */
export type AudioLockScreenOptions = {
  /**
   * Whether the seek forward button should be displayed on the lock screen.
   */
  showSeekForward?: boolean;
  /**
   * Whether the seek backward button should be displayed on the lock screen.
   */
  showSeekBackward?: boolean;
  /**
   * Whether the audio is a live stream. When `true`, the lock screen will hide the duration
   * and scrub bar, and disable seek controls.
   */
  isLiveStream?: boolean;
  /**
   * Whether the next track button should be displayed on the lock screen and in the media
   * notification. Presses (and next-track commands from headphones, Bluetooth devices or the car)
   * emit a `remoteCommand` event with `nextTrack`; the app decides what plays next.
   */
  showNextTrack?: boolean;
  /**
   * Whether the previous track button should be displayed on the lock screen and in the media
   * notification. Presses emit a `remoteCommand` event with `previousTrack`.
   */
  showPreviousTrack?: boolean;
  /**
   * When `true`, play, pause, toggle and seek commands from the lock screen, the media notification,
   * headphones and other remote controls are not applied to the player. Each one emits a
   * `remoteCommand` event instead, so the app applies it through its own playback logic.
   */
  forwardRemoteCommands?: boolean;
};
