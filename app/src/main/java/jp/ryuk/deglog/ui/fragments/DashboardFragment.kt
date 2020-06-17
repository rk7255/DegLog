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
import jp.ryuk.deglog.ui.data.ChartData
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

        // 初期化
        loadSharedPreferences(viewModel)
        initDashboard(binding, viewModel)

        viewModel.apply {

            allDiary.observe(viewLifecycleOwner) { setDiary() }
            allProfile.observe(viewLifecycleOwner) { setProfile(requireContext()) }
            allTodo.observe(viewLifecycleOwner) { setTodoList() }

            // アイコン ViewModel上で択一処理
            icon.observe(viewLifecycleOwner) {
                binding.dbPersonalIcon.setImageResource(it)
            }
            iconJsonString.observe(viewLifecycleOwner) {
                it?.let { jsonString ->
                    val bitmap = BitmapUtils.convertJsonToBitmap(jsonString)
                    binding.dbPersonalIcon.setImageBitmap(bitmap)
                }
            }

            // データの更新
            binding.apply {
                weightData.observe(viewLifecycleOwner) { dbIncludeWeight.displayData = it }
                lengthData.observe(viewLifecycleOwner) { dbIncludeLength.displayData = it }
                free1Data.observe(viewLifecycleOwner) { dbIncludeFree1.displayData = it }
                free2Data.observe(viewLifecycleOwner) { dbIncludeFree2.displayData = it }
            }

            // グラフの更新
            binding.apply {
                weightChartData.observe(viewLifecycleOwner) {
                    createLineChart(dbIncludeWeight.partDbChart, it, listOf(selected.value ?: ""))
                }
                lengthChartData.observe(viewLifecycleOwner) {
                    createLineChart(dbIncludeLength.partDbChart, it, listOf(selected.value ?: ""))
                }
                free1ChartData.observe(viewLifecycleOwner) {
                    createLineChart(dbIncludeFree1.partDbChart, it, listOf(selected.value ?: ""))
                }
                free2ChartData.observe(viewLifecycleOwner) {
                    createLineChart(dbIncludeFree2.partDbChart, it, listOf(selected.value ?: ""))
                }
            }

            // ToDoの処理
            val recyclerView = binding.dbTodoRecyclerView
            val adapter = TodoAdapter(TodoListener {
                DialogBuilder.confirmDeleteTodoDialogBuilder(
                    requireContext(),
                    it
                ) { viewModel.doneTodo(it.id) }.show()
            })
            recyclerView.adapter = adapter
            todoList.observe(viewLifecycleOwner) {
                if (!it.isNullOrEmpty()) {
                    adapter.submitList(it)
                }
            }

            // クリックリスナー
            clicked.observe(viewLifecycleOwner) { where ->
                if (where != null) {
                    with(WhereClicked) {
                        when (where) {
                            ICON -> navigateToNewProfile()
                            NAME -> selectDashboard(nameListInDiary.toTypedArray())
                            NOTIFY -> ""
                            WEIGHT -> navigateToDiaryList(NavMode.FROM_WEIGHT)
                            LENGTH -> navigateToDiaryList(NavMode.FROM_LENGTH)
                            TODO -> createTodo(requireContext())
                            FREE1 -> ""
                            FREE2 -> ""
                            else -> ""
                        }
                    }
                    doneClick()
                }
            }
        }

        return binding.root
    }

    private fun initDashboard(binding: FragmentDashboardBinding, viewModel: DashboardViewModel) {
        viewModel.apply {
            binding.apply {
                dbIncludeWeight.partDbTitle.text = getString(R.string.title_weight)
                dbIncludeLength.partDbTitle.text = getString(R.string.title_length)
                dbIncludeFree1.partDbTitle.text = free1Title
                dbIncludeFree2.partDbTitle.text = free2Title

                msgNothingDiary.setOnClickListener { navigateToNewDiary() }
                dbButtonSettings.setOnClickListener { navigateToSettings() }

                dbIncludeFree1.partDbContainer.visibility =
                    if (free1Enabled) View.VISIBLE else View.GONE
                dbIncludeFree2.partDbContainer.visibility =
                    if (free2Enabled) View.VISIBLE else View.GONE

                dbIncludeWeight.partDbContainer.setOnClickListener { onClick(WhereClicked.WEIGHT) }
                dbIncludeLength.partDbContainer.setOnClickListener { onClick(WhereClicked.LENGTH) }
                dbIncludeFree1.partDbContainer.setOnClickListener { onClick(WhereClicked.FREE1) }
                dbIncludeFree2.partDbContainer.setOnClickListener { onClick(WhereClicked.FREE2) }
            }
        }
    }

    private fun selectDashboard(array: Array<String>) {
        val dialog = DialogBuilder.selectDashboardDialogBuilder(
            requireContext(), array
        ) { selectDashboardCallback(it) }
        dialog.show()
    }

    private fun selectDashboardCallback(name: String) {
        with(viewModel) {
            selected.value = name
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
            createTodo(selected.value ?: "", text)
        }
    }

    private fun createLineChart(
        lineChart: LineChart,
        chartData: List<ChartData>,
        nameList: List<String>
    ) {
        ChartCreator.createLineChartByIndex(lineChart, chartData, nameList, Deco.DASHBOARD)

    }

    private fun saveSharedPreferences(selected: String) {
        val sharedPreferences =
            requireContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_DASHBOARD, selected)
        editor.apply()
    }

    private fun loadSharedPreferences(viewModel: DashboardViewModel) {
        val sharedPreferences =
            requireContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)

        viewModel.apply {
            selected.value = sharedPreferences.getString(KEY_DASHBOARD, "") ?: ""
            unitWeight.value = sharedPreferences.getString(KEY_UNIT_WEIGHT, "g") ?: "g"
            unitLength.value = sharedPreferences.getString(KEY_UNIT_LENGTH, "mm") ?: "mm"

            free1Enabled = sharedPreferences.getBoolean(KEY_DB1_ENABLED, false)
            free2Enabled = sharedPreferences.getBoolean(KEY_DB2_ENABLED, false)
            free1Title = sharedPreferences.getString(KEY_DB1_NAME, "- Free1 -") ?: "- Free1 -"
            free2Title = sharedPreferences.getString(KEY_DB2_NAME, "- Free2 -") ?: "- Free2 -"
            free2Unit = sharedPreferences.getString(KEY_DB1_UNIT, "") ?: ""
            free2Unit = sharedPreferences.getString(KEY_DB2_UNIT, "") ?: ""
        }
    }

    override fun onPause() {
        super.onPause()
        saveSharedPreferences(viewModel.selected.value ?: "")
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
            DashboardFragmentDirections.toNewDiaryFragment(
                NavMode.NEW,
                -1,
                viewModel.selected.value ?: ""
            )
        )
    }

    private fun navigateToDiaryList(key: Int) {
        this.findNavController().navigate(
            DashboardFragmentDirections.toDiaryListFragment(
                key, viewModel.selected.value ?: ""
            )
        )
    }

    private fun navigateToNewProfile() {
        this.findNavController().navigate(
            DashboardFragmentDirections.toNewProfileFragment(
                NavMode.DASHBOARD,
                viewModel.selected.value ?: ""
            )
        )
    }

    private fun navigateToSettings() {
        this.findNavController().navigate(
            DashboardFragmentDirections.toSettingsFragment()
        )
    }
}
