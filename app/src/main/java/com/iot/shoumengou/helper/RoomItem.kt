package com.iot.shoumengou.helper

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.iot.shoumengou.R
import com.juphoon.cloud.JCMediaChannelParticipant
import com.juphoon.cloud.JCMediaDevice
import com.juphoon.cloud.JCMediaDeviceVideoCanvas
import com.juphoon.cloud.juphooncommon.helper.JCCommonUtils

class RoomItem(activity: Activity) {

    var partp: JCMediaChannelParticipant? = null
    var canvas: JCMediaDeviceVideoCanvas? = null
    var constraintLayout: ConstraintLayout = activity.layoutInflater.inflate(R.layout.room_layout_view_partp, null) as ConstraintLayout
    var tvDisplayName: TextView
    var rect: JCCommonUtils.SubViewRect? = null

    init {
        tvDisplayName = constraintLayout.findViewById<View>(R.id.tvDisplayName) as TextView
    }

    fun reset() {
        if (canvas != null) {
            // 关闭视频请求
            partp?.stopVideo()
            constraintLayout.removeView(canvas!!.videoView)
            canvas = null
        }
    }

    fun deal() {
        if (partp!!.isVideo) {
            if (canvas == null) {
                val requestSize = JCCommonUtils.caclRequestVideoSize(rect!!)
                canvas = partp?.startVideo(JCMediaDevice.RENDER_FULL_SCREEN, requestSize)
                constraintLayout.addView(canvas?.videoView, 1)
            }
        } else {
            if (canvas != null) {
                reset()
            }
        }
        tvDisplayName.text = partp!!.displayName
    }
}