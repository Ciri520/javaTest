package cn.e3mall.order.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.order.pojo.OrderInfo;
import cn.e3mall.order.service.OrderService;
import cn.e3mall.pojo.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;

@Controller
public class OrderCartController {
	
	@Autowired
	private CartService cartService;
	@Autowired
	private OrderService orderService;

	@RequestMapping("/order/order-cart")
	public String showOrderCart(HttpServletRequest request){
		// 获取用户信息
		TbUser user = (TbUser) request.getAttribute("user");
		// 获取购物车列表
		Long itemId = user.getId();
		List<TbItem> cartList = cartService.getCartList(itemId);
		// 把商品列表传递给jsp
		request.setAttribute("cartList",cartList );
		return "order-cart";
	}
	
	@RequestMapping("/order/create")
	public String createOrder(OrderInfo orderInfo,Model model){
		E3Result result = orderService.createOrder(orderInfo);
		model.addAttribute("orderId", result.getData());
		model.addAttribute("payment", orderInfo.getPayment());
		return "success";
	}
}
