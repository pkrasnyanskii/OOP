package ru.nsu.krasnyanskii.model.results;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Aggregated test run counts: passed, failed, skipped. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestCounts {
    private int passed;
    private int failed;
    private int skipped;

    /**
     * Returns total number of tests (passed + failed + skipped).
     *
     * @return total test count
     */
    public int getTotal() {
        return passed + failed + skipped;
    }

    /**
     * Adds counts from another instance (used when aggregating multiple XML files).
     *
     * @param other counts to add
     */
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
