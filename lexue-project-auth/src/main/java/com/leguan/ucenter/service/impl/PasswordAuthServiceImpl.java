package com.leguan.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.leguan.ucenter.feignclient.CheckCodeClient;
import com.leguan.ucenter.mapper.XcUserMapper;
import com.leguan.ucenter.model.dto.AuthParamsDto;
import com.leguan.ucenter.model.dto.XcUserExt;
import com.leguan.ucenter.model.po.XcUser;
import com.leguan.ucenter.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @desciption 账号密码方式
 */
@Service("password_authservice")
public class PasswordAuthServiceImpl implements AuthService {

    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CheckCodeClient checkCodeClient;

    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {

        String username = authParamsDto.getUsername(); //账号

        //输入的验证码
        String checkCode = authParamsDto.getCheckcode();
        //验证码对应的key
        String checkCodeKey = authParamsDto.getCheckcodekey();

        if (StringUtils.isEmpty(checkCode) || StringUtils.isEmpty(checkCodeKey)) {
            throw new RuntimeException("请输入正确的验证码");
        }

        //远程调用验证码服务接口去校验验证码
        Boolean verify = checkCodeClient.verify(checkCodeKey, checkCode);
        if (verify == null || !verify) {
            throw new RuntimeException("验证码输入错误");
        }

        //根据username账号查询数据库
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));

        //查询到用户不存在，要返回null，spring security框架抛出异常用户不存在
        if (xcUser == null) {
            throw new RuntimeException("账号不存在");
        }

        //验证密码是否正确
        //如果查到了用户拿到正确的密码
        String passwordDb = xcUser.getPassword();
        //拿到用户输入的密码
        String passwordForm = authParamsDto.getPassword();
        //校验密码
        boolean matches = passwordEncoder.matches(passwordForm, passwordDb);
        if (!matches) {
            throw new RuntimeException("账号或密码错误");
        }

        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser, xcUserExt);

        return xcUserExt;
    }
}
