package com.aurora.ctoip.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Aurora
 * @since 2023-02-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String username;

    private String password;

    private LocalDateTime created;

    private LocalDateTime lastLogin;

    private Integer statu;


}
