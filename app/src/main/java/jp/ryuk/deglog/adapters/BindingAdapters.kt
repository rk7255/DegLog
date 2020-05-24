package jp.ryuk.deglog.adapters

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import jp.ryuk.deglog.utilities.Converter
import jp.ryuk.deglog.utilities.Converter.convertUnit

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean?) {
    view.visibility = if (isGone == true) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("isVisible")
fun bindIsVisible(view: View, isVisible: Boolean?) {
    view.visibility = if (isVisible == true) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}



@BindingAdapter("number", "suffix")
fun TextView.setDetailList(number: Float?, suffix: String) {
    text = if (number == null) "" else Converter.convertUnit(number, suffix, true)
}

@BindingAdapter("date")
fun TextView.bindDate(date: Long) {
    text = Converter.longToDateString(date)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("weight")
fun TextView.bindWeight(weight: Float) {
    text = "${weight.toInt()} g"
}

@SuppressLint("SetTextI18n")
@BindingAdapter("length")
fun TextView.bindLength(length: Float) {
    text = "${length.toInt()} mm"
}


@SuppressLint("SetTextI18n")
@BindingAdapter("age")
fun TextView.bindAge(age: Int) {
    text = "$age 歳"
}
