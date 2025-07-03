package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final WeatherClient weatherClient;

    @Transactional
    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail())
        );
    }

    /**
     * 할 일 검색 (날씨, 수정일)
     *
     * @param page 페이지 번호
     * @param size 한 페이지에 데이터 수
     * @param weather 날씨 검색
     * @param startDate 수정일 검색 기간 시작일
     * @param endDate 수정일 검색 기간 종료일
     * @return 페이징된 할 일 목록
     */
    public Page<TodoResponse> getTodos(int page, int size, String weather, String startDate, String endDate) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Todo> todos;

        // 날씨, 수정일 모두 검색
        if(weather != null && startDate != null && endDate != null){
            LocalDateTime startOfDay = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
            LocalDateTime endOfDay = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE).atTime(23, 59, 59);

            todos = todoRepository.findByWeatherAndModifiedAt(weather, startOfDay, endOfDay, pageable);
        }
        // 수정일 검색
        else if(weather == null && startDate != null && endDate != null){
            LocalDateTime startOfDay = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
            LocalDateTime endOfDay = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE).atTime(23, 59, 59);

            todos = todoRepository.findByModifiedAt(startOfDay, endOfDay, pageable);
        }
        // 날짜 검색
        else if(weather != null){
            todos = todoRepository.findByWeather(weather, pageable);
        }
        // 모두 검색 (검색 조건 없음)
        else{
            todos = todoRepository.findAllByOrderByModifiedAtDesc(pageable);
        }

        return todos.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }

    /**
     * 할 일 id 조회
     *
     * @param todoId 할 일 id
     * @return 검색된 할 일
     */
    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = todo.getUser();

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }

    /**
     * 할 일 제목으로 조회
     *
     * @param page 페이지 번호
     * @param size 한 페이지에 데이터 수
     * @param title 제목 검색
     * @return 페이징된 할 일 목록
     */
    public Page<TodoSearchResponse> searchTodoByTitle(int page, int size, String title) {
        Pageable pageable = PageRequest.of(page - 1, size);

        return todoRepository.findByTitleContains(title, pageable);
    }

    /**
     * 할 일 생성일 기간 조회
     *
     * @param page 페이지 번호
     * @param size 한 페이지에 데이터 수
     * @param startDate 생성일 검색 기간 시작일
     * @param endDate 생성일 검색 기간 종료일
     * @return 페이징된 할 일 목록
     */
    public Page<TodoSearchResponse> searchTodoByCreateAt(int page, int size, String startDate, String endDate) {
        Pageable pageable = PageRequest.of(page - 1, size);

        LocalDateTime startOfDay = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        LocalDateTime endOfDay = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE).atTime(23, 59, 59);

        return todoRepository.findByCreatedAt(startOfDay, endOfDay, pageable);
    }

    /**
     * 할 일 작성자 닉네임 검색
     * @param page 페이지 번호
     * @param size 한 페이지에 데이터 수
     * @param nickname 닉네임 검색
     * @return 페이징된 할 일 목록
     */
    public Page<TodoSearchResponse> searchTodoByNickname(int page, int size, String nickname) {
        Pageable pageable = PageRequest.of(page - 1, size);

        return todoRepository.findByNicknameContains(nickname, pageable);
    }
}
