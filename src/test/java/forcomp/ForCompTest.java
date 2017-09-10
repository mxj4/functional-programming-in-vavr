package forcomp;

import io.vavr.Tuple;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ForCompTest {
    @Test
    public void testOccurrences() {
        assertEquals(
                List.of(Tuple.of('a', 1), Tuple.of('b', 1), Tuple.of('c', 1), Tuple.of('d', 1)),
                Anagrams.wordOccurrences("abcd")
        );
        assertEquals(
                List.of(Tuple.of('b', 1), Tuple.of('e', 1), Tuple.of('o', 1), Tuple.of('r', 2), Tuple.of('t', 1)),
                Anagrams.wordOccurrences("Robert")
        );
        assertEquals(
                List.of(Tuple.of('a', 1), Tuple.of('b', 1), Tuple.of('c', 1), Tuple.of('d', 1), Tuple.of('e', 1)),
                Anagrams.sentenceOccurrences(List.of("abcd", "e"))
        );
        assertEquals(
                Option.some(HashSet.of("ate", "eat", "tea")),
                Anagrams.dictionaryByOccurrences.get(List.of(Tuple.of('a', 1), Tuple.of('e', 1), Tuple.of('t', 1))).map(Seq::toSet)
        );
    }

    @Test
    public void testWordAnagrams() {
        assertEquals(HashSet.of("married", "admirer"), Anagrams.wordAnagrams("married").toSet());
        assertEquals(HashSet.of("parley", "pearly", "player", "replay"), Anagrams.wordAnagrams("player").toSet());
    }

    @Test
    public void testSentenceAnagrams() {
        assertEquals(
                List.of(Tuple.of('a', 1), Tuple.of('d', 1), Tuple.of('l', 1)),
                Anagrams.subtract(
                        List.of(Tuple.of('a', 1), Tuple.of('d', 1), Tuple.of('l', 1), Tuple.of('r', 1)),
                        List.of(Tuple.of('r', 1))
                )
        );
        assertEquals(
                List.of(Tuple.of('e', 1),
                        Tuple.of('i', 1),
                        Tuple.of('l', 2),
                        Tuple.of('r', 1),
                        Tuple.of('u', 1),
                        Tuple.of('x', 1),
                        Tuple.of('z', 1)),
                Anagrams.subtract(
                        List.of(Tuple.of('e', 1),
                                Tuple.of('i', 1),
                                Tuple.of('l', 2),
                                Tuple.of('n', 1),
                                Tuple.of('r', 1),
                                Tuple.of('u', 2),
                                Tuple.of('x', 1),
                                Tuple.of('z', 1)),
                        List.of(Tuple.of('n', 1),
                                Tuple.of('u', 1))
                )
        );
        assertEquals(List.of(List.empty()), Anagrams.combinations(List.empty()));

        System.out.println(Anagrams.sentenceOccurrences(List.of("I", "Love", "you")).mkString());
        assertEquals(
                HashSet.of(
                        List.empty(),
                        List.of(Tuple.of('a', 1)),
                        List.of(Tuple.of('a', 2)),
                        List.of(Tuple.of('b', 1)),
                        List.of(Tuple.of('a', 1), Tuple.of('b', 1)),
                        List.of(Tuple.of('a', 2), Tuple.of('b', 1)),
                        List.of(Tuple.of('b', 2)),
                        List.of(Tuple.of('a', 1), Tuple.of('b', 2)),
                        List.of(Tuple.of('a', 2), Tuple.of('b', 2))
                ),
                Anagrams.combinations(List.of(Tuple.of('a', 2), Tuple.of('b', 2))).toSet()
        );

        assertEquals(List.of(List.empty()), Anagrams.sentenceAnagrams(List.empty()));
        assertEquals(
                HashSet.of(
                        List.of("Rex", "Lin", "Zulu"),
                        List.of("nil", "Zulu", "Rex"),
                        List.of("Rex", "nil", "Zulu"),
                        List.of("Zulu", "Rex", "Lin"),
                        List.of("null", "Uzi", "Rex"),
                        List.of("Rex", "Zulu", "Lin"),
                        List.of("Uzi", "null", "Rex"),
                        List.of("Rex", "null", "Uzi"),
                        List.of("null", "Rex", "Uzi"),
                        List.of("Lin", "Rex", "Zulu"),
                        List.of("nil", "Rex", "Zulu"),
                        List.of("Rex", "Uzi", "null"),
                        List.of("Rex", "Zulu", "nil"),
                        List.of("Zulu", "Rex", "nil"),
                        List.of("Zulu", "Lin", "Rex"),
                        List.of("Lin", "Zulu", "Rex"),
                        List.of("Uzi", "Rex", "null"),
                        List.of("Zulu", "nil", "Rex"),
                        List.of("rulez", "Linux"),
                        List.of("Linux", "rulez")
                ),
                Anagrams.sentenceAnagrams(List.of("Linux", "rulez")).toSet()
        );
    }
}
