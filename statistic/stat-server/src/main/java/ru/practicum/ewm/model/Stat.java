package ru.practicum.ewm.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stats")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "app", nullable = false, length = 100)
    private String app;

    @Column(name = "uri", nullable = false, length = 200)
    private String uri;

    @Column(name = "ip", nullable = false, length = 15)
    private String ip;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}
