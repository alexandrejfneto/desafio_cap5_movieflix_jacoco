package com.devsuperior.dsmovie.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {

	@InjectMocks
	private MovieService service;

	@Mock
	private MovieRepository repository;

	private String existingTitle;

	private Long existingMovieId, nonExistingMovieId, dependentMovieId;

	private PageImpl<MovieEntity> page;

	private MovieEntity movieEntity;

	private MovieDTO movieDTO;

	private Pageable pageable;

	@BeforeEach
	void setup() throws Exception {

		existingTitle = "titulo";
		existingMovieId = 1L;
		nonExistingMovieId = 2L;
		dependentMovieId = 3L;
		movieEntity = MovieFactory.createMovieEntity();
		movieDTO = new MovieDTO(movieEntity);
		page = new PageImpl<>(List.of(movieEntity));
		pageable = PageRequest.of(0, 10);

		Mockito.when(repository.searchByTitle(existingTitle, pageable)).thenReturn(page);

		Mockito.when(repository.findById(existingMovieId)).thenReturn(Optional.of(movieEntity));
		Mockito.when(repository.findById(nonExistingMovieId)).thenReturn(Optional.empty());

		Mockito.when(repository.save(any())).thenReturn(movieEntity);

		Mockito.when(repository.getReferenceById(existingMovieId)).thenReturn(movieEntity);
		Mockito.when(repository.getReferenceById(nonExistingMovieId)).thenThrow(EntityNotFoundException.class);
		
		Mockito.when(repository.existsById(existingMovieId)).thenReturn(true);
		Mockito.when(repository.existsById(nonExistingMovieId)).thenReturn(false);
		Mockito.when(repository.existsById(dependentMovieId)).thenReturn(true);
		
		Mockito.doNothing().when(repository).deleteById(existingMovieId);
		Mockito.doThrow(ResourceNotFoundException.class).when(repository).deleteById(nonExistingMovieId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentMovieId);

	}

	@Test
	public void findAllShouldReturnPagedMovieDTO() {

		Page<MovieDTO> result = service.findAll(existingTitle, pageable);

		Assertions.assertNotNull(result);
		Mockito.verify(repository).searchByTitle(existingTitle, pageable);

	}

	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {

		MovieDTO result = service.findById(existingMovieId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getTitle(), movieDTO.getTitle());
		Mockito.verify(repository).findById(existingMovieId);

	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {

			service.findById(nonExistingMovieId);

		});
		Mockito.verify(repository).findById(nonExistingMovieId);

	}

	@Test
	public void insertShouldReturnMovieDTO() {

		MovieDTO result = service.insert(movieDTO);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getTitle(), movieEntity.getTitle());

	}

	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {

		MovieDTO result = service.update(existingMovieId, movieDTO);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getTitle(), movieEntity.getTitle());

	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {

			service.update(nonExistingMovieId, movieDTO);

		});
		Mockito.verify(repository).getReferenceById(nonExistingMovieId);

	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		
		Assertions.assertDoesNotThrow(()->{
			service.delete(existingMovieId);
			});
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingMovieId);
		
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {

			service.delete(nonExistingMovieId);

		});
		Mockito.verify(repository).existsById(nonExistingMovieId);
		
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		
		Assertions.assertThrows(DatabaseException.class, () -> {

			service.delete(dependentMovieId);

		});
		Mockito.verify(repository).existsById(dependentMovieId);
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentMovieId);
		
		
	}
	
}
