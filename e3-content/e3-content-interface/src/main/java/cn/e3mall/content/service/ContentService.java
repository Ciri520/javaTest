package cn.e3mall.content.service;

import java.util.List;

import cn.e3mall.pojo.DataGridResult;
import cn.e3mall.pojo.E3Result;
import cn.e3mall.pojo.TbContent;

public interface ContentService {

	DataGridResult getContentListGrid(int page, int rows, long categoryId);

	E3Result addContent(TbContent content);
	
	List<TbContent> getContentList(long cid);

	E3Result editContent(TbContent content);
}
