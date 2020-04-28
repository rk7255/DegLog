package jp.ryuk.deglog.ui.profile.newprofile

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import jp.ryuk.deglog.R
import jp.ryuk.deglog.data.ProfileRepository
import jp.ryuk.deglog.databinding.FragmentNewProfileBinding
import jp.ryuk.deglog.utilities.*
import java.util.*


class NewProfileFragment : Fragment() {

    private lateinit var binding: FragmentNewProfileBinding
    private lateinit var newProfileViewModel: NewProfileViewModel

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_new_profile, container, false
        )
        newProfileViewModel = createViewModel()

        binding.lifecycleOwner = this
        binding.viewModel = newProfileViewModel

        // キーボードの非表示
        binding.newProfileContainer.setOnTouchListener { view, event -> hideKeyboard(activity!!, view, event) }

        // 性別
        binding.newProfileToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                val select = group.findViewById<MaterialButton>(checkedId).text.toString()
                newProfileViewModel.gender = select
            }
        }

        // 種類
        val dialogType = typeDialogBuilder(binding.newProfileEditTypeText)
        binding.newProfileEditType.setEndIconOnClickListener { dialogType.show() }

        // 誕生日
        val etBirthday = binding.newProfileEditBirthdayText
        etBirthday.setOnClickListener {
            showDatePicker(etBirthday, parentFragmentManager)
        }

        // 単位
        val dialogWeightUnit = unitDialogBuilder(
            binding.newProfileEditWeightUnitText,
            resources.getStringArray(R.array.weight_unit))
        val dialogLengthUnit = unitDialogBuilder(
            binding.newProfileEditLengthUnitText,
            resources.getStringArray(R.array.length_unit))
        binding.newProfileEditWeightUnit.setEndIconOnClickListener { dialogWeightUnit.show() }
        binding.newProfileEditLengthUnit.setEndIconOnClickListener { dialogLengthUnit.show() }

        // エラー
        newProfileViewModel.submitError.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                binding.newProfileEditName.error = getString(R.string.name_empty_error_string)
            } else {
                binding.newProfileEditName.error = null
            }
        })

        // ナビゲーション
        newProfileViewModel.navigateToProfiles.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                this.findNavController().navigate(
                    NewProfileFragmentDirections.actionNewProfileFragmentToProfilesFragment()
                )
                newProfileViewModel.doneNavigateToProfiles()
            }
        })

        newProfileViewModel.backToProfiles.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                this.findNavController().navigate(
                    NewProfileFragmentDirections.actionNewProfileFragmentPop()
                )
                newProfileViewModel.doneBackToProfiles()
            }
        })

        return binding.root
    }

    private fun unitDialogBuilder(editText: EditText, units: Array<String>): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(context)
            .setTitle(getString(R.string.choice_unit))
            .setItems(units) { _, unit ->
                when (unit) {
                    0 -> editText.setText(units[0])
                    1 -> editText.setText(units[1])
                    2 -> editText.setText(units[2])
                }
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
                    selected.set(date.getYear(), date.getMonth() - 1, date.getDayOfMonth())
                    editText.setText(convertLongToDateString(selected.timeInMillis))
                    newProfileViewModel.birthday = selected.timeInMillis
                }
            }.show(fm, getString(R.string.dialog_tag))
    }


    private fun typeDialogBuilder(editText: EditText): MaterialAlertDialogBuilder {
        val types = resources.getStringArray(R.array.types)
        val typesBig = resources.getStringArray(R.array.types_big)
        val typesMedium = resources.getStringArray(R.array.types_medium)
        val typesSmall = resources.getStringArray(R.array.types_small)

        return MaterialAlertDialogBuilder(context)
            .setTitle(getString(R.string.choice_type))
            .setItems(types) { _, size ->
                when (size) {
                    0 -> {
                        MaterialAlertDialogBuilder(context)
                            .setTitle(R.string.choice_type_small)
                            .setItems(typesSmall) { _, type -> editText.setText(typesSmall[type]) }
                            .show()
                    }
                    1 -> {
                        MaterialAlertDialogBuilder(context)
                            .setTitle(R.string.choice_type_medium)
                            .setItems(typesMedium) { _, type -> editText.setText(typesMedium[type]) }
                            .show()
                    }
                    2 -> {
                        MaterialAlertDialogBuilder(context)
                            .setTitle(R.string.choice_type_big)
                            .setItems(typesBig) { _, type -> editText.setText(typesBig[type]) }
                            .show()
                    }
                }
            }
    }

    private fun createViewModel(): NewProfileViewModel {
        val application = requireNotNull(this.activity).application
        val dataSourceProfile = ProfileRepository.getInstance(application).profileDao
        val viewModelFactory =
            NewProfileViewModelFactory(dataSourceProfile)
        return ViewModelProvider(this, viewModelFactory).get(NewProfileViewModel::class.java)
    }

}
