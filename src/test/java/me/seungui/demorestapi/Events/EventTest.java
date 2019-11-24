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
}