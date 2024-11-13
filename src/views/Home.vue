<template>
  <el-container>
    <el-main class="main-content">
      <div class="welcome-section">
        <h1>歡迎來到票務網站</h1>
        <p>在這裡您可以輕鬆訂購各種活動的票券！</p>
      </div>
      <el-row :gutter="20" class="ticket-cards-row">
        <el-col :span="24" :md="12" :lg="8" class="ticket-card-col" v-for="card in cards" :key="card.title">
          <el-card class="ticket-card">
            <img :src="card.image" class="card-image" alt="圖片">
            <div class="card-content">
              <h3>{{ card.title }}</h3>
              <p>{{ card.description }}</p>
              <el-button type="primary" @click="goToShop">立即訂票</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>

<script>
import { useRouter } from 'vue-router';
import ticketDemo from '../assets/ticket-demo.png';

export default {
  name: 'HomePage',
  setup() {
    const router = useRouter();

    const goToShop = () => {
      router.push('/shop');
    };

    const cards = [
      {
        title: '熱門活動',
        description: '參加我們的最新熱門活動，立即購票，數量有限！',
        image: ticketDemo,
      },
      {
        title: '即將推出',
        description: '不要錯過即將到來的精彩活動，提前預訂！',
        image: ticketDemo,
      },
      {
        title: '特惠票',
        description: '享受特別優惠票價，限時折扣，搶先預訂！',
        image: ticketDemo,
      },
    ];

    return {
      goToShop,
      cards,
    };
  },
};
</script>

<style>
.main-content {
  padding: 20px;
  text-align: center;
}

.welcome-section h1 {
  font-size: 2.5em;
  margin-bottom: 10px;
}

.welcome-section p {
  font-size: 1.2em;
  color: #666;
}

.ticket-card {
  border-radius: 10px;
  overflow: hidden;
}

.card-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.ticket-card-col {
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .welcome-section h1 {
    font-size: 2em;
  }

  .welcome-section p {
    font-size: 1em;
  }

  .ticket-card-col {
    margin-bottom: 20px;
    padding: 0 10px;
  }

  .ticket-cards-row {
    flex-direction: column;
    align-items: center;
  }
}
</style>
