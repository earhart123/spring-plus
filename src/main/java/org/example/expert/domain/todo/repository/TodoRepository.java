package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long>, QTodoRepository {

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    @Query("SELECT t FROM Todo t " +
            "WHERE t.modifiedAt >= :startOfDay AND t.modifiedAt <= :endOfDay " +
            "ORDER BY t.modifiedAt DESC")
    Page<Todo> findByModifiedAt(@Param("startOfDay") LocalDateTime startOfDay,
                                @Param("endOfDay") LocalDateTime endOfDay,
                                Pageable pageable);

    @Query("SELECT t FROM Todo t " +
            "WHERE t.modifiedAt >= :startOfDay AND " +
            "t.modifiedAt <= :endOfDay AND " +
            "t.weather = :weather " +
            "ORDER BY t.modifiedAt DESC")
    Page<Todo> findByWeatherAndModifiedAt(@Param("weather") String weather,
                                          @Param("startOfDay") LocalDateTime startOfDay,
                                          @Param("endOfDay") LocalDateTime endOfDay,
                                          Pageable pageable);

    @Query("SELECT t FROM Todo t " +
            "WHERE t.weather = :weather " +
            "ORDER BY t.modifiedAt DESC")
    Page<Todo> findByWeather(String weather, Pageable pageable);

//    @Query("SELECT t FROM Todo t " +
//            "LEFT JOIN t.user " +
//            "WHERE t.id = :todoId")
//    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);
}
