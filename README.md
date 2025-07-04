# SPRING PLUS

시행 착오
 

1. Spring Security<br>

https://hyeonha.tistory.com/41<br>

1-1. Spring Security를 적용한 후 Controller 에서 @AuthenticationPrincipal 어노테이션이 아닌 기존 @Auth 어노테이션을 사용하고 있어서 오류가 발생하여 수정했다.<br>



1-2. UserRole 상수에 "USER", "ADMIN"으로 등록되어 있어서 Spring Security에서 사용하기 위해

hasAuthority 메서드를 사용했지만 오류가 있어서 hasRole 메서드를 사용하고 "ROLE_" 접두어를 붙이는 방식을 사용했다.<br>

 

1-3. postman 테스트 시 uri를 /todos/search-title/?title="todo" 형색으로 사용하고 있어서 오류가 발생하여 수정했다.<br>

 
 

 
<br><br>
2. QueryDSL

 

2-1. SQL 인젝션 가능성과 성능 저하를 방지하기 위해 like 방식 대신 contains를 사용했다.

![image](https://github.com/user-attachments/assets/1edd2769-09fb-4f0e-85ac-a75132343847)

<br>
 

2-2. 생성일 기간을 받은 후 그 사이에 있는 값을 확인하기 위해 goe, loe를 사용했다.

goe : greater or equal, loe : less or equal

![image](https://github.com/user-attachments/assets/209a6666-0ab3-4b7b-aa87-ef97ae42c44c)

 
<br>
 

2-3. N+1 문제가 발생할 것 같은 코드를 확인하여 fetchJoin을 추가했다.

 

todoId로 todo 조회

![image](https://github.com/user-attachments/assets/c8b0e742-6421-4b25-9e82-c8b5fc64ac4d)

nickname으로 작성자 닉네임을 확인하여 todo 조회

![image](https://github.com/user-attachments/assets/3b95e796-9f3f-4af8-b6f3-02e8647ded7e)


 <br><br>
3. AOP

 

3-1. @AfterReturning을 사용하여 실패한 경우에만 AOP에서 로그를 기록하도록 처리하는 방식에서

@Around를 사용하는 방식으로 수정하여 성공, 실패 로그를 모두 AOP에서 기록할 수 있도록 수정하였다.

 <br>

 

3-2. 새로 생성한 log 객체 및 테이블에서 @CreatedDate 어노테이션이 작동하지 않고 null 값으로 들어가는 현상이 발생했다.

@EnableJpaAuditing 어노테이션이 적용되어야 하는데 해당 어노테이션이 적용되어 있는 PersistenceConfig 클래스에 아래 코드를 추가했다.

 
![image](https://github.com/user-attachments/assets/2763a21e-79f1-4da0-a5a7-b02d160ad1a7)

 <br>

@EnableJpaAuditing 어노테이션을 Application이 아닌 별도 클래스로 분리하는 것은 관심사 분리, 즉 모듈화 관점에서 유지 보수성, 가독성이 좋아진다는 것을 알게 되었다.
