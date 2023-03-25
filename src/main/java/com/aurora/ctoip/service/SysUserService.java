package com.aurora.ctoip.service;

import com.aurora.ctoip.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Aurora
 * @since 2023-02-26
 */
public interface SysUserService extends IService<SysUser> {
    SysUser getByUsername(String username);

    String getUserAuthorityInfo(Integer userId);

    void updateLastLogin(String username);
}
