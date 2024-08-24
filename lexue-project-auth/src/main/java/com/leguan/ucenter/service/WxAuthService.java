package com.leguan.ucenter.service;


import com.leguan.ucenter.model.po.XcUser;

public interface WxAuthService {

    /**
     * 微信扫码认证，申请令牌，携带令牌查询用户信息，保存用户信息到数据库
     */
    public XcUser wxAuth(String code);
}
