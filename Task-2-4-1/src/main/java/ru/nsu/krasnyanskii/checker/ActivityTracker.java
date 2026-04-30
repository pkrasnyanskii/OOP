package ru.nsu.krasnyanskii.checker;

import java.nio.file.Path;
import java.util.Set;
import ru.nsu.krasnyanskii.model.ActivityConfig;

/** Counts active commit weeks and calculates the corresponding bonus. */
public class ActivityTracker {
    private final GitManager gitManager;

    public ActivityTracker(GitManager gitManager) {
        this.gitManager = gitManager;
    }

    /**
     * Counts weeks in which the student made at least one commit within the course period.
     *
     * @param repoPath local repository path
     * @param config   activity configuration with course dates
     * @return number of active weeks
     */
    public int countActiveWeeks(Path repoPath, ActivityConfig config) {
        if (config == null
                || config.getCourseStart() == null
                || config.getCourseEnd() == null) {
            return 0;
        }
        Set<String> weeks = gitManager.getActiveWeeks(
                repoPath, config.getCourseStart(), config.getCourseEnd());
        return weeks.size();
    }

    /**
     * Calculates the activity bonus. Full bonus if weeks >= threshold, proportional otherwise.
     *
     * @param activeWeeks number of weeks with at least one commit
     * @param config      activity configuration
     * @return bonus points to award
     */
    public double calculateActivityBonus(int activeWeeks, ActivityConfig config) {
        if (config == null) {
            return 0.0;
        }
        if (activeWeeks >= config.getMinActiveWeeks()) {
            return config.getBonusPoints();
        }
        double fraction = (double) activeWeeks / config.getMinActiveWeeks();
        return Math.floor(fraction * config.getBonusPoints());
    }
}
