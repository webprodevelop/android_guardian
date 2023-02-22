package com.iot.shoumengou.helper

import com.juphoon.cloud.JCCallItem
import com.juphoon.cloud.JCMediaChannelParticipant

class JCDuoEvent(val event: Any?) {

    /** 底层回调的事件 */
    data class Login(val result: Boolean, val reason: Int)
    data class Logout(val reason: Int)
    data class NetChange(val newNetType: Int, val oldNetType: Int)
    data class ClientStateChange(val state: Int, val oldstate: Int)
    data class CallAdd(val callItem: JCCallItem?)
    data class CallRemove(val callItem: JCCallItem?, val reason: Int, val description: String?)
    data class CallUpdate(val callItem: JCCallItem?, val changeParam: JCCallItem.ChangeParam?)
    data class CallMissed(val callItem: JCCallItem?)
    data class ParticipantVolumeChange(val p0: JCMediaChannelParticipant?)
    data class Progress(val p0: Boolean)
    data class Refresh(val p0: Boolean)
    data class QueryServerUidResult(val operationId: Int, val result: Boolean, val resultMap: MutableMap<String, String>?)
}