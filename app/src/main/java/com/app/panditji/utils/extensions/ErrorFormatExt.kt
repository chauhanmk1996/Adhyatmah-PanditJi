package com.app.panditji.utils.extensions

import com.app.panditji.core.network.Error
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Unified error‑JSON parser that supports **both** the new flat schema
 *
 * ```json
 * { "error": true, "code": 404, "status": 0, "message": "Invalid password", "payload": {} }
 * ```
 *
 * **and** the legacy nested schema you were using before:
 *
 * ```json
 * { "error": { "errorMessage": "…", "errorCode": 506, "statusCode": 403 } }
 * ```
 *
 * It always returns a `core.network.Error` object so the rest of your code
 * (toasts, logging, etc.) stays unchanged.
 */
/*fun String.getError(): Error {
    var msg = "Unknown error"
    var errorCode = 0
    var statusCode = 0

    if (!isJSONValid(this)) return Error(errorCode, statusCode, msg)

    val root = JSONObject(this)

    // 🔹 NEW flat structure -------------------------------------------------
    if (root.has("message")) {
        msg = root.optString("message", msg)
    }
    if (root.has("code")) {
        errorCode = root.optInt("code", errorCode)
    }
    if (root.has("status")) {
        statusCode = root.optInt("status", statusCode)
    }

    // 🔹 Legacy nested structure -------------------------------------------
    if (root.has("error")) {
        val inner = root.opt("error")
        if (inner is JSONObject) {
            msg = inner.optString("errorMessage", msg)
            errorCode = inner.optInt("errorCode", errorCode)
            statusCode = inner.optInt("statusCode", statusCode)
        }
    }

    return Error(errorCode, statusCode, msg)
}*/

fun String.getError(): Error {
    var msg = "Unknown error"
    var errorCode = 0
    var statusCode = 0

    if (!isJSONValid(this)) return Error(errorCode, statusCode, msg)

    val root = JSONObject(this)

    // 🔹 New flat structure -----------------------------------------------
    val devMsg = root.optString("dev_message", "").takeIf { it.isNotBlank() }
    msg = devMsg ?: root.optString("message", msg) // Use dev_message if available, else message

    if (root.has("code")) {
        errorCode = root.optInt("code", errorCode)
    }
    if (root.has("status")) {
        statusCode = root.optInt("status", statusCode)
    }

    // 🔹 Legacy nested structure -------------------------------------------
    if (root.has("error")) {
        val inner = root.opt("error")
        if (inner is JSONObject) {
            msg = inner.optString("errorMessage", msg)
            errorCode = inner.optInt("errorCode", errorCode)
            statusCode = inner.optInt("statusCode", statusCode)
        }
    }

    return Error(errorCode, statusCode, msg)
}

/** Simple JSON validity check (object or array). */
fun isJSONValid(test: String?): Boolean {
    if (test.isNullOrBlank()) return false
    return try {
        JSONObject(test)
        true
    } catch (ex: JSONException) {
        try {
            JSONArray(test)
            true
        } catch (ex1: JSONException) {
            false
        }
    }
}
