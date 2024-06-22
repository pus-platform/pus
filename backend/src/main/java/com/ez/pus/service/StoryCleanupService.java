package com.ez.pus.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;
import com.ez.pus.repository.StoryRepository;

@Service
public class StoryCleanupService {

    private final StoryRepository storyRepository;

    public StoryCleanupService(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void deleteOldStories() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        storyRepository.deleteByCreatedAtBefore(cutoffTime);
    }
}
