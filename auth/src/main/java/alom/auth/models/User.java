package alom.auth.models;

public class User {
	
	private String nickname;
	private String lastname;
	private String firstname;
	private String pwd;
	private String loginToken;
	
	
	public User() {}

	public User(String nickname, String lastname, String firstname, String pwd) {
		super();
		this.nickname = nickname;
		this.lastname = lastname;
		this.firstname = firstname;
		this.pwd = pwd;
	}



	public String getNickname() {
		return nickname;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getPwd() {
		return pwd;
	}
	
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	
	public String getLoginToken() {
		return loginToken;
	}

	public void setLoginToken(String loginToken) {
		this.loginToken = loginToken;
	}

	public void clearLoginToken() {
        this.loginToken = null;
    }

}
