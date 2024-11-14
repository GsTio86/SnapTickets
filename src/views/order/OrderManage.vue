<template>
  <div class="manage-orders">
    <el-card>
      <h2>管理訂單資料</h2>
      <el-table :data="paginatedOrders" stripe style="width: 100%">
        <el-table-column label="訂單編號" prop="orderId"></el-table-column>
        <el-table-column label="購買帳號" prop="username"></el-table-column>
        <el-table-column label="票券編號" prop="ticketId"></el-table-column>
        <el-table-column label="數量" prop="quantity" width="100"></el-table-column>
        <el-table-column label="總金額" prop="totalPrice" width="200"></el-table-column>
        <el-table-column label="狀態" prop="orderStatus" width="100">
          <template #default="scope">
            <span>{{ getStatusName(scope.row.orderStatus) }}</span>
          </template>
        </el-table-column>
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
            <el-button size="small" type="primary" @click="editOrder(scope.row)">編輯</el-button>
            <el-button size="small" type="danger" @click="confirmDelete(scope.row.orderId)">刪除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
          v-if="orders.length > pageSize"
          :current-page="currentPage"
          :page-size="pageSize"
          :pager-count="11"
          :total="orders.length"
          background
          class="pagination"
          hide-on-single-page
          layout="prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </el-card>

    <el-dialog v-model="editDialogVisible" title="編輯訂單資料" width="30%">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="狀態">
          <el-select v-model="editForm.orderStatus" placeholder="選擇狀態">
            <el-option v-for="status in orderStatuses" :key="status.value" :label="status.label" :value="status.value"/>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateOrder">儲存</el-button>
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
  name: 'OrderManage',
  setup() {
    const token = cookies.get('login-user-token');
    const orders = ref([]);
    const editDialogVisible = ref(false);
    const editForm = ref({
      orderId: '',
      orderStatus: '',
    });

    const orderStatuses = [
      {value: 'PENDING', label: '待付款'},
      {value: 'PAID', label: '已付款'},
      {value: 'COMPLETED', label: '已完成'},
      {value: 'CANCELLED', label: '已取消'},
    ];

    const currentPage = ref(1);
    const pageSize = ref(12);

    const loadOrders = async () => {
      try {
        const response = await axios.get(`${import.meta.env.VITE_API_URL}/admin/order/all`, {
          headers: {
            AdminAuthorization: `Bearer ${token}`
          }
        });
        orders.value = response.data.sort((a, b) => a.createdAt.localeCompare(b.createdAt)); // 按建立日期排序
      } catch (error) {
        ElMessage.error('無法取得訂單資料');
      }
    };

    const paginatedOrders = computed(() => {
      const start = (currentPage.value - 1) * pageSize.value;
      return orders.value.slice(start, start + pageSize.value);
    });

    const handleSizeChange = (newSize) => {
      pageSize.value = newSize;
      currentPage.value = 1;
    };

    const handleCurrentChange = (newPage) => {
      currentPage.value = newPage;
    };

    const editOrder = (order) => {
      editForm.value = {...order};
      editDialogVisible.value = true;
    };

    const updateOrder = async () => {
      try {
        await axios.post(`${import.meta.env.VITE_API_URL}/admin/order/update/${editForm.value.orderId}`,
            editForm.value.orderStatus,
            {
              headers: {
                AdminAuthorization: `Bearer ${token}`,
                'Content-Type': 'application/json'
              }
            }
        );
        ElMessage.success('成功更新訂單資料');
        editDialogVisible.value = false;
        await loadOrders();
      } catch (error) {
        ElMessage.error('更新訂單資料失敗');
      }
    };

    const confirmDelete = async (orderId) => {
      ElMessageBox.confirm('此操作將永久刪除該訂單, 是否繼續?', '警告', {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        await deleteOrder(orderId);
      }).catch(() => {
        ElMessage.info('已取消刪除');
      });
    };

    const deleteOrder = async (orderId) => {
      try {
        await axios.delete(`${import.meta.env.VITE_API_URL}/admin/order/delete/${orderId}`, {
          headers: {
            AdminAuthorization: `Bearer ${token}`
          }
        });
        ElMessage.success('成功刪除訂單');
        await loadOrders();
      } catch (error) {
        ElMessage.error('刪除訂單失敗');
      }
    };

    const getStatusName = (status) => {
      const statusObj = orderStatuses.find(item => item.value === status);
      return statusObj ? statusObj.label : status;
    };

    onMounted(() => {
      if (!token) {
        router.push('/login');
      } else {
        loadOrders();
      }
    });

    return {
      orders,
      editDialogVisible,
      editForm,
      editOrder,
      updateOrder,
      confirmDelete,
      deleteOrder,
      getStatusName,
      orderStatuses,
      paginatedOrders,
      currentPage,
      pageSize,
      handleSizeChange,
      handleCurrentChange
    };
  }
};
</script>

<style scoped>
.manage-orders {
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
