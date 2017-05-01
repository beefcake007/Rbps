package com.fanye.web;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fanye.dao.RoleDao;
import com.fanye.dao.UserDao;
import com.fanye.model.PageBean;
import com.fanye.model.User;
import com.fanye.util.DbUtil;
import com.fanye.util.JsonUtil;
import com.fanye.util.ResponseUtil;
import com.fanye.util.StringUtil;

public class UserServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DbUtil dbUtil=new DbUtil();
	UserDao userDao=new UserDao();
	RoleDao roleDao=new RoleDao();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String action=request.getParameter("action");
		if("login".equals(action)){
			this.login(request, response);
		}else if("logout".equals(action)){
			this.logout(request, response);
		}else if("modifyPassWord".equals(action)){
			this.modifyPassWord(request, response);
		}else if("list".equals(action)){
			this.list(request, response);
		}else if("save".equals(action)){
			this.save(request, response);
		}else if("delete".equals(action)){
			this.delete(request, response);
		}
		
	}

	private void login(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session=request.getSession();
		String userName=request.getParameter("userName");
		String passWord=request.getParameter("passWord");
		String imageCode=request.getParameter("imageCode");
		request.setAttribute("userName", userName);
		request.setAttribute("passWord", passWord);
		request.setAttribute("imageCode", imageCode);
		
		if(StringUtil.isEmpty(userName)||StringUtil.isEmpty(passWord)){
			request.setAttribute("error", "用户名或密码不能为空!");
			request.getRequestDispatcher("login.jsp").forward(request, response);
			return;
		}
		if(StringUtil.isEmpty(imageCode)){
			request.setAttribute("error", "验证码为空！");
			request.getRequestDispatcher("login.jsp").forward(request, response);
			return;
		}
		if(!imageCode.equals(session.getAttribute("sRand"))){
			request.setAttribute("error", "验证码错误！");
			request.getRequestDispatcher("login.jsp").forward(request, response);
		}
		
		User user=new User(userName,passWord);
		Connection con=null;
		
		try{
			con=dbUtil.getCon();
			User currentUser=userDao.login(con, user);
			if(currentUser==null){
				request.setAttribute("error", "用户名或密码错误！");
				request.getRequestDispatcher("login.jsp").forward(request, response);
			}else{
				String roleName=roleDao.getRoleNameById(con, currentUser.getRoleId());
				currentUser.setRoleName(roleName);
				session.setAttribute("currentUser", currentUser);
				response.sendRedirect("mainTemp.jsp");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				dbUtil.closeCon(con);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void logout(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getSession().invalidate();
		response.sendRedirect("login.jsp");
	}

	
	private void modifyPassWord(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String userId=request.getParameter("userId");
		String newPassWord=request.getParameter("newPassWord");
		User user=new User();
		user.setUserId(Integer.parseInt(userId));
		user.setPassWord(newPassWord);
		
		Connection con=null;
		
		try{
			JSONObject result=new JSONObject();
			con=dbUtil.getCon();
			int updateNum=userDao.modifyPassWord(con, user);
			if(updateNum>0){
				result.put("success", "true");
			}else{
				result.put("success", "true");
				result.put("errorMsg", "修改密码失败！");
			}
			ResponseUtil.write(response, result);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				dbUtil.closeCon(con);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void list(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String page=request.getParameter("page");
		String rows=request.getParameter("rows");
		User user=new User();
		String s_userName=request.getParameter("s_userName");
		String s_roleId=request.getParameter("s_roleId");
		if(StringUtil.isNotEmpty(s_userName)){
			user.setUserName(s_userName);
		}
		if(StringUtil.isNotEmpty(s_roleId)){
			user.setRoleId(Integer.parseInt(s_roleId));
		}
		PageBean pageBean=new PageBean(Integer.parseInt(page),Integer.parseInt(rows));
		
		Connection con=null;
		
		try{
			con=dbUtil.getCon();
			JSONObject result=new JSONObject();
			JSONArray jsonArray=JsonUtil.formatRsToJsonArray(userDao.userList(con, pageBean,user));
			int total=userDao.userCount(con,user);
			result.put("rows", jsonArray);
			result.put("total", total);
			ResponseUtil.write(response, result);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				dbUtil.closeCon(con);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void save(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String userName=request.getParameter("userName");
		String passWord=request.getParameter("passWord");
		String roleId=request.getParameter("roleId");
		String userDescription=request.getParameter("userDescription");
		String userId=request.getParameter("userId");
		
		User user=new User(userName,passWord,Integer.parseInt(roleId),userDescription);
		
		if(StringUtil.isNotEmpty(userId)){
			user.setUserId(Integer.parseInt(userId));
		}
		Connection con=null;
		
		try{
			JSONObject result=new JSONObject();
			con=dbUtil.getCon();
			int saveNums=0;
			if(StringUtil.isNotEmpty(userId)){
				saveNums=userDao.userUpdate(con, user);
			}else{
				if(userDao.existUserWithUserName(con, userName)){
					saveNums=-1;
				}else{
					saveNums=userDao.userAdd(con, user);
				}
				
			}
			if(saveNums==-1){
				result.put("errorMsg", "此用户名已经存在");
			}else if(saveNums==0){
				result.put("errorMsg", "保存失败！");
			}else{
				result.put("success", "true");
			}
			ResponseUtil.write(response, result);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				dbUtil.closeCon(con);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	private void delete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String delIds=request.getParameter("delIds");
		
		Connection con=null;
		
		try{
			con=dbUtil.getCon();
			JSONObject result=new JSONObject();
			int delNums=userDao.userDelete(con, delIds);
			if(delNums>0){
				result.put("success", "true");
				result.put("delNums", delNums);
			}else{
				result.put("errorMsg", "删除失败！");
			}
			ResponseUtil.write(response, result);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				dbUtil.closeCon(con);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
