package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	public static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

	@Autowired
	private WeChatProperties weChatProperties;
	@Autowired
	private UserMapper userMapper;


	@Override
	public User login(UserLoginDTO userLoginDTO) {
		// 调用微信接口获取用户的openid
		Map<String, String> map = new HashMap<>();
		map.put("appid", weChatProperties.getAppid());
		map.put("secret", weChatProperties.getSecret());
		map.put("js_code", userLoginDTO.getCode());
		map.put("grant_type", "authorization_code");

		String json = HttpClientUtil.doGet(WX_LOGIN_URL, map);
		log.info("微信接口返回的json数据: {}", json);

		JSONObject jsonObject = JSON.parseObject(json);
		String openid = jsonObject.getString("openid");

		// 判断openid是否为空, 空则登录失败
		if (openid == null) {
			throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
		}

		// 判断数据库中是否存在该用户, 存在则返回用户信息, 不存在则注册即保存用户信息并返回
		User user = userMapper.getByOpenid(openid);
		if (user == null) {
			user = User.builder()
					.openid(openid)
					.createTime(LocalDateTime.now())
					.build();

			userMapper.insert(user);
		}

		return user;
	}

}
