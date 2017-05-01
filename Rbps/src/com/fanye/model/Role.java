package com.fanye.model;

public class Role {

	private int roleId;
	private String roleName;
	private String authIds;
	private String authStrs;
	private String roleDescription;
	
	
	
	public Role() {
		super();
	}
	
	
	public Role(String roleName, String roleDescription) {
		super();
		this.roleName = roleName;
		this.roleDescription = roleDescription;
	}

	

	public Role(int roleId, String authIds) {
		super();
		this.roleId = roleId;
		this.authIds = authIds;
	}


	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getAuthIds() {
		return authIds;
	}
	public void setAuthIds(String authIds) {
		this.authIds = authIds;
	}
	public String getAuthStrs() {
		return authStrs;
	}
	public void setAuthStrs(String authStrs) {
		this.authStrs = authStrs;
	}
	public String getRoleDescription() {
		return roleDescription;
	}
	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}
	
	
}
