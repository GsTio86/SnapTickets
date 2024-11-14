<template>
  <el-container class="profile-container">
    <el-card class="profile-card">
      <h2>帳號資料</h2>
      <el-form ref="userForm" :model="user" label-width="100px">
        <el-form-item label="帳號">
          <el-input v-model="user.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="user.name"></el-input>
        </el-form-item>
        <el-form-item label="電子郵件">
          <el-input v-model="user.email"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="updateUser">更新資料</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </el-container>
</template>

<script>
import {onMounted, ref} from 'vue';
import axios from 'axios';
import {useRouter} from 'vue-router';
import {ElMessage} from 'element-plus';
import cookies from "vue-cookies";

export default {
  name: 'Profile',
  setup() {
    const router = useRouter();
    const token = cookies.get('login-user-token');
    const username = cookies.get('login-user');
    const user = ref({
      username: '',
      name: '',
      email: ''
    });

    const getUserData = async () => {
      try {
        const response = await axios.get(`${import.meta.env.VITE_API_URL}/admin/account/info/${username}`, {
          headers: {
            AdminAuthorization: `Bearer ${token}`
          },
          withCredentials: true
        });
        user.value = response.data;
      } catch (error) {
        ElMessage.error('無法獲得資料');
      }
    };

    const updateUser = async () => {
      try {
        await axios.put(`${import.meta.env.VITE_API_URL}/admin/account/update/${username}`, user.value, {
          headers: {
            AdminAuthorization: `Bearer ${token}`
          }
        });
        ElMessage.success('更改資料成功');
      } catch (error) {
        ElMessage.error('更改資料失敗');
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
      updateUser
    };
  }
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

.profile-card {
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
  .profile-card {
    padding: 15px;
  }

  h2 {
    font-size: 1.5em;
  }
}
</style>
