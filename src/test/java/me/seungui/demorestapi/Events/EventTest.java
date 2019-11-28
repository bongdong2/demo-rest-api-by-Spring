package me.seungui.demorestapi.Events;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EventTest {

    @Test
    public void builder() {
        Event event = Event.builder()
                .name("java study")
                .limitOfEnrollment(3)
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() {
        // given
        String name = "rest-api-study";
        String description = "by spring";

        // when
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        // then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }

    @Test
    public void testFree() {
        // given
        Event event = Event.builder()
                .basePrice(0)
                .maxPrice(0)
                .build();

        // when
        event.update();

        // then
        assertThat(event.isFree()).isTrue();

        // given
        event = Event.builder()
                .basePrice(100)
                .maxPrice(0)
                .build();

        // when
        event.update();

        // then
        assertThat(event.isFree()).isFalse();

        // given
        event = Event.builder()
                .basePrice(0)
                .maxPrice(100)
                .build();

        // when
        event.update();

        // then
        assertThat(event.isFree()).isFalse();
    }

    @Test
    public void testOffine() throws Exception {
        //given 테스트 전의 상태
        Event event = Event.builder()
                .build();

        //when 테스트 행위
        event.update();

        //than 테스트 검증
        assertThat(event.isOffline()).isFalse();

        //given 테스트 전의 상태
        event = Event.builder()
                .location("제주도 어딘가")
                .build();

        //when 테스트 행위
        event.update();

        //than 테스트 검증
        assertThat(event.isOffline()).isTrue();
    }
}