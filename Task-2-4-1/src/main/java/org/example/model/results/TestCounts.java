package org.example.model.results;

public class TestCounts {
    private int passed;
    private int failed;
    private int skipped;

    public TestCounts() {}

    public TestCounts(int passed, int failed, int skipped) {
        this.passed = passed;
        this.failed = failed;
        this.skipped = skipped;
    }

    public int getPassed() { return passed; }
    public int getFailed() { return failed; }
    public int getSkipped() { return skipped; }
    public int getTotal() { return passed + failed + skipped; }

    public void add(TestCounts other) {
        this.passed += other.passed;
        this.failed += other.failed;
        this.skipped += other.skipped;
    }

    @Override
    public String toString() {
        return passed + "P / " + failed + "F / " + skipped + "S";
    }
}
