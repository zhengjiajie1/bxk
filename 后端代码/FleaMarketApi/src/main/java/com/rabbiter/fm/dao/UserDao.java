package com.rabbiter.fm.dao;

import com.rabbiter.fm.model.UserModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDao {
    int deleteByPrimaryKey(Long id);

    int insert(UserModel record);

    int insertSelective(UserModel record);

    UserModel userLogin(@Param("accountNumber") String accountNumber, @Param("userPassword") String userPassword);

    UserModel selectByPrimaryKey(Long id);

    List<UserModel> getUserList();

    List<UserModel> findUserByList(List<Long> idList);

    List<UserModel> getNormalUser(int begin, int nums);

    List<UserModel> getBanUser(int begin, int nums);

    List<UserModel> getPendingUser(int begin, int nums);

    int countNormalUser();

    int countBanUser();

    int countPendingUser();

    int updateByPrimaryKeySelective(UserModel record);

    int updateByPrimaryKey(UserModel record);

    int updatePassword(@Param("newPassword") String newPassword,
                       @Param("oldPassword") String oldPassword,@Param("id") Long id);
}