<template>
    <div>
        <app-head></app-head>
        <app-body>
            <div class="idle-details-container">
                <div class="details-header">
                    <div class="details-header-user-info">
                        <el-image
                                style="width: 80px; height: 80px;border-radius: 5px; cursor: pointer;"
                                :src="$store.state.baseApi + idleItemInfo.user.avatar"
                                fit="contain"
                                @click="toUserStore"></el-image>
                        <div style="margin-left: 10px;">
                            <div class="details-header-user-info-nickname" @click="toUserStore" style="cursor: pointer;"> 发布者：{{idleItemInfo.user.nickname}}</div>
                            <div class="details-header-user-info-time"> 于{{idleItemInfo.user.signInTime.substring(0,10)}}加入平台</div>
                        </div>
                    </div>
                    <div class="details-header-buy">
                        <div class="product-price">￥{{idleItemInfo.idlePrice}}</div>
                        <div v-if="!isMaster&&idleItemInfo.idleStatus!==1" style="color: red;font-size: 16px;">闲置已下架或删除</div>
                        <div v-if="!isMaster&&idleItemInfo.idleStatus===1" class="purchase-controls">
                            <div class="product-stats">
                                <div class="stat-item">
                                    <span class="stat-label">在售数量：</span>
                                    <span class="stat-value">{{idleItemInfo.idleStock || 0}}</span>
                                </div>
                                <div class="stat-item">
                                    <span class="stat-label">历史销量：</span>
                                    <span class="stat-value">{{idleItemInfo.soldCount || 0}}</span>
                                </div>
                            </div>
                            <div class="quantity-selector">
                                <span>数量：</span>
                                <el-input-number v-model="purchaseQuantity" :min="1" :max="idleItemInfo.idleStock || 1" size="small"></el-input-number>
                            </div>
                            <div class="purchase-buttons">
                                <el-button type="danger" plain @click="buyButton(idleItemInfo)"> 立即购买</el-button>
                            </div>
                        </div>
                        <div v-if="isMaster" class="seller-controls">
                            <div class="product-stats">
                                <div class="stat-item">
                                    <span class="stat-label">在售数量：</span>
                                    <span class="stat-value">{{idleItemInfo.idleStock || 1}}</span>
                                </div>
                                <div class="stat-item">
                                    <span class="stat-label">历史销量：</span>
                                    <span class="stat-value">{{idleItemInfo.soldCount || 0}}</span>
                                </div>
                                <div class="stat-item">
                                    <span class="stat-label">状态：</span>
                                    <span class="stat-value">{{idleItemInfo.idleStatus === 1 ? '已上架' : '已下架'}}</span>
                                </div>
                            </div>
                            <div class="seller-actions">
                                <div v-if="idleItemInfo.idleStatus===1">
                                    <el-button type="primary" size="small" @click="showEditStockDialog" plain>修改数量</el-button>
                                    <el-button type="danger" size="small" @click="changeStatus(idleItemInfo,2)" plain>下架商品</el-button>
                                </div>
                                <div v-else>
                                    <el-button type="success" size="small" @click="changeStatus(idleItemInfo,1)" plain>重新上架</el-button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="details-info">
                    <div class="details-info-title">{{idleItemInfo.idleName}}</div>
                    <div class="details-info-main" v-html="idleItemInfo.idleDetails">
                    </div>
                    <div class="details-picture">
                        <el-image v-for="(imgUrl,i) in idleItemInfo.pictureList" :key="i"
                                  style="width: 60% !important; max-width: 600px; margin-bottom: 20px;"
                                  :src="$store.state.baseApi + imgUrl"
                                  fit="contain"></el-image>
                    </div>
                    <div class="details-info-price-buy">
                        <div v-if="idleItemInfo.avgRating" class="details-info-rating">
                            <span>商品评分：</span>
                            <el-rate
                                v-model="idleItemInfo.avgRating"
                                disabled
                                show-score
                                text-color="#ff9900"
                                score-template="{value}分">
                            </el-rate>
                            <span class="review-count">({{ idleItemInfo.reviewCount }}条评价)</span>
                        </div>
                        
                        <div class="details-info-price">
                            ¥{{ idleItemInfo.idlePrice }}
                        </div>
                    </div>
                </div>

                <!-- 商品评价区 -->
                <div class="details-reviews" v-if="reviewList.length > 0">
                    <div class="details-title">
                        <div>用户评价({{ reviewList.length }})</div>
                    </div>
                    <div class="review-list">
                        <div class="review-item" v-for="(review, index) in reviewList" :key="'review-'+index">
                            <div class="review-user">
                                <img :src="review.user.avatar" alt="头像" class="avatar">
                                <div class="user-info">
                                    <div class="nickname">{{ review.user.nickname }}</div>
                                    <div class="time">{{ formatTime(review.createTime) }}</div>
                                </div>
                                <div class="rating">
                                    <el-rate v-model="review.rating" disabled show-score text-color="#ff9900" score-template="{value}分"></el-rate>
                                </div>
                            </div>
                            <div class="review-content" v-html="review.content"></div>
                        </div>
                    </div>
                </div>
            </div>
            <app-foot></app-foot>
        </app-body>

        <!-- 库存修改对话框 -->
        <el-dialog
            title="修改在售数量"
            :visible.sync="stockDialogVisible"
            width="30%">
            <el-form :model="stockForm">
                <el-form-item label="在售数量">
                    <el-input-number v-model="stockForm.stock" :min="1" :step="1"></el-input-number>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="stockDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="updateStock">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    import AppHead from '../common/AppHeader.vue';
    import AppBody from '../common/AppPageBody.vue'
    import AppFoot from '../common/AppFoot.vue'

    export default {
        name: "idle-details",
        components: {
            AppHead,
            AppBody,
            AppFoot
        },
        data() {
            return {
                idleItemInfo:{
                    id:'',
                    idleName:'',
                    idleDetails:'',
                    pictureList:[],
                    idlePrice:0,
                    idlePlace:'',
                    idleLabel:'',
                    idleStatus:-1,
                    idleStock: 1,
                    soldCount: 0,
                    avgRating: null,
                    reviewCount: 0,
                    userId:'',
                    user:{
                        avatar:'',
                        nickname:'',
                        signInTime:''
                    },
                },
                isMaster:false,
                purchaseQuantity:1,
                stockDialogVisible:false,
                stockForm:{},
                reviewList: []
            };
        },
        mounted(){
            let id = this.$route.query.id || this.$route.query.idleId; // 兼容两种URL参数格式
            if(id == null){
                this.$router.push({path:'/index'});
                return;
            }
            
            this.$api.getIdleItem({
                id:id
            }).then(res=>{
                console.log(res);
                if(res.data){
                    let list=res.data.idleDetails.split(/\r?\n/);
                    let str='';
                    for(let i=0;i<list.length;i++){
                        str+='<p>'+list[i]+'</p>';
                    }
                    res.data.idleDetails=str;
                    res.data.pictureList=JSON.parse(res.data.pictureList);
                    this.idleItemInfo=res.data;
                    let userId=this.getCookie('shUserId');
                    if(userId == this.idleItemInfo.userId){
                        console.log('isMaster');
                        this.isMaster=true;
                    }
                    
                    // 获取商品评价
                    this.getIdleReviews();
                }
                $('html,body').animate({
                    scrollTop: 0
                }, {duration: 500, easing: "swing"});
            });
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
                return "";
            },
            changeStatus(idle,status){
                this.$api.updateIdleItem({
                    id:idle.id,
                    idleStatus:status
                }).then(res=>{
                    console.log(res);
                    if(res.status_code===1){
                        this.idleItemInfo.idleStatus=status;
                    }else {
                        this.$message.error(res.msg)
                    }
                });
            },
            buyButton(idleItemInfo){
                // 判断购买数量是否有效
                if (this.purchaseQuantity < 1) {
                    this.$message.error('购买数量不能小于1');
                    return;
                }
                
                // 判断购买数量是否超过库存
                if (this.purchaseQuantity > idleItemInfo.idleStock) {
                    this.$message.error('购买数量不能超过库存');
                    return;
                }
                
                // 在前端计算总价
                const totalPrice = parseFloat((idleItemInfo.idlePrice * this.purchaseQuantity).toFixed(2));
                
                this.$api.addOrder({
                    idleId: idleItemInfo.id,
                    orderPrice: totalPrice, // 传递计算好的总价
                    purchaseQuantity: this.purchaseQuantity
                }).then(res=>{
                    console.log(res);
                    if(res.status_code===1){
                        this.$router.push({path: '/order', query: {id: res.data.id}});
                    }else {
                        this.$message.error(res.msg)
                    }
                }).catch(e=>{

                })
            },
            showEditStockDialog(){
                this.stockDialogVisible = true;
                this.stockForm.stock = this.idleItemInfo.idleStock || 1;
            },
            updateStock(){
                this.$api.updateIdleItem({
                    id: this.idleItemInfo.id,
                    idleStock: this.stockForm.stock
                }).then(res => {
                    console.log(res);
                    if (res.status_code === 1) {
                        this.idleItemInfo.idleStock = this.stockForm.stock;
                        this.$message({
                            message: '库存修改成功！',
                            type: 'success'
                        });
                    } else {
                        this.$message.error(res.msg);
                    }
                }).catch(() => {
                    this.$message.error("库存修改失败！");
                });
                this.stockDialogVisible = false;
            },
            // 获取商品评价
            getIdleReviews() {
                if (!this.idleItemInfo || !this.idleItemInfo.id) return;
                
                this.$api.getIdleReviews({
                    idleId: this.idleItemInfo.id
                }).then(res => {
                    if (res.status_code === 1) {
                        this.reviewList = res.data || [];
                    }
                }).catch(err => {
                    console.error('获取商品评价失败', err);
                });
            },
            // 格式化时间
            formatTime(time) {
                if (!time) return '';
                return new Date(time).toLocaleString();
            },
            toUserStore() {
                if (this.idleItemInfo.user && this.idleItemInfo.user.id) {
                    // 跳转到指定用户的商品橱窗
                    this.$router.push({
                        path: `/store/${this.idleItemInfo.user.id}`
                    });
                }
            }
        },
    }
</script>

<style scoped>
    .idle-details-container {
        min-height: 85vh;
    }

    .details-header {
        min-height: 120px;
        height: auto;
        border-bottom: 10px solid #f6f6f6;
        display: flex;
        justify-content: space-between;
        padding: 20px;
        align-items: flex-start;
    }

    .details-header-user-info {
        display: flex;
        width: 30%;
    }

    .details-header-user-info-nickname {
        font-size: 22px;
        margin-bottom: 10px;
    }

    .details-header-user-info-time {
        font-size: 16px;
        color: #555555;
    }

    .details-header-buy {
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        padding: 0 15px;
        width: 65%;
        max-width: none;
    }
    
    .product-price {
        color: #ff4d4f;
        font-size: 32px;
        font-weight: 600;
        margin-bottom: 15px;
    }

    .details-info {
        padding: 20px 50px;
        max-width: 1200px;
        margin: 0 auto;
    }

    .details-info-title {
        font-size: 24px;
        font-weight: 600;
        margin-bottom: 20px;
        color: #333;
        border-bottom: 1px solid #eee;
        padding-bottom: 10px;
    }

    .details-info-main {
        font-size: 16px;
        color: #333;
        line-height: 1.8;
        margin-bottom: 30px;
    }

    .details-picture {
        margin: 20px 0;
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    .details-picture .el-image {
        width: 60% !important;
        max-width: 600px;
        margin-bottom: 20px;
    }

    .details-info-price-buy {
        margin-top: 20px;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .details-info-rating {
        margin-bottom: 10px;
        display: flex;
        align-items: center;
    }

    .review-count {
        font-size: 14px;
        color: #999;
        margin-left: 5px;
    }

    .details-info-price {
        font-size: 32px;
        font-weight: 600;
    }

    .details-message {
        padding: 20px;
        margin-bottom: 20px;
        border-bottom: 10px solid #f6f6f6;
    }

    .details-title {
        font-size: 20px;
        font-weight: 600;
        margin-bottom: 20px;
    }

    .details-reviews {
        padding: 20px;
        margin-bottom: 20px;
        border-bottom: 10px solid #f6f6f6;
    }

    .review-list {
        margin-top: 20px;
    }

    .review-item {
        margin-bottom: 20px;
        padding-bottom: 20px;
        border-bottom: 1px solid #eee;
    }

    .review-item:last-child {
        border-bottom: none;
    }

    .review-user {
        display: flex;
        align-items: center;
        margin-bottom: 10px;
    }

    .review-user .avatar {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        margin-right: 10px;
    }

    .review-user .user-info {
        flex: 1;
    }

    .review-user .nickname {
        font-weight: bold;
    }

    .review-user .time {
        font-size: 12px;
        color: #999;
    }

    .review-user .rating {
        margin-left: auto;
    }

    .review-content {
        padding-left: 50px;
        line-height: 1.5;
    }

    /* 购买控件样式 */
    .purchase-controls {
        display: flex;
        flex-direction: column;
        width: 100%;
    }

    .product-stats {
        display: flex;
        flex-direction: column;
        margin-bottom: 15px;
    }
    
    .stat-item {
        display: flex;
        justify-content: flex-start;
        margin-bottom: 5px;
    }
    
    .stat-label {
        font-size: 14px;
        font-weight: 600;
        min-width: 80px;
    }
    
    .stat-value {
        font-size: 14px;
        color: #606266;
        margin-left: 10px;
    }

    .quantity-selector {
        display: flex;
        align-items: center;
        margin-bottom: 15px;
        margin-top: 10px;
    }

    .quantity-selector span {
        margin-right: 10px;
        font-size: 14px;
    }

    .purchase-buttons {
        display: flex;
        justify-content: flex-start;
        margin-top: 10px;
    }
    
    .purchase-buttons button {
        margin-right: 15px;
        width: 150px;
        height: 40px;
    }

    .seller-controls {
        display: flex;
        flex-direction: column;
        width: 100%;
        margin-top: 10px;
    }
    
    .seller-actions {
        display: flex;
        justify-content: flex-start;
    }
    
    .seller-actions button {
        margin-right: 10px;
    }
</style>