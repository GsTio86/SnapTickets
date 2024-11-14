<template>
  <router-view></router-view>
</template>

<script>
import {onMounted, provide, ref} from "vue";
import cookies from "vue-cookies";

export default {
  name: 'App',
  setup() {
    const isLoggedIn = ref(false);
    const token = cookies.get('login-user-token');
    const username = cookies.get('login-user');

    const setLoginStatus = (status) => {
      isLoggedIn.value = status;
    };

    onMounted(() => {
      if (token && username) { // 檢查是否有 Token
        setLoginStatus(true);
      }
    });

    provide('isLoggedIn', isLoggedIn);
    provide('setLoginStatus', setLoginStatus);

    return {};
  }
};
</script>

<style>
body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f5f5;
}
</style>
