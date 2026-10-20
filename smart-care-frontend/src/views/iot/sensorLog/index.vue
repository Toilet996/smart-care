<template>
  <div class="app-container">
    <el-form
      ref="queryRef"
      :model="queryParams"
      :inline="true"
      v-show="showSearch"
      label-width="88px"
    >
      <el-form-item label="设备编号" prop="deviceId">
        <el-input
          v-model="queryParams.deviceId"
          placeholder="请输入设备编号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备名称" prop="deviceName">
        <el-input
          v-model="queryParams.deviceName"
          placeholder="请输入设备名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="老人姓名" prop="elderlyName">
        <el-input
          v-model="queryParams.elderlyName"
          placeholder="请输入老人姓名"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="指标类型" prop="metricType">
        <el-select v-model="queryParams.metricType" placeholder="请选择指标类型" clearable>
          <el-option
            v-for="dict in metricTypeOptions"
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['iot:sensorLog:add']">
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
          v-hasPermi="['iot:sensorLog:edit']"
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
          v-hasPermi="['iot:sensorLog:remove']"
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
          v-hasPermi="['iot:sensorLog:export']"
        >
          导出
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="Upload" @click="handleSimulateUpload">
          模拟上报
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="sensorLogList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="日志ID" align="center" prop="logId" width="90" />
      <el-table-column label="设备编号" align="center" prop="deviceId" min-width="140" />
      <el-table-column label="设备名称" align="center" prop="deviceName" min-width="150" />
      <el-table-column label="老人ID" align="center" prop="elderlyId" width="90" />
      <el-table-column label="老人姓名" align="center" prop="elderlyName" min-width="120" />
      <el-table-column label="指标类型" align="center" prop="metricType" min-width="120">
        <template #default="scope">
          <dict-tag :options="metricTypeOptions" :value="scope.row.metricType" />
        </template>
      </el-table-column>
      <el-table-column label="指标值" align="center" prop="metricValue" min-width="100" />
      <el-table-column label="上报时间" align="center" prop="reportTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.reportTime, "{y}-{m}-{d} {h}:{i}:{s}") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template #default="scope">
          <el-button
            link
            type="primary"
            icon="Edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['iot:sensorLog:edit']"
          >
            修改
          </el-button>
          <el-button
            link
            type="primary"
            icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['iot:sensorLog:remove']"
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

    <el-dialog :title="title" v-model="open" width="560px" append-to-body>
      <el-form ref="sensorLogRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="设备编号" prop="deviceId">
          <el-input v-model="form.deviceId" placeholder="请输入设备编号" />
        </el-form-item>
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="form.deviceName" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="老人ID" prop="elderlyId">
          <el-input v-model.number="form.elderlyId" placeholder="请输入老人ID" />
        </el-form-item>
        <el-form-item label="老人姓名" prop="elderlyName">
          <el-input v-model="form.elderlyName" placeholder="请输入老人姓名" />
        </el-form-item>
        <el-form-item label="指标类型" prop="metricType">
          <el-select v-model="form.metricType" placeholder="请选择指标类型">
            <el-option
              v-for="dict in metricTypeOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="指标值" prop="metricValue">
          <el-input v-model="form.metricValue" placeholder="请输入指标值" />
        </el-form-item>
        <el-form-item label="上报时间" prop="reportTime">
          <el-date-picker
            v-model="form.reportTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择上报时间"
            style="width: 100%"
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

    <el-dialog title="模拟传感器上报" v-model="uploadOpen" width="620px" append-to-body>
      <el-form ref="uploadRef" :model="uploadForm" :rules="uploadRules" label-width="100px">
        <el-form-item label="设备编号" prop="deviceId">
          <el-input v-model="uploadForm.deviceId" placeholder="请输入设备编号，例如 D001" />
        </el-form-item>
        <el-form-item label="模拟场景" prop="scene">
          <el-radio-group v-model="uploadForm.scene" @change="handleSceneChange">
            <el-radio label="normal">正常数据</el-radio>
            <el-radio label="heart_abnormal">心率异常</el-radio>
            <el-radio label="bed_leave">离床异常</el-radio>
            <el-radio label="smoke_alert">烟雾异常</el-radio>
            <el-radio label="door_out">门磁外出</el-radio>
            <el-radio label="pill_taken">药盒已服</el-radio>
            <el-radio label="pill_missed">药盒漏服</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="指标类型" prop="metricType">
          <el-select v-model="uploadForm.metricType" placeholder="请选择指标类型">
            <el-option
              v-for="dict in metricTypeOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="指标值" prop="value">
          <el-input v-model="uploadForm.value" placeholder="请输入指标值" />
        </el-form-item>
        <el-form-item label="上报时间" prop="reportTime">
          <el-date-picker
            v-model="uploadForm.reportTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择上报时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="批量上报">
          <el-switch v-model="uploadForm.batchMode" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" :loading="uploadLoading" @click="submitUpload">提交</el-button>
          <el-button @click="uploadOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="SensorLog">
import { computed } from "vue"
import { addSensorLog, delSensorLog, getSensorLog, listSensorLog, updateSensorLog, uploadSensorData, uploadSensorDataBatch } from "@/api/iot/sensorLog"
import { parseTime } from "@/utils/ruoyi"

const { proxy } = getCurrentInstance()
const { biz_metric_type } = proxy.useDict("biz_metric_type")
const metricTypeOptions = computed(() => {
  const source = [...biz_metric_type.value]
  ;[
    { label: "门磁状态", value: "door_status" },
    { label: "服药状态", value: "pill_status" }
  ].forEach((item) => {
    if (!source.find((option) => option.value === item.value)) {
      source.push(item)
    }
  })
  return source
})

const sceneTemplates = {
  normal: {
    metricType: "heart_rate",
    value: "72",
    batchData: [
      { metricType: "heart_rate", value: "72" },
      { metricType: "breath_rate", value: "16" }
    ]
  },
  heart_abnormal: {
    metricType: "heart_rate",
    value: "130",
    batchData: [{ metricType: "heart_rate", value: "130" }]
  },
  bed_leave: {
    metricType: "leave_bed",
    value: "off_bed",
    batchData: [{ metricType: "leave_bed", value: "off_bed" }]
  },
  smoke_alert: {
    metricType: "smoke_value",
    value: "50",
    batchData: [{ metricType: "smoke_value", value: "50" }]
  },
  door_out: {
    metricType: "door_status",
    value: "open",
    batchData: [{ metricType: "door_status", value: "open" }]
  },
  pill_taken: {
    metricType: "pill_status",
    value: "taken",
    batchData: [{ metricType: "pill_status", value: "taken" }]
  },
  pill_missed: {
    metricType: "pill_status",
    value: "missed",
    batchData: [{ metricType: "pill_status", value: "missed" }]
  }
}

const sensorLogList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const uploadOpen = ref(false)
const uploadLoading = ref(false)

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    deviceId: null,
    deviceName: null,
    elderlyName: null,
    metricType: null
  },
  rules: {
    deviceId: [{ required: true, message: "设备编号不能为空", trigger: "blur" }],
    metricType: [{ required: true, message: "指标类型不能为空", trigger: "change" }],
    metricValue: [{ required: true, message: "指标值不能为空", trigger: "blur" }],
    reportTime: [{ required: true, message: "上报时间不能为空", trigger: "change" }]
  },
  uploadForm: {
    deviceId: "D001",
    scene: "normal",
    metricType: "heart_rate",
    value: "72",
    reportTime: formatNow(),
    batchMode: false
  },
  uploadRules: {
    deviceId: [{ required: true, message: "设备编号不能为空", trigger: "blur" }],
    metricType: [{ required: true, message: "指标类型不能为空", trigger: "change" }],
    value: [{ required: true, message: "指标值不能为空", trigger: "blur" }],
    reportTime: [{ required: true, message: "上报时间不能为空", trigger: "change" }]
  }
})

const { form, queryParams, rules, uploadForm, uploadRules } = toRefs(data)

function formatNow() {
  return parseTime(new Date(), "{y}-{m}-{d} {h}:{i}:{s}")
}

function getList() {
  loading.value = true
  listSensorLog(queryParams.value)
    .then((response) => {
      sensorLogList.value = response.rows
      total.value = response.total
    })
    .finally(() => {
      loading.value = false
    })
}

function reset() {
  form.value = {
    logId: null,
    deviceId: null,
    deviceName: null,
    elderlyId: null,
    elderlyName: null,
    metricType: null,
    metricValue: null,
    reportTime: formatNow()
  }
  proxy.resetForm("sensorLogRef")
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
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.logId)
  single.value = selection.length !== 1
  multiple.value = selection.length === 0
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "新增传感器流水"
}

function handleUpdate(row) {
  const logId = row?.logId || ids.value[0]
  if (!logId) {
    proxy.$modal.msgWarning("请选择需要修改的数据")
    return
  }
  reset()
  getSensorLog(logId).then((response) => {
    form.value = { ...response.data }
    open.value = true
    title.value = "修改传感器流水"
  })
}

function submitForm() {
  proxy.$refs.sensorLogRef.validate((valid) => {
    if (!valid) {
      return
    }
    const request = form.value.logId ? updateSensorLog(form.value) : addSensorLog(form.value)
    request.then(() => {
      proxy.$modal.msgSuccess(form.value.logId ? "修改成功" : "新增成功")
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const logIds = row?.logId ? [row.logId] : ids.value
  if (!logIds.length) {
    proxy.$modal.msgWarning("请先选择要删除的数据")
    return
  }
  proxy.$modal
    .confirm(`是否确认删除传感器流水编号为“${logIds.join(",")}”的数据项？`)
    .then(() => delSensorLog(logIds.join(",")))
    .then(() => {
      proxy.$modal.msgSuccess("删除成功")
      getList()
    })
    .catch(() => {})
}

function handleExport() {
  proxy.download("iot/sensorLog/export", { ...queryParams.value }, `sensorLog_${new Date().getTime()}.xlsx`)
}

function handleSimulateUpload() {
  uploadOpen.value = true
  handleSceneChange(uploadForm.value.scene)
}

function handleSceneChange(scene) {
  const template = sceneTemplates[scene]
  if (!template) {
    return
  }
  uploadForm.value.metricType = template.metricType
  uploadForm.value.value = template.value
  if (!uploadForm.value.reportTime) {
    uploadForm.value.reportTime = formatNow()
  }
}

function buildBatchPayload() {
  const template = sceneTemplates[uploadForm.value.scene]
  return [
    {
      deviceId: uploadForm.value.deviceId,
      dataList: template.batchData.map((item) => ({
        metricType: item.metricType,
        value: item.value,
        reportTime: uploadForm.value.reportTime
      }))
    }
  ]
}

function submitUpload() {
  proxy.$refs.uploadRef.validate((valid) => {
    if (!valid) {
      return
    }
    uploadLoading.value = true
    const request = uploadForm.value.batchMode
      ? uploadSensorDataBatch(buildBatchPayload())
      : uploadSensorData({
          deviceId: uploadForm.value.deviceId,
          metricType: uploadForm.value.metricType,
          value: uploadForm.value.value,
          reportTime: uploadForm.value.reportTime
        })
    request
      .then(() => {
        proxy.$modal.msgSuccess("上报成功")
        uploadOpen.value = false
        getList()
      })
      .finally(() => {
        uploadLoading.value = false
      })
  })
}

getList()
</script>
