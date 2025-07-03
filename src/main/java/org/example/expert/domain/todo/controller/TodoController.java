package org.example.expert.domain.todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/todos")
    public ResponseEntity<TodoSaveResponse> saveTodo(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody TodoSaveRequest todoSaveRequest
    ) {
        return ResponseEntity.ok(todoService.saveTodo(authUser, todoSaveRequest));
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
    @GetMapping("/todos")
    public ResponseEntity<Page<TodoResponse>> getTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String weather,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        return ResponseEntity.ok(todoService.getTodos(page, size, weather, startDate, endDate));
    }

    /**
     * 할 일 id 조회
     *
     * @param todoId 할 일 id
     * @return 검색된 할 일
     */
    @GetMapping("/todos/{todoId}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable long todoId) {
        return ResponseEntity.ok(todoService.getTodo(todoId));
    }

    /**
     * 할 일 제목으로 조회
     *
     * @param page 페이지 번호
     * @param size 한 페이지에 데이터 수
     * @param title 제목 검색
     * @return 페이징된 할 일 목록
     */
    @GetMapping("/todos/search-title")
    public ResponseEntity<Page<TodoSearchResponse>> searchTodoByTitle(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String title
    ) {
        return ResponseEntity.ok(todoService.searchTodoByTitle(page, size, title));
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
    @GetMapping("/todos/search-date")
    public ResponseEntity<Page<TodoSearchResponse>> searchTodoByDate(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        return ResponseEntity.ok(todoService.searchTodoByCreateAt(page, size, startDate, endDate));
    }

    /**
     * 할 일 작성자 닉네임 검색
     * @param page 페이지 번호
     * @param size 한 페이지에 데이터 수
     * @param nickname 닉네임 검색
     * @return 페이징된 할 일 목록
     */
    @GetMapping("/todos/search-nickname")
    public ResponseEntity<Page<TodoSearchResponse>> searchTodoByNickname(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String nickname
    ) {
        return ResponseEntity.ok(todoService.searchTodoByNickname(page, size, nickname));
    }
}
