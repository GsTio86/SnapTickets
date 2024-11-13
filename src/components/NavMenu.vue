<template>
  <el-header class="header">
    <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" :ellipsis="false" @select="handleSelect">
      <el-menu-item  @click="goToHome">
        <img style="width: 100px" src="../assets/logo.svg" alt="Logo"/>
      </el-menu-item>
      <el-menu-item index="/" @click="goToHome">首頁</el-menu-item>
      <el-menu-item index="/shop" @click="goToShop">訂票</el-menu-item>
      <el-sub-menu index="user-menu" style="margin-left: auto;">
        <template #title>
          <span class="account-label">帳號</span>
        </template>

        <el-menu-item v-if="!isLoggedIn"  index="/login" @click="goToLogin">登入</el-menu-item>
        <el-menu-item v-if="!isLoggedIn"  index="/register" @click="goToRegister">註冊</el-menu-item>

        <el-menu-item v-if="isLoggedIn"  index="/profile" @click="goToProfile">帳號資料</el-menu-item>
        <el-menu-item v-if="isLoggedIn"  index="/order" @click="goToUserOrders">訂單紀錄</el-menu-item>
        <el-menu-item v-if="isLoggedIn"  index="/ticket" @click="goToUserTickets">票券夾</el-menu-item>
        <el-menu-item v-if="isLoggedIn"  index="/logout" @click="doLogout">登出</el-menu-item>
      </el-sub-menu>
    </el-menu>
  </el-header>
</template>

<script>
import { inject, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from "element-plus";

export default {
  name: 'NavMenu',
  setup() {
    const router = useRouter();
    const route = useRoute();
    const activeIndex = ref(route.path);
    const isLoggedIn = inject('isLoggedIn');
    const setLoginStatus = inject('setLoginStatus');

    const handleSelect = (key) => {
      activeIndex.value = key;
    };

    const goToHome = () => {
      router.push('/');
      activeIndex.value = '/';
    };

    const goToShop = () => {
      router.push('/shop');
      activeIndex.value = '/shop';
    };


    const goToProfile = () => {
      router.push('/user/profile');
      activeIndex.value = '/profile';
    };

    const goToUserOrders = () => {
      router.push('/user/order');
      activeIndex.value = '/order';
    };

    const goToUserTickets = () => {
      router.push('/user/ticket');
      activeIndex.value = '/ticket';
    };

    const goToLogin = () => {
      router.push('/login');
      activeIndex.value = '/login';
    };

    const goToRegister = () => {
      router.push('/register');
      activeIndex.value = '/register';
    };

    const doLogout = () => {
      localStorage.removeItem('token');
      localStorage.removeItem('username');
      setLoginStatus(false);
      ElMessage.success('登出成功');
      goToHome();
    };

    watch(route, () => {
      activeIndex.value = route.path;
    });

    return {
      activeIndex,
      isLoggedIn,
      handleSelect,
      goToHome,
      goToProfile,
      goToShop,
      goToUserTickets,
      goToUserOrders,
      goToLogin,
      goToRegister,
      doLogout,
    };
  },
};
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-around;
  align-items: center;
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
}

.el-menu--horizontal {
  width: 100%;
  max-width: 100%;
  padding: 0;
  display: flex;
  justify-content: space-around;
}

.el-menu--horizontal .el-menu-item {
  flex-grow: 1;
  text-align: center;
  font-size: 0.9em;
}

.el-menu-item:nth-child(1) {
  margin-right: auto;
}


@media (max-width: 768px) {
  .nav-container {
    width: 100%;
    padding: 0 10px;
  }

  .header {
    flex-direction: column;
  }

  .el-menu--horizontal .el-menu-item {
    padding: 10px 5px;
    font-size: 0.8em;
  }

  .view-container {
    padding: 10px;
  }
}
</style>
