package com.onfonmobile.projectx.ui.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.onfonmobile.projectx.R
import com.onfonmobile.projectx.data.entities.User
import com.onfonmobile.projectx.databinding.ItemGroupMemberBinding


class GroupMembersAdapter(
    private var userList: MutableList<User>,
    private val onUserClicked: ((User) -> Unit)? = null,
    private val onTotalContributionFetched: (String, (Double) -> Unit) -> Unit
) : RecyclerView.Adapter<GroupMembersAdapter.UserViewHolder>() {

    val userContributions = mutableMapOf<String, Double>() // Cache contributions

    inner class UserViewHolder(private val binding: ItemGroupMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.apply {
                // Set user name and initial
                userName.text = user.username
                userInitial.text = user.username.firstOrNull()?.toString()?.uppercase() ?: "?"

                // Configure role chip
                roleChip.apply {
                    text = user.role
                    when (user.role.lowercase()) {
                        "admin" -> {
                            setChipBackgroundColorResource(R.color.admin_chip_background)
                            setTextColor(ContextCompat.getColor(context, R.color.admin_chip_text))
                        }
                        "member" -> {
                            setChipBackgroundColorResource(R.color.user_chip_background)
                            setTextColor(ContextCompat.getColor(context, R.color.user_chip_text))
                        }
                        else -> {
                            setChipBackgroundColorResource(R.color.role_chip_background)
                            setTextColor(ContextCompat.getColor(context, R.color.role_chip_text))
                        }
                    }
                }

                // If the contribution is cached, display it immediately
                if (userContributions.containsKey(user.id)) {
                    totalContribution.text =
                        "Total: $${String.format("%.2f", userContributions[user.id])}"
                } else {
                    // Display loading state
                    totalContribution.text = "Loading..."

                    // Use the callback style to fetch the contribution
                    onTotalContributionFetched(user.id) { total ->
                        // Cache and update UI on the main thread
                        userContributions[user.id] = total
                        totalContribution.text =
                            "Total: $${String.format("%.2f", total)}"
                    }
                }

                // Set click listener for the entire item
                root.setOnClickListener {
                    onUserClicked?.invoke(user)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemGroupMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount() = userList.size

    // Update method for changing the user list
    fun setUsers(newList: List<User>) {
        userList.clear()
        userList.addAll(newList)
        notifyDataSetChanged()
    }

    // New refreshData method with DiffUtil for better animations
    fun refreshData(newList: List<User>) {
        // Clear contribution cache
        userContributions.clear()

        // Update list with DiffUtil for better animation
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = userList.size
            override fun getNewListSize(): Int = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return userList[oldItemPosition].id == newList[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return userList[oldItemPosition] == newList[newItemPosition]
            }
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)

        userList.clear()
        userList.addAll(newList)

        // Apply changes with animations
        diffResult.dispatchUpdatesTo(this)
    }
}
