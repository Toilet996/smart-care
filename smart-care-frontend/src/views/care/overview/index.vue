<template>
  <div class="overview-page" v-loading="loading">
    <section class="hero-panel">
      <div class="hero-copy">
        <div class="eyebrow">Community Smart Care</div>
        <h1>社区预警态势总览</h1>
        <p>
          用一张图集中观察辖区老人状态、24小时活动轨迹、生命体征波形和预警处置进展，
          让社区工作人员先看到风险，再快速联动预警、工单和随访。
        </p>
      </div>
      <div class="hero-actions">
        <el-button type="primary" @click="goTo('/smartcare/alertRecord')">预警中心</el-button>
        <el-button @click="goTo('/smartcare/workOrder')">工单中心</el-button>
        <el-button @click="goTo('/smartcare/elderly')">老人档案</el-button>
        <el-button @click="toggleAutoRefresh">{{ autoRefreshEnabled ? "暂停轮播" : "自动轮播" }}</el-button>
        <el-button @click="toggleFullscreen">{{ isFullscreen ? "退出全屏" : "全屏展示" }}</el-button>
      </div>
    </section>

    <section class="stats-grid">
      <div v-for="item in statCards" :key="item.key" class="stat-card">
        <div class="stat-label">{{ item.label }}</div>
        <div class="stat-value" :class="item.highlight">{{ item.value }}</div>
        <div class="stat-desc">{{ item.desc }}</div>
      </div>
    </section>

    <section class="chart-grid">
      <el-card shadow="hover" class="panel-card chart-card">
        <template #header>
          <div class="panel-header">
            <span>{{ focusElderlyName }} 24小时活动轨迹热力图</span>
            <span class="panel-note">近7天按小时聚合</span>
          </div>
        </template>
        <div ref="activityHeatmapRef" class="chart-container"></div>
      </el-card>

      <el-card shadow="hover" class="panel-card chart-card">
        <template #header>
          <div class="panel-header">
            <span>{{ focusElderlyName }} 生命体征实时波形</span>
            <span class="panel-note">心率 / 呼吸频率</span>
          </div>
        </template>
        <div ref="vitalsWaveRef" class="chart-container"></div>
      </el-card>

      <el-card shadow="hover" class="panel-card chart-card">
        <template #header>
          <div class="panel-header">
            <span>社区预警态势大屏</span>
            <span class="panel-note">近7天分级预警趋势</span>
          </div>
        </template>
        <div ref="alertDashboardRef" class="chart-container"></div>
      </el-card>
    </section>

    <section class="insight-grid">
      <el-card shadow="hover" class="panel-card chart-card">
        <template #header>
          <div class="panel-header">
            <span>高风险老人排行</span>
            <span class="panel-note">按未结工单、预警数、最新等级综合排序</span>
          </div>
        </template>
        <div ref="riskRankingRef" class="chart-container chart-container-small"></div>
      </el-card>

      <el-card shadow="hover" class="panel-card chart-card">
        <template #header>
          <div class="panel-header">
            <span>设备类型分布</span>
            <span class="panel-note">当前已接入设备结构</span>
          </div>
        </template>
        <div ref="deviceTypePieRef" class="chart-container chart-container-small"></div>
      </el-card>
    </section>

    <section class="main-grid">
      <el-card shadow="hover" class="panel-card">
        <template #header>
          <div class="panel-header">
            <span>重点老人监控</span>
            <el-button link type="primary" @click="goTo('/smartcare/elderly')">查看全部</el-button>
          </div>
        </template>
        <div class="elderly-grid">
          <div v-for="item in elderlyWatchList" :key="item.elderlyId" class="elderly-card">
            <div class="elderly-top">
              <div>
                <div class="elderly-name">{{ item.elderlyName || "--" }}</div>
                <div class="elderly-address">{{ item.address || "未维护地址" }}</div>
              </div>
              <el-tag :type="alertLevelTagType(item.latestAlertLevel)" effect="light">
                {{ latestAlertLabel(item) }}
              </el-tag>
            </div>
            <div class="elderly-metrics">
              <div>
                <span class="metric-label">设备在线</span>
                <span class="metric-value">{{ item.onlineDeviceCount }}/{{ item.deviceCount }}</span>
              </div>
              <div>
                <span class="metric-label">未结工单</span>
                <span class="metric-value danger">{{ item.pendingOrderCount || 0 }}</span>
              </div>
              <div>
                <span class="metric-label">预警总数</span>
                <span class="metric-value">{{ item.alertCount || 0 }}</span>
              </div>
              <div>
                <span class="metric-label">最新评分</span>
                <span class="metric-value">{{ item.latestHealthScore ?? "--" }}</span>
              </div>
            </div>
            <div class="elderly-footer">
              <span>最近预警：{{ formatDateTime(item.latestAlertTime) }}</span>
              <el-button link type="primary" @click="goToElderly(item)">查看档案</el-button>
            </div>
          </div>
        </div>
      </el-card>

      <div class="side-panels">
        <el-card shadow="hover" class="panel-card">
          <template #header>
            <div class="panel-header">
              <span>预警等级分布</span>
            </div>
          </template>
          <div class="distribution-list">
            <div v-for="item in alertLevelStatsDisplay" :key="item.value" class="distribution-item">
              <div class="distribution-left">
                <el-tag :type="alertLevelTagType(item.value)" effect="light">{{ item.label }}</el-tag>
              </div>
              <div class="distribution-right">{{ item.count }}</div>
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" class="panel-card">
          <template #header>
            <div class="panel-header">
              <span>设备在线态势</span>
            </div>
          </template>
          <div class="distribution-list">
            <div v-for="item in deviceStatusStatsDisplay" :key="item.value" class="distribution-item">
              <div class="distribution-left">
                <el-tag :type="deviceStatusTagType(item.value)" effect="light">{{ item.label }}</el-tag>
              </div>
              <div class="distribution-right">{{ item.count }}</div>
            </div>
          </div>
        </el-card>
      </div>
    </section>

    <section class="table-grid">
      <el-card shadow="hover" class="panel-card">
        <template #header>
          <div class="panel-header">
            <span>最新预警</span>
            <el-button link type="primary" @click="goTo('/smartcare/alertRecord')">更多</el-button>
          </div>
        </template>
        <el-table :data="recentAlerts" size="small">
          <el-table-column label="老人" min-width="90">
            <template #default="{ row }">
              <el-button link type="primary" @click="goToAlert(row)">{{ row.elderlyName }}</el-button>
            </template>
          </el-table-column>
          <el-table-column label="等级" width="90">
            <template #default="{ row }">
              <el-tag :type="alertLevelTagType(row.alertLevel)" effect="light">
                {{ getAlertLevelLabel(row.alertLevel) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="类型" prop="alertType" min-width="110" />
          <el-table-column label="时间" min-width="150">
            <template #default="{ row }">
              {{ formatDateTime(row.createTime) }}
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card shadow="hover" class="panel-card">
        <template #header>
          <div class="panel-header">
            <span>待处理工单</span>
            <el-button link type="primary" @click="goTo('/smartcare/workOrder')">更多</el-button>
          </div>
        </template>
        <el-table :data="pendingOrders" size="small">
          <el-table-column label="工单ID" width="90">
            <template #default="{ row }">
              <el-button link type="primary" @click="goToOrder(row)">{{ row.orderId }}</el-button>
            </template>
          </el-table-column>
          <el-table-column label="老人" prop="elderlyName" min-width="90" />
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="orderStatusTagType(row.status)" effect="light">{{ getOrderStatusLabel(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" min-width="150">
            <template #default="{ row }">
              {{ formatDateTime(row.createTime) }}
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card shadow="hover" class="panel-card">
        <template #header>
          <div class="panel-header">
            <span>最新健康报告</span>
            <el-button link type="primary" @click="goTo('/smartcare/healthReport')">更多</el-button>
          </div>
        </template>
        <el-table :data="latestReports" size="small">
          <el-table-column label="老人" min-width="90">
            <template #default="{ row }">
              <el-button link type="primary" @click="goToReport(row)">{{ row.elderlyName }}</el-button>
            </template>
          </el-table-column>
          <el-table-column label="类型" width="80">
            <template #default="{ row }">
              <dict-tag :options="biz_report_type" :value="row.reportType" />
            </template>
          </el-table-column>
          <el-table-column label="评分" width="80" prop="totalScore" />
          <el-table-column label="日期" min-width="120">
            <template #default="{ row }">
              {{ formatDate(row.reportDate) }}
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </section>
  </div>
</template>

<script setup name="SmartcareOverview">
import { computed, getCurrentInstance, nextTick, onBeforeUnmount, ref } from "vue"
import { useRouter } from "vue-router"
import * as echarts from "echarts"
import { parseTime } from "@/utils/ruoyi"
import { getOverviewData } from "@/api/care/overview"
import { listSensorLog } from "@/api/iot/sensorLog"
import { listAlertRecord } from "@/api/care/alertRecord"
import { listDevice } from "@/api/iot/device"

const { proxy } = getCurrentInstance()
const router = useRouter()
const { biz_alert_level, biz_device_status, biz_report_type } = proxy.useDict(
  "biz_alert_level",
  "biz_device_status",
  "biz_report_type"
)

const loading = ref(false)
const coreStats = ref({})
const alertLevelStats = ref([])
const deviceStatusStats = ref([])
const elderlyWatchList = ref([])
const recentAlerts = ref([])
const pendingOrders = ref([])
const latestReports = ref([])
const sensorLogs = ref([])
const alertTrendSource = ref([])
const deviceList = ref([])
const autoRefreshEnabled = ref(true)
const isFullscreen = ref(false)

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
  {
    key: "elderlyTotal",
    label: "辖区老人",
    value: coreStats.value.elderlyTotal ?? 0,
    desc: "当前已纳入监护的老人档案总数",
    highlight: "",
  },
  {
    key: "onlineDeviceTotal",
    label: "在线设备",
    value: coreStats.value.onlineDeviceTotal ?? 0,
    desc: "当前处于在线状态的设备数量",
    highlight: "",
  },
  {
    key: "todayAlertTotal",
    label: "今日预警",
    value: coreStats.value.todayAlertTotal ?? 0,
    desc: "当天自动识别并生成的预警记录",
    highlight: "warning",
  },
  {
    key: "urgentAlertTotal",
    label: "紧急预警",
    value: coreStats.value.urgentAlertTotal ?? 0,
    desc: "当天紧急级别预警，需优先处置",
    highlight: "danger",
  },
  {
    key: "pendingOrderTotal",
    label: "待办工单",
    value: coreStats.value.pendingOrderTotal ?? 0,
    desc: "待接单和处理中工单总数",
    highlight: "danger",
  },
  {
    key: "weekVisitTotal",
    label: "近7天随访",
    value: coreStats.value.weekVisitTotal ?? 0,
    desc: "社区端最近7天完成的随访次数",
    highlight: "",
  },
])

const alertLevelStatsDisplay = computed(() => enrichStats(alertLevelStats.value, biz_alert_level.value))
const deviceStatusStatsDisplay = computed(() => enrichStats(deviceStatusStats.value, biz_device_status.value))
const riskRankingList = computed(() => {
  return [...elderlyWatchList.value]
    .map((item) => {
      const latestLevel = Number(item.latestAlertLevel ?? -1)
      const healthPenalty = item.latestHealthScore == null ? 0 : Math.max(0, 100 - Number(item.latestHealthScore))
      const riskScore =
        Number(item.pendingOrderCount || 0) * 12 +
        Number(item.alertCount || 0) * 4 +
        Math.max(0, latestLevel) * 18 +
        healthPenalty * 0.2
      return {
        ...item,
        riskScore: Math.round(riskScore),
      }
    })
    .sort((a, b) => b.riskScore - a.riskScore)
    .slice(0, 6)
})

const deviceTypeDistribution = computed(() => {
  const map = new Map()
  deviceList.value.forEach((item) => {
    const key = item.deviceType || "unknown"
    map.set(key, (map.get(key) || 0) + 1)
  })
  return Array.from(map.entries()).map(([name, value]) => ({ name, value }))
})

function enrichStats(list, dictOptions) {
  return (Array.isArray(list) ? list : []).map((item) => {
    const matched = (dictOptions || []).find((dict) => dict.value === String(item.value))
    return {
      ...item,
      label: matched?.label || item.value,
      count: Number(item.count || 0),
    }
  })
}

function getAlertLevelLabel(value) {
  return biz_alert_level.value.find((item) => item.value === String(value))?.label || value || "--"
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

function deviceStatusTagType(value) {
  if (String(value) === "0") return "success"
  if (String(value) === "1") return "info"
  return "danger"
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

function formatDate(value) {
  return value ? parseTime(value, "{y}-{m}-{d}") : "--"
}

function formatDateTime(value) {
  return value ? parseTime(value, "{y}-{m}-{d} {h}:{i}") : "--"
}

function goTo(path) {
  router.push(path)
}

function goToElderly(row) {
  router.push({
    path: "/smartcare/elderly",
    query: {
      elderlyId: row.elderlyId,
      elderlyName: row.elderlyName,
    },
  })
}

function goToAlert(row) {
  router.push({
    path: "/smartcare/alertRecord",
    query: {
      alertId: row.alertId,
      elderlyId: row.elderlyId,
      elderlyName: row.elderlyName,
    },
  })
}

function goToOrder(row) {
  router.push({
    path: "/smartcare/workOrder",
    query: {
      orderId: row.orderId,
      alertId: row.alertId,
      elderlyId: row.elderlyId,
      elderlyName: row.elderlyName,
    },
  })
}

function goToReport(row) {
  router.push({
    path: "/smartcare/healthReport",
    query: {
      elderlyId: row.elderlyId,
      elderlyName: row.elderlyName,
    },
  })
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
  const matrix = dayKeys.flatMap((dayKey, dayIndex) =>
    hours.map((_, hourIndex) => [hourIndex, dayIndex, 0])
  )

  const focusLogs = sensorLogs.value.filter((log) => {
    if (!focusElderlyId.value) return true
    return Number(log.elderlyId) === Number(focusElderlyId.value)
  })

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
      const score = normalizeMetricValue(log)
      nextValue = Math.max(currentValue, score >= 110 ? 4 : 2)
    } else if (log.metricType === "breath_rate") {
      const score = normalizeMetricValue(log)
      nextValue = Math.max(currentValue, score >= 22 ? 3 : 2)
    } else if (log.metricType === "leave_bed") {
      nextValue = Math.max(currentValue, log.metricValue === "off_bed" ? 5 : 1)
    } else if (log.metricType === "smoke_value") {
      const score = normalizeMetricValue(log)
      nextValue = Math.max(currentValue, score >= 50 ? 5 : 2)
    }
    matrix[matrixIndex][2] = nextValue
  })

  const hasData = matrix.some((item) => item[2] > 0)
  if (!hasData) {
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
    if (!timelineMap.has(key)) {
      timelineMap.set(key, { heartRate: null, breathRate: null })
    }
    if (log.metricType === "heart_rate") {
      timelineMap.get(key).heartRate = Number(log.metricValue)
    }
    if (log.metricType === "breath_rate") {
      timelineMap.get(key).breathRate = Number(log.metricValue)
    }
  })

  const timeLabels = Array.from(timelineMap.keys())
  const heartSeries = timeLabels.map((key) => timelineMap.get(key).heartRate)
  const breathSeries = timeLabels.map((key) => timelineMap.get(key).breathRate)
  return { timeLabels, heartSeries, breathSeries }
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

  if (total.every((value) => value === 0)) {
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
  if (!activityHeatmapChart) {
    activityHeatmapChart = echarts.init(activityHeatmapRef.value)
  }
  const { dayLabels, hours, matrix } = buildHeatmapSeries()
  activityHeatmapChart.setOption({
    tooltip: {
      position: "top",
      formatter: (params) => `${dayLabels[params.value[1]]} ${hours[params.value[0]]}<br/>活跃度：${params.value[2]}`
    },
    grid: {
      left: 52,
      right: 16,
      top: 16,
      bottom: 28,
    },
    xAxis: {
      type: "category",
      data: hours,
      splitArea: { show: true },
      axisLabel: { color: "#6b7a90", interval: 1, rotate: 45 },
    },
    yAxis: {
      type: "category",
      data: dayLabels,
      splitArea: { show: true },
      axisLabel: { color: "#6b7a90" },
    },
    visualMap: {
      min: 0,
      max: 5,
      orient: "horizontal",
      left: "center",
      bottom: 0,
      text: ["高活跃", "低活跃"],
      inRange: {
        color: ["#eaf4ff", "#9dd3ff", "#409eff", "#ffb74d", "#f56c6c"],
      },
    },
    series: [
      {
        name: "活动轨迹",
        type: "heatmap",
        data: matrix,
        label: { show: false },
        emphasis: {
          itemStyle: {
            shadowBlur: 8,
            shadowColor: "rgba(0, 0, 0, 0.25)",
          },
        },
      },
    ],
  })
}

function initVitalsWave() {
  if (!vitalsWaveRef.value) return
  if (!vitalsWaveChart) {
    vitalsWaveChart = echarts.init(vitalsWaveRef.value)
  }
  const { timeLabels, heartSeries, breathSeries } = buildWaveData()
  vitalsWaveChart.setOption({
    tooltip: { trigger: "axis" },
    legend: {
      top: 0,
      textStyle: { color: "#5d6b7a" },
    },
    grid: {
      left: 40,
      right: 20,
      top: 40,
      bottom: 24,
    },
    xAxis: {
      type: "category",
      data: timeLabels,
      boundaryGap: false,
      axisLabel: { color: "#6b7a90" },
    },
    yAxis: [
      {
        type: "value",
        name: "心率",
        axisLabel: { color: "#6b7a90" },
        splitLine: { lineStyle: { color: "#edf2f7" } },
      },
      {
        type: "value",
        name: "呼吸",
        axisLabel: { color: "#6b7a90" },
        splitLine: { show: false },
      },
    ],
    series: [
      {
        name: "心率",
        type: "line",
        smooth: true,
        symbol: "circle",
        symbolSize: 8,
        lineStyle: { width: 3, color: "#f56c6c" },
        areaStyle: { color: "rgba(245, 108, 108, 0.12)" },
        data: heartSeries,
      },
      {
        name: "呼吸频率",
        type: "line",
        smooth: true,
        yAxisIndex: 1,
        symbol: "circle",
        symbolSize: 8,
        lineStyle: { width: 3, color: "#409eff" },
        areaStyle: { color: "rgba(64, 158, 255, 0.12)" },
        data: breathSeries,
      },
    ],
  })
}

function initAlertDashboard() {
  if (!alertDashboardRef.value) return
  if (!alertDashboardChart) {
    alertDashboardChart = echarts.init(alertDashboardRef.value)
  }
  const trend = buildAlertTrendData()
  alertDashboardChart.setOption({
    tooltip: { trigger: "axis" },
    legend: {
      top: 0,
      textStyle: { color: "#5d6b7a" },
    },
    grid: {
      left: 40,
      right: 20,
      top: 42,
      bottom: 24,
    },
    xAxis: {
      type: "category",
      data: trend.labels,
      axisLabel: { color: "#6b7a90" },
    },
    yAxis: [
      {
        type: "value",
        name: "预警数",
        axisLabel: { color: "#6b7a90" },
        splitLine: { lineStyle: { color: "#edf2f7" } },
      },
    ],
    series: [
      {
        name: "提示级",
        type: "bar",
        stack: "alerts",
        itemStyle: { color: "#91cc75" },
        data: trend.level0,
      },
      {
        name: "重要级",
        type: "bar",
        stack: "alerts",
        itemStyle: { color: "#fac858" },
        data: trend.level1,
      },
      {
        name: "紧急级",
        type: "bar",
        stack: "alerts",
        itemStyle: { color: "#ee6666" },
        data: trend.level2,
      },
      {
        name: "总预警",
        type: "line",
        smooth: true,
        symbolSize: 8,
        itemStyle: { color: "#5470c6" },
        lineStyle: { width: 3 },
        data: trend.total,
      },
    ],
  })
}

function initRiskRanking() {
  if (!riskRankingRef.value) return
  if (!riskRankingChart) {
    riskRankingChart = echarts.init(riskRankingRef.value)
  }
  const ranking = riskRankingList.value
  riskRankingChart.setOption({
    tooltip: {
      trigger: "axis",
      axisPointer: { type: "shadow" },
      formatter: (params) => {
        const item = params[0]
        return `${item.name}<br/>综合风险分：${item.value}`
      },
    },
    grid: {
      left: 80,
      right: 20,
      top: 18,
      bottom: 12,
    },
    xAxis: {
      type: "value",
      axisLabel: { color: "#6b7a90" },
      splitLine: { lineStyle: { color: "#edf2f7" } },
    },
    yAxis: {
      type: "category",
      data: ranking.map((item) => item.elderlyName),
      axisLabel: { color: "#6b7a90" },
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
            { offset: 1, color: "#f56c6c" },
          ]),
        },
        label: {
          show: true,
          position: "right",
          color: "#5d6b7a",
        },
      },
    ],
  })
}

function initDeviceTypePie() {
  if (!deviceTypePieRef.value) return
  if (!deviceTypePieChart) {
    deviceTypePieChart = echarts.init(deviceTypePieRef.value)
  }
  const data = deviceTypeDistribution.value.length
    ? deviceTypeDistribution.value.map((item) => ({
        value: item.value,
        name: getDeviceTypeLabel(item.name),
      }))
    : [
        { value: 1, name: "智能床垫" },
        { value: 1, name: "跌倒雷达" },
        { value: 1, name: "烟感设备" },
      ]
  deviceTypePieChart.setOption({
    tooltip: {
      trigger: "item",
      formatter: "{b}<br/>{c} 台 ({d}%)",
    },
    legend: {
      bottom: 0,
      textStyle: { color: "#6b7a90" },
    },
    series: [
      {
        type: "pie",
        radius: ["42%", "70%"],
        center: ["50%", "46%"],
        itemStyle: {
          borderRadius: 8,
          borderColor: "#fff",
          borderWidth: 3,
        },
        label: {
          show: true,
          formatter: "{b}\n{c}台",
        },
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
  autoRefreshTimer = setInterval(() => {
    loadOverview(false)
  }, 30000)
}

function toggleAutoRefresh() {
  autoRefreshEnabled.value = !autoRefreshEnabled.value
  setupAutoRefresh()
}

async function toggleFullscreen() {
  const target = document.documentElement
  if (!document.fullscreenElement) {
    await target.requestFullscreen?.()
    isFullscreen.value = true
  } else {
    await document.exitFullscreen?.()
    isFullscreen.value = false
  }
}

async function loadOverview(showLoading = true) {
  if (showLoading) {
    loading.value = true
  }
  try {
    const [overviewResponse, sensorResponse, alertResponse, deviceResponse] = await Promise.all([
      getOverviewData(),
      listSensorLog({ pageNum: 1, pageSize: 200 }),
      listAlertRecord({ pageNum: 1, pageSize: 200 }),
      listDevice({ pageNum: 1, pageSize: 200 }),
    ])
    const data = overviewResponse.data || {}
    coreStats.value = data.coreStats || {}
    alertLevelStats.value = data.alertLevelStats || []
    deviceStatusStats.value = data.deviceStatusStats || []
    elderlyWatchList.value = data.elderlyWatchList || []
    recentAlerts.value = data.recentAlerts || []
    pendingOrders.value = data.pendingOrders || []
    latestReports.value = data.latestReports || []
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
  disposeCharts()
})

loadOverview()
setupAutoRefresh()
</script>

<style scoped>
.overview-page {
  min-height: 100%;
  padding: 20px;
  background:
    radial-gradient(circle at top right, rgba(31, 92, 143, 0.16), transparent 22%),
    linear-gradient(180deg, #f5f9ff 0%, #eef4fb 100%);
}

.hero-panel {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 18px;
  padding: 28px 30px;
  border-radius: 24px;
  background: linear-gradient(135deg, #123c66 0%, #1f5c8f 52%, #2f7dc1 100%);
  color: #fff;
  box-shadow: 0 16px 40px rgba(18, 60, 102, 0.22);
}

.hero-copy {
  max-width: 760px;
}

.eyebrow {
  font-size: 13px;
  letter-spacing: 0.12em;
  opacity: 0.82;
  text-transform: uppercase;
}

.hero-panel h1 {
  margin: 10px 0 12px;
  font-size: 34px;
  line-height: 1.2;
}

.hero-panel p {
  margin: 0;
  font-size: 15px;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.88);
}

.hero-actions {
  display: flex;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 12px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 18px;
}

.stat-card {
  padding: 20px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(216, 228, 240, 0.95);
  box-shadow: 0 12px 30px rgba(38, 78, 118, 0.08);
}

.stat-label {
  color: #6b7a90;
  font-size: 14px;
}

.stat-value {
  margin-top: 12px;
  font-size: 30px;
  font-weight: 700;
  color: #16324f;
}

.stat-value.warning {
  color: #d48806;
}

.stat-value.danger {
  color: #c44536;
}

.stat-desc {
  margin-top: 8px;
  font-size: 12px;
  line-height: 1.7;
  color: #8b97a8;
}

.chart-grid {
  display: grid;
  grid-template-columns: 1.2fr 1fr 1fr;
  gap: 16px;
  margin-bottom: 16px;
}

.insight-grid {
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 16px;
  margin-bottom: 16px;
}

.chart-card {
  overflow: hidden;
}

.chart-container {
  width: 100%;
  height: 320px;
}

.chart-container-small {
  height: 280px;
}

.main-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
  margin-bottom: 16px;
}

.side-panels {
  display: grid;
  gap: 16px;
}

.panel-card {
  border: none;
  border-radius: 20px;
  box-shadow: 0 12px 30px rgba(38, 78, 118, 0.08);
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-weight: 600;
  color: #16324f;
}

.panel-note {
  font-size: 12px;
  color: #8a97ab;
  font-weight: 400;
}

.elderly-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.elderly-card {
  padding: 16px;
  border-radius: 18px;
  background: linear-gradient(180deg, #fbfdff 0%, #f3f8fd 100%);
  border: 1px solid #e5edf6;
}

.elderly-top {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}

.elderly-name {
  font-size: 18px;
  font-weight: 700;
  color: #16324f;
}

.elderly-address {
  margin-top: 6px;
  font-size: 13px;
  color: #7e8ca0;
}

.elderly-metrics {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.metric-label {
  display: block;
  font-size: 12px;
  color: #8a97ab;
}

.metric-value {
  display: inline-block;
  margin-top: 6px;
  font-size: 20px;
  font-weight: 700;
  color: #16324f;
}

.metric-value.danger {
  color: #c44536;
}

.elderly-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 16px;
  font-size: 12px;
  color: #7e8ca0;
}

.distribution-list {
  display: grid;
  gap: 12px;
}

.distribution-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  border-radius: 14px;
  background: #f7fbff;
}

.distribution-right {
  font-size: 26px;
  font-weight: 700;
  color: #16324f;
}

.table-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

@media (max-width: 1400px) {
  .stats-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .chart-grid,
  .insight-grid,
  .table-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 992px) {
  .hero-panel,
  .main-grid {
    display: grid;
    grid-template-columns: 1fr;
  }

  .stats-grid,
  .elderly-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .overview-page {
    padding: 12px;
  }

  .hero-panel {
    padding: 20px;
  }

  .hero-panel h1 {
    font-size: 28px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .chart-container {
    height: 280px;
  }
}
</style>
