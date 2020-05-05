package jp.ryuk.deglog.utilities

import android.app.Activity
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager

// デバッグ用 ログ解析タグ
const val deg = "DEBUG"

fun hideKeyboard(activity: Activity, view: View, event: MotionEvent?): Boolean {
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    view.requestFocus()
    return view.onTouchEvent(event)
}