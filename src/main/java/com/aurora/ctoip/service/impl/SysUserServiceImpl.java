package com.aurora.ctoip.service.impl;

import com.aurora.ctoip.entity.SysUser;
import com.aurora.ctoip.mapper.SysUserMapper;
import com.aurora.ctoip.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author Aurora
 * @description: 继承了com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
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

    //指定sql语句
    /*
    @Mapper
    public interface MyMapper extends BaseMapper<MyEntity> {
        @Select("SELECT * FROM my_table WHERE id = #{id}")
        MyEntity getById(@Param("id") Long id);
    }
    @Service
    public class MyService {
        @Autowired
        private MyMapper myMapper;
        public MyEntity getById(Long id) {
            return myMapper.getById(id);
        }
    }
     */

    /**
     * 单个数据项更新而不是整个实体更新
     *
     * @param username
     * @return
     */
    public void updateLastLogin(String username) {
        UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("username", username).set("last_login", LocalDateTime.now());
        update(updateWrapper);
    }


}
