<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>管理員後台登入</h2>
      <el-form ref="loginForm" :model="form" :rules="rules" hide-required-asterisk label-width="auto" status-icon>
        <el-form-item label="帳號" prop="identifier">
          <el-input v-model="form.identifier" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="密碼" prop="password">
          <el-input v-model="form.password" autocomplete="off" type="password"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button :disabled="isLoggingIn" class="login-button" type="primary" @click="tryLogin">登入</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
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
      identifier: [{required: true, message: '請輸入帳號', trigger: 'blur'}],
      password: [{required: true, message: '請輸入密碼', trigger: 'blur'}]
    };

    const tryLogin = async () => {
      isLoggingIn.value = true;
      try {
        const response = await axios.post(`${import.meta.env.VITE_API_URL}/admin/auth/login`, null, {
          params: {
            identifier: form.value.identifier,
            password: form.value.password
          }
        });
        cookies.set('login-user-token', response.data.token, "30min");
        cookies.set('login-user', response.data.username, "30min");
        setLoginStatus(true);
        ElMessage.success('登入成功');
        setTimeout(() => {
          isLoggingIn.value = false;
          router.push('/dashboard');
        }, 300);
      } catch (error) {
        isLoggingIn.value = false;
        console.error(error);
        ElMessage.error('登入失敗，請檢查帳號或密碼');
      }
    };

    return {
      form,
      rules,
      isLoggingIn,
      tryLogin,
    };
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background-color: #0763bf;
}

.login-card {
  width: 400px;
}

@media (max-width: 768px) {
  .login-card {
    width: 90%;
  }
}
</style>
