package com.fanye.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fanye.dao.AuthDao;
import com.fanye.dao.RoleDao;
import com.fanye.model.Auth;
import com.fanye.model.User;
import com.fanye.util.DbUtil;
import com.fanye.util.ResponseUtil;
import com.fanye.util.StringUtil;

public class AuthServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DbUtil dbUtil=new DbUtil();
	AuthDao authDao=new AuthDao();
	RoleDao roleDao=new RoleDao();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String action=request.getParameter("action");
		if("menu".equals(action)){
			this.menuAction(request, response);
		}else if("authMenu".equals(action)){
			this.authMenuAction(request, response);
		}else if("authTreeGridMenu".equals(action)){
			this.authTreeGridMenuAction(request, response);
		}else if("save".equals(action)){
			this.saveAction(request, response);
		}else if("delete".equals(action)){
			this.deleteAction(request, response);
		}
		
	}
	
	private void menuAction(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		String parentId=request.getParameter("parentId");
		Connection con=null;
		
		try{
			con=dbUtil.getCon();
			HttpSession session=request.getSession();
			User currentUser=(User) session.getAttribute("currentUser");
			String authIds=roleDao.getAuthIdsById(con, currentUser.getRoleId());
			JSONArray jsonArray=authDao.getAuthsByParentId(con, parentId,authIds);
			ResponseUtil.write(response, jsonArray);
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
	
	
	private void authMenuAction(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		String parentId=request.getParameter("parentId");
		String roleId=request.getParameter("roleId");
		Connection con=null;
		
		try{
			con=dbUtil.getCon();
			String authIds=roleDao.getAuthIdsById(con, Integer.parseInt(roleId));
			JSONArray jsonArray=authDao.getCheckedAuthsByParentId(con, parentId,authIds);
			ResponseUtil.write(response, jsonArray);
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
	
	
	private void authTreeGridMenuAction(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		String parentId=request.getParameter("parentId");
		Connection con=null;
		
		try{
			con=dbUtil.getCon();
			JSONArray jsonArray=authDao.getListByParentId(con, parentId);
			ResponseUtil.write(response, jsonArray);
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
	
	private void saveAction(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String authId=request.getParameter("authId");
		String authName=request.getParameter("authName");
		String authPath=request.getParameter("authPath");
		String authDescription=request.getParameter("authDescription");
		String parentId=request.getParameter("parentId");
		String iconCls=request.getParameter("iconCls");
		
		Auth auth=new Auth(authName,authPath,authDescription,iconCls);
		if(StringUtil.isNotEmpty(authId)){
			auth.setAuthId(Integer.parseInt(authId));
		}else{
			auth.setParentId(Integer.parseInt(parentId));
		}
		Connection con=null;
		boolean isLeaf=false;
		try{
			JSONObject result=new JSONObject();
			con=dbUtil.getCon();
			int saveNums=0;
			if(StringUtil.isNotEmpty(authId)){
				saveNums=authDao.authUpdate(con, auth);
			}else{
				//添加操作
				isLeaf=authDao.isLeaf(con, parentId);
				if(isLeaf){
					con.setAutoCommit(false);
					authDao.updateStateByAuthId(con, "closed", parentId);
					saveNums=authDao.authAdd(con, auth);
					con.commit();
				}else{
					saveNums=authDao.authAdd(con, auth);
				}
			}
			if(saveNums>0){
				result.put("success", "true");
			}else{
				result.put("errorMsg", "保存失败");
			}
			ResponseUtil.write(response, result);
		}catch(Exception e){
			if(isLeaf){
				try {
					con.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}finally{
			try {
				dbUtil.closeCon(con);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void deleteAction(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String authId=request.getParameter("authId");
		String parentId=request.getParameter("parentId");
		
		Connection con=null;
		int sonNum=-1;
		try{
			con=dbUtil.getCon();
			JSONObject result=new JSONObject();
			if(!authDao.isLeaf(con, authId)){
				result.put("errorMsg", "该菜单节点有子节点，不能删除！");
			}else{
				int delNum=0;
				sonNum=authDao.getAuthCountByParentId(con, parentId);
				if(sonNum==1){
					con.setAutoCommit(false);
					authDao.updateStateByAuthId(con, "open", parentId);
					delNum=authDao.authDelete(con, authId);
					con.commit();
				}else{
					delNum=authDao.authDelete(con, authId);
				}
				if(delNum>0){
					result.put("success", "true");
					result.put("delNums", delNum);
				}else{
					result.put("errorMsg", "删除失败！");
				}
			}
			
			ResponseUtil.write(response, result);
		}catch(Exception e){
			if(sonNum==1){
				try {
					con.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
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
