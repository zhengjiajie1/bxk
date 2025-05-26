<template>
    <div class="main-border">
        <el-menu default-active="1" class="el-menu-demo" mode="horizontal" @select="handleSelect">
            <el-menu-item index="1">正常用户</el-menu-item>
            <el-menu-item index="2">违规用户</el-menu-item>
            <el-menu-item index="3">管理员</el-menu-item>

            <div v-show="this.mode ==3" class="addAdminButton">
                <el-button size="mini" type="success" @click="adminRegVisible = true"> 添加管理员</el-button>
                <el-dialog
                        title="添加管理员"
                        :visible.sync="adminRegVisible"
                        width="25%"
                       >
                    <span style="margin-left: 10px">新增管理员名称</span>
                    <el-input v-model="adminName"  maxlength="8" placeholder="请输入管理员名称" style="padding: 10px 0" clearable required></el-input>
                    <span style="margin-left: 10px">新增管理员账号</span>
                    <el-input v-model="adminAccount" minlength="8" maxlength="10" placeholder="请输入管理员账户" style="padding: 10px 0"
                               clearable required></el-input>
                    <span style="margin-left: 10px">新增管理员密码</span>
                    <el-input v-model="adminPassword" minlength="8" placeholder="请输入管理员密码" style="padding: 10px 0" show-password required></el-input>
                    <span style="margin-left: 10px">确认管理员密码</span>
                    <el-input v-model="adminRePassword" minlength="10" placeholder="请再次输入管理员密码" style="padding: 10px 0" show-password required></el-input>
                    <span slot="footer" class="dialog-footer">
                        <el-button type="primary" @click="regAdmin"> 添加</el-button>
                    </span>
                </el-dialog>

            </div>
        </el-menu>
        
        <!-- 新增：用户等级设置对话框 -->
        <el-dialog
                title="设置用户等级"
                :visible.sync="levelDialogVisible"
                width="30%">
            <div style="margin-bottom: 20px">
                <span>用户信息：</span>
                <div v-if="currentUser" style="margin-top: 10px; padding: 10px; background-color: #f5f5f5; border-radius: 4px;">
                    <p><strong>账号：</strong>{{ currentUser.accountNumber }}</p>
                    <p><strong>昵称：</strong>{{ currentUser.nickname }}</p>
                    <p><strong>当前等级：</strong>{{ getLevelText(currentUser.userLevel) }}</p>
                </div>
            </div>
            <div style="margin-bottom: 20px">
                <span style="margin-right: 10px">新等级：</span>
                <el-select v-model="newUserLevel" placeholder="请选择用户等级" style="width: 200px">
                    <el-option label="等级1 (服务费0.1%)" :value="1"></el-option>
                    <el-option label="等级2 (服务费0.2%)" :value="2"></el-option>
                    <el-option label="等级3 (服务费0.5%)" :value="3"></el-option>
                    <el-option label="等级4 (服务费0.75%)" :value="4"></el-option>
                    <el-option label="等级5 (服务费1%)" :value="5"></el-option>
                </el-select>
            </div>
            <div style="margin-bottom: 20px; color: #909399; font-size: 14px">
                <p><strong>说明：</strong></p>
                <p>• 用户等级决定了卖家在交易中需要支付的服务费费率</p>
                <p>• 等级越高，服务费费率越高</p>
                <p>• 服务费仅向卖家征收，不对买家征收</p>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="levelDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="updateUserLevel" :loading="levelUpdating">确定</el-button>
            </span>
        </el-dialog>
        <el-table v-show="this.mode == 1"
                  :data="userData"
                  stripe
                  style="width: 100%;color: #5a5c61;">
            <el-table-column label="头像" width="50">
                <template slot-scope="scope">
                    <el-avatar shape="square" :size="23" :src="$store.state.baseApi + scope.row.avatar"></el-avatar>
                </template>
            </el-table-column>
            <el-table-column
                    prop="accountNumber"
                    label="用户账号"
                    show-overflow-tooltip
                    min-width="150"
                    width="150">
            </el-table-column>
            <el-table-column
                    prop="nickname"
                    label="用户昵称"
                    show-overflow-tooltip
                    min-width="150"
                    width="150">
            </el-table-column>
            <el-table-column
                    prop="signInTime"
                    label="注册时间"
                    show-overflow-tooltip
                    width="200">
            </el-table-column>
            <el-table-column
                    label="用户等级"
                    width="100">
                <template slot-scope="scope">
                    <el-tag :type="getLevelTagType(scope.row.userLevel)">
                        {{ getLevelText(scope.row.userLevel) }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
                <template slot-scope="scope">
                    <div class="operation-buttons">
                        <el-button
                                size="mini"
                                type="primary"
                                @click="showLevelDialog(scope.$index)">设置等级</el-button>
                        <el-button
                                size="mini"
                                type="danger"
                                @click="sealUser(scope.$index)">封号</el-button>
                    </div>
                </template>
            </el-table-column>
        </el-table>
        <el-table v-show="this.mode == 2"
                  :data="badUserData"
                  stripe
                  style="width: 100%;color: #5a5c61;">
            <el-table-column
                    label="头像"
                    width="50">
                <template slot-scope="scope">
                    <el-avatar shape="square" :size="23" :src="$store.state.baseApi + scope.row.avatar"></el-avatar>
                </template>
            </el-table-column>
            <el-table-column
                    prop="accountNumber"
                    label="用户账号"
                    show-overflow-tooltip
                    min-width="150"
                    width="150">
            </el-table-column>
            <el-table-column
                    prop="nickname"
                    label="用户昵称"
                    show-overflow-tooltip
                    width="150"
            >
            </el-table-column>
            <el-table-column
                    prop="signInTime"
                    label="注册时间"
                    show-overflow-tooltip
                    width="200">
            </el-table-column>
            <el-table-column
                    label="用户等级"
                    width="100">
                <template slot-scope="scope">
                    <el-tag :type="getLevelTagType(scope.row.userLevel)">
                        {{ getLevelText(scope.row.userLevel) }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="140">
                <template slot-scope="scope">
                    <el-button
                            size="mini"
                            type="success"
                            @click="unsealUser(scope.$index)"> 解封</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-table  v-show="this.mode == 3"
                :data="userManage"
                stripe
                style="width: 100%;color: #5a5c61;">
            <el-table-column
                    prop="accountNumber"
                    label="管理员账号"
                    show-overflow-tooltip
                    width="200">
            </el-table-column>
            <el-table-column
                    prop="adminName"
                    label="管理名称"
                    >
            </el-table-column>
        </el-table>
        <div class="block">
            <el-pagination
                    @current-change="handleCurrentChange"
                    :current-page.sync="nowPage"
                    :page-size="7"
                    background
                    layout="prev, pager, next,jumper"
                    :total="total">
            </el-pagination>
        </div>
    </div>
</template>

<script>
    export default {
        name: "userList",
        created() {
            this.getUserData();
        },
        methods: {
            handleCurrentChange(val) {
                this.nowPage = val;
                if(this.mode == 1){
                    this.getUserData();
                }
                if(this.mode == 2){
                    this.getBadUserData();
                }
                if(this.mode == 3){
                    this.getUserManage();
                }
            },
            handleSelect(val){
                if(this.mode !== val){
                    this.mode = val
                    if(val == 1){
                        this.nowPage = 1;
                        this.getUserData();
                    }
                    if(val == 2){
                        this.nowPage = 1;
                        this.getBadUserData();
                    }
                    if(val == 3){
                        this.nowPage = 1;
                        this.getUserManage();
                    }
                }
            },
            getUserData(){
                //正常普通用户
                this.$api.getUserData({
                    page: this.nowPage,
                    nums:8,
                    status:0
                }).then(res => {
                    if(res.status_code==1){
                        this.userData = res.data.list;
                        this.total = res.data.count;
                    }else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            },
            getBadUserData(){
                //违规用户
                this.$api.getUserData({
                    page: this.nowPage,
                    nums:8,
                    status:1
                }).then(res => {
                    if(res.status_code==1){
                        this.badUserData = res.data.list;
                        this.total = res.data.count;
                    }else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                });
            },
            getUserManage(){
                //管理员
                this.$api.getUserManage({
                    page: this.nowPage,
                    nums:8,
                }).then(res => {
                    if(res.status_code==1){
                        this.userManage = res.data.list;
                        this.total = res.data.count;
                    }else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            },
            sealUser(i){
                console.log( this.userData[i].id);
                this.$api.updateUserStatus({
                    id: this.userData[i].id,
                    status:1
                }).then(res => {
                    if(res.status_code==1){
                        this.getUserData();
                    }else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            },
            unsealUser(i){
                this.$api.updateUserStatus({
                    id: this.badUserData[i].id,
                    status:0
                }).then(res => {
                    if(res.status_code==1){
                        this.getBadUserData();
                    }else {
                        this.$message.error(res.msg)
                    }

                }).catch(e => {
                    console.log(e)
                })
            },
            regAdmin(){
                if(this.adminPassword == this.adminRePassword){
                    this.$api.regAdministrator({
                        adminName: this.adminName,
                        accountNumber: this.adminAccount,
                        adminPassword: this.adminPassword,
                    }).then(res => {
                        if(res.status_code==1){
                            this.total = this.total+1;
                            this.nowPage= Math.ceil(this.total/8);
                            console.log(this.nowPage);
                            this.getUserManage();
                        }else {
                            this.$message.error(res.msg)
                        }
                    }).catch(e => {
                        console.log(e);
                        this.$message.error("添加失败，账号重复或网络异常")
                    });
                    this.adminRegVisible = false
                }
                else {
                    this.$message.error("两次输入的密码不一致");
                }
            },
            // 新增：用户等级管理相关方法
            showLevelDialog(index) {
                this.currentUser = this.userData[index];
                this.newUserLevel = this.currentUser.userLevel || 1;
                this.levelDialogVisible = true;
            },
            updateUserLevel() {
                if (!this.currentUser || !this.newUserLevel) {
                    this.$message.error("请选择有效的用户等级");
                    return;
                }
                
                this.levelUpdating = true;
                this.$api.updateUserLevel({
                    userId: this.currentUser.id,
                    userLevel: this.newUserLevel
                }).then(res => {
                    if (res.status_code == 1) {
                        this.$message.success("用户等级更新成功");
                        this.levelDialogVisible = false;
                        this.getUserData(); // 刷新用户列表
                    } else {
                        this.$message.error(res.msg || "更新失败");
                    }
                }).catch(e => {
                    console.log(e);
                    this.$message.error("更新失败，网络异常");
                }).finally(() => {
                    this.levelUpdating = false;
                });
            },
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
        },
        data(){
            return {
                mode:1,
                nowPage: 1,
                total: 63,
                adminRegVisible: false,
                adminAccount:'',
                adminPassword:'',
                adminRePassword:'',
                adminName:'',
                userData: [],
                badUserData:[],
                userManage:[],
                // 新增：用户等级管理相关数据
                levelDialogVisible: false,
                currentUser: null,
                newUserLevel: 1,
                levelUpdating: false,
            }
        },
    }
</script>

<style scoped>
    .main-border{
        background-color: #FFF;
        padding: 10px 30px;
        box-shadow: 0 1px 15px -6px rgba(0,0,0,.5);
        border-radius: 5px;
    }
    .block {
        display: flex;
        justify-content:center;
        padding-top: 15px;
        padding-bottom: 10px;
        width: 100%;
    }
    .addAdminButton{
        display:flex;
        justify-content: flex-end;
        align-items: center;
        height: 60px;
        outline: none;
    }
    
    /* 操作按钮样式 */
    .operation-buttons {
        display: flex;
        gap: 8px;
        align-items: center;
        justify-content: flex-start;
    }
    
    .operation-buttons .el-button {
        width: 80px !important;
        height: 28px !important;
        padding: 6px 8px !important;
        font-size: 12px !important;
        line-height: 1 !important;
        text-align: center !important;
        border-radius: 4px !important;
    }
    
    .operation-buttons .el-button span {
        font-size: 12px !important;
        font-weight: normal !important;
        white-space: nowrap !important;
    }
</style>