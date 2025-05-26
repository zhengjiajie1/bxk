<template>
    <div>
        <app-head :nickname-value="userInfo.nickname" :avatarValue="userInfo.avatar"></app-head>
        <app-body>
            <div v-show="!eidtAddress">
                <div class="user-info-container">
                    <div class="user-info-details">

                        <el-upload action="http://localhost:9321/file/" :on-success="fileHandleSuccess"
                            :file-list="imgFileList" accept="image/*">
                            <el-image style="width: 120px; height: 120px;border-radius: 10px;"
                                :src="$store.state.baseApi + userInfo.avatar" fit="contain"></el-image>
                        </el-upload>
                        <div class="user-info-details-text">
                            <div class="user-info-details-text-nickname">{{ userInfo.nickname }}</div>
                            <div class="user-info-details-text-time">{{ userInfo.signInTime }} 加入平台</div>
                            <div class="user-info-details-text-level" v-if="userInfo.userLevel">
                                <el-tag :type="getLevelTagType(userInfo.userLevel)" size="small">
                                    {{ getLevelText(userInfo.userLevel) }}
                                </el-tag>
                                <span class="level-description">(卖出商品时收取{{ getLevelFeeRate(userInfo.userLevel) }}服务费)</span>
                            </div>
                            <div class="user-info-details-text-bio" v-if="userInfo.bio">{{ userInfo.bio }}</div>
                            <div class="user-info-details-text-edit">
                                <el-button type="primary" plain @click="userInfoDialogVisible = true"> 编辑个人信息</el-button>
                                <el-button type="success" plain @click="eidtAddress = true"> 编辑收货地址</el-button>
                            </div>
                            
                            <el-dialog @close="finishEdit" title="编辑个人信息" :visible.sync="userInfoDialogVisible"
                                width="400px">
                                <div class="edit-tip"> 昵称</div>
                                <el-input v-model="userInfo.nickname" :disabled="notUserNicknameEdit"
                                    @change="saveUserNickname">
                                    <el-button slot="append" type="primary" icon="el-icon-edit"
                                        @click="notUserNicknameEdit = false"> 修改昵称
                                    </el-button>
                                </el-input>

                                <div class="edit-tip"> 邮箱</div>
                                <el-input v-model="userInfo.email" :disabled="notUserEmailEdit"
                                    @change="saveUserEmail">
                                    <el-button slot="append" type="primary" icon="el-icon-edit"
                                        @click="notUserEmailEdit = false"> 修改邮箱
                                    </el-button>
                                </el-input>

                                <div class="edit-tip"> 城市</div>
                                <div class="area-select-container">
                                    <el-cascader 
                                        :options="options" 
                                        v-model="userLocationOptions" 
                                        @change="handleUserLocationChange"
                                        :separator="' '" 
                                        :disabled="notUserCityEdit"
                                        style="width: 100%;">
                                    </el-cascader>
                                    <div style="margin-top: 10px; text-align: right;">
                                        <el-button type="primary" icon="el-icon-edit" 
                                            @click="notUserCityEdit = false"> 修改城市
                                        </el-button>
                                    </div>
                                </div>
                                <div class="edit-tip" style="margin-top: 10px;"></div>
                                <div class="edit-tip"> 性别</div>
                                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                                    <el-radio-group v-model="userInfo.gender" :disabled="notUserGenderEdit" style="flex: 1;">
                                        <el-radio label="男">男</el-radio>
                                        <el-radio label="女">女</el-radio>
                                    </el-radio-group>
                                    <el-button v-if="notUserGenderEdit" type="primary" icon="el-icon-edit" 
                                        @click="notUserGenderEdit = false"> 修改性别
                                    </el-button>
                                </div>

                                <div class="edit-tip"> 银行卡号</div>
                                <el-input v-model="userInfo.bankCard" :disabled="notUserBankCardEdit"
                                    @input="validateBankCard" maxlength="16">
                                    <el-button slot="append" type="primary" icon="el-icon-edit"
                                        @click="notUserBankCardEdit = false"> 修改银行卡号
                                    </el-button>
                                </el-input>

                                <div class="edit-tip"> 个人简介</div>
                                <el-input v-model="userInfo.bio" 
                                    type="textarea" :rows="3" maxlength="50" show-word-limit>
                                </el-input>

                                <div v-if="userPasswordEdit">
                                    <div class="edit-tip">原密码</div>
                                    <el-input v-model="userPassword1" show-password></el-input>
                                    <div class="edit-tip">新密码</div>
                                    <el-input v-model="userPassword2" show-password></el-input>
                                    <div class="edit-tip">确认新密码</div>
                                    <el-input v-model="userPassword3" show-password></el-input>
                                    <div class="edit-tip"></div>
                                    <el-button @click="savePassword" type="primary" plain> 确认修改密码</el-button>
                                </div>
                                <div v-else>
                                    <div class="edit-tip">密码</div>
                                    <el-input value="123456" :disabled="true" show-password>
                                        <el-button slot="append" type="primary" icon="el-icon-edit"
                                            @click="userPasswordEdit = true"> 修改密码
                                        </el-button>
                                    </el-input>
                                </div>
                                <span slot="footer" class="dialog-footer">
                                    <el-button @click="saveUserInfo" type="success"> 保存修改</el-button>
                                    <el-button @click="userInfoDialogVisible = false" type="primary"> 关闭</el-button>
                                </span>
                            </el-dialog>
                        </div>
                    </div>
                </div>
                <div class="idle-container">
                    <el-tabs v-model="activeName" @tab-click="handleClick">
                        <el-tab-pane label="我发布的" name="1"></el-tab-pane>
                        <el-tab-pane label="我下架的" name="2"></el-tab-pane>
                        <el-tab-pane label="我卖出的" name="3"></el-tab-pane>
                        <el-tab-pane label="我买到的" name="4"></el-tab-pane>
                        <el-tab-pane label="评价记录" name="5"></el-tab-pane>
                    </el-tabs>
                    <div class="idle-container-list">
                        <!-- 评价记录标签页内容 -->
                        <div v-if="activeName === '5'" class="review-records">
                            <el-divider content-position="left">我评价的商品</el-divider>
                            <div v-if="reviewData.userReviews.length === 0" class="empty-data">
                                暂无评价记录
                            </div>
                            <!-- 我的评价列表 -->
                            <el-card v-for="(review, index) in reviewData.userReviews" :key="'user-'+index" class="review-item">
                                <div class="review-flex-container">
                                    <!-- 左侧：商品信息 -->
                                    <div class="review-left">
                                        <div class="review-product-info">
                                            <el-image 
                                                v-if="review.idleItem && review.idleItem.pictureList"
                                                style="width: 100px; height: 100px;" 
                                                :src="$store.state.baseApi + (JSON.parse(review.idleItem.pictureList)[0] || '')"
                                                fit="cover">
                                                <div slot="error" class="image-slot">
                                                    <i class="el-icon-picture-outline">无图</i>
                                                </div>
                                            </el-image>
                                            <div class="review-product-details">
                                                <h4 class="review-product-name">{{ review.idleItem ? review.idleItem.idleName : '未知商品' }}</h4>
                                                <div class="review-product-price">¥{{ review.idleItem ? review.idleItem.idlePrice : '0.00' }}</div>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <!-- 右侧：评价内容 -->
                                    <div class="review-right">
                                        <div class="review-rating">
                                            <span>我的评分：</span>
                                            <el-rate
                                                :value="review.rating"
                                                disabled
                                                show-score
                                                text-color="#ff9900">
                                            </el-rate>
                                        </div>
                                        <div class="review-text">
                                            <p>{{ review.content }}</p>
                                        </div>
                                        <div class="review-time">评价时间: {{ formatTime(review.createTime) }}</div>
                                    </div>
                                </div>
                            </el-card>
                            
                            <el-divider content-position="left">收到的评价</el-divider>
                            <div v-if="reviewData.sellerReviews.length === 0" class="empty-data">
                                暂无收到的评价
                            </div>
                            <!-- 收到的评价列表 -->
                            <el-card v-for="(review, index) in reviewData.sellerReviews" :key="'seller-'+index" class="review-item">
                                <div class="review-flex-container">
                                    <!-- 左侧：评价者信息和评价内容 -->
                                    <div class="review-left">
                                        <div class="review-user-info">
                                            <el-avatar :size="40" :src="$store.state.baseApi + (review.user ? review.user.avatar : '')"></el-avatar>
                                            <span class="review-username">{{ review.user ? review.user.nickname : '匿名用户' }}</span>
                                        </div>
                                        <div class="review-rating" style="margin-top: 10px;">
                                            <span>评分：</span>
                                            <el-rate
                                                :value="review.rating"
                                                disabled
                                                show-score
                                                text-color="#ff9900">
                                            </el-rate>
                                        </div>
                                        <div class="review-text">
                                            <p>{{ review.content }}</p>
                                        </div>
                                    </div>
                                    
                                    <!-- 右侧：商品信息 -->
                                    <div class="review-right">
                                        <div class="review-product-info-received">
                                            <el-image 
                                                v-if="review.idleItem && review.idleItem.pictureList"
                                                style="width: 100px; height: 100px; margin-right: 15px;" 
                                                :src="$store.state.baseApi + (JSON.parse(review.idleItem.pictureList)[0] || '')"
                                                fit="cover">
                                                <div slot="error" class="image-slot">
                                                    <i class="el-icon-picture-outline">无图</i>
                                                </div>
                                            </el-image>
                                            <div class="review-product-details">
                                                <h4 class="review-product-name">{{ review.idleItem ? review.idleItem.idleName : '未知商品' }}</h4>
                                                <div class="review-product-price">¥{{ review.idleItem ? review.idleItem.idlePrice : '0.00' }}</div>
                                                <div class="review-time">评价时间: {{ formatTime(review.createTime) }}</div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </el-card>
                        </div>
                        
                        <!-- 原有的商品列表 -->
                        <div v-for="(item, index) in dataList[activeName - 1]" :key="index" class="idle-container-list-item">
                            <div class="idle-container-list-item-detile" @click="toDetails(activeName, item)">
                                <el-image style="width: 115px; height: 115px;" :src="$store.state.baseApi + item.imgUrl"
                                    fit="cover">
                                    <div slot="error" class="image-slot">
                                        <i class="el-icon-picture-outline">无图</i>
                                    </div>
                                </el-image>
                                <div class="idle-container-list-item-text">
                                    <div class="idle-container-list-title">
                                        {{ item.idleName }}
                                    </div>
                                    <div class="idle-container-list-idle-details">
                                        {{ item.idleDetails }}
                                    </div>
                                    <div class="idle-container-list-idle-time">{{ item.timeStr }}</div>

                                    <div class="idle-item-foot">
                                        <div class="idle-prive">￥{{ item.idlePrice }}
                                            {{ (activeName === '3' || activeName === '4') ? orderStatus[item.orderStatus] : '' }}
                                        </div>
                                        <el-button v-if="activeName !== '3' && activeName !== '4'" type="danger"
                                            slot="reference" plain
                                            @click.stop="handle(activeName, item, index)">{{ handleName[activeName - 1] }}
                                        </el-button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div v-show="eidtAddress" class="address-container">
                <el-page-header class="address-container-back" @back="eidtAddress = false" content="收货地址"></el-page-header>
                <div class="address-container-add">
                    <div class="address-container-add-title"><b> 新增收货地址：</b></div>

                    <div class="address-container-add-item">
                        <el-input placeholder="请输入收货人姓名" v-model="addressInfo.consigneeName" maxlength="10" show-word-limit>
                            <div slot="prepend">收货人姓名</div>
                        </el-input>
                    </div>
                    <div class="address-container-add-item">
                        <el-input placeholder="请输入收货人手机号" v-model="addressInfo.consigneePhone"
                            onkeyup="this.value=this.value.replace(/[^\d.]/g,'');" maxlength="11" show-word-limit>
                            <div slot="prepend">手机号</div>
                        </el-input>
                    </div>

                    <div class="address-container-add-item">
                        <span class="demonstration">省/市/区</span>
                        <el-cascader :options="options" v-model="selectedOptions" @change="handleAddressChange"
                            :separator="' '">
                        </el-cascader>
                    </div>
                    <div class="address-container-add-item">
                        <el-input placeholder="请输入详细地址（如道路、门牌号、小区、楼栋号等信息）" v-model="addressInfo.detailAddress"
                            maxlength="50" show-word-limit>
                            <div slot="prepend">详细地址</div>
                        </el-input>
                    </div>
                    <el-checkbox v-model="addressInfo.defaultFlag">设置为默认地址</el-checkbox>
                    <el-button type="success" style="margin-left: 20px;" @click="saveAddress"> 保存</el-button>
                </div>
                <div class="address-container-list">
                    <hr>
                    <div style="color: black;padding-left: 10px;font-size: 26px;"><b>已存在的收货地址：</b></div>
                    <br>
                    <el-table stripe :data="addressData" style="width: 100%">
                        <el-table-column prop="consigneeName" label="收货人姓名" width="100">
                        </el-table-column>
                        <el-table-column prop="consigneePhone" label="手机号" width="120">
                        </el-table-column>
                        <el-table-column prop="detailAddressText" label="地址" width="270">
                        </el-table-column>
                        <el-table-column label="是否默认地址" width="110">
                            <template slot-scope="scope">
                                <el-button v-if="!scope.row.defaultFlag" size="mini"
                                    @click="handleSetDefault(scope.$index, scope.row)">设为默认
                                </el-button>
                                <div v-else style="padding-left: 10px;color: #409EFF;">{{ scope.row.defaultAddress }}
                                </div>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" width="250" fixed="right">
                            <template slot-scope="scope">
                                <el-button type="primary" size="mini" @click="handleEdit(scope.$index, scope.row)"> 编辑
                                </el-button>
                                <el-button size="mini" type="danger" @click="handleDelete(scope.$index, scope.row)"> 删除
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </div>
            </div>
        </app-body>
        <app-foot></app-foot>
        
        <!-- 商品库存编辑对话框 -->
        <el-dialog
            title="修改商品库存"
            :visible.sync="stockDialogVisible"
            width="30%">
            <el-form :model="stockForm">
                <el-form-item label="商品名称">
                    <span>{{stockForm.idleName}}</span>
                </el-form-item>
                <el-form-item label="当前库存">
                    <span>{{stockForm.oldStock}}</span>
                </el-form-item>
                <el-form-item label="新的库存">
                    <el-input-number v-model="stockForm.newStock" :min="1" :step="1"></el-input-number>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="stockDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="updateItemStock">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import AppHead from '../common/AppHeader.vue';
import AppBody from '../common/AppPageBody.vue'
import AppFoot from '../common/AppFoot.vue'
import options from '../common/country-data.js'

export default {
    name: "me",
    components: {
        AppHead,
        AppBody,
        AppFoot
    },
    data() {
        return {
            imgFileList: [],
            addressInfo: {
                consigneeName: '',
                consigneePhone: '',
                provinceName: '',
                cityName: '',
                regionName: '',
                detailAddress: '',
                defaultFlag: false
            },
            activeName: '1',
            handleName: [' 下架', ' 删除', '', ''],
            dataList: [
                [],
                [],
                [],
                [],
                [],
            ],
            orderStatus: ['待付款', '待发货', '待收货', '已完成', '已取消'],
            userInfoDialogVisible: false,
            notUserNicknameEdit: true,
            userPasswordEdit: false,
            userPassword1: '',
            userPassword2: '',
            userPassword3: '',
            eidtAddress: false,
            selectedOptions: [],
            options,
            userInfo: {
                accountNumber: "",
                id: null,
                nickname: "",
                avatar: "",
                signInTime: "",
                email: "",
                city: "",
                gender: "男",
                bankCard: "",
                provinceName: "",
                cityName: "",
                regionName: "",
                bio: "无",
                userLevel: 1
            },
            reviewData: {
                userReviews: [], // 我评价的
                sellerReviews: [] // 评价我的
            },
            userLocationOptions: [],
            addressData: [],
            notUserEmailEdit: true,
            notUserCityEdit: true,
            notUserGenderEdit: true,
            notUserBankCardEdit: true,
            notUserBioEdit: false,
            cityOptions: [
                { value: '北京', label: '北京' },
                { value: '上海', label: '上海' },
                { value: '广州', label: '广州' },
                { value: '深圳', label: '深圳' },
                { value: '杭州', label: '杭州' },
                { value: '南京', label: '南京' },
                { value: '武汉', label: '武汉' },
                { value: '西安', label: '西安' },
                { value: '成都', label: '成都' },
                { value: '重庆', label: '重庆' },
                { value: '天津', label: '天津' },
                { value: '苏州', label: '苏州' },
                { value: '长沙', label: '长沙' },
                { value: '郑州', label: '郑州' },
                { value: '青岛', label: '青岛' }
            ],
            stockDialogVisible: false,
            stockForm: {
                id: null,
                idleName: '',
                oldStock: 0,
                newStock: 1
            }
        };
    },
    created() {
        if (!this.$globalData.userInfo.nickname) {
            this.$api.getUserInfo().then(res => {
                if (res.status_code === 1) {
                    res.data.signInTime = res.data.signInTime.substring(0, 10);
                    console.log(res.data);
                    this.$globalData.userInfo = res.data;
                    this.userInfo = this.$globalData.userInfo;
                    // 初始化省市区选择器
                    this.initUserLocationOptions();
                }
            })
        } else {
            this.userInfo = this.$globalData.userInfo;
            console.log(this.userInfo);
            // 初始化省市区选择器
            this.initUserLocationOptions();
        }
        this.getAddressData();
        this.getIdleItemData();
        this.getMyOrder();
        this.getMySoldIdle();
        this.getCityOptions();
        this.getUserLevel();
    },
    methods: {
        initUserLocationOptions() {
            if (this.userInfo.provinceName && this.userInfo.cityName && this.userInfo.regionName) {
                // 如果有完整的省市区信息，使用它们
                this.userLocationOptions = [
                    this.userInfo.provinceName,
                    this.userInfo.cityName,
                    this.userInfo.regionName
                ];
            } else if (this.userInfo.city) {
                // 如果只有city字段，尝试在options中查找匹配的省份
                const foundProvince = this.findProvinceInOptions(this.userInfo.city);
                if (foundProvince) {
                    // 如果找到匹配的省份，设置为该省份及其第一个市和区
                    const firstCity = foundProvince.children[0];
                    const firstDistrict = firstCity.children[0];
                    this.userLocationOptions = [
                        foundProvince.value,
                        firstCity.value,
                        firstDistrict.value
                    ];
                    
                    // 同时更新userInfo中的字段
                    this.userInfo.provinceName = foundProvince.value;
                    this.userInfo.cityName = firstCity.value;
                    this.userInfo.regionName = firstDistrict.value;
                } else {
                    // 如果没有找到，设置city为省份，空的市和区
                    this.userLocationOptions = [this.userInfo.city, '', ''];
                }
            }
        },
        
        findProvinceInOptions(provinceName) {
            if (!provinceName) return null;
            return this.options.find(province => province.value === provinceName || province.label === provinceName);
        },
        getMySoldIdle() {
            this.$api.getMySoldIdle().then(res => {
                if (res.status_code === 1) {
                    console.log('getMySoldIdle', res.data);
                    for (let i = 0; i < res.data.length; i++) {
                        let pictureList = JSON.parse(res.data[i].idleItem.pictureList);
                        this.dataList[2].push({
                            id: res.data[i].id,
                            imgUrl: pictureList.length > 0 ? pictureList[0] : '',
                            idleName: res.data[i].idleItem.idleName,
                            idleDetails: res.data[i].idleItem.idleDetails,
                            timeStr: res.data[i].createTime.substring(0, 10) + " " + res.data[i].createTime.substring(11, 19),
                            idlePrice: res.data[i].orderPrice,
                            orderStatus: res.data[i].orderStatus
                        });
                    }
                }
            })
        },
        getMyOrder() {
            this.$api.getMyOrder().then(res => {
                if (res.status_code === 1) {
                    console.log('getMyOrder', res.data);
                    for (let i = 0; i < res.data.length; i++) {
                        let pictureList = JSON.parse(res.data[i].idleItem.pictureList);
                        this.dataList[3].push({
                            id: res.data[i].id,
                            imgUrl: pictureList.length > 0 ? pictureList[0] : '',
                            idleName: res.data[i].idleItem.idleName,
                            idleDetails: res.data[i].idleItem.idleDetails,
                            timeStr: res.data[i].createTime.substring(0, 10) + " " + res.data[i].createTime.substring(11, 19),
                            idlePrice: res.data[i].orderPrice,
                            orderStatus: res.data[i].orderStatus
                        });
                    }
                }
            })
        },
        getIdleItemData() {
            this.$api.getAllIdleItem().then(res => {
                console.log(res);
                if (res.status_code === 1) {
                    for (let i = 0; i < res.data.length; i++) {
                        res.data[i].timeStr = res.data[i].releaseTime.substring(0, 10) + " " + res.data[i].releaseTime.substring(11, 19);
                        let pictureList = JSON.parse(res.data[i].pictureList);
                        res.data[i].imgUrl = pictureList.length > 0 ? pictureList[0] : '';
                        if (res.data[i].idleStatus === 1) {
                            this.dataList[0].push(res.data[i]);
                        } else if (res.data[i].idleStatus === 2) {
                            this.dataList[1].push(res.data[i]);
                        }
                    }
                }
            })
        },
        getAddressData() {
            this.$api.getAddress().then(res => {
                if (res.status_code === 1) {
                    let data = res.data;
                    for (let i = 0; i < data.length; i++) {
                        data[i].detailAddressText = data[i].provinceName + data[i].cityName + data[i].regionName + data[i].detailAddress;
                        data[i].defaultAddress = data[i].defaultFlag ? '默认地址' : '设为默认';
                    }
                    console.log(data);
                    this.addressData = data;
                }
            })
        },
        handleClick(tab, event) {
            // console.log(tab, event);
            console.log(this.activeName);
            // 处理评价记录标签页
            if (this.activeName === '5') {
                this.getReviewData();
            }
        },
        getReviewData() {
            // 获取自己作为买家的评价
            this.$api.getUserReviews().then(res => {
                if (res.status_code === 1) {
                    console.log('getUserReviews', res.data);
                    this.reviewData.userReviews = res.data;
                }
            });
            
            // 获取自己作为卖家收到的评价
            this.$api.getSellerReviews().then(res => {
                if (res.status_code === 1) {
                    console.log('getSellerReviews', res.data);
                    this.reviewData.sellerReviews = res.data;
                }
            });
        },
        saveUserNickname() {
            this.notUserNicknameEdit = true;
            this.$api.updateUserPublicInfo({
                nickname: this.userInfo.nickname
            }).then(res => {
                console.log(res);
                this.$globalData.userInfo.nickname = this.userInfo.nickname;
            })
        },
        savePassword() {
            if (!this.userPassword1 || !this.userPassword2) {
                this.$message.error('密码为空！');
            } else if (this.userPassword2 === this.userPassword3) {
                this.$api.updatePassword({
                    oldPassword: this.userPassword1,
                    newPassword: this.userPassword2
                }).then(res => {
                    if (res.status_code === 1) {
                        this.userPasswordEdit = false;
                        this.$message({
                            message: '修改成功！',
                            type: 'success'
                        });
                        this.userPassword1 = '';
                        this.userPassword2 = '';
                        this.userPassword3 = '';
                    } else {
                        this.$message.error('旧密码错误，修改失败！');
                    }
                })
            } else {
                this.$message.error('两次输入的密码不一致！');
            }

        },
        finishEdit() {
            this.notUserNicknameEdit = true;
            this.notUserEmailEdit = true;
            this.notUserCityEdit = true;
            this.notUserGenderEdit = true;
            this.notUserBankCardEdit = true;
            this.notUserBioEdit = false;
            this.userPasswordEdit = false;
        },
        handleAddressChange(value) {
            console.log(value);
            this.addressInfo.provinceName = value[0];
            this.addressInfo.cityName = value[1];
            this.addressInfo.regionName = value[2];
        },
        handleEdit(index, row) {
            console.log(index, row);
            this.addressInfo = JSON.parse(JSON.stringify(row));
            this.selectedOptions = ['', '', ''];
            this.selectedOptions[0] = row.provinceName;
            this.selectedOptions[1] = row.cityName;
            this.selectedOptions[2] = row.regionName;
        },
        handleDelete(index, row) {
            console.log(index, row);
            this.$confirm('是否确定删除该地址?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.$api.deleteAddress(row).then(res => {
                    if (res.status_code === 1) {
                        this.$message({
                            message: '删除成功！',
                            type: 'success'
                        });
                        this.addressData.splice(index, 1);
                        if (row.defaultFlag && this.addressData.length > 0) {
                            this.addressData[0].defaultFlag = true;
                            this.addressData[0].defaultAddress = '默认地址';
                            this.update({
                                id: this.addressData[0].id,
                                defaultFlag: true
                            });
                        }
                    } else {
                        this.$message.error('系统异常，删除失败！')
                    }
                }).catch(() => {
                    this.$message.error('网络异常！')
                });
            }).catch(() => {
            });

        },
        handleSetDefault(index, row) {
            console.log(index, row);
            row.defaultFlag = true;
            this.update(row);
        },
        toDetails(activeName, item) {
            if (activeName === '3' || activeName === '4') {
                this.$router.push({ path: '/order', query: { id: item.id } });
            } else {
                this.$router.push({ path: '/details', query: { id: item.id } });
            }
        },
        handle(activeName, item, index) {
            console.log(activeName, item, index);
            this.$confirm('是否确认？', '提示', {
                confirmButtonText: '确认',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                if (activeName === '1') {
                    this.$api.updateIdleItem({
                        id: item.id,
                        idleStatus: 2
                    }).then(res => {
                        console.log(res);
                        if (res.status_code === 1) {
                            this.dataList[0].splice(index, 1);
                            item.idleStatus = 2;
                            this.dataList[1].unshift(item);
                        } else {
                            this.$message.error(res.msg)
                        }
                    });
                } else if (activeName === '2') {
                    this.$api.updateIdleItem({
                        id: item.id,
                        idleStatus: 0
                    }).then(res => {
                        console.log(res);
                        if (res.status_code === 1) {
                            this.dataList[1].splice(index, 1);
                        } else {
                            this.$message.error(res.msg)
                        }
                    });
                }
            }).catch(() => {
            });

        },
        fileHandleSuccess(response, file, fileList) {
            console.log("file:", response, file, fileList);
            let imgUrl = response.data;
            this.imgFileList = [];
            this.$api.updateUserPublicInfo({
                avatar: imgUrl
            }).then(res => {
                console.log(res);
                this.userInfo.avatar = imgUrl;
                this.$globalData.userInfo.avatar = imgUrl;
            })
        },
        update(data) {
            this.$api.updateAddress(data).then(res => {
                if (res.status_code === 1) {
                    this.getAddressData();
                    this.$message({
                        message: '修改成功！',
                        type: 'success'
                    });
                } else {
                    this.$message.error('系统异常，修改失败！')
                }
            }).catch(() => {
                this.$message.error('网络异常！')
            })
        },
        saveAddress() {
            if (this.addressInfo.id) {
                console.log('update:', this.addressInfo);
                this.update(this.addressInfo);
                this.addressInfo = {
                    consigneeName: '',
                    consigneePhone: '',
                    provinceName: '',
                    cityName: '',
                    regionName: '',
                    detailAddress: '',
                    defaultFlag: false
                };
                this.selectedOptions = [];
            } else {
                if (this.addressData.length >= 5) {
                    this.$message.error('已达到最大地址数量！')
                } else {
                    console.log(this.addressInfo);
                    this.$api.addAddress(this.addressInfo).then(res => {
                        if (res.status_code === 1) {
                            this.getAddressData();
                            this.$message({
                                message: '新增成功！',
                                type: 'success'
                            });
                            this.selectedOptions = [];
                            this.addressInfo = {
                                consigneeName: '',
                                consigneePhone: '',
                                provinceName: '',
                                cityName: '',
                                regionName: '',
                                detailAddress: '',
                                defaultFlag: false
                            };
                        } else {
                            this.$message.error('系统异常，新增失败！')
                        }
                    }).catch(e => {
                        this.$message.error('网络异常！')
                    })
                }
            }
        },
        getCityOptions() {
            // 由于我们已经在data中硬编码了城市选项，不需要从API获取
            // 这个方法目前为空，保留是为了将来可能从API获取城市列表
        },
        handleUserLocationChange(value) {
            if(value && value.length === 3) {
                this.userInfo.provinceName = value[0];
                this.userInfo.cityName = value[1];
                this.userInfo.regionName = value[2];
                // 设置城市字段为省份名称，保持与后端兼容性
                this.userInfo.city = value[0];
            }
        },
        saveUserEmail() {
            this.notUserEmailEdit = true;
            // 这个单独的方法暂时不使用，改用saveUserInfo方法统一保存
        },
        saveUserInfo() {
            this.notUserNicknameEdit = true;
            this.notUserEmailEdit = true;
            this.notUserCityEdit = true;
            this.notUserGenderEdit = true;
            this.notUserBankCardEdit = true;
            this.$api.updateUserPublicInfo({
                nickname: this.userInfo.nickname,
                email: this.userInfo.email,
                city: this.userInfo.city,
                gender: this.userInfo.gender,
                bankCard: this.userInfo.bankCard,
                provinceName: this.userInfo.provinceName,
                cityName: this.userInfo.cityName,
                regionName: this.userInfo.regionName,
                bio: this.userInfo.bio
            }).then(res => {
                console.log(res);
                this.$globalData.userInfo.nickname = this.userInfo.nickname;
                this.$globalData.userInfo.email = this.userInfo.email;
                this.$globalData.userInfo.city = this.userInfo.city;
                this.$globalData.userInfo.gender = this.userInfo.gender;
                this.$globalData.userInfo.bankCard = this.userInfo.bankCard;
                this.$globalData.userInfo.provinceName = this.userInfo.provinceName;
                this.$globalData.userInfo.cityName = this.userInfo.cityName;
                this.$globalData.userInfo.regionName = this.userInfo.regionName;
                this.$globalData.userInfo.bio = this.userInfo.bio;
                this.$message.success('个人信息更新成功！');
            }).catch(e => {
                console.error(e);
                this.$message.error('个人信息更新失败！');
            });
        },
        validateBankCard() {
            if (this.userInfo.bankCard && this.userInfo.bankCard.length !== 16) {
                this.$message.warning('银行卡号应为16位数字');
            }
        },
        formatStatus(row, column) {
            const status = row[column.property];
            return status === 1 ? '上架' : status === 2 ? '下架' : '未知状态';
        },
        editIdleItem(item) {
            this.stockDialogVisible = true;
            this.stockForm.id = item.id;
            this.stockForm.idleName = item.idleName;
            this.stockForm.oldStock = item.idleStock || 1;
            this.stockForm.newStock = item.idleStock || 1;
        },
        updateItemStock() {
            this.$api.updateIdleItem({
                id: this.stockForm.id,
                idleStock: this.stockForm.newStock
            }).then(res => {
                if (res.status_code === 1) {
                    this.$message({
                        message: '库存修改成功！',
                        type: 'success'
                    });
                    
                    // 更新本地列表中的数据
                    const item = this.dataList[1].find(item => item.id === this.stockForm.id);
                    if (item) {
                        item.idleStock = this.stockForm.newStock;
                    }
                } else {
                    this.$message.error(res.msg || '修改失败');
                }
                this.stockDialogVisible = false;
            }).catch(() => {
                this.$message.error('网络错误，请稍后重试');
                this.stockDialogVisible = false;
            });
        },
        deleteIdleItem(item) {
            // 实现删除商品的逻辑
            console.log('删除商品:', item);
        },
        updateStatus(item) {
            // 实现更新商品状态的逻辑
            console.log('更新商品状态:', item);
        },
        // 格式化时间
        formatTime(time) {
            if (!time) return '无';
            let date = new Date(time);
            return date.toLocaleString();
        },
        // 新增：获取用户等级
        getUserLevel() {
            this.$api.getUserLevel().then(res => {
                if (res.status_code === 1) {
                    this.userInfo.userLevel = res.data;
                    this.$globalData.userInfo.userLevel = res.data;
                }
            }).catch(e => {
                console.log('获取用户等级失败:', e);
            });
        },
        // 新增：获取等级文本
        getLevelText(level) {
            const levelMap = {
                1: "等级1",
                2: "等级2", 
                3: "等级3",
                4: "等级4",
                5: "等级5"
            };
            return levelMap[level] || "等级1";
        },
        // 新增：获取等级标签类型
        getLevelTagType(level) {
            const typeMap = {
                1: "success",
                2: "info",
                3: "warning", 
                4: "warning",
                5: "danger"
            };
            return typeMap[level] || "success";
        },
        // 新增：获取等级费率
        getLevelFeeRate(level) {
            const rateMap = {
                1: "0.1%",
                2: "0.2%",
                3: "0.5%",
                4: "0.75%",
                5: "1%"
            };
            return rateMap[level] || "0.1%";
        }
    }
}
</script>

<style scoped>
.user-info-container {
    width: 100%;
    height: 200px;
    border-bottom: 15px solid #f6f6f6;
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.user-info-details {
    display: flex;
    height: 140px;
    align-items: center;
    margin: 20px 40px;
}

.user-info-details-text {
    margin-left: 20px;
}

.user-info-details-text-nickname {
    font-size: 26px;
    font-weight: 600;
    margin: 10px 0;
}

.user-info-details-text-time {
    font-size: 14px;
    margin-bottom: 10px;
}

.user-info-details-text-level {
    font-size: 14px;
    margin-bottom: 10px;
}

.level-description {
    margin-left: 8px;
    color: #909399;
    font-size: 12px;
}

.user-info-details-text-bio {
    font-size: 14px;
    color: #666;
    margin-bottom: 10px;
    font-style: italic;
}

.user-info-splace {
    margin-right: 90px;
}

.idle-container {
    padding: 0 20px;
}

.idle-container-list {
    min-height: 55vh;
}

.idle-container-list-item {
    border-bottom: 1px solid #eeeeee;
    cursor: pointer;
}

.idle-container-list-item:last-child {
    border-bottom: none;
}

.idle-container-list-item-detile {
    height: 120px;
    display: flex;
    align-items: center;
}

.idle-container-list-item-text {
    margin-left: 10px;
    height: 120px;
    max-width: 800px;
}

.idle-container-list-title {
    font-weight: 600;
    font-size: 18px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}

.idle-container-list-idle-details {
    font-size: 14px;
    color: #555555;
    padding-top: 5px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}

.idle-container-list-idle-time {
    font-size: 13px;
    padding-top: 5px;
}

.idle-prive {
    font-size: 15px;
    padding-top: 5px;
    color: red;
}

.edit-tip {
    font-size: 14px;
    margin: 10px 5px;
}

.address-container {
    padding: 10px 20px;
}

.address-container-back {
    margin-bottom: 10px;
}

.address-container-add-title {
    color: #409EFF;
    padding: 10px;
    color: black;
    font-size: 28px;
}

.address-container-add-item {
    margin-bottom: 20px;
}

.demonstration {
    color: #666666;
    font-size: 14px;
    padding: 10px;
}

.address-container-add {
    padding: 0 200px;
}

.address-container-list {
    padding: 30px 100px;
}

.idle-item-foot {
    width: 800px;
    display: flex;
    justify-content: space-between;
}

.area-select-container {
    margin-bottom: 20px;
}

.review-records {
    padding: 20px;
}

.review-item {
    margin-bottom: 20px;
}

.review-flex-container {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
}

.review-left {
    flex: 1;
    padding-right: 20px;
    border-right: 1px solid #eee;
}

.review-right {
    flex: 2;
    padding-left: 20px;
}

.review-product-info {
    display: flex;
    align-items: center;
}

.review-product-info-received {
    display: flex;
    align-items: flex-start;
}

.review-product-details {
    margin-left: 15px;
}

.review-product-name {
    font-weight: 600;
    font-size: 16px;
    margin-bottom: 8px;
    max-width: 250px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}

.review-product-price {
    font-size: 14px;
    color: #ff5500;
    margin-bottom: 8px;
}

.review-rating {
    margin-bottom: 10px;
}

.review-text {
    margin-bottom: 10px;
    max-width: 100%;
    word-break: break-word;
}

.review-text p {
    line-height: 1.5;
    color: #333;
}

.review-time {
    font-size: 12px;
    color: #909399;
}

.review-user-info {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
}

.review-username {
    margin-left: 10px;
    font-weight: bold;
}

.empty-data {
    text-align: center;
    padding: 30px;
    color: #909399;
    background: #f8f8f8;
    border-radius: 4px;
}
</style>