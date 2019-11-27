package me.seungui.demorestapi.Events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//입력 값을 받는 Dto
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EventDto {

    // Controller에서 Dto로 입력을 받으므로 아래 프로퍼티가 아닌 것을 받는 경우 무시된다.

    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
}
