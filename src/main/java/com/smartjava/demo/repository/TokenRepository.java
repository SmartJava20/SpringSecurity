package com.smartjava.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartjava.demo.model.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer>{

}
