package com.onfonmobile.projectx.ui.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.onfonmobile.projectx.R
import com.onfonmobile.projectx.data.entities.User
import com.onfonmobile.projectx.databinding.ItemGroupMemberBinding

class GroupMembersAdapter(
    private var userList: MutableList<User>,
    private val onUserClicked: ((User) -> Unit)? = null
) : RecyclerView.Adapter<GroupMembersAdapter.UserViewHolder>() {

    inner class UserViewHolder(
        private val binding: ItemGroupMemberBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.apply {
                // Set user name
                userName.text = user.username

                // Set user initial in avatar
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

                // Remove totalContributionTextView reference (since User doesn't have it)

                // Set click listener for the entire item
                root.setOnClickListener {
                    onUserClicked?.invoke(user)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemGroupMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount() = userList.size

    // Update method for changing the user list
    fun updateList(newList: List<User>) {
        userList.clear()
        userList.addAll(newList)
        notifyDataSetChanged()
    }
}
