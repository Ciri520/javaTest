package cn.e3mall.sso.service;

import cn.e3mall.pojo.E3Result;
import cn.e3mall.pojo.TbUser;

public interface UserService {
	E3Result checkData(String param, int type);
	E3Result createUser(TbUser user);
	E3Result login(String username, String password);
	E3Result getUserByToken(String Token);
	E3Result logout(String token);

}
