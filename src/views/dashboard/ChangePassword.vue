<template>
  <el-container class="change-password-container">
    <el-card class="change-password-card">
      <h2>更改密碼</h2>
      <el-form ref="resetPasswordForm" :model="form" :rules="rules" hide-required-asterisk label-width="auto">
        <el-form-item label="舊密碼" prop="oldPassword">
          <el-input v-model="form.oldPassword" autocomplete="off" show-password type="password"></el-input>
        </el-form-item>
        <el-form-item label="新密碼" prop="newPassword">
          <el-input v-model="form.newPassword" autocomplete="off" show-password type="password"></el-input>
        </el-form-item>
        <el-form-item label="確認新密碼" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" autocomplete="off" show-password type="password"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button class="reset-button" type="primary" @click="changePassword">更改密碼</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </el-container>
</template>

<script>
import {ref, onMounted, inject} from 'vue';
import {useRouter} from 'vue-router';
import {ElMessage} from 'element-plus';
import axios from 'axios';
import cookies from "vue-cookies";

export default {
  name: 'ChangePassword',
  setup() {
    const router = useRouter();
    const token = cookies.get('login-user-token');
    const username = cookies.get('login-user');
    const setLoginStatus = inject('setLoginStatus');

    const form = ref({
      username: '',
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    });

    const rules = {
      oldPassword: [{required: true, message: '請輸入舊密碼', trigger: 'blur'}],
      newPassword: [
        {required: true, message: '請輸入新密碼', trigger: 'blur'},
        {min: 8, message: '密碼長度需至少8位', trigger: 'blur'},
        {
          pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]+$/,
          message: '密碼需包含大小寫字母、數字及特殊符號'
        }
      ],
      confirmPassword: [
        {required: true, message: '請再次輸入新密碼', trigger: 'blur'},
        {
          validator: (rule, value, callback) => {
            if (value !== form.value.newPassword) {
              callback(new Error('兩次輸入的新密碼不一致'));
            } else {
              callback();
            }
          },
          trigger: 'blur'
        }
      ]
    };

    const changePassword = async () => {
      if (form.value.newPassword !== form.value.confirmPassword) {
        ElMessage.error('新密碼與確認密碼不一致');
        return;
      }

      try {
        await axios.put(`${import.meta.env.VITE_API_URL}/admin/account/change-password/${username}`, null, {
              params: {
                oldPassword: form.value.oldPassword,
                newPassword: form.value.newPassword
              },
              headers: {
                AdminAuthorization: `Bearer ${token}`
              }
            }
        );
        ElMessage.success('密碼更改成功，請重新登入');
        sessionStorage.removeItem('login-user-token');
        sessionStorage.removeItem('login-user');
        setLoginStatus(false);
        await router.push('/login');
      } catch (error) {
        ElMessage.error(error.response?.data || '密碼更改失敗');
      }
    };

    onMounted(() => {
      if (!token || !username) {
        router.push('/login');
      } else {
        form.value.username = username;
      }
    });

    return {
      form,
      rules,
      changePassword
    };
  }
};
</script>

<style scoped>
.change-password-container {
  display: flex;
  justify-content: center;
  padding-top: 20px;
  padding-left: 20px;
  padding-right: 20px;
  width: 100%;
  box-sizing: border-box;
}

.change-password-card {
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

.reset-button {
  width: 100%;
}

@media (max-width: 768px) {
  .reset-password-card {
    padding: 15px;
  }

  h2 {
    font-size: 1.5em;
  }
}
</style>
