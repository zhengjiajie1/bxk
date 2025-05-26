<template>
    <div>
        <app-head></app-head>
        <app-body>
            <div class="store-container">
                <!-- 用户信息区域 -->
                <div class="user-info-section">
                    <div class="user-avatar">
                        <el-image 
                            :src="$store.state.baseApi + userInfo.avatar" 
                            style="width: 120px; height: 120px; border-radius: 10px;"
                            fit="cover">
                            <div slot="error" class="image-slot">
                                <i class="el-icon-picture-outline"></i>
                            </div>
                        </el-image>
                    </div>
                    <div class="user-details">
                        <h2 class="user-nickname">{{ userInfo.nickname }}</h2>
                        <div class="user-meta">
                            <p class="user-city">
                                <i class="el-icon-location"></i>
                                {{ userInfo.city || '未知城市' }}
                            </p>
                            <p class="user-join-time">
                                <i class="el-icon-time"></i>
                                {{ formatJoinTime(userInfo.signInTime) }}加入平台
                            </p>
                        </div>
                        <div class="user-bio">
                            <p v-if="userInfo.bio">{{ userInfo.bio }}</p>
                            <p v-else class="no-bio">这个用户很懒，什么都没有留下~</p>
                        </div>
                        <!-- 如果是自己的橱窗，显示编辑按钮 -->
                        <div v-if="isOwnStore" class="edit-bio-section">
                            <el-button size="mini" type="primary" @click="showEditBio">编辑个人简介</el-button>
                        </div>
                    </div>
                </div>

                <!-- 商品展示区域 -->
                <div class="goods-section">
                    <div class="section-header">
                        <h3>TA的商品 ({{ totalGoods }})</h3>
                    </div>
                    
                    <div v-if="goodsList.length > 0" class="goods-grid">
                        <div 
                            class="goods-item" 
                            v-for="item in goodsList" 
                            :key="item.id"
                            @click="toGoodsDetail(item.id)">
                            <div class="goods-image">
                                <el-image 
                                    :src="getFirstImage(item.pictureList)"
                                    style="width: 100%; height: 200px;"
                                    fit="cover">
                                    <div slot="error" class="image-slot">
                                        <i class="el-icon-picture-outline"></i>
                                    </div>
                                </el-image>
                            </div>
                            <div class="goods-info">
                                <h4 class="goods-title">{{ item.idleName }}</h4>
                                <p class="goods-price">￥{{ item.idlePrice }}</p>
                                <div class="goods-meta">
                                    <span class="goods-location">{{ item.idlePlace }}</span>
                                    <span class="goods-time">{{ formatTime(item.releaseTime) }}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div v-else class="no-goods">
                        <el-empty 
                            description="暂无商品"
                            :image-size="100">
                        </el-empty>
                    </div>

                    <!-- 分页 -->
                    <div v-if="totalGoods > pageSize" class="pagination-section">
                        <el-pagination
                            @current-change="handlePageChange"
                            :current-page="currentPage"
                            layout="prev, pager, next"
                            :page-size="pageSize"
                            :total="totalGoods">
                        </el-pagination>
                    </div>
                </div>
            </div>

            <!-- 编辑个人简介对话框 -->
            <el-dialog 
                title="编辑个人简介" 
                :visible.sync="editBioVisible"
                width="30%">
                <el-input
                    type="textarea"
                    v-model="editBioText"
                    placeholder="介绍一下自己吧~"
                    :rows="4"
                    maxlength="200"
                    show-word-limit>
                </el-input>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="editBioVisible = false">取 消</el-button>
                    <el-button type="primary" @click="updateBio">确 定</el-button>
                </span>
            </el-dialog>

            <app-foot></app-foot>
        </app-body>
    </div>
</template>

<script>
import AppHead from '../common/AppHeader.vue';
import AppBody from '../common/AppPageBody.vue';
import AppFoot from '../common/AppFoot.vue';

export default {
    name: "store-front",
    components: {
        AppHead,
        AppBody,
        AppFoot
    },
    data() {
        return {
            userInfo: {
                id: null,
                nickname: '',
                avatar: '',
                bio: '',
                city: '',
                signInTime: ''
            },
            goodsList: [],
            currentPage: 1,
            pageSize: 12,
            totalGoods: 0,
            userId: null,
            isOwnStore: false,
            editBioVisible: false,
            editBioText: ''
        };
    },
    mounted() {
        this.userId = this.$route.params.userId;
        
        // 如果没有指定用户ID，则显示当前用户的橱窗
        if (!this.userId) {
            this.getUserInfo().then(() => {
                this.userId = this.$globalData.userInfo.id;
                this.isOwnStore = true;
                this.loadStoreData();
            });
        } else {
            this.userId = parseInt(this.userId);
            // 检查是否是自己的橱窗
            this.getUserInfo().then(() => {
                this.isOwnStore = this.userId === this.$globalData.userInfo.id;
                this.loadStoreData();
            });
        }
    },
    methods: {
        async getUserInfo() {
            if (!this.$globalData.userInfo.id) {
                try {
                    const res = await this.$api.getUserInfo();
                    if (res.status_code === 1) {
                        this.$globalData.userInfo = res.data;
                    }
                } catch (error) {
                    console.error('获取用户信息失败:', error);
                }
            }
        },
        loadStoreData() {
            this.loadUserPublicInfo();
            this.loadUserGoods();
        },
        loadUserPublicInfo() {
            if (this.isOwnStore) {
                // 对于自己的橱窗，也通过API获取最新用户信息，确保数据完整性
                this.$api.getUserInfo().then(res => {
                    if (res.status_code === 1) {
                        this.userInfo = res.data;
                    } else {
                        // 如果API失败，使用本地数据作为备用
                        this.userInfo = { ...this.$globalData.userInfo };
                    }
                }).catch(error => {
                    console.error('获取自己用户信息失败:', error);
                    // API失败时使用本地数据
                    this.userInfo = { ...this.$globalData.userInfo };
                });
            } else {
                // 获取其他用户的公开信息
                this.$api.get({
                    url: '/user/public-info',
                    params: { userId: this.userId }
                }).then(res => {
                    if (res.status_code === 1) {
                        this.userInfo = res.data;
                    } else {
                        this.$message.error('获取用户信息失败');
                        this.$router.push('/');
                    }
                }).catch(error => {
                    console.error('获取用户公开信息失败:', error);
                    this.$message.error('获取用户信息失败');
                    this.$router.push('/');
                });
            }
        },
        loadUserGoods() {
            this.$api.get({
                url: '/idle/user-published',
                params: {
                    userId: this.userId,
                    page: this.currentPage,
                    nums: this.pageSize
                }
            }).then(res => {
                if (res.status_code === 1) {
                    this.goodsList = res.data.list || [];
                    this.totalGoods = res.data.count || 0;
                } else {
                    this.$message.error('获取商品列表失败');
                }
            }).catch(error => {
                console.error('获取用户商品失败:', error);
                this.$message.error('获取商品列表失败');
            });
        },
        handlePageChange(page) {
            this.currentPage = page;
            this.loadUserGoods();
        },
        toGoodsDetail(goodsId) {
            this.$router.push({
                path: '/details',
                query: { id: goodsId }
            });
        },
        getFirstImage(pictureList) {
            try {
                if (pictureList) {
                    const images = JSON.parse(pictureList);
                    if (images && images.length > 0) {
                        return this.$store.state.baseApi + images[0];
                    }
                }
                return '';
            } catch (e) {
                return '';
            }
        },
        formatTime(timeStr) {
            if (timeStr) {
                return timeStr.substring(0, 10);
            }
            return '';
        },
        formatJoinTime(timeStr) {
            if (timeStr) {
                return timeStr.substring(0, 10) + ' ';
            }
            return '';
        },
        showEditBio() {
            this.editBioText = this.userInfo.bio || '';
            this.editBioVisible = true;
        },
        updateBio() {
            this.$api.post({
                url: '/user/info',
                data: {
                    bio: this.editBioText
                }
            }).then(res => {
                if (res.status_code === 1) {
                    this.userInfo.bio = this.editBioText;
                    this.$globalData.userInfo.bio = this.editBioText;
                    this.editBioVisible = false;
                    this.$message.success('个人简介更新成功');
                } else {
                    this.$message.error('更新失败：' + res.msg);
                }
            }).catch(error => {
                console.error('更新个人简介失败:', error);
                this.$message.error('更新失败');
            });
        }
    }
};
</script>

<style scoped>
.store-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
}

.user-info-section {
    display: flex;
    background: white;
    border-radius: 10px;
    padding: 30px;
    margin-bottom: 30px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.user-avatar {
    margin-right: 30px;
}

.user-details {
    flex: 1;
}

.user-nickname {
    margin: 0 0 15px 0;
    color: #333;
    font-size: 28px;
    font-weight: 600;
}

.user-meta {
    margin-bottom: 15px;
}

.user-meta p {
    margin: 5px 0;
    color: #666;
    font-size: 14px;
}

.user-meta i {
    margin-right: 5px;
    color: #409EFF;
}

.user-bio {
    margin-bottom: 15px;
    line-height: 1.6;
}

.user-bio p {
    margin: 0;
    color: #333;
    font-size: 16px;
}

.no-bio {
    color: #999 !important;
    font-style: italic;
}

.edit-bio-section {
    margin-top: 15px;
}

.goods-section {
    background: white;
    border-radius: 10px;
    padding: 30px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.section-header {
    margin-bottom: 20px;
    border-bottom: 2px solid #f5f5f5;
    padding-bottom: 10px;
}

.section-header h3 {
    margin: 0;
    color: #333;
    font-size: 20px;
    font-weight: 600;
}

.goods-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 20px;
    margin-bottom: 30px;
}

.goods-item {
    background: #f9f9f9;
    border-radius: 8px;
    overflow: hidden;
    cursor: pointer;
    transition: all 0.3s ease;
    border: 2px solid transparent;
}

.goods-item:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 25px rgba(0,0,0,0.15);
    border-color: #409EFF;
}

.goods-image {
    position: relative;
}

.goods-info {
    padding: 15px;
}

.goods-title {
    margin: 0 0 10px 0;
    font-size: 16px;
    font-weight: 600;
    color: #333;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.goods-price {
    margin: 0 0 10px 0;
    font-size: 18px;
    font-weight: 700;
    color: #e74c3c;
}

.goods-meta {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: #999;
}

.no-goods {
    text-align: center;
    padding: 50px 0;
}

.pagination-section {
    text-align: center;
    margin-top: 30px;
}

.image-slot {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    background: #f5f7fa;
    color: #909399;
    font-size: 30px;
}
</style> 