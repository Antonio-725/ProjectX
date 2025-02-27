package com.onfonmobile.projectx.ui.login

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.onfonmobile.projectx.R
import com.onfonmobile.projectx.data.AppDatabase
import com.onfonmobile.projectx.data.entities.Contribution
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ContributionDialog(
    private val context: Context,
    private val onSave: (String, Double, Long) -> Unit
) {
    private val TAG = "ContributionDialog"
    private val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_contribution, null)
    private val dialog = MaterialAlertDialogBuilder(context)
        .setView(dialogView)
        .create()

    private val userDao = AppDatabase.getDatabase(context).userDao()
    private val contributionDao = AppDatabase.getDatabase(context).contributionDao()

    private var selectedDate = System.currentTimeMillis()

    fun show() {
        Log.d(TAG, "Showing contribution dialog")

        val etUsername = dialogView.findViewById<TextInputEditText>(R.id.etUsername)
        val etAmount = dialogView.findViewById<TextInputEditText>(R.id.etContributionAmount)
        val tilDate = dialogView.findViewById<TextInputLayout>(R.id.tilDate)
        val etDate = dialogView.findViewById<TextInputEditText>(R.id.etDate)
        val btnSave = dialogView.findViewById<MaterialButton>(R.id.btnSaveContribution)

        // Set initial date
        updateDateDisplay(etDate)

        // Custom date picker dialog
        // Get today's date
        // Get today's date
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0) // Reset to start of the day

// Remove the start constraint to allow past months
        val constraints = CalendarConstraints.Builder().build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Contribution Date")
            .setSelection(today.timeInMillis) // Default to today
            .setCalendarConstraints(constraints) // Apply constraints
            .setTheme(R.style.CustomDatePickerTheme)
            .build()

        // Date picker callbacks
        datePicker.addOnPositiveButtonClickListener { selection ->
            selectedDate = selection
            updateDateDisplay(etDate)
            Log.d(TAG, "Date selected: ${getFormattedDate(selection)}")
        }

        // Show date picker when clicking the date field
        etDate.setOnClickListener {
            datePicker.show(
                (context as FragmentActivity).supportFragmentManager,
                "DATE_PICKER"
            )
        }

        // Input validation
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                btnSave.isEnabled = etUsername.text?.isNotEmpty() == true &&
                        etAmount.text?.isNotEmpty() == true
            }
        }

        etUsername.addTextChangedListener(textWatcher)
        etAmount.addTextChangedListener(textWatcher)

        // Save button click handler
        btnSave.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val amount = etAmount.text.toString().toDoubleOrNull()

            if (validateInput(username, amount)) {
                saveContribution(username, amount!!)
            }
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun updateDateDisplay(dateField: TextInputEditText) {
        dateField.setText(getFormattedDate(selectedDate))
    }

    private fun getFormattedDate(timestamp: Long): String {
        return SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
    }

    private fun validateInput(username: String, amount: Double?): Boolean {
        when {
            username.isEmpty() -> {
                showError("Please enter a username")
                return false
            }
            amount == null || amount <= 0 -> {
                showError("Please enter a valid amount")
                return false
            }
        }
        return true
    }

    private fun saveContribution(username: String, amount: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = userDao.getUserByUsername(username)

                if (user != null) {
                    val contribution = Contribution(
                        userId = user.id,
                        amount = amount,
                        date = selectedDate
                    )
                    contributionDao.insertContribution(contribution)

                    withContext(Dispatchers.Main) {
                        onSave(username, amount, selectedDate)
                        dialog.dismiss()
                        showSuccess("Contribution saved successfully")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showError("User not found")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving contribution", e)
                withContext(Dispatchers.Main) {
                    showError("Failed to save contribution")
                }
            }
        }
    }

    private fun showError(message: String) {
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()

        // Ensure the "OK" button is clearly visible by setting a better text color
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(context.getColor(R.color.facebook_blue)) // Change to a color that fits your theme
    }


    private fun showSuccess(message: String) {
        Snackbar.make(
            (context as Activity).findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }
}