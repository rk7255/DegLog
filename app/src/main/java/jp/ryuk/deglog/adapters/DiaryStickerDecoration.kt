package jp.ryuk.deglog.adapters

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextPaint
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import jp.ryuk.deglog.R
import jp.ryuk.deglog.database.Diary
import jp.ryuk.deglog.utilities.getMonth
import kotlin.math.min

class DiaryStickerDecoration(private val context: Context, private val list: List<Diary>) :
    RecyclerView.ItemDecoration() {
    private val textPaint = TextPaint()
    private val bgdPaint = Paint()
    private val dp1 =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, context.resources.displayMetrics)

    private val sizeRect = dp1 * 42
    private val sizeText = dp1 * 18

    init {
        textPaint.apply {
            isAntiAlias = true
            color = ContextCompat.getColor(context, R.color.primaryTextColor)
            typeface = Typeface.DEFAULT_BOLD
            textSize = sizeText
        }
        bgdPaint.color = ContextCompat.getColor(context, R.color.primaryLightColor)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val pos = parent.getChildAdapterPosition(view)
        if (pos >= list.size || pos < 0) return

        if (pos == 0 || list[pos].date.getMonth() != list[pos - 1].date.getMonth()) {
            outRect.top = sizeRect.toInt()
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        for (i in 0 until parent.childCount) {
            val view = parent[i]
            val pos = parent.getChildAdapterPosition(view)
            if (pos >= list.size || pos < 0) return
            val month = "${list[pos].date.getMonth()}月"

            if (view.top > sizeRect) {
                if (pos == 0 || list[pos].date.getMonth() != list[pos - 1].date.getMonth()) {
                    c.drawRect(
                        view.left.toFloat(),
                        view.top - sizeRect,
                        view.right.toFloat(),
                        view.top.toFloat(),
                        bgdPaint
                    )
                    c.drawText(
                        month,
                        view.left + sizeRect / 2,
                        view.top - (sizeRect / 2 - sizeText / 2),
                        textPaint
                    )
                }
            } else {
                if (pos + 1 < list.size && list[pos].date.getMonth() != list[pos + 1].date.getMonth()) {
                    val stickY = min(view.bottom.toFloat(), sizeRect)
                    c.drawRect(
                        view.left.toFloat(),
                        stickY - sizeRect,
                        view.right.toFloat(),
                        stickY,
                        bgdPaint
                    )

                    c.drawText(
                        month,
                        view.left + sizeRect / 2,
                        stickY - (sizeRect / 2 - sizeText / 2),
                        textPaint
                    )

                } else {
                    c.drawRect(
                        view.left.toFloat(),
                        0f,
                        view.right.toFloat(),
                        sizeRect,
                        bgdPaint
                    )
                    c.drawText(
                        month,
                        view.left + sizeRect / 2,
                        sizeRect - (sizeRect / 2 - sizeText / 2),
                        textPaint
                    )
                }
            }
        }
    }
}