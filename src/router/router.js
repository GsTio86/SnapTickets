import { createRouter, createWebHistory } from 'vue-router';
import cookies from 'vue-cookies';

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/login/Login.vue'),
        beforeEnter: (to, from, next) => {
            const token = cookies.get('login-user-token');
            const username = cookies.get('login-user');
            if (token && username) {
                return next('/dashboard');
            } else {
                next();
            }
        }
    },
    {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/Dashboard.vue'),
        beforeEnter: (to, from, next) => {
            const token = cookies.get('login-user-token');
            const username = cookies.get('login-user');
            if (!token || !username) {
                return next('/login');
            } else {
                next();
            }
        },
        children: [
            {
                path: '',
                component: () => import('../views/statistics/Statistics.vue')
            },
            {
                path: 'profile',
                name: 'Profile',
                component: () => import('../views/dashboard/Profile.vue')
            },
            {
                path: 'change-password',
                name: 'ChangePassword',
                component: () => import('../views/dashboard/ChangePassword.vue')
            },
            {
                path: 'product/manage',
                name: 'ProductManage',
                component: () => import('../views/product/ProductManage.vue')
            },
            {
                path: 'order/manage',
                name: 'OrderManage',
                component: () => import('../views/order/OrderManage.vue')
            },
            {
                path: 'payment/manage',
                name: 'PaymentManage',
                component: () => import('../views/payment/PaymentManage.vue')
            },
            {
                path: 'member/manage',
                name: 'MemberManage',
                component: () => import('../views/member/MemberManage.vue')
            },
            {
                path: 'admin/manage',
                name: 'AdminManage',
                component: () => import('../views/admin/AdminManage.vue')
            }
        ]
    }
];


const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;
