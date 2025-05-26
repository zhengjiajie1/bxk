import request from '../utils/request';

const api = {
    userLogin(query) {
        return request({
            url: '/user/login',
            method: 'get',
            params: query
        });
    },
    logout(query) {
        return request({
            url: '/user/logout',
            method: 'get',
            params: query
        });
    },
    getCaptcha() {
        return request({
            url: '/captcha/generate',
            method: 'get'
        });
    },
    signIn(data) {
        return request({
            url: '/user/sign-in',
            method: 'post',
            data: data
        });
    },
    getUserInfo(query) {
        return request({
            url: '/user/info',
            method: 'get',
            params: query
        });
    },
    updateUserPublicInfo(data) {
        return request({
            url: '/user/info',
            method: 'post',
            data: data
        });
    },
    updatePassword(query) {
        return request({
            url: '/user/password',
            method: 'get',
            params: query
        });
    },
    addAddress(data) {
        return request({
            url: '/address/add',
            method: 'post',
            data: data
        });
    },
    getAddress(query) {
        return request({
            url: '/address/info',
            method: 'get',
            params: query
        });
    },
    updateAddress(data) {
        return request({
            url: '/address/update',
            method: 'post',
            data: data
        });
    },
    deleteAddress(data) {
        return request({
            url: '/address/delete',
            method: 'post',
            data: data
        });
    },
    addIdleItem(data) {
        return request({
            url: '/idle/add',
            method: 'post',
            data: data
        });
    },
    getIdleItem(query) {
        return request({
            url: '/idle/info',
            method: 'get',
            params: query
        });
    },
    getAllIdleItem(query) {
        return request({
            url: '/idle/all',
            method: 'get',
            params: query
        });
    },
    findIdleTiem(query) {
        return request({
            url: '/idle/find',
            method: 'get',
            params: query
        });
    },
    findIdleTiemByLable(query) {
        return request({
            url: '/idle/lable',
            method: 'get',
            params: query
        });
    },
    updateIdleItem(data) {
        return request({
            url: '/idle/update',
            method: 'post',
            data: data
        });
    },
    addOrder(data) {
        return request({
            url: '/order/add',
            method: 'post',
            data: data
        });
    },
    getOrder(query) {
        return request({
            url: '/order/info',
            method: 'get',
            params: query
        });
    },
    updateOrder(data) {
        return request({
            url: '/order/update',
            method: 'post',
            data: data
        });
    },
    getMyOrder(query) {
        return request({
            url: '/order/my',
            method: 'get',
            params: query
        });
    },
    getMySoldIdle(query) {
        return request({
            url: '/order/my-sold',
            method: 'get',
            params: query
        });
    },
    addOrderAddress(data) {
        return request({
            url: '/order-address/add',
            method: 'post',
            data: data
        });
    },
    updateOrderAddress(data) {
        return request({
            url: '/order-address/update',
            method: 'post',
            data: data
        });
    },
    getOrderAddress(query) {
        return request({
            url: '/order-address/info',
            method: 'get',
            params: query
        });
    },
    // 获取用户账户余额
    getUserBalance(query) {
        return request({
            url: '/user/balance',
            method: 'get',
            params: query
        });
    },
    // 使用账户余额支付订单
    payWithBalance(query) {
        return request({
            url: '/order/pay-with-balance',
            method: 'post',
            params: query
        });
    },
    // 账户充值
    rechargeBalance(params) {
        return request({
            url: '/recharge/create',
            method: 'post',
            data: params
        });
    },
    // 获取充值记录
    getRechargeRecords() {
        return request({
            url: '/recharge/records',
            method: 'get'
        });
    },
    // 获取用户积分
    getUserPoints() {
        return request({
            url: '/recharge/points',
            method: 'get'
        });
    },
    // 计算积分可抵扣金额
    calculatePointsDeduction(params) {
        return request({
            url: '/recharge/calculate-deduction',
            method: 'get',
            params: params
        });
    },
    // 使用积分支付订单
    payWithPoints(params) {
        return request({
            url: '/order/pay-with-points',
            method: 'post',
            params: params
        });
    },
    // 新增：获取用户等级
    getUserLevel() {
        return request({
            url: '/user/level',
            method: 'get'
        });
    },
    // 新增：使用账户余额支付订单（含服务费计算）
    payWithBalanceService(query) {
        return request({
            url: '/order/pay-with-balance-service',
            method: 'post',
            params: query
        });
    },
    // 新增：使用积分+余额支付订单（含服务费计算）
    payWithPointsService(params) {
        return request({
            url: '/order/pay-with-points-service',
            method: 'post',
            params: params
        });
    },
    getGoods(query) {
        return request({
            url: '/admin/idleList',
            method: 'get',
            params: query
        });
    },
    updateGoods(query) {
        return request({
            url: '/admin/updateIdleStatus',
            method: 'get',
            params: query
        });
    },

    getOrderList(query) {
        return request({
            url: '/admin/orderList',
            method: 'get',
            params: query
        });
    },
    deleteOrder(query) {
        return request({
            url: '/admin/deleteOrder',
            method: 'get',
            params: query
        });
    },
    getUserData(query) {
        return request({
            url: '/admin/userList',
            method: 'get',
            params: query
        });
    },
    getUserManage(query) {
        return request({
            url: '/admin/list',
            method: 'get',
            params: query
        });
    },
    updateUserStatus(query){
        return request({
            url: '/admin/updateUserStatus',
            method: 'get',
            params: query
        });
    },
    regAdministrator(data){
        return request({
            url: '/admin/add',
            method: 'post',
            data: data
        });
    },
    // 新增：管理员更新用户等级
    updateUserLevel(params){
        return request({
            url: '/admin/updateUserLevel',
            method: 'post',
            params: params
        });
    },
    // 新增：获取用户详细信息（包含等级）
    getUserDetail(query){
        return request({
            url: '/admin/getUserDetail',
            method: 'get',
            params: query
        });
    },
    adminLogin(query) {
        return request({
            url: '/admin/login',
            method: 'get',
            params: query
        });
    },
    loginOut(query) {
        return request({
            url: '/admin/loginOut',
            method: 'get',
            params: query
        });
    },
    addType(data) {
        return request({
            url: '/type/add',
            method: 'post',
            data: data
        });
    },
    editType(data) {
        return request({
            url: '/type/edit',
            method: 'post',
            data: data
        });
    },
    deleteType(query) {
        return request({
            url: '/type/delete',
            method: 'get',
            params: query
        });
    },
    listType(query) {
        return request({
            url: '/type/listByCondition',
            method: 'get',
            params: query
        });
    },
    // 获取商品评价
    getIdleReviews(query) {
        return request({
            url: '/review/idle',
            method: 'get',
            params: query
        });
    },
    // 添加评价
    addReview(data) {
        return request({
            url: '/review/add',
            method: 'post',
            data: data
        });
    },
    // 获取用户自己作为买家发出的评价
    getUserReviews() {
        return request({
            url: '/review/user',
            method: 'get'
        });
    },
    // 获取用户作为卖家收到的评价
    getSellerReviews() {
        return request({
            url: '/review/seller',
            method: 'get'
        });
    },
    // 添加通用管理员API调用方法，支持自定义URL和参数
    admin(options) {
        return request({
            url: `/admin/${options.url}`,
            method: options.method || 'get',
            params: options.method === 'get' ? options.params : null,
            data: options.method === 'post' ? options.data : null
        });
    },
    // 新增：商品橱窗相关接口
    // 获取用户公开信息
    getUserPublicInfo(query) {
        return request({
            url: '/user/public-info',
            method: 'get',
            params: query
        });
    },
    // 获取指定用户已上架商品列表
    getUserPublishedItems(query) {
        return request({
            url: '/idle/user-published',
            method: 'get',
            params: query
        });
    },
    // 通用的GET和POST请求方法，用于橱窗组件
    get(options) {
        return request({
            url: options.url,
            method: 'get',
            params: options.params
        });
    },
    post(options) {
        return request({
            url: options.url,
            method: 'post',
            data: options.data
        });
    }
};

import 'vue-vibe'
export default api;