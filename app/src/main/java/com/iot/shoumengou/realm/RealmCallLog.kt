package com.iot.shoumengou.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmCallLog : RealmObject() {
    companion object {
        const val FILED_START_TIME = "startTime"
    }

    @PrimaryKey
    var startTime: Long? = null
    var endTime: Long? = null
    var phone: String? = null
    var nickName: String? = null
    var video: Boolean? = null
    var duration: Int? = 0
    var direction: Int? = null
    var state: Int? = null
    var talkingStartTime: Long? = null
}