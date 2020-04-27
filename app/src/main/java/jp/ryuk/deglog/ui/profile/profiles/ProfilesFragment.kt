package jp.ryuk.deglog.ui.profile.profiles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import jp.ryuk.deglog.R
import jp.ryuk.deglog.databinding.FragmentProfilesBinding
import jp.ryuk.deglog.ui.profile.newprofile.NewProfileFragment


class ProfilesFragment : Fragment() {

    private lateinit var binding: FragmentProfilesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profiles, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.appBarProfiles)

        binding.newProfileButton.setOnClickListener {
            this.findNavController().navigate(
                ProfilesFragmentDirections.actionProfileFragmentToNewProfileFragment()
            )
        }

        return binding.root
    }
}
