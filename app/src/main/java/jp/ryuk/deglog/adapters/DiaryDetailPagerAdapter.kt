package jp.ryuk.deglog.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import jp.ryuk.deglog.databinding.DetailViewPagerBinding
import jp.ryuk.deglog.databinding.ItemDetailBinding
import jp.ryuk.deglog.ui.diarydetail.Detail

class DiaryDetailPagerAdapter : ListAdapter<Detail, DiaryDetailPagerAdapter.ViewHolder>(DetailDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    class ViewHolder private constructor(private val binding: ItemDetailBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Detail) {
//            binding.diary = item
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDetailBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

class DetailDiffCallback : DiffUtil.ItemCallback<Detail>() {
    override fun areItemsTheSame(oldItem: Detail, newItem: Detail): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Detail, newItem: Detail): Boolean {
        return oldItem == newItem
    }
}