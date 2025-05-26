<template>
    <div class="main-border">
        <el-table
            :data="pendingUsers"
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
                min-width="120"
                width="120">
            </el-table-column>
            <el-table-column
                prop="nickname"
                label="用户昵称"
                show-overflow-tooltip
                min-width="120"
                width="120">
            </el-table-column>
            <el-table-column
                prop="email"
                label="邮箱"
                show-overflow-tooltip
                min-width="150"
                width="150">
            </el-table-column>
            <el-table-column
                prop="city"
                label="城市"
                show-overflow-tooltip
                width="80">
            </el-table-column>
            <el-table-column
                prop="signInTime"
                label="注册时间"
                show-overflow-tooltip
                width="180">
            </el-table-column>
            <el-table-column
                width="200"
                label="操作">
                <template slot-scope="scope">
                    <el-button 
                        size="mini" 
                        type="success"
                        @click="handleApprove(scope.row)">通过</el-button>
                    <el-button 
                        size="mini" 
                        type="danger"
                        @click="handleReject(scope.row)">拒绝</el-button>
                </template>
            </el-table-column>
        </el-table>
        <div class="block">
            <el-pagination
                @current-change="handleCurrentChange"
                layout="prev, pager, next"
                :page-size="pageSize"
                :total="total">
            </el-pagination>
        </div>
    </div>
</template>

<script>
    export default {
        name: "userAudit",
        data() {
            return {
                pendingUsers: [],
                currentPage: 1,
                pageSize: 8,
                total: 0
            }
        },
        created() {
            this.getPendingUsers();
        },
        methods: {
            getPendingUsers() {
                this.$api.admin({
                    url: 'getPendingUsers',
                    method: 'get',
                    params: {
                        page: this.currentPage,
                        nums: this.pageSize
                    }
                }).then(res => {
                    if (res.status_code === 1) {
                        this.pendingUsers = res.data.list;
                        this.total = res.data.count;
                    } else {
                        this.$message.error(res.msg || '获取待审核用户失败');
                    }
                }).catch(err => {
                    console.error(err);
                    this.$message.error('获取待审核用户失败');
                });
            },
            handleCurrentChange(val) {
                this.currentPage = val;
                this.getPendingUsers();
            },
            handleApprove(row) {
                this.$confirm('确认通过该用户的注册申请?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'success'
                }).then(() => {
                    this.approveUser(row.id, true);
                }).catch(() => {
                    // 取消操作
                });
            },
            handleReject(row) {
                this.$confirm('确认拒绝该用户的注册申请? 该操作将删除此用户的所有数据。', '警告', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.approveUser(row.id, false);
                }).catch(() => {
                    // 取消操作
                });
            },
            approveUser(userId, approved) {
                this.$api.admin({
                    url: 'approveUser',
                    method: 'get',
                    params: {
                        id: userId,
                        approved: approved
                    }
                }).then(res => {
                    if (res.status_code === 1) {
                        this.$message({
                            type: 'success',
                            message: approved ? '已通过用户申请' : '已拒绝用户申请'
                        });
                        this.getPendingUsers(); // 刷新列表
                    } else {
                        this.$message.error(res.msg || '操作失败');
                    }
                }).catch(err => {
                    console.error(err);
                    this.$message.error('操作失败');
                });
            }
        }
    }
</script>

<style scoped>
.main-border {
    padding: 10px;
    margin: 10px;
    border-radius: 5px;
    background-color: #fff;
}
.block {
    margin-top: 20px;
    text-align: center;
}
</style> 