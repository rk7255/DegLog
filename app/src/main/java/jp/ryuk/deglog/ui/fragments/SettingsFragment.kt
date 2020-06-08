package jp.ryuk.deglog.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import jp.ryuk.deglog.databinding.FragmentSettingsBinding
import jp.ryuk.deglog.ui.data.DialogBuilder
import jp.ryuk.deglog.ui.viewmodels.SettingsViewModel
import jp.ryuk.deglog.utilities.*

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingsViewModel by viewModels {
        InjectorUtil.provideSettingsViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        loadSharedPreferences()

        with(viewModel) {
            with(binding) {
                stUnitWeightContainer.setOnClickListener {
                    val dialog = DialogBuilder.selectUnitWeightDialogBuilder(requireContext()) {
                        unitWeight.value = it
                    }
                    dialog.show()
                }

                stUnitLengthContainer.setOnClickListener {
                    val dialog = DialogBuilder.selectUnitLengthDialogBuilder(requireContext()) {
                        unitLength.value = it
                    }
                    dialog.show()
                }

                stButtonBack.setOnClickListener {
                    pop()
                }

                stButtonSave.setOnClickListener {
                    saveSharedPreferences()
                    Utils.showSnackbar(requireView().rootView, "保存しました")
                }
            }
        }


        return binding.root
    }

    private fun pop() {
        this.findNavController().navigate(SettingsFragmentDirections.actionPop())
    }

    private fun saveSharedPreferences() {
        val sharedPreferences =
            requireContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString(KEY_UNIT_WEIGHT, viewModel.unitWeight.value ?: "g")
        editor.putString(KEY_UNIT_LENGTH, viewModel.unitLength.value ?: "mm")


        editor.apply()
    }

    private fun loadSharedPreferences() {
        val sharedPreferences =
            requireContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)

        val w = sharedPreferences.getString(KEY_UNIT_WEIGHT, "g") ?: "g"
        viewModel.unitWeight.value = w
        Log.d(TAG, "loadSharedPreferences: ${viewModel.unitWeight.value}")

        val l = sharedPreferences.getString(KEY_UNIT_LENGTH, "mm") ?: "mm"
        viewModel.unitLength.value = l

    }
}