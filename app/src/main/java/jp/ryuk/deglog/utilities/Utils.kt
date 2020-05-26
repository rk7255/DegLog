package jp.ryuk.deglog.utilities

import android.app.Activity
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import jp.ryuk.deglog.R

object Utils {

    fun <T : View> findViewsWithType(root: View, type: Class<T>): List<T> {
        val views = ArrayList<T>()
        findViewsWithType(root, type, views)
        return views
    }

    private fun <T : View> findViewsWithType(view: View, type: Class<T>, views: MutableList<T>) {
        if (type.isInstance(view)) {
            type.cast(view)?.let { views.add(it) }
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                findViewsWithType(view.getChildAt(i), type, views)
            }
        }
    }

    fun hideKeyboard(activity: Activity, view: View, event: MotionEvent?): Boolean {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        view.requestFocus()
        return view.onTouchEvent(event)
    }

    fun getColorMap(context: Context): Map<String, Int> {
        val colorMap = mutableMapOf<String, Int>()
        colorMap["red"] = ContextCompat.getColor(context, R.color.pink)
        colorMap["blue"] = ContextCompat.getColor(context, R.color.blue)
        colorMap["green"] = ContextCompat.getColor(context, R.color.green)
        colorMap["yellow"] = ContextCompat.getColor(context, R.color.yellow)
        colorMap["orange"] = ContextCompat.getColor(context, R.color.orange)
        colorMap["purple"] = ContextCompat.getColor(context, R.color.purple)
        colorMap["brown"] = ContextCompat.getColor(context, R.color.primaryColor)
        colorMap["gray"] = ContextCompat.getColor(context, R.color.gray)
        colorMap["none"] = ContextCompat.getColor(context, R.color.primaryTextColor)
        return colorMap
    }

    fun colorSelector(color: Int?): String {
        return when (color) {
            0 -> "red"
            1 -> "blue"
            2 -> "green"
            3 -> "yellow"
            4 -> "orange"
            5 -> "purple"
            6 -> "brown"
            7 -> "gray"
            else -> "none"
        }
    }

    fun iconSelector(context: Context, type: String?): Int {
        return when (type) {
            null -> R.drawable.ic_pets
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

            else -> R.drawable.ic_pets
        }
    }
}