package jp.ryuk.deglog.ui.diarydetail

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

import jp.ryuk.deglog.R
import jp.ryuk.deglog.adapters.DiaryDetailPagerAdapter
import jp.ryuk.deglog.data.DiaryRepository
import jp.ryuk.deglog.databinding.FragmentDiaryDetailBinding
import jp.ryuk.deglog.ui.diarylist.ListKey
import jp.ryuk.deglog.ui.diarylist.lists.WeightViewModel
import jp.ryuk.deglog.ui.diarylist.lists.WeightViewModelFactory
import jp.ryuk.deglog.utilities.convertLongToDateStringOutYear


class DiaryDetailFragment : Fragment() {

    private lateinit var binding: FragmentDiaryDetailBinding
    private lateinit var diaryDetailViewModel: DiaryDetailViewModel
    private lateinit var args: DiaryDetailFragmentArgs
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_diary_detail, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.appBarDiaryDetail)
        args = DiaryDetailFragmentArgs.fromBundle(arguments!!)

        diaryDetailViewModel = createViewModel(args.diaryKey, args.selectedName)
        binding.lifecycleOwner = this


        val viewPager = binding.diaryListViewPager
        tabLayout = binding.diaryDetailTab
        val adapter = DiaryDetailPagerAdapter()
        viewPager.adapter = adapter

        diaryDetailViewModel.diaries.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        diaryDetailViewModel.diaryPosition.observe(viewLifecycleOwner, Observer {
            viewPager.setCurrentItem(it, false)
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = getTitle(position)
            }.attach()
        })

    return binding.root
}

    private fun getTitle(position: Int): String? {
        return diaryDetailViewModel.diaries.value?.get(position)?.date?.let {
            convertLongToDateStringOutYear(it)
        }
    }

    private fun createViewModel(diaryKey: Long, selectedName: String): DiaryDetailViewModel {
        val application = requireNotNull(this.activity).application
        val dataSourceDiary = DiaryRepository.getInstance(application).diaryDao
        val viewModelFactory =
            DiaryDetailViewModelFactory(diaryKey, selectedName, dataSourceDiary)
        return ViewModelProvider(this, viewModelFactory).get(DiaryDetailViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu_detail, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    private fun getTabPos(): Int {
        return tabLayout.selectedTabPosition
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar_delete -> {
                MaterialAlertDialogBuilder(context)
                    .setTitle("削除しますか？")
                    .setMessage("データは永久に削除されます")
                    .setNeutralButton("キャンセル") { _, _ -> }
                    .setPositiveButton("はい") { _, _ ->
                        val deleteName = diaryDetailViewModel.deleteDiary(getTabPos())
                        val fromKey = when (args.fromKey) {
                            ListKey.FROM_WEIGHT -> ListKey.FROM_DETAIL_WEIGHT
                            ListKey.FROM_LENGTH -> ListKey.FROM_DETAIL_LENGTH
                            else -> ListKey.FROM_UNKNOWN
                        }

                        this.findNavController().navigate(
                            DiaryDetailFragmentDirections
                                .actionDiaryDetailFragmentToDiaryListFragment(fromKey, deleteName)
                        )
                    }
                    .show()
            }
            R.id.toolbar_save -> {
                MaterialAlertDialogBuilder(context)
                    .setTitle("上書き保存しますか？")
                    .setNeutralButton("キャンセル") { _, _ -> }
                    .setPositiveButton("はい") { _, _ ->
                        diaryDetailViewModel.deleteDiary(getTabPos())
                        Snackbar.make(binding.root, "上書きしました", Snackbar.LENGTH_LONG).show()
                    }
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
