package jp.ryuk.deglog.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.switchmaterial.SwitchMaterial
import jp.ryuk.deglog.adapters.DiaryListAdapter
import jp.ryuk.deglog.adapters.DiaryListListener
import jp.ryuk.deglog.adapters.DiaryStickerDecoration
import jp.ryuk.deglog.databinding.FragmentDiaryListBinding
import jp.ryuk.deglog.ui.viewmodels.DiaryListViewModel
import jp.ryuk.deglog.utilities.InjectorUtil
import jp.ryuk.deglog.utilities.NavMode
import jp.ryuk.deglog.utilities.Utils

class DiaryListFragment : Fragment() {

    private lateinit var binding: FragmentDiaryListBinding
    private val args: DiaryListFragmentArgs by lazy {
        DiaryListFragmentArgs.fromBundle(requireArguments())
    }
    private val viewModel: DiaryListViewModel by viewModels {
        InjectorUtil.provideDiaryListViewModelFactory(requireContext(), args.name)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiaryListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        with(binding) {
            detailListCheckWeight.isChecked = args.from == NavMode.FROM_WEIGHT
            detailListCheckLength.isChecked = args.from == NavMode.FROM_LENGTH
            detailListCheckNote.isChecked = false
        }

        val recyclerView = binding.detailListRecyclerView
        val adapter = DiaryListAdapter(DiaryListListener { onClick(it) })
        recyclerView.adapter = adapter

        val switches =
            Utils.findViewsWithType(binding.diaryListSwitches, SwitchMaterial::class.java)

        initSwitches(switches) { viewModel.applyFilter(it) }

        with(viewModel) {
            allDiary.observe(viewLifecycleOwner) {
                val checked = getCheckedSwitches(switches)
                applyFilter(checked)
            }

            diaries.observe(viewLifecycleOwner) {
                if (it.isNullOrEmpty()) {
                    binding.hasData = false
                } else {
                    if (recyclerView.itemDecorationCount > 0) recyclerView.removeItemDecorationAt(0)
                    binding.hasData = true
                    adapter.submitList(it)
                    val decoration = DiaryStickerDecoration(requireActivity(), it)
                    recyclerView.addItemDecoration(decoration)
                }
            }
        }

        return binding.root
    }

    private fun onClick(id: Long) {
        this.findNavController().navigate(
            DiaryListFragmentDirections.toDiaryDetailFragment(
                id,
                args.name
            )
        )
    }

    private fun initSwitches(switches: List<SwitchMaterial>, func: (List<String>) -> Unit) {
        switches.forEach { switch ->
            switch.setOnCheckedChangeListener { _, _ ->
                val checked = getCheckedSwitches(switches)
                func(checked)
            }
        }
    }

    private fun getCheckedSwitches(switches: List<SwitchMaterial>): List<String> {
        return mutableListOf<String>().apply {
            switches.forEach { if (it.isChecked) add(it.text.toString()) }
        }
    }
}