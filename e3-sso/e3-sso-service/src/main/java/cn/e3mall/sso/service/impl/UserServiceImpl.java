package cn.e3mall.sso.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.e3mall.jedis.JedisClient;
import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.E3Result;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import cn.e3mall.pojo.TbUserExample.Criteria;
import cn.e3mall.sso.service.UserService;
import cn.e3mall.utils.JsonUtils;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;

	@Autowired
	private JedisClient jedisClient;

	@Value("${user.session.expire}")
	private Integer sessionExpire;

	@Override
	public E3Result checkData(String param, int type) {
		// 1、从tb_user表中查询数据
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		// 2、查询条件根据参数动态生成。
		// 1、2、3分别代表username、phone、email
		if (type == 1) {
			criteria.andUsernameEqualTo(param);
		} else if (type == 2) {
			criteria.andPhoneEqualTo(param);
		} else if (type == 3) {
			criteria.andEmailEqualTo(param);
		} else {
			return E3Result.build(400, "类型错误!!!");
		}
		// 执行查询
		List<TbUser> list = userMapper.selectByExample(example);
		if (list == null || list.size() == 0) {
			return E3Result.ok(true);
		}
		return E3Result.ok(false);
	}

	@Override
	public E3Result createUser(TbUser user) {
		if (StringUtils.isBlank(user.getUsername())) {
			return E3Result.build(400, "姓名不能为空!");
		}
		if (StringUtils.isBlank(user.getPassword())) {
			return E3Result.build(400, "密码不能为空!");
		}
		// 校验数据是否可用
		E3Result result = checkData(user.getUsername(), 1);

		if (!(boolean) result.getData()) {
			return E3Result.build(400, "该用户名已经存在!");
		}
		// 校验电话
		if (StringUtils.isNoneBlank(user.getPhone())) {
			E3Result result2 = checkData(user.getPhone(), 2);
			if (!(boolean) result2.getData()) {
				return E3Result.build(400, "该手机号已经存在");
			}
		}
		// 校验邮箱
		if (StringUtils.isNoneBlank(user.getEmail())) {
			E3Result result3 = checkData(user.getEmail(), 3);
			if (!(boolean) result3.getData()) {
				return E3Result.build(400, "该邮箱已经存在");
			}
		}
		// 校验通过,把数据插入数据库
		// 补全属性
		user.setCreated(new Date());
		user.setUpdated(new Date());
		// 对密码进行MD5加密
		String md5DigestAsHex = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		user.setPassword(md5DigestAsHex);
		// 把用户信息插入到数据库中。
		userMapper.insert(user);
		return E3Result.ok();
	}

	@Override
	public E3Result login(String username, String password) {
		// 1、判断用户名密码是否正确。
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		// 查询用户信息
		List<TbUser> list = userMapper.selectByExample(example);
		if (list == null || list.size() == 0) {
			return E3Result.build(400, "用户名或密码错误");
		}
		TbUser user = list.get(0);
		// 校验密码
		if (!user.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))) {
			return E3Result.build(400, "用户名或密码错误");
		}
		// 2、登录成功后生成token。Token相当于原来的jsessionid，字符串，可以使用uuid。
		String token = UUID.randomUUID().toString();
		// 3、把用户信息保存到redis。Key就是token，value就是TbUser对象转换成json。
		// 密码保护,不把密码保存到其他设备
		user.setPassword(null);
		// 4、使用String类型保存Session信息。可以使用“前缀:token”为key
		jedisClient.hset("user_session:" + token, "user", JsonUtils.objectToJson(user));
		// 5、设置key的过期时间。模拟Session的过期时间。一般半个小时。
		jedisClient.expire("user_session:" + token, sessionExpire);
		// 6、返回e3Result包装token。
		return E3Result.ok(token);
	}

	@Override
	public E3Result getUserByToken(String token) {
		// 从redis获取用户
		String json = jedisClient.hget("user_session:" + token, "user");
		if (StringUtils.isBlank(json)) {
			// 查询不到数据
			return E3Result.build(400, "用户信息已过期");
		}
		// 如果查询到数据，说明用户已经登录。
		// 重置redis中的用户信息过期时间
		jedisClient.expire("user_session:" + token, sessionExpire);
		// 把json数据转换成TbUser对象，然后使用e3Result包装并返回。
		TbUser user = JsonUtils.jsonToPojo(json, TbUser.class);
		return E3Result.ok(user);
	}

	@Override
	public E3Result logout(String token) {
		// 删除redis中的用户信息
		jedisClient.del("user_session:" + token);
		return E3Result.ok();
	}

}
