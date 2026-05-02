package ru.nsu.krasnyanskii.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanskii.model.results.TestCounts;

@DisplayName("TestCounts — test result aggregation")
class TestCountsTest {

    @Test
    @DisplayName("getTotal() = passed + failed + skipped")
    void totalIsSum() {
        TestCounts tc = new TestCounts(5, 2, 1);
        assertEquals(8, tc.getTotal());
    }

    @Test
    @DisplayName("Empty counts are all zero")
    void emptyCountsAreZero() {
        TestCounts tc = new TestCounts();
        assertEquals(0, tc.getTotal());
        assertEquals(0, tc.getPassed());
        assertEquals(0, tc.getFailed());
        assertEquals(0, tc.getSkipped());
    }

    @Test
    @DisplayName("add() combines all fields")
    void addCombinesAll() {
        TestCounts a = new TestCounts(3, 1, 0);
        TestCounts b = new TestCounts(2, 0, 1);
        a.add(b);

        assertEquals(5, a.getPassed());
        assertEquals(1, a.getFailed());
        assertEquals(1, a.getSkipped());
        assertEquals(7, a.getTotal());
    }

    @Test
    @DisplayName("toString() contains P/F/S labels")
    void toStringFormat() {
        TestCounts tc = new TestCounts(4, 1, 2);
        String s = tc.toString();
        assertTrue(s.contains("4"));
        assertTrue(s.contains("P"));
        assertTrue(s.contains("1"));
        assertTrue(s.contains("F"));
        assertTrue(s.contains("S"));
    }
}
