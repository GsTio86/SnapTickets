<template>
  <el-container>
    <el-main class="ticket-detail">
      <el-card v-if="ticket" class="ticket-card">
        <el-row class="ticket-content">
          <el-col :span="8" class="image-gallery">
            <el-image
                :src="getImageUrl(ticket.ticketId, 300, 300)"
                class="main-image"
                alt="圖片">
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </el-col>
          <el-col :span="16" class="ticket-info">
            <h2 class="ticket-title">{{ ticket.name }}</h2>
            <p class="ticket-description">{{ ticket.description }}</p>
            <p class="ticket-price">
              <span class="sale-price">{{ ticket.price }} 元</span>
            </p>
            <p class="ticket-stock">剩餘數量: <strong>{{ ticket.stock }}</strong></p>
            <p class="ticket-date">開賣日期: <strong>{{ formatDate(ticket.startDate) }}</strong></p>

            <el-alert v-if="!isAvailableForPurchase" title="尚未開賣，請稍後再試" type="warning" show-icon></el-alert>

            <el-form ref="orderForm" label-width="100px" class="order-form">
              <el-form-item label="數量" prop="quantity">
                <el-input-number v-model="order.quantity" :min="1" :max="ticket.stock" />
              </el-form-item>
              <el-form-item label="付款方式" prop="paymentMethod">
                <el-select v-model="order.paymentMethod" placeholder="選擇付款方式">
                  <el-option label="信用卡" value="CREDIT"></el-option>
                  <el-option label="超商代碼" value="CVS"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item>
                <div class="total-and-checkout">
                  <p class="total-amount">總金額: <strong>{{ calculateTotal() }} 元</strong></p>
                  <el-button type="danger" class="checkout-button" @click="confirmOrder" :disabled="!isAvailableForPurchase">直接購買</el-button>
                </div>
              </el-form-item>
            </el-form>
          </el-col>
        </el-row>
      </el-card>
      <div v-else class="loading">載入中...</div>
    </el-main>
  </el-container>
</template>

<script>
import { ref, onMounted, computed, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { Picture } from '@element-plus/icons-vue'

export default {
  name: 'DetailTicket',
  setup() {
    const route = useRoute();
    const router = useRouter();

    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');

    const ticket = ref(null);
    const order = ref({
      quantity: 1,
      paymentMethod: '',
    });
    const now = ref(new Date());
    let intervalId;

    const getImageUrl = (ticketId, width, height) => {
      return `${import.meta.env.VITE_API_URL}/ticket/${ticketId}/${width}x${height}/image.png`;
    };

    const formatDate = (date) => {
      const options = {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false,
      };
      return new Date(date).toLocaleString('zh-TW', options);
    };

    const calculateTotal = () => {
      return (ticket.value.price * order.value.quantity).toFixed(2);
    };

    const confirmOrder = () => {
      if (!token || !username) {
        // 如果未登入，顯示提示並跳轉到登入頁面
        alert("尚未登入，將跳轉到登入頁面");
        router.push({ path: '/login', query: { redirect: route.fullPath } });
        return;
      }

      if (!order.value.paymentMethod) {
        alert("請選擇付款方式");
        return;
      }

      const orderDto = {
        username: username, // 這裡應使用登入的使用者名稱
        ticketId: ticket.value.ticketId,
        quantity: order.value.quantity,
        totalPrice: calculateTotal(),
        paymentMethod: order.value.paymentMethod.toUpperCase(),
      };

      axios.post(`${import.meta.env.VITE_API_URL}/order/checkout`, orderDto, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        withCredentials: true,
      }).then((response) => {
        document.open();
        document.write(response.data);
        document.close();
      })
          .catch((error) => {
            console.error("無法建立付款頁面", error);
            alert("訂單建立失敗");
          });
    };

    const isAvailableForPurchase = computed(() => {
      return ticket.value && new Date(ticket.value.startDate) <= now.value;
    });

    const startTimer = () => {
      intervalId = setInterval(() => {
        now.value = new Date();
      }, 1000);
    };

    onMounted(() => {
      const ticketId = route.params.ticketId;
      axios.get(`${import.meta.env.VITE_API_URL}/ticket/info/${ticketId}`)
          .then(response => {
            ticket.value = response.data;
            startTimer();  // 開始倒數計時
          })
          .catch(error => {
            router.push('/shop');
          });
    });

    onUnmounted(() => {
      clearInterval(intervalId);
    });

    return {
      ticket,
      order,
      getImageUrl,
      formatDate,
      calculateTotal,
      confirmOrder,
      isAvailableForPurchase,
    };
  },
};
</script>

<style scoped>
.ticket-detail {
  padding: 20px;
  display: flex;
  justify-content: center;
}

.ticket-card {
  width: 800px;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.ticket-content {
  display: flex;
  align-items: flex-start;
}

.image-gallery {
  display: flex;
  justify-content: center;
}

.main-image {
  width: 300px;
  height: 300px;
  border-radius: 10px;
  border: 1px solid #e0e0e0;
}

.main-image .image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background-color: var(--el-fill-color-light);
  color: var(--el-text-color-secondary);
  border-radius: 10px;
}

.main-image .image-slot .el-icon {
  font-size: 50px;
}

.ticket-info {
  padding-left: 20px;
}

.ticket-title {
  font-size: 1.8em;
  margin-bottom: 10px;
}

.ticket-description {
  font-size: 1.2em;
  color: #666;
  margin-bottom: 15px;
}

.ticket-price {
  font-size: 1.4em;
  color: #f56c6c;
  margin-bottom: 10px;
}

.original-price {
  font-size: 1em;
  color: #999;
  margin-right: 10px;
}

.sale-price {
  font-size: 1.4em;
  color: #ff4444;
}

.ticket-stock,
.ticket-date {
  font-size: 1.1em;
  margin-bottom: 8px;
}

.order-form {
  margin-top: 20px;
}

.total-and-checkout {
  display: flex;
  align-items: flex-start;
  flex-direction: column;
}

.total-amount {
  font-size: 1.2em;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.checkout-button {
  font-size: 1em;
  padding: 10px 20px;
  width: 100%;
}

.loading {
  font-size: 1.5em;
  color: #999;
  text-align: center;
  margin-top: 20px;
}
</style>