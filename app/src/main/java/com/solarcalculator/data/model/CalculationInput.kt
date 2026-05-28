package com.solarcalculator.data.model

data class CalculationInput(
    val installedCapacity: Double, // 装机容量 (kW)
    val cityName: String, // 城市名称
    val panelType: SolarPanelType, // 组件类型
    val tiltAngle: Double, // 安装角度 (度)
    val orientation: Orientation, // 朝向
    val customPeakSunHours: Double? = null, // 自定义峰值日照时数
    
    // 收益计算参数
    val systemCost: Double, // 系统总成本 (元)
    val electricityPrice: Double, // 电价 (元/kWh)
    val feedInTariff: Double, // 上网电价 (元/kWh)
    val subsidyAmount: Double, // 补贴金额 (元/kWh)
    val selfConsumptionRatio: Double, // 自用电比例 (0-1)
    val annualDegradationRate: Double = 0.005, // 年衰减率 (默认0.5%)
    val discountRate: Double = 0.05, // 折现率 (默认5%)
    val projectLifetime: Int = 25 // 项目寿命 (默认25年)
) {
    companion object {
        fun default(): CalculationInput {
            return CalculationInput(
                installedCapacity = 10.0,
                cityName = "北京",
                panelType = SolarPanelType.MONO_CRYSTALLINE,
                tiltAngle = 30.0,
                orientation = Orientation.SOUTH,
                systemCost = 50000.0,
                electricityPrice = 0.55,
                feedInTariff = 0.38,
                subsidyAmount = 0.0,
                selfConsumptionRatio = 0.3
            )
        }
    }
}
