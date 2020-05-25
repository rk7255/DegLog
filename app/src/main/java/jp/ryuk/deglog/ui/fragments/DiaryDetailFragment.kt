package jp.ryuk.deglog.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import jp.ryuk.deglog.R
import jp.ryuk.deglog.databinding.FragmentDiaryDetailBinding
import jp.ryuk.deglog.ui.data.DialogBuilder
import jp.ryuk.deglog.ui.data.FlickListener
import jp.ryuk.deglog.ui.viewmodels.DiaryDetailViewModel
import jp.ryuk.deglog.utilities.InjectorUtil
import jp.ryuk.deglog.utilities.NavMode

class DiaryDetailFragment : Fragment() {

    private lateinit var binding: FragmentDiaryDetailBinding
    private val args: DiaryDetailFragmentArgs by lazy {
        DiaryDetailFragmentArgs.fromBundle(requireArguments())
    }
    private val viewModel: DiaryDetailViewModel by viewModels {
        InjectorUtil.provideDiaryDetailViewModelFactory(requireContext(), args.id, args.name)
    }

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
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        with(binding) {
            appBarDiaryDetail.title = args.name + getString(R.string.title_diary_detail_at_name)

            detailDateImageNext.setOnClickListener { moveDiary("d", "next") }
            detailDateImageBackView.setOnClickListener { moveDiary("d", "back") }
            detailWeightImageNextView.setOnClickListener { moveDiary("w", "next") }
            detailWeightImageBackView.setOnClickListener { moveDiary("w", "back") }
            detailLengthImageNextView.setOnClickListener { moveDiary("l", "next") }
            detailLengthImageBackView.setOnClickListener { moveDiary("l", "back") }

            detailDateContainer.setOnTouchListener(FlickListener(flickListenerForDate))
            detailWeightContainer.setOnTouchListener(FlickListener(flickListenerForWeight))
            detailLengthContainer.setOnTouchListener(FlickListener(flickListenerForLength))
        }

        with(viewModel) {
            allDiary.observe(viewLifecycleOwner) {
                initDiary()
            }

            animTrigger.observe(viewLifecycleOwner) {
                if (it != null) showAnimation(it)
            }
        }

        return binding.root
    }

    private fun showAnimation(moveTo: String) {
        val anim = when (moveTo) {
            "next" -> AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right)
            "back" -> AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_left)
            else -> return
        }
        with (binding) {
            detailWeightLatest.startAnimation(anim)
            detailLengthLatest.startAnimation(anim)
        }
    }

    private fun moveDiary(which: String, moveTo: String) {
        viewModel.setDiary(which, moveTo)
    }

    private val flickListenerForDate = object : FlickListener.Listener {
        override fun onFlickToLeft() { moveDiary("d", "next") }
        override fun onFlickToRight() { moveDiary("d", "back") }
    }

    private val flickListenerForWeight = object : FlickListener.Listener {
        override fun onFlickToLeft() { moveDiary("w", "next") }
        override fun onFlickToRight() { moveDiary("w", "back") }
    }

    private val flickListenerForLength = object : FlickListener.Listener {
        override fun onFlickToLeft() { moveDiary("l", "next") }
        override fun onFlickToRight() { moveDiary("l", "back") }
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu_detail, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar_delete -> {
                val dialog =
                    DialogBuilder.createDeleteDiaryDialog(requireContext()) { deleteDiaryCallback() }
                dialog.show()
            }
            R.id.toolbar_edit -> navigateToNewDiary(viewModel.id, args.name)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteDiaryCallback() {
        viewModel.deleteDiary()
        navigatePop()
        Snackbar.make(
            requireView().rootView,
            getString(R.string.dialog_delete_success),
            Snackbar.LENGTH_SHORT
        )
            .setAnchorView(R.id.bottom_navigation_bar)
            .show()
    }

    private fun navigatePop() {
        this.findNavController().navigate(
            DiaryDetailFragmentDirections.actionPop()
        )
    }

    private fun navigateToNewDiary(id: Long, name: String) {
        this.findNavController().navigate(
            DiaryDetailFragmentDirections.toNewDiaryFragment(NavMode.EDIT, id, name)
        )
    }
}



