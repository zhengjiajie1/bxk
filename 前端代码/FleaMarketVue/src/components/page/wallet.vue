<template>
    <div>
        <app-head></app-head>
        <app-body>
            <div class="wallet-container">
                <div class="wallet-header">
                    <h2>我的钱包</h2>
                </div>
                <div class="wallet-card">
                    <div class="wallet-card-header">
                        <h3>账户余额</h3>
                    </div>
                    <div class="wallet-card-balance">
                        <span class="wallet-money-symbol">￥</span>
                        <span class="wallet-money-amount">{{ accountBalance }}</span>
                        <el-button 
                            type="primary" 
                            size="medium" 
                            class="recharge-button" 
                            @click="showRechargeDialog">
                            账户充值
                        </el-button>
                    </div>
                </div>
                
                <div class="wallet-card points-card">
                    <div class="wallet-card-header">
                        <h3>消费积分</h3>
                    </div>
                    <div class="wallet-card-balance">
                        <span class="points-icon"><i class="el-icon-medal"></i></span>
                        <span class="wallet-money-amount">{{ userPoints }}</span>
                        <div class="points-info">
                            <p>积分可在购买商品时抵扣部分金额</p>
                            <p>每100积分可抵扣1元</p>
                        </div>
                    </div>
                </div>
                
                <div class="wallet-history">
                    <el-tabs v-model="activeTab">
                        <el-tab-pane label="交易记录" name="orders">
                            <h3>交易记录</h3>
                            <el-empty 
                                v-if="recentOrders.length === 0"
                                description="暂无交易记录" 
                                :image-size="200">
                            </el-empty>
                            <el-table
                                v-else
                                :data="recentOrders"
                                style="width: 100%">
                                <el-table-column
                                    prop="orderNumber"
                                    label="订单号"
                                    width="180">
                                </el-table-column>
                                <el-table-column                            
                                    prop="transactionType"                            
                                    label="交易类型"                            
                                    width="120">                            
                                    <template slot-scope="scope">                                
                                        <span :style="scope.row.transactionType === '收入' ? 'color: #67c23a;' : 'color: #f56c6c;'">                                    
                                            {{ scope.row.transactionType }}                                
                                        </span>                            
                                    </template>                        
                                </el-table-column>                        
                                <el-table-column                            
                                    prop="orderPrice"                            
                                    label="金额"                            
                                    width="120">                            
                                    <template slot-scope="scope">                                
                                        <span :style="scope.row.transactionType === '收入' ? 'color: #67c23a;' : 'color: #f56c6c;'">                                    
                                            {{ scope.row.transactionType === '收入' ? '+' : '-' }}￥{{ scope.row.transactionType === '收入' ? scope.row.actualIncome : scope.row.orderPrice }}                                
                                        </span>                            
                                    </template>                        
                                </el-table-column>
                                <el-table-column
                                    prop="paymentTime"
                                    label="支付时间"
                                    width="180">
                                    <template slot-scope="scope">
                                        {{ scope.row.paymentTime ? scope.row.paymentTime.substring(0, 10) + ' ' + scope.row.paymentTime.substring(11, 19) : '' }}
                                    </template>
                                </el-table-column>
                                <el-table-column
                                    prop="paymentWay"
                                    label="支付方式"
                                    width="120">
                                </el-table-column>
                                <el-table-column
                                    label="操作">
                                    <template slot-scope="scope">
                                        <el-button
                                            @click="viewOrderDetail(scope.row.id)"
                                            type="text"
                                            size="small">
                                            查看详情
                                        </el-button>
                                    </template>
                                </el-table-column>
                            </el-table>
                        </el-tab-pane>
                        <el-tab-pane label="充值记录" name="recharges">
                            <h3>充值记录</h3>
                            <el-empty 
                                v-if="rechargeRecords.length === 0"
                                description="暂无充值记录" 
                                :image-size="200">
                            </el-empty>
                            <el-table
                                v-else
                                :data="rechargeRecords"
                                style="width: 100%">
                                <el-table-column
                                    prop="id"
                                    label="记录ID"
                                    width="100">
                                </el-table-column>
                                <el-table-column
                                    prop="amount"
                                    label="充值金额"
                                    width="120">
                                    <template slot-scope="scope">
                                        <span style="color: #67c23a;">+￥{{ scope.row.amount }}</span>
                                    </template>
                                </el-table-column>
                                <el-table-column
                                    prop="rechargeTime"
                                    label="充值时间"
                                    width="180">
                                    <template slot-scope="scope">
                                        {{ scope.row.rechargeTime ? scope.row.rechargeTime.substring(0, 10) + ' ' + scope.row.rechargeTime.substring(11, 19) : '' }}
                                    </template>
                                </el-table-column>
                                <el-table-column
                                    prop="status"
                                    label="状态"
                                    width="120">
                                    <template slot-scope="scope">
                                        {{ scope.row.status === 1 ? '成功' : '处理中' }}
                                    </template>
                                </el-table-column>
                            </el-table>
                        </el-tab-pane>
                        <el-tab-pane label="积分记录" name="points">
                            <h3>积分记录</h3>
                            <el-alert
                                title="购买商品后可获得相应积分，每消费1元可得1积分（不计小数部分）"
                                type="info"
                                :closable="false"
                                style="margin-bottom: 15px;">
                            </el-alert>
                            <el-empty 
                                v-if="pointsOrders.length === 0"
                                description="暂无积分记录" 
                                :image-size="200">
                            </el-empty>
                            <el-table
                                v-else
                                :data="pointsOrders"
                                style="width: 100%">
                                <el-table-column
                                    prop="orderNumber"
                                    label="订单号"
                                    width="180">
                                </el-table-column>
                                <el-table-column                            
                                    prop="pointsChange"                            
                                    label="积分变动"                            
                                    width="120">                            
                                    <template slot-scope="scope">                                
                                        <span style="color: #67c23a;">
                                            +{{ scope.row.pointsEarned || parseInt(scope.row.orderPrice) }}                              
                                        </span>                            
                                    </template>                        
                                </el-table-column>
                                <el-table-column
                                    prop="paymentTime"
                                    label="获得时间"
                                    width="180">
                                    <template slot-scope="scope">
                                        {{ scope.row.paymentTime ? scope.row.paymentTime.substring(0, 10) + ' ' + scope.row.paymentTime.substring(11, 19) : '' }}
                                    </template>
                                </el-table-column>
                                <el-table-column
                                    label="操作">
                                    <template slot-scope="scope">
                                        <el-button
                                            @click="viewOrderDetail(scope.row.id)"
                                            type="text"
                                            size="small">
                                            查看详情
                                        </el-button>
                                    </template>
                                </el-table-column>
                            </el-table>
                        </el-tab-pane>
                    </el-tabs>
                </div>
            </div>
            <app-foot></app-foot>
        </app-body>

        <!-- 充值对话框 -->
        <el-dialog
            title="账户充值"
            :visible.sync="rechargeDialogVisible"
            width="30%">
            <el-form :model="rechargeForm" :rules="rechargeRules" ref="rechargeForm" label-width="100px">
                <el-form-item label="充值金额" prop="amount">
                    <el-input
                        v-model="rechargeForm.amount"
                        placeholder="请输入充值金额"
                        autocomplete="off">
                        <template slot="prepend">￥</template>
                    </el-input>
                </el-form-item>
                <div class="recharge-notes">
                    <p>• 充值金额范围：0.01-999999.00</p>
                    <p>• 充值成功后，金额将实时到账</p>
                </div>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="rechargeDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitRecharge" :loading="recharging">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import AppHead from '../common/AppHeader.vue';
import AppBody from '../common/AppPageBody.vue';
import AppFoot from '../common/AppFoot.vue';

export default {
    name: "wallet",
    components: {
        AppHead,
        AppBody,
        AppFoot
    },
    data() {
        return {
            accountBalance: '0.00',
            userPoints: 0,
            recentOrders: [],
            rechargeRecords: [],
            pointsOrders: [],
            activeTab: 'orders',
            rechargeDialogVisible: false,
            recharging: false,
            rechargeForm: {
                amount: ''
            },
            rechargeRules: {
                amount: [
                    { required: true, message: '请输入充值金额', trigger: 'blur' },
                    { 
                        validator: (rule, value, callback) => {
                            if (value === '') {
                                callback(new Error('请输入充值金额'));
                            } else {
                                const amount = parseFloat(value);
                                if (isNaN(amount)) {
                                    callback(new Error('请输入有效的金额'));
                                } else if (amount <= 0) {
                                    callback(new Error('充值金额必须大于0'));
                                } else if (amount > 999999) {
                                    callback(new Error('充值金额不能超过999999'));
                                } else {
                                    callback();
                                }
                            }
                        },
                        trigger: 'blur'
                    }
                ]
            }
        };
    },
    created() {
        this.getWalletInfo();
        this.getUserPoints();
        this.getRecentOrders();
        this.getRechargeRecords();
    },
    methods: {
        getWalletInfo() {
            this.$api.getUserBalance().then(res => {
                if (res.status_code === 1) {
                    this.accountBalance = parseFloat(res.data).toFixed(2);
                } else {
                    this.$message.error(res.msg || '获取余额失败');
                }
            }).catch(err => {
                console.error('获取余额出错:', err);
                this.$message.error('网络错误，请稍后重试');
            });
        },
        getUserPoints() {
            this.$api.getUserPoints().then(res => {
                if (res.status_code === 1) {
                    this.userPoints = res.data;
                    this.getPointsRecords();
                } else {
                    this.$message.error(res.msg || '获取积分失败');
                }
            }).catch(err => {
                console.error('获取积分出错:', err);
                this.$message.error('网络错误，请稍后重试');
            });
        },
        // 获取我购买的订单（支出）
        getBuyOrders() {
            return new Promise((resolve, reject) => {
                this.$api.getMyOrder().then(res => {
                    if (res.status_code === 1) {
                        // 筛选已支付的订单并标记为支出
                        const buyOrders = res.data.filter(order => 
                            order.paymentStatus === 1 && 
                            (order.paymentWay === '账户余额' || order.paymentWay === '账户余额+积分')
                        ).map(order => ({
                            ...order,
                            transactionType: '支出'
                        }));
                        resolve(buyOrders);
                    } else {
                        this.$message.error(res.msg || '获取购买记录失败');
                        reject(new Error(res.msg || '获取购买记录失败'));
                    }
                }).catch(err => {
                    console.error('获取购买记录出错:', err);
                    this.$message.error('网络错误，请稍后重试');
                    reject(err);
                });
            });
        },
        // 获取我卖出的订单（收入）
        getSellOrders() {
            return new Promise((resolve, reject) => {
                this.$api.getMySoldIdle().then(res => {
                    if (res.status_code === 1) {
                        console.log('getMySoldIdle原始数据:', res.data);
                        // 筛选已支付的订单并标记为收入
                        const sellOrders = res.data ? res.data.filter(order => 
                            order.paymentStatus === 1 && 
                            (order.paymentWay === '账户余额' || order.paymentWay === '账户余额+积分')
                        ).map(order => {
                            console.log('卖家订单:', order.orderNumber, 'sellerAmount:', order.sellerAmount, 'orderPrice:', order.orderPrice, 'serviceFee:', order.serviceFee);
                            
                            // 直接使用数据库中的真实交易数据
                            let actualIncome = order.sellerAmount;
                            if (!actualIncome || actualIncome == 0) {
                                if (order.serviceFee && order.serviceFee > 0) {
                                    // 如果有服务费数据，直接使用
                                    actualIncome = parseFloat(order.orderPrice) - parseFloat(order.serviceFee);
                                    console.log('使用数据库服务费:', order.orderNumber, '原价:', order.orderPrice, '服务费:', order.serviceFee, '实收:', actualIncome);
                                } else {
                                    // 如果数据库中没有服务费数据，显示原价（历史数据兼容）
                                    actualIncome = order.orderPrice;
                                    console.log('历史数据兼容:', order.orderNumber, '显示原价:', actualIncome);
                                }
                            }
                            
                            return {
                                ...order,
                                transactionType: '收入',
                                actualIncome: actualIncome
                            };
                        }) : [];
                        resolve(sellOrders);
                    } else {
                        this.$message.error(res.msg || '获取售出记录失败');
                        reject(new Error(res.msg || '获取售出记录失败'));
                    }
                }).catch(err => {
                    console.error('获取售出记录出错:', err);
                    this.$message.error('网络错误，请稍后重试');
                    reject(err);
                });
            });
        },
        // 获取所有交易记录
        getRecentOrders() {
            Promise.all([this.getBuyOrders(), this.getSellOrders()])
                .then(([buyOrders, sellOrders]) => {
                    // 合并购买和卖出订单
                    const allOrders = [...buyOrders, ...sellOrders];
                    
                    // 按支付时间降序排序
                    this.recentOrders = allOrders.sort((a, b) => {
                        const timeA = a.paymentTime ? new Date(a.paymentTime).getTime() : 0;
                        const timeB = b.paymentTime ? new Date(b.paymentTime).getTime() : 0;
                        return timeB - timeA; // 降序
                    });
                })
                .catch(err => {
                    console.error('获取交易记录出错:', err);
                });
        },
        // 获取充值记录
        getRechargeRecords() {
            this.$api.getRechargeRecords().then(res => {
                if (res.status_code === 1) {
                    this.rechargeRecords = res.data || [];
                } else {
                    console.error('获取充值记录失败:', res.msg);
                }
            }).catch(err => {
                console.error('获取充值记录出错:', err);
            });
        },
        // 获取积分记录（基于购买订单）
        getPointsRecords() {
            this.$api.getMyOrder().then(res => {
                if (res.status_code === 1) {
                    // 筛选已支付的订单
                    const pointsOrders = res.data.filter(order => 
                        order.paymentStatus === 1 && 
                        (order.paymentWay === '账户余额' || order.paymentWay === '账户余额+积分')
                    );
                    
                    // 按支付时间降序排序
                    this.pointsOrders = pointsOrders.sort((a, b) => {
                        const timeA = a.paymentTime ? new Date(a.paymentTime).getTime() : 0;
                        const timeB = b.paymentTime ? new Date(b.paymentTime).getTime() : 0;
                        return timeB - timeA; // 降序
                    });
                } else {
                    console.error('获取积分记录失败:', res.msg);
                }
            }).catch(err => {
                console.error('获取积分记录出错:', err);
            });
        },
        viewOrderDetail(orderId) {
            this.$router.push({ path: '/order', query: { id: orderId } });
        },
        // 显示充值对话框
        showRechargeDialog() {
            this.rechargeForm.amount = '';
            this.rechargeDialogVisible = true;
            this.$nextTick(() => {
                this.$refs.rechargeForm && this.$refs.rechargeForm.clearValidate();
            });
        },
        // 提交充值
        submitRecharge() {
            this.$refs.rechargeForm.validate(valid => {
                if (valid) {
                    this.recharging = true;
                    this.$api.rechargeBalance({ amount: this.rechargeForm.amount }).then(res => {
                        this.recharging = false;
                        if (res.status_code === 1) {
                            this.$message.success('充值成功');
                            this.rechargeDialogVisible = false;
                            // 刷新余额和充值记录
                            this.getWalletInfo();
                            this.getRechargeRecords();
                        } else {
                            this.$message.error(res.msg || '充值失败，请稍后重试');
                        }
                    }).catch(err => {
                        this.recharging = false;
                        console.error('充值失败:', err);
                        this.$message.error('网络错误，请稍后重试');
                    });
                }
            });
        }
    }
};
</script>

<style scoped>
.wallet-container {
    padding: 20px;
    min-height: 85vh;
}

.wallet-header {
    margin-bottom: 20px;
}

.wallet-card {
    background: linear-gradient(135deg, #409EFF, #67c23a);
    border-radius: 10px;
    color: white;
    padding: 20px;
    margin-bottom: 30px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.points-card {
    background: linear-gradient(135deg, #ff9500, #ff6a00);
}

.wallet-card-header {
    margin-bottom: 10px;
}

.wallet-card-balance {
    display: flex;
    align-items: center;
    padding: 10px 0;
}

.wallet-money-symbol {
    font-size: 24px;
    margin-right: 5px;
}

.wallet-money-amount {
    font-size: 36px;
    font-weight: bold;
    margin-right: 30px;
}

.points-icon {
    font-size: 24px;
    margin-right: 5px;
}

.points-info {
    margin-left: auto;
    font-size: 14px;
}

.points-info p {
    margin: 5px 0;
}

.recharge-button {
    margin-left: auto;
}

.wallet-history {
    background-color: white;
    border-radius: 5px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.recharge-notes {
    color: #909399;
    font-size: 12px;
    margin-top: 15px;
    padding-left: 100px;
}

.recharge-notes p {
    margin: 5px 0;
}
</style> 