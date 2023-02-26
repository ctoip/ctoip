package com.aurora.ctoip.controller;


import cn.hutool.core.map.MapUtil;
import com.aurora.ctoip.common.dto.PassDto;
import com.aurora.ctoip.common.lang.Result;
import com.aurora.ctoip.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Aurora
 * @since 2023-02-26
 */
@RestController
@RequestMapping("/sys-user")
public class SysUserController extends BaseController{
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/updatePass")
    public Result updatePass(@Validated @RequestBody PassDto passDto, Principal principal) {

        //principal已经被spring实现了所以可以直接调用
        SysUser sysUser = sysUserService.getByUsername(principal.getName());

        boolean matches = passwordEncoder.matches(passDto.getCurrentPass(), sysUser.getPassword());
        if (!matches) {
            return Result.fail("旧密码不正确");
        }
        sysUser.setPassword(passwordEncoder.encode(passDto.getPassword()));
        sysUserService.updateById(sysUser);
        return Result.success("");
    }

    @GetMapping("/userInfo")
    public Result userInfo(Principal principal) {
        SysUser sysUser = sysUserService.getByUsername(principal.getName());
        return Result.success(MapUtil.builder()
                .put("id", sysUser.getId())
                .put("username", sysUser.getUsername())
                .put("lastLogin", sysUser.getLastLogin())
                .put("created", sysUser.getCreated())
                .put("statu",sysUser.getStatu())
                .map()
        );
    }

}
