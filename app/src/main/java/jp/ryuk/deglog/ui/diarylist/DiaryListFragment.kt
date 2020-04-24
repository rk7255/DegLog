package jp.ryuk.deglog.ui.diarylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import jp.ryuk.deglog.R
import jp.ryuk.deglog.adapters.DiaryListPagerAdapter
import jp.ryuk.deglog.adapters.LENGTH_PAGE_INDEX
import jp.ryuk.deglog.adapters.WEIGHT_PAGE_INDEX
import jp.ryuk.deglog.databinding.FragmentDiaryListBinding


class DiaryListFragment : Fragment() {

    private lateinit var binding: FragmentDiaryListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_diary_list, container, false)

        val arguments = DiaryListFragmentArgs.fromBundle(arguments!!)

        var fromKey = arguments.fromKey
        when (fromKey) {
            ListKey.FROM_DETAIL_WEIGHT -> {
                Snackbar.make(binding.root, "削除しました", Snackbar.LENGTH_LONG).show()
                fromKey = ListKey.FROM_WEIGHT
            }
            ListKey.FROM_DETAIL_LENGTH -> {
                Snackbar.make(binding.root, "削除しました", Snackbar.LENGTH_LONG).show()
                fromKey = ListKey.FROM_LENGTH
            }
        }

        val tabLayout = binding.diaryListTab
        val viewPager = binding.diaryListViewPager

        viewPager.adapter = DiaryListPagerAdapter(this, arguments.selectedName)
        viewPager.setCurrentItem(fromKey, false)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()

        return binding.root
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            WEIGHT_PAGE_INDEX -> getString(R.string.title_weight)
            LENGTH_PAGE_INDEX -> getString(R.string.title_length)
            else -> null
        }
    }
}
