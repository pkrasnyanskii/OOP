import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Tests for realization of Rabin-Karp algorithm.
 */
public class RabinKarpTest {
    @Test
    public void simpleTest() throws IOException {
        try (InputStream stream =
                     getClass().getClassLoader().getResourceAsStream("text_simple.txt")) {
            RabinKarp alg = new RabinKarp(stream);
            List<Integer> act = alg.rabinKarp("e");
            List<Integer> exp = Arrays.asList(1, 6, 17, 22, 29, 34, 37);

            Assertions.assertEquals(exp, act);
        }
    }

    @Test
    public void countTest() throws IOException {
        try (InputStream stream =
                     getClass().getClassLoader().getResourceAsStream("text_big.txt")) {
            RabinKarp alg = new RabinKarp(stream);
            Integer act = Math.toIntExact(alg.rabinKarp("a").stream().count());
            Integer exp = 120;

            Assertions.assertEquals(exp, act);
        }
    }
}