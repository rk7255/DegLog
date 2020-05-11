package jp.ryuk.deglog.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.databinding.ItemDetailListBinding

class DetailListAdapter(
    private val clickListener: DetailListListener,
    private val suffixWeight: String,
    private val suffixLength: String
) : ListAdapter<Diary, DetailListAdapter.ViewHolder>(DetailListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener, suffixWeight, suffixLength)
    }

    class ViewHolder private constructor(
        private val binding: ItemDetailListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Diary, clickListener: DetailListListener, suffixWeight: String, suffixLength: String) {
            binding.diary = item
            binding.suffixWeight = suffixWeight
            binding.suffixLength = suffixLength
            binding.clickListener = clickListener
            binding.hasComment = item.memo.isNullOrEmpty()
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDetailListBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

class DetailListDiffCallback : DiffUtil.ItemCallback<Diary>() {
    override fun areItemsTheSame(oldItem: Diary, newItem: Diary): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Diary, newItem: Diary): Boolean {
        return oldItem == newItem
    }
}

class DetailListListener(val clickListener: (id: Long) -> Unit) {
    fun onClick(diary: Diary) = clickListener(diary.id)
}