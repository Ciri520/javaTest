package test;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

import cn.e3mall.utils.FastDFSClient;

public class FastDFSTest {
	@Test
	public void testFileUpload() throws Exception{
		// 1_加载配置文件,文件中的内容就是tracker服务器的地址
		ClientGlobal.init("F:/268/e3mall/e3-manager-web/src/test/resources/fdfs_client.conf");
		// 2_创建一个trackerClient对象,直接new一个
		TrackerClient trackerClient = new TrackerClient ();
		// 3_使用tarclerClient对象创建链接,获取trackerServer对象
		TrackerServer trackerServer = trackerClient.getConnection();
		// 4_创建一个StorageServer的引用,值为null
		StorageServer storageServer = null;
		// 5_创建一个storageClient对象,需要两个参数trackerServer和StorageServer的引用
		StorageClient storageClient = new StorageClient(trackerServer, storageServer);
		// 6_使用StorageClient对象上传图片
		// 扩展名不带
		String[] file = storageClient.upload_file("E:/图片/壁纸/128096-106.jpg","jpg",null);
		// 7_返回数组包含组名和图片的路径
		for (String string : file) {
			System.out.println(string);
		}
	}
	
	@Test
	public void testFastDfsClient()throws Exception{
		FastDFSClient fastDFSClient = new FastDFSClient("F:/268/e3mall/e3-manager-web/src/test/resources/fdfs_client.conf");
		String file = fastDFSClient.uploadFile("E:/图片/壁纸/t0111920b5249916290.jpg");
		System.out.println(file);
	}
}
