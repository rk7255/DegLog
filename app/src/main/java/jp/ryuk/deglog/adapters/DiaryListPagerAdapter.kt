package jp.ryuk.deglog.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.ui.diarylist.ListKey
import jp.ryuk.deglog.ui.diarylist.lists.LengthFragment
import jp.ryuk.deglog.ui.diarylist.lists.MemoFragment
import jp.ryuk.deglog.ui.diarylist.lists.WeightFragment

const val WEIGHT_PAGE_INDEX = ListKey.FROM_WEIGHT
const val LENGTH_PAGE_INDEX = ListKey.FROM_LENGTH
const val MEMO_PAGE_INDEX = ListKey.FROM_MEMO

class DiaryListPagerAdapter(
    fragment: Fragment,
    selectedName:String,
    list: List<Diary>,
    suffixWeight: String,
    suffixLength: String
) :
    FragmentStateAdapter(fragment) {

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        WEIGHT_PAGE_INDEX to {
            WeightFragment(
                selectedName,
                list.filter { it.weight != null },
                suffixWeight)
        },
        LENGTH_PAGE_INDEX to {
            LengthFragment(
                selectedName,
                list.filter { it.length != null },
                suffixLength)
        },
        MEMO_PAGE_INDEX to {
            MemoFragment(
                selectedName,
                list.filter { it.memo != null })
        }
    )

    override fun getItemCount(): Int = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }

}