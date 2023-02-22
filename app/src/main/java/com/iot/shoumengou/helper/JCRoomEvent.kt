package com.iot.shoumengou.helper

import com.juphoon.cloud.JCMediaChannelParticipant
//import com.juphoon.cloud.room.data.ResponseBean

class JCRoomEvent(val event: Any?) {

    /** 底层回调的事件 */
    data class Login(val result: Boolean, val reason: Int)
    data class Logout(val reason: Int)
    data class ClientStateChange(val state: Int, val oldstate: Int)
    data class Join(val result: Boolean, val reason: Int, val channelId: String?)
    data class Leave(val reason: Int, val channelId: String?)
    data class ParticipantJoin(val participant: JCMediaChannelParticipant)
    data class ParticipantLeft(val participant: JCMediaChannelParticipant)
    data class ParticipantUpdate(val participant: JCMediaChannelParticipant, val changeParam: JCMediaChannelParticipant.ChangeParam)
    data class MessageReceive(val type: String, val content: String, val fromUserId: String?)
    data class Stop(val result: Boolean, val reason: Int)
    data class NetChange(val newNetType: Int, val oldNetType: Int)
    data class ParticipantVolumeChange(val p0: JCMediaChannelParticipant?)
    data class Progress(val p0: Boolean)
    data class Refresh(val p0: Boolean)

}