package com.solarcalculator.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "calculation_history")
data class CalculationResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val createdAt: Date = Date(),
    val calculationName: String = "",
    
    // 输入参数
    val installedCapacity: Double,
    val cityName: String,
    val panelType: String,
    val tiltAngle: Double,
    val orientation: String,
    val systemCost: Double,
    
    // 发电量结果
    val annualGeneration: Double, // 年发电量 (kWh)
    val monthlyGeneration: List<Double>, // 月发电量分布
    val dailyAverageGeneration: Double, // 日均发电量 (kWh)
    
    // 收益结果
    val annualRevenue: Double, // 年收益 (元)
    val cumulativeRevenue: List<Double>, // 累计收益
    val netPresentValue: Double, // 净现值
    
    // 回本分析
    val paybackPeriod: Double, // 投资回收期 (年)
    val internalRateOfReturn: Double, // 内部收益率
    
    // 系统效率
    val systemEfficiency: Double // 系统综合效率
) {
    // 获取第n年的发电量（考虑衰减）
    fun getGenerationForYear(year: Int, degradationRate: Double = 0.005): Double {
        return annualGeneration * Math.pow(1 - degradationRate, year - 1)
    }
    
    // 获取第n年的收益
    fun getRevenueForYear(year: Int, degradationRate: Double = 0.005): Double {
        return annualRevenue * Math.pow(1 - degradationRate, year - 1)
    }
}
