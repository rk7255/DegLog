package jp.ryuk.deglog.utilities

import android.app.Activity
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import jp.ryuk.deglog.R

// デバッグ用 ログ解析タグ
const val deg = "DEBUG"

fun hideKeyboard(activity: Activity, view: View, event: MotionEvent?): Boolean {
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    view.requestFocus()
    return view.onTouchEvent(event)
}

fun colorSelector(color: Int?): Int? {
    return when (color) {
        0 -> R.color.pink
        1 -> R.color.blue
        2 -> R.color.green
        3 -> R.color.yellow
        4 -> R.color.orange
        5 -> R.color.purple
        6 -> R.color.primaryColor
        7 -> R.color.gray
        else -> null
    }
}

fun colorSelectorRGB(color: Int?): String {
    return when (color) {
        0 -> "#F08279"
        1 -> "#3AB6EF"
        2 -> "#9AFF6E"
        3 -> "#FFFE77"
        4 -> "#FFBF5B"
        5 -> "#D768E8"
        6 -> "#C6AA80"
        7 -> "#AEAEAE"
        else -> "#3D332A"
    }
}



fun iconSelector(context: Context, type: String?): Int {
    return when (type) {
        null -> R.drawable.nav_ic_diary
        // 大型
        context.getString(R.string.animal_ushi) -> R.drawable.img_big_ushi
        context.getString(R.string.animal_uma) -> R.drawable.img_big_uma
        context.getString(R.string.animal_buta) -> R.drawable.img_big_buta
        context.getString(R.string.animal_hitsuji) -> R.drawable.img_big_hitsuji
        context.getString(R.string.animal_yagi) -> R.drawable.img_big_yagi
        // 中型
        context.getString(R.string.animal_inu) -> R.drawable.img_medium_inu
        context.getString(R.string.animal_neko) -> R.drawable.img_medium_neko
        context.getString(R.string.animal_kitsune) -> R.drawable.img_medium_kitsune
        context.getString(R.string.animal_tanuki) -> R.drawable.img_medium_tanuki
        // 小型
        context.getString(R.string.animal_usagi) -> R.drawable.img_small_usagi
        context.getString(R.string.animal_degu) -> R.drawable.img_small_degu
        context.getString(R.string.animal_chinchira) -> R.drawable.img_small_chinchira
        context.getString(R.string.animal_hamster) -> R.drawable.img_small_hamster
        context.getString(R.string.animal_harinezumi) -> R.drawable.img_small_harinezumi
        context.getString(R.string.animal_nezumi) -> R.drawable.img_small_nezumi
        context.getString(R.string.animal_marmot) -> R.drawable.img_small_marmot
        context.getString(R.string.animal_risu) -> R.drawable.img_small_risu
        context.getString(R.string.animal_momonga) -> R.drawable.img_small_momonga
        // 鳥類
        context.getString(R.string.animal_inko) -> R.drawable.img_bird_inko
        context.getString(R.string.animal_finchi) -> R.drawable.img_bird_finchi
        context.getString(R.string.animal_moukin) -> R.drawable.img_bird_moukinrui
        // その他
        context.getString(R.string.animal_hachurui) -> R.drawable.img_etc_hachurui
        context.getString(R.string.animal_gyorui) -> R.drawable.img_etc_gyorui
        context.getString(R.string.animal_konchu) -> R.drawable.img_etc_konchu

        else -> R.drawable.nav_ic_diary
    }
}
