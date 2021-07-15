package com.starwars.movies.repository;

import com.starwars.movies.entity.MovieCharacter;
import com.starwars.movies.model.MovieCharPage;
import com.starwars.movies.model.MovieCharSearchCriteria;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class MovieCharCriteriaRepository
{
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;
    
    public MovieCharCriteriaRepository(EntityManager entityManager)
    {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }
    
    public Page<MovieCharacter> findAllWithFilters(MovieCharPage movieCharPage, MovieCharSearchCriteria movieCharSearchCriteria, Long id)
    {
        CriteriaQuery<MovieCharacter> criteriaQuery = criteriaBuilder.createQuery(MovieCharacter.class);
        Root<MovieCharacter> movieRoot = criteriaQuery.from(MovieCharacter.class);
        Predicate predicate = getPredicate(movieCharSearchCriteria, movieRoot, id);
        
        criteriaQuery.where(predicate);
        setOrder(movieCharPage, criteriaQuery, movieRoot);
        TypedQuery<MovieCharacter> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(movieCharPage.getPageNumber() * movieCharPage.getPageSize());
        typedQuery.setMaxResults(movieCharPage.getPageSize());
        
        Pageable pageable = getPageable(movieCharPage);
        
        
        long movieCharCount = getMovieCharCount(predicate);
        
        return new PageImpl<>(typedQuery.getResultList(), pageable, movieCharCount);
    }
    
    
    private Predicate getPredicate(MovieCharSearchCriteria movieCharSearchCriteria, Root<MovieCharacter> movieRoot,long id)
    {
        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(movieCharSearchCriteria.getGender()))
        {
            predicates.add(criteriaBuilder.equal(movieRoot.get("gender"), movieCharSearchCriteria.getGender()));
            predicates.add(criteriaBuilder.equal(movieRoot.get("movie_id"), id));
        }
        
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
    
    private void setOrder(MovieCharPage movieCharPage, CriteriaQuery<MovieCharacter> criteriaQuery, Root<MovieCharacter> movieRoot)
    {
        if (movieCharPage.getDirection().equals(Sort.Direction.ASC))
        {
            criteriaQuery.orderBy(criteriaBuilder.asc(movieRoot.get(movieCharPage.getSortBy())));
        }
        else
        {
            criteriaQuery.orderBy(criteriaBuilder.desc(movieRoot.get(movieCharPage.getSortBy())));
        }
    }
    
    private Pageable getPageable(MovieCharPage movieCharPage)
    {
        Sort sort = Sort.by(movieCharPage.getDirection(), movieCharPage.getSortBy());
        return PageRequest.of(movieCharPage.getPageNumber(), movieCharPage.getPageSize(), sort);
    }
    
    private long getMovieCharCount(Predicate predicate)
    {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<MovieCharacter> countRoot = countQuery.from(MovieCharacter.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
