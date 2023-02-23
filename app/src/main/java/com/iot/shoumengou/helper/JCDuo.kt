package com.iot.shoumengou.helper

import com.iot.shoumengou.App
import com.juphoon.cloud.*
import com.iot.shoumengou.realm.RealmHelper
import com.juphoon.cloud.juphooncommon.data.DataOperation
import com.juphoon.cloud.juphooncommon.helper.JCCommonConstants
import com.juphoon.cloud.juphooncommon.helper.JCCommonUtils
import org.greenrobot.eventbus.EventBus

class JCDuo private constructor() : JCCallCallback, JCMediaDeviceCallback, JCMessageChannelCallback,
        JCClientCallback, JCNetCallback, JCAccountCallback {

    var client: JCClient = JCClient.create(App.Instance(), "63lYKVqlK4UpekBihdRJFkb1g4ccc9", this, null)
    val mediaDevice: JCMediaDevice = JCMediaDevice.create(client, this)
    val call: JCCall = JCCall.create(client, mediaDevice, this)
    var net: JCNet = JCNet.getInstance()
    val messageChannel: JCMessageChannel = JCMessageChannel.create(client, this)
    val account: JCAccount = JCAccount.create(this)

    companion object {
        const val DUO_DATA_TYPE_NICKNAME = "DUO_DATA_TYPE_NICKNAME"

        private var instance: JCDuo? = null
            get() {
                if (field == null) {
                    field = JCDuo()
                }
                return field
            }

        @Synchronized
        fun get(): JCDuo {
            return instance!!
        }
    }

    init {
        JCNet.getInstance().addCallback(this)
        mediaDevice.setCameraProperty(1280, 720, 30)
        call.maxCallNum = 1
    }

    override fun onCameraUpdate() {
    }

    override fun onRenderReceived(canvas: JCMediaDeviceVideoCanvas?) {
    }

    override fun onRenderStart(canvas: JCMediaDeviceVideoCanvas?) {
    }

    override fun onAudioOutputTypeChange(type: Int) {
    }

    override fun onCallItemRemove(callItem: JCCallItem?, reason: Int, description: String?) {
        RealmHelper.updateCall(callItem!!)
        EventBus.getDefault().post(JCDuoEvent(JCDuoEvent.CallRemove(callItem, reason, description)))
    }

    override fun onMessageReceive(type: String?, content: String?, callItem: JCCallItem?) {
    }

    override fun onMissedCallItem(callItem: JCCallItem?) {
        RealmHelper.addCall(callItem!!)
        EventBus.getDefault().post(JCDuoEvent(JCDuoEvent.CallMissed(callItem)))
    }

    override fun onCallItemAdd(callItem: JCCallItem?) {
        RealmHelper.addCall(callItem!!)
        EventBus.getDefault().post(JCDuoEvent(JCDuoEvent.CallAdd(callItem)))
    }

    override fun onCallItemUpdate(callItem: JCCallItem?, changeParam: JCCallItem.ChangeParam?) {
        EventBus.getDefault().post(JCDuoEvent(JCDuoEvent.CallUpdate(callItem, changeParam)))
    }

    override fun onMessageSendUpdate(message: JCMessageChannelItem?) {

    }

    override fun onMessageRecv(message: JCMessageChannelItem?) {
        if (message!!.messageType == DUO_DATA_TYPE_NICKNAME) {
            if (message.userId == call.activeCallItem?.userId) {
                // 主叫收到被叫发来的昵称
                call.activeCallItem.displayName = message.text
                RealmHelper.updateCall(call.activeCallItem!!)
            }
        }
    }

    fun tryLogin() {
        if (net.hasNet()) {
            client.displayName = JCCommonUtils.getDisplayName()
            val userId = JCCommonConstants.USEID_PREFIX_DUO + JCCommonUtils.getAccount()
            client.login(userId, "123", null, null)
        }
    }

    override fun onLogin(result: Boolean, reason: Int) {
        EventBus.getDefault().post(JCDuoEvent(JCDuoEvent.Login(result, reason)))
    }

    override fun onLogout(reason: Int) {
        DataOperation.alreadyHttpLogin = false// 在退出登录之后，需要重新登录 http 服务器
        EventBus.getDefault().post(JCDuoEvent(JCDuoEvent.Logout(reason)))
    }

    override fun onClientStateChange(state: Int, oldState: Int) {
    }

    override fun onNetChange(newNetType: Int, oldNetType: Int) {
        if (DataOperation.alreadyHttpLogin) {
            tryLogin()
        }
        EventBus.getDefault().post(JCDuoEvent(JCDuoEvent.NetChange(newNetType, oldNetType)))
    }

    override fun onDrawBackMessageResult(p0: Int, p1: Boolean, p2: Int) {
    }

    override fun onMessageListRecv(p0: MutableList<JCMessageChannelItem>?) {
    }

    override fun onMarkRecvResult(p0: Int, p1: Boolean, p2: Int) {
    }

    override fun onReceiveMarkRead(p0: String?, p1: Long) {
    }

    override fun onMarkReadResult(p0: Int, p1: Boolean, p2: Int) {
    }

    override fun onFetchMessageResult(p0: Int, p1: Boolean, p2: Int) {
    }

    override fun onRefreshConversation(p0: Int, p1: Boolean, p2: MutableList<JCMessageChannelConversation>?, p3: Int, p4: Long, p5: String?) {
    }

    override fun onReceiveMarkRecv(p0: String?, p1: Long) {
    }

    override fun onDtmfReceived(item: JCCallItem?, value: Int) {

    }

    override fun onSetContactDnd(operationId: Int, result: Boolean, reason: Int) {
    }

    override fun onQueryServerUidResult(operationId: Int, result: Boolean, resultMap: MutableMap<String, String>?) {
        EventBus.getDefault().post(JCDuoEvent(JCDuoEvent.QueryServerUidResult(operationId, result, resultMap)))
    }

    override fun onQueryUserStatusResult(operationId: Int, result: Boolean, accountItemList: MutableList<JCAccountItem>?) {
    }

    override fun onDealContact(operationId: Int, result: Boolean, reason: Int) {
    }

    override fun onQueryUserIdResult(operationId: Int, result: Boolean, resultMap: MutableMap<String, String>?) {
    }

    override fun onRefreshContacts(operationId: Int, result: Boolean, contacts: MutableList<JCAccountContact>?, updateTime: Long, fullUpdate: Boolean) {
    }

    override fun onContactsChange(contactList: MutableList<JCAccountContact>?) {
    }

    override fun onVideoError(p0: JCMediaDeviceVideoCanvas?) {
    }
}