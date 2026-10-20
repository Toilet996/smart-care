<template>
  <div class="app-container">
    <el-row :gutter="12" class="mb12">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="summary-card">
          <div class="summary-label">报告总数</div>
          <div class="summary-value">{{ total }}</div>
          <div class="summary-tip">当前筛选条件下的日报与周报</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="summary-card">
          <div class="summary-label">平均睡眠分</div>
          <div class="summary-value">{{ avgSleepScore }}</div>
          <div class="summary-tip">帮助判断睡眠质量趋势</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="summary-card">
          <div class="summary-label">平均活力分</div>
          <div class="summary-value">{{ avgActivityScore }}</div>
          <div class="summary-tip">反映日常活动与精神状态</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="summary-card">
          <div class="summary-label">高风险报告</div>
          <div class="summary-value danger">{{ highRiskCount }}</div>
          <div class="summary-tip">综合评分低于 60 的重点关注对象</div>
        </el-card>
      </el-col>
    </el-row>

    <el-form
      ref="queryRef"
      :model="queryParams"
      :inline="true"
      v-show="showSearch"
      label-width="88px"
    >
      <el-form-item label="老人姓名" prop="elderlyName">
        <el-input
          v-model="queryParams.elderlyName"
          placeholder="请输入老人姓名"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="报告类型" prop="reportType">
        <el-select v-model="queryParams.reportType" placeholder="请选择报告类型" clearable>
          <el-option v-for="dict in biz_report_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="报告日期" prop="reportDate">
        <el-date-picker
          v-model="queryParams.reportDate"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择报告日期"
          clearable
        />
      </el-form-item>
      <el-form-item label="最低睡眠分" prop="sleepScore">
        <el-input v-model.number="queryParams.sleepScore" placeholder="请输入最低睡眠分" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="最低活力分" prop="activityScore">
        <el-input
          v-model.number="queryParams.activityScore"
          placeholder="请输入最低活力分"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['care:healthReport:add']">
          新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate()"
          v-hasPermi="['care:healthReport:edit']"
        >
          修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete()"
          v-hasPermi="['care:healthReport:remove']"
        >
          删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['care:healthReport:export']"
        >
          导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="healthReportList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="报告ID" align="center" prop="reportId" width="90" />
      <el-table-column label="老人姓名" align="center" prop="elderlyName" min-width="120">
        <template #default="scope">
          <el-button link type="primary" @click="goToElderly(scope.row)">
            {{ scope.row.elderlyName }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="报告类型" align="center" prop="reportType" width="100">
        <template #default="scope">
          <dict-tag :options="biz_report_type" :value="scope.row.reportType" />
        </template>
      </el-table-column>
      <el-table-column label="报告日期" align="center" prop="reportDate" width="120">
        <template #default="scope">
          <span>{{ parseTime(scope.row.reportDate, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="睡眠分" align="center" width="90">
        <template #default="scope">
          <el-tag :type="scoreTagType(scope.row.sleepScore)">{{ scope.row.sleepScore ?? "-" }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="活力分" align="center" width="90">
        <template #default="scope">
          <el-tag :type="scoreTagType(scope.row.activityScore)">{{ scope.row.activityScore ?? "-" }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="综合评分" align="center" width="96">
        <template #default="scope">
          <span>{{ getTotalScore(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="健康等级" align="center" width="110">
        <template #default="scope">
          <el-tag :type="healthLevelTagType(scope.row)">
            {{ getHealthLevel(scope.row) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="AI评估结论" align="center" prop="aiEvaluation" min-width="260" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ scope.row.aiEvaluation || "暂无评估结论" }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="220">
        <template #default="scope">
          <el-button link type="primary" @click="goToElderly(scope.row)">老人档案</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['care:healthReport:edit']">
            修改
          </el-button>
          <el-button
            link
            type="primary"
            icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['care:healthReport:remove']"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" v-model="open" width="620px" append-to-body>
      <el-form ref="healthReportRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="老人ID" prop="elderlyId">
          <el-input v-model.number="form.elderlyId" placeholder="请输入老人ID" />
        </el-form-item>
        <el-form-item label="老人姓名" prop="elderlyName">
          <el-input v-model="form.elderlyName" placeholder="请输入老人姓名" />
        </el-form-item>
        <el-form-item label="报告类型" prop="reportType">
          <el-select v-model="form.reportType" placeholder="请选择报告类型">
            <el-option v-for="dict in biz_report_type" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="报告日期" prop="reportDate">
          <el-date-picker
            v-model="form.reportDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择报告日期"
            clearable
          />
        </el-form-item>
        <el-form-item label="睡眠分" prop="sleepScore">
          <el-input v-model.number="form.sleepScore" placeholder="请输入睡眠分" />
        </el-form-item>
        <el-form-item label="活力分" prop="activityScore">
          <el-input v-model.number="form.activityScore" placeholder="请输入活力分" />
        </el-form-item>
        <el-form-item label="AI评估结论" prop="aiEvaluation">
          <el-input
            v-model="form.aiEvaluation"
            type="textarea"
            :rows="4"
            placeholder="请输入AI健康综合评估，建议写明睡眠、活动和风险结论"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="HealthReport">
import { computed } from "vue"
import { addHealthReport, delHealthReport, getHealthReport, listHealthReport, updateHealthReport } from "@/api/care/healthReport"
import { parseTime } from "@/utils/ruoyi"

const { proxy } = getCurrentInstance()
const route = useRoute()
const router = useRouter()
const { biz_report_type } = proxy.useDict("biz_report_type")

const healthReportList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    elderlyId: null,
    elderlyName: null,
    reportType: null,
    reportDate: null,
    sleepScore: null,
    activityScore: null
  },
  rules: {
    elderlyId: [{ required: true, message: "老人ID不能为空", trigger: "blur" }],
    elderlyName: [{ required: true, message: "老人姓名不能为空", trigger: "blur" }],
    reportType: [{ required: true, message: "请选择报告类型", trigger: "change" }],
    reportDate: [{ required: true, message: "请选择报告日期", trigger: "change" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

const avgSleepScore = computed(() => calculateAverage("sleepScore"))
const avgActivityScore = computed(() => calculateAverage("activityScore"))
const highRiskCount = computed(() => healthReportList.value.filter((item) => getTotalScore(item) < 60).length)

function calculateAverage(field) {
  const validValues = healthReportList.value
    .map((item) => item[field])
    .filter((value) => typeof value === "number" && !Number.isNaN(value))
  if (!validValues.length) {
    return "-"
  }
  const totalValue = validValues.reduce((sum, current) => sum + current, 0)
  return Math.round(totalValue / validValues.length)
}

function getTotalScore(row) {
  const sleepScore = typeof row?.sleepScore === "number" ? row.sleepScore : null
  const activityScore = typeof row?.activityScore === "number" ? row.activityScore : null
  if (sleepScore === null && activityScore === null) {
    return "-"
  }
  if (sleepScore === null) {
    return activityScore
  }
  if (activityScore === null) {
    return sleepScore
  }
  return Math.round((sleepScore + activityScore) / 2)
}

function getHealthLevel(row) {
  const totalScore = getTotalScore(row)
  if (typeof totalScore !== "number") {
    return "待评估"
  }
  if (totalScore >= 85) {
    return "良好"
  }
  if (totalScore >= 60) {
    return "关注"
  }
  return "高风险"
}

function healthLevelTagType(row) {
  const level = getHealthLevel(row)
  if (level === "良好") {
    return "success"
  }
  if (level === "关注") {
    return "warning"
  }
  if (level === "高风险") {
    return "danger"
  }
  return "info"
}

function scoreTagType(score) {
  if (typeof score !== "number") {
    return "info"
  }
  if (score >= 85) {
    return "success"
  }
  if (score >= 60) {
    return "warning"
  }
  return "danger"
}

function getList() {
  loading.value = true
  listHealthReport(queryParams.value)
    .then((response) => {
      healthReportList.value = response.rows
      total.value = response.total
    })
    .finally(() => {
      loading.value = false
    })
}

function reset() {
  form.value = {
    reportId: null,
    elderlyId: null,
    elderlyName: null,
    reportType: "0",
    reportDate: parseTime(new Date(), "{y}-{m}-{d}"),
    sleepScore: null,
    activityScore: null,
    aiEvaluation: null
  }
  proxy.resetForm("healthReportRef")
}

function cancel() {
  open.value = false
  reset()
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  applyRouteQuery()
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.reportId)
  single.value = selection.length !== 1
  multiple.value = selection.length === 0
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "新增健康评估报告"
}

function handleUpdate(row) {
  const reportId = row?.reportId || ids.value[0]
  if (!reportId) {
    proxy.$modal.msgWarning("请先选择要修改的数据")
    return
  }
  reset()
  getHealthReport(reportId).then((response) => {
    form.value = { ...response.data }
    open.value = true
    title.value = "修改健康评估报告"
  })
}

function submitForm() {
  proxy.$refs.healthReportRef.validate((valid) => {
    if (!valid) {
      return
    }
    const request = form.value.reportId ? updateHealthReport(form.value) : addHealthReport(form.value)
    request.then(() => {
      proxy.$modal.msgSuccess(form.value.reportId ? "修改成功" : "新增成功")
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const reportIds = row?.reportId ? [row.reportId] : ids.value
  if (!reportIds.length) {
    proxy.$modal.msgWarning("请先选择要删除的数据")
    return
  }
  proxy.$modal
    .confirm(`是否确认删除健康评估报告编号为“${reportIds.join(",")}”的数据项？`)
    .then(() => delHealthReport(reportIds.join(",")))
    .then(() => {
      proxy.$modal.msgSuccess("删除成功")
      getList()
    })
    .catch(() => {})
}

function handleExport() {
  proxy.download("care/healthReport/export", { ...queryParams.value }, `healthReport_${new Date().getTime()}.xlsx`)
}

function goToElderly(row) {
  router.push({
    path: "/smartcare/elderly",
    query: {
      elderlyName: row.elderlyName
    }
  })
}

function applyRouteQuery() {
  queryParams.value.elderlyId = route.query.elderlyId ?? null
  queryParams.value.elderlyName = route.query.elderlyName ?? null
}

watch(
  () => route.query,
  () => {
    applyRouteQuery()
    getList()
  },
  { deep: true }
)

reset()
applyRouteQuery()
getList()
</script>

<style scoped>
.mb12 {
  margin-bottom: 12px;
}

.summary-card {
  border-radius: 10px;
}

.summary-label {
  color: #606266;
  font-size: 14px;
}

.summary-value {
  margin-top: 10px;
  color: #303133;
  font-size: 28px;
  font-weight: 700;
}

.summary-value.danger {
  color: #f56c6c;
}

.summary-tip {
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
}
</style>
