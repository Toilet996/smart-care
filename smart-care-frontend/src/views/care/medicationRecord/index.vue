<template>
  <div class="app-container">
    <el-row :gutter="12" class="mb12">
      <el-col :xs="24" :sm="12" :md="6"><el-card shadow="hover"><div class="stat-label">执行记录</div><div class="stat-value">{{ total }}</div></el-card></el-col>
      <el-col :xs="24" :sm="12" :md="6"><el-card shadow="hover"><div class="stat-label">已服</div><div class="stat-value success">{{ takenCount }}</div></el-card></el-col>
      <el-col :xs="24" :sm="12" :md="6"><el-card shadow="hover"><div class="stat-label">待服</div><div class="stat-value warning">{{ waitingCount }}</div></el-card></el-col>
      <el-col :xs="24" :sm="12" :md="6"><el-card shadow="hover"><div class="stat-label">漏服</div><div class="stat-value danger">{{ missedCount }}</div></el-card></el-col>
    </el-row>

    <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="老人姓名" prop="elderlyName">
        <el-input v-model="queryParams.elderlyName" placeholder="请输入老人姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="药品名称" prop="medicationName">
        <el-input v-model="queryParams.medicationName" placeholder="请输入药品名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="执行状态" prop="executeStatus">
        <el-select v-model="queryParams.executeStatus" placeholder="请选择状态" clearable style="width: 180px">
          <el-option v-for="item in executeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="计划日期" prop="scheduledDate">
        <el-date-picker v-model="queryParams.scheduledDate" type="date" value-format="YYYY-MM-DD" clearable style="width: 180px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['care:medicationRecord:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['care:medicationRecord:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['care:medicationRecord:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="recordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="老人姓名" align="center" prop="elderlyName" min-width="120" />
      <el-table-column label="药品名称" align="center" prop="medicationName" min-width="140" />
      <el-table-column label="计划时间" align="center" prop="planTime" width="100" />
      <el-table-column label="计划日期" align="center" prop="scheduledDate" width="120" />
      <el-table-column label="执行状态" align="center" prop="executeStatus" width="100">
        <template #default="scope"><el-tag :type="executeTagType(scope.row.executeStatus)">{{ executeLabel(scope.row.executeStatus) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="实际执行时间" align="center" prop="actualTime" min-width="160" />
      <el-table-column label="来源" align="center" prop="sourceType" width="100" />
      <el-table-column label="备注" align="center" prop="remark" min-width="220" show-overflow-tooltip />
      <el-table-column label="操作" align="center" width="220" fixed="right">
        <template #default="scope">
          <el-button link type="success" @click="quickUpdate(scope.row, '1')" v-if="scope.row.executeStatus !== '1'">标记已服</el-button>
          <el-button link type="warning" @click="quickUpdate(scope.row, '2')" v-if="scope.row.executeStatus !== '2'">标记漏服</el-button>
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['care:medicationRecord:edit']">修改</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="680px" append-to-body>
      <el-form ref="recordRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="关联老人" prop="elderlyId">
          <el-select v-model="form.elderlyId" placeholder="请选择老人" style="width: 100%" @change="handleElderlyChange">
            <el-option v-for="item in elderlyOptions" :key="item.elderlyId" :label="item.elderlyName" :value="item.elderlyId" />
          </el-select>
        </el-form-item>
        <el-form-item label="药品名称" prop="medicationName">
          <el-input v-model="form.medicationName" placeholder="请输入药品名称" />
        </el-form-item>
        <el-form-item label="计划时间" prop="planTime">
          <el-time-picker v-model="form.planTime" value-format="HH:mm" format="HH:mm" style="width: 100%" />
        </el-form-item>
        <el-form-item label="计划日期" prop="scheduledDate">
          <el-date-picker v-model="form.scheduledDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="执行状态" prop="executeStatus">
          <el-radio-group v-model="form.executeStatus">
            <el-radio-button label="0">待服</el-radio-button>
            <el-radio-button label="1">已服</el-radio-button>
            <el-radio-button label="2">漏服</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="实际执行时间" prop="actualTime">
          <el-date-picker v-model="form.actualTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="来源" prop="sourceType">
          <el-input v-model="form.sourceType" placeholder="如 sensor / manual" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
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

<script setup name="MedicationRecord">
import { computed, getCurrentInstance, reactive, ref, toRefs, watch } from 'vue'
import { useRoute } from 'vue-router'
import { addMedicationRecord, delMedicationRecord, getMedicationRecord, listMedicationRecord, updateMedicationRecord } from '@/api/care/medicationRecord'
import { getFamilyOptions } from '@/api/care/family'

const { proxy } = getCurrentInstance()
const route = useRoute()

const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const recordList = ref([])
const elderlyOptions = ref([])
const open = ref(false)
const title = ref('')
const ids = ref([])
const single = ref(true)
const multiple = ref(true)

const executeOptions = [
  { label: '待服', value: '0' },
  { label: '已服', value: '1' },
  { label: '漏服', value: '2' }
]

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    planId: undefined,
    elderlyId: undefined,
    elderlyName: undefined,
    medicationName: undefined,
    executeStatus: undefined,
    scheduledDate: undefined
  },
  form: {},
  rules: {
    elderlyId: [{ required: true, message: '请选择老人', trigger: 'change' }],
    medicationName: [{ required: true, message: '请输入药品名称', trigger: 'blur' }],
    planTime: [{ required: true, message: '请选择计划时间', trigger: 'change' }],
    scheduledDate: [{ required: true, message: '请选择计划日期', trigger: 'change' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

const takenCount = computed(() => recordList.value.filter(item => item.executeStatus === '1').length)
const waitingCount = computed(() => recordList.value.filter(item => item.executeStatus === '0').length)
const missedCount = computed(() => recordList.value.filter(item => item.executeStatus === '2').length)

function getList() {
  loading.value = true
  listMedicationRecord(queryParams.value).then(response => {
    recordList.value = response.rows
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

function applyRouteQuery() {
  queryParams.value.planId = route.query.planId ?? undefined
  queryParams.value.elderlyId = route.query.elderlyId ?? undefined
  queryParams.value.elderlyName = route.query.elderlyName ?? undefined
}

function reset() {
  form.value = {
    recordId: undefined,
    planId: queryParams.value.planId,
    elderlyId: queryParams.value.elderlyId,
    elderlyName: queryParams.value.elderlyName,
    medicationName: undefined,
    planTime: '08:00',
    executeStatus: '0',
    sourceType: 'manual',
    scheduledDate: new Date().toISOString().slice(0, 10),
    actualTime: undefined,
    remark: undefined
  }
  proxy.resetForm('recordRef')
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
  applyRouteQuery()
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.recordId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '新增服药执行记录'
}

function handleUpdate(row) {
  const recordId = row?.recordId || ids.value[0]
  if (!recordId) {
    proxy.$modal.msgWarning('请先选择执行记录')
    return
  }
  getMedicationRecord(recordId).then(response => {
    form.value = { ...response.data }
    open.value = true
    title.value = '修改服药执行记录'
  })
}

function submitForm() {
  proxy.$refs.recordRef.validate(valid => {
    if (!valid) return
    const request = form.value.recordId ? updateMedicationRecord(form.value) : addMedicationRecord(form.value)
    request.then(() => {
      proxy.$modal.msgSuccess(form.value.recordId ? '修改成功' : '新增成功')
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const recordIds = row?.recordId ? [row.recordId] : ids.value
  if (!recordIds.length) {
    proxy.$modal.msgWarning('请先选择执行记录')
    return
  }
  proxy.$modal.confirm(`是否确认删除记录编号为“${recordIds.join(',')}”的数据项？`).then(() => {
    return delMedicationRecord(recordIds.join(','))
  }).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
  }).catch(() => {})
}

function quickUpdate(row, executeStatus) {
  const payload = {
    ...row,
    executeStatus,
    actualTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
    sourceType: 'manual',
    remark: executeStatus === '1' ? '人工标记已服' : '人工标记漏服'
  }
  updateMedicationRecord(payload).then(() => {
    proxy.$modal.msgSuccess('状态已更新')
    getList()
  })
}

function cancel() {
  open.value = false
  reset()
}

function executeLabel(value) {
  return executeOptions.find(item => item.value === value)?.label || '待服'
}

function executeTagType(value) {
  if (value === '1') return 'success'
  if (value === '2') return 'danger'
  return 'warning'
}

watch(() => route.query, () => {
  applyRouteQuery()
  getList()
}, { deep: true })

applyRouteQuery()
loadOptions()
getList()
</script>

<style scoped>
.mb12 { margin-bottom: 12px; }
.stat-label { color: #606266; font-size: 14px; }
.stat-value { margin-top: 10px; font-size: 28px; font-weight: 700; color: #303133; }
.stat-value.success { color: #67c23a; }
.stat-value.warning { color: #e6a23c; }
.stat-value.danger { color: #f56c6c; }
</style>
