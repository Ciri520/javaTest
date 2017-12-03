package cn.e3mall.content.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.mapper.TbContentCategoryMapper;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.E3Result;
import cn.e3mall.pojo.EasyUITreeNode;
import cn.e3mall.pojo.TbContentCategory;
import cn.e3mall.pojo.TbContentCategoryExample;
import cn.e3mall.pojo.TbContentCategoryExample.Criteria;

@Service
public class ContentCatServiceImpl implements ContentCatService {

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;

	@Override
	public List<EasyUITreeNode> getContentCatList(long parentId) {
		// 1_根据parentId查询节点列表
		// 设置查询条件
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		// 执行查询
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		// 2_把节点列表转换成EasyUITreeNode节点列表
		List<EasyUITreeNode> treeNodes = new ArrayList<>();
		for (TbContentCategory tbContentCategory : list) {
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(tbContentCategory.getId());
			node.setText(tbContentCategory.getName());
			node.setState(tbContentCategory.getIsParent() ? "closed" : "open");
			treeNodes.add(node);
		}
		// 3_返回结果
		return treeNodes;
	}

	@Override
	public E3Result addContentCategory(long parentId, String name) {
		// 1）创建一个tb_content_category表对应的pojo对象
		TbContentCategory contentCategory = new TbContentCategory();
		// 2）补全pojo对象的属性
		contentCategory.setName(name);
		contentCategory.setParentId(parentId);
		// 1(正常),2(删除),新添加的节点一定是正常状态。
		contentCategory.setStatus(1);
		// 排序方式默认是1
		contentCategory.setSortOrder(1);
		// 新添加的节点一定是叶子节点，isparent的值应该是false
		contentCategory.setIsParent(false);
		contentCategory.setCreated(new Date());
		contentCategory.setUpdated(new Date());
		// 3）将数据插入到数据库
		int insert = contentCategoryMapper.insert(contentCategory);
		// 4）根据parentid查询父节点信息
		TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
		// 5）判断父节点的isparent是否为true，如果不是true应该改为true，把结果更新到数据库。
		if (!parent.getIsParent()) {
			parent.setIsParent(true);
			// 更新到数据库中
			contentCategoryMapper.updateByPrimaryKey(parent);
		}
		// 6）把pojo对象放到E3Result中返回。
		return E3Result.ok(contentCategory);
	}

	@Override
	public E3Result updataeContentCategory(Long id, String name) {
		// 1_根据id查询节点信息
		TbContentCategory contentCategory = contentCategoryMapper.selectByPrimaryKey(id);
		// 2_更新name
		contentCategory.setName(name);
		// 3_执行更新
		contentCategoryMapper.updateByPrimaryKey(contentCategory);
		return null;
	}

	@Override
	public E3Result deleteContentCategory(Long id) {
		// 1_根据id查询节点信息
		TbContentCategory contentCategory = contentCategoryMapper.selectByPrimaryKey(id);
		if(contentCategory.getIsParent()){
			return E3Result.build(500, "该节点不能删除!");
		}
		contentCategoryMapper.deleteByPrimaryKey(id);
		Long parentId = contentCategory.getParentId();
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		if(list.size()==0){
			TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
			parent.setIsParent(false);
			// 更新到数据库中
			contentCategoryMapper.updateByPrimaryKey(parent);
		}
		return E3Result.ok();
	}

}
