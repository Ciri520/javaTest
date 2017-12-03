package cn.e3mall.freemarker;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreeMarkerTest {

	@Test
	public void genFile() throws Exception {
		// 1:创建一个Configuration对象,直接new一个对象.构造方法的参数就是freemarker对应的版本号
		Configuration configuration = new Configuration(Configuration.getVersion());
		// 2:设置模板文件路径
		configuration.setDirectoryForTemplateLoading(new File("F:/268/e3mall/e3-item-web/src/main/webapp/WEB-INF/ftl"));
		// 3:设置模板文件所使用的字符集
		configuration.setDefaultEncoding("utf-8");
		// 4:加载一个模板创建一个模板,加载指定文件
		Template template = configuration.getTemplate("hello.ftl");
		// 5:创建一个模板所使用的数据集,可以是pojo也可以是map,一般是map
		Map dataModel = new HashMap<>();
		// 6:向数据集添加数据
		dataModel.put("hello", "this is my first freemarker test.");
		// 7:创建一个writer对象,一般创建一个filewriter对象,指定生成的文件名
		FileWriter out = new FileWriter(new File("D:/temp/out/hello.html"));
		// 8:调用模板对象的process方法输出文件
		template.process(dataModel, out);
		// 9:关闭流
		out.close();
	}

	@Test
	public void testFreemarker() throws Exception {
		// 1:添加freemarker的jar包
		// 2:创建一个模板文件,也可以是本地文件
		// 3:创建一个configuration对象
		Configuration configuration = new Configuration(Configuration.getVersion());
		// 4:设置模板文件所在的目录
		configuration.setDirectoryForTemplateLoading(new File("F:/268/e3mall/e3-item-web/src/main/webapp/WEB-INF/ftl"));
		// 5:设置模板文件的编码格式
		configuration.setDefaultEncoding("utf-8");
		// 6:加载模板文件,指定文件名加载
		Template template = configuration.getTemplate("student.ftl");
		// 7:创建一个数据集,可以是map 也可以是pojo
		Map dataModel = new HashMap<>();
		// 8:向数据集中添加数据
		dataModel.put("hello", "Hello Freemarker");
		// 添加一个student对象
		dataModel.put("student", new Student(1, "张三", 18, "北京市"));
		List<Student> stuList = new ArrayList<>();
		stuList.add(new Student(1, "张三1", 18, "顺义马坡南"));
		stuList.add(new Student(2, "张三2", 18, "顺义马坡南"));
		stuList.add(new Student(3, "张三3", 18, "顺义马坡南"));
		stuList.add(new Student(4, "张三4", 18, "顺义马坡南"));
		stuList.add(new Student(5, "张三5", 18, "顺义马坡南"));
		stuList.add(new Student(6, "张三6", 18, "顺义马坡南"));
		stuList.add(new Student(7, "张三7", 18, "顺义马坡南"));
		stuList.add(new Student(8, "张三8", 18, "顺义马坡南"));
		dataModel.put("stuList", stuList);
		// 添加一个日期类型
		dataModel.put("date", new Date());
		// null
		dataModel.put("myval", "haha");
		// 9:创建一个writer对象,指定输出的文件路径和文件名
		FileWriter out = new FileWriter(new File("D:/temp/out/student.html"));
		// 10:使用模板的process方法生成文件
		template.process(dataModel, out);
		// 11:关闭流
		out.close();
	}
}
