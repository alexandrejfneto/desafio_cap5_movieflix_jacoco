package com.devsuperior.dsmovie.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;
	
	@Mock
	private UserRepository repository;
	
	@Mock
	private CustomUserUtil userUtil;
	
	private UserEntity userEntity;
	
	private String existingUserName, nonExistingUserName;
	
	private List<UserDetailsProjection> userDetails;
	
	@BeforeEach
	void setup () throws Exception {
		
		existingUserName = "maria@gmail.com";
		nonExistingUserName = "usuario02@gmail.com";
		
		userEntity = UserFactory.createUserEntity();
		
		userDetails = UserDetailsFactory.createCustomAdminClientUser(existingUserName);
		
		Mockito.when(repository.findByUsername(existingUserName)).thenReturn(Optional.of(userEntity));
		Mockito.when(repository.findByUsername(nonExistingUserName)).thenReturn(Optional.empty());
		
		Mockito.when(repository.searchUserAndRolesByUsername(existingUserName)).thenReturn(userDetails);
		Mockito.when(repository.searchUserAndRolesByUsername(nonExistingUserName)).thenReturn(new ArrayList<>());
		
	}

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {
		
		Mockito.when(userUtil.getLoggedUsername()).thenReturn(existingUserName);
		
		UserEntity result = service.authenticated();
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getUsername(), existingUserName);
		
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		
		Mockito.when(userUtil.getLoggedUsername()).thenThrow(UsernameNotFoundException.class);
		
		Assertions.assertThrows(UsernameNotFoundException.class, ()->{
			
			service.authenticated();
			
		});
		
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
		
		UserDetails result = service.loadUserByUsername(existingUserName);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getUsername(), existingUserName);
		
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		
		Assertions.assertThrows(UsernameNotFoundException.class, ()-> {
			
			service.loadUserByUsername(nonExistingUserName);
			
		});
		
	}
	
}
