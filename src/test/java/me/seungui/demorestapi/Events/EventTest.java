package me.seungui.demorestapi.Events;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
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


    private Object[] parametersForTestFree() {
        return new Object[] {
                new Object[] {0, 0, true},
                new Object[] {100, 0, false},
                new Object[] {0, 100, false},
                new Object[] {100, 100, false}
        };
    }

    @Test
    @Parameters
    public void testFree(int basePrice, int maxPrice, boolean isFree) {
        // given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();

        // when
        event.update();

        // then
        assertThat(event.isFree()).isEqualTo(isFree);
    }


    private Object[] parametersForTestOffine() {
        return new Object[] {
                new Object[] {"jeju", true},
                new Object[] {null, false},
                new Object[] {"   ", false}
        };
    }

    @Test
    @Parameters
    public void testOffine(String location, boolean isOffline) {
        //given 테스트 전의 상태
        Event event = Event.builder()
                .location(location)
                .build();

        //when 테스트 행위
        event.update();

        //than 테스트 검증
        assertThat(event.isOffline()).isEqualTo(isOffline);
    }
}