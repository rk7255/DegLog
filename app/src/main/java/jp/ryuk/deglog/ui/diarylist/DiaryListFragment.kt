package jp.ryuk.deglog.ui.diarylist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import jp.ryuk.deglog.R
import jp.ryuk.deglog.adapters.DiaryListPagerAdapter
import jp.ryuk.deglog.adapters.LENGTH_PAGE_INDEX
import jp.ryuk.deglog.adapters.MEMO_PAGE_INDEX
import jp.ryuk.deglog.adapters.WEIGHT_PAGE_INDEX
import jp.ryuk.deglog.data.DiaryRepository
import jp.ryuk.deglog.data.ProfileRepository
import jp.ryuk.deglog.databinding.FragmentDiaryListBinding
import jp.ryuk.deglog.utilities.InjectorUtil


class DiaryListFragment : Fragment() {

    private lateinit var binding: FragmentDiaryListBinding
    private lateinit var viewModel: DiaryListViewModel
    private lateinit var args: DiaryListFragmentArgs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_diary_list, container, false)

        args = DiaryListFragmentArgs.fromBundle(arguments!!)
        viewModel = createViewModel(requireContext(), args.name)
        binding.appBarDiaryList.title = args.name + getString(R.string.title_diary_detail_at_name)

        val tabLayout = binding.diaryListTab
        val viewPager = binding.diaryListViewPager

        viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.position.value = position
            }
        })

        viewModel.diaries.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                viewModel.diariesLoaded.value = true
                viewModel.sectionLoaded()
            }
        })

        viewModel.profile.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                viewModel.profileLoaded.value = true
                viewModel.sectionLoaded()
            }
        })

        viewModel.allLoaded.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                viewPager.adapter = DiaryListPagerAdapter(
                    this,
                    args.name,
                    viewModel.diaries.value!!,
                    viewModel.weightUnit.value!!,
                    viewModel.lengthUnit.value!!
                )
                if (viewModel.position.value == null) {
                    viewPager.setCurrentItem(args.from, false)
                } else {
                    viewPager.setCurrentItem(viewModel.position.value!!, false)
                }
                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = getTabTitle(position)
                }.attach()
            }
        })

        return binding.root
    }

    private fun makeSnackBar(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG).show()
    }

    private fun getTabTitle(position: Int): String? = when (position) {
            WEIGHT_PAGE_INDEX -> getString(R.string.title_weight)
            LENGTH_PAGE_INDEX -> getString(R.string.title_length)
            MEMO_PAGE_INDEX -> getString(R.string.title_memo)
            else -> null
        }

    private fun createViewModel(context: Context, name: String): DiaryListViewModel {
        val viewModelFactory = InjectorUtil.provideDiaryListViewModelFactory(context, name)
        return ViewModelProvider(this, viewModelFactory).get(DiaryListViewModel::class.java)
    }
}
