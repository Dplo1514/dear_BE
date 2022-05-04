package com.sparta.hh99_actualproject.model;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class Selection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long selectionId;

    @Column(nullable = false)
    private Long voteBoardId;

    @Column(nullable = false)
    private String memberId;

    @Column(nullable = false)
    private Integer selectionNum;
}
