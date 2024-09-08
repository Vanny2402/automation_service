package com.vanny.Automateapi.repository.jwtrepository;

import org.springframework.data.repository.CrudRepository;

import com.vanny.Automateapi.model.jwtmodel.UserDao;


public interface UserRepository extends CrudRepository<UserDao, Long> {
	public UserDao findByUsername(String userName);
	
}

