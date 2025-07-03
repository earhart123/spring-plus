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

    /**
     * 특정 id를 가진 todo를 user와 같이 조회
     *
     * <p>JPQL 예시:
     * <pre>
     * SELECT t FROM Todo t
     * LEFT JOIN FETCH t.user
     * WHERE t.id = :todoId
     * </pre>
     *
     * @param todoId 조회할 todo id
     * @return 해당하는 id의 todo, user
     * 존재하지 않으면 빈 Optional 반환
     */
    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        return Optional.ofNullable(queryFactory.selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchFirst());
    }

    /**
     * 특정 제목이 포함된 할 일 검색
     * 각 todo의 매니저 수, 댓글 수 포함
     *
     * @param title 검색할 todo 제목
     * @param pageable 페이징 정보
     * @return 페이징 된 TodoSearchResponse 목록
     */
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

    /**
     * 생성일 기간으로 todo 검색
     * 각 todo의 매니저 수, 댓글 수 포함
     *
     * @param start 생성일 기간 시작일
     * @param end 생성일 기간 종료일
     * @param pageable 페이징 정보
     * @return 페이징 된 TodoSearchResponse 목록
     */
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

    /**
     * 작성자 닉네임으로 할 일 검색
     * 각 todo의 매니저 수, 댓글 수 포함
     *
     * @param nickname 검색할 닉네임
     * @param pageable 페이징 정보
     * @return 페이징 된 TodoSearchResponse 목록
     */
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
                .leftJoin(todo.user, user).fetchJoin()
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
