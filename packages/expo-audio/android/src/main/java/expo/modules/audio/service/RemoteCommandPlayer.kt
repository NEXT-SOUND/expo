package expo.modules.audio.service

import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import expo.modules.audio.AudioLockScreenOptions
import expo.modules.audio.AudioPlayer

// The player the media session (notification, lock screen, headphones, Bluetooth, Android Auto)
// controls. App calls go to the ExoPlayer directly, so only remote commands pass through here.
// Track changes always reach JavaScript; with `forwardRemoteCommands`, play, pause and seeks do too
// instead of acting on the player.
@UnstableApi
internal class RemoteCommandPlayer(
  private val audioPlayer: AudioPlayer,
  private val options: AudioLockScreenOptions?
) : ForwardingPlayer(audioPlayer.ref) {
  private val forwardsCommands = options?.forwardRemoteCommands == true
  private val isLive = options?.isLiveStream ?: audioPlayer.isLive

  override fun getAvailableCommands(): Player.Commands {
    val commands = super.getAvailableCommands().buildUpon()
    if (isLive) {
      commands.remove(Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM)
    }
    if (options?.showNextTrack == true) {
      commands.addAll(Player.COMMAND_SEEK_TO_NEXT, Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
    }
    if (options?.showPreviousTrack == true) {
      commands.addAll(Player.COMMAND_SEEK_TO_PREVIOUS, Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
    }
    return commands.build()
  }

  override fun isCommandAvailable(command: Int): Boolean = availableCommands.contains(command)

  override fun play() {
    if (forwardsCommands) audioPlayer.emitRemoteCommand("play") else super.play()
  }

  override fun pause() {
    if (forwardsCommands) audioPlayer.emitRemoteCommand("pause") else super.pause()
  }

  override fun setPlayWhenReady(playWhenReady: Boolean) {
    if (forwardsCommands) {
      audioPlayer.emitRemoteCommand(if (playWhenReady) "play" else "pause")
    } else {
      super.setPlayWhenReady(playWhenReady)
    }
  }

  override fun stop() {
    if (forwardsCommands) audioPlayer.emitRemoteCommand("pause") else super.stop()
  }

  // A play press on an ended item rewinds it first; the app decides how an ended track replays.
  override fun seekToDefaultPosition() {
    if (!forwardsCommands) super.seekToDefaultPosition()
  }

  override fun seekToDefaultPosition(mediaItemIndex: Int) {
    if (!forwardsCommands) super.seekToDefaultPosition(mediaItemIndex)
  }

  override fun seekTo(positionMs: Long) {
    if (forwardsCommands) emitSeek(positionMs) else super.seekTo(positionMs)
  }

  override fun seekTo(mediaItemIndex: Int, positionMs: Long) {
    if (forwardsCommands) emitSeek(positionMs) else super.seekTo(mediaItemIndex, positionMs)
  }

  override fun seekBack() {
    if (forwardsCommands) emitSeek(currentPosition - seekBackIncrement) else super.seekBack()
  }

  override fun seekForward() {
    if (forwardsCommands) emitSeek(currentPosition + seekForwardIncrement) else super.seekForward()
  }

  override fun seekToNext() {
    audioPlayer.emitRemoteCommand("nextTrack")
  }

  override fun seekToNextMediaItem() {
    audioPlayer.emitRemoteCommand("nextTrack")
  }

  override fun seekToPrevious() {
    audioPlayer.emitRemoteCommand("previousTrack")
  }

  override fun seekToPreviousMediaItem() {
    audioPlayer.emitRemoteCommand("previousTrack")
  }

  private fun emitSeek(positionMs: Long) {
    audioPlayer.emitRemoteCommand("seek", positionMs.coerceAtLeast(0) / 1000.0)
  }
}
