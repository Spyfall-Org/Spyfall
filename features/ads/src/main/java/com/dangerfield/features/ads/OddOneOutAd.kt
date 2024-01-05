package com.dangerfield.features.ads

import com.dangerfield.spyfall.features.ads.R

sealed class OddOneOutAd(val resId: Int) {
    data object SingleDeviceVoting : OddOneOutAd(R.string.single_device_voting_id)
    data object SingleDeviceResults : OddOneOutAd(R.string.single_device_results_id)
    data object RoleRevealBanner : OddOneOutAd(R.string.waiting_room_banner_id)
    data object WaitingRoomBanner : OddOneOutAd(R.string.waiting_room_banner_id)
    data object MultiPlayerGamePlayBanner : OddOneOutAd(R.string.multi_player_game_play_banner_id)
    data object SingleDeviceGamePlayBanner : OddOneOutAd(R.string.single_device_game_play_banner_id)
    data object GameRestartInterstitial : OddOneOutAd(R.string.game_restart_interstitial_id)
}