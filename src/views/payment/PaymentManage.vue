<template>
  <div class="manage-payments">
    <el-card>
      <h2>管理付款資料</h2>
      <el-table :data="paginatedPayments" stripe style="width: 100%">
        <el-table-column label="付款編號" prop="paymentId"></el-table-column>
        <el-table-column label="訂單編號" prop="orderId"></el-table-column>
        <el-table-column label="付款方式" prop="paymentMethod">
          <template #default="scope">
            <span>{{ getMethodName(scope.row.paymentMethod) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="付款狀態" prop="paymentStatus">
          <template #default="scope">
            <span>{{ getStatusName(scope.row.paymentStatus) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="付款金額" prop="paymentAmount"></el-table-column>
        <el-table-column label="建立日期" prop="createdAt">
          <template #default="scope">
            <span>{{ new Date(scope.row.createdAt).toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column label="更新日期" prop="updatedAt">
          <template #default="scope">
            <span>{{ new Date(scope.row.updatedAt).toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" type="primary" @click="editPayment(scope.row)">編輯</el-button>
            <el-button size="small" type="danger" @click="confirmDelete(scope.row.paymentId)">刪除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
          v-if="payments.length > pageSize"
          :current-page="currentPage"
          :page-size="pageSize"
          :total="payments.length"
          background
          class="pagination"
          hide-on-single-page
          layout="prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </el-card>

    <el-dialog v-model="editDialogVisible" title="編輯付款資料" width="30%">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="狀態">
          <el-select v-model="editForm.paymentStatus" placeholder="選擇狀態">
            <el-option v-for="status in paymentStatuses" :key="status.value" :label="status.label"
                       :value="status.value"/>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updatePayment">儲存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {ref, onMounted, computed} from 'vue';
import axios from 'axios';
import {ElMessageBox, ElMessage} from 'element-plus';
import cookies from "vue-cookies";

export default {
  name: 'PaymentManage',
  setup() {
    const token = cookies.get('login-user-token');
    const payments = ref([]);
    const editDialogVisible = ref(false);
    const editForm = ref({
      paymentId: '',
      paymentStatus: '',
    });

    const paymentStatuses = [
      {value: 'PENDING', label: '待付款'},
      {value: 'CHECKING', label: '待確認'},
      {value: 'SIMULATING', label: '模擬付款'},
      {value: 'REFUND', label: '已退款'},
      {value: 'COMPLETED', label: '已付款'},
      {value: 'FAILED', label: '付款失敗'},
    ];

    const currentPage = ref(1);
    const pageSize = ref(12);

    const loadPayments = async () => {
      try {
        const response = await axios.get(`${import.meta.env.VITE_API_URL}/admin/payment/all`, {
          headers: {
            AdminAuthorization: `Bearer ${token}`
          }
        });
        payments.value = response.data.sort((a, b) => a.createdAt.localeCompare(b.createdAt)); // 按建立日期排序
      } catch (error) {
        ElMessage.error('無法取得付款資料');
      }
    };

    const paginatedPayments = computed(() => {
      const start = (currentPage.value - 1) * pageSize.value;
      return payments.value.slice(start, start + pageSize.value);
    });

    const handleSizeChange = (newSize) => {
      pageSize.value = newSize;
      currentPage.value = 1;
    };

    const handleCurrentChange = (newPage) => {
      currentPage.value = newPage;
    };

    const editPayment = (payment) => {
      editForm.value = {...payment};
      editDialogVisible.value = true;
    };

    const updatePayment = async () => {
      console.log(editForm.value.paymentStatus);

      try {
        await axios.put(`${import.meta.env.VITE_API_URL}/admin/payment/update/${editForm.value.paymentId}`, null, {
          headers: {
            AdminAuthorization: `Bearer ${token}`
          },
          params: {
            status: editForm.value.paymentStatus
          }
        });
        ElMessage.success('成功更新付款資料');
        editDialogVisible.value = false;
        await loadPayments();
      } catch (error) {
        ElMessage.error('更新付款資料失敗');
      }
    };

    const confirmDelete = async (paymentId) => {
      ElMessageBox.confirm('此操作將永久刪除該付款資料, 是否繼續?', '警告', {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        await deletePayment(paymentId);
      }).catch(() => {
        ElMessage.info('已取消刪除');
      });
    };

    const deletePayment = async (paymentId) => {
      try {
        await axios.delete(`${import.meta.env.VITE_API_URL}/admin/payment/delete/${paymentId}`, {
          headers: {
            AdminAuthorization: `Bearer ${token}`
          }
        });
        ElMessage.success('成功刪除付款資料');
        await loadPayments();
      } catch (error) {
        ElMessage.error('刪除付款資料失敗');
      }
    };

    const getStatusName = (status) => {
      const statusObj = paymentStatuses.find(item => item.value === status);
      return statusObj ? statusObj.label : status;
    };

    const getMethodName = (method) => {
      const methodMap = {
        ALL: '不指定',
        CREDIT: '信用卡',
        WEBATM: '網路轉帳',
        ATM: 'ATM轉帳',
        CVS: '超商代碼',
        BARCODE: '超商條碼',
        APPLEPAY: 'ApplePay'
      };
      return methodMap[method] || method;
    };

    onMounted(() => {
      if (!token) {
        router.push('/login');
      } else {
        loadPayments();
      }
    });

    return {
      payments,
      editDialogVisible,
      editForm,
      editPayment,
      updatePayment,
      confirmDelete,
      deletePayment,
      getStatusName,
      getMethodName,
      paymentStatuses,
      paginatedPayments,
      currentPage,
      pageSize,
      handleSizeChange,
      handleCurrentChange
    };
  }
};
</script>

<style scoped>
.manage-payments {
  padding: 20px;
  margin-left: 200px;
}

.el-card {
  margin-bottom: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  padding: 10px 0;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
