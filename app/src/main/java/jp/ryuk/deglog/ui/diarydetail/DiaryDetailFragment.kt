package jp.ryuk.deglog.ui.diarydetail

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import jp.ryuk.deglog.R
import jp.ryuk.deglog.adapters.DiaryDetailPagerAdapter
import jp.ryuk.deglog.data.DiaryRepository
import jp.ryuk.deglog.data.ProfileRepository
import jp.ryuk.deglog.databinding.FragmentDiaryDetailBinding
import jp.ryuk.deglog.ui.diarylist.ListKey
import jp.ryuk.deglog.utilities.InjectorUtil
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

        diaryDetailViewModel = createViewModel(requireContext(), args.diaryKey, args.selectedName)
        binding.lifecycleOwner = this
        binding.appBarDiaryDetail.title = args.selectedName + getString(R.string.title_diary_detail_at_name)

        val viewPager = binding.diaryListViewPager
        tabLayout = binding.diaryDetailTab
        val adapter = DiaryDetailPagerAdapter()
        viewPager.adapter = adapter

        diaryDetailViewModel.details.observe(viewLifecycleOwner, Observer {
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
        return diaryDetailViewModel.details.value?.get(position)?.date?.let {
            convertLongToDateStringOutYear(it)
        }
    }

    private fun createViewModel(context: Context, id: Long, name: String): DiaryDetailViewModel {
        val viewModelFactory = InjectorUtil.provideDiaryDetailViewModelFactory(context, id, name)
        return ViewModelProvider(this, viewModelFactory).get(DiaryDetailViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu_detail, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    private fun getTabPos(): Int = tabLayout.selectedTabPosition

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val fromKey = when (args.fromKey) {
            ListKey.FROM_WEIGHT -> ListKey.FROM_DETAIL_WEIGHT
            ListKey.FROM_LENGTH -> ListKey.FROM_DETAIL_LENGTH
            else -> ListKey.FROM_UNKNOWN
        }

        when (item.itemId) {
            R.id.toolbar_delete -> {
                MaterialAlertDialogBuilder(context)
                    .setTitle(getString(R.string.delete_title))
                    .setMessage(getString(R.string.delete_message))
                    .setNeutralButton(getString(R.string.dialog_cancel)) { _, _ -> }
                    .setPositiveButton(getString(R.string.dialog_yes)) { _, _ ->
                        val deleteName = diaryDetailViewModel.deleteDiary(getTabPos())
                        this.findNavController().navigate(
                            DiaryDetailFragmentDirections
                                .actionDiaryDetailFragmentToDiaryListFragment(fromKey, deleteName))
                    }
                    .show()
            }
            R.id.toolbar_edit -> {
                this.findNavController().navigate(
                    DiaryDetailFragmentDirections.actionDiaryDetailFragmentToNewDiaryFragment(
                        fromKey, diaryDetailViewModel.editDiary(getTabPos()), args.selectedName)
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
