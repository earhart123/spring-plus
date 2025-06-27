package org.example.expert.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.stereotype.Repository;

import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QTodoRepositoryImpl implements QTodoRepository{

    private final JPAQueryFactory queryFactory;


    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
//        "SELECT t FROM Todo t "
//        "LEFT JOIN t.user "
//        "WHERE t.id = :todoId")

        return Optional.ofNullable(queryFactory.selectFrom(todo)
                .leftJoin(todo.user, user)
                .where(todo.id.eq(todoId))
                .fetchFirst());
    }
}
