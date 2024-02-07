package com.template.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_view")
public class User {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long userId;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;
}
