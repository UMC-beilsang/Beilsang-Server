package com.BeilsangServer.domain.feed.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;


public class AddFeedRequestDTO {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateFeedDTO{
        private String review;
        private MultipartFile feedImage;
    }


}
