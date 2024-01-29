package com.BeilsangServer.domain.feed.service;

import com.BeilsangServer.domain.feed.dto.AddFeedRequestDTO;
import com.BeilsangServer.domain.feed.dto.FeedDTO;
import com.BeilsangServer.domain.feed.entity.Feed;
import com.BeilsangServer.domain.feed.mapper.FeedMapper;
import com.BeilsangServer.domain.feed.repository.FeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private FeedRepository feedRepository;
    private FeedMapper feedMapper;


}
