package com.ken.mksz358.feignApi.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResDto {

    private String avatarUrl;

    private int bonus;

    private Integer id;

    private String wxNickname;
}
