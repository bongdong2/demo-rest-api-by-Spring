package me.seungui.demorestapi.Events;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventResource extends EntityModel<Event> {

    public EventResource(Event event, Link... links) {
        super(event, links);
        // 같은 내용인데  아래 소스가 type safe
        // add(new Link("http://localhost:8080/api/events" + event.getId()));
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }
}
