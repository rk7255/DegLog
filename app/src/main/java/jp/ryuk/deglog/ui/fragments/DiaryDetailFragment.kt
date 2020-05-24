package jp.ryuk.deglog.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import jp.ryuk.deglog.R
import jp.ryuk.deglog.adapters.DiaryDetailPagerAdapter
import jp.ryuk.deglog.databinding.FragmentDiaryDetailBinding
import jp.ryuk.deglog.ui.viewmodels.DiaryDetailViewModel
import jp.ryuk.deglog.utilities.Converter
import jp.ryuk.deglog.ui.data.FlickListener
import jp.ryuk.deglog.utilities.InjectorUtil
import jp.ryuk.deglog.utilities.NavMode

class DiaryDetailFragment : Fragment() {

    private lateinit var binding: FragmentDiaryDetailBinding
    private lateinit var viewModel: DiaryDetailViewModel
    private lateinit var args: DiaryDetailFragmentArgs
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_diary_detail, container, false
        )
        (activity as AppCompatActivity).setSupportActionBar(binding.appBarDiaryDetail)
        args = DiaryDetailFragmentArgs.fromBundle(
            requireArguments()
        )

        viewModel = createViewModel(requireContext(), args.id, args.name)
        binding.viewModel = viewModel
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
        viewPager.setCurrentItem(viewModel.position, false)

        viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position != viewModel.diaryPosition.value) {
                    viewModel.diaryPosition.value = position
                }
            }
        })

        viewModel.detailsLoaded.observe(viewLifecycleOwner, Observer {
            adapter.submitList(viewModel.details.value)

            val pos = viewModel.diaryPosition.value ?: 1
            viewPager.setCurrentItem(pos, false)
            viewModel.position = pos

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = getTitle(position)
            }.attach()
        })

        viewModel.diaryPosition.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                viewPager.setCurrentItem(it, true)
                viewModel.getDetail()
                viewModel.position = it
            }
        })

        viewModel.memo.observe(viewLifecycleOwner, Observer {
            binding.hasMemo = !it.isNullOrEmpty()
        })

        binding.detailDateContainer.setOnTouchListener(
            FlickListener(
                flickListenerForDate
            )
        )
        binding.detailWeightContainer.setOnTouchListener(
            FlickListener(
                flickListenerForWeight
            )
        )
        binding.detailLengthContainer.setOnTouchListener(
            FlickListener(
                flickListenerForLength
            )
        )

        return binding.root
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
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.dialog_delete_title))
                    .setMessage(getString(R.string.dialog_delete_message))
                    .setNeutralButton(getString(R.string.dialog_cancel)) { _, _ -> }
                    .setPositiveButton(getString(R.string.dialog_yes)) { _, _ ->
                        viewModel.deleteDiary(getTabPos())
                        this.findNavController().navigate(
                            DiaryDetailFragmentDirections.actionDiaryDetailFragmentPop()
                        )
                        Snackbar.make(
                            requireView().rootView,
                            getString(R.string.dialog_delete_success),
                            Snackbar.LENGTH_SHORT
                        )
                            .setAnchorView(R.id.bottom_navigation_bar)
                            .show()
                    }
                    .show()
            }
            R.id.toolbar_edit -> {
                this.findNavController().navigate(
                    DiaryDetailFragmentDirections.actionDiaryDetailFragmentToNewDiaryFragment(
                        NavMode.EDIT, viewModel.editDiary(getTabPos()), args.name
                    )
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val flickListenerForDate = object : FlickListener.Listener {
        override fun onButtonPressed() {}
        override fun onButtonReleased() {}
        override fun onFlickToLeft() { viewModel.onDateNext() }
        override fun onFlickToRight() { viewModel.onDateBack() }
    }

    private val flickListenerForWeight = object : FlickListener.Listener {
        override fun onButtonPressed() {}
        override fun onButtonReleased() {}
        override fun onFlickToLeft() { viewModel.onWeightNext() }
        override fun onFlickToRight() { viewModel.onWeightBack() }
    }

    private val flickListenerForLength = object : FlickListener.Listener {
        override fun onButtonPressed() {}
        override fun onButtonReleased() {}
        override fun onFlickToLeft() { viewModel.onLengthNext() }
        override fun onFlickToRight() { viewModel.onLengthBack() }
    }

    private fun getTitle(position: Int): String? {
        return viewModel.details.value?.get(position)?.date?.let {
            Converter.longToDateShortString(it)
        }
    }
}
