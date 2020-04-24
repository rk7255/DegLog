package jp.ryuk.deglog.ui.newdiary

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
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
import jp.ryuk.deglog.ui.diarylist.ListKey

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

        newDiaryViewModel = createViewModel()
        binding.viewModel = newDiaryViewModel
        binding.lifecycleOwner = this

        newDiaryViewModel.initialized.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                binding.newDiaryEditWeightText.setText(newDiaryViewModel.weight)
                binding.newDiaryEditLengthText.setText(newDiaryViewModel.length)
                binding.newDiaryEditMemoText.setText(newDiaryViewModel.memo)


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

        binding.newDiaryContainer.setOnTouchListener { v, event ->
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            v?.onTouchEvent(event) ?: true
        }

        return binding.root
    }

    private fun createViewModel(): NewDiaryViewModel {
        val application = requireNotNull(this.activity).application
        val dataSourceDiary = DiaryRepository.getInstance(application).diaryDao
        val dataSourceProfile = ProfileRepository.getInstance(application).profileDao
        val viewModelFactory =
            NewDiaryViewModelFactory(args.diaryKey, args.selectedName, dataSourceDiary, dataSourceProfile)
        return ViewModelProvider(this, viewModelFactory).get(NewDiaryViewModel::class.java)
    }
}
