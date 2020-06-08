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
import jp.ryuk.deglog.R
import jp.ryuk.deglog.databinding.FragmentNewProfileBinding
import jp.ryuk.deglog.ui.data.DialogBuilder
import jp.ryuk.deglog.ui.viewmodels.NewProfileViewModel
import jp.ryuk.deglog.utilities.*
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

            npTitle.text = when (args.mode) {
                NavMode.NEW -> getString(R.string.title_new_profile)
                NavMode.EDIT -> getString(R.string.title_edit_profile)
                else -> getString(R.string.title_new_profile)
            }

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
                val dialog = DialogBuilder.iconSelectDialogBuilder(requireContext()) {
                    when (it) {
                        0 -> {
                            deleteIcon()
                            npIcon.setImageResource(R.drawable.ic_pets)
                        }
                        else -> {
                            getUriFromGallery { uri ->
                                val bitmap = BitmapUtils.createBitmap(requireContext(), uri)
                                npIcon.setImageBitmap(bitmap)
                                val jsonString = BitmapUtils.convertBitmapToJsonString(bitmap)
                                setJsonString(jsonString)
                            }
                        }

                    }
                }
                dialog.show()
            }

            npNameText.addTextChangedListener { npNameLayout.error = null }

            npButtonBack.setOnClickListener { pop() }
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
                        Utils.showSnackbar(requireView().rootView, getString(R.string.registered_name))
                }
            }

            submitMassage.observe(viewLifecycleOwner) {
                it?.let {  profile ->
                    val dialog = DialogBuilder.confirmChangeNameDialogBuilder(requireContext()) {
                        changeName(profile)
                    }
                    dialog.show()
                }
            }

            submit.observe(viewLifecycleOwner) {
                if (it == true) {
                    when (args.mode) {
                        NavMode.NEW -> Utils.showSnackbar(requireView().rootView, getString(R.string.registered_profile))
                        NavMode.EDIT -> Utils.showSnackbar(requireView().rootView, getString(R.string.edited_prodile))
                    }
                    pop()
                }
            }
        }


        return binding.root
    }

    private fun deleteIcon() {
        viewModel.deleteJsonString()
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