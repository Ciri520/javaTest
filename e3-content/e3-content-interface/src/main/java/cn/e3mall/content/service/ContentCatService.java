package cn.e3mall.content.service;

import java.util.List;

import cn.e3mall.pojo.E3Result;
import cn.e3mall.pojo.EasyUITreeNode;
import cn.e3mall.pojo.TbContent;

public interface ContentCatService {
	List<EasyUITreeNode> getContentCatList(long parentId);
	E3Result addContentCategory(long parentId, String name);
	E3Result updataeContentCategory(Long id, String name);
	E3Result deleteContentCategory(Long id);

}
