package jp.ryuk.deglog.ui.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import jp.ryuk.deglog.R
import jp.ryuk.deglog.databinding.FragmentChartBinding
import kotlin.random.Random


class ChartFragment : Fragment() {

    private lateinit var binding: FragmentChartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_chart, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.appBarChart)

        binding.testText.text = Random.nextInt().toString()

        return binding.root
    }
}
