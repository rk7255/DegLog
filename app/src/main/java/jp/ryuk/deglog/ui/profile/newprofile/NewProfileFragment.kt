package jp.ryuk.deglog.ui.profile.newprofile

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import jp.ryuk.deglog.R
import jp.ryuk.deglog.databinding.FragmentNewProfileBinding
import jp.ryuk.deglog.utilities.*
import java.lang.StringBuilder
import java.util.*


class NewProfileFragment : Fragment() {

    private lateinit var binding: FragmentNewProfileBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_new_profile, container, false)

        binding.newProfileContainer.setOnTouchListener { v, event ->
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            binding.newProfileContainer.requestFocus()
            v?.onTouchEvent(event) ?: true
        }


        val types = arrayOf("小型", "中型", "大型")
        val typesBig = resources.getStringArray(R.array.types_big)
        val typesMedium = resources.getStringArray(R.array.types_medium)
        val typesSmall = resources.getStringArray(R.array.types_small)

        val dialogType = MaterialAlertDialogBuilder(context)
            .setTitle("種類")
            .setItems(types) { _, size ->
                val dialogType = MaterialAlertDialogBuilder(context)
                when (size) {
                    0 -> {
                        dialogType
                            .setTitle("小型")
                            .setItems(typesSmall) { _, type ->
                            binding.newProfileEditTypeText.setText(typesSmall[type])
                        }.show()
                    }
                    1 -> {
                        dialogType
                            .setTitle("中型")
                            .setItems(typesMedium) { _, type ->
                            binding.newProfileEditTypeText.setText(typesMedium[type])
                        }.show()
                    }
                    2 -> {
                        dialogType
                            .setTitle("大型")
                            .setItems(typesBig) { _, type ->
                            binding.newProfileEditTypeText.setText(typesBig[type])
                        }.show()
                    }
                }
            }

        binding.newProfileEditType.setEndIconOnClickListener{ dialogType.show() }

        // Birthday
        binding.newProfileEditBirthdayText.setOnClickListener {
            val today = Calendar.getInstance()
            val selected = Calendar.getInstance()
            MaterialDatePicker.Builder.datePicker()
                .setSelection(today.timeInMillis)
                .setTitleText("日付の選択")
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .build()
                .apply {
                    addOnPositiveButtonClickListener { time ->
                        selected.set(time.getYear(), time.getMonth() - 1, time.getDayOfMonth())
                        binding.newProfileEditBirthdayText.setText(convertLongToDateString(selected.timeInMillis))
                    }
                }.show(parentFragmentManager,  "Tag")
        }

        // Unit
        val weightUnit = resources.getStringArray(R.array.weight_unit)
        val lengthUnit = resources.getStringArray(R.array.length_unit)

        val dialogWeightUnit = MaterialAlertDialogBuilder(context)
            .setTitle("体重の単位")
            .setItems(weightUnit) { _, unit ->
                when (unit) {
                    0 -> binding.newProfileEditWeightUnitText.setText(weightUnit[0])
                    1 -> binding.newProfileEditWeightUnitText.setText(weightUnit[1])
                    2 -> binding.newProfileEditWeightUnitText.setText(weightUnit[2])
                }
            }
        val dialogLengthUnit = MaterialAlertDialogBuilder(context)
            .setTitle("体重の単位")
            .setItems(lengthUnit) { _, unit ->
                when (unit) {
                    0 -> binding.newProfileEditLengthUnitText.setText(lengthUnit[0])
                    1 -> binding.newProfileEditLengthUnitText.setText(lengthUnit[1])
                    2 -> binding.newProfileEditLengthUnitText.setText(lengthUnit[2])
                }
            }

        binding.newProfileEditWeightUnit.setEndIconOnClickListener { dialogWeightUnit.show() }
        binding.newProfileEditLengthUnit.setEndIconOnClickListener { dialogLengthUnit.show() }


        // button
        binding.newProfileSubmitButton.setOnClickListener {
            val str = StringBuilder()
            str.append("名前: ${binding.newProfileEditNameText.text}\n")
            when (binding.newProfileToggleGroup.checkedButtonId) {
                R.id.gender_male -> str.append("性別: オス\n")
                R.id.gender_female -> str.append("性別: メス\n")
                R.id.gender_unknown -> str.append("性別: 不明\n")
            }
            str.append("種類: ${binding.newProfileEditTypeText.text}\n")
            str.append("誕生日: ${binding.newProfileEditBirthdayText.text}\n")
            str.append("体重の単位: ${binding.newProfileEditWeightUnitText.text}\n")
            str.append("体長の単位: ${binding.newProfileEditLengthUnitText.text}")

            Toast.makeText(context, str, Toast.LENGTH_LONG).show()
        }

        return binding.root
    }

}
