<template>
  <div class="app-container">
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
      <el-form-item label="设备编号" prop="deviceId">
        <el-input
          v-model="queryParams.deviceId"
          placeholder="请输入设备编号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预警级别" prop="alertLevel">
        <el-select v-model="queryParams.alertLevel" placeholder="请选择预警级别" clearable>
          <el-option
            v-for="dict in biz_alert_level"
            :key="dict.value"
            :label="dict.label"
            :value="Number(dict.value)"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="预警类型" prop="alertType">
        <el-select v-model="queryParams.alertType" placeholder="请选择预警类型" clearable>
          <el-option
            v-for="dict in biz_alert_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="处理状态" prop="handleStatus">
        <el-select v-model="queryParams.handleStatus" placeholder="请选择处理状态" clearable>
          <el-option
            v-for="dict in biz_handle_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['care:alertRecord:add']">
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
          v-hasPermi="['care:alertRecord:edit']"
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
          v-hasPermi="['care:alertRecord:remove']"
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
          v-hasPermi="['care:alertRecord:export']"
        >
          导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="alertRecordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="预警ID" align="center" prop="alertId" width="90" />
      <el-table-column label="工单ID" align="center" prop="orderId" width="90">
        <template #default="scope">
          <el-button
            v-if="scope.row.orderId"
            link
            type="primary"
            @click="handleViewWorkOrder(scope.row)"
          >
            {{ scope.row.orderId }}
          </el-button>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="老人姓名" align="center" prop="elderlyName" min-width="120" />
      <el-table-column label="设备编号" align="center" prop="deviceId" min-width="120" />
      <el-table-column label="设备名称" align="center" prop="deviceName" min-width="140" />
      <el-table-column label="预警级别" align="center" prop="alertLevel" width="110">
        <template #default="scope">
          <dict-tag :options="biz_alert_level" :value="scope.row.alertLevel" />
        </template>
      </el-table-column>
      <el-table-column label="预警类型" align="center" prop="alertType" min-width="120">
        <template #default="scope">
          <dict-tag :options="biz_alert_type" :value="scope.row.alertType" />
        </template>
      </el-table-column>
      <el-table-column label="预警描述" align="center" prop="alertDesc" min-width="220" show-overflow-tooltip />
      <el-table-column label="处理状态" align="center" prop="handleStatus" width="110">
        <template #default="scope">
          <dict-tag :options="biz_handle_status" :value="scope.row.handleStatus" />
        </template>
      </el-table-column>
      <el-table-column label="发生时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, "{y}-{m}-{d} {h}:{i}:{s}") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="处理备注" align="center" prop="remark" min-width="180" show-overflow-tooltip />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template #default="scope">
          <el-button
            link
            type="primary"
            icon="Edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['care:alertRecord:edit']"
          >
            修改
          </el-button>
          <el-button
            link
            type="primary"
            icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['care:alertRecord:remove']"
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
      <el-form ref="alertRecordRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="老人ID" prop="elderlyId">
          <el-input v-model.number="form.elderlyId" placeholder="请输入老人ID" />
        </el-form-item>
        <el-form-item label="老人姓名" prop="elderlyName">
          <el-input v-model="form.elderlyName" placeholder="请输入老人姓名" />
        </el-form-item>
        <el-form-item label="设备编号" prop="deviceId">
          <el-input v-model="form.deviceId" placeholder="请输入设备编号" />
        </el-form-item>
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="form.deviceName" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="预警级别" prop="alertLevel">
          <el-select v-model="form.alertLevel" placeholder="请选择预警级别">
            <el-option
              v-for="dict in biz_alert_level"
              :key="dict.value"
              :label="dict.label"
              :value="Number(dict.value)"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="预警类型" prop="alertType">
          <el-select v-model="form.alertType" placeholder="请选择预警类型">
            <el-option
              v-for="dict in biz_alert_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="预警描述" prop="alertDesc">
          <el-input v-model="form.alertDesc" type="textarea" placeholder="请输入预警描述" />
        </el-form-item>
        <el-form-item label="处理状态" prop="handleStatus">
          <el-select v-model="form.handleStatus" placeholder="请选择处理状态">
            <el-option
              v-for="dict in biz_handle_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="发生时间" prop="createTime">
          <el-date-picker
            v-model="form.createTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择发生时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="处理备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入处理备注" />
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

<script setup name="AlertRecord">
import { addAlertRecord, delAlertRecord, getAlertRecord, listAlertRecord, updateAlertRecord } from "@/api/care/alertRecord"
import { parseTime } from "@/utils/ruoyi"

const { proxy } = getCurrentInstance()
const route = useRoute()
const router = useRouter()
const { biz_alert_level, biz_alert_type, biz_handle_status } = proxy.useDict(
  "biz_alert_level",
  "biz_alert_type",
  "biz_handle_status"
)

const alertRecordList = ref([])
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
    alertId: null,
    elderlyName: null,
    deviceId: null,
    alertLevel: null,
    alertType: null,
    handleStatus: null
  },
  rules: {
    elderlyId: [{ required: true, message: "老人ID不能为空", trigger: "blur" }],
    elderlyName: [{ required: true, message: "老人姓名不能为空", trigger: "blur" }],
    alertLevel: [{ required: true, message: "预警级别不能为空", trigger: "change" }],
    alertType: [{ required: true, message: "预警类型不能为空", trigger: "change" }],
    handleStatus: [{ required: true, message: "处理状态不能为空", trigger: "change" }],
    createTime: [{ required: true, message: "发生时间不能为空", trigger: "change" }]
  }
})

const { form, queryParams, rules } = toRefs(data)

function formatNow() {
  return parseTime(new Date(), "{y}-{m}-{d} {h}:{i}:{s}")
}

function getList() {
  loading.value = true
  listAlertRecord(queryParams.value)
    .then((response) => {
      alertRecordList.value = response.rows
      total.value = response.total
    })
    .finally(() => {
      loading.value = false
    })
}

function reset() {
  form.value = {
    alertId: null,
    orderId: null,
    elderlyId: null,
    elderlyName: null,
    deviceId: null,
    deviceName: null,
    alertLevel: null,
    alertType: null,
    alertDesc: null,
    handleStatus: "0",
    createTime: formatNow(),
    remark: null
  }
  proxy.resetForm("alertRecordRef")
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
  ids.value = selection.map((item) => item.alertId)
  single.value = selection.length !== 1
  multiple.value = selection.length === 0
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "新增预警记录"
}

function handleUpdate(row) {
  const alertId = row?.alertId || ids.value[0]
  if (!alertId) {
    proxy.$modal.msgWarning("请选择需要修改的数据")
    return
  }
  reset()
  getAlertRecord(alertId).then((response) => {
    form.value = { ...response.data }
    open.value = true
    title.value = "修改预警记录"
  })
}

function submitForm() {
  proxy.$refs.alertRecordRef.validate((valid) => {
    if (!valid) {
      return
    }
    const request = form.value.alertId ? updateAlertRecord(form.value) : addAlertRecord(form.value)
    request.then(() => {
      proxy.$modal.msgSuccess(form.value.alertId ? "修改成功" : "新增成功")
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const alertIds = row?.alertId ? [row.alertId] : ids.value
  if (!alertIds.length) {
    proxy.$modal.msgWarning("请先选择要删除的数据")
    return
  }
  proxy.$modal
    .confirm(`是否确认删除预警记录编号为“${alertIds.join(",")}”的数据项？`)
    .then(() => delAlertRecord(alertIds.join(",")))
    .then(() => {
      proxy.$modal.msgSuccess("删除成功")
      getList()
    })
    .catch(() => {})
}

function handleViewWorkOrder(row) {
  if (!row?.orderId) {
    return
  }
  router.push({
    path: "/smartcare/workOrder",
    query: {
      orderId: row.orderId,
      alertId: row.alertId
    }
  })
}

function handleExport() {
  proxy.download("care/alertRecord/export", { ...queryParams.value }, `alertRecord_${new Date().getTime()}.xlsx`)
}

function applyRouteQuery() {
  queryParams.value.alertId = route.query.alertId ?? null
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

applyRouteQuery()
getList()
</script>
