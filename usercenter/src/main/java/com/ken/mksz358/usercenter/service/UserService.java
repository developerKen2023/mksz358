package com.ken.mksz358.usercenter.service;

import com.ken.mksz358.feignApi.pojo.UserLoginDto;
import com.ken.mksz358.usercenter.dao.user.UserMapper;
import com.ken.mksz358.usercenter.domain.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {

    private final UserMapper userMapper;

    public User findById(Integer id) {
        return userMapper.selectByPrimaryKey(id);
    }

    public User addUser(UserLoginDto userLoginDto) {
        User user = userMapper.selectOne(User.builder()
                .wxId(userLoginDto.getWxId())
                .build());
        if (Objects.isNull(user)) {
            User insertUser = User.builder()
                    .wxNickname(userLoginDto.getWxNickname())
                    .wxId(userLoginDto.getWxId())
                    .roles("user")
                    .createTime(new Date())
                    .updateTime(new Date())
                    .avatarUrl("to be defined")
                    .build();
            userMapper.insertSelective(insertUser);
            return insertUser;
        } else {
            return user;
        }
    }
}
