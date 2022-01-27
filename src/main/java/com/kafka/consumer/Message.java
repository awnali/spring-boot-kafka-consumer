package com.kafka.consumer;

import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@Builder
public
class Message {

    Integer id;

    String name;

    String address;

    String phone;

    boolean isActive;
}
