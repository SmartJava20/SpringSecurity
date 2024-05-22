package com.smartjava.demo.model;

import org.hibernate.annotations.ManyToAny;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="token")
public class Token {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String accessToken;
	private String refreshToken;
	
	@ManyToOne
	@JoinColumn(name="userTokenId")
	private User userTokenId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public User getUserTokenId() {
		return userTokenId;
	}

	public void setUserTokenId(User userTokenId) {
		this.userTokenId = userTokenId;
	}

	@Override
	public String toString() {
		return "Token [id=" + id + ", accessToken=" + accessToken + ", refreshToken=" + refreshToken + ", userTokenId="
				+ userTokenId + "]";
	}
	
	
}
