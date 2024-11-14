<template>
  <el-container>
    <el-main class="main-content">
      <h1>所有票券</h1>
      <el-row :gutter="20" v-if="paginatedTickets.length > 0">
        <el-col :span="24" :sm="24" :md="12" :lg="8" class="ticket-card-col" v-for="ticket in paginatedTickets" :key="ticket.ticketId">
          <el-card class="ticket-card">
            <el-image
                :src="getImageUrl(ticket.ticketId, 300, 200)"
                class="card-image"
                alt="圖片">
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div class="card-content">
              <h3>{{ ticket.name }}</h3>
              <p>{{ ticket.description }}</p>
              <p>價格: {{ ticket.price }} 元</p>
              <p>剩餘數量: {{ ticket.stock }}</p>
              <p>開賣時間: {{ formatDate(ticket.startDate) }}</p>
              <el-button type="primary" @click="goToShopTicket(ticket)">查看</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <div v-else class="no-tickets">載入中...</div>
      <el-pagination
          v-if="tickets.length > pageSize"
          :current-page="currentPage"
          :page-size="pageSize"
          :total="tickets.length"
          layout="prev, pager, next, jumper"
          background
          class="pagination"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </el-main>
  </el-container>
</template>

<script>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { Picture } from '@element-plus/icons-vue';

export default {
  name: 'ViewTickets',
  setup() {
    const router = useRouter();
    const tickets = ref([]);
    const currentPage = ref(1);
    const pageSize = ref(6);

    const getImageUrl = (ticketId, width, height) => {
      return `${import.meta.env.VITE_API_URL}/ticket/${ticketId}/${width}x${height}/image.png`;
    };

    const formatDate = (date) => {
      const options = { year: 'numeric', month: 'long', day: 'numeric' };
      return new Date(date).toLocaleDateString('zh-TW', options);
    };

    const goToShopTicket = (ticket) => {
      router.push(`/shop/${ticket.ticketId}`);
    };

    onMounted(() => {
      axios.get(`${import.meta.env.VITE_API_URL}/ticket/all`)
          .then(response => {
            tickets.value = response.data;
          })
          .catch(error => {
            ElMessage.error('無法獲取票券資料');
          });
    });

    const paginatedTickets = computed(() => {
      const start = (currentPage.value - 1) * pageSize.value;
      return tickets.value.slice(start, start + pageSize.value);
    });

    const handleSizeChange = (newSize) => {
      pageSize.value = newSize;
      currentPage.value = 1; // Reset to first page on page size change
    };

    const handleCurrentChange = (newPage) => {
      currentPage.value = newPage;
    };

    return {
      tickets,
      getImageUrl,
      formatDate,
      goToShopTicket,
      paginatedTickets,
      currentPage,
      pageSize,
      handleSizeChange,
      handleCurrentChange
    };
  }
};
</script>

<style scoped>
.main-content {
  padding: 20px;
  text-align: center;
}

.ticket-card {
  border-radius: 10px;
  overflow: hidden;
}

.ticket-card-col {
  margin-bottom: 20px;
}

.card-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.card-content {
  padding: 14px;
}

.no-tickets {
  font-size: 1.5em;
  color: #999;
  margin-top: 20px;
}

.card-image .image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: var(--el-fill-color-light);
  color: var(--el-text-color-secondary);
  font-size: 30px;
}
.card-image .image-slot .el-icon {
  font-size: 30px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}

@media (max-width: 768px) {
  .main-content {
    padding: 10px;
  }

  .ticket-card-col {
    width: 100%;
    margin-bottom: 20px;
  }

  .card-content h3 {
    font-size: 1.2em;
    margin-bottom: 8px;
  }

  .card-content p {
    font-size: 0.9em;
    color: #666;
    margin-bottom: 6px;
  }

  .el-button {
    margin-top: 10px;
    width: 100%;
    font-size: 0.9em;
  }

  h1 {
    font-size: 1.8em;
  }

  .ticket-card {
    margin-bottom: 15px;
  }
}
</style>
