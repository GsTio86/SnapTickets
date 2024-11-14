<template>
  <div class="manage-users">
    <el-card>
      <h2>管理會員資料</h2>
      <el-table :data="paginatedUser" stripe style="width: 100%">
        <el-table-column label="帳號" prop="username" width="150"></el-table-column>
        <el-table-column label="姓名" prop="name" width="150"></el-table-column>
        <el-table-column label="電子郵件" prop="email"></el-table-column>
        <el-table-column label="手機號碼" prop="phone"></el-table-column>
        <el-table-column label="地址" prop="address"></el-table-column>
        <el-table-column label="建立日期" prop="createdAt" width="180">
          <template #default="scope">
            <span>{{ new Date(scope.row.createdAt).toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" type="primary" @click="editUser(scope.row)">編輯</el-button>
            <el-button size="small" type="danger" @click="confirmDelete(scope.row.username)">刪除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          v-if="users.length > pageSize"
          :current-page="currentPage"
          :page-size="pageSize"
          :pager-count="11"
          :total="users.length"
          background
          class="pagination"
          hide-on-single-page
          layout="prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </el-card>

    <el-dialog v-model="editDialogVisible" title="編輯會員資料" width="30%">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="editForm.name"></el-input>
        </el-form-item>
        <el-form-item label="電子郵件">
          <el-input v-model="editForm.email"></el-input>
        </el-form-item>
        <el-form-item label="手機號碼">
          <el-input v-model="editForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="editForm.address"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateUser">儲存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {ref, onMounted, computed} from 'vue';
import axios from 'axios';
import {ElMessageBox, ElMessage} from 'element-plus';
import cookies from "vue-cookies";

export default {
  name: 'MemberManage',
  setup() {
    const token = cookies.get('login-user-token');
    const username = cookies.get('login-user');

    const users = ref([]);
    const editDialogVisible = ref(false);
    const editForm = ref({
      username: '',
      name: '',
      email: '',
      phone: '',
      address: ''
    });

    const currentPage = ref(1);
    const pageSize = ref(15);

    const loadUsers = async () => {
      try {
        const response = await axios.get(`${import.meta.env.VITE_API_URL}/admin/user/all`, {
          headers: {
            AdminAuthorization: `Bearer ${token}`
          }
        });
        users.value = response.data.sort((a, b) => a.username.localeCompare(b.username)); // 按字母順序排序
      } catch (error) {
        ElMessage.error('無法取得會員資料');
      }
    };

    const paginatedUser = computed(() => {
      const start = (currentPage.value - 1) * pageSize.value;
      return users.value.slice(start, start + pageSize.value);
    });

    const handleSizeChange = (newSize) => {
      pageSize.value = newSize;
      currentPage.value = 1;
    };

    const handleCurrentChange = (newPage) => {
      currentPage.value = newPage;
    };

    const editUser = (user) => {
      editForm.value = {...user};
      editDialogVisible.value = true;
    };

    const updateUser = async () => {
      try {
        await axios.put(`${import.meta.env.VITE_API_URL}/admin/user/update`, editForm.value, {
          headers: {
            AdminAuthorization: `Bearer ${token}`
          }
        });
        ElMessage.success('成功更新會員資料');
        editDialogVisible.value = false;
        await loadUsers();
      } catch (error) {
        ElMessage.error('更新會員資料失敗');
      }
    };

    const confirmDelete = async (username) => {
      ElMessageBox.confirm('此操作將永久刪除該帳號, 是否繼續?', '警告', {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        await deleteUser(username);
      }).catch(() => {
        ElMessage.info('已取消刪除');
      });
    };

    const deleteUser = async (username) => {
      try {
        await axios.delete(`${import.meta.env.VITE_API_URL}/admin/user/delete/${username}`, {
          headers: {
            AdminAuthorization: `Bearer ${token}`
          }
        });
        ElMessage.success('成功刪除帳號');
        await loadUsers();
      } catch (error) {
        ElMessage.error('刪除帳號失敗');
      }
    };

    onMounted(() => {
      if (!token || !username) {
        router.push('/login');
      } else {
        loadUsers();
      }
    });

    return {
      users,
      editDialogVisible,
      editForm,
      editUser,
      updateUser,
      confirmDelete,
      deleteUser,
      paginatedUser,
      currentPage,
      pageSize,
      handleSizeChange,
      handleCurrentChange
    };
  }
}
</script>

<style scoped>
.manage-users {
  padding: 20px;
  margin-left: 200px;
}

.el-card {
  margin-bottom: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  padding: 10px 0;
}
</style>
