package jp.ryuk.deglog.adapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import jp.ryuk.deglog.utilities.convertLongToDateStringInTime
import jp.ryuk.deglog.utilities.convertLongToDateStringOutYear
import jp.ryuk.deglog.utilities.convertUnit

/**
 * DiaryDetailList
 */
@BindingAdapter("number", "suffix")
fun TextView.setDetailList(number: Float, suffix: String) {
    text = convertUnit(number, suffix)
}


// 名前
//@BindingAdapter("diaryName")
//fun TextView.setDiaryName(item: String?) {
//    item?.let {
//        text = item
//    }
//}

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


/*
// 体重
@BindingAdapter("diaryWeightFormatted")
fun TextView.setDiaryWeightFormatted(weight: Float?) {
    weight?.let {
        text = convertWeight(weight)
    }
}
// 体長
@BindingAdapter("diaryLengthFormatted")
fun TextView.setDiaryLengthFormatted(length: Float?) {
    length?.let {
        text = convertLength(length)
    }
}
*/

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

