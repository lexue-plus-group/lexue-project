package com.leguan.ucenter.service;

import com.leguan.ucenter.model.dto.AuthParamsDto;
import com.leguan.ucenter.model.dto.XcUserExt;

/**
 * @desciption 统一的认证接口
 */
public interface AuthService {

    /**
     * 认证方法
     * @param authParamsDto 认证参数
     * @return
     */
    XcUserExt execute(AuthParamsDto authParamsDto);
}
