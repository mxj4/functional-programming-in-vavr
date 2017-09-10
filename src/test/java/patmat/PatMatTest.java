package patmat;

import io.vavr.Tuple;
import io.vavr.collection.CharSeq;
import io.vavr.collection.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static patmat.CodeTree.Fork;
import static patmat.CodeTree.Leaf;

public class PatMatTest {
    private CodeTree t1 = new Fork(new Leaf('a', 2), new Leaf('b', 3), List.of('a', 'b'), 5);
    private CodeTree t2 = new Fork(new Fork(new Leaf('a', 2), new Leaf('b', 3), List.of('a', 'b'), 5), new Leaf('d', 4), List.of('a', 'b', 'd'), 9);
    
    @Test
    public void testTrees() {
        assertEquals(Integer.valueOf(5), t1.weight());
        assertEquals(CharSeq.of('a', 'b', 'd'), t2.characters());
        assertEquals(CodeTree.string2Chars("hello, world"), CharSeq.of("hello, world"));
        assertEquals(
                CodeTree.makeOrderedLeafList(List.of(Tuple.of('t', 2), Tuple.of('e', 1), Tuple.of('x', 3))),
                List.of(new Leaf('e', 1), new Leaf('t', 2), new Leaf('x', 3))
        );
        assertEquals(
                List.of(
                        new Fork(new Leaf('e', 1),
                                 new Leaf('t', 2),
                                 List.of('e', 't'),
                                 3),
                        new Leaf('x', 4)
                ),
                CodeTree.combine(List.of(
                        new Leaf('e', 1),
                        new Leaf('t', 2),
                        new Leaf('x', 4)
                ))
        );
    }

    @Test
    public void testDecode() {
        assertEquals(CharSeq.of("ab"), t1.decode(t1.encode().apply(CharSeq.of("ab"))));
        //assertEquals(CharSeq.of("huffmanestcool"), CodeTree.decodedSecret());
    }

    @Test
    public void testEncode() {
        assertEquals(
                List.of(0, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1),
                CodeTree.frenchCode.encode().apply(CharSeq.of("huffmanestcool"))
        );
        assertEquals(
                CodeTree.secret,
                CharSeq.of("huffmanestcool").flatMap(CodeTree.codeBits(CodeTree.frenchCode.convert()))
        );
        assertEquals(CodeTree.secret, CodeTree.frenchCode.quickEncode().apply(CharSeq.of("huffmanestcool")));
    }
}
