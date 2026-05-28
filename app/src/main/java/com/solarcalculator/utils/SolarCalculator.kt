package com.solarcalculator.utils

import com.solarcalculator.data.model.*
import kotlin.math.*

object SolarCalculator {
    
    // 系统效率系数
    private const val INVERTER_EFFICIENCY = 0.98 // 逆变器效率
    private const val WIRING_LOSS = 0.98 // 线损系数
    private const val DUST_SHADING_FACTOR = 0.95 // 灰尘遮挡系数
    private const val TEMPERATURE_FACTOR = 0.97 // 温度系数
    
    // 计算系统综合效率
    fun calculateSystemEfficiency(panelType: SolarPanelType): Double {
        return panelType.efficiency * INVERTER_EFFICIENCY * WIRING_LOSS * 
               DUST_SHADING_FACTOR * TEMPERATURE_FACTOR
    }
    
    // 计算倾斜角修正系数
    fun calculateTiltFactor(tiltAngle: Double, latitude: Double): Double {
        // 最优倾斜角通常接近当地纬度
        val optimalTilt = latitude
        val angleDiff = abs(tiltAngle - optimalTilt)
        // 每偏离最优角度1度，效率降低约0.5%
        return max(0.85, 1.0 - (angleDiff * 0.005))
    }
    
    // 计算年发电量
    fun calculateAnnualGeneration(
        installedCapacity: Double, // kW
        peakSunHours: Double, // 峰值日照时数
        systemEfficiency: Double,
        orientation: Orientation,
        tiltFactor: Double
    ): Double {
        // 年发电量 = 装机容量 × 峰值日照时数 × 系统效率 × 朝向系数 × 倾斜角系数 × 365
        val orientationFactor = orientation.efficiencyFactor
        return installedCapacity * peakSunHours * systemEfficiency * 
               orientationFactor * tiltFactor * 365
    }
    
    // 计算月发电量分布
    fun calculateMonthlyGeneration(
        installedCapacity: Double,
        monthlySunHours: List<Double>,
        systemEfficiency: Double,
        orientation: Orientation,
        tiltFactor: Double
    ): List<Double> {
        val orientationFactor = orientation.efficiencyFactor
        return monthlySunHours.map { sunHours ->
            installedCapacity * sunHours * systemEfficiency * 
            orientationFactor * tiltFactor * 30 // 按30天计算
        }
    }
    
    // 计算日均发电量
    fun calculateDailyAverageGeneration(annualGeneration: Double): Double {
        return annualGeneration / 365.0
    }
    
    // 计算年收益
    fun calculateAnnualRevenue(
        annualGeneration: Double,
        electricityPrice: Double, // 自用电价
        feedInTariff: Double, // 上网电价
        subsidyAmount: Double, // 补贴
        selfConsumptionRatio: Double // 自用电比例
    ): Double {
        val selfConsumption = annualGeneration * selfConsumptionRatio
        val feedInAmount = annualGeneration * (1 - selfConsumptionRatio)
        
        val selfConsumptionRevenue = selfConsumption * electricityPrice
        val feedInRevenue = feedInAmount * feedInTariff
        val subsidyRevenue = annualGeneration * subsidyAmount
        
        return selfConsumptionRevenue + feedInRevenue + subsidyRevenue
    }
    
    // 计算累计收益（考虑衰减）
    fun calculateCumulativeRevenue(
        annualRevenue: Double,
        projectLifetime: Int,
        degradationRate: Double
    ): List<Double> {
        val cumulativeRevenue = mutableListOf<Double>()
        var totalRevenue = 0.0
        
        for (year in 1..projectLifetime) {
            val yearRevenue = annualRevenue * Math.pow(1 - degradationRate, year - 1)
            totalRevenue += yearRevenue
            cumulativeRevenue.add(totalRevenue)
        }
        
        return cumulativeRevenue
    }
    
    // 计算净现值(NPV)
    fun calculateNPV(
        annualRevenue: Double,
        systemCost: Double,
        projectLifetime: Int,
        discountRate: Double,
        degradationRate: Double
    ): Double {
        var npv = -systemCost // 初始投资为负现金流
        
        for (year in 1..projectLifetime) {
            val yearRevenue = annualRevenue * Math.pow(1 - degradationRate, year - 1)
            val discountedRevenue = yearRevenue / Math.pow(1 + discountRate, year)
            npv += discountedRevenue
        }
        
        return npv
    }
    
    // 计算投资回收期
    fun calculatePaybackPeriod(
        annualRevenue: Double,
        systemCost: Double,
        projectLifetime: Int,
        degradationRate: Double
    ): Double {
        var cumulativeRevenue = 0.0
        
        for (year in 1..projectLifetime) {
            val yearRevenue = annualRevenue * Math.pow(1 - degradationRate, year - 1)
            cumulativeRevenue += yearRevenue
            
            if (cumulativeRevenue >= systemCost) {
                // 精确计算回收期
                val previousCumulative = cumulativeRevenue - yearRevenue
                val remaining = systemCost - previousCumulative
                return (year - 1) + (remaining / yearRevenue)
            }
        }
        
        // 如果25年内无法回本，返回估计值
        return if (cumulativeRevenue > 0) {
            systemCost / (cumulativeRevenue / projectLifetime)
        } else {
            Double.POSITIVE_INFINITY
        }
    }
    
    // 计算内部收益率(IRR) - 简化计算
    fun calculateIRR(
        systemCost: Double,
        annualRevenue: Double,
        projectLifetime: Int,
        degradationRate: Double
    ): Double {
        // 使用二分法估算IRR
        var low = 0.0
        var high = 1.0
        var irr = 0.0
        
        for (i in 0..50) {
            irr = (low + high) / 2
            val npv = calculateNPV(annualRevenue, systemCost, projectLifetime, irr, degradationRate)
            
            if (npv > 0) {
                low = irr
            } else {
                high = irr
            }
        }
        
        return irr
    }
    
    // 主计算函数
    fun calculate(input: CalculationInput, cityData: CitySolarData): CalculationResult {
        // 使用自定义峰值日照时数或城市默认值
        val peakSunHours = input.customPeakSunHours ?: cityData.annualPeakSunHours
        
        // 计算系统效率
        val systemEfficiency = calculateSystemEfficiency(input.panelType)
        
        // 计算倾斜角修正系数
        val tiltFactor = calculateTiltFactor(input.tiltAngle, cityData.latitude)
        
        // 计算年发电量
        val annualGeneration = calculateAnnualGeneration(
            input.installedCapacity,
            peakSunHours,
            systemEfficiency,
            input.orientation,
            tiltFactor
        )
        
        // 计算月发电量
        val monthlyGeneration = calculateMonthlyGeneration(
            input.installedCapacity,
            cityData.monthlySunHours,
            systemEfficiency,
            input.orientation,
            tiltFactor
        )
        
        // 计算日均发电量
        val dailyAverageGeneration = calculateDailyAverageGeneration(annualGeneration)
        
        // 计算年收益
        val annualRevenue = calculateAnnualRevenue(
            annualGeneration,
            input.electricityPrice,
            input.feedInTariff,
            input.subsidyAmount,
            input.selfConsumptionRatio
        )
        
        // 计算累计收益
        val cumulativeRevenue = calculateCumulativeRevenue(
            annualRevenue,
            input.projectLifetime,
            input.annualDegradationRate
        )
        
        // 计算净现值
        val npv = calculateNPV(
            annualRevenue,
            input.systemCost,
            input.projectLifetime,
            input.discountRate,
            input.annualDegradationRate
        )
        
        // 计算投资回收期
        val paybackPeriod = calculatePaybackPeriod(
            annualRevenue,
            input.systemCost,
            input.projectLifetime,
            input.annualDegradationRate
        )
        
        // 计算内部收益率
        val irr = calculateIRR(
            input.systemCost,
            annualRevenue,
            input.projectLifetime,
            input.annualDegradationRate
        )
        
        return CalculationResult(
            installedCapacity = input.installedCapacity,
            cityName = input.cityName,
            panelType = input.panelType.displayName,
            tiltAngle = input.tiltAngle,
            orientation = input.orientation.displayName,
            systemCost = input.systemCost,
            annualGeneration = annualGeneration,
            monthlyGeneration = monthlyGeneration,
            dailyAverageGeneration = dailyAverageGeneration,
            annualRevenue = annualRevenue,
            cumulativeRevenue = cumulativeRevenue,
            netPresentValue = npv,
            paybackPeriod = paybackPeriod,
            internalRateOfReturn = irr,
            systemEfficiency = systemEfficiency
        )
    }
}
