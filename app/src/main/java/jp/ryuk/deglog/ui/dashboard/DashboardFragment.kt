package jp.ryuk.deglog.ui.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import jp.ryuk.deglog.R
import jp.ryuk.deglog.adapters.TodoAdapter
import jp.ryuk.deglog.adapters.TodoListener
import jp.ryuk.deglog.databinding.FragmentDashboardBinding
import jp.ryuk.deglog.ui.diarylist.ListKey
import jp.ryuk.deglog.utilities.InjectorUtil
import jp.ryuk.deglog.utilities.iconSelector


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
                Log.d("DEBUG", "$it")
                viewModel.diariesLoaded.value = true
                viewModel.sectionLoaded()
            } else {
                viewModel.diariesLoaded.value = false
            }
        })

        viewModel.profiles.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                viewModel.profilesLoaded.value = true
                viewModel.sectionLoaded()
            } else {
                viewModel.profilesLoaded.value = false
            }
        })

        viewModel.names.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                viewModel.namesLoaded.value = true
                names = it.toTypedArray()
                if (viewModel.selected.value.isNullOrEmpty()) viewModel.selected.value = names[0]
                binding.dbPersonalName.text = viewModel.selected.value
                viewModel.sectionLoaded()
            } else {
                viewModel.namesLoaded.value = true
            }
        })

        viewModel.type.observe(viewLifecycleOwner, Observer {
            binding.dbPersonalIcon.setImageResource(iconSelector(requireContext(), it))
        })

        val recyclerView = binding.dbTodoRecyclerView
        val adapter = TodoAdapter(TodoListener { todo ->
            dialogDeleteTodoBuilder(requireContext(), todo).show()
        })
        recyclerView.adapter = adapter

        viewModel.todoList.observe(viewLifecycleOwner, Observer {
            it?.let { adapter.submitList(it) }
        })

        viewModel.call.observe(viewLifecycleOwner, Observer { call ->
            if (call != null) {
                when (call) {
                    OnCallKey.PERSONAL_ICON -> navigateToEditProfile()
                    OnCallKey.PERSONAL_CONTAINER -> dialogSelectBuilder(
                        requireContext(),
                        names
                    ).show()
                    OnCallKey.NOTIFY_CONTAINER -> {
                    }
                    OnCallKey.WEIGHT_CONTAINER -> navigateToDiaryList(ListKey.FROM_WEIGHT)
                    OnCallKey.LENGTH_CONTAINER -> navigateToDiaryList(ListKey.FROM_LENGTH)
                    OnCallKey.TODO_CONTAINER -> {
                        val dialog = dialogCreateTodoBuilder(requireContext())
                        dialog.show()
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                    }
                }
                viewModel.doneCall()
            }
        })

        return binding.root
    }

    private fun navigateToDiaryList(key: Int) {
        this.findNavController().navigate(
            DashboardFragmentDirections.actionDiaryFragmentToDiaryDetailFragment(
                key,
                viewModel.selected.value!!
            )
        )
    }

    private fun navigateToEditProfile() {
        this.findNavController().navigate(
            DashboardFragmentDirections.actionDiaryFragmentToNewProfileFragment(
                "dashboard",
                viewModel.selected.value!!
            )
        )
    }

    @SuppressLint("InflateParams")
    private fun dialogCreateTodoBuilder(context: Context): AlertDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_todo, null)
        val titleText = view.findViewById<TextView>(R.id.dialog_todo_title)
        val editText = view.findViewById<EditText>(R.id.dialog_todo_text)
        titleText.text = getString(R.string.add_new_todo)
        editText.hint = getString(R.string.todo)

        val dialog = MaterialAlertDialogBuilder(context)
            .setView(view)
            .setPositiveButton(getString(R.string.add)) { _, _ ->
                viewModel.newTodo(editText.text.toString())
            }
            .setNeutralButton(getString(R.string.dialog_cancel), null)
            .create()

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = !s.isNullOrBlank()
            }
        })

        return dialog
    }

    private fun dialogDeleteTodoBuilder(context: Context, todo: Todo): AlertDialog {
        return MaterialAlertDialogBuilder(context)
            .setMessage("選択したToDoを完了します\n\"${todo.todo}\"")
            .setNeutralButton(getString(R.string.dialog_cancel), null)
            .setPositiveButton(getString(R.string.dialog_success)) { _, _ ->
                viewModel.deleteTodo(todo.id)
            }
            .create()
    }

    private fun dialogSelectBuilder(context: Context, list: Array<String>): AlertDialog {
        return MaterialAlertDialogBuilder(context)
            .setTitle(getString(R.string.choice_pet))
            .setItems(list) { _, which ->
                viewModel.selected.value = list[which]
                viewModel.changeDashboard()
            }
            .create()
    }

    private fun createViewModel(context: Context): DashboardViewModel {
        val viewModelFactory = InjectorUtil.provideDashboardViewModelFactory(context)
        return ViewModelProvider(this, viewModelFactory).get(DashboardViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu_dashboard, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar_add -> {
                this.findNavController().navigate(
                    DashboardFragmentDirections
                        .actionDiaryFragmentToNewDiaryFragment(
                            "new",
                            -1,
                            viewModel.selected.value ?: ""
                        )
                )
            }
            R.id.toolbar_list -> {
                if (viewModel.allLoaded.value == true) {
                    navigateToDiaryList(-1)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
