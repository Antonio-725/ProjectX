package com.onfonmobile.projectx.ui.user

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.onfonmobile.projectx.R
import com.onfonmobile.projectx.data.AppDatabase
import com.onfonmobile.projectx.data.entities.User
import com.onfonmobile.projectx.data.repositories.ContributionRepository
import com.onfonmobile.projectx.databinding.ActivityAdmin2Binding
import com.onfonmobile.projectx.ui.Adapters.GroupMembersAdapter
import com.onfonmobile.projectx.ui.Adapters.UsersAdapter
import com.onfonmobile.projectx.ui.di.SessionManager
//import com.onfonmobile.projectx.ui.dialogs.ContributionDialog
//import com.onfonmobile.projectx.ui.adapters.GroupMembersAdapter
import com.onfonmobile.projectx.ui.login.ContributionDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Admin : AppCompatActivity() {
    private lateinit var binding: ActivityAdmin2Binding
    private lateinit var adapter: GroupMembersAdapter
    private lateinit var adapter1: UsersAdapter
    private lateinit var viewModel: AdminViewModel
    private lateinit  var contributionRepository: ContributionRepository

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityAdmin2Binding.inflate(layoutInflater)
    setContentView(binding.root)

    setupViewModel()
    setupRecyclerView()
    setupDashboardActions()
    loadUsers()
    resetUIBasedOnRole() // Use this instead of checkUserRole()
}

    private fun setupViewModel() {
        val db = AppDatabase.getDatabase(this)
        viewModel = ViewModelProvider(this, AdminViewModelFactory(db)).get(AdminViewModel::class.java)
        contributionRepository = ContributionRepository(db)  // Initialize with the database, not just the DAO
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        Log.d("Admin", "Menu inflated: ${menu.size()} items")
        // Loop through menu items to verify IDs
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            Log.d("Admin", "Menu item: ${item.itemId} with title: ${item.title}")
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("Admin", "Menu item clicked: ${item.itemId} with title: ${item.title}")
        return when (item.itemId) {
            R.id.action_refresh -> {
                Log.d("Admin", "Refresh action triggered")
                refreshData()
                true
            }
            else -> {
                Log.d("Admin", "Unhandled menu item clicked")
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = GroupMembersAdapter(
            userList = mutableListOf(),
            onUserClicked = { user ->
                showUserDetailsDialog(user)
            },
            onTotalContributionFetched = { userId, callback ->
                viewModel.getTotalContribution(userId) { totalContribution ->
                    callback(totalContribution)
                }
            }
        )

        binding.groupMembersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@Admin)
            adapter = this@Admin.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupDashboardActions() {
        binding.apply {
            updateSavingsButton.setOnClickListener {
                showContributionDialog()
            }

            viewEditUsersButton.setOnClickListener {
                showUserManagementDialog()
            }

            calculateStatsButton.setOnClickListener {
                navigateToStatistics()
            }

            sendRemindersButton.setOnClickListener {
                showRemindersDialog()
            }
        }
    }

    private fun loadUsers() {
        viewModel.loadUsers { users ->
            adapter.setUsers(users)
        }
    }

    private fun showContributionDialog() {
        ContributionDialog(this) { username, amount, date ->
            viewModel.saveContribution(
                username = username,
                amount = amount,
                date = date,
                onSuccess = {
                    showSuccessMessage("Contribution saved successfully")
                    loadUsers() // Refresh the list
                },
                onError = { error ->
                    showErrorMessage(error)
                }
            )
        }.show()
    }

private fun checkUserRole() {
    val sessionManager = SessionManager(this)
    Log.d("Admin", "Checking user role: isAdmin = ${sessionManager.isAdmin()}")
    if (!sessionManager.isAdmin()) {
        restrictUserActions()
    }
}


    private fun restrictUserActions() {
        binding.apply {
            updateSavingsButton.isEnabled = false
            viewEditUsersButton.isEnabled = false
            calculateStatsButton.isEnabled = false
            sendRemindersButton.isEnabled = false
        }
        showReadOnlyDialog()
    }

    private fun showReadOnlyDialog() {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Access Restricted")
            .setMessage("You are not the admin. You have read-only permission on this page.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(R.color.facebook_blue, theme))
    }

    // In your Admin class, update showUserManagementDialog method:

    private fun showUserManagementDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_manage_users, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.usersRecyclerView)
        val btnSaveChanges = dialogView.findViewById<Button>(R.id.btnSaveChanges)

        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(dialogView)

        // Create the adapter here
        val usersAdapter = UsersAdapter(
            mutableListOf(),
            onTotalContributionFetched = { userId, callback ->
                viewModel.getTotalContribution(userId) { totalContribution ->
                    callback(totalContribution)
                }
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = usersAdapter

        viewModel.loadUsers { users ->
            runOnUiThread {
                usersAdapter.updateUsers(users)
            }
        }

        // Set up the Save Changes button click handler
        btnSaveChanges.setOnClickListener {
            // Use usersAdapter (local variable) instead of adapter1 (uninitialized class property)
            val updatedUsers = usersAdapter.getUpdatedUsers()
            val updatedContributions = usersAdapter.getUpdatedContributions()

            // Launch a coroutine to save the changes
            lifecycleScope.launch {
                try {
                    // Make sure contributionRepository is initialized
                    if (!::contributionRepository.isInitialized) {
                        contributionRepository = ContributionRepository(AppDatabase.getDatabase(this@Admin))
                    }

                    // Save user updates (roles)
                    if (updatedUsers != null && updatedUsers.isNotEmpty()) {
                        for (user in updatedUsers) {
                            contributionRepository.updateUser(user)
                        }
                        Log.d("SaveChanges", "Updated ${updatedUsers.size} users")
                    }

                    // Save contribution updates
                    if (updatedContributions.isNotEmpty()) {
                        for ((userId, amount) in updatedContributions) {
                            contributionRepository.updateUserTotalContribution(userId, amount)
                        }
                        Log.d("SaveChanges", "Updated ${updatedContributions.size} contribution totals")
                    }

                    // Show success message
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Admin, "Changes saved successfully", Toast.LENGTH_SHORT).show()
                        bottomSheetDialog.dismiss() // Close the dialog after saving
                    }
                } catch (e: Exception) {
                    Log.e("SaveChanges", "Error saving changes", e)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Admin, "Error saving changes: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        bottomSheetDialog.show()
    }

    // Add this method to handle UI updates based on role
    private fun resetUIBasedOnRole() {
        val sessionManager = SessionManager(this)
        val isAdmin = sessionManager.isAdmin()

        Log.d("Admin", "Resetting UI based on role. isAdmin: $isAdmin")

        if (isAdmin) {
            binding.apply {
                updateSavingsButton.isEnabled = true
                viewEditUsersButton.isEnabled = true
                calculateStatsButton.isEnabled = true
                sendRemindersButton.isEnabled = true
            }
        } else {
            restrictUserActions()
        }
    }

    // Update the onCreate method to call resetUIBasedOnRole instead of checkUserRo

    // Modify the refreshData method to also reset UI permissions
    private fun refreshData() {
        val snackbar = Snackbar.make(binding.root, "Refreshing data...", Snackbar.LENGTH_INDEFINITE)
        snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.facebook_blue))
        snackbar.setTextColor(ContextCompat.getColor(this, R.color.white))
        snackbar.show()
        Log.d("Admin", "Refreshing data...")

        viewModel.loadUsers { users ->
            adapter.refreshData(users)

            // Reset UI permissions when data is refreshed
            resetUIBasedOnRole()

            viewModel.getTotalContributionsForAllUsers { contributions ->
                runOnUiThread {
                    snackbar.dismiss()
                    Toast.makeText(this, "Data refreshed successfully!", Toast.LENGTH_SHORT).show()
                    Log.d("Admin", "Refresh complete, Snackbar dismissed.")
                }
            }
        }
    }


    private fun navigateToStatistics() {
        // TODO: Navigate to statistics screen
        // Intent to StatisticsActivity
        showUnderDevelopmentMessage("Statistics")
    }

    private fun showRemindersDialog() {
        // TODO: Implement reminders functionality
        showUnderDevelopmentMessage("Reminders")
    }

    private fun showUserDetailsDialog(user: User) {
        // TODO: Show user details in a bottom sheet or dialog
        showUnderDevelopmentMessage("User Details")
    }

    // Helper functions for showing messages
    private fun showSuccessMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
    }

    private fun showUnderDevelopmentMessage(feature: String) {
        Toast.makeText(this, "$feature feature coming soon!", Toast.LENGTH_SHORT).show()
    }
}

