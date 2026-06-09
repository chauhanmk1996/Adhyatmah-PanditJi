package com.app.panditji.utils.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Int.getTimeFormat(): String {
    return String.format("%02d:%02d", (this % 3600) / 60, (this % 60))
}

fun String.getLocalDate(): Date? {
    val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val df1 = SimpleDateFormat("HH:mm", Locale.getDefault())
    df.timeZone = TimeZone.getTimeZone("UTC")
    df1.timeZone = TimeZone.getTimeZone("UTC")
    val date = df.parse(this)

    return date
}

fun String.getPostedTime(otherPattern:Boolean=false): String {
    val postDate =  if (!otherPattern) this.getLocalDate() else this.getPassedTime()
    val date = Date()
    if (date.after(postDate)) {
        val time = (date.time - (postDate?.time ?: Date().time)) / 1000
        when {
            time>(24*60*60*365)->{
                val count=time.div(24 * 60 * 60* 365).toInt()
                return ("$count ${if (count==1)"year" else "years"} ago")
            }

            time>(24*60*60*30)->{
                val count=time.div(24 * 60 * 60* 30).toInt()
                return ("$count ${if (count==1)"month" else "months"} ago")
            }

            time > (24 * 60 * 60) -> {
                val count=time.div(24 * 60 * 60).toInt()
                return ("$count ${if (count==1)"day" else "days"} ago")
            }
            time > 60 * 60 -> {
                val count =time.div(60 * 60).toInt()
                return ("$count ${if (count==1)"hr" else "hrs"} ago")
            }
            time>60 -> {
                val count =time.div(60).toInt()
                return ("$count ${if (count==1)"min" else "mins"} ago")
            }
            else->{
                return ("$time  ${if (time.toInt()==1) "sec" else "secs" } ago")
            }
        }
    } else {
        return ("")
    }
}


//for other patterns
fun String.getPassedTime(): Date? {
    val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    df.timeZone = TimeZone.getTimeZone("UTC")
    val date = df.parse(this)

    return date
}


