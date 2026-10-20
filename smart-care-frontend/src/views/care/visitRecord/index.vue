<template>
  <div class="app-container">
    <el-row :gutter="16" class="summary-row">
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card">
          <div class="summary-label">随访总数</div>
          <div class="summary-value">{{ total }}</div>
          <div class="summary-desc">累计社区上门与健康服务记录</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card">
          <div class="summary-label">近7天随访</div>
          <div class="summary-value">{{ recentVisitCount }}</div>
          <div class="summary-desc">便于查看近期服务活跃度</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card">
          <div class="summary-label">覆盖老人</div>
          <div class="summary-value">{{ elderlyCoverage }}</div>
          <div class="summary-desc">当前列表涉及的老人数量</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card">
          <div class="summary-label">最近随访</div>
          <div class="summary-value summary-time">{{ latestVisitTime || "--" }}</div>
          <div class="summary-desc">最近一次上门或回访时间</div>
        </div>
      </el-col>
    </el-row>

    <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch" label-width="82px">
      <el-form-item label="老人姓名" prop="elderlyName">
        <el-input
          v-model="queryParams.elderlyName"
          placeholder="请输入老人姓名"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="老人ID" prop="elderlyId">
        <el-input
          v-model="queryParams.elderlyId"
          placeholder="请输入老人ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="随访类型" prop="visitType">
        <el-select v-model="queryParams.visitType" placeholder="请选择随访类型" clearable>
          <el-option
            v-for="dict in biz_visit_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="随访日期" prop="visitTime">
        <el-date-picker
          v-model="queryParams.visitTime"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择随访日期"
          clearable
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['care:visitRecord:add']"
        >
          新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['care:visitRecord:edit']"
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
          @click="handleDelete"
          v-hasPermi="['care:visitRecord:remove']"
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
          v-hasPermi="['care:visitRecord:export']"
        >
          导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="visitRecordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="记录ID" align="center" prop="recordId" width="90" />
      <el-table-column label="老人姓名" align="center" min-width="120">
        <template #default="{ row }">
          <el-button link type="primary" @click="goToElderly(row)">
            {{ row.elderlyName || "--" }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="随访类型" align="center" width="120">
        <template #default="{ row }">
          <dict-tag :options="biz_visit_type" :value="row.visitType" />
        </template>
      </el-table-column>
      <el-table-column label="风险关注" align="center" width="120">
        <template #default="{ row }">
          <el-tag :type="getRiskType(row)" effect="light">
            {{ getRiskLabel(row) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="随访时间" align="center" width="180">
        <template #default="{ row }">
          <span>{{ parseTime(row.visitTime, "{y}-{m}-{d} {h}:{i}") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="服务摘要" min-width="360">
        <template #default="{ row }">
          <span>{{ getVisitSummary(row.visitContent) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="随访人员" align="center" prop="createBy" min-width="120" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template #default="{ row }">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(row)" v-hasPermi="['care:visitRecord:edit']">
            修改
          </el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(row)" v-hasPermi="['care:visitRecord:remove']">
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

    <el-dialog :title="title" v-model="open" width="680px" append-to-body>
      <el-form ref="visitRecordRef" :model="form" :rules="rules" label-width="88px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="老人ID" prop="elderlyId">
              <el-input v-model="form.elderlyId" placeholder="请输入老人ID" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="老人姓名" prop="elderlyName">
              <el-input v-model="form.elderlyName" placeholder="请输入老人姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="随访类型" prop="visitType">
              <el-select v-model="form.visitType" placeholder="请选择随访类型" style="width: 100%">
                <el-option
                  v-for="dict in biz_visit_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="随访时间" prop="visitTime">
              <el-date-picker
                v-model="form.visitTime"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="请选择随访时间"
                style="width: 100%"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="随访记录" prop="visitContent">
          <editor v-model="form.visitContent" :min-height="220" />
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

<script setup name="VisitRecord">
import { computed, getCurrentInstance, reactive, ref, toRefs } from "vue"
import { useRoute, useRouter } from "vue-router"
import { listVisitRecord, getVisitRecord, delVisitRecord, addVisitRecord, updateVisitRecord } from "@/api/care/visitRecord"

const { proxy } = getCurrentInstance()
const route = useRoute()
const router = useRouter()
const { biz_visit_type } = proxy.useDict("biz_visit_type")

const visitRecordList = ref([])
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
    elderlyId: undefined,
    elderlyName: undefined,
    visitType: undefined,
    visitTime: undefined,
  },
  rules: {
    elderlyId: [{ required: true, message: "老人ID不能为空", trigger: "blur" }],
    elderlyName: [{ required: true, message: "老人姓名不能为空", trigger: "blur" }],
    visitType: [{ required: true, message: "随访类型不能为空", trigger: "change" }],
    visitTime: [{ required: true, message: "随访时间不能为空", trigger: "change" }],
    visitContent: [{ required: true, message: "随访记录不能为空", trigger: "blur" }],
  },
})

const { queryParams, form, rules } = toRefs(data)

const recentVisitCount = computed(() => {
  const now = new Date()
  const sevenDaysAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000)
  return visitRecordList.value.filter((item) => {
    if (!item.visitTime) return false
    return new Date(item.visitTime) >= sevenDaysAgo
  }).length
})

const elderlyCoverage = computed(() => {
  const set = new Set(
    visitRecordList.value
      .map((item) => item.elderlyId)
      .filter((item) => item !== null && item !== undefined && item !== "")
  )
  return set.size
})

const latestVisitTime = computed(() => {
  if (!visitRecordList.value.length) return ""
  const sorted = [...visitRecordList.value]
    .filter((item) => item.visitTime)
    .sort((a, b) => new Date(b.visitTime) - new Date(a.visitTime))
  return sorted.length ? proxy.parseTime(sorted[0].visitTime, "{y}-{m}-{d}") : ""
})

function buildQueryFromRoute() {
  queryParams.value.elderlyId = route.query.elderlyId ? Number(route.query.elderlyId) : undefined
  queryParams.value.elderlyName = route.query.elderlyName || undefined
}

function getRiskLabel(row) {
  const content = stripHtml(row.visitContent)
  if (/跌倒|胸闷|呼吸困难|燃气|烟雾|意识|异常/.test(content)) return "重点关注"
  if (row.visitType === "1") return "健康跟进"
  if (row.visitType === "2") return "服务保障"
  return "常规"
}

function getRiskType(row) {
  const label = getRiskLabel(row)
  if (label === "重点关注") return "danger"
  if (label === "健康跟进") return "warning"
  if (label === "服务保障") return "success"
  return "info"
}

function stripHtml(content) {
  if (!content) return ""
  return content
    .replace(/<[^>]+>/g, " ")
    .replace(/&nbsp;/g, " ")
    .replace(/\s+/g, " ")
    .trim()
}

function getVisitSummary(content) {
  const text = stripHtml(content)
  if (!text) return "--"
  return text.length > 68 ? `${text.slice(0, 68)}...` : text
}

function getList() {
  loading.value = true
  listVisitRecord(queryParams.value).then((response) => {
    visitRecordList.value = response.rows || []
    total.value = response.total || 0
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    recordId: undefined,
    elderlyId: queryParams.value.elderlyId || undefined,
    elderlyName: queryParams.value.elderlyName || undefined,
    visitType: undefined,
    visitTime: undefined,
    visitContent: "",
    createBy: undefined,
  }
  proxy.resetForm("visitRecordRef")
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  buildQueryFromRoute()
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.recordId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "新增日常随访记录"
}

function handleUpdate(row) {
  reset()
  const recordId = row.recordId || ids.value[0]
  getVisitRecord(recordId).then((response) => {
    form.value = response.data
    open.value = true
    title.value = "修改日常随访记录"
  })
}

function submitForm() {
  proxy.$refs.visitRecordRef.validate((valid) => {
    if (!valid) return
    const request = form.value.recordId ? updateVisitRecord(form.value) : addVisitRecord(form.value)
    request.then(() => {
      proxy.$modal.msgSuccess(form.value.recordId ? "修改成功" : "新增成功")
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const recordIds = row.recordId || ids.value
  proxy.$modal
    .confirm(`是否确认删除日常随访记录编号为"${recordIds}"的数据项？`)
    .then(() => delVisitRecord(recordIds))
    .then(() => {
      getList()
      proxy.$modal.msgSuccess("删除成功")
    })
    .catch(() => {})
}

function handleExport() {
  proxy.download(
    "care/visitRecord/export",
    { ...queryParams.value },
    `visitRecord_${new Date().getTime()}.xlsx`
  )
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

buildQueryFromRoute()
reset()
getList()
</script>

<style scoped>
.summary-row {
  margin-bottom: 18px;
}

.summary-card {
  min-height: 112px;
  padding: 18px 20px;
  border-radius: 14px;
  background: linear-gradient(135deg, #f7fbff 0%, #eef6ff 100%);
  border: 1px solid #e4eef8;
  box-shadow: 0 8px 20px rgba(15, 76, 129, 0.08);
}

.summary-label {
  font-size: 14px;
  color: #6b7a90;
}

.summary-value {
  margin-top: 10px;
  font-size: 30px;
  font-weight: 700;
  line-height: 1.2;
  color: #16324f;
}

.summary-time {
  font-size: 22px;
}

.summary-desc {
  margin-top: 8px;
  font-size: 12px;
  color: #8a97ab;
}
</style>
