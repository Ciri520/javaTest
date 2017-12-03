package cn.e3mall.content.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.jedis.JedisClient;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.DataGridResult;
import cn.e3mall.pojo.E3Result;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentExample;
import cn.e3mall.pojo.TbContentExample.Criteria;
import cn.e3mall.utils.JsonUtils;

@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper tbContentMapper;
	
	@Autowired
	private JedisClient jedisClient;

	@Override
	public DataGridResult getContentListGrid(int page, int rows, long categoryId) {
		// 1_设置分页信息
		PageHelper.startPage(page, rows);
		// 2_创建查询条件
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		// 3_执行查询得到内容列表
		List<TbContent> list = tbContentMapper.selectByExample(example);
		// 4_获取分页结果得到total
		PageInfo<TbContent> pageInfo = new PageInfo<>(list);
		long total = pageInfo.getTotal();
		// 5_把结果封装到DataGridResult中
		DataGridResult result = new DataGridResult();
		result.setTotal(total);
		result.setRows(list);
		// 6_返回结果
		return result;
	}

	@Override
	public E3Result addContent(TbContent content) {
		// 1、补全TbContent对象的属性
		content.setCreated(new Date());
		content.setUpdated(new Date());
		// 2、把数据插入到数据库
		tbContentMapper.insert(content);
		//同步缓存
		jedisClient.hdel("CONTENT_INFO", content.getCategoryId().toString());
		// 3、返回成功
		return E3Result.ok();
	}

	@Override
	public List<TbContent> getContentList(long cid) {
		// 先从缓存查询数据
		try {
			String hget = jedisClient.hget("CONTENT_INFO", cid+"");
			if(StringUtils.isNotBlank(hget)){
				List<TbContent> list = JsonUtils.jsonToList(hget, TbContent.class);
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 根据分类id查询内容列表
		// 如果缓存中没有数据，查询数据库
		// 设置查询条件
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(cid);
		List<TbContent> list = tbContentMapper.selectByExample(example);
		// 把查询的结果放入redis
		try {
			jedisClient.hset("CONTENT_INFO", cid+"", JsonUtils.objectToJson(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public E3Result editContent(TbContent content) {
		// 1、补全TbContent对象的属性
		content.setUpdated(new Date());
		// 2、把数据更新到数据库
		tbContentMapper.updateByPrimaryKey(content);
		//同步缓存
		jedisClient.hdel("CONTENT_INFO", content.getCategoryId().toString());
		return E3Result.ok();
	}

}
