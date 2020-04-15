package jp.ryuk.deglog.ui.newdiary

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import jp.ryuk.deglog.R
import jp.ryuk.deglog.data.DiaryRepository
import jp.ryuk.deglog.data.ProfileRepository
import jp.ryuk.deglog.databinding.FragmentNewDiaryBinding
import kotlinx.android.synthetic.main.fragment_new_diary.*
import androidx.core.widget.doOnTextChanged as doOnTextChanged1

class NewDiaryFragment : Fragment() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentNewDiaryBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_new_diary, container, false)

        val application = requireNotNull(this.activity).application
        val dataSourceDiary = DiaryRepository.getInstance(application).diaryDao
        val dataSourceProfile = ProfileRepository.getInstance(application).profileDao
        val viewModelFactory = NewDiaryViewModelFactory(dataSourceDiary, dataSourceProfile)
        val newDiaryViewModel =
            ViewModelProvider(this, viewModelFactory).get(NewDiaryViewModel::class.java)
        binding.newDiaryViewModel = newDiaryViewModel
        binding.lifecycleOwner = this

        newDiaryViewModel.navigateToDiary.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                this.findNavController().navigate(
                    NewDiaryFragmentDirections.actionNewDiaryFragmentToDiaryFragment())
                newDiaryViewModel.doneNavigateToDiary()
            }
        })

        newDiaryViewModel.submitError.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                binding.newDiaryEditName.error = getString(R.string.name_empty_error_string)
            } else {
                binding.newDiaryEditName.error = null
            }
        })

        val items = listOf("Natsu", "Coco", "Taro", "Kojiro")
        val adapter = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, items)
        (binding.newDiaryEditName.editText as? AutoCompleteTextView)?.setAdapter(adapter)


        return binding.root
    }


}
