package com.smartjava.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartjava.demo.filter.JwtFilter;
import com.smartjava.demo.model.Token;
import com.smartjava.demo.model.User;
import com.smartjava.demo.repository.TokenRepository;
import com.smartjava.demo.repository.UserRepository;
import com.smartjava.demo.response.MetadataResponse;
import com.smartjava.demo.response.ResultResponse;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TokenRepository tokenRepository;
	
	@Autowired
	private JwtFilter jwtFilter;
	
	@Autowired
	private AuthenticationService authenticationService;

	public ResultResponse login(String userName, String pass) {
		
		MetadataResponse metadataResponse=new MetadataResponse();
		ResultResponse response=new ResultResponse();
	
		User userData=userRepository.findByUserName(userName);
		Token token=new Token();
		if(userData.getPassword().equals(pass)) {
			
			String accessToken=jwtFilter.generateAccessToken(userData);
			String refreshToken=jwtFilter.generateRefreshToken(userData, token);
			
			token.setAccessToken(accessToken);
			token.setRefreshToken(refreshToken);
			token.setUserTokenId(userData);
			tokenRepository.save(token);
			metadataResponse.setCode("200");
			metadataResponse.setMessage("login sucessfully");
			metadataResponse.setNoOfRecords(1);
            response.setMetadataResponse(metadataResponse);
            response.setResult(tokenRepository.findById(token.getId()));
			return response;
		}else {
			metadataResponse.setCode("400");
			metadataResponse.setMessage("failed to login");
			metadataResponse.setNoOfRecords(0);
            response.setMetadataResponse(metadataResponse);
            response.setResult(null);
			return response;
		}
	}

	public ResultResponse refreshToken(Token token) {
		MetadataResponse metadataResponse=new MetadataResponse();
		ResultResponse response=new ResultResponse();
		if(jwtFilter.validateRefreshToken(token.getRefreshToken())) {
			User user=authenticationService.findByuserName(jwtFilter.getUserIdFromRefreshToken(token.getRefreshToken()));
			String accessToken=jwtFilter.generateAccessToken(user);
			String refreshToken=jwtFilter.generateRefreshToken(user, token);
			
			token.setAccessToken(accessToken);
			token.setRefreshToken(refreshToken);
			token.setUserTokenId(user);
			tokenRepository.save(token);
			metadataResponse.setCode("200");
			metadataResponse.setMessage("Refresh token generate sucessfully");
			metadataResponse.setNoOfRecords(1);
            response.setMetadataResponse(metadataResponse);
            response.setResult(tokenRepository.findById(token.getId()));
			return response;
		}else {
			metadataResponse.setCode("400");
			metadataResponse.setMessage("failed to generate refresh token");
			metadataResponse.setNoOfRecords(0);
            response.setMetadataResponse(metadataResponse);
            response.setResult(null);
			return response;
		}
	}

	public ResultResponse list() {
		MetadataResponse metadataResponse=new MetadataResponse();
		ResultResponse response=new ResultResponse();
		
		try {
			metadataResponse.setCode("200");
			metadataResponse.setMessage("get records from database");
			metadataResponse.setNoOfRecords(userRepository.findAll().size());
            response.setMetadataResponse(metadataResponse);
            response.setResult(userRepository.findAll());
			return response;
		} catch (Exception e) {
			metadataResponse.setCode("400");
			metadataResponse.setMessage("failred to fetch records from database");
			metadataResponse.setNoOfRecords(0);
            response.setMetadataResponse(metadataResponse);
            response.setResult(null);
			return response;
		}
		
	}

}
