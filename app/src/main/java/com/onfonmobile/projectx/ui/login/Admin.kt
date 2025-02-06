//package com.onfonmobile.projectx.ui.login
//
//import android.os.Bundle
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.onfonmobile.projectx.R
//import com.onfonmobile.projectx.data.AppDatabase
//import com.onfonmobile.projectx.ui.Adapters.GroupMembersAdapter
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class Admin : AppCompatActivity() {
//    private lateinit var recyclerView: RecyclerView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_admin2)
//        recyclerView = findViewById(R.id.groupMembersRecyclerView)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//        val userDao = AppDatabase.getDatabase(this).userDao()
//
//        CoroutineScope(Dispatchers.IO).launch {
//            val users = userDao.getAllUsers()
//
//            withContext(Dispatchers.Main) {
//                recyclerView.adapter = GroupMembersAdapter(users)
//            }
//        }
//    }
//}

package com.onfonmobile.projectx.ui.login

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.onfonmobile.projectx.R
import com.onfonmobile.projectx.data.AppDatabase
import com.onfonmobile.projectx.ui.Adapters.GroupMembersAdapter
import com.onfonmobile.projectx.ui.user.AdminViewModel
import com.onfonmobile.projectx.ui.user.AdminViewModelFactory

class Admin : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: AdminViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin2)

        val db = AppDatabase.getDatabase(this)
        viewModel = ViewModelProvider(this, AdminViewModelFactory(db)).get(AdminViewModel::class.java)

        recyclerView = findViewById(R.id.groupMembersRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load Users
        // Load Users
        viewModel.loadUsers { users ->
            recyclerView.adapter = GroupMembersAdapter(users.toMutableList()) // Convert List<User> to MutableList<User>
        }


        // Handle Update Button
        val updateButton = findViewById<MaterialButton>(R.id.updateSavingsButton)
        updateButton.setOnClickListener {
            showContributionDialog()
        }
    }

    private fun showContributionDialog() {
        Log.d("AdminActivity", "Update button clicked")
        val dialog = ContributionDialog(this) { username, amount, date ->
            viewModel.saveContribution(
                username,
                amount,
                date,
                onSuccess = { Toast.makeText(this, "Contribution saved!", Toast.LENGTH_SHORT).show() },
                onError = { error -> Toast.makeText(this, error, Toast.LENGTH_SHORT).show() }
            )
        }
        dialog.show()
    }

}
