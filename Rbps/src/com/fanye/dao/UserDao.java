package com.fanye.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.fanye.model.PageBean;
import com.fanye.model.User;
import com.fanye.util.StringUtil;

public class UserDao {

	public User login(Connection con,User user)throws Exception{
		User resultUser=null;
		String sql="select * from t_user where userName=? and passWord=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, user.getUserName());
		pstmt.setString(2, user.getPassWord());
		ResultSet re=pstmt.executeQuery();
		if(re.next()){
			resultUser=new User();
			resultUser.setUserId(re.getInt("userId"));
			resultUser.setUserName(re.getString("userName"));
			resultUser.setPassWord(re.getString("passWord"));
			resultUser.setRoleId(re.getInt("roleId"));
		}
		return resultUser;
	}
	
	
	public int modifyPassWord(Connection con,User user)throws Exception{
		
		String sql="update t_user set passWord=? where UserId=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, user.getPassWord());
		pstmt.setInt(2, user.getUserId());
		return pstmt.executeUpdate();
	}
	
	public ResultSet userList(Connection con,PageBean pageBean,User user)throws Exception{
		StringBuffer sb=new StringBuffer("select * from t_user u,t_role r where u.roleId=r.roleId and u.userType!=1 ");
		if(StringUtil.isNotEmpty(user.getUserName())){
			sb.append(" and u.userName like '%"+user.getUserName()+"%'");
		}
		if(user.getRoleId()!=-1){
			sb.append(" and u.roleId="+user.getRoleId());
		}
		if(pageBean!=null){
			sb.append(" limit "+pageBean.getStart()+","+pageBean.getRows());
		}
		PreparedStatement pstmt=con.prepareStatement(sb.toString());
		return pstmt.executeQuery();
	}
	
	public int userCount(Connection con,User user)throws Exception{
		
		StringBuffer sb=new StringBuffer("select count(*) as total from t_user u,t_role r where u.roleId=r.roleId and u.userType!=1");
		if(StringUtil.isNotEmpty(user.getUserName())){
			sb.append(" and u.userName like '%"+user.getUserName()+"%'");
		}
		if(user.getRoleId()!=-1){
			sb.append(" and u.roleId="+user.getRoleId());
		}
		PreparedStatement pstmt=con.prepareStatement(sb.toString());
		ResultSet re=pstmt.executeQuery();
		if(re.next()){
			return re.getInt("total");
		}else{
			return 0;
		}
	}
	
	public int userAdd(Connection con,User user)throws Exception{
		
		String sql="insert into t_user values(null,?,?,2,?,?)";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, user.getUserName());
		pstmt.setString(2, user.getPassWord());
		pstmt.setInt(3, user.getRoleId());
		pstmt.setString(4, user.getUserDescription());
		return pstmt.executeUpdate();
	}
	
	public int userUpdate(Connection con,User user)throws Exception{
		String sql="update t_user set userName=?,passWord=?,roleId=?,userDescription=? where userId=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, user.getUserName());
		pstmt.setString(2, user.getPassWord());
		pstmt.setInt(3, user.getRoleId());
		pstmt.setString(4, user.getUserDescription());
		pstmt.setInt(5, user.getUserId());
		return pstmt.executeUpdate();
	}
	
	public boolean existUserWithUserName(Connection con,String userName)throws Exception{
		String sql="select * from t_user where userName=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, userName);
		return pstmt.executeQuery().next();
	}
	
	public int userDelete(Connection con,String delIds)throws Exception{
		String sql="delete from t_user where userId in ("+delIds+")";
		PreparedStatement pstmt=con.prepareStatement(sql);
		return pstmt.executeUpdate();
	}
	
	public boolean existUserWithRoleId(Connection con,String roleId)throws Exception{
		String sql="select * from t_user where roleId=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, roleId);
		return pstmt.executeQuery().next();
	}
}
