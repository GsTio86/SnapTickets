<template>
  <el-container>
    <el-main>
      <h2>票券夾</h2>
      <el-tabs v-model="activeTab" @tab-click="filterTickets">
        <el-tab-pane label="未使用" name="unused"></el-tab-pane>
        <el-tab-pane label="已使用" name="used"></el-tab-pane>
      </el-tabs>
      <div class="ticket-list">
        <div
            v-for="order in filteredTickets"
            :key="order.orderId"
            class="ticket-item"
        >
          <div class="ticket-summary" @click="toggleTicketDetails(order.orderId)">
            <el-image
                :src="getImageUrl(order.ticket.ticketId, 75, 75)"
                class="ticket-image"
                alt="票券圖片">
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div class="ticket-info">
              <h3 class="ticket-name">{{ order.name }}</h3>
              <p class="ticket-price">${{ order.ticket.price * order.tickets.length }} 元</p>
              <el-tag v-if="order.status === 'USED'" type="info" class="status-tag">已使用</el-tag>
              <el-tag v-else type="success" class="status-tag">未使用</el-tag>
            </div>
            <div class="ticket-date">
              <p>📆 {{ formatDate(order.issuedAt) }}</p>
              <p class="ticket-quantity">{{ order.tickets.length }} 張</p>
            </div>
          </div>

          <transition name="fade">
            <div v-if="currentOrderId === order.orderId" class="ticket-details-dropdown">
              <h4>票券明細：</h4>
              <div v-for="ticket in order.tickets" :key="ticket.code" class="ticket-detail">
                <p @click.stop="showQRCode(ticket.code)" class="ticket-code">票號: {{ formatUserTicketId(ticket.code) }}</p>
              </div>
            </div>
          </transition>
        </div>
      </div>
      <el-dialog v-model="showQRDialog" width="300px" center>
        <template #title>票券 QR Code</template>
        <div class="qrcode-container">
          <qrcode-vue :value="currentQRCode" :size="150" level="L" :margin="1" />
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="showQRDialog = false">關閉</el-button>
        </span>
      </el-dialog>
    </el-main>
  </el-container>
</template>

<script>
import { ref, onMounted, watch } from 'vue';
import axios from 'axios';
import QrcodeVue from 'qrcode.vue';
import { Picture } from '@element-plus/icons-vue'
import cookies from "vue-cookies";

export default {
  name: 'Tickets',
  components: { QrcodeVue },
  setup() {
    const token = cookies.get('auth-token');
    const username = cookies.get('auth-user');

    const tickets = ref([]);
    const groupedTickets = ref([]);
    const filteredTickets = ref([]);
    const loading = ref(true);
    const showQRDialog = ref(false);
    const currentQRCode = ref('');
    const currentOrderId = ref(null);
    const activeTab = ref("unused");

    const loadTickets = async () => {
      try {
        const response = await axios.get(`${import.meta.env.VITE_API_URL}/user/ticket/${username}`, {
          headers: {
            Authorization: `Bearer ${token}`
          },
          withCredentials: true,
        });
        tickets.value = response.data;
        groupTickets();
        filterTickets();
      } catch (error) {
        console.error('無法取得票券資料', error);
      } finally {
        loading.value = false;
      }
    };

    const groupTickets = () => {
      const orders = {};
      tickets.value.forEach((ticket) => {
        if (!orders[ticket.orderId]) {
          orders[ticket.orderId] = {
            orderId: ticket.orderId,
            name: ticket.ticket.name,
            ticket: ticket.ticket,
            issuedAt: ticket.issuedAt,
            status: ticket.status,
            tickets: [],
          };
        }
        orders[ticket.orderId].tickets.push(ticket);
      });
      groupedTickets.value = Object.values(orders);
    };

    const filterTickets = () => {
      filteredTickets.value = groupedTickets.value.filter((order) =>
          activeTab.value === "unused" ? order.status === "NOT_USED" : order.status === "USED"
      );
    };

    const getImageUrl = (ticketId, width, height) => {
      return `${import.meta.env.VITE_API_URL}/ticket/${ticketId}/${width}x${height}/image.png`;
    };

    const formatUserTicketId = (userticketId) => {
      return userticketId.replace(/^USER_TICKET/, '');
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

    const toggleTicketDetails = (orderId) => {
      currentOrderId.value = currentOrderId.value === orderId ? null : orderId;
    };

    const showQRCode = (code) => {
      currentQRCode.value = code;
      showQRDialog.value = true;
    };

    onMounted(()=> {
      if (!token || !username) {
        router.push('/login');
      } else {
        loadTickets();
      }
    });

    watch(activeTab, filterTickets);

    return {
      tickets,
      groupedTickets,
      filteredTickets,
      loading,
      formatDate,
      showQRDialog,
      currentQRCode,
      showQRCode,
      toggleTicketDetails,
      formatUserTicketId,
      getImageUrl,
      currentOrderId,
      activeTab,
    };
  },
};
</script>

<style scoped>
h2 {
  font-size: 1.8em;
  margin-bottom: 20px;
  text-align: center;
}

.ticket-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.ticket-item {
  display: flex;
  flex-direction: column;
  padding: 10px;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
}

.ticket-summary {
  display: flex;
  align-items: center;
}

.ticket-image {
  width: 80px;
  height: 80px;
  border-radius: 5px;
  margin-right: 15px;
  object-fit: cover;
  border-radius: 10px;
  border: 1px solid #e0e0e0;
}

.ticket-image .image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background-color: var(--el-fill-color-light);
  color: var(--el-text-color-secondary);
  border-radius: 10px;
}

.ticket-image .image-slot .el-icon {
  font-size: 35px;
}

.ticket-info {
  flex: 1;
}

.ticket-name {
  font-size: 1.2em;
  margin: 0;
}

.ticket-price {
  color: #666;
}

.ticket-date {
  margin-left: auto;
  text-align: right;
  font-size: 0.9em;
  color: #999;
}

.status-tag {
  margin-left: auto;
}

.ticket-details-dropdown {
  margin-top: 10px;
  padding-left: 65px;
  border-left: 3px solid #409EFF;
}

.ticket-detail {
  cursor: pointer;
  padding: 5px 0;
}

.ticket-code {
  color: #409EFF;
  cursor: pointer;
}

.qrcode-container {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

@media (max-width: 768px) {
  h2 {
    font-size: 1.5em;
  }
}
</style>
