package jp.ryuk.deglog.ui.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import jp.ryuk.deglog.R
import jp.ryuk.deglog.databinding.FragmentDashboardBinding
import jp.ryuk.deglog.ui.diarylist.ListKey
import jp.ryuk.deglog.utilities.InjectorUtil
import jp.ryuk.deglog.utilities.deg


class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var viewModel: DashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.dbAppBar)

        viewModel = createViewModel(requireContext())
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        viewModel.chartWeight = binding.dbWeightChart
        viewModel.chartLength = binding.dbLengthChart

        var names = arrayOf<String>()

        viewModel.diaries.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                viewModel.diariesLoaded.value = true
                viewModel.sectionLoaded()
            }
        })

        viewModel.profiles.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                viewModel.profilesLoaded.value = true
                viewModel.sectionLoaded()
            }
        })

        viewModel.names.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                Log.d(deg, "names loaded")
                viewModel.namesLoaded.value = true
                names = it.toTypedArray()
                if (viewModel.selected.value.isNullOrEmpty()) viewModel.selected.value = names[0]
                binding.dbPersonalName.text = viewModel.selected.value
                viewModel.sectionLoaded()
            }
        })

        viewModel.call.observe(viewLifecycleOwner, Observer { call ->
            if (call != null) {
                when (call) {
                    OnCallKey.PERSONAL_ICON -> makeSnackBar("ICON")
                    OnCallKey.PERSONAL_CONTAINER -> dialogBuilder(requireContext(), names).show()
                    OnCallKey.NOTIFY_CONTAINER -> makeSnackBar("NOTIFY")
                    OnCallKey.WEIGHT_CONTAINER -> navigateToDiaryList(ListKey.FROM_WEIGHT)
                    OnCallKey.LENGTH_CONTAINER -> navigateToDiaryList(ListKey.FROM_LENGTH)
                    OnCallKey.TODO_CONTAINER -> makeSnackBar("TODO")
                }
                viewModel.doneCall()
            }
        })

        return binding.root
    }

    private fun navigateToDiaryList(key: Int) {
        this.findNavController().navigate(
            DashboardFragmentDirections.actionDiaryFragmentToDiaryDetailFragment(key, viewModel.selected.value!!)
        )
    }

    private fun dialogBuilder(context: Context, list: Array<String>): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(context)
            .setTitle("ペットの選択")
            .setItems(list) { _, which ->
                viewModel.selected.value = list[which]
                viewModel.changeDashboard()
            }
    }

    private fun makeSnackBar(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG).show()
    }

    private fun createViewModel(context: Context): DashboardViewModel {
        val viewModelFactory = InjectorUtil.provideDashboardViewModelFactory(context)
        return ViewModelProvider(this, viewModelFactory).get(DashboardViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar_add -> {
                this.findNavController().navigate(
                    DashboardFragmentDirections
                        .actionDiaryFragmentToNewDiaryFragment(
                            ListKey.FROM_UNKNOWN, -1 , viewModel.selected.value ?: ""))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
