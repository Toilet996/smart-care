<template>
  <div class="app-container focus-page" v-loading="loading">
    <section class="hero-panel">
      <div>
        <div class="hero-eyebrow">Focus Management</div>
        <h1>重点老人 / 高风险人群专项管理</h1>
        <p>集中查看高频预警、工单积压、长期未巡访和健康下降老人，并维护纳管进度、处置责任和跟进结果。</p>
      </div>
      <div class="hero-actions">
        <el-button type="primary" @click="getList">刷新数据</el-button>
        <el-button @click="goTo('/smartcare/overview')">返回总览</el-button>
        <el-button @click="goTo('/smartcare/overviewScreen')">社区大屏</el-button>
      </div>
    </section>

    <el-row :gutter="16" class="summary-row">
      <el-col :xs="24" :sm="12" :lg="6"><div class="summary-card"><div class="summary-label">重点老人</div><div class="summary-value">{{ summary.focusCount }}</div><div class="summary-desc">风险分达到 60 分以上</div></div></el-col>
      <el-col :xs="24" :sm="12" :lg="6"><div class="summary-card warning"><div class="summary-label">紧急预警老人</div><div class="summary-value">{{ summary.urgentCount }}</div><div class="summary-desc">存在三级预警或紧急事件</div></div></el-col>
      <el-col :xs="24" :sm="12" :lg="6"><div class="summary-card danger"><div class="summary-label">工单积压老人</div><div class="summary-value">{{ summary.pendingCount }}</div><div class="summary-desc">未结工单 2 单及以上</div></div></el-col>
      <el-col :xs="24" :sm="12" :lg="6"><div class="summary-card info"><div class="summary-label">长期未巡访</div><div class="summary-value">{{ summary.noVisitCount }}</div><div class="summary-desc">7 天内无随访记录</div></div></el-col>
    </el-row>

    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="filters" label-width="88px">
        <el-form-item label="老人姓名"><el-input v-model="filters.keyword" clearable @keyup.enter="applyFilters" /></el-form-item>
        <el-form-item label="专项分组">
          <el-select v-model="filters.group" style="width: 180px">
            <el-option label="重点老人" value="focus" />
            <el-option label="全部对象" value="all" />
            <el-option label="紧急预警" value="urgent" />
            <el-option label="工单积压" value="pending" />
            <el-option label="长期未巡访" value="noVisit" />
            <el-option label="健康下降" value="lowHealth" />
          </el-select>
        </el-form-item>
        <el-form-item label="风险等级">
          <el-select v-model="filters.riskLevel" style="width: 180px">
            <el-option label="全部等级" value="all" />
            <el-option label="高风险" value="high" />
            <el-option label="中风险" value="medium" />
            <el-option label="低风险" value="low" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="applyFilters">筛选</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="16" class="content-row">
      <el-col :xs="24" :xl="17">
        <el-card shadow="hover" class="table-card">
          <template #header><div class="card-header"><span>专项对象清单</span><span class="header-note">共 {{ filteredList.length }} 人</span></div></template>
          <el-table :data="pagedList" stripe>
            <el-table-column label="老人" min-width="100"><template #default="{ row }"><el-button link type="primary" @click="goToElderly(row)">{{ row.elderlyName }}</el-button></template></el-table-column>
            <el-table-column label="纳管状态" width="110" align="center"><template #default="{ row }"><el-tag :type="managementStatusTagType(row.managementStatus)" effect="light">{{ managementStatusLabel(row.managementStatus) }}</el-tag></template></el-table-column>
            <el-table-column label="责任人" prop="responsiblePerson" width="100" align="center" />
            <el-table-column label="最近处置人" prop="latestHandler" width="110" align="center" />
            <el-table-column label="风险分" width="86" align="center"><template #default="{ row }"><el-tag :type="riskScoreTagType(row.riskScore)" effect="dark">{{ row.riskScore }}</el-tag></template></el-table-column>
            <el-table-column label="预警情况" min-width="150"><template #default="{ row }"><div>{{ row.alertCount }} 次 / {{ row.urgentAlertCount }} 次紧急</div><div class="sub-text">{{ formatDateTime(row.latestAlertTime) }}</div></template></el-table-column>
            <el-table-column label="纳管节点" min-width="170"><template #default="{ row }"><div>开始：{{ formatDateTime(row.managementStartTime) }}</div><div class="sub-text">下次随访：{{ formatDateTime(row.nextVisitTime) }}</div></template></el-table-column>
            <el-table-column label="跟进结果" prop="followUpResult" min-width="150" show-overflow-tooltip />
            <el-table-column label="解除原因" prop="releaseReason" min-width="150" show-overflow-tooltip />
            <el-table-column label="操作" width="320" fixed="right">
              <template #default="{ row }">
                <el-button link type="danger" @click="goToAlerts(row)">预警</el-button>
                <el-button link type="warning" @click="goToOrders(row)">工单</el-button>
                <el-button link type="success" @click="goToVisits(row)">巡访</el-button>
                <el-button link type="primary" @click="goToReports(row)">健康报告</el-button>
                <el-button link type="primary" @click="openManageDialog(row)">纳管</el-button>
              </template>
            </el-table-column>
          </el-table>
          <pagination v-show="filteredList.length > 0" :total="filteredList.length" v-model:page="pageNum" v-model:limit="pageSize" />
        </el-card>
      </el-col>

      <el-col :xs="24" :xl="7">
        <div class="side-column">
          <el-card shadow="hover" class="side-card">
            <template #header><div class="card-header"><span>处置建议</span></div></template>
            <div class="advice-list">
              <div v-for="item in recommendations" :key="item.title" class="advice-item">
                <div class="advice-title">{{ item.title }}</div>
                <div class="advice-desc">{{ item.desc }}</div>
              </div>
            </div>
          </el-card>
        </div>
      </el-col>
    </el-row>

    <el-dialog v-model="manageDialogOpen" title="专项管理维护" width="720px" append-to-body>
      <el-form :model="manageForm" label-width="100px">
        <el-form-item label="老人姓名"><el-input v-model="manageForm.elderlyName" disabled /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="纳管状态"><el-select v-model="manageForm.managementStatus" style="width: 100%"><el-option v-for="item in managementStatusOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="责任人"><el-input v-model="manageForm.responsiblePerson" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="纳管开始"><el-date-picker v-model="manageForm.managementStartTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="下次随访"><el-date-picker v-model="manageForm.nextVisitTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="最近处置人"><el-input v-model="manageForm.latestHandler" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="跟进结果"><el-input v-model="manageForm.followUpResult" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="干预措施"><el-input v-model="manageForm.interventionPlan" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="解除原因"><el-input v-model="manageForm.releaseReason" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer><div class="dialog-footer"><el-button type="primary" @click="submitManageForm">保存</el-button><el-button @click="manageDialogOpen = false">取消</el-button></div></template>
    </el-dialog>
  </div>
</template>

<script setup name="FocusManagement">
import { computed, getCurrentInstance, reactive, ref } from "vue"
import { useRouter } from "vue-router"
import { listAlertRecord } from "@/api/care/alertRecord"
import { getElderly, listElderly, updateElderly } from "@/api/care/elderly"
import { listHealthReport } from "@/api/care/healthReport"
import { listVisitRecord } from "@/api/care/visitRecord"
import { listWorkOrder } from "@/api/care/workOrder"
const { proxy } = getCurrentInstance()
const router = useRouter()
const loading = ref(false), pageNum = ref(1), pageSize = ref(10), rawList = ref([]), manageDialogOpen = ref(false), manageForm = ref({})
const managementStatusOptions = [{ label: "未纳管", value: "0" }, { label: "重点纳管", value: "1" }, { label: "高风险跟踪", value: "2" }, { label: "已解除", value: "3" }]
const filters = reactive({ keyword: "", group: "focus", riskLevel: "all" })
const summary = reactive({ focusCount: 0, urgentCount: 0, pendingCount: 0, noVisitCount: 0 })
const filteredList = computed(() => { let list = [...rawList.value]; if (filters.keyword) list = list.filter((i) => (i.elderlyName || "").includes(filters.keyword.trim())); if (filters.group !== "all") list = list.filter((i) => filters.group === "focus" ? i.riskScore >= 60 : filters.group === "urgent" ? i.urgentAlertCount > 0 : filters.group === "pending" ? i.pendingOrderCount >= 2 : filters.group === "noVisit" ? i.isLongNoVisit : i.latestHealthScore !== null && i.latestHealthScore < 70); if (filters.riskLevel !== "all") list = list.filter((i) => i.riskLevel === filters.riskLevel); return list.sort((a, b) => b.riskScore - a.riskScore) })
const pagedList = computed(() => filteredList.value.slice((pageNum.value - 1) * pageSize.value, pageNum.value * pageSize.value))
const recommendations = computed(() => [{ title: "先清紧急，再清积压", desc: `当前有 ${summary.urgentCount} 位老人存在紧急预警，建议先完成紧急处置。` }, { title: "巡访补位", desc: `当前有 ${summary.noVisitCount} 位老人超过 7 天未巡访，适合安排网格员或社区医生优先回访。` }, { title: "跟进留痕", desc: "建议同步维护跟进结果、最近处置人和解除原因，方便后续复盘和统计。" }])
function getList() { loading.value = true; Promise.all([listElderly({ pageNum: 1, pageSize: 500 }), listAlertRecord({ pageNum: 1, pageSize: 500 }), listWorkOrder({ pageNum: 1, pageSize: 500 }), listVisitRecord({ pageNum: 1, pageSize: 500 }), listHealthReport({ pageNum: 1, pageSize: 500 })]).then(([e, a, o, v, h]) => { rawList.value = (e.rows || []).map((item) => buildRow(item, a.rows || [], o.rows || [], v.rows || [], h.rows || [])); summary.focusCount = rawList.value.filter((i) => i.riskScore >= 60).length; summary.urgentCount = rawList.value.filter((i) => i.urgentAlertCount > 0).length; summary.pendingCount = rawList.value.filter((i) => i.pendingOrderCount >= 2).length; summary.noVisitCount = rawList.value.filter((i) => i.isLongNoVisit).length; pageNum.value = 1 }).finally(() => { loading.value = false }) }
function buildRow(elderly, alerts, orders, visits, reports) { const id = Number(elderly.elderlyId); const ea = alerts.filter((i) => Number(i.elderlyId) === id); const eo = orders.filter((i) => Number(i.elderlyId) === id); const ev = visits.filter((i) => Number(i.elderlyId) === id); const er = reports.filter((i) => Number(i.elderlyId) === id); const latestAlert = sortByDateDesc(ea, "createTime")[0]; const latestOrder = sortByDateDesc(eo, "createTime")[0]; const latestVisit = sortByDateDesc(ev, "visitTime")[0]; const latestReport = sortByDateDesc(er, "reportDate")[0]; const urgent = ea.filter((i) => Number(i.alertLevel) >= 2).length; const pending = eo.filter((i) => String(i.status) !== "2").length; const health = calcHealthScore(latestReport); const noVisit = latestVisit?.visitTime ? diffDays(new Date(), new Date(latestVisit.visitTime)) >= 7 : true; const risk = Math.min(100, ea.length * 8 + urgent * 18 + pending * 14 + (noVisit ? 15 : 0) + ((health !== null && health < 70) ? 18 : 0)); return { ...elderly, alertCount: ea.length, urgentAlertCount: urgent, pendingOrderCount: pending, latestAlertTime: latestAlert?.createTime || null, latestOrderTime: latestOrder?.createTime || null, latestVisitDate: latestVisit?.visitTime || null, latestHealthScore: health, riskScore: risk, riskLevel: risk >= 80 ? "high" : risk >= 50 ? "medium" : "low", isLongNoVisit: noVisit } }
function sortByDateDesc(list, field) { return [...list].sort((a, b) => new Date(b?.[field] || 0).getTime() - new Date(a?.[field] || 0).getTime()) }
function calcHealthScore(report) { if (!report) return null; const scores = [Number(report.sleepScore), Number(report.activityScore)].filter((i) => !Number.isNaN(i)); return scores.length ? Math.round(scores.reduce((s, i) => s + i, 0) / scores.length) : null }
function diffDays(end, start) { return Math.floor((end.getTime() - start.getTime()) / 86400000) }
function applyFilters() { pageNum.value = 1 }
function resetFilters() { filters.keyword = ""; filters.group = "focus"; filters.riskLevel = "all"; pageNum.value = 1 }
function managementStatusLabel(value) { return managementStatusOptions.find((i) => i.value === String(value ?? "0"))?.label || "未纳管" }
function managementStatusTagType(value) { const v = String(value ?? "0"); if (v === "2") return "danger"; if (v === "1") return "warning"; if (v === "3") return "success"; return "info" }
function riskScoreTagType(score) { if (score >= 80) return "danger"; if (score >= 50) return "warning"; return "success" }
function formatDateTime(value) { return value ? proxy.parseTime(value, "{y}-{m}-{d} {h}:{i}") : "--" }
function goTo(path, query = {}) { router.push({ path, query }) }
function goToElderly(row) { goTo("/smartcare/elderly", { elderlyId: row.elderlyId, elderlyName: row.elderlyName }) }
function goToAlerts(row) { goTo("/smartcare/alertRecord", { elderlyId: row.elderlyId, elderlyName: row.elderlyName }) }
function goToOrders(row) { goTo("/smartcare/workOrder", { elderlyId: row.elderlyId, elderlyName: row.elderlyName }) }
function goToVisits(row) { goTo("/smartcare/visitRecord", { elderlyId: row.elderlyId, elderlyName: row.elderlyName }) }
function goToReports(row) { goTo("/smartcare/healthReport", { elderlyId: row.elderlyId, elderlyName: row.elderlyName }) }
function openManageDialog(row) { getElderly(row.elderlyId).then((r) => { manageForm.value = { ...r.data }; manageDialogOpen.value = true }) }
function submitManageForm() { updateElderly(manageForm.value).then(() => { proxy.$modal.msgSuccess("专项信息已更新"); manageDialogOpen.value = false; getList() }) }
getList()
</script>

<style scoped>
.focus-page { background: linear-gradient(180deg, #f6f9ff 0%, #ffffff 42%, #f8fbf9 100%); min-height: 100%; }
.hero-panel { display: flex; justify-content: space-between; gap: 24px; align-items: flex-start; padding: 24px 28px; border-radius: 24px; margin-bottom: 18px; background: radial-gradient(circle at top left, rgba(39,110,241,.16), transparent 32%), radial-gradient(circle at top right, rgba(19,166,117,.14), transparent 28%), #0f2f4b; color: #fff; }
.hero-panel h1 { margin: 6px 0 10px; font-size: 30px; line-height: 1.2; }
.hero-panel p { margin: 0; max-width: 760px; color: rgba(255,255,255,.82); line-height: 1.8; }
.hero-eyebrow { font-size: 12px; letter-spacing: .18em; text-transform: uppercase; color: rgba(255,255,255,.7); }
.hero-actions { display: flex; gap: 12px; flex-wrap: wrap; }
.summary-row,.content-row { margin-bottom: 18px; }
.summary-card { border-radius: 20px; padding: 20px 22px; background: #fff; box-shadow: 0 14px 34px rgba(19,45,74,.08); }
.summary-card.warning { background: linear-gradient(135deg, #fff6e9, #ffffff); }
.summary-card.danger { background: linear-gradient(135deg, #fff0ee, #ffffff); }
.summary-card.info { background: linear-gradient(135deg, #eef7ff, #ffffff); }
.summary-label { color: #6b7a90; font-size: 14px; }
.summary-value { margin: 12px 0 8px; font-size: 34px; font-weight: 700; color: #17324f; }
.summary-desc,.sub-text { color: #94a0b2; font-size: 13px; }
.filter-card,.table-card,.side-card { border-radius: 18px; }
.filter-card { margin-bottom: 18px; }
.card-header { display: flex; align-items: center; justify-content: space-between; gap: 12px; font-weight: 600; }
.header-note { color: #97a3b5; font-size: 12px; }
.side-column,.advice-list { display: grid; gap: 16px; }
.advice-item { padding: 14px 16px; border-radius: 16px; background: #f8fbff; }
.advice-title { font-size: 15px; font-weight: 600; color: #16304c; margin-bottom: 6px; }
.advice-desc { color: #607188; line-height: 1.8; }
@media (max-width: 768px) { .hero-panel { flex-direction: column; } }
</style>
