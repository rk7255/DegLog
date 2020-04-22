package jp.ryuk.deglog.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.databinding.WeightItemBinding

class WeightAdapter(private val clickListener: WeightListener)
    : androidx.recyclerview.widget.ListAdapter<Diary, WeightAdapter.ViewHolder>(WeightDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    class ViewHolder private constructor(private val binding: WeightItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Diary, clickListener: WeightListener) {
            binding.diary = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = WeightItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

class WeightDiffCallback : DiffUtil.ItemCallback<Diary>() {
    override fun areItemsTheSame(oldItem: Diary, newItem: Diary): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Diary, newItem: Diary): Boolean {
        return oldItem == newItem
    }
}

class WeightListener(val clickListener: (id: Long) -> Unit) {
    fun onClick(diary: Diary) = clickListener(diary.id)
}