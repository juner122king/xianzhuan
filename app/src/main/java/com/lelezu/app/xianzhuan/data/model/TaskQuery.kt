package com.lelezu.app.xianzhuan.data.model

import android.os.Parcel
import android.os.Parcelable

/**
 * @author:Administrator
 * @date:2023/7/26 0026
 * @description:任务查询条件类
 *
 */
data class TaskQuery(

    val queryCond: String?,
    var current: Int = 1,
    val highPrice: Float? = null,
    val lowPrice: Float? = null,
    val size: Int? = null,
    val taskStatus: Int? = null,
    val taskTypeId: String? = null,
    val taskTitle: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as Int,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeString(queryCond)
        parcel.writeValue(current)
        parcel.writeValue(highPrice)
        parcel.writeValue(lowPrice)
        parcel.writeValue(size)
        parcel.writeValue(taskStatus)
        parcel.writeString(taskTypeId)
        parcel.writeString(taskTitle)
    }

    companion object CREATOR : Parcelable.Creator<TaskQuery> {
        override fun createFromParcel(parcel: Parcel): TaskQuery {
            return TaskQuery(parcel)
        }

        override fun newArray(size: Int): Array<TaskQuery?> {
            return arrayOfNulls(size)
        }
    }

    fun toMap(): Map<String, String?> {
        val map = mutableMapOf<String, String?>()
        if (queryCond != null) map["queryCond"] = queryCond
        map["current"] = current.toString()
        if (highPrice != null) map["highPrice"] = highPrice.toString()
        if (lowPrice != null) map["lowPrice"] = lowPrice.toString()
        if (size != null) map["size"] = size.toString()
        if (taskStatus != null) map["taskStatus"] = taskStatus.toString()
        if (taskTypeId != null) map["taskTypeId"] = taskTypeId
        if (taskTitle != null) map["taskTitle"] = taskTitle
        return map
    }
}
