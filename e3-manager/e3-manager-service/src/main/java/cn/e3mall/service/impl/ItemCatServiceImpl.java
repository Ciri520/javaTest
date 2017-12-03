package cn.e3mall.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.mapper.TbItemCatMapper;
import cn.e3mall.pojo.EasyUITreeNode;
import cn.e3mall.pojo.TbItemCat;
import cn.e3mall.pojo.TbItemCatExample;
import cn.e3mall.pojo.TbItemCatExample.Criteria;
import cn.e3mall.service.ItemCatService;
@Service
public class ItemCatServiceImpl implements ItemCatService {
	
	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	@Override
	public List<EasyUITreeNode> getCatList(long parentId) {
		//1.根据parentId查询子节点
		TbItemCatExample example = new TbItemCatExample();
		//2.设置查询条件
		 Criteria criteria = example.createCriteria();
		 criteria.andParentIdEqualTo(parentId);
		 List<TbItemCat> list = itemCatMapper.selectByExample(example);
		 //3.转换成EasyUITreeNode列表
		 ArrayList<EasyUITreeNode> resultList  = new ArrayList<>();
		 for (TbItemCat tbItemCat : list) {
			EasyUITreeNode easyUITreeNode = new EasyUITreeNode();
			easyUITreeNode.setId(tbItemCat.getId());
			easyUITreeNode.setState(tbItemCat.getIsParent()?"closed":"open");
			easyUITreeNode.setText(tbItemCat.getName());
			//4.添加到列表
			resultList .add(easyUITreeNode);
		}
		//5.返回
		return resultList ;
	}

}
