package jp.ryuk.deglog.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import jp.ryuk.deglog.R
import jp.ryuk.deglog.database.Profile
import jp.ryuk.deglog.databinding.FragmentNewProfileBinding
import jp.ryuk.deglog.ui.viewmodels.NewProfileViewModel
import jp.ryuk.deglog.utilities.*
import java.util.*


class NewProfileFragment : Fragment() {

    private lateinit var binding: FragmentNewProfileBinding
    private lateinit var viewModel: NewProfileViewModel
    private lateinit var args: NewProfileFragmentArgs

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewProfileBinding.inflate(inflater, container, false)
        args =
            NewProfileFragmentArgs.fromBundle(
                requireArguments()
            )
        viewModel = createViewModel(requireContext(), args.name)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.apply {
            newProfileTitle.text =
                when (args.mode) {
                    NavMode.NEW -> getString(R.string.title_new_profile)
                    else -> getString(R.string.title_edit_profile)
                }

            newProfileContainer.setOnTouchListener { view, event ->
                Utils.hideKeyboard(requireActivity(), view, event)
                binding.newProfileEditName.error = null
                false
            }

            newProfileEditNameText.setOnKeyListener { _, _, _ ->
                binding.newProfileEditName.error = null
                false
            }

            newProfileEditType.setEndIconOnClickListener {
                typeDialogBuilder(requireContext(), binding.newProfileEditTypeText).show()
            }

            newProfileEditWeightUnit.setEndIconOnClickListener {
                unitDialogBuilder(
                    requireContext(),
                    binding.newProfileEditWeightUnitText,
                    resources.getStringArray(R.array.weight_unit)
                ).show()
            }

            newProfileEditLengthUnit.setEndIconOnClickListener {
                unitDialogBuilder(
                    requireContext(),
                    binding.newProfileEditLengthUnitText,
                    resources.getStringArray(R.array.length_unit)
                ).show()
            }

        }

        binding.newProfileToggleGroupGender.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked)
                viewModel.gender.value =
                    group.findViewById<MaterialButton>(checkedId).text.toString()
        }

        viewModel.apply {
            profile.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    viewModel.setValues()
                    when (viewModel.gender.value) {
                        "オス" -> binding.genderMale.isChecked = true
                        "メス" -> binding.genderFemale.isChecked = true
                        else -> binding.genderUnknown.isChecked = true
                    }
                }
            })

            submit.observe(viewLifecycleOwner, Observer {
                if (it == true) {
                    if (viewModel.isNameChanged && args.mode == NavMode.DASHBOARD) {
                        backToDashboard()
                    } else {
                        pop()
                    }
                }
            })

            submitError.observe(viewLifecycleOwner, Observer {
                if (it == true) {
                    binding.newProfileEditName.error = getString(R.string.name_empty_error_string)
                } else {
                    binding.newProfileEditName.error = null
                }
            })

            onDateCLick.observe(viewLifecycleOwner, Observer {
                if (it == true)
                    showDatePicker(binding.newProfileEditBirthdayText, parentFragmentManager)
            })

            selectedColor.observe(viewLifecycleOwner, Observer {
                val colorMap = Utils.getColorMap(requireContext())
                val colorKey = Utils.colorSelector(it)
                val colorLabel = colorMap[colorKey]
                binding.viewColor.setBackgroundColor(colorLabel!!)
            })

            confirmUpdate.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    confirmDialogBuilder(requireContext(), it).show()
                }
            })
        }



        return binding.root
    }

    private fun pop() {
        this.findNavController().navigate(
            NewProfileFragmentDirections.actionNewProfileFragmentPop()
        )
    }

    private fun backToDashboard() {
        this.findNavController().navigate(
            NewProfileFragmentDirections.actionNewProfileFragmentToDiaryFragment()
        )
    }

    private fun confirmDialogBuilder(context: Context, profile: Profile): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(context)
            .setTitle("名前を変更します")
            .setMessage("過去に記録した日誌データにも反映されます\n名前が登録済みの場合は上書きされます")
            .setNeutralButton(getString(R.string.dialog_cancel), null)
            .setPositiveButton(getString(R.string.dialog_ok)) { _, _ ->
                viewModel.updateAndChange(profile)
            }
    }

    private fun unitDialogBuilder(
        context: Context,
        editText: EditText,
        units: Array<String>
    ): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(context)
            .setTitle(getString(R.string.choice_unit))
            .setItems(units) { _, i ->
                editText.setText(units[i])
            }
    }

    private fun showDatePicker(editText: EditText, fm: FragmentManager) {
        val today = Calendar.getInstance()
        val selected = Calendar.getInstance()

        MaterialDatePicker.Builder.datePicker()
            .setSelection(today.timeInMillis)
            .setTitleText(getString(R.string.choice_date))
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .build()
            .apply {
                addOnPositiveButtonClickListener { date ->
                    selected.set(date.getYear(), date.getMonth() - 1, date.getDayOfMonth(), 0, 0, 0)
                    editText.setText(Converter.longToDateString(selected.timeInMillis))
                    viewModel.doneOnDateClick(selected.timeInMillis)
                }
            }.show(fm, getString(R.string.dialog_tag))
    }


    private fun typeDialogBuilder(
        context: Context,
        editText: EditText
    ): MaterialAlertDialogBuilder {
        val types = resources.getStringArray(R.array.types)
        val typesBig = resources.getStringArray(R.array.types_big)
        val typesMedium = resources.getStringArray(R.array.types_medium)
        val typesSmall = resources.getStringArray(R.array.types_small)
        val typesBird = resources.getStringArray(R.array.types_bird)
        val typesEtc = resources.getStringArray(R.array.types_etc)

        return MaterialAlertDialogBuilder(context)
            .setTitle(getString(R.string.choice_type))
            .setItems(types) { _, size ->
                when (size) {
                    0 -> {
                        MaterialAlertDialogBuilder(context)
                            .setTitle(R.string.type_small)
                            .setItems(typesSmall) { _, type -> editText.setText(typesSmall[type]) }
                            .show()
                    }
                    1 -> {
                        MaterialAlertDialogBuilder(context)
                            .setTitle(R.string.type_medium)
                            .setItems(typesMedium) { _, type -> editText.setText(typesMedium[type]) }
                            .show()
                    }
                    2 -> {
                        MaterialAlertDialogBuilder(context)
                            .setTitle(R.string.type_big)
                            .setItems(typesBig) { _, type -> editText.setText(typesBig[type]) }
                            .show()
                    }
                    3 -> {
                        MaterialAlertDialogBuilder(context)
                            .setTitle(R.string.type_bird)
                            .setItems(typesBird) { _, type -> editText.setText(typesBird[type]) }
                            .show()
                    }
                    4 -> {
                        MaterialAlertDialogBuilder(context)
                            .setTitle(R.string.type_etc)
                            .setItems(typesEtc) { _, type -> editText.setText(typesEtc[type]) }
                            .show()
                    }
                }
            }
    }

    private fun createViewModel(context: Context, name: String): NewProfileViewModel {
        val viewModelFactory = InjectorUtil.provideNewProfileViewModelFactory(context, name)
        return ViewModelProvider(this, viewModelFactory).get(NewProfileViewModel::class.java)
    }

}
