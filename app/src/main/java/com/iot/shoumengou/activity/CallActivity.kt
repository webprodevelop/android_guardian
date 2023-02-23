package com.iot.shoumengou.activity

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.iot.shoumengou.R
import com.iot.shoumengou.helper.JCDuo
import com.iot.shoumengou.helper.JCDuoEvent
import com.juphoon.cloud.JCCall
import com.juphoon.cloud.JCCallItem
import com.juphoon.cloud.JCMediaDevice
import com.juphoon.cloud.JCMediaDeviceVideoCanvas
import com.juphoon.cloud.juphooncommon.helper.JCCommonConstants
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import kotlinx.android.synthetic.main.duo_activity_call.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class CallActivity : AppCompatActivity() {

    private val mHandler = Handler()
    private lateinit var mTimerRunnable: Runnable
    private var mLocalCanvas: JCMediaDeviceVideoCanvas? = null
    private var mOtherCanvas: JCMediaDeviceVideoCanvas? = null
    private var mBeginDragPos: Point = Point(0, 0)
    private var mBeginCanvasPos: Point = Point(0, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        setContentView(R.layout.duo_activity_call)
        QMUIStatusBarHelper.setStatusBarLightMode(this)
        EventBus.getDefault().register(this)
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        flVideoPanel.removeAllViews()
        EventBus.getDefault().unregister(this)
    }

    private fun initView() {
        flVideoPanel.post {
            startTimer()
            updateUi()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: JCDuoEvent) {
        when (event.event) {
            is JCDuoEvent.CallAdd, is JCDuoEvent.CallUpdate, is JCDuoEvent.CallRemove -> {
                updateUi()
            }
        }
    }

    fun onEnd(view: View) {
        JCDuo.get().call.term(JCDuo.get().call.activeCallItem, JCCall.REASON_NONE, "")
        finish()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun updateUi() {
        val item = JCDuo.get().call.activeCallItem
        if (item == null) {
            stopTimer()
//            finish()
            return
        }
        val needAnswer = (item.direction == JCCall.DIRECTION_IN && item.state == JCCall.STATE_PENDING)
        val video = item.video
        llAudioIncoming.visibility = (if (!video && needAnswer) View.VISIBLE else View.INVISIBLE)
        llAudioIn.visibility = if (!video && !needAnswer) View.VISIBLE else View.INVISIBLE
        llVideoIncoming.visibility = if (video && needAnswer) View.VISIBLE else View.INVISIBLE
        llVideoIn.visibility = if (video && !needAnswer) View.VISIBLE else View.INVISIBLE
        ivAudioMute.visibility = (if (item.mute || item.state != JCCall.STATE_TALKING) View.INVISIBLE else View.VISIBLE)
        ivAudioUnMute.visibility = (if (item.mute && item.state == JCCall.STATE_TALKING) View.VISIBLE else View.INVISIBLE)
        ivAudioSpeaker.visibility = (if (JCDuo.get().mediaDevice.isSpeakerOn || item.state != JCCall.STATE_TALKING) View.INVISIBLE else View.VISIBLE)
        ivAudioUnSpeaker.visibility = (if (JCDuo.get().mediaDevice.isSpeakerOn && item.state == JCCall.STATE_TALKING) View.VISIBLE else View.INVISIBLE)
        ivVideoMute.visibility = (if (item.mute || item.state != JCCall.STATE_TALKING) View.INVISIBLE else View.VISIBLE)
        ivVideoUnMute.visibility = (if (item.mute && item.state == JCCall.STATE_TALKING) View.VISIBLE else View.INVISIBLE)
        ivVideoSpeaker.visibility = (if (JCDuo.get().mediaDevice.isSpeakerOn || item.state != JCCall.STATE_TALKING) View.INVISIBLE else View.VISIBLE)
        ivVideoUnSpeaker.visibility = (if (JCDuo.get().mediaDevice.isSpeakerOn && item.state == JCCall.STATE_TALKING) View.VISIBLE else View.INVISIBLE)
        icSwitchCamera.visibility = (if (item.state == JCCall.STATE_TALKING) View.VISIBLE else View.INVISIBLE)

        tvDisplayName.text = (if (item.displayName == item.userId) item.displayName.replace(JCCommonConstants.USEID_PREFIX_DUO, "") else item.displayName)
        if (item.state != JCCall.STATE_TALKING) {
            clControlPanel.visibility = View.VISIBLE
        }
        if (item.video && item.state >= JCCall.STATE_CONNECTING) {
            if (item.uploadVideoStreamSelf) {
                if (mLocalCanvas == null) {
                    mLocalCanvas = item.startSelfVideo(JCMediaDevice.RENDER_FULL_CONTENT)
                    mLocalCanvas!!.videoView.setZOrderMediaOverlay(true)
                    val params = FrameLayout.LayoutParams(QMUIDisplayHelper.getScreenWidth(this) / 4, QMUIDisplayHelper.getScreenHeight(this) / 4)
                    params.topMargin = QMUIDisplayHelper.dp2px(this, 16)
                    params.leftMargin = QMUIDisplayHelper.getScreenWidth(this) * 3 / 4 - QMUIDisplayHelper.dp2px(this, 16)
                    mLocalCanvas!!.videoView.layoutParams = params
                    flVideoPanel.addView(mLocalCanvas!!.videoView)
                    mLocalCanvas!!.videoView.setOnTouchListener { v, event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                mBeginDragPos.set(event.rawX.toInt(), event.rawY.toInt())
                                mBeginCanvasPos.set(v.x.toInt(), v.y.toInt())
                            }
                            MotionEvent.ACTION_MOVE -> {
                                val localLayoutParams = FrameLayout.LayoutParams(QMUIDisplayHelper.getScreenWidth(this) / 4, QMUIDisplayHelper.getScreenHeight(this) / 4)
                                localLayoutParams.topMargin = mBeginCanvasPos.y + event.rawY.toInt() - mBeginDragPos.y
                                localLayoutParams.leftMargin = mBeginCanvasPos.x + event.rawX.toInt() - mBeginDragPos.x
                                v.layoutParams = localLayoutParams
                            }
                            MotionEvent.ACTION_UP -> {
                            }
                        }
                        return@setOnTouchListener true
                    }
                }
            } else {
                if (mLocalCanvas != null) {
                    flVideoPanel.removeView(mLocalCanvas!!.videoView)
                    item.stopSelfVideo()
                    mLocalCanvas = null
                }
            }
            if (item.uploadVideoStreamOther) {
                if (mOtherCanvas == null) {
                    mOtherCanvas = item.startOtherVideo(JCMediaDevice.RENDER_FULL_CONTENT)
                    flVideoPanel.addView(mOtherCanvas!!.videoView, 0)
                }
            } else {
                if (mOtherCanvas != null) {
                    flVideoPanel.removeView(mOtherCanvas!!.videoView)
                    item.stopOtherVideo()
                    mOtherCanvas = null
                }
            }
        }
        if (this::mTimerRunnable.isInitialized) {
            mTimerRunnable.run()
        }
    }

    fun onAudioAnswer(view: View) {
        val item = JCDuo.get().call.activeCallItem
        if (item != null) {
            JCDuo.get().call.answer(item, false)
        }
    }

    fun onVideoAnswer(view: View) {
        val item = JCDuo.get().call.activeCallItem
        if (item != null) {
            JCDuo.get().call.answer(item, true)
        }
    }

    fun onMute(view: View) {
        ivAudioMute.visibility = View.INVISIBLE
        ivAudioUnMute.visibility = View.VISIBLE
        ivVideoMute.visibility = View.INVISIBLE
        ivVideoUnMute.visibility = View.VISIBLE
        val item = JCDuo.get().call.activeCallItem
        if (item != null) {
            JCDuo.get().call.mute(item)
        }
    }

    fun onUnMute(view: View) {
        ivAudioMute.visibility = View.VISIBLE
        ivAudioUnMute.visibility = View.INVISIBLE
        ivVideoMute.visibility = View.VISIBLE
        ivVideoUnMute.visibility = View.INVISIBLE
        val item = JCDuo.get().call.activeCallItem
        if (item != null) {
            JCDuo.get().call.mute(item)
        }
    }

    fun onSpeaker(view: View) {
        ivAudioSpeaker.visibility = View.INVISIBLE
        ivAudioUnSpeaker.visibility = View.VISIBLE
        ivVideoSpeaker.visibility = View.INVISIBLE
        ivVideoUnSpeaker.visibility = View.VISIBLE
        JCDuo.get().mediaDevice.enableSpeaker(true)
    }

    fun onUnSpeaker(view: View) {
        ivAudioSpeaker.visibility = View.VISIBLE
        ivAudioUnSpeaker.visibility = View.INVISIBLE
        ivVideoSpeaker.visibility = View.VISIBLE
        ivVideoUnSpeaker.visibility = View.INVISIBLE
        JCDuo.get().mediaDevice.enableSpeaker(false)
    }

    fun onSwitchCamera(view: View) {
        JCDuo.get().mediaDevice.switchCamera()
    }

    private fun startTimer() {
        if (!this::mTimerRunnable.isInitialized) {
            mTimerRunnable = Runnable {
                val item = JCDuo.get().call.activeCallItem
                if (item != null) {
                    tvInfo.text = genCallInfo(item)
                    if (item.state == JCCall.STATE_TALKING) {
                        if (item.video && item.direction == JCCall.DIRECTION_OUT) {
                            showNetState(item.videoNetSendStatus)
                        } else if (!item.video && item.direction == JCCall.DIRECTION_OUT) {
                            showNetState(item.audioNetSendStatus)
                        } else if (item.video && item.direction == JCCall.DIRECTION_IN) {
                            showNetState(item.videoNetReceiveStatus)
                        } else if (!item.video && item.direction == JCCall.DIRECTION_IN) {
                            showNetState(item.audioNetReceiveStatus)
                        }
                    } else {
                        ivNetState.setImageDrawable(null)
                    }
                    mHandler.postDelayed(mTimerRunnable, 1000);
                }
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

    private fun genCallInfo(item: JCCallItem?): String {
        if (item == null) {
            return ""
        }
        when (item.state) {
            JCCall.STATE_INIT -> return getString(R.string.duo_call_state_init)
            JCCall.STATE_PENDING -> return getString(R.string.duo_call_state_pending)
            JCCall.STATE_CONNECTING -> return getString(R.string.duo_call_state_connecting)
            JCCall.STATE_TALKING -> if (item.hold) {
                return getString(R.string.duo_call_state_hold)
            } else if (item.held) {
                return getString(R.string.duo_call_state_held)
            } else if (item.otherAudioInterrupt) {
                return getString(R.string.duo_call_state_audio_interrupt)
            } else {
                val secondes = System.currentTimeMillis() / 1000 - item.talkingBeginTime
                return String.format(Locale.getDefault(), "%02d:%02d", secondes / 60, secondes % 60)
            }
            JCCall.STATE_OK -> return getString(R.string.duo_call_state_ok)
            JCCall.STATE_CANCEL -> return getString(R.string.duo_call_state_ok)
            JCCall.STATE_CANCELED -> return getString(R.string.duo_call_state_canceled)
            JCCall.STATE_MISSED -> return getString(R.string.duo_call_state_missed)
            else -> return getString(R.string.duo_call_state_error)
        }
    }

    fun onControlPanel(view: View) {
        val item = JCDuo.get().call.activeCallItem
        if (item != null && item.video && item.state == JCCall.STATE_TALKING) {
            if (clControlPanel.visibility == View.VISIBLE) {
                clControlPanel.visibility = View.INVISIBLE
            } else {
                clControlPanel.visibility = View.VISIBLE
            }
        }
    }

    private fun showNetState(netStatus: Int) {
        when (netStatus) {
            JCCall.NET_STATUS_DISCONNECTED -> ivNetState.setImageDrawable(null)
            JCCall.NET_STATUS_VERY_BAD -> ivNetState.setImageResource(R.drawable.ic_meeting_volume1)
            JCCall.NET_STATUS_BAD -> ivNetState.setImageResource(R.drawable.ic_meeting_volume2)
            JCCall.NET_STATUS_NORMAL -> ivNetState.setImageResource(R.drawable.ic_meeting_volume3)
            JCCall.NET_STATUS_GOOD -> ivNetState.setImageResource(R.drawable.ic_meeting_volume4)
            JCCall.NET_STATUS_VERY_GOOD -> ivNetState.setImageResource(R.drawable.ic_meeting_volume5)
        }
    }
}
