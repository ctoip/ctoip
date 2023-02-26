package com.aurora.ctoip.service.impl;

import com.aurora.ctoip.entity.SysUser;
import com.aurora.ctoip.mapper.SysUserMapper;
import com.aurora.ctoip.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Aurora
 * @since 2023-02-26
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public SysUser getByUsername(String username) {
        return getOne(new QueryWrapper<SysUser>().eq("username", username));
    }

    @Override
    public String getUserAuthorityInfo(Integer userId) {
        return null;
    }
}
