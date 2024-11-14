<template>
  <div class="statistics">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="revenue-card">
          <el-icon>
            <el-icon-money/>
          </el-icon>
          <div>
            <h3>本月營收</h3>
            <p>{{ monthlyRevenue }} 元</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="best-selling-card">
          <el-icon>
            <el-icon-goods/>
          </el-icon>
          <div>
            <h3>本月賣出最多的票券</h3>
            <p>{{ bestSellingTicket }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="chart-card">
      <el-row>
        <el-col :span="6">
          <el-select v-model="timePeriod" placeholder="選擇時間範圍" @change="fetchChartData">
            <el-option label="天" value="day"></el-option>
            <el-option label="月" value="month"></el-option>
            <el-option label="年" value="year"></el-option>
          </el-select>
        </el-col>
      </el-row>
      <div ref="chart" class="chart"></div>
    </el-card>
  </div>
</template>

<script>
import {ref, onMounted, nextTick} from 'vue';
import * as echarts from 'echarts';
import axios from 'axios';
import cookies from "vue-cookies";

export default {
  name: 'Statistics',
  setup() {
    const token = cookies.get('login-user-token');
    const monthlyRevenue = ref(0);
    const bestSellingTicket = ref('');
    const timePeriod = ref('day');
    const chart = ref(null);
    const orders = ref([]);
    const payments = ref([]);

    const initializeChart = async () => {
      await nextTick();
      chart.value = echarts.init(chart.value);
      fetchChartData();
    };

    const loadOrderData = async () => {
      try {
        const response = await axios.get(`${import.meta.env.VITE_API_URL}/admin/order/all`, {
          headers: {
            AdminAuthorization: `Bearer ${token}`
          }
        });
        orders.value = response.data;
        await calculateStatistics();
        fetchChartData();
      } catch (error) {
        console.error('Error loading orders:', error);
      }
    };

    const loadPaymentData = async () => {
      try {
        const response = await axios.get(`${import.meta.env.VITE_API_URL}/admin/payment/all`, {
          headers: {
            AdminAuthorization: `Bearer ${token}`
          }
        });
        payments.value = response.data;
        calculateMonthlyRevenue();
      } catch (error) {
        console.error('Error loading payments:', error);
      }
    };

    const calculateStatistics = async () => {
      const ticketSales = {};
      orders.value.forEach(order => {
        if (order.orderStatus === 'COMPLETED') {
          ticketSales[order.ticketId] = (ticketSales[order.ticketId] || 0) + order.quantity;
        }
      });

      const bestSellingTicketId = Object.keys(ticketSales).reduce((a, b) => (ticketSales[a] > ticketSales[b] ? a : b), '');
      if (bestSellingTicketId) {
        try {
          const response = await axios.get(`${import.meta.env.VITE_API_URL}/ticket/info/${bestSellingTicketId}`, {
            headers: {
              AdminAuthorization: `Bearer ${token}`
            }
          });
          bestSellingTicket.value = response.data.name;
        } catch (error) {
          console.error('Error fetching ticket info:', error);
        }
      }
    };

    const calculateMonthlyRevenue = () => {
      monthlyRevenue.value = payments.value
          .filter(payment => payment.paymentStatus === 'COMPLETED')
          .reduce((total, payment) => total + parseFloat(payment.paymentAmount), 0);
    };

    const fetchChartData = () => {
      const salesAmount = [];
      const soldQuantity = [];
      const labels = [];

      const now = new Date();

      if (timePeriod.value === 'day') { // 日統計 當月所有天數
        const year = now.getFullYear();
        const month = now.getMonth();
        const daysInMonth = new Date(year, month + 1, 0).getDate();
        for (let day = 1; day <= daysInMonth; day++) {
          labels.push(`${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`);
          salesAmount.push(0);
          soldQuantity.push(0);
        }
      } else if (timePeriod.value === 'month') { // 月統計 當年度 12個月
        const year = now.getFullYear();
        for (let month = 1; month <= 12; month++) {
          labels.push(`${year}-${String(month).padStart(2, '0')}`);
          salesAmount.push(0);
          soldQuantity.push(0);
        }
      } else if (timePeriod.value === 'year') { // 年統計 近6年
        const currentYear = now.getFullYear();
        for (let year = currentYear - 5; year <= currentYear; year++) {
          labels.push(`${year}`);
          salesAmount.push(0);
          soldQuantity.push(0);
        }
      }

      orders.value.forEach(order => {
        if (order.orderStatus === 'COMPLETED') {
          const orderDate = new Date(order.createdAt);
          let label;

          if (timePeriod.value === 'day') {
            label = orderDate.toISOString().split('T')[0];
          } else if (timePeriod.value === 'month') {
            label = `${orderDate.getFullYear()}-${String(orderDate.getMonth() + 1).padStart(2, '0')}`;
          } else if (timePeriod.value === 'year') {
            label = `${orderDate.getFullYear()}`;
          }

          const index = labels.indexOf(label);
          if (index !== -1) {
            salesAmount[index] += parseFloat(order.totalPrice);
            soldQuantity[index] += order.quantity;
          }
        }
      });

      // 統計圖表 設定
      chart.value.setOption({
        title: {text: '銷售統計'},
        tooltip: {trigger: 'axis'},
        legend: {data: ['銷售金額', '賣出數量']},
        xAxis: {type: 'category', data: labels},
        yAxis: {type: 'value'},
        series: [
          {
            name: '銷售金額',
            type: 'line',
            data: salesAmount
          },
          {
            name: '賣出數量',
            type: 'line',
            data: soldQuantity
          },
        ]
      });
    };

    onMounted(() => {
      if (!token) {
        router.push('/login');
      } else {
        initializeChart();
        loadOrderData();
        loadPaymentData();
      }
    });

    return {
      monthlyRevenue,
      bestSellingTicket,
      timePeriod,
      chart,
      fetchChartData
    };
  }
};
</script>

<style scoped>
.statistics {
  padding: 20px;
  margin-left: 200px;
}

.revenue-card, .best-selling-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.revenue-card div, .best-selling-card div {
  margin-left: 15px;
}

.chart-card {
  width: 100%;
  margin-top: 20px;
}

.chart {
  width: 100%;
  height: 400px;
}
</style>
