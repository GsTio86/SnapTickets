<template>
  <el-container class="profile-container">
    <el-card class="profile-password-card">
      <h2>帳號資料</h2>
      <el-form :model="user" ref="userForm" label-width="100px">
        <el-form-item label="帳號">
          <el-input v-model="user.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="密碼">
          <el-input v-model="user.password" type="password" placeholder="••••••••" disabled></el-input>
          <el-button type="text" @click="goToChangePassword">更改密碼</el-button>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="user.name"></el-input>
        </el-form-item>
        <el-form-item label="電子郵件">
          <el-input v-model="user.email"></el-input>
        </el-form-item>
        <el-form-item label="手機號碼">
          <el-input v-model="user.phone"></el-input>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="user.address"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="updateUser">更新資料</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </el-container>
</template>

<script>
import { onMounted, ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import {ElMessage} from "element-plus";

export default {
  name: 'Profile',
  setup() {
    const router = useRouter();
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');
    const user = ref({
      username: '',
      password: '',
      name: '',
      email: '',
      phone: '',
      address: ''
    });

    const getUserData = async () => {
      try {
        const response = await axios.get(`${import.meta.env.VITE_API_URL}/user/info/${username}`, {
          headers: {
            Authorization: `Bearer ${token}`
          },
          withCredentials: true,
        });
        user.value = response.data;
      } catch (error) {
        ElMessage.error('無法獲得資料');
      }
    };

    const updateUser = async () => {
      try {
        await axios.put(`${import.meta.env.VITE_API_URL}/user/update/${username}`, user.value, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        ElMessage.success('更改資料成功');
      } catch (error) {
        ElMessage.error('更改資料失敗');
      }
    };

    const goToChangePassword = () => {
      if (token && username) {
        router.push('/user/change-password');
      } else {
        router.push('/login');
      }
    };

    onMounted(() => {
      if (!token || !username) {
        router.push('/login');
      } else {
        getUserData();
      }
    });

    return {
      user,
      updateUser,
      goToChangePassword
    };
  },
};
</script>

<style scoped>
.profile-container {
  display: flex;
  justify-content: center;
  padding-top: 20px;
  padding-left: 20px;
  padding-right: 20px;
  width: 100%;
  box-sizing: border-box;
}

.profile-password-card {
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

.update-button {
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
