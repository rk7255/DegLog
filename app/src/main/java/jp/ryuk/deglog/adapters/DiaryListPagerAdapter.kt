package jp.ryuk.deglog.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import jp.ryuk.deglog.ui.diarylist.ListKey
import jp.ryuk.deglog.ui.diarylist.lists.LengthFragment
import jp.ryuk.deglog.ui.diarylist.lists.WeightFragment


const val WEIGHT_PAGE_INDEX = ListKey.FROM_WEIGHT
const val LENGTH_PAGE_INDEX = ListKey.FROM_LENGTH

class DiaryListPagerAdapter(fragment: Fragment, selectedName: String) : FragmentStateAdapter(fragment) {

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        WEIGHT_PAGE_INDEX to { WeightFragment(selectedName) },
        LENGTH_PAGE_INDEX to { LengthFragment(selectedName) }
    )
    override fun getItemCount(): Int = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }

}