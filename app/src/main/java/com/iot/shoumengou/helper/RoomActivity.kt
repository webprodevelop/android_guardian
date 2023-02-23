package com.iot.shoumengou.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.iot.shoumengou.BuildConfig
import com.iot.shoumengou.R
import com.juphoon.cloud.JCClient
import com.juphoon.cloud.JCMediaChannel
import com.juphoon.cloud.juphooncommon.CommonApplication
import com.juphoon.cloud.juphooncommon.helper.JCCommon
import com.juphoon.cloud.juphooncommon.helper.JCCommonConstants
import com.juphoon.cloud.juphooncommon.helper.JCCommonUtils
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.util.*
import kotlinx.android.synthetic.main.activity_call_service.*

class RoomActivity : AppCompatActivity() {
    companion object {
        private const val MSG_LET_JOIN = 1001

        @JvmStatic public var chatId: String = ""
        @JvmStatic public var chatPass: String = ""
    }

    private val mRoomItems: MutableList<RoomItem> = ArrayList()
    private val mHandler = Handler()
    private lateinit var mTimerRunnable: Runnable
    private var mBeginTime = System.currentTimeMillis()
    private lateinit var mAlert: AlertDialog
    private lateinit var mStatus: String

    private fun startLogin() {
        if (JCRoom.get().client.state != JCClient.STATE_LOGINED) {
            JCRoom.get().tryLogin()
        } else {
            mpHandler.sendEmptyMessage(MSG_LET_JOIN)
        }
    }

    private fun joinToRoom(room: String, pass: String) {
        if (!JCCommon.get().net!!.hasNet()) {
            Toast.makeText(this, getString(R.string.room_no_net), Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        if (room.isEmpty() || pass.isEmpty()) {
            finish()
            return
        }
        mStatus = getString(R.string.room_joining)
        mBeginTime = System.currentTimeMillis()
        JCRoom.get().mediaChannel.enableUploadAudioStream(true)
        JCRoom.get().mediaChannel.enableUploadVideoStream(true)
        JCRoom.get().mediaChannel.enableAudioOutput(true)
        val param = JCMediaChannel.JoinParam()
        if (pass.isNotEmpty()) {
            param.password = pass
        }
        //param.maxResolution = JCCommonUtils.getResolution()
        //param.framerate = JCCommonUtils.getFramerate()
        param.maxResolution = 0
        param.framerate = 20
        param.capacity = 2
        if (JCRoom.get().mediaChannel.join(room, param)) {
        } else {
            Toast.makeText(this, getString(R.string.room_join_channel_failed), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CommonApplication.setContext(Application())
        JCRoom.get()

        mStatus = getString(R.string.room_logining);
        askForPermissions(Permission.CAMERA, Permission.RECORD_AUDIO) {
            if (it.isAllGranted()) {
            }
        }
        setContentView(R.layout.activity_call_service)
        EventBus.getDefault().register(this)
        startLogin()
        initView()
        mAlert = AlertDialog.Builder(this)
                .setTitle(getString(R.string.room_tip))
                .setMessage(getString(R.string.room_leave_sure))
                .setPositiveButton(getString(R.string.room_leave)) { dialog, _ ->
                    if (JCRoom.get().mediaChannel.participants.size == 1) {
                        JCRoom.get().mediaChannel.stop()
                    } else {
                        //JCRoom.get().mediaChannel.leave()
                        JCRoom.get().mediaChannel.stop()
                    }
                    (dialog as AlertDialog).setOnDismissListener(null)
                    finish()
                }.setNegativeButton(R.string.room_cancel) {dialog, _ ->
                }.setOnDismissListener {
                }.create();
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        stopTimer()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        val jsonString = intent?.getStringExtra("notificationExtras")
        if (jsonString.isNullOrBlank()) {
            return
        }
        val json = JSONObject(intent.getStringExtra("notificationExtras"))
        val senderName = json.optString("selfName")
        val roomName = json.optString("roomName")
        val roomPassword = json.optString("roomPWD")
        val notifyType = json.optString("notitype")

        if (notifyType != JCCommonConstants.NOTIFY_ROOM_INVITE_PEOPLE) {
            return
        }
        if (senderName == JCRoom.get().mediaChannel.channelId) {
            // 该用户已在此房间中
            return
        }

        val dialogBuilder = QMUIDialog.MessageDialogBuilder(this).apply {
            setTitle(getString(R.string.room_want_to_join_room))
            setMessage("$senderName 邀请您加入 $roomName 房间")
            addAction(getString(R.string.room_cancel))
            { dialog, _ ->
                dialog.dismiss()
            }
            addAction(getString(R.string.room_join))
            { dialog, _ ->
                // 离开原来的房间
                JCRoom.get().mediaChannel.leave()
                val backIntent = Intent()
                backIntent.putExtra("receiveInviteRoomName", roomName)
                backIntent.putExtra("receiveInviteRoomPassword", roomPassword)
                setResult(Activity.RESULT_OK, backIntent)
                dialog.dismiss()
            }
        }
        dialogBuilder.create().show()
    }

    private fun initView() {
        QMUIStatusBarHelper.translucent(this)
        QMUIStatusBarHelper.setStatusBarLightMode(this)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        flPartp.post {
            layoutItemUI()
            refreshItemUI()
        }
        //tvRoomId.text = JCRoom.get().mediaChannel.channelId.replaceFirst(JCCommonConstants.ROOMID_PREFIX_ROOM, "")
        initNetStatus()
        startTimer()
    }

    override fun onBackPressed() {
        onLeave(ivLeave)
    }

    private val mpHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_LET_JOIN -> {
                    joinToRoom(chatId, chatPass)
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: JCRoomEvent) {
        when (event.event) {
            is JCRoomEvent.Stop, is JCRoomEvent.Leave -> {
                finish()
            }
            is JCRoomEvent.ParticipantJoin -> {
                mStatus = ""
                mBeginTime = System.currentTimeMillis()
                layoutItemUI()
                refreshItemUI()
            }
            is JCRoomEvent.ParticipantLeft -> {
                layoutItemUI()
                refreshItemUI()
                JCRoom.get().mediaChannel.stop()
                finish()
            }
            is JCRoomEvent.ParticipantUpdate -> {
                refreshItemUI()
                updateNetStatus(event.event.participant.netStatus)
            }

            is JCRoomEvent.Login -> {
                if (!event.event.result) {
                    Toast.makeText(this, "client login failed, reason = " + event.event.reason, Toast.LENGTH_SHORT).show()
                    return
                }
                mpHandler.sendEmptyMessage(MSG_LET_JOIN)
            }
            is JCRoomEvent.Join -> {
                if (event.event.result) {
                    layoutItemUI()
                    refreshItemUI()
                    if (JCRoom.get().mediaChannel.participants.size > 1) {
                        mStatus = ""
                    } else {
                        mStatus = getString(R.string.room_waiting_participant)
                    }
                    mBeginTime = System.currentTimeMillis()
                } else {
                    when (event.event.reason) {
                        JCMediaChannel.REASON_INVALID_PASSWORD -> Toast.makeText(this, getString(R.string.room_join_channel_failed_invalid_password), Toast.LENGTH_SHORT).show()
                        JCMediaChannel.REASON_FULL -> Toast.makeText(this, getString(R.string.room_join_channel_failed_full), Toast.LENGTH_SHORT).show()
                        JCMediaChannel.REASON_INTERNAL_ERROR -> Toast.makeText(this, getString(R.string.room_join_channel_failed_full), Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(this, getString(R.string.room_join_channel_failed), Toast.LENGTH_SHORT).show()
                    }
                    finish()
                }
            }
            is JCRoomEvent.Logout -> {
            }
            is JCRoomEvent.NetChange -> {
            }
        }
    }

    /**
     * 视频成员布局调整
     */
    private fun layoutItemUI() {
        val partps = JCRoom.get().mediaChannel.participants
        val subViewRects = JCCommonUtils.caclSubViewRect(flPartp.width, flPartp.height, partps.size)
        var i = 0
        while (true) {
            if (i < partps.size) {
                val partp = partps[i]
                val subViewRect = subViewRects[i]
                val item: RoomItem
                if (mRoomItems.size <= i) {
                    item = RoomItem(this)
                    mRoomItems.add(item)
                    flPartp.addView(item.constraintLayout)
                } else {
                    item = mRoomItems.get(i)
                }
                if (item.partp !== partp) {
                    item.reset()
                    item.partp = partp
                }
                item.rect = subViewRect
                val params = FrameLayout.LayoutParams(subViewRect.width, subViewRect.height)
                params.setMargins(subViewRect.x, subViewRect.y, 0, 0)
                item.constraintLayout.layoutParams = params
                i++
                continue
            } else if (i < mRoomItems.size) {
                for (j in mRoomItems.size - 1 downTo i) {
                    mRoomItems.get(j).reset()
                    flPartp.removeView(mRoomItems.get(j).constraintLayout)
                    mRoomItems.removeAt(i)
                }
            }
            break
        }
    }

    private fun refreshItemUI() {
        for (item in mRoomItems) {
            item.deal()
        }
    }

    fun onLeave(view: View) {
        mAlert.show()
    }

    fun onCamera(view: View) {
        if (view == ivCameraOpen) {
            JCRoom.get().mediaChannel.enableUploadVideoStream(false)
            ivCameraOpen.visibility = View.INVISIBLE
            ivCameraClose.visibility = View.VISIBLE
        } else {
            JCRoom.get().mediaChannel.enableUploadVideoStream(true)
            ivCameraOpen.visibility = View.VISIBLE
            ivCameraClose.visibility = View.INVISIBLE
        }
    }

    fun onSwitchCamera(view: View) {
        JCRoom.get().mediaDevice.switchCamera()
    }

    fun onMute(view: View) {
        if (view == ivMute) {
            JCRoom.get().mediaChannel.enableUploadAudioStream(true)
            ivMute.visibility = View.INVISIBLE
            ivUnMute.visibility = View.VISIBLE
        } else {
            JCRoom.get().mediaChannel.enableUploadAudioStream(false)
            ivMute.visibility = View.VISIBLE
            ivUnMute.visibility = View.INVISIBLE
        }
    }

    fun onSpeaker(view: View) {
        if (view == ivSpeaker) {
            JCRoom.get().mediaDevice.enableSpeaker(false)
            ivSpeaker.visibility = View.INVISIBLE
            ivUnSpeaker.visibility = View.VISIBLE
        } else {
            JCRoom.get().mediaDevice.enableSpeaker(true)
            ivSpeaker.visibility = View.VISIBLE
            ivUnSpeaker.visibility = View.INVISIBLE
        }
    }

    fun onShowFull(view: View) {
        if (clControl.visibility == View.VISIBLE) {
            clControl.visibility = View.INVISIBLE
        } else {
            clControl.visibility = View.VISIBLE
        }
    }

    private fun startTimer() {
        if (!this::mTimerRunnable.isInitialized) {
            mTimerRunnable = Runnable {
                val l = (System.currentTimeMillis() - mBeginTime) / 1000
                var strStatus = mStatus + " " + String.format("%02d:%02d", l / 60, l % 60)
                tvTime.text = strStatus
                mHandler.postDelayed(mTimerRunnable, 1000);
            }
        }
        mHandler.post(mTimerRunnable)
    }

    private fun stopTimer() {
        if (!this::mTimerRunnable.isInitialized) {
            return
        }
        mHandler.removeCallbacks(mTimerRunnable)
    }

    /**
     * 更新自己的会议网络状态
     */
    private fun updateNetStatus(netStatus: Int) {
        when (netStatus) {
            JCMediaChannel.NET_STATUS_VERY_BAD -> {
                ivNetStatus.setBackgroundResource(R.drawable.ic_meeting_volume1)
            }
            JCMediaChannel.NET_STATUS_BAD -> {
                ivNetStatus.setBackgroundResource(R.drawable.ic_meeting_volume2)
            }
            JCMediaChannel.NET_STATUS_NORMAL -> {
                ivNetStatus.setBackgroundResource(R.drawable.ic_meeting_volume3)
            }
            JCMediaChannel.NET_STATUS_GOOD -> {
                ivNetStatus.setBackgroundResource(R.drawable.ic_meeting_volume4)
            }
            JCMediaChannel.NET_STATUS_VERY_GOOD -> {
                ivNetStatus.setBackgroundResource(R.drawable.ic_meeting_volume5)
            }
        }
    }

    /**
     * 初始化自己的会议网络状态
     */
    private fun initNetStatus() {
        val partps = JCRoom.get().mediaChannel.participants
        for (partp in partps) {
            if (partp.isSelf) {
                updateNetStatus(partp.netStatus)
            }
        }
    }
}
