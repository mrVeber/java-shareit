package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "COMMENTS")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text")
    private String text;

    @JoinColumn(name = "author")
    @ManyToOne
    private User author;

    @JoinColumn(name = "item_id")
    @ManyToOne
    private Item item;

    @Column(name = "created")
    private LocalDateTime created;
}