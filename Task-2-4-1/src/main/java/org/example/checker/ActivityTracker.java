package org.example.checker;

import org.example.model.ActivityConfig;

import java.nio.file.Path;
import java.util.Set;

public class ActivityTracker {
    private final GitManager gitManager;

    public ActivityTracker(GitManager gitManager) {
        this.gitManager = gitManager;
    }

    /**
     * Count weeks in which the student made at least one commit.
     */
    public int countActiveWeeks(Path repoPath, ActivityConfig config) {
        if (config == null || config.getCourseStart() == null || config.getCourseEnd() == null) {
            return 0;
        }
        Set<String> weeks = gitManager.getActiveWeeks(repoPath, config.getCourseStart(), config.getCourseEnd());
        return weeks.size();
    }

    /**
     * Calculate activity bonus based on active weeks vs threshold.
     */
    public double calculateActivityBonus(int activeWeeks, ActivityConfig config) {
        if (config == null) return 0.0;
        if (activeWeeks >= config.getMinActiveWeeks()) {
            return config.getBonusPoints();
        }
        // Partial bonus: proportional to how close they are to the threshold
        double fraction = (double) activeWeeks / config.getMinActiveWeeks();
        return Math.floor(fraction * config.getBonusPoints());
    }
}

