<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>系统主界面</title>
	
	<%
		if(session.getAttribute("currentUser")==null){
			response.sendRedirect("login.jsp");
			return;
		}
	 %>
	
	<link rel="stylesheet" type="text/css" href="jquery-easyui-1.5.2/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="jquery-easyui-1.5.2/themes/icon.css">
	<link rel="stylesheet" type="text/css" href="jquery-easyui-1.5.2/demo/demo.css">
	<script type="text/javascript" src="jquery-easyui-1.5.2/jquery.min.js"></script>
	<script type="text/javascript" src="jquery-easyui-1.5.2/jquery.easyui.min.js"></script>
	<script type="text/javascript">
		$(function(){
			$("#tree").tree({
				line:true,
				url:'auth?action=menu&parentId=-1',
				onLoadSuccess:function(){
					$("#tree").tree('expandAll');
				},
				onClick:function(node){
					if(node.id==16){
						logout();
					}else if(node.id==15){
						openPassWordModifyDialog();
					}else if(node.attributes.authPath){
						openTab(node);
					}
				}
			});
			
			function logout(){
				$.messager.confirm('系统提示','您确定要退出系统吗?',function(r){
					if(r){
						window.location.href='user?action=logout';
					}
				});
			}
			
			function openPassWordModifyDialog(){
				url="user?action=modifyPassWord";
				$("#dlg").dialog("open").dialog("setTitle","修改密码");
			}
			
			function openTab(node){
				if($("#tabs").tabs("exists",node.text)){
					$("#tabs").tabs("select",node.text);
					
				}else{
					var content="<iframe frameborder=0 scrolling='auto' style='width:100%;height:100%' src="+node.attributes.authPath+"></iframe>"
						$("#tabs").tabs("add",{
						title:node.text,
						iconCls:node.iconCls,
						closable:true,
						content:content
					});
				}
			}
		});
		
		
		function modifyPassWord(){
			$("#fm").form("submit",{
				url:url,
				onSubmit:function(){
					var oldPassWord=$("#oldPassWord").val();
					var newPassWord=$("#newPassWord").val();
					var newPassWord2=$("#newPassWord2").val();
					if(!$(this).form("validate")){
						return false;
					}
					if(oldPassWord!='${currentUser.passWord}'){
						$.messager.alert('系统提示','用户名密码输入错误！');
						return false;
					}
					if(newPassWord!=newPassWord2){
						$.messager.alert('系统提示','确认密码输入错误！');
						return false;
					}
					return true;
				},
				success:function(result){
					var result=eval('('+result+')');
					if(result.errorMsg){
						$.messager.alert('系统提示',result.errorMsg);
						return;
					}else{
						$.messager.alert('系统提示','密码修改成功，下一次登录生效！');
						closePassWordModifyDialog();
					}
				}
			});
		}
		
		function closePassWordModifyDialog(){
			$("#dlg").dialog("close");
			$("#oldPassWord").val("");
			$("#newPassWord").val("");
			$("#newPassWord2").val("");
		}
	</script>
</head>
<body class="easyui-layout">
	<div data-options="region:'north',border:false" style="height:78px; background:#B3DFDA;" >
		<div style="padding:0px;margin:0px;">
			<table>
				<tr>
					<td><img src="images/mainlogo.png"></td>
					<td valign="bottom">欢迎：${currentUser.userName }&nbsp;&nbsp;『${currentUser.roleName }』</td>
				</tr>
			</table>
		</div>
	</div>
	<div data-options="region:'south',border:false" style="height:30px; padding:5px; background:#A9FACD;padding:10px;" align="center">版权所有@fanye<a href="http://www.fqybz.cn" target="_blank">www.fqybz.cn</a></div>
	<div data-options="region:'west',split:true" title="导航菜单" style="width:160px;padding:5px;">
		<ul id="tree" class="easuui-tree"></ul>
	</div>
	<div data-options="region:'center'">
		<div class="easyui-tabs" fit="true" border="false" id="tabs">
			<div title="首页" data-options="iconCls:'icon-home'">
				<div align="center" style="padding-top:100px;"><font color="red" size="10">欢迎使用</font></div>
			</div>
		</div>
	</div>
<div id="dlg" class="easyui-dialog" style="width:400px; height:220px; padding:10px 20px" closed="true" buttons="#dlg-buttons" data-options="iconCls:'icon-modifyPassword'">
	<form id="fm" method="post">
		<table>
			<tr>
				<td>用户名：</td>
				<td><input type="hidden" name="userId" id="userId" value="${currentUser.userId }"><input type="text" name="userName" id="userName" readonly="readonly" value="${currentUser.userName }" style="width:200px;"></td>
			</tr>
			<tr>
				<td>原密码：</td>
				<td><input type="password" class="easyui-validatebox" name="oldPassWord" id="oldPassWord" style="width:200px;" required="true"></td>
			</tr>
			<tr>
				<td>新密码：</td>
				<td><input type="password" class="easyui-validatebox" name="newPassWord" id="newPassWord" style="width:200px;" required="true"></td>
			</tr>
			<tr>
				<td>确认密码：</td>
				<td><input type="password" class="easyui-validatebox" name="newPassWord2" id="newPassWord2" style="width:200px;" required="true"></td>
			</tr>
		</table>
	</form>
</div>
<div id="dlg-buttons">
	<a href="javascript:modifyPassWord()" class="easyui-linkbutton" iconCls="icon-ok">保存</a>
	<a href="javascript:closePassWordModifyDialog()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
</div>
</body>
</html>
