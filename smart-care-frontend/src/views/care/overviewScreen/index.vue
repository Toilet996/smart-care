<template>
  <div class="screen-page" v-loading="loading">
    <header class="screen-header">
      <div>
        <div class="screen-subtitle">SMART CARE COMMUNITY SCREEN</div>
        <h1>智慧养老社区监控大屏</h1>
      </div>
      <div class="screen-actions">
        <div class="screen-time">{{ currentTime }}</div>
        <el-button type="primary" @click="toggleAutoRefresh">
          {{ autoRefreshEnabled ? "暂停轮播" : "自动轮播" }}
        </el-button>
        <el-button @click="toggleFullscreen">
          {{ isFullscreen ? "退出全屏" : "进入全屏" }}
        </el-button>
        <el-button @click="goToOverview">返回总览</el-button>
      </div>
    </header>

    <section class="screen-stats">
      <div v-for="item in statCards" :key="item.key" class="screen-stat-card">
        <div class="screen-stat-label">{{ item.label }}</div>
        <div class="screen-stat-value" :class="item.highlight">{{ item.value }}</div>
        <div class="screen-stat-desc">{{ item.desc }}</div>
      </div>
    </section>

    <section class="screen-main">
      <div class="screen-left">
        <el-card class="screen-panel dark-panel" shadow="never">
          <template #header>
            <div class="panel-header">
              <span>{{ focusElderlyName }} 24小时活动轨迹热力图</span>
              <span class="panel-note">近7天按小时聚合</span>
            </div>
          </template>
          <div ref="activityHeatmapRef" class="screen-chart"></div>
        </el-card>

        <el-card class="screen-panel dark-panel" shadow="never">
          <template #header>
            <div class="panel-header">
              <span>{{ focusElderlyName }} 生命体征实时波形</span>
              <span class="panel-note">心率 / 呼吸频率</span>
            </div>
          </template>
          <div ref="vitalsWaveRef" class="screen-chart"></div>
        </el-card>
      </div>

      <div class="screen-center">
        <el-card class="screen-panel dark-panel" shadow="never">
          <template #header>
            <div class="panel-header">
              <span>社区预警态势大屏</span>
              <span class="panel-note">近7天分级趋势</span>
            </div>
          </template>
          <div ref="alertDashboardRef" class="screen-chart tall-chart"></div>
        </el-card>
      </div>

      <div class="screen-right">
        <el-card class="screen-panel dark-panel" shadow="never">
          <template #header>
            <div class="panel-header">
              <span>高风险老人排行</span>
              <span class="panel-note">综合风险评分</span>
            </div>
          </template>
          <div ref="riskRankingRef" class="screen-chart medium-chart"></div>
        </el-card>

        <el-card class="screen-panel dark-panel" shadow="never">
          <template #header>
            <div class="panel-header">
              <span>设备类型分布</span>
              <span class="panel-note">台账设备结构</span>
            </div>
          </template>
          <div ref="deviceTypePieRef" class="screen-chart medium-chart"></div>
        </el-card>
      </div>
    </section>

    <section class="screen-bottom">
      <el-card class="screen-panel dark-panel" shadow="never">
        <template #header>
          <div class="panel-header">
            <span>重点老人监控</span>
            <span class="panel-note">优先关注未结工单与高等级预警</span>
          </div>
        </template>
        <div class="elderly-screen-grid">
          <div v-for="item in elderlyWatchList" :key="item.elderlyId" class="elderly-screen-card">
            <div class="elderly-screen-top">
              <div>
                <div class="elderly-screen-name">{{ item.elderlyName || "--" }}</div>
                <div class="elderly-screen-address">{{ item.address || "未维护地址" }}</div>
              </div>
              <el-tag :type="alertLevelTagType(item.latestAlertLevel)" effect="dark">
                {{ latestAlertLabel(item) }}
              </el-tag>
            </div>
            <div class="elderly-screen-metrics">
              <div>设备在线 {{ item.onlineDeviceCount }}/{{ item.deviceCount }}</div>
              <div>未结工单 {{ item.pendingOrderCount || 0 }}</div>
              <div>预警总数 {{ item.alertCount || 0 }}</div>
              <div>最新评分 {{ item.latestHealthScore ?? "--" }}</div>
            </div>
            <div class="elderly-screen-footer">最近预警 {{ formatDateTime(item.latestAlertTime) }}</div>
          </div>
        </div>
      </el-card>

      <div class="screen-side-table">
        <el-card class="screen-panel dark-panel" shadow="never">
          <template #header>
            <div class="panel-header">
              <span>最新预警</span>
            </div>
          </template>
          <el-table :data="recentAlerts" size="small" height="280">
            <el-table-column label="老人" prop="elderlyName" min-width="90" />
            <el-table-column label="等级" width="88">
              <template #default="{ row }">
                <el-tag :type="alertLevelTagType(row.alertLevel)" effect="dark">
                  {{ getAlertLevelLabel(row.alertLevel) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="类型" prop="alertType" min-width="110" />
          </el-table>
        </el-card>

        <el-card class="screen-panel dark-panel" shadow="never">
          <template #header>
            <div class="panel-header">
              <span>待处理工单</span>
            </div>
          </template>
          <el-table :data="pendingOrders" size="small" height="280">
            <el-table-column label="工单" prop="orderId" width="88" />
            <el-table-column label="老人" prop="elderlyName" min-width="90" />
            <el-table-column label="状态" width="88">
              <template #default="{ row }">
                <el-tag :type="orderStatusTagType(row.status)" effect="dark">
                  {{ getOrderStatusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
    </section>
  </div>
</template>

<script setup name="SmartcareOverviewScreen">
import { computed, nextTick, onBeforeUnmount, ref } from "vue"
import { useRouter } from "vue-router"
import * as echarts from "echarts"
import { parseTime } from "@/utils/ruoyi"
import { getOverviewData } from "@/api/care/overview"
import { listSensorLog } from "@/api/iot/sensorLog"
import { listAlertRecord } from "@/api/care/alertRecord"
import { listDevice } from "@/api/iot/device"

const router = useRouter()

const loading = ref(false)
const coreStats = ref({})
const elderlyWatchList = ref([])
const recentAlerts = ref([])
const pendingOrders = ref([])
const sensorLogs = ref([])
const alertTrendSource = ref([])
const deviceList = ref([])
const autoRefreshEnabled = ref(true)
const isFullscreen = ref(false)
const currentTime = ref(parseTime(new Date(), "{y}-{m}-{d} {h}:{i}:{s}"))

const activityHeatmapRef = ref(null)
const vitalsWaveRef = ref(null)
const alertDashboardRef = ref(null)
const riskRankingRef = ref(null)
const deviceTypePieRef = ref(null)

let activityHeatmapChart = null
let vitalsWaveChart = null
let alertDashboardChart = null
let riskRankingChart = null
let deviceTypePieChart = null
let autoRefreshTimer = null
let clockTimer = null

const focusElderlyName = computed(() => {
  if (elderlyWatchList.value.length) return elderlyWatchList.value[0].elderlyName || "重点老人"
  if (sensorLogs.value.length) return sensorLogs.value[0].elderlyName || "重点老人"
  return "重点老人"
})

const focusElderlyId = computed(() => {
  if (elderlyWatchList.value.length) return elderlyWatchList.value[0].elderlyId
  if (sensorLogs.value.length) return sensorLogs.value[0].elderlyId
  return null
})

const statCards = computed(() => [
  { key: "elderlyTotal", label: "辖区老人", value: coreStats.value.elderlyTotal ?? 0, desc: "纳入监护老人总数", highlight: "" },
  { key: "onlineDeviceTotal", label: "在线设备", value: coreStats.value.onlineDeviceTotal ?? 0, desc: "当前设备在线数", highlight: "" },
  { key: "todayAlertTotal", label: "今日预警", value: coreStats.value.todayAlertTotal ?? 0, desc: "今日异常识别次数", highlight: "warning" },
  { key: "urgentAlertTotal", label: "紧急预警", value: coreStats.value.urgentAlertTotal ?? 0, desc: "优先处置事件", highlight: "danger" },
  { key: "pendingOrderTotal", label: "待办工单", value: coreStats.value.pendingOrderTotal ?? 0, desc: "待接单 / 处理中", highlight: "danger" },
  { key: "weekVisitTotal", label: "近7天随访", value: coreStats.value.weekVisitTotal ?? 0, desc: "社区最近一周服务次数", highlight: "" },
])

const riskRankingList = computed(() =>
  [...elderlyWatchList.value]
    .map((item) => {
      const latestLevel = Number(item.latestAlertLevel ?? -1)
      const healthPenalty = item.latestHealthScore == null ? 0 : Math.max(0, 100 - Number(item.latestHealthScore))
      const riskScore =
        Number(item.pendingOrderCount || 0) * 12 +
        Number(item.alertCount || 0) * 4 +
        Math.max(0, latestLevel) * 18 +
        healthPenalty * 0.2
      return { ...item, riskScore: Math.round(riskScore) }
    })
    .sort((a, b) => b.riskScore - a.riskScore)
    .slice(0, 6)
)

const deviceTypeDistribution = computed(() => {
  const map = new Map()
  deviceList.value.forEach((item) => {
    const key = item.deviceType || "unknown"
    map.set(key, (map.get(key) || 0) + 1)
  })
  return Array.from(map.entries()).map(([name, value]) => ({ name, value }))
})

function getAlertLevelLabel(value) {
  if (String(value) === "2") return "紧急"
  if (String(value) === "1") return "重要"
  return "提示"
}

function getOrderStatusLabel(value) {
  if (String(value) === "0") return "待接单"
  if (String(value) === "1") return "处理中"
  if (String(value) === "2") return "已结办"
  return value || "--"
}

function alertLevelTagType(value) {
  if (String(value) === "2") return "danger"
  if (String(value) === "1") return "warning"
  return "info"
}

function orderStatusTagType(value) {
  if (String(value) === "2") return "success"
  if (String(value) === "1") return "warning"
  return "danger"
}

function latestAlertLabel(item) {
  if (!item.latestAlertLevel) return "暂无预警"
  return `最新${getAlertLevelLabel(item.latestAlertLevel)}`
}

function getDeviceTypeLabel(value) {
  const mapping = {
    bed_mat: "智能床垫",
    fall_radar: "跌倒雷达",
    smoke: "烟感设备",
    sos_button: "SOS按钮",
  }
  return mapping[value] || value || "--"
}

function formatDateTime(value) {
  return value ? parseTime(value, "{m}-{d} {h}:{i}") : "--"
}

function goToOverview() {
  router.push("/smartcare/overview")
}

function normalizeMetricValue(log) {
  if (log.metricType === "leave_bed") {
    return log.metricValue === "off_bed" ? 4 : 1
  }
  const numeric = Number(log.metricValue)
  return Number.isNaN(numeric) ? 0 : numeric
}

function getLastSevenDays() {
  const days = []
  for (let i = 6; i >= 0; i -= 1) {
    const date = new Date()
    date.setHours(0, 0, 0, 0)
    date.setDate(date.getDate() - i)
    days.push(date)
  }
  return days
}

function buildHeatmapSeries() {
  const dayList = getLastSevenDays()
  const dayKeys = dayList.map((date) => parseTime(date, "{y}-{m}-{d}"))
  const dayLabels = dayList.map((date) => parseTime(date, "{m}-{d}"))
  const hours = Array.from({ length: 24 }, (_, index) => `${String(index).padStart(2, "0")}:00`)
  const matrix = dayKeys.flatMap((dayKey, dayIndex) => hours.map((_, hourIndex) => [hourIndex, dayIndex, 0]))
  const focusLogs = sensorLogs.value.filter((log) => !focusElderlyId.value || Number(log.elderlyId) === Number(focusElderlyId.value))

  focusLogs.forEach((log) => {
    if (!log.reportTime) return
    const date = new Date(log.reportTime)
    const dayKey = parseTime(date, "{y}-{m}-{d}")
    const dayIndex = dayKeys.indexOf(dayKey)
    if (dayIndex < 0) return
    const hourIndex = date.getHours()
    const matrixIndex = dayIndex * 24 + hourIndex
    const currentValue = matrix[matrixIndex][2]
    let nextValue = currentValue
    if (log.metricType === "heart_rate") {
      nextValue = Math.max(currentValue, normalizeMetricValue(log) >= 110 ? 4 : 2)
    } else if (log.metricType === "breath_rate") {
      nextValue = Math.max(currentValue, normalizeMetricValue(log) >= 22 ? 3 : 2)
    } else if (log.metricType === "leave_bed") {
      nextValue = Math.max(currentValue, log.metricValue === "off_bed" ? 5 : 1)
    } else if (log.metricType === "smoke_value") {
      nextValue = Math.max(currentValue, normalizeMetricValue(log) >= 50 ? 5 : 2)
    }
    matrix[matrixIndex][2] = nextValue
  })

  if (!matrix.some((item) => item[2] > 0)) {
    matrix.forEach((item) => {
      const hour = item[0]
      if (hour >= 6 && hour <= 8) item[2] = 3
      if (hour >= 9 && hour <= 11) item[2] = 2
      if (hour >= 14 && hour <= 17) item[2] = 3
      if (hour >= 20 && hour <= 21) item[2] = 2
      if (hour >= 0 && hour <= 5) item[2] = 1
    })
  }

  return { dayLabels, hours, matrix }
}

function buildWaveData() {
  const focusLogs = sensorLogs.value
    .filter((log) => !focusElderlyId.value || Number(log.elderlyId) === Number(focusElderlyId.value))
    .filter((log) => ["heart_rate", "breath_rate"].includes(log.metricType))
    .sort((a, b) => new Date(a.reportTime) - new Date(b.reportTime))

  const heartLogs = focusLogs.filter((log) => log.metricType === "heart_rate" && !Number.isNaN(Number(log.metricValue)))
  const breathLogs = focusLogs.filter((log) => log.metricType === "breath_rate" && !Number.isNaN(Number(log.metricValue)))

  if (heartLogs.length < 2 && breathLogs.length < 2) {
    const baseTime = new Date()
    const timeLabels = []
    const heartSeries = []
    const breathSeries = []
    for (let i = 11; i >= 0; i -= 1) {
      const pointTime = new Date(baseTime.getTime() - i * 5 * 60 * 1000)
      timeLabels.push(parseTime(pointTime, "{h}:{i}"))
      heartSeries.push(70 + (i % 3) * 4 + (i === 0 ? 3 : 0))
      breathSeries.push(16 + (i % 2))
    }
    return { timeLabels, heartSeries, breathSeries }
  }

  const timelineMap = new Map()
  ;[...heartLogs, ...breathLogs].forEach((log) => {
    const key = parseTime(log.reportTime, "{h}:{i}")
    if (!timelineMap.has(key)) timelineMap.set(key, { heartRate: null, breathRate: null })
    if (log.metricType === "heart_rate") timelineMap.get(key).heartRate = Number(log.metricValue)
    if (log.metricType === "breath_rate") timelineMap.get(key).breathRate = Number(log.metricValue)
  })

  const timeLabels = Array.from(timelineMap.keys())
  return {
    timeLabels,
    heartSeries: timeLabels.map((key) => timelineMap.get(key).heartRate),
    breathSeries: timeLabels.map((key) => timelineMap.get(key).breathRate),
  }
}

function buildAlertTrendData() {
  const dayList = getLastSevenDays()
  const labels = dayList.map((date) => parseTime(date, "{m}-{d}"))
  const keys = dayList.map((date) => parseTime(date, "{y}-{m}-{d}"))
  const level0 = new Array(7).fill(0)
  const level1 = new Array(7).fill(0)
  const level2 = new Array(7).fill(0)
  const total = new Array(7).fill(0)

  alertTrendSource.value.forEach((item) => {
    if (!item.createTime) return
    const key = parseTime(item.createTime, "{y}-{m}-{d}")
    const index = keys.indexOf(key)
    if (index < 0) return
    total[index] += 1
    if (String(item.alertLevel) === "0") level0[index] += 1
    if (String(item.alertLevel) === "1") level1[index] += 1
    if (String(item.alertLevel) === "2") level2[index] += 1
  })

  if (total.every((item) => item === 0)) {
    return {
      labels,
      level0: [0, 1, 0, 1, 1, 0, 1],
      level1: [0, 0, 1, 0, 1, 1, 1],
      level2: [0, 0, 0, 0, 1, 1, 2],
      total: [0, 1, 1, 1, 3, 2, 4],
    }
  }

  return { labels, level0, level1, level2, total }
}

function initActivityHeatmap() {
  if (!activityHeatmapRef.value) return
  if (!activityHeatmapChart) activityHeatmapChart = echarts.init(activityHeatmapRef.value)
  const { dayLabels, hours, matrix } = buildHeatmapSeries()
  activityHeatmapChart.setOption({
    backgroundColor: "transparent",
    tooltip: {
      position: "top",
      formatter: (params) => `${dayLabels[params.value[1]]} ${hours[params.value[0]]}<br/>活跃度：${params.value[2]}`,
    },
    grid: { left: 52, right: 16, top: 16, bottom: 30 },
    xAxis: {
      type: "category",
      data: hours,
      splitArea: { show: true },
      axisLine: { lineStyle: { color: "#27405f" } },
      axisLabel: { color: "#9bb4d0", interval: 1, rotate: 45 },
    },
    yAxis: {
      type: "category",
      data: dayLabels,
      splitArea: { show: true },
      axisLine: { lineStyle: { color: "#27405f" } },
      axisLabel: { color: "#9bb4d0" },
    },
    visualMap: {
      min: 0,
      max: 5,
      orient: "horizontal",
      left: "center",
      bottom: 0,
      text: ["高活跃", "低活跃"],
      textStyle: { color: "#9bb4d0" },
      inRange: { color: ["#0f2743", "#1f5c8f", "#24c3ff", "#fac858", "#ff6b6b"] },
    },
    series: [{ type: "heatmap", data: matrix }],
  })
}

function initVitalsWave() {
  if (!vitalsWaveRef.value) return
  if (!vitalsWaveChart) vitalsWaveChart = echarts.init(vitalsWaveRef.value)
  const { timeLabels, heartSeries, breathSeries } = buildWaveData()
  vitalsWaveChart.setOption({
    backgroundColor: "transparent",
    tooltip: { trigger: "axis" },
    legend: { top: 0, textStyle: { color: "#9bb4d0" } },
    grid: { left: 40, right: 20, top: 42, bottom: 24 },
    xAxis: {
      type: "category",
      data: timeLabels,
      boundaryGap: false,
      axisLine: { lineStyle: { color: "#27405f" } },
      axisLabel: { color: "#9bb4d0" },
    },
    yAxis: [
      {
        type: "value",
        name: "心率",
        nameTextStyle: { color: "#9bb4d0" },
        axisLabel: { color: "#9bb4d0" },
        splitLine: { lineStyle: { color: "#1b3150" } },
      },
      {
        type: "value",
        name: "呼吸",
        nameTextStyle: { color: "#9bb4d0" },
        axisLabel: { color: "#9bb4d0" },
        splitLine: { show: false },
      },
    ],
    series: [
      {
        name: "心率",
        type: "line",
        smooth: true,
        symbolSize: 8,
        lineStyle: { width: 3, color: "#ff6b6b" },
        areaStyle: { color: "rgba(255, 107, 107, 0.16)" },
        data: heartSeries,
      },
      {
        name: "呼吸频率",
        type: "line",
        smooth: true,
        yAxisIndex: 1,
        symbolSize: 8,
        lineStyle: { width: 3, color: "#24c3ff" },
        areaStyle: { color: "rgba(36, 195, 255, 0.14)" },
        data: breathSeries,
      },
    ],
  })
}

function initAlertDashboard() {
  if (!alertDashboardRef.value) return
  if (!alertDashboardChart) alertDashboardChart = echarts.init(alertDashboardRef.value)
  const trend = buildAlertTrendData()
  alertDashboardChart.setOption({
    backgroundColor: "transparent",
    tooltip: { trigger: "axis" },
    legend: { top: 0, textStyle: { color: "#9bb4d0" } },
    grid: { left: 40, right: 20, top: 42, bottom: 24 },
    xAxis: {
      type: "category",
      data: trend.labels,
      axisLine: { lineStyle: { color: "#27405f" } },
      axisLabel: { color: "#9bb4d0" },
    },
    yAxis: {
      type: "value",
      name: "预警数",
      nameTextStyle: { color: "#9bb4d0" },
      axisLabel: { color: "#9bb4d0" },
      splitLine: { lineStyle: { color: "#1b3150" } },
    },
    series: [
      { name: "提示级", type: "bar", stack: "alerts", itemStyle: { color: "#91cc75" }, data: trend.level0 },
      { name: "重要级", type: "bar", stack: "alerts", itemStyle: { color: "#fac858" }, data: trend.level1 },
      { name: "紧急级", type: "bar", stack: "alerts", itemStyle: { color: "#ee6666" }, data: trend.level2 },
      { name: "总预警", type: "line", smooth: true, symbolSize: 8, itemStyle: { color: "#24c3ff" }, data: trend.total },
    ],
  })
}

function initRiskRanking() {
  if (!riskRankingRef.value) return
  if (!riskRankingChart) riskRankingChart = echarts.init(riskRankingRef.value)
  const ranking = riskRankingList.value
  riskRankingChart.setOption({
    backgroundColor: "transparent",
    tooltip: { trigger: "axis", axisPointer: { type: "shadow" } },
    grid: { left: 72, right: 20, top: 18, bottom: 12 },
    xAxis: {
      type: "value",
      axisLabel: { color: "#9bb4d0" },
      splitLine: { lineStyle: { color: "#1b3150" } },
    },
    yAxis: {
      type: "category",
      data: ranking.map((item) => item.elderlyName),
      axisLabel: { color: "#9bb4d0" },
    },
    series: [
      {
        type: "bar",
        data: ranking.map((item) => item.riskScore),
        barWidth: 18,
        itemStyle: {
          borderRadius: [0, 10, 10, 0],
          color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
            { offset: 0, color: "#ff8a65" },
            { offset: 1, color: "#ff4d6d" },
          ]),
        },
        label: { show: true, position: "right", color: "#c4d6eb" },
      },
    ],
  })
}

function initDeviceTypePie() {
  if (!deviceTypePieRef.value) return
  if (!deviceTypePieChart) deviceTypePieChart = echarts.init(deviceTypePieRef.value)
  const data = deviceTypeDistribution.value.length
    ? deviceTypeDistribution.value.map((item) => ({ value: item.value, name: getDeviceTypeLabel(item.name) }))
    : [
        { value: 1, name: "智能床垫" },
        { value: 1, name: "跌倒雷达" },
        { value: 1, name: "烟感设备" },
      ]
  deviceTypePieChart.setOption({
    backgroundColor: "transparent",
    tooltip: { trigger: "item", formatter: "{b}<br/>{c} 台 ({d}%)" },
    legend: { bottom: 0, textStyle: { color: "#9bb4d0" } },
    series: [
      {
        type: "pie",
        radius: ["42%", "72%"],
        center: ["50%", "44%"],
        itemStyle: { borderRadius: 8, borderColor: "#051423", borderWidth: 2 },
        label: { show: true, color: "#d7e7f8", formatter: "{b}\n{c}台" },
        data,
      },
    ],
  })
}

function initCharts() {
  initActivityHeatmap()
  initVitalsWave()
  initAlertDashboard()
  initRiskRanking()
  initDeviceTypePie()
}

function resizeCharts() {
  activityHeatmapChart?.resize()
  vitalsWaveChart?.resize()
  alertDashboardChart?.resize()
  riskRankingChart?.resize()
  deviceTypePieChart?.resize()
}

function disposeCharts() {
  activityHeatmapChart?.dispose()
  vitalsWaveChart?.dispose()
  alertDashboardChart?.dispose()
  riskRankingChart?.dispose()
  deviceTypePieChart?.dispose()
  activityHeatmapChart = null
  vitalsWaveChart = null
  alertDashboardChart = null
  riskRankingChart = null
  deviceTypePieChart = null
}

function clearAutoRefresh() {
  if (autoRefreshTimer) {
    clearInterval(autoRefreshTimer)
    autoRefreshTimer = null
  }
}

function setupAutoRefresh() {
  clearAutoRefresh()
  if (!autoRefreshEnabled.value) return
  autoRefreshTimer = setInterval(() => loadScreen(false), 30000)
}

function toggleAutoRefresh() {
  autoRefreshEnabled.value = !autoRefreshEnabled.value
  setupAutoRefresh()
}

async function toggleFullscreen() {
  if (!document.fullscreenElement) {
    await document.documentElement.requestFullscreen?.()
    isFullscreen.value = true
  } else {
    await document.exitFullscreen?.()
    isFullscreen.value = false
  }
}

function startClock() {
  clockTimer = setInterval(() => {
    currentTime.value = parseTime(new Date(), "{y}-{m}-{d} {h}:{i}:{s}")
  }, 1000)
}

async function loadScreen(showLoading = true) {
  if (showLoading) loading.value = true
  try {
    const [overviewResponse, sensorResponse, alertResponse, deviceResponse] = await Promise.all([
      getOverviewData(),
      listSensorLog({ pageNum: 1, pageSize: 200 }),
      listAlertRecord({ pageNum: 1, pageSize: 200 }),
      listDevice({ pageNum: 1, pageSize: 200 }),
    ])
    const data = overviewResponse.data || {}
    coreStats.value = data.coreStats || {}
    elderlyWatchList.value = data.elderlyWatchList || []
    recentAlerts.value = data.recentAlerts || []
    pendingOrders.value = data.pendingOrders || []
    sensorLogs.value = sensorResponse.rows || []
    alertTrendSource.value = alertResponse.rows || []
    deviceList.value = deviceResponse.rows || []
    await nextTick()
    initCharts()
  } finally {
    loading.value = false
  }
}

window.addEventListener("resize", resizeCharts)
document.addEventListener("fullscreenchange", () => {
  isFullscreen.value = !!document.fullscreenElement
  resizeCharts()
})

onBeforeUnmount(() => {
  window.removeEventListener("resize", resizeCharts)
  clearAutoRefresh()
  if (clockTimer) clearInterval(clockTimer)
  disposeCharts()
})

loadScreen()
setupAutoRefresh()
startClock()
</script>

<style scoped>
.screen-page {
  min-height: 100vh;
  padding: 18px;
  color: #e6f1ff;
  background:
    radial-gradient(circle at top left, rgba(36, 195, 255, 0.18), transparent 22%),
    radial-gradient(circle at right center, rgba(255, 107, 107, 0.12), transparent 20%),
    linear-gradient(180deg, #04111d 0%, #071a2f 52%, #051423 100%);
}

.screen-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding: 16px 22px;
  border: 1px solid rgba(62, 112, 160, 0.35);
  border-radius: 22px;
  background: linear-gradient(135deg, rgba(14, 36, 60, 0.96), rgba(7, 23, 41, 0.9));
  box-shadow: 0 18px 48px rgba(2, 8, 15, 0.45);
}

.screen-subtitle {
  font-size: 12px;
  letter-spacing: 0.2em;
  color: #6ec8ff;
}

.screen-header h1 {
  margin: 8px 0 0;
  font-size: 34px;
  font-weight: 700;
}

.screen-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.screen-time {
  min-width: 210px;
  font-size: 22px;
  font-weight: 700;
  color: #6ec8ff;
}

.screen-stats {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 16px;
}

.screen-stat-card {
  padding: 18px 20px;
  border: 1px solid rgba(62, 112, 160, 0.32);
  border-radius: 20px;
  background: linear-gradient(180deg, rgba(11, 29, 49, 0.96), rgba(8, 23, 40, 0.9));
}

.screen-stat-label {
  font-size: 14px;
  color: #8eb6d8;
}

.screen-stat-value {
  margin-top: 10px;
  font-size: 30px;
  font-weight: 700;
  color: #f4fbff;
}

.screen-stat-value.warning {
  color: #fac858;
}

.screen-stat-value.danger {
  color: #ff7a7a;
}

.screen-stat-desc {
  margin-top: 8px;
  font-size: 12px;
  color: #6f8cab;
}

.screen-main {
  display: grid;
  grid-template-columns: 1.1fr 1fr 0.9fr;
  gap: 16px;
  margin-bottom: 16px;
}

.screen-left,
.screen-right {
  display: grid;
  gap: 16px;
}

.screen-panel {
  border: 1px solid rgba(62, 112, 160, 0.32);
  border-radius: 22px;
}

.dark-panel {
  background: linear-gradient(180deg, rgba(11, 29, 49, 0.96), rgba(8, 23, 40, 0.9));
  box-shadow: 0 20px 50px rgba(2, 8, 15, 0.35);
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-weight: 600;
  color: #d9edff;
}

.panel-note {
  color: #7896b7;
  font-size: 12px;
}

.screen-chart {
  width: 100%;
  height: 300px;
}

.tall-chart {
  height: 620px;
}

.medium-chart {
  height: 302px;
}

.screen-bottom {
  display: grid;
  grid-template-columns: 1.8fr 1fr;
  gap: 16px;
}

.screen-side-table {
  display: grid;
  gap: 16px;
}

.elderly-screen-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.elderly-screen-card {
  padding: 14px;
  border-radius: 18px;
  background: linear-gradient(180deg, rgba(9, 40, 67, 0.88), rgba(7, 29, 49, 0.8));
  border: 1px solid rgba(66, 118, 168, 0.28);
}

.elderly-screen-top {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}

.elderly-screen-name {
  font-size: 18px;
  font-weight: 700;
}

.elderly-screen-address {
  margin-top: 5px;
  font-size: 13px;
  color: #7fa0c0;
}

.elderly-screen-metrics {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
  margin-top: 14px;
  color: #c1dbf5;
  font-size: 14px;
}

.elderly-screen-footer {
  margin-top: 14px;
  color: #83a2c2;
  font-size: 12px;
}

:deep(.el-card__header) {
  border-bottom: 1px solid rgba(62, 112, 160, 0.18);
}

:deep(.el-table) {
  --el-table-border-color: rgba(56, 91, 130, 0.45);
  --el-table-header-bg-color: rgba(13, 36, 60, 0.86);
  --el-table-tr-bg-color: rgba(7, 23, 41, 0.2);
  --el-table-row-hover-bg-color: rgba(28, 71, 115, 0.35);
  --el-table-bg-color: transparent;
  --el-table-text-color: #d9edff;
  --el-table-header-text-color: #8eb6d8;
}

@media (max-width: 1600px) {
  .screen-main,
  .screen-bottom,
  .screen-stats {
    grid-template-columns: 1fr;
  }

  .tall-chart {
    height: 340px;
  }
}
</style>
