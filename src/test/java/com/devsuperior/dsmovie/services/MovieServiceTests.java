package com.devsuperior.dsmovie.services;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.tests.MovieFactory;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {
	
	@InjectMocks
	private MovieService service;
	
	@Mock
	private MovieRepository repository;
	
	private String existingTitle, nonExistingTitle;
	
	private PageImpl<MovieEntity> page;
	
	private MovieEntity movieEntity;
	
	private MovieDTO movieDTO;
	
	private Pageable pageable;
	
	@BeforeEach
	void setup () throws Exception {
		
		existingTitle = "titulo";
		nonExistingTitle = "titulo02";
		movieEntity = MovieFactory.createMovieEntity();
		movieDTO = new MovieDTO(movieEntity);
		page = new PageImpl<>(List.of(movieEntity));
		pageable = PageRequest.of(0, 10);
		
		Mockito.when(repository.searchByTitle(existingTitle, (Pageable)ArgumentMatchers.any())).thenReturn(page);
				
	}
	
	@Test
	public void findAllShouldReturnPagedMovieDTO() {
		
		//Pageable pageable  = PageRequest.of(0, 10);
		
		Page<MovieDTO> result = service.findAll(existingTitle, pageable);
		
		Assertions.assertNotNull(result);
		Mockito.verify(repository).searchByTitle(existingTitle, pageable);
		
	}
	
	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
	}
	
	@Test
	public void insertShouldReturnMovieDTO() {
	}
	
	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
	}
}
