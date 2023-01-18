package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private long timestamp;
    private int userId;
    @NotNull
    private String eventType;
    @NotNull
    private String operation;
    private int eventId;
    private int entityId;
}