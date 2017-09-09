package recfun;

import io.vavr.collection.CharSeq;
import io.vavr.collection.List;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RecFunTest {
    @Test
    public void testPascal() {
        assertEquals(Integer.valueOf(1), RecFun.pascal(0, 2));
        assertEquals(Integer.valueOf(2), RecFun.pascal(1, 2));
        assertEquals(Integer.valueOf(3), RecFun.pascal(1, 3));
    }

    @Test
    public void testBalance() {
        assertTrue(RecFun.balance(CharSeq.of("(if (zero? x) max (/ 1 x))")));
        assertTrue(RecFun.balance(CharSeq.of("I told him (that it's not (yet) done).\n(But he wasn't listening)")));
        assertFalse(RecFun.balance(CharSeq.of(":-)")));
        assertFalse(RecFun.balance(CharSeq.of("())(")));

        assertTrue(RecFun.balance(CharSeq.of("")));
        assertFalse(RecFun.balance(CharSeq.of("(")));
        assertFalse(RecFun.balance(CharSeq.of(")")));
        assertTrue(RecFun.balance(CharSeq.of("()")));
        assertFalse(RecFun.balance(CharSeq.of(")(")));
        assertFalse(RecFun.balance(CharSeq.of(")()(")));
    }

    @Test
    public void testConutntChange() {
        assertEquals(Integer.valueOf(3), RecFun.countChange(4, List.of(1, 2)));
        assertEquals(Integer.valueOf(1022), RecFun.countChange(300, List.of(5,10,20,50,100,200,500)));
        assertEquals(Integer.valueOf(0), RecFun.countChange(301, List.of(5,10,20,50,100,200,500)));
        assertEquals(Integer.valueOf(1022), RecFun.countChange(300, List.of(500,5,50,100,20,200,10)));
    }
}
