<template>
    <div class="main-border">
        <el-table
            :data="pendingGoods"
            stripe
            style="width: 100%;color: #5a5c61;">
            <el-table-column label="商品图片" width="80">
                <template slot-scope="scope">
                    <el-image 
                        style="width: 60px; height: 60px" 
                        :src="getFirstImage(scope.row.pictureList)"
                        fit="cover">
                        <div slot="error" class="image-slot">
                            <i class="el-icon-picture-outline">无图</i>
                        </div>
                    </el-image>
                </template>
            </el-table-column>
            <el-table-column
                prop="idleName"
                label="商品名称"
                show-overflow-tooltip
                min-width="150"
                width="150">
            </el-table-column>
            <el-table-column
                label="商品价格"
                width="100">
                <template slot-scope="scope">
                    ￥{{ scope.row.idlePrice }}
                </template>
            </el-table-column>
            <el-table-column
                prop="idlePlace"
                label="发货地区"
                width="100">
            </el-table-column>
            <el-table-column
                label="发布者"
                width="120">
                <template slot-scope="scope">
                    <div v-if="scope.row.user" style="display: flex; align-items: center;">
                        <el-avatar shape="square" :size="20" :src="$store.state.baseApi + scope.row.user.avatar" style="margin-right: 8px;"></el-avatar>
                        <span>{{ scope.row.user.nickname }}</span>
                    </div>
                </template>
            </el-table-column>
            <el-table-column
                prop="releaseTime"
                label="发布时间"
                show-overflow-tooltip
                width="180">
                <template slot-scope="scope">
                    {{ formatTime(scope.row.releaseTime) }}
                </template>
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
        name: "goodsAudit",
        data() {
            return {
                pendingGoods: [],
                currentPage: 1,
                pageSize: 8,
                total: 0
            }
        },
        created() {
            this.getPendingGoods();
        },
        methods: {
            getPendingGoods() {
                this.$api.admin({
                    url: 'getPendingGoods',
                    method: 'get',
                    params: {
                        page: this.currentPage,
                        nums: this.pageSize
                    }
                }).then(res => {
                    if (res.status_code === 1) {
                        this.pendingGoods = res.data.list;
                        this.total = res.data.count;
                    } else {
                        this.$message.error(res.msg || '获取待审核商品失败');
                    }
                }).catch(err => {
                    console.error(err);
                    this.$message.error('获取待审核商品失败');
                });
            },
            handleCurrentChange(val) {
                this.currentPage = val;
                this.getPendingGoods();
            },
            handleApprove(row) {
                this.$confirm('确认通过该商品的发布申请?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'success'
                }).then(() => {
                    this.approveGoods(row.id, true);
                }).catch(() => {
                    // 取消操作
                });
            },
            handleReject(row) {
                this.$confirm('确认拒绝该商品的发布申请? 该操作将删除此商品的所有数据。', '警告', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.approveGoods(row.id, false);
                }).catch(() => {
                    // 取消操作
                });
            },
            approveGoods(goodsId, approved) {
                this.$api.admin({
                    url: 'approveGoods',
                    method: 'get',
                    params: {
                        id: goodsId,
                        approved: approved
                    }
                }).then(res => {
                    if (res.status_code === 1) {
                        this.$message({
                            type: 'success',
                            message: approved ? '已通过商品发布申请' : '已拒绝商品发布申请'
                        });
                        this.getPendingGoods(); // 刷新列表
                    } else {
                        this.$message.error(res.msg || '操作失败');
                    }
                }).catch(err => {
                    console.error(err);
                    this.$message.error('操作失败');
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
                    return timeStr.substring(0, 10) + " " + timeStr.substring(11, 19);
                }
                return '';
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
.image-slot {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    background: #f5f7fa;
    color: #909399;
    font-size: 12px;
}
</style> 