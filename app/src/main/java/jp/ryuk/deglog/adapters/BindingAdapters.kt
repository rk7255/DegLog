package jp.ryuk.deglog.adapters

import android.text.BoringLayout
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import jp.ryuk.deglog.utilities.convertLongToDateStringInTime
import jp.ryuk.deglog.utilities.convertLongToDateStringOutYear
import jp.ryuk.deglog.utilities.convertUnit

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("hasAlert")
fun bindHasAlert(view: View, hasAlert: Boolean) {

}


@BindingAdapter("number", "suffix")
fun TextView.setDetailList(number: Float?, suffix: String) {
    text = if (number == null) "" else convertUnit(number, suffix, true)
}

// 日付
@BindingAdapter("diaryDateFormatted")
fun TextView.setDiaryDateFormatted(item: Long?){
    item?.let {
        text = convertLongToDateStringInTime(item)
    }
}

// 日付
@BindingAdapter("weightDateFormatted")
fun TextView.setWeightDateFormatted(item: Long?){
    item?.let {
        text = convertLongToDateStringOutYear(item)
    }
}


// メモ
@BindingAdapter("diaryMemoFormatted")
fun TextView.setDiaryMemoFormatted(memo: String?) {
    memo?.let {
        text = memo
    }
}

// メモ
@BindingAdapter("detailMemoFormatted")
fun TextView.setDetailMemoFormatted(memo: String?) {
    text = if (memo.isNullOrEmpty()) {
        "メモがありません"
    } else {
        memo
    }
}

