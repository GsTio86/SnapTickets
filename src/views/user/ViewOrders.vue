<template>
  <el-container>
    <el-main>
      <h2>訂單紀錄</h2>
      <el-table v-if="!isMobile" :data="paginatedOrders" v-loading="loading" style="width: 100%">
        <el-table-column prop="orderId" label="訂單編號" width="150">
          <template #default="scope">
            {{ formatOrderId(scope.row.orderId) }}
          </template>
        </el-table-column>
        <el-table-column prop="ticket.name" label="票券名稱" />
        <el-table-column prop="quantity" label="購買數量" width="100" />
        <el-table-column prop="totalPrice" label="總金額" width="120">
          <template #default="scope">{{ scope.row.totalPrice }} 元</template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="訂單狀態" width="120">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.orderStatus)">
              {{ getStatusText(scope.row.orderStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="建立時間" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
      </el-table>
      <div v-else class="order-cards">
        <el-card v-for="order in paginatedOrders" :key="order.orderId" class="order-card">
          <h3>訂單編號: {{ formatOrderId(order.orderId) }}</h3>
          <p>票券名稱: {{ order.ticket.name }}</p>
          <p>購買數量: {{ order.quantity }}</p>
          <p>總金額: {{ order.totalPrice }} 元</p>
          <el-tag :type="getStatusTagType(order.orderStatus)">
            {{ getStatusText(order.orderStatus) }}
          </el-tag>
          <p>建立時間: {{ formatDate(order.createdAt) }}</p>
        </el-card>
      </div>
      <el-config-provider :locale="zhTw">
        <el-pagination
            v-if="orders.length > pageSize"
            background
            hide-on-single-page
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="currentPage"
            :page-size="pageSize"
            :pager-count="11"
            :total="orders.length"
            layout="prev, pager, next, jumper"
            class="pagination"
        />
      </el-config-provider>
    </el-main>
  </el-container>
</template>

<script>
import axios from 'axios';
import { ref, onMounted, computed } from 'vue';
import { ElConfigProvider } from 'element-plus'
import {zhTw} from "element-plus/es/locale/index";

export default {
  name: 'Orders',
  computed: {
    zhTw() {
      return zhTw
    }
  },
  setup() {
    const orders = ref([]);
    const loading = ref(true);
    const isMobile = ref(window.innerWidth <= 768);

    const currentPage = ref(1);
    const pageSize = ref(10);

    const fetchOrders = async () => {
      const username = localStorage.getItem('username');
      const token = localStorage.getItem('token');

      if (!token || !username) {
        await router.push('/login');
        return;
      }

      try {
        const response = await axios.get(`${import.meta.env.VITE_API_URL}/user/order/${username}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
          withCredentials: true,
        });
        orders.value = response.data;
      } catch (error) {
        console.error('無法取得訂單資料', error);
      } finally {
        loading.value = false;
      }
    };

    const getStatusText = (status) => {
      const statusMap = {
        PENDING: '待付款',
        PAID: '已付款',
        COMPLETED: '已完成',
        CANCELLED: '已取消',
      };
      return statusMap[status] || status;
    };

    const getStatusTagType = (status) => {
      const tagTypeMap = {
        PENDING: 'warning',
        PAID: 'info',
        COMPLETED: 'success',
        CANCELLED: 'danger',
      };
      return tagTypeMap[status] || 'info';
    };

    const formatOrderId = (orderId) => {
      return orderId.replace(/^ORDER/, '');
    };

    const formatDate = (dateString) => {
      const date = new Date(dateString);
      return new Intl.DateTimeFormat('zh-TW', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
      }).format(date);
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

    onMounted(fetchOrders);

    window.addEventListener('resize', () => {
      isMobile.value = window.innerWidth <= 768;
    });

    return {
      orders,
      loading,
      getStatusText,
      getStatusTagType,
      formatOrderId,
      formatDate,
      isMobile,
      currentPage,
      pageSize,
      paginatedOrders,
      handleSizeChange,
      handleCurrentChange,
    };
  },
};
</script>

<style scoped>
h2 {
  font-size: 1.8em;
  margin-bottom: 20px;
}

.order-cards {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.order-card h3 {
  margin: 0;
  font-size: 1.2em;
  font-weight: bold;
}

.order-card p {
  margin: 5px 0;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}

@media (max-width: 768px) {
  h2 {
    font-size: 1.5em;
    text-align: center;
  }
}
</style>
