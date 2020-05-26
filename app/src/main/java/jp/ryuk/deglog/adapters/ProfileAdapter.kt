package jp.ryuk.deglog.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.ryuk.deglog.R
import jp.ryuk.deglog.database.Profile
import jp.ryuk.deglog.databinding.ItemProfilesBinding
import jp.ryuk.deglog.utilities.Utils
import jp.ryuk.deglog.utilities.getAgeAndBirthday

class ProfileAdapter(
    private val context: Context,
    private val clickListener: ProfileListener)
    : ListAdapter<Profile, ProfileAdapter.ViewHolder>(ProfileDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener, context)
    }

    class ViewHolder private constructor(private val binding: ItemProfilesBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(profile: Profile, clickListener: ProfileListener, context: Context) {
            val colorMap = Utils.getColorMap(context)
            val colorGender = when (profile.gender) {
                context.getString(R.string.gender_male) -> colorMap["blue"]
                context.getString(R.string.gender_female) -> colorMap["red"]
                else -> colorMap["gray"]
            }
            val colorKey = Utils.colorSelector(profile.color)
            val colorLabel = colorMap[colorKey]

            with(binding) {
                this.profile = profile
                this.clickListener = clickListener
                ageAndBirthday = profile.getAgeAndBirthday()
                binding.profileIcon.setImageResource(Utils.iconSelector(context, profile.type))
                profileGender.setTextColor(colorGender!!)
                profileViewColor.setBackgroundColor(colorLabel!!)
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemProfilesBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

class ProfileDiffCallback : DiffUtil.ItemCallback<Profile>() {
    override fun areItemsTheSame(oldItem: Profile, newItem: Profile): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Profile, newItem: Profile): Boolean {
        return oldItem == newItem
    }
}

class ProfileListener(val clickListener: (name: String) -> Unit) {
    fun onClick(profile: Profile) = clickListener(profile.name)
}