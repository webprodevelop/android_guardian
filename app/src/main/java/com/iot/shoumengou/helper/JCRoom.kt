package com.iot.shoumengou.helper

import com.iot.shoumengou.App
import com.iot.shoumengou.Prefs
import com.juphoon.cloud.*
import com.juphoon.cloud.juphooncommon.CommonApplication
import com.juphoon.cloud.juphooncommon.data.DataOperation
import org.greenrobot.eventbus.EventBus

class JCRoom private constructor() : JCMediaChannelCallback, JCMediaDeviceCallback, JCClientCallback, JCNetCallback {

    val client: JCClient = JCClient.create(App.getAppContext(), "508db2a847ff9850fbee5097", this, null)
    //val client: JCClient = JCClient.create(App.getAppContext(), "44fa98003a41967834234097", this, null)
    val net: JCNet = JCNet.getInstance()
    val mediaDevice: JCMediaDevice = JCMediaDevice.create(client, this)
    val mediaChannel: JCMediaChannel = JCMediaChannel.create(client, mediaDevice, this)

    var userName: String = Prefs.Instance().getUserName()
    var userPhone: String = com.iot.shoumengou.Prefs.Instance().getUserPhone()
    var userPass: String = "123"

    companion object {
        private var instance: JCRoom? = null
            get() {
                if (field == null) {
                    field = JCRoom()
                }
                return field
            }

        @Synchronized
        fun get(): JCRoom {
            return instance!!
        }
    }

    init {
        JCNet.getInstance().addCallback(this)
        mediaChannel.volumeChangeNotify = false
        mediaDevice.setCameraProperty(1080, 576, 20)
        mediaDevice.defaultSpeakerOn = true
    }

    override fun onMediaChannelStateChange(state: Int, oldState: Int) {
    }

    override fun onMediaChannelPropertyChange(propChangeParam: JCMediaChannel.PropChangeParam) {
    }

    override fun onJoin(result: Boolean, reason: Int, channelId: String?) {
        EventBus.getDefault().post(JCRoomEvent(JCRoomEvent.Join(result, reason, channelId)))
        if (result) {
            mediaDevice.enableSpeaker(true)
        }
    }

    override fun onLeave(reason: Int, channelId: String?) {
        EventBus.getDefault().post(JCRoomEvent(JCRoomEvent.Leave(reason, channelId)))
    }

    override fun onStop(result: Boolean, reason: Int) {
        EventBus.getDefault().post(JCRoomEvent(JCRoomEvent.Stop(result, reason)))
    }

    override fun onQuery(operationId: Int, result: Boolean, reason: Int, queryInfo: JCMediaChannelQueryInfo?) {
    }

    override fun onParticipantJoin(participant: JCMediaChannelParticipant) {
        EventBus.getDefault().post(JCRoomEvent(JCRoomEvent.ParticipantJoin(participant)))
    }

    override fun onParticipantLeft(participant: JCMediaChannelParticipant) {
        EventBus.getDefault().post(JCRoomEvent(JCRoomEvent.ParticipantLeft(participant)))
    }

    override fun onParticipantUpdate(participant: JCMediaChannelParticipant, changeParam: JCMediaChannelParticipant.ChangeParam) {
        EventBus.getDefault().post(JCRoomEvent(JCRoomEvent.ParticipantUpdate(participant, changeParam)))
    }

    override fun onMessageReceive(type: String, content: String, fromUserId: String?) {
        EventBus.getDefault().post(JCRoomEvent(JCRoomEvent.MessageReceive(type, content, fromUserId)))
    }

    override fun onInviteSipUserResult(operationId: Int, result: Boolean, reason: Int) {
    }

    override fun onParticipantVolumeChange(p0: JCMediaChannelParticipant?) {
        EventBus.getDefault().post(JCRoomEvent(JCRoomEvent.ParticipantVolumeChange(p0)))
    }

    override fun onCameraUpdate() {
    }

    override fun onRenderReceived(canvas: JCMediaDeviceVideoCanvas?) {
    }

    override fun onRenderStart(canvas: JCMediaDeviceVideoCanvas?) {
    }

    override fun onAudioOutputTypeChange(type: Int) {
    }

    fun tryLogin() {
        if (net.hasNet()) {
            client.displayName = userName
            client.login(userPhone, userPass, null, null)
        }
    }

    override fun onLogout(reason: Int) {
        // 在退出登录之后，需要重新登录 http 服务器
        DataOperation.alreadyHttpLogin = false
        EventBus.getDefault().post(JCRoomEvent(JCRoomEvent.Logout(reason)))
    }

    override fun onLogin(result: Boolean, reason: Int) {
        EventBus.getDefault().post(JCRoomEvent(JCRoomEvent.Login(result, reason)))
    }

    override fun onClientStateChange(state: Int, oldState: Int) {
    }

    override fun onNetChange(newNetType: Int, oldNetType: Int) {
        tryLogin()
        EventBus.getDefault().post(JCRoomEvent(JCRoomEvent.NetChange(newNetType, oldNetType)))
    }

    override fun onVideoError(p0: JCMediaDeviceVideoCanvas?) {
    }
}