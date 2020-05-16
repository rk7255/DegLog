package jp.ryuk.deglog.ui.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import jp.ryuk.deglog.R
import jp.ryuk.deglog.databinding.FragmentChartBinding


class ChartFragment : Fragment() {

    private lateinit var binding: FragmentChartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChartBinding.inflate(inflater, container, false)

        binding.chartFilterOpen.setOnClickListener {
            showFilterAnimation()
        }

        binding.chartFilterBg.setOnClickListener {
            showFilterAnimation()
        }

        return binding.root
    }

    private fun showFilterAnimation() {
        val container = binding.chartFilterContainer
        val bg = binding.chartFilterBg
        val icon = binding.chartFilterIcon
        val rotate = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_right)

        if (binding.chartFilterContainer.visibility == View.GONE) {
            val slideIn = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_top)
            val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)

            container.startAnimation(slideIn)
            container.visibility = View.VISIBLE
            bg.startAnimation(fadeIn)
            bg.visibility = View.VISIBLE
            icon.startAnimation(rotate)
            icon.setImageResource(R.drawable.ic_less)
        } else {
            val slideOut = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_top)
            val fadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)

            container.startAnimation(slideOut)
            container.visibility = View.GONE
            bg.startAnimation(fadeOut)
            bg.visibility = View.GONE
            icon.startAnimation(rotate)
            icon.setImageResource(R.drawable.ic_more)
        }
    }

}
