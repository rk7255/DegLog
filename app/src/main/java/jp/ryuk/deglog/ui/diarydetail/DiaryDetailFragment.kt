package jp.ryuk.deglog.ui.diarydetail

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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
import jp.ryuk.deglog.data.ProfileRepository
import jp.ryuk.deglog.databinding.FragmentDiaryDetailBinding
import jp.ryuk.deglog.ui.diarylist.DiaryListFragment
import jp.ryuk.deglog.ui.diarylist.ListKey
import jp.ryuk.deglog.utilities.InjectorUtil
import jp.ryuk.deglog.utilities.convertLongToDateStringOutYear
import kotlinx.android.synthetic.main.detail_view_pager.*

class DiaryDetailFragment : Fragment() {

    private lateinit var binding: FragmentDiaryDetailBinding
    private lateinit var viewModel: DiaryDetailViewModel
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

        viewModel = createViewModel(requireContext(), args.id, args.name)
        binding.lifecycleOwner = this
        binding.appBarDiaryDetail.title = args.name + getString(R.string.title_diary_detail_at_name)

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

        val viewPager = binding.diaryListViewPager
        tabLayout = binding.diaryDetailTab
        val adapter = DiaryDetailPagerAdapter()
        viewPager.adapter = adapter

        viewModel.details.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        viewModel.diaryPosition.observe(viewLifecycleOwner, Observer {
            viewPager.setCurrentItem(it, false)
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = getTitle(position)
            }.attach()
        })

    return binding.root
}

    private fun getTitle(position: Int): String? {
        return viewModel.details.value?.get(position)?.date?.let {
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
        when (item.itemId) {
            R.id.toolbar_delete -> {
                MaterialAlertDialogBuilder(context)
                    .setTitle(getString(R.string.delete_title))
                    .setMessage(getString(R.string.delete_message))
                    .setNeutralButton(getString(R.string.dialog_cancel)) { _, _ -> }
                    .setPositiveButton(getString(R.string.dialog_yes)) { _, _ ->
                        viewModel.deleteDiary(getTabPos())
                        this.findNavController().navigate(
                            DiaryDetailFragmentDirections
                                .actionDiaryDetailFragmentPop())
                        Snackbar.make(view!!.rootView, getString(R.string.delete_success), Snackbar.LENGTH_SHORT)
                            .setAnchorView(R.id.bottom_navigation_bar)
                            .show()
                    }
                    .show()
            }
            R.id.toolbar_edit -> {
                this.findNavController().navigate(
                    DiaryDetailFragmentDirections.actionDiaryDetailFragmentToNewDiaryFragment(
                        "edit", viewModel.editDiary(getTabPos()), args.name)
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
