package com.ken.mksz358.contentcenter.service;

import com.ken.mksz358.contentcenter.dao.share.ShareMapper;
import com.ken.mksz358.contentcenter.domain.entity.share.Share;
import com.ken.mksz358.feignApi.clients.UserCenterFeignClient;
import com.ken.mksz358.feignApi.pojo.ShareDto;
import com.ken.mksz358.feignApi.pojo.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ShareService {

    private final ShareMapper shareMapper;

    private final UserCenterFeignClient userCenterFeignClient;

    public ShareDto getById(Integer id) {
        if (null == id) throw new IllegalArgumentException("id can not be null!");
        //获取分享详情
        Share share = shareMapper.selectByPrimaryKey(id);

        UserDto userDto = userCenterFeignClient.findById(share.getUserId());

        ShareDto shareDto = new ShareDto();

        BeanUtils.copyProperties(share, shareDto);

        shareDto.setWxNickname(userDto.getWxNickname());

        return shareDto;

    }
}
