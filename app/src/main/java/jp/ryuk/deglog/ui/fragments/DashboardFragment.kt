package jp.ryuk.deglog.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.LineChart
import jp.ryuk.deglog.R
import jp.ryuk.deglog.adapters.TodoAdapter
import jp.ryuk.deglog.adapters.TodoListener
import jp.ryuk.deglog.databinding.FragmentDashboardBinding
import jp.ryuk.deglog.ui.data.ChartCreator
import jp.ryuk.deglog.ui.data.DialogBuilder
import jp.ryuk.deglog.ui.viewmodels.DashboardViewModel
import jp.ryuk.deglog.utilities.*


class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private val viewModel: DashboardViewModel by viewModels {
        InjectorUtil.provideDashboardViewModelFactory(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.dbAppBar)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.selected = loadSharedPreferences()

        val recyclerView = binding.dbTodoRecyclerView
        val adapter = TodoAdapter(TodoListener {
            DialogBuilder.deleteTodoDialogBuilder(
                requireContext(), it
            ) { viewModel.doneTodo(it.id) }
                .show()
        })
        recyclerView.adapter = adapter

        with(viewModel) {

            allDiary.observe(viewLifecycleOwner) { setDiary() }
            allProfile.observe(viewLifecycleOwner) { setProfile(requireContext()) }
            allTodo.observe(viewLifecycleOwner) { setTodoList() }

            icon.observe(viewLifecycleOwner) {
                binding.dbPersonalIcon.setImageResource(it)
            }

            todoList.observe(viewLifecycleOwner) {
                if (!it.isNullOrEmpty()) {
                    adapter.submitList(it)
                }
            }

            weightDataList.observe(viewLifecycleOwner) {
                createLineChart(binding.dbWeightChart, it)
            }

            lengthDataList.observe(viewLifecycleOwner) {
                createLineChart(binding.dbLengthChart, it)
            }

            clicked.observe(viewLifecycleOwner) { where ->
                if (where != null) {
                    with(WhereClicked) {
                        when (where) {
                            ICON -> navigateToNewProfile()
                            NAME -> selectDashboard(nameList.toTypedArray())
                            NOTIFY -> ""
                            WEIGHT -> navigateToDiaryList(NavMode.FROM_WEIGHT)
                            LENGTH -> navigateToDiaryList(NavMode.FROM_LENGTH)
                            TODO -> createTodo(requireContext())
                            else -> ""
                        }
                    }
                    doneClick()
                }
            }
        }

        return binding.root
    }

    private fun selectDashboard(array: Array<String>) {
        val dialog = DialogBuilder.selectDashboardDialogBuilder(
            requireContext(), array) { selectDashboardCallback(it) }
        dialog.show()
    }

    private fun selectDashboardCallback(name: String) {
        with(viewModel) {
            selected = name
            setDiary()
            setProfile(requireContext())
            setTodoList()
        }
    }

    private fun createTodo(context: Context) {
        val dialog = DialogBuilder.createTodoDialog(context) {
            createTodoCallback(it)
        }
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
    }

    private fun createTodoCallback(text: String) {
        with(viewModel) {
            createTodo(selected, text)
        }
    }

    private fun createLineChart(lineChart: LineChart, dataList: List<Float>) {
        ChartCreator.createLineChartByIndex(lineChart, dataList)
    }

    private fun saveSharedPreferences(selected: String) {
        val sharedPreferences =
            requireContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_DASHBOARD, selected)
        editor.apply()
    }

    private fun loadSharedPreferences(): String {
        val sharedPreferences =
            requireContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_DASHBOARD, "") ?: ""
    }

    override fun onPause() {
        super.onPause()
        saveSharedPreferences(viewModel.selected)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu_dashboard, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar_add -> navigateToNewDiary()
            R.id.toolbar_list -> navigateToDiaryList(-1)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToNewDiary() {
        this.findNavController().navigate(
            DashboardFragmentDirections.toNewDiaryFragment(NavMode.NEW, -1, viewModel.selected)
        )
    }

    private fun navigateToDiaryList(key: Int) {
        this.findNavController().navigate(
            DashboardFragmentDirections.toDiaryListFragment(
                key, viewModel.selected)
        )
    }

    private fun navigateToNewProfile() {
        this.findNavController().navigate(
            DashboardFragmentDirections.toNewProfileFragment(NavMode.DASHBOARD, viewModel.selected)
        )
    }
}
