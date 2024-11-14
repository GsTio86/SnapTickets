<template>
  <el-aside width="200px" class="sidebar">
    <el-scrollbar>
      <el-menu
          :default-active="$route.path"
          class="el-menu-vertical-demo"
          active-text-color="#ffd04b"
          background-color="#384454"
          text-color="#bfcbd9"
          style="height: 100%">
        <el-menu-item index="/dashboard" @click="$router.push('/dashboard')">
          <el-icon><HomeFilled /></el-icon>
          <span>首頁</span>
        </el-menu-item>
        <el-sub-menu index="1">
          <template #title>
            <el-icon><TakeawayBox /></el-icon>
            <span>商品</span>
          </template>
          <el-menu-item index="/dashboard/product/manage" @click="$router.push('/dashboard/product/manage')">管理商品</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="2">
          <template #title>
            <el-icon><Tickets /></el-icon>
            <span>訂單</span>
          </template>
          <el-menu-item index="/dashboard/order/manage" @click="$router.push('/dashboard/order/manage')">管理訂單</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="3">
          <template #title>
            <el-icon><CreditCard /></el-icon>
            <span>付款</span>
          </template>
          <el-menu-item index="/dashboard/payment/manage" @click="$router.push('/dashboard/payment/manage')">管理付款</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="4">
          <template #title>
            <el-icon><User /></el-icon>
            <span>會員</span>
          </template>
          <el-menu-item index="/dashboard/member/manage" @click="$router.push('/dashboard/member/manage')">管理會員</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="5">
          <template #title>
            <el-icon><Tools /></el-icon>
            <span>管理</span>
          </template>
          <el-menu-item index="/dashboard/admin/manage" @click="$router.push('/dashboard/admin/manage')">管理帳號</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-scrollbar>
  </el-aside>
</template>

<script>
import { HomeFilled, TakeawayBox, Tickets, CreditCard, User, Tools } from '@element-plus/icons-vue';

export default {
  name: 'Sidebar',
  components: {
    HomeFilled,
    TakeawayBox,
    Tickets,
    CreditCard,
    User,
    Tools
  }
};
</script>

<style scoped>
.sidebar {
  color: #bfcbd9;
  height: 100vh;
  position: fixed;
  top: 0;
  left: 0;
  overflow: hidden;
  width: 200px;
  display: flex;
  flex-direction: column;
  background-color: #384454;
}
</style>
