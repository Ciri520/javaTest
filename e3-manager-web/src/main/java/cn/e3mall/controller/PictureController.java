package cn.e3mall.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.e3mall.utils.FastDFSClient;
import cn.e3mall.utils.JsonUtils;

@Controller
@SuppressWarnings("all")
public class PictureController {

	@Value("${image_server_url}")
	private String image_server_url;

//	@RequestMapping(value="/pic/upload",produces="text/plain;charset=UTF-8")
	@RequestMapping(value="/pic/upload",produces=MediaType.TEXT_PLAIN_VALUE+" ;charset=utf-8")
	public	@ResponseBody String fileUpload(MultipartFile uploadFile) {
		try {
			// 1_获取文件扩展名
			String originalFilename = uploadFile.getOriginalFilename();
			String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
			// 2_创建一个FastDFS客户端
			FastDFSClient fastDFSClient = new FastDFSClient("F:/268/e3mall/e3-manager-web/src/main/resources/conf/fdfs_client.conf");
			// 3_执行上传处理
			String path = fastDFSClient.uploadFile(uploadFile.getBytes(),extName);
			// 4_返回拼接的url和地址,拼装成完整的url
			String url = image_server_url+path;
			// 5_返回Map
			Map result = new HashMap<>();
			result.put("error", 0);
			result.put("url", url);
			return JsonUtils.objectToJson(result);
		} catch (Exception e) {
			e.printStackTrace();
			// 5_返回Map
			Map result = new HashMap<>();
			result.put("error", 1);
			result.put("message", "图片上传失败");
			return JsonUtils.objectToJson(result);
		}
	}
}
