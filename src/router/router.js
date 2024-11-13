import {createRouter, createWebHistory} from "vue-router";


const routes = [
    {
        path: '/',
        component: () => import('@/views/Home.vue')
    },
    {
        path: '/register',
        component: () => import('@/views/login/Register.vue')
    },
    {
        path: '/login',
        component: () => import('@/views/login/Login.vue')
    },
    {
        path: '/reset-password',
        component: () => import('@/views/login/ResetPassword.vue')
    },
    {
        path: '/shop',
        component: () => import('@/views/shop/ShopTickets.vue')
    },
    {
        path: '/shop/:ticketId',
        component: () => import('@/views/shop/ShopTicketDetail.vue')
    },
    {
        path: '/user/profile',
        component: () => import('@/views/user/Profile.vue')
    },
    {
        path: '/user/change-password',
        component: () => import('@/views/user/ChangePassword.vue')
    },
    {
        path: '/user/order',
        component: () => import('@/views/user/ViewOrders.vue')
    },
    {
        path: '/user/ticket',
        component: () => import('@/views/user/ViewTickets.vue')
    },
    {
        path: '/:any(.*)*', // 任何未設定的路徑 都會導向首頁
        redirect: '/'
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

export default router;