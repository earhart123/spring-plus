package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

import java.time.LocalDateTime;
import java.util.List;
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

    @Override
    public Page<TodoSearchResponse> findByTitleContains(String title, Pageable pageable) {
        QTodo todo = QTodo.todo;
        QManager manager = QManager.manager;
        QComment comment = QComment.comment;

        List<TodoSearchResponse> content = queryFactory
                .select(Projections.constructor(TodoSearchResponse.class,
                        todo.id,
                        todo.title,
                        manager.id.countDistinct().intValue(),
                        comment.id.countDistinct().intValue()
                ))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .where(todo.title.contains(title))
                .groupBy(todo.id, todo.title)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory.select(todo.count())
                .from(todo)
                .where(todo.title.contains(title))
                .fetchOne();

        return new PageImpl<>(content, pageable, count == null ? 0 : count);
    }

    @Override
    public Page<TodoSearchResponse> findByCreatedAt(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        QTodo todo = QTodo.todo;
        QManager manager = QManager.manager;
        QComment comment = QComment.comment;

        List<TodoSearchResponse> content = queryFactory
                .select(Projections.constructor(TodoSearchResponse.class,
                        todo.id,
                        todo.title,
                        manager.id.countDistinct().intValue(),
                        comment.id.countDistinct().intValue()
                ))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .where(todo.createdAt.goe(start)
                        .and(todo.createdAt.loe(end)))
                .groupBy(todo.id)
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory.select(todo.countDistinct())
                .from(todo)
                .where(todo.createdAt.goe(start)
                        .and(todo.createdAt.loe(end)))
                .fetchOne();

        return new PageImpl<>(content, pageable, count == null ? 0 : count);
    }

    @Override
    public Page<TodoSearchResponse> findByNicknameContains(String nickname, Pageable pageable) {
        QTodo todo = QTodo.todo;
        QManager manager = QManager.manager;
        QComment comment = QComment.comment;

        List<TodoSearchResponse> content = queryFactory
                .select(Projections.constructor(TodoSearchResponse.class,
                        todo.id,
                        todo.title,
                        manager.id.countDistinct().intValue(),
                        comment.id.countDistinct().intValue()
                ))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .where(todo.user.nickname.contains(nickname))
                .groupBy(todo.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory.select(todo.count())
                .from(todo)
                .where(todo.user.nickname.contains(nickname))
                .fetchOne();

        return new PageImpl<>(content, pageable, count == null ? 0 : count);
    }


}
