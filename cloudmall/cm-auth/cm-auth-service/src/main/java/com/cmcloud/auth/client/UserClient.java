package com.cmcloud.auth.client;

import com.cmcloud.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @description:
 * @author: xiaoliyu
 * @date: 2019-04-17 10:26
 */
@FeignClient("user-service")
public interface UserClient extends UserApi {

}
