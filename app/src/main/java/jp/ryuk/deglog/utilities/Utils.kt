package jp.ryuk.deglog.utilities

import android.app.Activity
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import jp.ryuk.deglog.R

// デバッグ用 ログ解析タグ
const val deg = "DEBUG"

fun hideKeyboard(activity: Activity, view: View, event: MotionEvent?): Boolean {
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    view.requestFocus()
    return view.onTouchEvent(event)
}

fun iconSelector(type: String?): Int {
    return when(type) {
        null -> R.drawable.nav_ic_diary
        // 大型
        "牛" -> R.drawable.img_big_ushi
        "馬" -> R.drawable.img_big_uma
        "豚" -> R.drawable.img_big_buta
        "羊" -> R.drawable.img_big_hitsuji
        "ヤギ" -> R.drawable.img_big_yagi
        // 中型
        "犬" -> R.drawable.img_medium_inu
        "猫" -> R.drawable.img_medium_neko
        "キツネ" -> R.drawable.img_medium_kitsune
        "タヌキ" -> R.drawable.img_medium_tanuki
        // 小型
        "ウサギ" -> R.drawable.img_small_usagi
        "デグー" -> R.drawable.img_small_degu
        "チンチラ" -> R.drawable.img_small_chinchira
        "ハムスター" -> R.drawable.img_small_hamster
        "ハリネズミ" -> R.drawable.img_small_harinezumi
        "マウス・ラット" -> R.drawable.img_small_nezumi
        "モルモット" -> R.drawable.img_small_marmot
        "リス" -> R.drawable.img_small_risu
        "モモンガ" -> R.drawable.img_small_momonga
        // 鳥類
        "インコ類" -> R.drawable.img_bird_inko
        "フィンチ類" -> R.drawable.img_bird_finchi
        "猛禽類" -> R.drawable.img_bird_moukinrui
        // その他
        "爬虫類" -> R.drawable.img_etc_hachurui
        "魚類" -> R.drawable.img_etc_gyorui
        "昆虫" -> R.drawable.img_etc_konchu

        else -> R.drawable.nav_ic_diary
    }
}
