package com.onfonmobile.projectx.data.entities

data class MonthlyContributionSummary(
    val month: String,
    val totalAmount: Double,
    val deficit: Double,
    val remark: String,
    val percentage: Double
)
