package patmat;

import io.vavr.API.Match.Pattern;
import io.vavr.API.Match.Pattern2;
import io.vavr.API.Match.Pattern4;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple4;
import io.vavr.collection.*;


public class CodeTreePatterns {
    static Tuple4<CodeTree, CodeTree, Seq<Character>, Integer> Fork(CodeTree.Fork fork) {
        return Tuple.of(fork.left, fork.right, fork.characters, fork.weight);
    }

    public static <_1 extends CodeTree, _2 extends CodeTree, _3 extends Seq<Character>, _4 extends Integer>
    Pattern4<CodeTree.Fork, _1, _2, _3, _4> $Fork(Pattern<_1, ?> p1, Pattern<_2, ?> p2, Pattern<_3, ?> p3, Pattern<_4, ?> p4) {
        return Pattern4.of(CodeTree.Fork.class, p1, p2, p3, p4, CodeTreePatterns::Fork);
    }

    static Tuple2<Character, Integer> Leaf(CodeTree.Leaf leaf) {
        return Tuple.of(leaf.character, leaf.weight);
    }

    public static <_1 extends Character, _2 extends Integer>
    Pattern2<CodeTree.Leaf, _1, _2> $Leaf(Pattern<_1, ?> p1, Pattern<_2, ?> p2) {
        return Pattern2.of(CodeTree.Leaf.class, p1, p2, CodeTreePatterns::Leaf);
    }
}
