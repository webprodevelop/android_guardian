package com.iot.shoumengou.realm

import com.juphoon.cloud.JCCallItem
import com.juphoon.cloud.juphooncommon.helper.JCCommonUtils
import io.realm.DynamicRealm
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmMigration
import java.util.*

object RealmHelper {
    private var sRealmConfiguration: RealmConfiguration? = null
    private var sConfigurationMap: HashMap<String, RealmConfiguration>? = HashMap()
    private var CURRENT_VERSION = 0L

    private class Migration : RealmMigration {

        override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
            // TODO
        }
    }

    fun getInstance(): Realm? {
        var realmFileName = JCCommonUtils.getAccount()
        if (realmFileName.isNullOrEmpty()) {
            realmFileName = "default"
        }
        return getInstance(realmFileName)
    }

    private fun getInstance(realmFilePrefix: String): Realm? {
        val realmFileName = "$realmFilePrefix.realm"
        sRealmConfiguration = sConfigurationMap!![realmFileName]
        if (sRealmConfiguration == null) {
            synchronized(RealmHelper::class.java) {
                if (sRealmConfiguration == null) {
                    sRealmConfiguration = RealmConfiguration.Builder().name(realmFileName).schemaVersion(CURRENT_VERSION).migration(Migration()).build()
                    sConfigurationMap!![realmFileName] = sRealmConfiguration!!
                }
            }
        }
        return Realm.getInstance(sRealmConfiguration!!)
    }

    /**
     * 加入一条通话日志
     *
     * @param item
     */
    fun addCall(item: JCCallItem) {
        val realm = getInstance()
        realm!!.executeTransaction {
            val realmCallLog = RealmCallLog()
            realmCallLog.startTime = item.beginTime
            realmCallLog.phone = item.userId
            realmCallLog.nickName = item.displayName
            realmCallLog.video = item.video
            realmCallLog.direction = item.direction
            realmCallLog.state = item.state
            it.insertOrUpdate(realmCallLog)
        }
    }

    /**
     * 更新一条通话日志
     *
     * @param item
     */
    fun updateCall(item: JCCallItem) {
        val realm = getInstance()
        val realmCallLog = realm!!.where(RealmCallLog::class.java).equalTo(RealmCallLog.FILED_START_TIME, item.beginTime).findFirst()
        if (realmCallLog != null) {
            realm.executeTransaction {
                realmCallLog.nickName = item.displayName
                realmCallLog.video = item.video
                realmCallLog.endTime = System.currentTimeMillis() / 1000
                realmCallLog.state = item.state
                realmCallLog.talkingStartTime = item.talkingBeginTime
                if (realmCallLog.talkingStartTime != 0L) {
                    realmCallLog.duration = (realmCallLog.endTime!! - realmCallLog.talkingStartTime!!).toInt()
                }
                it.insertOrUpdate(realmCallLog)
            }
        }

    }

    /**
     * 删除一条通话日志
     *
     * @param startTime
     */
    fun removeCall(startTime: Long) {
        val realm = getInstance()
        val realmCallLog = realm!!.where(RealmCallLog::class.java).equalTo(RealmCallLog.FILED_START_TIME, startTime).findFirst()
        if (realmCallLog != null) {
            realm.executeTransaction {
                realmCallLog.deleteFromRealm()
            }
        }
    }
}