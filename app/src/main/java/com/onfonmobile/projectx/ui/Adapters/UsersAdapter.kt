package com.onfonmobile.projectx.ui.Adapters

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.onfonmobile.projectx.R
import com.onfonmobile.projectx.data.entities.User

class UsersAdapter(
    private var users: MutableList<User>,
    private val onTotalContributionFetched: (String, (Double) -> Unit) -> Unit
) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    // Use a Map with userId as key to prevent duplicate entries and track changes properly
    private val updatedUsers = mutableMapOf<String, User>()
    private val userContributions = mutableMapOf<String, Double>()

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUsername: TextView = view.findViewById(R.id.txtUsername)
        val edtContribution: EditText = view.findViewById(R.id.edtContribution)
        val spinnerRole: Spinner = view.findViewById(R.id.spinnerRole)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        Log.d("UsersAdapter", "Binding user ${user.username} with role ${user.role}")

        holder.txtUsername.text = user.username

        // Fetch totalContribution dynamically
        if (userContributions.containsKey(user.id)) {
            holder.edtContribution.setText(userContributions[user.id].toString())
        } else {
            holder.edtContribution.setText("Loading...")
            onTotalContributionFetched(user.id) { total ->
                userContributions[user.id] = total
                holder.edtContribution.setText(total.toString())
            }
        }

        // Set role spinner selection - use case-insensitive comparison
        val rolePosition = if (user.role.equals("Admin", ignoreCase = true)) 0 else 1
        holder.spinnerRole.setSelection(rolePosition)

        // Update contributions on text change
        holder.edtContribution.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val newContribution = s?.toString()?.toDoubleOrNull()
                if (newContribution != null) {
                    Log.d("UsersAdapter", "Contribution changed for ${user.username}: $newContribution")

                    // Update the userContributions map
                    userContributions[user.id] = newContribution

                    // Get or create a copy of the user to update
                    val updatedUser = updatedUsers[user.id] ?: user.copy()

                    // Store the updated user - we still need this in the updatedUsers map
                    // even though we're not changing a property on the User object itself
                    updatedUsers[user.id] = updatedUser

                    // Note: We're not setting updatedUser.totalContribution because it doesn't exist
                    // You'll need a separate mechanism to save the contribution changes
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Handle role selection changes
        holder.spinnerRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val newRole = parent?.getItemAtPosition(position).toString()

                // Only update if role has changed
                if (!user.role.equals(newRole, ignoreCase = true)) {
                    Log.d("UsersAdapter", "Role changed for ${user.username}: ${user.role} -> $newRole")

                    // Get or create a copy of the user to update
                    val updatedUser = updatedUsers[user.id] ?: user.copy()

                    // Update the role
                    updatedUser.role = newRole

                    // Store the updated user
                    updatedUsers[user.id] = updatedUser
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<User>) {
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }

    fun getUpdatedUsers(): List<User>? {
        return if (updatedUsers.isNotEmpty()) {
            Log.d("UsersAdapter", "Returning ${updatedUsers.size} updated users")
            updatedUsers.values.toList()
        } else {
            Log.d("UsersAdapter", "No users updated")
            null
        }
    }
    fun getUpdatedContributions(): Map<String, Double> {
        return userContributions.toMap()
    }
}