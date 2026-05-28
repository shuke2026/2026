# 🌞 光伏发电测算工具

一款专业的光伏发电系统收益测算Android应用，采用Material Design 3现代化设计风格，帮助用户快速评估光伏项目的投资回报。

## ✨ 功能特性

### 核心测算功能
- ✅ **发电量智能估算**
  - 支持多种组件类型（单晶硅、多晶硅、薄膜、PERC、HJT、TOPCon）
  - 考虑安装角度、朝向、地理位置等因素
  - 提供月度和年度发电量预测
  
- ✅ **投资收益分析**
  - 年收益、累计收益计算
  - 净现值(NPV)分析
  - 内部收益率(IRR)计算
  
- ✅ **回本周期计算**
  - 动态投资回收期分析
  - 考虑资金时间价值的折现计算
  - 组件衰减率影响评估

### 数据展示
- 📊 **可视化图表**
  - 月发电量分布柱状图
  - 累计收益趋势曲线
  - 投资回报分析图表

### 数据管理
- 🏙️ **城市光照数据库**
  - 内置中国20+主要城市光照数据
  - 支持自定义峰值日照时数
  - 历史年均光照数据查询

- 💾 **历史记录管理**
  - 测算结果本地存储
  - 历史记录查看与删除
  - 结果分享功能

## 📱 界面预览

应用采用Material Design 3设计规范，以绿色系（环保主题）和蓝色系（科技感）为主色调：

- **首页**: 欢迎界面、统计概览、最近测算记录
- **测算页**: 参数输入、滑动调节、实时计算
- **结果页**: 详细数据展示、图表可视化、分享保存

## 🛠️ 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Kotlin | 1.9.20 | 开发语言 |
| Android SDK | API 26-34 | 目标平台 |
| MVVM | - | 架构模式 |
| Hilt | 2.48 | 依赖注入 |
| Room | 2.6.1 | 本地数据库 |
| Retrofit | 2.9.0 | 网络请求 |
| MPAndroidChart | v3.1.0 | 图表库 |
| Coroutines | 1.7.3 | 异步处理 |
| Material Design 3 | 1.11.0 | UI组件 |

## 📋 系统要求

- **最低SDK**: API 26 (Android 8.0)
- **目标SDK**: API 34 (Android 14)
- **开发环境**: Android Studio Hedgehog | 2023.1.1 或更高版本
- **JDK**: 17

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/yourusername/SolarCalculator.git
cd SolarCalculator
```

### 2. 配置签名密钥（可选）

如需生成签名APK，请在GitHub仓库设置中添加以下Secrets：

- `SIGNING_KEY`: Base64编码的签名密钥库
- `ALIAS`: 密钥别名
- `KEY_STORE_PASSWORD`: 密钥库密码
- `KEY_PASSWORD`: 密钥密码

### 3. 构建项目

```bash
# 调试版本
./gradlew assembleDebug

# 发布版本
./gradlew assembleRelease
```

### 4. 运行测试

```bash
# 单元测试
./gradlew test

# Lint检查
./gradlew lint
```

## 📁 项目结构

```
app/
├── src/main/
│   ├── java/com/solarcalculator/
│   │   ├── data/
│   │   │   ├── local/          # Room数据库、SharedPreferences
│   │   │   ├── model/          # 数据模型
│   │   │   └── repository/     # 数据仓库
│   │   ├── di/                 # Hilt依赖注入模块
│   │   ├── ui/
│   │   │   ├── home/           # 首页
│   │   │   ├── calculator/     # 测算页面
│   │   │   ├── result/         # 结果展示
│   │   │   └── components/     # 可复用组件
│   │   └── utils/              # 工具类
│   ├── res/                    # 资源文件
│   └── AndroidManifest.xml
├── build.gradle.kts
└── proguard-rules.pro
```

## 🔧 核心算法

### 发电量计算

```
年发电量(kWh) = 装机容量(kW) × 峰值日照时数(h) × 系统效率 × 365 × 朝向系数 × 倾斜角系数

系统效率 = 组件效率 × 逆变器效率(0.98) × 线损系数(0.98) × 灰尘遮挡系数(0.95) × 温度系数(0.97)
```

### 收益计算

```
年收益 = 自用电量 × 电价 + 上网电量 × 上网电价 + 补贴金额

投资回收期 = 系统总投资 / 年均净收益（考虑衰减）
```

## 🔄 CI/CD

项目配置了完整的GitHub Actions工作流：

- **自动构建**: 每次推送到main/develop分支时自动构建
- **代码检查**: 自动运行Lint检查和单元测试
- **APK签名**: 自动签名发布版本
- **Release发布**: main分支推送时自动创建Release

## 📊 内置城市数据

应用内置以下中国主要城市的光照数据：

| 城市 | 年均峰值日照时数 | 年太阳辐射量 |
|------|------------------|--------------|
| 拉萨 | 6.5h | 1980 kWh/m² |
| 昆明 | 5.2h | 1550 kWh/m² |
| 北京 | 4.8h | 1450 kWh/m² |
| 乌鲁木齐 | 4.8h | 1450 kWh/m² |
| 深圳 | 4.6h | 1380 kWh/m² |
| 天津 | 4.6h | 1380 kWh/m² |
| 广州 | 4.5h | 1350 kWh/m² |
| 济南 | 4.5h | 1350 kWh/m² |
| 沈阳 | 4.4h | 1320 kWh/m² |
| 西安 | 4.3h | 1280 kWh/m² |
| 郑州 | 4.3h | 1280 kWh/m² |
| 哈尔滨 | 4.2h | 1280 kWh/m² |
| 上海 | 4.2h | 1250 kWh/m² |
| 武汉 | 4.1h | 1220 kWh/m² |
| 南京 | 4.1h | 1220 kWh/m² |
| 杭州 | 4.0h | 1180 kWh/m² |
| 福州 | 4.0h | 1200 kWh/m² |
| 长沙 | 3.8h | 1150 kWh/m² |
| 成都 | 3.2h | 950 kWh/m² |
| 重庆 | 3.0h | 900 kWh/m² |

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📄 开源协议

本项目采用 [MIT License](LICENSE) 开源协议。

## 🙏 致谢

- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) - 图表库
- [Material Design 3](https://m3.material.io/) - 设计规范
- [Android Jetpack](https://developer.android.com/jetpack) - 架构组件

---

<p align="center">
  Made with ❤️ for a greener future 🌍
</p>
