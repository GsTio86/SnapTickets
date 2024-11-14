<template>
  <div class="manage-tickets">
    <el-card>
      <h2>管理商品資料</h2>
      <el-button type="primary" @click="openCreateDialog">新建商品</el-button>

      <el-table :data="paginatedTickets" stripe style="width: 100%">
        <el-table-column label="票券編號" prop="ticketId"></el-table-column>
        <el-table-column label="名稱" prop="name"></el-table-column>
        <el-table-column label="描述" prop="description"></el-table-column>
        <el-table-column label="圖片">
          <template #default="scope">
            <el-image
                :key="scope.row.ticketId + '-' + Date.now()"
                :src="getImageUrl(scope.row.ticketId)"
                alt="Ticket Image"
                class="ticket-image">
              <template #error>
                <div class="image-slot">
                  <el-icon>
                    <Picture/>
                  </el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column label="價格" prop="price"></el-table-column>
        <el-table-column label="庫存" prop="stock"></el-table-column>
        <el-table-column label="開賣日期" prop="startDate">
          <template #default="scope">
            <span>{{ new Date(scope.row.startDate).toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column label="結束日期" prop="endDate">
          <template #default="scope">
            <span>{{ new Date(scope.row.endDate).toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column label="建立日期" prop="createdAt">
          <template #default="scope">
            <span>{{ new Date(scope.row.createdAt).toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column label="更新日期" prop="updatedAt">
          <template #default="scope">
            <span>{{ new Date(scope.row.updatedAt).toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" type="primary" @click="editTicket(scope.row)">編輯</el-button>
            <el-button size="small" type="danger" @click="confirmDelete(scope.row.ticketId)">刪除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
          v-if="tickets.length > pageSize"
          :current-page="currentPage"
          :page-size="pageSize"
          :total="tickets.length"
          background
          class="pagination"
          hide-on-single-page
          layout="prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </el-card>

    <el-dialog v-model="createDialogVisible" title="新建新商品" width="30%">
      <el-form ref="createFormRef" :model="createForm" :rules="createFormRules" hide-required-asterisk
               label-width="80px">
        <el-form-item label="名稱" prop="name">
          <el-input v-model="createForm.name"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="createForm.description" :autosize="{ minRows: 2, maxRows: 4 }" type="textarea"></el-input>
        </el-form-item>
        <el-form-item label="價格" prop="price">
          <el-input-number v-model="createForm.price" :min="1" min="0">
            <template #prefix>
              <span>$</span>
            </template>
          </el-input-number>
        </el-form-item>
        <el-form-item label="庫存" prop="stock">
          <el-input-number v-model="createForm.stock" min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="開賣時間" prop="startDate">
          <el-date-picker
              v-model="createForm.startDate"
              :disabled-date="startDateOptions"
              format="YYYY/MM/DD HH:mm:ss"
              placeholder="選擇開賣時間"
              type="datetime"
              value-format="YYYY/MM/DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="結束時間" prop="endDate">
          <el-date-picker
              v-model="createForm.endDate"
              :disabled-date="endDateOptions"
              format="YYYY/MM/DD HH:mm:ss"
              placeholder="選擇結束時間"
              type="datetime"
              value-format="YYYY/MM/DD HH:mm:ss"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="validateCreateForm">下一步</el-button>
      </div>
    </el-dialog>

    <el-dialog v-model="uploadDialogVisible" title="上傳票券圖片" width="30%">
      <el-upload
          :action="getUploadUrl(newTicketId)"
          :data="{ ticketId: newTicketId }"
          :headers="uploadHeaders"
          :show-file-list="false"
          name="image"
          @success="handleUploadSuccess"
      >
        <el-button type="primary">選擇圖片</el-button>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="finishCreation">完成</el-button>
      </div>
    </el-dialog>

    <el-dialog v-model="editDialogVisible" title="編輯票券資料" width="30%">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="名稱">
          <el-input v-model="editForm.name"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editForm.description" :autosize="{ minRows: 2, maxRows: 4 }" type="textarea"></el-input>
        </el-form-item>
        <el-form-item label="價格">
          <el-input-number v-model="editForm.price" :min="1" min="0">
            <template #prefix>
              <span>$</span>
            </template>
          </el-input-number>
        </el-form-item>
        <el-form-item label="庫存">
          <el-input-number v-model="editForm.stock" min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="開賣時間">
          <el-date-picker
              v-model="editForm.startDate"
              :disabled-date="startDateOptions"
              format="YYYY/MM/DD HH:mm:ss"
              placeholder="選擇開賣時間"
              type="datetime"
              value-format="YYYY/MM/DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="結束時間">
          <el-date-picker
              v-model="editForm.endDate"
              :disabled-date="editEndDateOptions"
              format="YYYY/MM/DD HH:mm:ss"
              placeholder="選擇結束時間"
              type="datetime"
              value-format="YYYY/MM/DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="上傳圖片">
          <el-upload
              :action="getUploadUrl(editForm.ticketId)"
              :data="{ ticketId: editForm.ticketId }"
              :headers="uploadHeaders"
              :show-file-list="false"
              name="image"
              @success="handleUploadSuccess"
          >
            <el-button type="primary">選擇圖片</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateTicket">儲存</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script lang="ts">
import {computed, onMounted, ref} from 'vue';
import axios from 'axios';
import {ElMessage, ElMessageBox} from 'element-plus';
import {format} from 'date-fns';
import cookies from "vue-cookies";

export default {
  name: 'ProductManage',
  setup() {
    const token = cookies.get('login-user-token');
    const tickets = ref([]);
    const editDialogVisible = ref(false);
    const createDialogVisible = ref(false);
    const uploadDialogVisible = ref(false);
    const createForm = ref({
      name: '',
      description: '',
      price: null,
      stock: null,
      startDate: '',
      endDate: ''
    });

    const createFormRules = ref({
      name: [
        {required: true, message: '請輸入名稱', trigger: 'blur'}
      ],
      description: [
        {required: true, message: '請輸入描述', trigger: 'blur'}
      ],
      price: [
        {required: true, message: '請輸入價格', trigger: 'change'}
      ],
      stock: [
        {required: true, message: '請輸入庫存', trigger: 'change'}
      ],
      startDate: [
        {required: true, message: '請選擇開賣時間', trigger: 'change'}
      ],
      endDate: [
        {required: true, message: '請選擇結束時間', trigger: 'change'}
      ]
    });

    const createFormRef = ref(null);

    const newTicketId = ref('');
    const editForm = ref({
      ticketId: '',
      name: '',
      description: '',
      price: null,
      stock: null,
      startDate: '',
      endDate: ''
    });

    const currentPage = ref(1);
    const pageSize = ref(12);

    const loadTickets = async () => {
      try {
        const response = await axios.get(`${import.meta.env.VITE_API_URL}/ticket/all`);
        tickets.value = response.data.sort((a, b) => a.createdAt.localeCompare(b.createdAt));
      } catch (error) {
        ElMessage.error('無法取得票券資料');
      }
    };

    const paginatedTickets = computed(() => {
      const start = (currentPage.value - 1) * pageSize.value;
      return tickets.value.slice(start, start + pageSize.value);
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
        name: '',
        description: '',
        price: null,
        stock: null,
        startDate: '',
        endDate: ''
      };
      createDialogVisible.value = true;
    };

    const validateCreateForm = () => {
      createFormRef.value.validate(async (valid) => {
        if (valid) {
          await createTicket();
        } else {
          ElMessage.error('請完整填寫商品內容');
        }
      });
    };

    const createTicket = async () => {
      if (createForm.value.startDate) {
        createForm.value.startDate = format(new Date(createForm.value.startDate), 'yyyy/MM/dd HH:mm:ss');
      }
      if (createForm.value.endDate) {
        createForm.value.endDate = format(new Date(createForm.value.endDate), 'yyyy/MM/dd HH:mm:ss');
      }
      if (new Date(createForm.value.endDate) < new Date(createForm.value.startDate)) {
        ElMessage.error('結束時間不能早於開賣日期');
        return;
      }
      try {
        const response = await axios.post(`${import.meta.env.VITE_API_URL}/admin/ticket/create`, createForm.value, {
          headers: {
            AdminAuthorization: `Bearer ${token}`
          }
        });
        newTicketId.value = response.data.ticketId;
        ElMessage.success('成功建立票券資料');
        createDialogVisible.value = false;
        uploadDialogVisible.value = true;
      } catch (error) {
        ElMessage.error('建立票券失敗');
      }
    };

    const editTicket = (ticket) => {
      editForm.value = {...ticket};
      editDialogVisible.value = true;
    };

    const updateTicket = async () => {
      const {ticketId, createdAt, updatedAt, ...ticketData} = editForm.value;
      if (ticketData.startDate) {
        ticketData.startDate = format(new Date(ticketData.startDate), 'yyyy/MM/dd HH:mm:ss');
      }
      if (ticketData.endDate) {
        ticketData.endDate = format(new Date(ticketData.endDate), 'yyyy/MM/dd HH:mm:ss');
      }
      if (new Date(ticketData.endDate) < new Date(ticketData.startDate)) {
        ElMessage.error('結束時間不能早於開賣日期');
        return;
      }
      try {
        await axios.put(`${import.meta.env.VITE_API_URL}/admin/ticket/update/${ticketId}`, ticketData, {
          headers: {
            AdminAuthorization: `Bearer ${token}`
          }
        });
        ElMessage.success('成功更新票券資料');
        editDialogVisible.value = false;
        await loadTickets();
      } catch (error) {
        console.error(error);
        ElMessage.error('更新票券資料失敗');
      }
    };

    const confirmDelete = async (ticketId) => {
      ElMessageBox.confirm('此操作將永久刪除該票券, 是否繼續?', '警告', {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        await deleteTicket(ticketId);
      }).catch(() => {
        ElMessage.info('已取消刪除');
      });
    };

    const deleteTicket = async (ticketId) => {
      try {
        await axios.delete(`${import.meta.env.VITE_API_URL}/admin/ticket/delete/${ticketId}`, {
          headers: {
            AdminAuthorization: `Bearer ${token}`
          }
        });
        ElMessage.success('成功刪除票券');
        await loadTickets();
      } catch (error) {
        ElMessage.error('刪除票券失敗');
      }
    };

    const getImageUrl = (ticketId) => {
      return `${import.meta.env.VITE_API_URL}/ticket/${ticketId}/200x200/image.png`;
    };

    const getUploadUrl = (ticketId) => {
      return `${import.meta.env.VITE_API_URL}/admin/ticket/upload/${ticketId}`;
    };

    const uploadHeaders = {
      AdminAuthorization: `Bearer ${token}`
    };

    const handleUploadSuccess = () => {
      ElMessage.success('圖片上傳成功');
      editDialogVisible.value = false;
      loadTickets();
    };

    const finishCreation = () => {
      uploadDialogVisible.value = false;
      loadTickets();
    };

    const startDateOptions = (time: Date) => {
      return time.getTime() < Date.now()
    };

    const endDateOptions = (time: Date) => {
      return time.getTime() < new Date(createForm.value.startDate).getTime();
    };

    const editEndDateOptions = (time: Date) => {
      return time.getTime() < new Date(editForm.value.startDate).getTime();
    };

    onMounted(() => {
      if (!token) {
        router.push('/login');
      } else {
        loadTickets();
      }
    });

    return {
      tickets,
      editDialogVisible,
      createDialogVisible,
      uploadDialogVisible,
      editForm,
      createForm,
      newTicketId,
      openCreateDialog,
      validateCreateForm,
      editTicket,
      createTicket,
      updateTicket,
      confirmDelete,
      deleteTicket,
      getImageUrl,
      getUploadUrl,
      handleUploadSuccess,
      finishCreation,
      paginatedTickets,
      currentPage,
      pageSize,
      handleSizeChange,
      handleCurrentChange,
      uploadHeaders,
      createFormRules,
      createFormRef,
      startDateOptions,
      endDateOptions,
      editEndDateOptions
    };
  }
};
</script>

<style scoped>
.manage-tickets {
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

.ticket-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
}

.ticket-image .image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background-color: var(--el-fill-color-light);
  color: var(--el-text-color-secondary);
  border-radius: 10px;
}

.ticket-image .image-slot .el-icon {
  font-size: 50px;
}
</style>
