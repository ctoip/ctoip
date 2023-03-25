package com.aurora.ctoip.common.dto;

import lombok.Data;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import java.io.Serializable;

@Data
public class PassDto implements Serializable {

    @NotBlank(message = "新密码不能为空")
    private String password;

    @NotBlank(message = "旧密码不能为空")
    private String currentPass;
}
