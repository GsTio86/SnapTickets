<template>
  <el-container class="login-container">
    <el-card class="login-card">
      <h2>帳號登入</h2>
      <el-form ref="loginForm" :model="form" :rules="rules" hide-required-asterisk label-width="auto" status-icon>
        <el-form-item label="帳號" prop="identifier">
          <el-input v-model="form.identifier" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="密碼" prop="password">
          <el-input v-model="form.password" autocomplete="off" type="password"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button :disabled="isLoggingIn" class="login-button" type="primary" @click="tryLogin">登入</el-button>
          <el-button class="forgot-password-button" type="text" @click="goToResetPassword">忘記密碼？</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </el-container>
</template>

<script>
import {inject, ref} from 'vue';
import {ElMessage} from 'element-plus';
import axios from "axios";
import {useRouter} from 'vue-router';
import cookies from "vue-cookies";

export default {
  name: 'Login',
  setup() {
    const router = useRouter();
    const isLoggingIn = ref(false);
    const setLoginStatus = inject('setLoginStatus');

    const form = ref({
      identifier: '',
      password: ''
    });

    const rules = {
      identifier: [{required: true, message: '請輸入帳號或電子郵件', trigger: 'blur'}],
      password: [{required: true, message: '請輸入密碼', trigger: 'blur'}]
    };

    const tryLogin = async () => {
      isLoggingIn.value = true;
      try {
        const response = await axios.post(`${import.meta.env.VITE_API_URL}/auth/login`, null, {
          params: {
            identifier: form.value.identifier,
            password: form.value.password
          }
        });

        cookies.set('auth-token', response.data.token); // 儲存 token 到 cookie
        cookies.set('auth-user', response.data.username); // 儲存使用者名稱到 cookie
        setLoginStatus(true); // 設定登入狀態
        ElMessage.success('登入成功');
        setTimeout(() => { // 登入成功後跳轉到首頁
          isLoggingIn.value = false;
          router.push('/');
        }, 500);
      } catch (error) {
        isLoggingIn.value = false;
        ElMessage.error('登入失敗，請檢查帳號或密碼');
      }
    };

    const goToResetPassword = () => {
      router.push('/reset-password');
    };

    return {
      form,
      rules,
      isLoggingIn,
      tryLogin,
      goToResetPassword
    };
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  padding-top: 20px;
  padding-left: 20px;
  padding-right: 20px;
  width: 100%;
  box-sizing: border-box;
}

.login-card {
  width: 100%;
  max-width: 400px;
  padding: 20px;
  box-sizing: border-box;
}

h2 {
  font-size: 1.8em;
  text-align: center;
  margin-bottom: 20px;
}

.login-button {
  width: 100%;
}

.forgot-password-button {
  position: absolute;
  right: 10px;
  top: 35px;
}

@media (max-width: 768px) {
  .login-card {
    padding: 15px;
  }

  h2 {
    font-size: 1.5em;
  }
}
</style>
