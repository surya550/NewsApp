package com.surya.newsapp.util

import java.text.SimpleDateFormat
import java.util.*

class TimeStamp {

    companion object {

        fun getMilliToDate(date: String?): String {
            val calendar = Calendar.getInstance()
            calendar.timeZone = TimeZone.getTimeZone(date)

            val sdf = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a")
            sdf.timeZone = TimeZone.getDefault()
            val result = sdf.format(calendar.time)

            return result
        }


    }
}