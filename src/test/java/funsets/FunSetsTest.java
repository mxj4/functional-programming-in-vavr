package funsets;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FunSetsTest {
    private Set s1 = Set.singletonSet(1);
    private Set s2 = Set.singletonSet(2);
    private Set s3 = Set.singletonSet(3);

    @Test
    public void testContains() {
        assertTrue(s1.contains(1));
    }

    @Test
    public void testUnion() {
        Set set = s1.union(s2);
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertFalse(set.contains(3));
    }

    @Test
    public void testIntersect() {
        Set set = s1.union(s2).intersect(s1);
        assertTrue(set.contains(1));
        assertFalse(set.contains(2));
    }

    @Test
    public void testDiff() {
        Set set = s1.diff(s2);
        assertTrue(set.contains(1));
        assertFalse(set.contains(2));
    }

    @Test
    public void testFilter() {
        Set set = s1.union(s2).filter(x -> x == 1);
        assertTrue(set.contains(1));
        assertFalse(set.contains(2));
        assertFalse(set.contains(1000));
    }

    @Test
    public void testForall() {
        assertTrue(s1.forall(x -> true));
        assertFalse(s1.union(s2).forall(x -> x == 1));
    }

    @Test
    public void testExists() {
        Set set = s1.union(s2);
        assertTrue(set.exists(x -> x == 1));
        assertFalse(set.exists(x -> x == 3));
    }

    @Test
    public void testMap() {
        Set set = s1.union(s2).union(s3).map(x -> x * x);
        assertTrue(set.contains(1));
        assertTrue(set.contains(4));
        assertTrue(set.contains(9));
    }
}
