package com.pickgo.domain.member.member.entity;

import com.pickgo.domain.member.member.entity.enums.Authority;
import com.pickgo.domain.member.member.entity.enums.SocialProvider;
import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(name = "member")
public class Member extends BaseEntity {

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<Reservation> reservations = new ArrayList<>();
    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;
    @Column(nullable = false)
    private String email;
    @Setter
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String nickname;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    private Authority authority;
    @Setter
    private String profile;
    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SocialProvider socialProvider;

    public void update(String nickname) {
        this.nickname = nickname;
    }

    public void addReservation(Reservation reservation) {
        this.reservations.add(reservation);
        reservation.setMember(this);
    }

}
