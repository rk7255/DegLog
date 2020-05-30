package jp.ryuk.deglog.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import jp.ryuk.deglog.R
import jp.ryuk.deglog.databinding.FragmentNewProfileBinding
import jp.ryuk.deglog.ui.data.DialogBuilder
import jp.ryuk.deglog.ui.viewmodels.NewProfileViewModel
import jp.ryuk.deglog.utilities.BitmapUtils
import jp.ryuk.deglog.utilities.InjectorUtil
import jp.ryuk.deglog.utilities.MessageCode
import jp.ryuk.deglog.utilities.Utils
import java.util.*


class NewProfileFragment : Fragment() {

    private lateinit var binding: FragmentNewProfileBinding
    private val args: NewProfileFragmentArgs by lazy {
        NewProfileFragmentArgs.fromBundle(requireArguments())
    }
    private val viewModel: NewProfileViewModel by viewModels {
        InjectorUtil.provideNewProfileViewModelFactory(requireContext(), args.name)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel


        val allTypes = resources.getStringArray(R.array.all_types)
        val adapter = ArrayAdapter(requireContext(), R.layout.item_name_list, allTypes)

        with(binding) {

            npContainer.setOnTouchListener { v, event ->
                Utils.hideKeyboard(requireActivity(), v, event)
            }

            npTypeLayout.setEndIconOnClickListener {
                val dialog = DialogBuilder.typeDialogBuilder(requireContext(), npTypeText)
                dialog.show()
            }
            npTypeText.setAdapter(adapter)

            npBirthdayText.setOnClickListener {
                val date = Calendar.getInstance().timeInMillis
                val dialog = DialogBuilder.datePickerDialogBuilder(
                    requireContext(),
                    date
                ) { year, month, dayOfMonth ->
                    setDate(year, month, dayOfMonth)
                }
                dialog.show()
            }

            npGenderGroup.setOnCheckedChangeListener { group, checkedId ->
                val checked = group.findViewById<RadioButton>(checkedId)
                val text = checked.text.toString()
                setGender(text)
            }

            npIcon.setOnClickListener {
                getUriFromGallery { uri ->
                    val bitmap = BitmapUtils.createBitmap(requireContext(), uri)
                    npIcon.setImageBitmap(bitmap)
                    val jsonString = BitmapUtils.convertBitmapToJsonString(bitmap)
                    setJsonString(jsonString)
                }
            }

            npNameText.addTextChangedListener { npNameLayout.error = null }

            npButtonCancel.setOnClickListener { pop() }
            npButtonSubmit.setOnClickListener { submit() }
        }


        with(viewModel) {

            profile.observe(viewLifecycleOwner) {
                if (it != null) {
                    setProfile(it)

                    with(binding) {
                        when (it.gender) {
                            getString(R.string.gender_male) -> npGenderMale.isChecked = true
                            getString(R.string.gender_female) -> npGenderFemale.isChecked = true
                            else -> npGenderUnknown.isChecked = true
                        }

                        it.icon?.let { jsonString ->
                            val bitmap = BitmapUtils.convertJsonToBitmap(jsonString)
                            npIcon.setImageBitmap(bitmap)
                        }

                    }
                }
            }

            nameList.observe(viewLifecycleOwner) {}

            selectedColor.observe(viewLifecycleOwner) {
                with(binding) {
                    npLabelSelect.visibility = View.VISIBLE
                    val colorMap = Utils.getColorMap(requireContext())
                    val colorKey = Utils.colorSelector(it)
                    val colorLabel = colorMap[colorKey]
                    npLabelSelect.setTextColor(colorLabel!!)
                    npLabelSelect.text =
                        if (colorKey == "none") "-"
                        else "●"
                }
            }

            submitError.observe(viewLifecycleOwner) {
                when (it) {
                    MessageCode.NAME_EMPTY ->
                        binding.npNameLayout.error = getString(R.string.enter_name)
                    MessageCode.NAME_REGISTERED ->
                        showSnackbar(getString(R.string.registered_name))
                }
            }

            submit.observe(viewLifecycleOwner) {
                if (it != null) {
                    pop()
                }
            }
        }


        return binding.root
    }

    private fun showSnackbar(text: String) {
        Snackbar.make(requireView().rootView, text, Snackbar.LENGTH_LONG)
            .setAnchorView(R.id.bottom_navigation_bar)
            .show()
    }

    private fun setJsonString(jsonString: String) {
        viewModel.setJsonString(jsonString)
    }

    private fun setGender(gender: String) {
        viewModel.setGender(gender)
    }

    private fun setDate(year: Int, month: Int, day: Int) {
        viewModel.setDate(year, month, day)
    }

    // ギャラリーからURI取得
    private fun getUriFromGallery(unit: (Uri) -> Unit) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK && it.data != null && it.data?.data != null) {
                val takeFlags: Int = intent.flags and
                        (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                requireContext().contentResolver.takePersistableUriPermission(it.data!!.data!!, takeFlags)

                val uri = it.data!!.data!!
                unit(uri)
            }
        }
        launcher.launch(intent)
    }

    private fun pop() {
        this.findNavController().navigate(
            NewProfileFragmentDirections.actionNewProfileFragmentPop()
        )
    }

    private fun submit() {
        viewModel.submit()
    }


}


//
//        binding.apply {
//            newProfileTitle.text =
//                when (args.mode) {
//                    NavMode.NEW -> getString(R.string.title_new_profile)
//                    else -> getString(R.string.title_edit_profile)
//                }
//
//            newProfileContainer.setOnTouchListener { view, event ->
//                Utils.hideKeyboard(requireActivity(), view, event)
//                binding.newProfileEditName.error = null
//                false
//            }
//
//            newProfileEditNameText.setOnKeyListener { _, _, _ ->
//                binding.newProfileEditName.error = null
//                false
//            }
//
//            newProfileEditType.setEndIconOnClickListener {
//                typeDialogBuilder(requireContext(), binding.newProfileEditTypeText).show()
//            }
//
//        }
//
//        binding.newProfileToggleGroupGender.addOnButtonCheckedListener { group, checkedId, isChecked ->
//            if (isChecked)
//                viewModel.gender.value =
//                    group.findViewById<MaterialButton>(checkedId).text.toString()
//        }
//
//        viewModel.apply {
//            profile.observe(viewLifecycleOwner, Observer {
//                if (it != null) {
//                    viewModel.setValues()
//                    when (viewModel.gender.value) {
//                        "オス" -> binding.genderMale.isChecked = true
//                        "メス" -> binding.genderFemale.isChecked = true
//                        else -> binding.genderUnknown.isChecked = true
//                    }
//                }
//            })
//
//            submit.observe(viewLifecycleOwner, Observer {
//                if (it == true) {
//                    if (viewModel.isNameChanged && args.mode == NavMode.DASHBOARD) {
//                        backToDashboard()
//                    } else {
//                        pop()
//                    }
//                }
//            })
//
//            submitError.observe(viewLifecycleOwner, Observer {
//                if (it == true) {
//                    binding.newProfileEditName.error = getString(R.string.name_empty_error_string)
//                } else {
//                    binding.newProfileEditName.error = null
//                }
//            })
//
//            onDateCLick.observe(viewLifecycleOwner, Observer {
//                if (it == true)
//                    showDatePicker(binding.newProfileEditBirthdayText, parentFragmentManager)
//            })
//
//            selectedColor.observe(viewLifecycleOwner, Observer {
//                val colorMap = Utils.getColorMap(requireContext())
//                val colorKey = Utils.colorSelector(it)
//                val colorLabel = colorMap[colorKey]
//                binding.viewColor.setBackgroundColor(colorLabel!!)
//            })
//
//            confirmUpdate.observe(viewLifecycleOwner, Observer {
//                if (it != null) {
//                    confirmDialogBuilder(requireContext(), it).show()
//                }
//            })
//        }
//
//
//
//        return binding.root
//    }
//
//    private fun pop() {
//        this.findNavController().navigate(
//            NewProfileFragmentDirections.actionNewProfileFragmentPop()
//        )
//    }
//
//    private fun backToDashboard() {
//        this.findNavController().navigate(
//            NewProfileFragmentDirections.actionNewProfileFragmentToDiaryFragment()
//        )
//    }
//
//    private fun confirmDialogBuilder(context: Context, profile: Profile): MaterialAlertDialogBuilder {
//        return MaterialAlertDialogBuilder(context)
//            .setTitle("名前を変更します")
//            .setMessage("過去に記録した日誌データにも反映されます\n名前が登録済みの場合は上書きされます")
//            .setNeutralButton(getString(R.string.dialog_cancel), null)
//            .setPositiveButton(getString(R.string.dialog_ok)) { _, _ ->
//                viewModel.updateAndChange(profile)
//            }
//    }
//
//    private fun showDatePicker(editText: EditText, fm: FragmentManager) {
//        val today = Calendar.getInstance()
//        val selected = Calendar.getInstance()
//
//        MaterialDatePicker.Builder.datePicker()
//            .setSelection(today.timeInMillis)
//            .setTitleText(getString(R.string.choice_date))
//            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
//            .build()
//            .apply {
//                addOnPositiveButtonClickListener { date ->
//                    selected.set(date.getYear(), date.getMonth() - 1, date.getDayOfMonth(), 0, 0, 0)
//                    editText.setText(Converter.longToDateString(selected.timeInMillis))
//                    viewModel.doneOnDateClick(selected.timeInMillis)
//                }
//            }.show(fm, getString(R.string.dialog_tag))
//    }
//
//
//    private fun typeDialogBuilder(
//        context: Context,
//        editText: EditText
//    ): MaterialAlertDialogBuilder {
//        val types = resources.getStringArray(R.array.types)
//        val typesBig = resources.getStringArray(R.array.types_big)
//        val typesMedium = resources.getStringArray(R.array.types_medium)
//        val typesSmall = resources.getStringArray(R.array.types_small)
//        val typesBird = resources.getStringArray(R.array.types_bird)
//        val typesEtc = resources.getStringArray(R.array.types_etc)
//
//        return MaterialAlertDialogBuilder(context)
//            .setTitle(getString(R.string.choice_type))
//            .setItems(types) { _, size ->
//                when (size) {
//                    0 -> {
//                        MaterialAlertDialogBuilder(context)
//                            .setTitle(R.string.type_small)
//                            .setItems(typesSmall) { _, type -> editText.setText(typesSmall[type]) }
//                            .show()
//                    }
//                    1 -> {
//                        MaterialAlertDialogBuilder(context)
//                            .setTitle(R.string.type_medium)
//                            .setItems(typesMedium) { _, type -> editText.setText(typesMedium[type]) }
//                            .show()
//                    }
//                    2 -> {
//                        MaterialAlertDialogBuilder(context)
//                            .setTitle(R.string.type_big)
//                            .setItems(typesBig) { _, type -> editText.setText(typesBig[type]) }
//                            .show()
//                    }
//                    3 -> {
//                        MaterialAlertDialogBuilder(context)
//                            .setTitle(R.string.type_bird)
//                            .setItems(typesBird) { _, type -> editText.setText(typesBird[type]) }
//                            .show()
//                    }
//                    4 -> {
//                        MaterialAlertDialogBuilder(context)
//                            .setTitle(R.string.type_etc)
//                            .setItems(typesEtc) { _, type -> editText.setText(typesEtc[type]) }
//                            .show()
//                    }
//                }
//            }
//    }
//
//    private fun createViewModel(context: Context, name: String): NewProfileViewModel {
//        val viewModelFactory = InjectorUtil.provideNewProfileViewModelFactory(context, name)
//        return ViewModelProvider(this, viewModelFactory).get(NewProfileViewModel::class.java)
//    }
//
//}
