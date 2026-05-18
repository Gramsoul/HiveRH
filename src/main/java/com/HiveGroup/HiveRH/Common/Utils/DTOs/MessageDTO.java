package com.HiveGroup.HiveRH.Common.Utils.DTOs;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MessageDTO {
    public String message;
    public LocalDate date;
}
