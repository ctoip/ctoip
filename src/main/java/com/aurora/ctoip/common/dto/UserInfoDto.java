package com.aurora.ctoip.common.dto;

import lombok.Data;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * @author:Aurora
 * @create: 2023-02-26 17:20
 * @Description:
 */
@Data
public class UserInfoDto implements Serializable {
    @NotBlank(message = "用户名不能为空")
    private String username;
    //private String currentPass;
}
