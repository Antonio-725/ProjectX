package com.onfonmobile.projectx.ui.login

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.onfonmobile.projectx.R
import com.onfonmobile.projectx.data.AppDatabase
import com.onfonmobile.projectx.data.entities.Contribution
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ContributionDialog(private val context: Context, private val onSave: (String, Double, Long) -> Unit) {

    private val TAG = "ContributionDialog"  // ✅ Tag for logging
    private val dialogView =
        LayoutInflater.from(context).inflate(R.layout.dialog_add_contribution, null)
    private val dialog = AlertDialog.Builder(context).setView(dialogView).create()
    private val userDao = AppDatabase.getDatabase(context).userDao()  // ✅ DAO for user validation
    private val contributionDao = AppDatabase.getDatabase(context).contributionDao()


    fun show() {
        Log.d(TAG, "Showing contribution dialog")  // Log to confirm method is called
        val etUsername = dialogView.findViewById<EditText>(R.id.etUsername)
        val etAmount = dialogView.findViewById<EditText>(R.id.etContributionAmount)
        val btnSelectDate = dialogView.findViewById<Button>(R.id.btnSelectDate)
        val tvSelectedDate = dialogView.findViewById<TextView>(R.id.tvSelectedDate)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveContribution)

        var selectedDate = System.currentTimeMillis()

        btnSelectDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    selectedDate = calendar.timeInMillis
                    val formattedDate =
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                    tvSelectedDate.text = formattedDate

                    // Log date selection
                    Log.d(TAG, "Date selected: $formattedDate")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnSave.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val amount = etAmount.text.toString().toDoubleOrNull()

            if (username.isNotEmpty() && amount != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    val user = userDao.getUserByUsername(username)

                    if (user != null) {
                        val contribution =
                            Contribution(userId = user.id, amount = amount, date = selectedDate)
                        contributionDao.insertContribution(contribution)
                        Log.d("ContributionDialog", "Contribution saved for user ID: ${user.id}")
                        withContext(Dispatchers.Main) { dialog.dismiss() }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "User not found!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(
                    context,
                    "Please enter a valid username and amount",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialog.show()
        Log.d(TAG, "Dialog should now be shown")  // Confirm dialog showing
    }
}
