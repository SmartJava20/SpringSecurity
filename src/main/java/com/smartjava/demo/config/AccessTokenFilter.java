package com.smartjava.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.smartjava.demo.filter.JwtFilter;
import com.smartjava.demo.service.AuthenticationService;

import java.io.IOException;
import java.util.Optional;

public class AccessTokenFilter extends OncePerRequestFilter {

	@Autowired
	private JwtFilter jwtHelper;

	@Autowired
	private AuthenticationService securityService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			Optional<String> accessToken = parseAccessToken(request);
			if (accessToken.isPresent() && jwtHelper.validateAccessToken(accessToken.get())) {
				String userName = jwtHelper.getUserIdFromAccessToken(accessToken.get());
				UserDetails user = securityService.loadUserByUsername(userName);
				UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(user, null,
						user.getAuthorities());
				upat.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(upat);
			}
		} catch (Exception e) {
			//log.error("cannot set authentication", e);
		}

		filterChain.doFilter(request, response);
	}

	private Optional<String> parseAccessToken(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer")) {
			return Optional.of(authHeader.replace("Bearer ", ""));
		}
		return Optional.empty();
	}
}
