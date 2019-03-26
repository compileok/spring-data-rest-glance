package com.github.springdatarestglance.data.repository;

import com.github.springdatarestglance.data.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author xudeming
 * @date 2019/3/19
 */
@Repository
@RepositoryRestResource(collectionResourceRel = "person", path = "person")
public interface PersonRepository extends JpaRepository<Person,Long> {

	List<Person> findByLastName(@Param("name") String name);
	List<Person> findByFirstName(@Param("name") String name);

}
