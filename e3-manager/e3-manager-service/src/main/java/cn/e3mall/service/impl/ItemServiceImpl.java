package cn.e3mall.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.fabric.xmlrpc.base.Data;

import cn.e3mall.mapper.TbItemDescMapper;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.DataGridResult;
import cn.e3mall.pojo.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.service.ItemService;
import cn.e3mall.utils.IDUtils;

@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private TbItemDescMapper itemDescMapper;

	@Override
	public TbItem getItemById(long itemId) {
		return itemMapper.selectByPrimaryKey(itemId);
	}

	@Override
	public DataGridResult getItemListPage(int page, int rows) {
		// 1)设置分页信息
		PageHelper.startPage(page, rows);
		// 2)执行查询
		TbItemExample example = new TbItemExample();
		List<TbItem> list = itemMapper.selectByExample(example);
		// 3)取查询结果
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		long total = pageInfo.getTotal();
		// 4)把查询结果封装到DataGridResult
		DataGridResult result = new DataGridResult();
		result.setRows(list);
		result.setTotal(total);
		// 5)返回结果
		return result;
	}

	@Override
	public E3Result addItem(TbItem item, String desc) {
		// 1、生成商品id
		long itemId = IDUtils.genItemId();
		// 2、补全TbItem对象的属性
		item.setId(itemId);
		// 商品状态，1-正常，2-下架，3-删除
		item.setStatus((byte) 1);
		Date date = new Date();
		item.setUpdated(date);
		item.setCreated(date);
		// 3、向商品表插入数据
		itemMapper.insert(item);
		// 4、创建一个TbItemDesc对象
		TbItemDesc tbItemDesc = new TbItemDesc();
		// 5、补全TbItemDesc的属性
		tbItemDesc.setCreated(date);
		tbItemDesc.setItemDesc(desc);
		tbItemDesc.setUpdated(date);
		tbItemDesc.setItemId(itemId);
		// 6、向商品描述表插入数据
		itemDescMapper.insert(tbItemDesc);
		// 7、E3Result.ok()
		return E3Result.ok();
	}

}
