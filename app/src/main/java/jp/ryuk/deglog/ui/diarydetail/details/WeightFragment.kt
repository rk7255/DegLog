package jp.ryuk.deglog.ui.diarydetail.details

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import jp.ryuk.deglog.R
import jp.ryuk.deglog.databinding.FragmentWeightBinding

class WeightFragment(private val selectedName: String) : Fragment() {

    private lateinit var binding: FragmentWeightBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_weight, container, false)

        val str = "FROM $selectedName"
        binding.weightFrom.text = str

        return binding.root
    }

}
