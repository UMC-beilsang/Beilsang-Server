package com.BeilsangServer.domain.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddFeedRequestDTO {

    private String review;

    private String feedUrl;

}
