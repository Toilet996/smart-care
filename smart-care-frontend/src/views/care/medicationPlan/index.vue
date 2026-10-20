<template>
  <div class="app-container">
    <el-row :gutter="12" class="mb12">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover"><div class="stat-label">计划总数</div><div class="stat-value">{{ total }}</div></el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover"><div class="stat-label">执行中计划</div><div class="stat-value success">{{ activeCount }}</div></el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover"><div class="stat-label">近期待服完成</div><div class="stat-value primary">{{ takenCount }}</div></el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover"><div class="stat-label">近期漏服</div><div class="stat-value danger">{{ missedCount }}</div></el-card>
      </el-col>
    </el-row>

    <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="老人姓名" prop="elderlyName">
        <el-input v-model="queryParams.elderlyName" placeholder="请输入老人姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="药品名称" prop="medicationName">
        <el-input v-model="queryParams.medicationName" placeholder="请输入药品名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="服药时段" prop="periodCode">
        <el-select v-model="queryParams.periodCode" placeholder="请选择时段" clearable style="width: 180px">
          <el-option v-for="item in periodOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="计划状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 180px">
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['care:medicationPlan:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['care:medicationPlan:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['care:medicationPlan:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="planList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="老人姓名" align="center" prop="elderlyName" min-width="120" />
      <el-table-column label="药品名称" align="center" prop="medicationName" min-width="140" />
      <el-table-column label="单次剂量" align="center" prop="dosage" width="110" />
      <el-table-column label="用药方式" align="center" prop="usageMethod" min-width="140" show-overflow-tooltip />
      <el-table-column label="时段" align="center" prop="periodCode" width="110">
        <template #default="scope">{{ periodLabel(scope.row.periodCode) }}</template>
      </el-table-column>
      <el-table-column label="计划时间" align="center" prop="planTime" width="100" />
      <el-table-column label="提醒" align="center" prop="reminderEnabled" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.reminderEnabled === '1' ? 'success' : 'info'">{{ scope.row.reminderEnabled === '1' ? '开启' : '关闭' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope"><el-tag :type="scope.row.status === '0' ? 'success' : 'info'">{{ scope.row.status === '0' ? '执行中' : '暂停' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="已服" align="center" prop="recentTakenCount" width="80" />
      <el-table-column label="漏服" align="center" prop="recentMissedCount" width="80" />
      <el-table-column label="操作" align="center" width="220" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="goToRecord(scope.row)">执行记录</el-button>
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['care:medicationPlan:edit']">修改</el-button>
          <el-button link type="danger" @click="handleDelete(scope.row)" v-hasPermi="['care:medicationPlan:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="680px" append-to-body>
      <el-form ref="planRef" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="关联老人" prop="elderlyId">
          <el-select v-model="form.elderlyId" placeholder="请选择老人" style="width: 100%" @change="handleElderlyChange">
            <el-option v-for="item in elderlyOptions" :key="item.elderlyId" :label="item.elderlyName" :value="item.elderlyId" />
          </el-select>
        </el-form-item>
        <el-form-item label="药品名称" prop="medicationName">
          <el-input v-model="form.medicationName" placeholder="请输入药品名称" />
        </el-form-item>
        <el-form-item label="单次剂量" prop="dosage">
          <el-input v-model="form.dosage" placeholder="如 1片 / 5ml" />
        </el-form-item>
        <el-form-item label="用药方式" prop="usageMethod">
          <el-input v-model="form.usageMethod" placeholder="如 饭后口服" />
        </el-form-item>
        <el-form-item label="服药时段" prop="periodCode">
          <el-select v-model="form.periodCode" placeholder="请选择时段" style="width: 100%">
            <el-option v-for="item in periodOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="计划时间" prop="planTime">
          <el-time-picker v-model="form.planTime" value-format="HH:mm" format="HH:mm" placeholder="请选择时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="计划状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio-button label="0">执行中</el-radio-button>
            <el-radio-button label="1">暂停</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="提醒开关" prop="reminderEnabled">
          <el-switch v-model="form.reminderEnabled" active-value="1" inactive-value="0" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="可填写特殊提醒或饮食要求" />
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

<script setup name="MedicationPlan">
import { computed, getCurrentInstance, reactive, ref, toRefs } from 'vue'
import { useRouter } from 'vue-router'
import { addMedicationPlan, delMedicationPlan, getMedicationPlan, listMedicationPlan, updateMedicationPlan } from '@/api/care/medicationPlan'
import { getFamilyOptions } from '@/api/care/family'

const { proxy } = getCurrentInstance()
const router = useRouter()

const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const planList = ref([])
const elderlyOptions = ref([])
const open = ref(false)
const title = ref('')
const ids = ref([])
const single = ref(true)
const multiple = ref(true)

const periodOptions = [
  { label: '早餐后', value: 'morning' },
  { label: '午餐后', value: 'noon' },
  { label: '晚餐后', value: 'evening' },
  { label: '睡前', value: 'bedtime' }
]
const statusOptions = [
  { label: '执行中', value: '0' },
  { label: '暂停', value: '1' }
]

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    elderlyName: undefined,
    medicationName: undefined,
    periodCode: undefined,
    status: undefined
  },
  form: {},
  rules: {
    elderlyId: [{ required: true, message: '请选择老人', trigger: 'change' }],
    medicationName: [{ required: true, message: '请输入药品名称', trigger: 'blur' }],
    dosage: [{ required: true, message: '请输入单次剂量', trigger: 'blur' }],
    periodCode: [{ required: true, message: '请选择服药时段', trigger: 'change' }],
    planTime: [{ required: true, message: '请选择计划时间', trigger: 'change' }],
    startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

const activeCount = computed(() => planList.value.filter(item => item.status === '0').length)
const takenCount = computed(() => planList.value.reduce((sum, item) => sum + Number(item.recentTakenCount || 0), 0))
const missedCount = computed(() => planList.value.reduce((sum, item) => sum + Number(item.recentMissedCount || 0), 0))

function getList() {
  loading.value = true
  listMedicationPlan(queryParams.value).then(response => {
    planList.value = response.rows
    total.value = response.total
  }).finally(() => {
    loading.value = false
  })
}

function loadOptions() {
  getFamilyOptions().then(response => {
    elderlyOptions.value = response.data?.elderlyList || []
  })
}

function reset() {
  form.value = {
    planId: undefined,
    elderlyId: undefined,
    elderlyName: undefined,
    medicationName: undefined,
    dosage: undefined,
    usageMethod: '饭后口服',
    periodCode: 'morning',
    planTime: '08:00',
    startDate: new Date().toISOString().slice(0, 10),
    endDate: undefined,
    status: '0',
    reminderEnabled: '1',
    remark: undefined
  }
  proxy.resetForm('planRef')
}

function handleElderlyChange(elderlyId) {
  const elderly = elderlyOptions.value.find(item => item.elderlyId === elderlyId)
  form.value.elderlyName = elderly?.elderlyName || ''
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.planId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '新增服药计划'
}

function handleUpdate(row) {
  const planId = row?.planId || ids.value[0]
  if (!planId) {
    proxy.$modal.msgWarning('请先选择服药计划')
    return
  }
  getMedicationPlan(planId).then(response => {
    form.value = { ...response.data }
    open.value = true
    title.value = '修改服药计划'
  })
}

function submitForm() {
  proxy.$refs.planRef.validate(valid => {
    if (!valid) return
    const request = form.value.planId ? updateMedicationPlan(form.value) : addMedicationPlan(form.value)
    request.then(() => {
      proxy.$modal.msgSuccess(form.value.planId ? '修改成功' : '新增成功')
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const planIds = row?.planId ? [row.planId] : ids.value
  if (!planIds.length) {
    proxy.$modal.msgWarning('请先选择服药计划')
    return
  }
  proxy.$modal.confirm(`是否确认删除计划编号为“${planIds.join(',')}”的数据项？`).then(() => {
    return delMedicationPlan(planIds.join(','))
  }).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
  }).catch(() => {})
}

function cancel() {
  open.value = false
  reset()
}

function goToRecord(row) {
  router.push({ path: '/smartcare/medicationRecord', query: { elderlyId: row.elderlyId, elderlyName: row.elderlyName, planId: row.planId } })
}

function periodLabel(value) {
  return periodOptions.find(item => item.value === value)?.label || value || '-'
}

loadOptions()
getList()
</script>

<style scoped>
.mb12 { margin-bottom: 12px; }
.stat-label { color: #606266; font-size: 14px; }
.stat-value { margin-top: 10px; font-size: 28px; font-weight: 700; color: #303133; }
.stat-value.success { color: #67c23a; }
.stat-value.primary { color: #409eff; }
.stat-value.danger { color: #f56c6c; }
</style>
