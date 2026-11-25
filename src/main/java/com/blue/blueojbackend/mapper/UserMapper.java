package com.blue.blueojbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blue.blueojbackend.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
