package com.onfonmobile.projectx.ui.di

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onfonmobile.projectx.R
import com.onfonmobile.projectx.ui.Adapters.UsersAdapter
import com.onfonmobile.projectx.ui.user.AdminViewModel

//class ManageUsersDialog(
//    context: Context,
//    private val viewModel: AdminViewModel
//) : Dialog(context) {
//
//    private lateinit var usersAdapter: UsersAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        setContentView(R.layout.dialog_manage_users)
//
//        val recyclerView: RecyclerView = findViewById(R.id.usersRecyclerView)
//        val btnSave: Button = findViewById(R.id.btnSaveChanges)
//
//        recyclerView.layoutManager = LinearLayoutManager(context)
//
//        // Fetch users and display
//        viewModel.loadUsers { users ->
//            usersAdapter = UsersAdapter(users)
//            recyclerView.adapter = usersAdapter
//        }
//
//        btnSave.setOnClickListener {
//            usersAdapter.getUpdatedUsers()?.let { updatedUsers ->
//                viewModel.updateUsers(updatedUsers)
//                dismiss()
//            }
//        }
//    }
//}
