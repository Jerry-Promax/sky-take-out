package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid=#{openid}")
    User getOpenid(String openid);


    void insert(User user);

    /**
     * 根据用户id查询用户
     * @param userId
     * @return
     */
    @Select("select * from user where id = #{userId}")
    User getById(Long userId);
    /*
    统计用户数量
     */
    Integer countByMap(Map map);
}
