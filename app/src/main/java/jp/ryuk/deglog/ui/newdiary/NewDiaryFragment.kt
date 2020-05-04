package jp.ryuk.deglog.ui.newdiary

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import jp.ryuk.deglog.R
import jp.ryuk.deglog.data.DiaryRepository
import jp.ryuk.deglog.data.ProfileRepository
import jp.ryuk.deglog.databinding.FragmentNewDiaryBinding
import jp.ryuk.deglog.ui.diarylist.ListKey
import jp.ryuk.deglog.utilities.*
import java.util.*

class NewDiaryFragment : Fragment() {
    private lateinit var binding: FragmentNewDiaryBinding
    private lateinit var newDiaryViewModel: NewDiaryViewModel
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

        args = NewDiaryFragmentArgs.fromBundle(arguments!!)
        Log.d("DEBUG", "${args.fromKey}")

        if (args.fromKey == ListKey.FROM_UNKNOWN) {
            binding.newDiaryTitle.text = getString(R.string.new_diary_title)
            binding.newDiaryEditName.isEnabled = true
        } else {
            binding.newDiaryTitle.text = getString(R.string.edit_diary_title)
            binding.newDiaryEditName.isEnabled = false
        }

        newDiaryViewModel = createViewModel(requireContext(), args.diaryKey, args.selectedName)
        binding.viewModel = newDiaryViewModel
        binding.lifecycleOwner = this

        newDiaryViewModel.initialized.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                binding.newDiaryEditWeightText.setText(newDiaryViewModel.weight)
                binding.newDiaryEditLengthText.setText(newDiaryViewModel.length)
                binding.newDiaryEditMemoText.setText(newDiaryViewModel.memo)
                binding.newDiaryEditDateText.setText(newDiaryViewModel.dateOfString)

                val adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    newDiaryViewModel.names
                )
                (binding.newDiaryEditName.editText as? AutoCompleteTextView)?.setAdapter(adapter)
                newDiaryViewModel.doneInitialized()
            }
        })

        newDiaryViewModel.navigateToDiary.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                this.findNavController().navigate(
                    NewDiaryFragmentDirections.actionNewDiaryFragmentToDiaryFragment()
                )
                newDiaryViewModel.doneNavigateToDiary()
            }
        })

        newDiaryViewModel.navigateToDiaryDetail.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                val fromKey = when (args.fromKey) {
                    ListKey.FROM_DETAIL_WEIGHT -> ListKey.FROM_EDIT_WEIGHT
                    ListKey.FROM_DETAIL_LENGTH -> ListKey.FROM_EDIT_LENGTH
                    else -> args.fromKey
                }
                this.findNavController().navigate(
                    NewDiaryFragmentDirections
                        .actionNewDiaryFragmentToDiaryListFragment(
                            fromKey, args.selectedName)
                )
                newDiaryViewModel.doneNavigateToDiaryDetail()
            }
        })

        newDiaryViewModel.backToDiary.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                this.findNavController().navigate(
                    NewDiaryFragmentDirections.actionNewDiaryFragmentPop()
                )
                newDiaryViewModel.doneBackToDiary()
            }
        })

        newDiaryViewModel.submitError.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                binding.newDiaryEditName.error = getString(R.string.name_empty_error_string)
            } else {
                binding.newDiaryEditName.error = null
            }
        })

        // キーボードの非表示
        binding.newDiaryContainer.setOnTouchListener { view, event -> hideKeyboard(activity!!, view, event) }

        // 日付の選択
        newDiaryViewModel.getCalendar.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                val today = Calendar.getInstance()
                val selected = Calendar.getInstance()
                showDateAndTimePicker(today, selected, parentFragmentManager)
            }
        })


        return binding.root
    }

    private fun showDateAndTimePicker(today: Calendar, selected: Calendar, fm: FragmentManager) {
        MaterialDatePicker.Builder.datePicker()
            .setSelection(today.timeInMillis)
            .setTitleText("日付の選択")
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .build()
            .apply {
                addOnPositiveButtonClickListener { date ->
                    TimePickerDialog(
                        context,
                        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                            selected.set(date.getYear(), date.getMonth()-1, date.getDayOfMonth(), hourOfDay, minute)
                            newDiaryViewModel.doneGetCalendar(selected.timeInMillis)
                        },
                        today.get(Calendar.HOUR_OF_DAY),
                        today.get(Calendar.MINUTE),
                        true
                    ).show()
                }
            }.show(fm, "Tag")
    }


    private fun createViewModel(context: Context, id: Long, name: String): NewDiaryViewModel {
        val viewModelFactory = InjectorUtil.provideNewDiaryViewModelFactory(context, id, name)
        return ViewModelProvider(this, viewModelFactory).get(NewDiaryViewModel::class.java)
    }


}
