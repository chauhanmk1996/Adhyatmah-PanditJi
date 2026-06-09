package com.app.panditji.utils.extensions

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

fun String.isValidJson(): Boolean {
    try {
        JSONObject(this)
    } catch (ex: JSONException) {
        // edited, to include @Arthur's comment
        // e.g. in case JSONArray is valid as well...
        try {
            JSONArray(this)
        } catch (ex1: JSONException) {
            return false
        }
    }
    return true
}

fun String.getMessage():String{
    if (this.isValidJson()) {
        val jsonObject = JSONObject(this)
        if (jsonObject.has("message")) {
            return jsonObject.getString("message")
        }
        else return "Error"
    }
    else
        return "Error"
}

fun String.getStatus():Int{
    if (this.isValidJson()){
        val jsonObject = JSONObject(this)
        if (jsonObject.has("status")) {
            return jsonObject.getInt("status")
        }
        else return 0
    }
    else return 0

}

