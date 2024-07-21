package com.devsuperior.dsmovie.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;
	
	@Mock
	private UserService userService;
	
	@Mock
	private MovieRepository movieRepository;
	
	@Mock
	private ScoreRepository scoreRepository;
	
	private Long existingMovieId, nonExistingMovieId;
	private UserEntity user;
	private MovieEntity movie;
	private ScoreEntity scoreEntity;
	private ScoreDTO scoreDTO;
	
	@BeforeEach
	void setup () throws Exception {
		
		existingMovieId = 1L;
		nonExistingMovieId = 2L;
		
		user = UserFactory.createUserEntity();
		movie = MovieFactory.createMovieEntity();
		scoreEntity = ScoreFactory.createScoreEntity();
		movie.getScores().add(scoreEntity);
		scoreDTO = ScoreFactory.createScoreDTO();

		Mockito.when(userService.authenticated()).thenReturn(user);
		
		Mockito.when(scoreRepository.saveAndFlush(any())).thenReturn(scoreEntity);
		
		Mockito.when(movieRepository.save(any())).thenReturn(movie);		
		
	}
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {
		
		Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movie));
		
		MovieDTO result = service.saveScore(scoreDTO);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getScore(), scoreDTO.getScore());
		
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		
		Mockito.when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());
		
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			
			MovieDTO result = service.saveScore(scoreDTO);
			
		});
		
	}
	
}
