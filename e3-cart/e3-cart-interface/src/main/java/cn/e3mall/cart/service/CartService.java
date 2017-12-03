package cn.e3mall.cart.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.e3mall.pojo.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;

public interface CartService {
	E3Result addCart(long userId, long itemId, int num);

	E3Result mergeCart(long userId, List<TbItem> itemList);

	List<TbItem> getCartList(long userId);
	
	E3Result updateCartItemNum(long userId, long itemId, int num);

	E3Result deleteCartItem(long userId, long itemId);
	
	// request, response 只能运用在表现层  不能传递到后台
	/*List<TbItem> getCartList(TbUser user, HttpServletRequest request,HttpServletResponse response);*/

}
