package com.audiobookshelf.app.managers

import android.content.Context
import android.net.wifi.WifiManager
import android.os.PowerManager
import com.audiobookshelf.app.player.PLAYER_CAST
import com.audiobookshelf.app.player.PlayerNotificationService

class WakeManager constructor(private val playerNotificationService: PlayerNotificationService) {
  private lateinit var wifiLock: WifiManager.WifiLock
  private lateinit var wakeLock: PowerManager.WakeLock

  init {
    val ctx = this.playerNotificationService.getContext()
    val wifiManager: WifiManager = ctx.getSystemService(Context.WIFI_SERVICE) as WifiManager
    this.wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_LOW_LATENCY, "abs:player-net")
    this.wifiLock.setReferenceCounted(false)

    val powerManager: PowerManager = ctx.getSystemService(Context.POWER_SERVICE) as PowerManager
    this.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "abs:player-cpu")
    this.wakeLock.setReferenceCounted(false)
  }

  fun isPlayingChanged(isPlaying: Boolean) {
    if (isPlaying) {
      this.wakeLock.acquire()
      if (this.playerNotificationService.getMediaPlayer() == PLAYER_CAST) {
        this.wifiLock.acquire()
      }
    } else {
      this.wakeLock.release()
      this.wifiLock.release()
    }
  }
}
