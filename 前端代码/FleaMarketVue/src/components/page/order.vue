<template>
    <div>
        <app-head></app-head>
        <app-body>
            <div class="order-page-container">
                <div class="idle-info-container" @click="toDetails(orderInfo.idleItem.id)">
                    <el-image
                            style="width: 150px; height: 150px;"
                            :src="$store.state.baseApi + orderInfo.idleItem.imgUrl"
                            fit="cover"></el-image>
                    <div class="idle-info-title">{{orderInfo.userId==userId?'买到的':'卖出的'}}：{{orderInfo.idleItem.idleName}}</div>
                    <div class="idle-info-price">￥{{orderInfo.orderPrice}}</div>

                </div>
                <div class="address-container" @click.stop="selectAddressDialog" :style="orderInfo.userId==userId&&orderInfo.orderStatus===0?'cursor: pointer;':''">
                    <div class="address-title">收货地址: {{addressInfo.consigneeName}} {{addressInfo.consigneePhone}}</div>
                    <div class="address-detials">{{addressInfo.detailAddress}}</div>
                    <el-button v-if="!addressInfo.detailAddress" @click.stop="selectAddressDialog" type="primary" plain>选择收货地址</el-button>
                </div>
                <el-dialog
                        title="选择地址"
                        :visible.sync="addressDialogVisible"
                        width="800px">
                    <el-table
                            stripe
                            empty-text="无地址信息，请先在个人中心添加地址"
                            :data="addressData"
                            style="width: 100%">
                        <el-table-column
                                prop="consigneeName"
                                label="收货人姓名"
                                width="120">
                        </el-table-column>
                        <el-table-column
                                prop="consigneePhone"
                                label="手机号"
                                width="140">
                        </el-table-column>
                        <el-table-column
                                prop="detailAddressText"
                                label="地址">
                        </el-table-column>
                        <el-table-column label=" " width="120">
                            <template slot-scope="scope">
                                <el-button
                                        size="mini"
                                        @click="selectAddress(scope.$index, scope.row)">选择
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-dialog>
                <div class="order-info-container">
                    <div class="order-info-title">订单信息（{{orderStatus[orderInfo.orderStatus]}}）：</div>
                    <div class="order-info-item">编号：{{orderInfo.orderNumber}}</div>
                    <div class="order-info-item">商品单价：¥{{orderInfo.idleItem.idlePrice}}</div>
                    <div class="order-info-item">购买数量：{{orderInfo.purchaseQuantity || 1}} 件</div>
                    <div class="order-info-item">订单金额：¥{{orderInfo.orderPrice}}</div>
                    <div class="order-info-item" v-if="orderInfo.pointsUsed > 0">使用积分：{{orderInfo.pointsUsed}} 积分</div>
                    <div class="order-info-item" v-if="orderInfo.pointsDiscount > 0">积分抵扣：¥{{orderInfo.pointsDiscount}}</div>
                    <div class="order-info-item" v-if="orderInfo.serviceFee > 0">服务费：¥{{orderInfo.serviceFee}} (费率{{(orderInfo.serviceFeeRate * 100).toFixed(2)}}%)</div>
                    <div class="order-info-item" v-if="orderInfo.sellerAmount > 0">卖家实收：¥{{orderInfo.sellerAmount}}</div>
                    <div class="order-info-item" v-if="orderInfo.pointsUsed > 0">实际支付：¥{{(orderInfo.orderPrice - (orderInfo.pointsDiscount || 0)).toFixed(2)}}</div>
                    <div class="order-info-item">支付状态：{{orderInfo.paymentStatus===0?'未支付':'已支付'}}</div>
                    <div class="order-info-item">支付方式：{{orderInfo.paymentWay}}</div>
                    <div class="order-info-item">创建时间：{{orderInfo.createTime.substring(0, 10) + ' ' +
                        orderInfo.createTime.substring(11, 19)}}
                    </div>
                    <div class="order-info-item">支付时间：{{orderInfo.paymentTime?orderInfo.paymentTime.substring(0, 10) + ' ' +
                        orderInfo.paymentTime.substring(11, 19):''}}
                    </div>
                </div>
                <div class="menu">
                    <span class="btn-span">
                        <el-button v-if="getOrderStatusText(orderInfo.orderStatus) === '已完成' && !isOrderReviewed && orderInfo.userId == userId" size="small" type="success" @click="goToReview">评价商品</el-button>
                        <el-button v-if="orderInfo.orderStatus === 0" class="go-pay" size="small" type="danger" @click="pay">去支付</el-button>
                        <el-button v-if="orderInfo.orderStatus === 0" size="small" @click="cancelOrder">取消订单</el-button>
                        <el-button v-if="orderInfo.orderStatus === 1 && orderInfo.idleItem.userId == userId" size="small" type="primary" @click="shipOrder">发货</el-button>
                        <el-button v-if="orderInfo.orderStatus === 2 && orderInfo.userId == userId" size="small" type="success" @click="confirmReceived">确认收货</el-button>
                    </span>
                </div>
            </div>
            <app-foot></app-foot>
        </app-body>
    </div>
</template>

<script>
    import AppHead from '../common/AppHeader.vue';
    import AppBody from '../common/AppPageBody.vue'
    import AppFoot from '../common/AppFoot.vue'

    export default {
        name: "order",
        components: {
            AppHead,
            AppBody,
            AppFoot
        },
        data() {
            return {
                addressDialogVisible:false,
                addressData: [],
                orderStatus: {
                    0: '待付款',
                    1: '待发货',
                    2: '待收货',
                    3: '已完成',
                    4: '已取消'
                },
                orderInfo: {
                    createTime: "",
                    id: 0,
                    idleId: 0,
                    idleItem: {
                        id: '',
                        idleName: '',
                        idleDetails: '',
                        pictureList: [],
                        idlePrice: 0,
                        idlePlace: '',
                        idleLabel: '',
                        idleStatus: -1,
                        userId: '',
                    },
                    orderNumber: "",
                    orderPrice: 0,
                    orderStatus: 0,
                    paymentStatus: 0,
                    paymentTime: "",
                    paymentWay: "",
                    userId: 0
                },
                addressInfo: {
                    id:'',
                    update:false,
                    consigneeName: '',
                    consigneePhone: '',
                    detailAddress: ''
                },
                userId: '',
                isOrderReviewed: false
            };
        },
        created() {
            this.userId=this.getCookie('shUserId');
            console.log('userId',this.userId,this.getCookie('shUserId'));
            let orderId = this.$route.query.id;
            console.log(orderId);
            this.$api.getOrder({
                id: orderId
            }).then(res => {
                console.log(res);
                if (res.status_code === 1) {
                    if (res.data.idleItem) {
                        let imgList = JSON.parse(res.data.idleItem.pictureList);
                        if (imgList.length > 0) {
                            res.data.idleItem.imgUrl = imgList[0];
                        } else {
                            res.data.idleItem.imgUrl = '';
                        }
                    } else {
                        res.data.idleItem = {
                            idleName: '',
                            imgUrl: ''
                        }
                    }
                    this.orderInfo = res.data;
                    this.$api.getOrderAddress({
                        orderId:this.orderInfo.id
                    }).then(res=>{
                        if(res.data){
                            this.addressInfo= res.data;
                            this.addressInfo.update=true;
                        }else{
                            this.getAddressData();
                        }
                    })
                    this.checkOrderReviewed();
                }
            })
        },
        methods: {
            getCookie(cname){
                var name = cname + "=";
                var ca = document.cookie.split(';');
                for(var i=0; i<ca.length; i++)
                {
                    var c = ca[i].trim();
                    if (c.indexOf(name)===0) return c.substring(name.length,c.length);
                }
                return "0";
            },
            toDetails(id) {
                this.$router.replace({path: 'details', query: {id: id}});
            },
            selectAddressDialog(){
                if(this.orderInfo.userId==this.userId&&this.orderInfo.orderStatus===0){
                    this.addressDialogVisible=true;
                    if(this.addressData.length===0){
                        this.getAddressData();
                    }
                }
            },
            getAddressData(){
                this.$api.getAddress().then(res => {
                    if (res.status_code === 1) {
                        let data = res.data;
                        for (let i = 0; i < data.length; i++) {
                            data[i].detailAddressText = data[i].provinceName + data[i].cityName + data[i].regionName + data[i].detailAddress;
                        }
                        console.log(data);
                        this.addressData = data;
                        if(!this.addressInfo.update){
                            for(let i=0;i<data.length;i++){
                                if(data[i].defaultFlag){
                                    this.selectAddress(i,data[i]);
                                }
                            }
                        }
                    }
                })
            },
            selectAddress(i,item){
                this.addressDialogVisible=false;
                console.log(item,this.addressInfo);
                this.addressInfo.consigneeName=item.consigneeName;
                this.addressInfo.consigneePhone=item.consigneePhone;
                this.addressInfo.detailAddress=item.detailAddressText;
                if(this.addressInfo.update){
                    this.$api.updateOrderAddress({
                        id:this.addressInfo.id,
                        consigneeName:item.consigneeName,
                        consigneePhone:item.consigneePhone,
                        detailAddress:item.detailAddressText
                    })
                }else{
                    this.$api.addOrderAddress({
                        orderId:this.orderInfo.id,
                        consigneeName:item.consigneeName,
                        consigneePhone:item.consigneePhone,
                        detailAddress:item.detailAddressText
                    }).then(res=>{
                        if(res.status_code===1){
                            this.addressInfo.update=true;
                            this.addressInfo.id=res.data.id;
                        }else {
                            this.$message.error(res.msg)
                        }
                    })
                }

            },
            changeOrderStatus(orderStatus, orderInfo) {
                if (orderStatus === 1) {
                    console.log('zhifu');
                    if(!this.addressInfo.detailAddress){
                        this.$message.error('请选择地址！')
                    } else {
                        // 获取用户积分，用于支付选项的展示
                        this.$api.getUserPoints().then(pointsRes => {
                            if (pointsRes.status_code === 1) {
                                const userPoints = pointsRes.data;
                                const orderPrice = parseFloat(orderInfo.orderPrice);
                                
                                // 如果用户有积分，显示支付选项对话框
                                if (userPoints > 0) {
                                    this.$confirm('是否使用积分抵扣部分金额?', '支付方式选择', {
                                        confirmButtonText: '使用积分',
                                        cancelButtonText: '直接支付',
                                        type: 'info',
                                        center: true
                                    }).then(() => {
                                        // 使用积分支付
                                        this.payWithPoints(orderInfo, userPoints);
                                    }).catch(() => {
                                        // 直接使用余额支付
                                        this.payWithBalance(orderInfo);
                                    });
                                } else {
                                    // 没有积分，直接使用余额支付
                                    this.payWithBalance(orderInfo);
                                }
                            } else {
                                // 获取积分失败，默认使用余额支付
                                this.$message.warning('无法获取积分信息，将直接使用余额支付');
                                this.payWithBalance(orderInfo);
                            }
                        }).catch(err => {
                            console.error('获取积分出错:', err);
                            this.$message.warning('无法获取积分信息，将直接使用余额支付');
                            this.payWithBalance(orderInfo);
                        });
                    }
                } else {
                    this.$api.updateOrder({
                        id: orderInfo.id,
                        orderStatus: orderStatus,
                    }).then(res => {
                        if (res.status_code === 1) {
                            this.$message({
                                message: '操作成功！',
                                type: 'success'
                            });
                            this.orderInfo.orderStatus = orderStatus;
                        }
                    })
                }
            },
            // 使用账户余额支付（含服务费计算）
            payWithBalance(orderInfo) {
                this.$confirm('是否使用账户余额支付？', '支付订单', {
                    confirmButtonText: '支付',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    // 先检查余额是否足够
                    this.$api.getUserBalance().then(balanceRes => {
                        if (balanceRes.status_code === 1) {
                            const balance = parseFloat(balanceRes.data);
                            const orderPrice = parseFloat(orderInfo.orderPrice);
                            
                            if (balance < orderPrice) {
                                this.$message.error('账户余额不足，请先充值！');
                                return;
                            }
                            
                            // 使用账户余额支付（含服务费计算）
                            this.$api.payWithBalanceService({
                                orderId: orderInfo.id
                            }).then(res => {
                                if (res.status_code === 1) {
                                    this.$message({
                                        message: '支付成功！',
                                        type: 'success'
                                    });
                                    this.orderInfo.orderStatus = 1; // 待发货
                                    this.orderInfo.paymentStatus = 1; // 已支付
                                    this.orderInfo.paymentWay = '账户余额';
                                    this.orderInfo.paymentTime = new Date().toISOString();
                                    
                                    // 刷新订单详情以获取服务费信息
                                    this.refreshOrderInfo();
                                } else {
                                    this.$message.error(res.msg || '支付失败，请稍后重试');
                                }
                            }).catch(err => {
                                console.error('支付出错:', err);
                                this.$message.error('网络错误，请稍后重试');
                            });
                        } else {
                            this.$message.error(balanceRes.msg || '获取余额失败');
                        }
                    }).catch(err => {
                        console.error('获取余额出错:', err);
                        this.$message.error('网络错误，请稍后重试');
                    });
                }).catch(() => {
                    // 用户取消支付
                });
            },
            // 使用积分抵扣支付
            payWithPoints(orderInfo, userPoints) {
                // 显示积分抵扣对话框
                this.$prompt('请输入要使用的积分数量（必须为100的整数倍）', '积分抵扣', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    inputType: 'number',
                    inputPattern: /^[0-9]*$/,
                    inputErrorMessage: '请输入有效的积分数量',
                    inputPlaceholder: `最多可使用 ${userPoints - userPoints % 100} 积分`,
                    inputValue: userPoints >= 100 ? Math.floor(userPoints / 100) * 100 : 0, // 默认值为最大可用积分(100的整数倍)
                }).then(({ value }) => {
                    const usePoints = parseInt(value);
                    
                    // 验证积分输入
                    if (isNaN(usePoints) || usePoints <= 0) {
                        this.$message.error('请输入有效的积分数量');
                        return;
                    }
                    
                    // 验证是否为100的整数倍
                    if (usePoints % 100 !== 0) {
                        this.$message.error('积分必须为100的整数倍');
                        return;
                    }
                    
                    // 验证是否至少为100
                    if (usePoints < 100) {
                        this.$message.error('最少使用100积分');
                        return;
                    }
                    
                    if (usePoints > userPoints) {
                        this.$message.error('积分不足');
                        return;
                    }
                    
                    // 计算积分可抵扣的金额
                    this.$api.calculatePointsDeduction({
                        points: usePoints
                    }).then(deductionRes => {
                        if (deductionRes.status_code === 1) {
                            const deduction = parseFloat(deductionRes.data.deduction);
                            const orderPrice = parseFloat(orderInfo.orderPrice);
                            const actualPayment = Math.max(0, orderPrice - deduction);
                            
                            // 显示抵扣详情确认
                            this.$confirm(`
                                <div style="text-align:left;">
                                    <p>订单金额: ￥${orderPrice.toFixed(2)}</p>
                                    <p>积分抵扣: ￥${deduction.toFixed(2)} (${usePoints}积分)</p>
                                    <p>实际支付: ￥${actualPayment.toFixed(2)}</p>
                                </div>
                            `, '确认支付详情', {
                                confirmButtonText: '确认支付',
                                cancelButtonText: '取消',
                                dangerouslyUseHTMLString: true,
                                center: true
                            }).then(() => {
                                // 检查余额是否足够
                                this.$api.getUserBalance().then(balanceRes => {
                                    if (balanceRes.status_code === 1) {
                                        const balance = parseFloat(balanceRes.data);
                                        
                                        if (balance < actualPayment) {
                                            this.$message.error('账户余额不足，请先充值！');
                                            return;
                                        }
                                        
                                        // 使用积分+余额支付（含服务费计算）
                                        this.$api.payWithPointsService({
                                            orderId: orderInfo.id,
                                            usePoints: usePoints
                                        }).then(res => {
                                            if (res.status_code === 1) {
                                                this.$message({
                                                    message: '支付成功！',
                                                    type: 'success'
                                                });
                                                this.orderInfo.orderStatus = 1; // 待发货
                                                this.orderInfo.paymentStatus = 1; // 已支付
                                                this.orderInfo.paymentWay = '账户余额+积分';
                                                this.orderInfo.paymentTime = new Date().toISOString();
                                                
                                                // 更新积分使用和抵扣金额信息
                                                this.orderInfo.pointsUsed = usePoints;
                                                this.orderInfo.pointsDiscount = deduction;
                                                
                                                // 刷新订单详情
                                                this.$api.getOrder({
                                                    id: orderInfo.id
                                                }).then(refreshRes => {
                                                    if (refreshRes.status_code === 1) {
                                                        if (refreshRes.data.idleItem) {
                                                            let imgList = JSON.parse(refreshRes.data.idleItem.pictureList);
                                                            if (imgList.length > 0) {
                                                                refreshRes.data.idleItem.imgUrl = imgList[0];
                                                            } else {
                                                                refreshRes.data.idleItem.imgUrl = '';
                                                            }
                                                        }
                                                        this.orderInfo = refreshRes.data;
                                                    }
                                                });
                                            } else {
                                                this.$message.error(res.msg || '支付失败，请稍后重试');
                                            }
                                        }).catch(err => {
                                            console.error('支付出错:', err);
                                            this.$message.error('网络错误，请稍后重试');
                                        });
                                    } else {
                                        this.$message.error(balanceRes.msg || '获取余额失败');
                                    }
                                }).catch(err => {
                                    console.error('获取余额出错:', err);
                                    this.$message.error('网络错误，请稍后重试');
                                });
                            }).catch(() => {
                                // 用户取消确认
                            });
                        } else {
                            this.$message.error(deductionRes.msg || '计算抵扣金额失败');
                        }
                    }).catch(err => {
                        console.error('计算抵扣金额出错:', err);
                        this.$message.error('网络错误，请稍后重试');
                    });
                }).catch(() => {
                    // 用户取消输入积分，回退到普通支付
                    this.payWithBalance(orderInfo);
                });
            },
            checkOrderReviewed() {
                if (!this.orderInfo || !this.orderInfo.id) return;
                
                this.$api.getIdleReviews({
                    orderId: this.orderInfo.id
                }).then(res => {
                    this.isOrderReviewed = res.status_code === 1 && res.data && res.data.length > 0;
                }).catch(err => {
                    console.error('检查订单评价状态失败', err);
                });
            },
            goToReview() {
                this.$router.push({
                    path: '/review',
                    query: { orderId: this.orderInfo.id }
                });
            },
            pay() {
                // 实现支付逻辑
                this.changeOrderStatus(1, this.orderInfo);
            },
            // 刷新订单信息
            refreshOrderInfo() {
                this.$api.getOrder({
                    id: this.orderInfo.id
                }).then(res => {
                    if (res.status_code === 1) {
                        if (res.data.idleItem) {
                            let imgList = JSON.parse(res.data.idleItem.pictureList);
                            if (imgList.length > 0) {
                                res.data.idleItem.imgUrl = imgList[0];
                            } else {
                                res.data.idleItem.imgUrl = '';
                            }
                        }
                        this.orderInfo = res.data;
                    }
                }).catch(err => {
                    console.error('刷新订单信息失败:', err);
                });
            },
            cancelOrder() {
                // 实现取消订单逻辑
                this.$confirm('确认要取消此订单吗?', '取消订单', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.changeOrderStatus(4, this.orderInfo);
                }).catch(() => {
                    // 用户取消操作
                });
            },
            getOrderStatusText(status) {
                return this.orderStatus[status];
            },
            shipOrder() {
                // 实现发货逻辑
                this.$confirm('确认要发货吗?', '发货', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'info'
                }).then(() => {
                    this.changeOrderStatus(2, this.orderInfo);
                }).catch(() => {
                    // 用户取消操作
                });
            },
            confirmReceived() {
                // 实现确认收货逻辑
                this.$confirm('确认已收到商品吗?', '确认收货', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'info'
                }).then(() => {
                    this.changeOrderStatus(3, this.orderInfo);
                }).catch(() => {
                    // 用户取消操作
                });
            }
        }

    }
</script>

<style scoped>
    .order-page-container {
        min-height: 85vh;
    }

    .idle-info-container {
        width: 100%;
        display: flex;
        border-bottom: 20px solid #f6f6f6;
        padding: 20px;
        cursor: pointer;
    }

    .idle-info-title {
        font-size: 18px;
        font-weight: 600;
        max-width: 750px;
        margin-left: 10px;
    }

    .idle-info-price {
        font-size: 18px;
        color: red;
        margin-left: 10px;
    }

    .address-container {
        min-height: 60px;
        padding: 20px;
        border-bottom: 20px solid #f6f6f6;

    }

    .address-title {
        font-size: 18px;
        font-weight: 600;
        margin-bottom: 10px;
    }

    .address-detials {
        font-size: 16px;
        color: #444444;
    }

    .order-info-container {
        padding: 20px;
    }

    .order-info-item {
        margin: 10px 0;
        font-size: 14px;
        color: #444444;
    }

    .menu {
        margin-left: 20px;
    }

    .btn-span {
        margin-right: 10px;
    }

    .go-pay {
        background-color: #f56c6c;
        border-color: #f56c6c;
    }
</style>