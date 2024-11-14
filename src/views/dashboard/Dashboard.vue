<template>
  <div class="dashboard">
    <el-container class="dashboard">
      <Sidebar/>
      <el-container>
        <el-header class="header">
          <div class="header-content">
            <span>後台管理面板</span>
            <div class="toolbar">
              <el-dropdown>
                <el-button class="account-dropdown-button" type="text">
                  <el-icon style="margin-right: 8px">
                    <Setting/>
                  </el-icon>
                  <span style="margin-left: 4px">帳號</span>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="$router.push('/dashboard/profile')">帳號資料</el-dropdown-item>
                    <el-dropdown-item @click="$router.push('/dashboard/change-password')">更改密碼</el-dropdown-item>
                    <el-dropdown-item :disabled="!isLoggedIn" @click="doLogout">登出</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-header>
        <el-main class="main-content">
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import {inject} from 'vue';
import {useRouter} from 'vue-router';
import {ElMessage} from 'element-plus';
import Sidebar from '@/components/Sidebar.vue';
import {Setting, ArrowDown} from '@element-plus/icons-vue';
import cookies from "vue-cookies";

export default {
  name: 'Dashboard',
  components: {
    Sidebar,
    Setting,
    ArrowDown: ArrowDown
  },
  setup() {
    const router = useRouter();

    const isLoggedIn = inject('isLoggedIn');
    const setLoginStatus = inject('setLoginStatus');

    const doLogout = () => {
      cookies.remove('login-user-token');
      cookies.remove('login-user');
      setLoginStatus(false);
      ElMessage.success('登出成功');
      router.push('/login');
    };

    return {
      isLoggedIn,
      doLogout
    };
  }
};
</script>

<style scoped>
.dashboard {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  padding: 20px;
  background-color: #f5f7fa;
  flex-grow: 1;
  overflow: auto;
}

.header {
  background-color: #409EFF;
  color: #fff;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: calc(100% - 200px);
  position: sticky;
  top: 0;
  z-index: 1000;
  left: 200px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  width: 100%;
  height: 100%;
}

.toolbar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.account-dropdown-button {
  color: #fff;
  font-weight: bold;
  display: inline-flex;
  align-items: center;
}

@media (max-width: 768px) {
  .main-content {
    margin-left: 0;
    padding-top: 50px;
  }
}
</style>
