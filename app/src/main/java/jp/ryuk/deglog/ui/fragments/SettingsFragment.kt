package jp.ryuk.deglog.ui.fragments

import android.content.Context
import android.os.Bundle
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

                stFreeDb1Text.text =
                    if (db1Enabled) "有効: $db1Name [ $db1Unit ]"
                    else "無効"

                stFreeDb2Text.text =
                    if (db2Enabled) "有効: $db2Name [ $db2Unit ]"
                    else "無効"


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

                stDeathDayContainer.setOnClickListener {
                    val sw = isEnableDeathDay.value ?: false
                    isEnableDeathDay.value = !sw
                }

                stFreeDb1Container.setOnClickListener {
                    val dialog =
                        DialogBuilder.settingFreeDbDialog(
                            requireContext(), db1Enabled, db1Name, db1Unit
                        ) { isEnable, itemName, unitName ->
                            db1Enabled = isEnable
                            db1Name = itemName
                            db1Unit = unitName

                            stFreeDb1Text.text =
                                if (isEnable) "有効: $itemName [ $unitName ]"
                                else "無効"
                        }
                    dialog.show()
                }

                stFreeDb2Container.setOnClickListener {
                    val dialog =
                        DialogBuilder.settingFreeDbDialog(
                            requireContext(), db2Enabled, db2Name, db2Unit
                        ) { isEnable, itemName, unitName ->
                            db2Enabled = isEnable
                            db2Name = itemName
                            db2Unit = unitName

                            stFreeDb2Text.text =
                                if (isEnable) "有効: $itemName [ $unitName ]"
                                else "無効"
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
        editor.putBoolean(KEY_DEATH_DAY, viewModel.isEnableDeathDay.value ?: false)

        editor.putBoolean(KEY_DB1_ENABLED, viewModel.db1Enabled)
        editor.putString(KEY_DB1_NAME, viewModel.db1Name)
        editor.putString(KEY_DB1_UNIT, viewModel.db1Unit)

        editor.putBoolean(KEY_DB2_ENABLED, viewModel.db2Enabled)
        editor.putString(KEY_DB2_NAME, viewModel.db2Name)
        editor.putString(KEY_DB2_UNIT, viewModel.db2Unit)

        editor.apply()
    }

    private fun loadSharedPreferences() {
        val sharedPreferences =
            requireContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)

        viewModel.apply {
            unitWeight.value = sharedPreferences.getString(KEY_UNIT_WEIGHT, "g") ?: "g"
            unitLength.value = sharedPreferences.getString(KEY_UNIT_LENGTH, "mm") ?: "mm"
            isEnableDeathDay.value = sharedPreferences.getBoolean(KEY_DEATH_DAY, false)

            db1Enabled = sharedPreferences.getBoolean(KEY_DB1_ENABLED, false)
            db1Name = sharedPreferences.getString(KEY_DB1_NAME, null)
            db1Unit = sharedPreferences.getString(KEY_DB1_UNIT, null)

            db2Enabled = sharedPreferences.getBoolean(KEY_DB2_ENABLED, false)
            db2Name = sharedPreferences.getString(KEY_DB2_NAME, null)
            db2Unit = sharedPreferences.getString(KEY_DB2_UNIT, null)
        }
    }
}