<template>
  <el-container class="register-container">
    <el-card class="register-card">
      <h2>註冊會員帳號</h2>
      <el-form ref="registerForm" :model="form" :rules="rules" hide-required-asterisk label-width="auto">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="帳號" prop="username">
          <el-input v-model="form.username" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="密碼" prop="password">
          <el-input v-model="form.password" autocomplete="off" show-password type="password"></el-input>
        </el-form-item>
        <el-form-item label="確認密碼" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" autocomplete="off" show-password type="password"></el-input>
        </el-form-item>
        <el-form-item label="電子郵件" prop="email">
          <el-input v-model="form.email" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="手機號碼" prop="phone">
          <el-input v-model="form.phone" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button :disabled="isRegisterSuccess" class="register-button" type="primary" @click="tryRegister">註冊
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </el-container>
</template>

<script>
import {ref} from 'vue';
import {ElMessage} from 'element-plus';
import axios from 'axios';
import {useRouter} from 'vue-router';

export default {
  name: 'Register',
  setup() {
    const router = useRouter();
    const isRegisterSuccess = ref(false);
    const form = ref({
      username: '',
      password: '',
      confirmPassword: '',
      name: '',
      email: '',
      phone: '',
      address: ''
    });

    const rules = {
      name: [{required: true, message: '請輸入姓名', trigger: 'blur'}],
      username: [{required: true, message: '請輸入帳號', trigger: 'blur'}],
      password: [
        {required: true, message: '請輸入密碼', trigger: 'blur'},
        {min: 8, message: '密碼長度至少為8位', trigger: 'blur'},
        {
          validator: (rule, value, callback) => {
            const passwordRegex = /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
            if (!passwordRegex.test(value)) {
              callback(new Error('密碼需包含大小寫字母、數字及特殊符號'));
            } else {
              callback();
            }
          },
          trigger: 'blur'
        }
      ],
      confirmPassword: [
        {required: true, message: '請再次輸入密碼', trigger: 'blur'},
        {
          validator: (rule, value, callback) => {
            if (value !== form.value.password) {
              callback(new Error('兩次輸入的密碼不一致'));
            } else {
              callback();
            }
          },
          trigger: 'blur'
        }
      ],
      email: [
        {required: true, message: '請輸入信箱', trigger: 'blur'},
        {type: 'email', message: '電子郵件格式不正確', trigger: 'blur'}
      ],
      phone: [{required: true, message: '請輸入手機號碼', trigger: 'blur'}],
      address: [{required: true, message: '請輸入地址', trigger: 'blur'}]
    };

    const tryRegister = async () => {
      isRegisterSuccess.value = true;
      try {
        await axios.post(`${import.meta.env.VITE_API_URL}/auth/register`, {
          name: form.value.name,
          username: form.value.username,
          password: form.value.password,
          email: form.value.email,
          phone: form.value.phone,
          address: form.value.address
        });
        ElMessage.success('註冊成功');
        setTimeout(() => {
          isRegisterSuccess.value = false;
          router.push('/login');
        }, 1000);
      } catch (error) {
        isRegisterSuccess.value = false;
        ElMessage.error(error);
      }
    };

    return {
      form,
      rules,
      isRegisterSuccess,
      tryRegister,
    };
  }
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  padding-top: 20px;
  padding-left: 20px;
  padding-right: 20px;
  width: 100%;
  box-sizing: border-box;
}

.register-card {
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

.register-button {
  width: 100%;
}

@media (max-width: 768px) {
  .register-card {
    padding: 15px;
  }

  h2 {
    font-size: 1.5em;
  }
}
</style>
