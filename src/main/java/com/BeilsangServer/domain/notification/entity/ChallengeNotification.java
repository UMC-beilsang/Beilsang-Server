package com.BeilsangServer.domain.notification.entity;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@DiscriminatorValue("C")
public class ChallengeNotification extends AppNotification{
    private Long challengeId;
}
