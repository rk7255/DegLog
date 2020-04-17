package jp.ryuk.deglog.ui.newdiary

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import jp.ryuk.deglog.R
import jp.ryuk.deglog.data.DiaryRepository
import jp.ryuk.deglog.data.ProfileRepository
import jp.ryuk.deglog.databinding.FragmentNewDiaryBinding

class NewDiaryFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentNewDiaryBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_new_diary, container, false)

        val newDiaryViewModel = createViewModel()
        binding.newDiaryViewModel = newDiaryViewModel
        binding.lifecycleOwner = this

        setObserve(newDiaryViewModel, binding)

        val items = listOf("Natsu", "Coco", "Taro", "Kojiro")
        val adapter = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, items)
        (binding.newDiaryEditName.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        return binding.root
    }

    private fun createViewModel(): NewDiaryViewModel {
        val application = requireNotNull(this.activity).application
        val dataSourceDiary = DiaryRepository.getInstance(application).diaryDao
        val dataSourceProfile = ProfileRepository.getInstance(application).profileDao
        val viewModelFactory = NewDiaryViewModelFactory(dataSourceDiary, dataSourceProfile)
        return ViewModelProvider(this, viewModelFactory).get(NewDiaryViewModel::class.java)
    }

    private fun setObserve(
        newDiaryViewModel: NewDiaryViewModel,
        binding: FragmentNewDiaryBinding
    ) {
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
    }
}
