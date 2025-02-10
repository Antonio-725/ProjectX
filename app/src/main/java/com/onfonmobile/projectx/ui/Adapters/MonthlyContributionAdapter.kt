package com.onfonmobile.projectx.ui.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.onfonmobile.projectx.R
import com.onfonmobile.projectx.data.entities.MonthlyContributionSummary

class MonthlyContributionAdapter(
    private val contributions: List<MonthlyContributionSummary>
) : RecyclerView.Adapter<MonthlyContributionAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val monthText: TextView = itemView.findViewById(R.id.monthTextView)
        val amountText: TextView = itemView.findViewById(R.id.amountTextView)
        val deficitText: TextView = itemView.findViewById(R.id.deficitTextView)
        val remarkText: TextView = itemView.findViewById(R.id.remarkTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_monthly_contribution, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = contributions[position]
        holder.monthText.text = item.month
        holder.amountText.text = "Amount: KES ${item.totalAmount}"
        holder.deficitText.text = "Deficit: KES ${item.deficit}"
        holder.remarkText.text = "Remark: ${item.remark}"
    }

    override fun getItemCount(): Int = contributions.size
}
