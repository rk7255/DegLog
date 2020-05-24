package jp.ryuk.deglog.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import jp.ryuk.deglog.R
import jp.ryuk.deglog.adapters.ProfileAdapter
import jp.ryuk.deglog.adapters.ProfileListener
import jp.ryuk.deglog.databinding.FragmentProfilesBinding
import jp.ryuk.deglog.ui.viewmodels.ProfilesViewModel
import jp.ryuk.deglog.utilities.InjectorUtil


class ProfilesFragment : Fragment() {

    private lateinit var binding: FragmentProfilesBinding
    private val viewModel: ProfilesViewModel by viewModels {
        InjectorUtil.provideProfilesViewModelFactory(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfilesBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.appBarProfiles)

        val recyclerView = binding.profilesRecyclerView
        val adapter = ProfileAdapter(
            requireContext(),
            ProfileListener { name ->
                viewModel.onClickProfile(name)
            })
        recyclerView.adapter = adapter

        viewModel.profiles.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) adapter.submitList(it)
        })

        viewModel.navigateToNewProfile.observe(viewLifecycleOwner, Observer {
            if (it != null) navigate(it)
        })

        return binding.root
    }

    private fun navigate(name: String) {
        this.findNavController().navigate(
            ProfilesFragmentDirections.actionProfileFragmentToNewProfileFragment(
                "edit",
                name
            )
        )
        viewModel.doneNavigateToNewProfile()
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar_add -> {
                this.findNavController().navigate(
                    ProfilesFragmentDirections.actionProfileFragmentToNewProfileFragment(
                        "new",
                        ""
                    )
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
