package org.example.model.results;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Результаты прогона тестов: количество прошедших / упавших / пропущенных.
 * Не используем @Data, чтобы сохранить кастомные toString и getTotal.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestCounts {
    private int passed;
    private int failed;
    private int skipped;

    /** Общее число тестов. */
    public int getTotal() {
        return passed + failed + skipped;
    }

    /** Прибавляет результаты другого набора тестов (для суммирования по XML-файлам). */
    public void add(TestCounts other) {
        this.passed  += other.passed;
        this.failed  += other.failed;
        this.skipped += other.skipped;
    }

    @Override
    public String toString() {
        return passed + "P / " + failed + "F / " + skipped + "S";
    }
}
