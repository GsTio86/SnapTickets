<template>
  <div class="manage-admins">
    <el-card>
      <h2>後台帳號管理</h2>
      <el-button :disabled="!isAdmin" type="primary" @click="openCreateDialog">新建後台帳號</el-button>

      <el-table :data="paginatedAdmins" stripe style="width: 100%">
        <el-table-column label="帳號" prop="username"></el-table-column>
        <el-table-column label="姓名" prop="name"></el-table-column>
        <el-table-column label="電子郵件" prop="email"></el-table-column>
        <el-table-column label="權限" prop="permission">
          <template #default="scope">
            <span>{{ scope.row.permission === 'ADMIN' ? '管理員' : '員工' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" type="primary" @click="editAdmin(scope.row)">編輯</el-button>
            <el-button :disabled="!isAdmin" size="small" type="danger" @click="confirmDelete(scope.row.username)">刪除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
          v-if="admins.length > pageSize"
          :current-page="currentPage"
          :page-size="pageSize"
          :total="admins.length"
          background
          class="pagination"
          hide-on-single-page
          layout="prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </el-card>

    <el-dialog v-model="createDialogVisible" title="新建後台帳號" width="30%">
      <el-form ref="createFormRef" :model="createForm" :rules="createFormRules" hide-required-asterisk
               label-width="80px">
        <el-form-item label="帳號" prop="username">
          <el-input v-model="createForm.username"></el-input>
        </el-form-item>
        <el-form-item label="密碼" prop="password">
          <el-input v-model="createForm.password" autocomplete="off" show-password type="password"></el-input>
        </el-form-item>
        <el-form-item label="確認密碼" prop="confirmPassword">
          <el-input v-model="createForm.confirmPassword" autocomplete="off" show-password type="password"></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="createForm.name"></el-input>
        </el-form-item>
        <el-form-item label="電子郵件" prop="email">
          <el-input v-model="createForm.email"></el-input>
        </el-form-item>
        <el-form-item label="權限" prop="permission">
          <el-select v-model="createForm.permission" placeholder="選擇權限">
            <el-option label="管理員" value="ADMIN"></el-option>
            <el-option label="員工" value="MOD"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="validateCreateForm">建立</el-button>
      </div>
    </el-dialog>

    <el-dialog v-model="editDialogVisible" title="編輯後台帳號" width="30%">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="帳號">
          <el-input v-model="editForm.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="editForm.name"></el-input>
        </el-form-item>
        <el-form-item label="電子郵件">
          <el-input v-model="editForm.email"></el-input>
        </el-form-item>
        <el-form-item label="權限">
          <el-select v-model="editForm.permission" placeholder="選擇權限">
            <el-option label="管理員" value="ADMIN"></el-option>
            <el-option label="員工" value="MOD"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateAdmin">儲存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import {computed, onMounted, ref} from 'vue';
import axios from 'axios';
import {ElMessage, ElMessageBox} from 'element-plus';
import cookies from "vue-cookies";

export default {
  name: 'AdminManage',
  setup() {
    const token = cookies.get('login-user-token');
    const username = cookies.get('login-user');
    const admins = ref([]);
    const editDialogVisible = ref(false);
    const createDialogVisible = ref(false);
    const createForm = ref({
      username: '',
      password: '',
      confirmPassword: '',
      name: '',
      email: '',
      permission: 'MOD'
    });

    const createFormRules = ref({
      username: [
        {required: true, message: '請輸入帳號', trigger: 'blur'}
      ],
      password: [
        {required: true, message: '請輸入密碼', trigger: 'blur'},
        {min: 8, message: '密碼長度至少8位', trigger: 'blur'},
        {
          pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/,
          message: '密碼需包含大小寫字母、數字和特殊符號',
          trigger: 'blur'
        }
      ],
      confirmPassword: [
        {required: true, message: '請確認密碼', trigger: 'blur'},
        {
          validator: (rule, value, callback) => {
            if (value !== createForm.value.password) {
              callback(new Error('兩次輸入的密碼不一致'));
            } else {
              callback();
            }
          }, trigger: 'blur'
        }
      ],
      name: [
        {required: true, message: '請輸入姓名', trigger: 'blur'}
      ],
      email: [
        {required: true, message: '請輸入電子郵件', trigger: 'blur'},
        {type: 'email', message: '請輸入有效的電子郵件地址', trigger: 'blur'}
      ],
      permission: [
        {required: true, message: '請選擇權限', trigger: 'change'}
      ]
    });

    const createFormRef = ref(null);

    const editForm = ref({
      username: '',
      name: '',
      email: '',
      permission: 'MOD'
    });

    const currentPage = ref(1);
    const pageSize = ref(12);
    const userPermission = ref('');

    const loadAdmins = async () => {
      try {
        const response = await axios.get(`${import.meta.env.VITE_API_URL}/admin/account/all`, {
          headers: {
            AdminAuthorization: `Bearer ${token}`
          }
        });
        admins.value = response.data;
      } catch (error) {
        ElMessage.error('無法取得後台帳號資料');
      }
    };

    const loadUserPermission = async () => {
      try {
        const response = await axios.get(`${import.meta.env.VITE_API_URL}/admin/account/info/${username}`, {
          headers: {
            AdminAuthorization: `Bearer ${token}`
          }
        });
        userPermission.value = response.data.permission;
      } catch (error) {
        ElMessage.error('無法取得帳號權限');
      }
    };

    const paginatedAdmins = computed(() => {
      const start = (currentPage.value - 1) * pageSize.value;
      return admins.value.slice(start, start + pageSize.value);
    });

    const handleSizeChange = (newSize) => {
      pageSize.value = newSize;
      currentPage.value = 1;
    };

    const handleCurrentChange = (newPage) => {
      currentPage.value = newPage;
    };

    const openCreateDialog = () => {
      createForm.value = {
        username: '',
        password: '',
        confirmPassword: '',
        name: '',
        email: '',
        permission: 'MOD'
      };
      createDialogVisible.value = true;
    };

    const validateCreateForm = () => {
      createFormRef.value.validate(async (valid) => {
        if (valid) {
          await createAdmin();
        } else {
          ElMessage.error('請完整填寫管理員資料');
        }
      });
    };

    const createAdmin = async () => {
      try {
        await axios.post(`${import.meta.env.VITE_API_URL}/admin/auth/register`, createForm.value, {
          headers: {
            AdminAuthorization: `Bearer ${token}`
          }
        });
        ElMessage.success('成功建立管理員帳號');
        createDialogVisible.value = false;
        await loadAdmins();
      } catch (error) {
        ElMessage.error('建立管理員帳號失敗');
      }
    };

    const editAdmin = (admin) => {
      editForm.value = {...admin};
      editDialogVisible.value = true;
    };

    const updateAdmin = async () => {
      const username = cookies.get('login-user');
      try {
        await axios.put(`${import.meta.env.VITE_API_URL}/admin/account/update/${username}`, editForm.value, {
          headers: {
            AdminAuthorization: `Bearer ${token}`
          }
        });
        ElMessage.success('成功更新管理員資料');
        editDialogVisible.value = false;
        await loadAdmins();
      } catch (error) {
        ElMessage.error('更新管理員資料失敗');
      }
    };

    const confirmDelete = async (username) => {
      const loggedInUsername = cookies.get('login-user');
      if (username === loggedInUsername) {
        ElMessage.error('無法刪除當前登入帳號');
        return;
      }
      ElMessageBox.confirm('此操作將永久刪除該管理員帳號, 是否繼續?', '警告', {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        await deleteAdmin(username);
      }).catch(() => {
        ElMessage.info('已取消刪除');
      });
    };

    const deleteAdmin = async (username) => {
      try {
        await axios.delete(`${import.meta.env.VITE_API_URL}/admin/account/delete/${username}`, {
          headers: {
            AdminAuthorization: `Bearer ${token}`
          }
        });
        ElMessage.success('成功刪除後台帳號');
        await loadAdmins();
      } catch (error) {
        ElMessage.error('刪除後台帳號失敗');
      }
    };

    const isAdmin = computed(() => userPermission.value === 'ADMIN');

    onMounted(() => {
      if (!token) {
        router.push('/login');
      } else {
        loadAdmins();
        loadUserPermission();
      }
    });

    return {
      admins,
      editDialogVisible,
      createDialogVisible,
      editForm,
      createForm,
      openCreateDialog,
      validateCreateForm,
      editAdmin,
      createAdmin,
      updateAdmin,
      confirmDelete,
      deleteAdmin,
      paginatedAdmins,
      currentPage,
      pageSize,
      handleSizeChange,
      handleCurrentChange,
      createFormRules,
      createFormRef,
      isAdmin
    };
  }
};
</script>

<style scoped>
.manage-admins {
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
