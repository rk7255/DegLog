package jp.ryuk.deglog.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import jp.ryuk.deglog.R
import jp.ryuk.deglog.databinding.FragmentNewDiaryBinding
import jp.ryuk.deglog.ui.data.DialogBuilder
import jp.ryuk.deglog.ui.viewmodels.NewDiaryViewModel
import jp.ryuk.deglog.utilities.InjectorUtil
import jp.ryuk.deglog.utilities.MessageCode
import jp.ryuk.deglog.utilities.NavMode
import jp.ryuk.deglog.utilities.Utils
import java.util.*

class NewDiaryFragment : Fragment() {
    private lateinit var binding: FragmentNewDiaryBinding
    private val args: NewDiaryFragmentArgs by lazy {
        NewDiaryFragmentArgs.fromBundle(requireArguments())
    }
    private val viewModel: NewDiaryViewModel by viewModels {
        InjectorUtil.provideNewDiaryViewModelFactory(requireContext(), args.id, args.name)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewDiaryBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        var date = Calendar.getInstance().timeInMillis

        with(binding) {

            ndTitle.text = when (args.mode) {
                NavMode.NEW -> getString(R.string.title_new_diary)
                NavMode.EDIT -> getString(R.string.title_edit_diary)
                else -> getString(R.string.title_new_diary)
            }

            ndContainer.setOnTouchListener { v, event ->
                Utils.hideKeyboard(requireActivity(), v, event)
            }

            ndDate.setOnClickListener {
                val dialog =
                    DialogBuilder.datePickerDialogBuilder(
                        requireContext(),
                        date
                    ) { year, month, dayOfMonth ->
                        setDate(year, month, dayOfMonth)
                        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
                        anim.duration = 600
                        ndDate.startAnimation(anim)
                    }
                dialog.show()
            }

            ndTime.setOnClickListener {
                val dialog =
                    DialogBuilder.timePickerDialogBuilder(requireContext(), date) { hour, minute ->
                        setTime(hour, minute)
                        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
                        anim.duration = 600
                        ndTime.startAnimation(anim)
                    }
                dialog.show()
            }

            ndNameText.addTextChangedListener { ndNameLayout.error = null }
            ndWeightText.addTextChangedListener { numberError(binding, false) }
            ndLengthText.addTextChangedListener { numberError(binding, false) }
            ndNoteText.addTextChangedListener { numberError(binding, false) }

            ndButtonBack.setOnClickListener { pop() }
            ndButtonSubmit.setOnClickListener { submit() }
        }

        with(viewModel) {

            diary.observe(viewLifecycleOwner) {
                if (it != null) {
                    setDiary(it)
                    date = it.date
                    binding.ndNameLayout.apply {
                        isEnabled = false
                        helperText = getString(R.string.name_do_not_edit)
                    }
                    binding.ndNameText.apply {
                        setTextColor(requireContext().getColor(R.color.grayThin))
                        setBackgroundColor(requireContext().getColor(R.color.bgEditTextThin))
                    }
                }
            }

            nameList.observe(viewLifecycleOwner) {
                val adapter = ArrayAdapter(requireContext(), R.layout.item_name_list, it)
                binding.ndNameText.setAdapter(adapter)
            }

            submitError.observe(viewLifecycleOwner) {
                when (it) {
                    MessageCode.NAME_EMPTY -> binding.ndNameLayout.error =
                        getString(R.string.enter_name)
                    MessageCode.NUMBER_EMPTY -> {
                        numberError(binding, true)
                        Utils.showSnackbar(requireView().rootView, getString(R.string.enter_number_up_one))
                    }
                }
            }

            submit.observe(viewLifecycleOwner) {
                val msg = when (it) {
                    MessageCode.NAME_UNREGISTERED -> getString(R.string.registered_diary_and_profile)
                    MessageCode.EDIT -> getString(R.string.edited_diary)
                    else -> getString(R.string.registered_diary)
                }
                Utils.showSnackbar(requireView().rootView, msg)
                pop()
            }
        }

        return binding.root
    }

    private fun numberError(binding: FragmentNewDiaryBinding, enable: Boolean) {
        with(binding) {
            if (enable) {
                ndWeightLayout.error = getString(R.string.asterisk)
                ndLengthLayout.error = getString(R.string.asterisk)
                ndNoteLayout.error = getString(R.string.asterisk)
            } else {
                ndWeightLayout.error = null
                ndLengthLayout.error = null
                ndNoteLayout.error = null
            }
        }
    }

    private fun setDate(year: Int, month: Int, day: Int) {
        viewModel.setDate(year, month, day)
    }

    private fun setTime(hour: Int, minute: Int) {
        viewModel.setTime(hour, minute)
    }

    private fun submit() {
        viewModel.submit()
    }

    private fun pop() {
        this.findNavController().navigate(
            NewDiaryFragmentDirections.actionNewDiaryFragmentPop()
        )
    }

}