package com.leguan.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.leguan.ucenter.mapper.XcMenuMapper;
import com.leguan.ucenter.mapper.XcUserMapper;
import com.leguan.ucenter.model.dto.AuthParamsDto;
import com.leguan.ucenter.model.dto.XcUserExt;
import com.leguan.ucenter.model.po.XcMenu;
import com.leguan.ucenter.model.po.XcUser;
import com.leguan.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    XcMenuMapper xcMenuMapper;

    //传入的请求认证的参数就是AuthParamsDto
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //将传入的json转为AuthParamsDto对象
        AuthParamsDto authParamsDto = null;

        try {
            authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
        } catch (Exception e) {
            throw new RuntimeException("请求认证的参数不符合要求");
        }

        //认证类型，password、wx
        String authType = authParamsDto.getAuthType();

        //根据认证类型从spring容器取出指定的bean
        String beanName = authType + "_authservice";
        AuthService authService = applicationContext.getBean(beanName, AuthService.class);
        //调用统一execute方法完成认证
        XcUserExt xcUserExt = authService.execute(authParamsDto);
        //封装xcUserExt用户信息为UserDetails
        //最后是根据UserDetails对象生成令牌
        UserDetails userPrincipal = getUserPrincipal(xcUserExt);

        return userPrincipal;
    }

    /**
     * @description 查询用户信息
     * @param xcUser  用户id，主键
     * @return com.leguan.ucenter.model.po.XcUser 用户信息
     */
    public UserDetails getUserPrincipal(XcUserExt xcUser) {
        String password = xcUser.getPassword();

        //权限
        String[] authorities = {"test"};
        //根据用户id查询用户的权限
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(xcUser.getId());
        if (xcMenus.size() > 0) {
            ArrayList<String> permissions = new ArrayList<>();
            xcMenus.forEach(m -> {
                //拿到了用户拥有的权限标识符
                permissions.add(m.getCode());
            });
            //将permission转成数组
            authorities = permissions.toArray(new String[0]);
        }

        xcUser.setPassword(null);
        //将用户信息转为json
        String userJson = JSON.toJSONString(xcUser);

        UserDetails userDetails = User.withUsername(userJson).password(password).authorities(authorities).build();

        return userDetails;

    }
}
