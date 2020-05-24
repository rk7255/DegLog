package jp.ryuk.deglog.ui.fragments

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import jp.ryuk.deglog.R
import jp.ryuk.deglog.databinding.FragmentNewDiaryBinding
import jp.ryuk.deglog.ui.viewmodels.NewDiaryViewModel
import jp.ryuk.deglog.utilities.*
import java.util.*

class NewDiaryFragment : Fragment() {
    private lateinit var binding: FragmentNewDiaryBinding
    private lateinit var viewModel: NewDiaryViewModel
    private lateinit var args: NewDiaryFragmentArgs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_diary, container, false)

        args = NewDiaryFragmentArgs.fromBundle(
            requireArguments()
        )

        when (args.mode) {
            NavMode.EDIT -> {
                binding.newDiaryTitle.text = getString(R.string.title_edit_title)
                binding.newDiaryEditName.isEnabled = false
            }
            else -> {
                binding.newDiaryTitle.text = getString(R.string.title_new_diary)
                binding.newDiaryEditName.isEnabled = true
            }
        }

        viewModel = createViewModel(requireContext(), args.id, args.name)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.newDiaryContainer.setOnTouchListener { view, event ->
            Utils.hideKeyboard(requireActivity(), view, event)
            binding.newDiaryEditName.error = null
            false
        }

        viewModel.names.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                val names = it.toTypedArray()
                binding.newDiaryEditName.setEndIconOnClickListener {
                    dialogNameSelectBuilder(
                        requireContext(),
                        binding.newDiaryEditNameText,
                        names
                    ).show()
                }
            }
        })

        viewModel.diary.observe(viewLifecycleOwner, Observer {
            if (it != null) viewModel.setValues()
        })

        viewModel.submit.observe(viewLifecycleOwner, Observer {
            if (it == true) pop()
        })

        viewModel.submitError.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                binding.newDiaryEditName.error = getString(R.string.name_empty_error_string)
            } else {
                binding.newDiaryEditName.error = null
            }
        })

        binding.newDiaryEditNameText.setOnKeyListener { _, _, _ ->
            binding.newDiaryEditName.error = null
            false
        }

        viewModel.onDateCLick.observe(viewLifecycleOwner, Observer {
            if (it == true) showDialogCalendar(requireContext(), parentFragmentManager)
        })

        return binding.root
    }

    private fun pop() {
        this.findNavController().navigate(
            NewDiaryFragmentDirections.actionNewDiaryFragmentPop()
        )
    }

    private fun dialogNameSelectBuilder(
        context: Context,
        editText: EditText,
        names: Array<String>
    ): AlertDialog {
        return MaterialAlertDialogBuilder(context)
            .setTitle(getString(R.string.choice_pet))
            .setItems(names) { _, i ->
                editText.setText(names[i])
            }
            .create()
    }

    private fun showDialogCalendar(context: Context, fm: FragmentManager) {
        val today = Calendar.getInstance()

        MaterialDatePicker.Builder.datePicker()
            .setSelection(today.timeInMillis)
            .setTitleText(getString(R.string.choice_date))
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .build()
            .apply {
                addOnPositiveButtonClickListener {
                    val selected = Calendar.getInstance()
                    TimePickerDialog(
                        context,
                        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                            selected.set(
                                it.getYear(),
                                it.getMonth() - 1,
                                it.getDayOfMonth(),
                                hourOfDay,
                                minute
                            )
                            viewModel.doneOnDateClick(selected.timeInMillis)
                        },
                        today.get(Calendar.HOUR_OF_DAY),
                        today.get(Calendar.MINUTE),
                        true
                    ).show()
                }
            }
            .show(fm, "dialogDateAndTimePickerTag")
    }

    private fun createViewModel(context: Context, id: Long, name: String): NewDiaryViewModel {
        val viewModelFactory = InjectorUtil.provideNewDiaryViewModelFactory(context, id, name)
        return ViewModelProvider(this, viewModelFactory).get(NewDiaryViewModel::class.java)
    }


}
