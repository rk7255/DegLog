package jp.ryuk.deglog.ui.newdiary

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import jp.ryuk.deglog.R
import jp.ryuk.deglog.data.DiaryRepository
import jp.ryuk.deglog.data.ProfileRepository
import jp.ryuk.deglog.databinding.FragmentNewDiaryBinding

class NewDiaryFragment : Fragment() {

    private lateinit var binding: FragmentNewDiaryBinding
    private lateinit var newDiaryViewModel: NewDiaryViewModel

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

        val arguments = NewDiaryFragmentArgs.fromBundle(arguments!!)

        newDiaryViewModel = createViewModel(arguments.selectedName)
        binding.newDiaryViewModel = newDiaryViewModel
        binding.lifecycleOwner = this

        setObserve(newDiaryViewModel, binding)

        newDiaryViewModel.initialized.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                val adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    newDiaryViewModel.names
                )
                (binding.newDiaryEditName.editText as? AutoCompleteTextView)?.setAdapter(adapter)
                newDiaryViewModel.doneInitialized()
            }
        })

        binding.newDiaryContainer.setOnTouchListener { v, event ->
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            v?.onTouchEvent(event) ?: true
        }

        return binding.root
    }

    private fun createViewModel(selectedName: String): NewDiaryViewModel {
        val application = requireNotNull(this.activity).application
        val dataSourceDiary = DiaryRepository.getInstance(application).diaryDao
        val dataSourceProfile = ProfileRepository.getInstance(application).profileDao
        val viewModelFactory =
            NewDiaryViewModelFactory(selectedName, dataSourceDiary, dataSourceProfile)
        return ViewModelProvider(this, viewModelFactory).get(NewDiaryViewModel::class.java)
    }

    private fun setObserve(
        newDiaryViewModel: NewDiaryViewModel,
        binding: FragmentNewDiaryBinding
    ) {
        newDiaryViewModel.navigateToDiary.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                this.findNavController().navigate(
                    NewDiaryFragmentDirections.actionNewDiaryFragmentToDiaryFragment()
                )
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
