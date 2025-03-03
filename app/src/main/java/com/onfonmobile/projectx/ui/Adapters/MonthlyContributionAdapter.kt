package com.onfonmobile.projectx.ui.Adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.onfonmobile.projectx.R
import com.onfonmobile.projectx.data.entities.MonthlyContributionSummary
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class MonthlyContributionAdapter(
    private val contributions: List<MonthlyContributionSummary>
) : RecyclerView.Adapter<MonthlyContributionAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val monthText: TextView = itemView.findViewById(R.id.monthTextView)
        val amountText: TextView = itemView.findViewById(R.id.amountTextView)
        val deficitText: TextView = itemView.findViewById(R.id.deficitTextView)
        val remarkChip: Chip = itemView.findViewById(R.id.remarkTextView)
        val progressBar: LinearProgressIndicator = itemView.findViewById(R.id.contributionProgress)
        val progressText: TextView = itemView.findViewById(R.id.progressText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_monthly_contribution, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = contributions[position]
    val context = holder.itemView.context

    // Set month
    holder.monthText.text = item.month

    // Format currency amounts using KES
    val formatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
        currency = Currency.getInstance("KES")
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    // Set amounts
    holder.amountText.text = formatter.format(item.totalAmount)
    holder.deficitText.text = formatter.format(item.deficit)

    // Use the precomputed percentage from the data model
    val progress = item.percentage.toInt().coerceIn(0, 100)
    holder.progressBar.progress = progress
    holder.progressText.text = String.format("%.1f%%", item.percentage)

    // Set remark chip style
    when (item.remark) {
        "Achieved" -> {
            holder.remarkChip.apply {
                setChipBackgroundColorResource(R.color.success_color)
                setTextColor(Color.WHITE)
                chipIcon = context.getDrawable(R.drawable.ic_check_circle)
                chipIconTint = context.getColorStateList(android.R.color.white)
            }
        }
        "Unachieved" -> {
            holder.remarkChip.apply {
                setChipBackgroundColorResource(R.color.error_color)
                setTextColor(Color.WHITE)
                chipIcon = context.getDrawable(R.drawable.ic_error_circle)
                //chipIconTint = context.getColorStateList(android.R.color.white)
            }
        }
    }
    holder.remarkChip.text = item.remark

    // Set card click listener
    holder.itemView.setOnClickListener {
        // Handle card click if needed
    }
}


    override fun getItemCount(): Int = contributions.size

//    companion object {
//        private const val TARGET_AMOUNT = 400.0
//    }
}