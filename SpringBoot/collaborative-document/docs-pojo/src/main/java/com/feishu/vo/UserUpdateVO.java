package com.feishu.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateVO implements Serializable {

    private String id;
    private String username;
    private String email;
    private String picture;
    private String desc;

}
