<template>
  <div class="app-container">
    <div class="nav-container">
      <NavMenu />
    </div>
    <div class="view-container">
      <router-view />
    </div>
  </div>
</template>

<script>
import { ref, provide, onMounted } from 'vue';
import NavMenu from "@/components/NavMenu.vue";
import cookies from "vue-cookies";

export default {
  name: 'App',
  components: {
    NavMenu,
  },
  setup() {
    const isLoggedIn = ref(false);

    const setLoginStatus = (status) => {
      isLoggedIn.value = status;
    };

    onMounted(() => {
      const token = cookies.get('auth-token');
      const username = cookies.get('auth-user');
      if (token && username) { // 檢查是否有 Token 和使用者名稱
        setLoginStatus(true);
      }
    });

    provide('isLoggedIn', isLoggedIn);
    provide('setLoginStatus', setLoginStatus);

    return {};
  },
};
</script>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0;
  box-sizing: border-box;
}

.nav-container {
  width: 100%;
  margin-bottom: 10px;
}

.view-container {
  width: 100%;
  padding: 10px;
  max-width: 1000px;
  box-sizing: border-box;
}

@media (max-width: 768px) {
  .app-container {
    padding: 0 10px;
  }
  .nav-container,
  .view-container {
    width: 100%;
    max-width: 100%;
  }
}
</style>
