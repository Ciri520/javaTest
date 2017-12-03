<html>
	<head>
		<title>student</title>
	<head>
	<body>
		Hello:${hello}<br>
		<!-- 
		<input id="ye0" type="checkbox" />文本<br>
		<label for="ye"><input id="ye"type="checkbox"/>文本</label>
		-->
		学生信息:<br>
		学号：${student.id}<br>
		姓名：${student.name}<br>
		年龄：${student.age}<br>
		家庭住址：${student.address}<br>	
		<hr color="green">
		<table border="1">
				<tr>
					<th>序号</th>
					<th>学号</th>
					<th>姓名</th>
					<th>年龄</th>
					<th>家庭住址</th>
				</tr>
			<#list stuList as stu>
			<#if stu_index % 2==0>
				<tr bgcolor="red">
			<#else>
				<tr bgcolor="blue">
			</#if>
					<td>${stu_index}</td>
					<td>${stu.id}</td>
					<td>${stu.name}</td>
					<td>${stu.age}</td>
					<td>${stu.address}</td>
				</tr>
		</#list>	
		</table>
		<br>
		<hr color="yellow">
		时间:<br>
		当前日期: ${date?date}<br>
		当前时间: ${date?time}<br>
		当前日期和时间: ${date?datetime}<br>
		自定义时间格式: ${date?string("yyyy/MM/dd hh:mm:ss")}<br>
		<hr>
		null值的处理:${myval!"myval为null"}<br>
		null值的处理:<br>
		<#if myval2??>
			myval2不为空时...
		<#else>
			myval2为空时###
		</#if>
		<hr color="#00f">
		<#include "hello.ftl">
	</body>
</html>