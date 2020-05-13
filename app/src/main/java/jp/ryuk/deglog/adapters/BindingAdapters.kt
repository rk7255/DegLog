package jp.ryuk.deglog.adapters

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import jp.ryuk.deglog.utilities.convertUnit

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("number", "suffix")
fun TextView.setDetailList(number: Float?, suffix: String) {
    text = if (number == null) "" else convertUnit(number, suffix, true)
}