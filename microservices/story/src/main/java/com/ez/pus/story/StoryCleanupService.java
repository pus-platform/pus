package com.ez.pus.story;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StoryCleanupService {

    private final StoryRepository storyRepository;

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void deleteOldStories() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        storyRepository.deleteByCreatedAtBefore(cutoffTime);
    }
}
