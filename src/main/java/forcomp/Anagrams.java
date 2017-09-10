package forcomp;

import io.vavr.Lazy;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.*;

import static io.vavr.API.*;
import static io.vavr.Patterns.$None;
import static io.vavr.Patterns.$Some;

public class Anagrams {
    /** A word is simply a `String`. */
    /** A sentence is a `List` of words. */
    /**
     * `Occurrences` is a `List` of pairs of characters and positive integers saying
     *  how often the character appears.
     *  This list is sorted alphabetically w.r.t. to the character in each pair.
     *  All characters in the occurrence list are lowercase.
     *
     *  Any list of pairs of lowercase characters and their frequency which is not sorted
     *  is **not** an occurrence list.
     *
     *  Note: If the frequency of some character is zero, then that character should not be
     *  in the list.
     */

    /**
     * The dictionary is simply a sequence of words.
     *  It is predefined and obtained as a sequence using the utility method `loadDictionary`.
     */
    private static Seq<String> dictionary = Dictionary.load();

    /**
     * Converts the word into its character occurence list.
     *
     *  Note: the uppercase and lowercase version of the character are treated as the
     *  same character, and are represented as a lowercase character in the occurrence list.
     */
    public static Seq<Tuple2<Character, Integer>> wordOccurrences(String word) {
        return charSeqOccurrences(CharSeq.of(word));
    }

    public static Seq<Tuple2<Character, Integer>> charSeqOccurrences(CharSeq charSeq) {
        return charSeq.toLowerCase().groupBy(x -> x).map(x -> Tuple.of(x._1, x._2.size())).sortBy(x -> x._1);
    }

    /** Converts a sentence into its character occurrence list. */
    public static Seq<Tuple2<Character, Integer>> sentenceOccurrences(Seq<String> sentence) {
        return charSeqOccurrences(sentence.mkCharSeq());
    }

    /**
     * The `dictionaryByOccurrences` is a `Map` from different occurrences to a sequence of all
     *  the words that have that occurrence count.
     *  This map serves as an easy way to obtain all the anagrams of a word given its occurrence list.
     *
     *  For example, the word "eat" has the following character occurrence list:
     *
     *     `List(('a', 1), ('e', 1), ('t', 1))`
     *
     *  Incidentally, so do the words "ate" and "tea".
     *
     *  This means that the `dictionaryByOccurrences` map will contain an entry:
     *
     *    List(('a', 1), ('e', 1), ('t', 1)) -> Seq("ate", "eat", "tea")
     *
     */
    public static Map<Seq<Tuple2<Character, Integer>>, Seq<String>> dictionaryByOccurrences = Lazy.val(
            () -> dictionary.groupBy(x -> wordOccurrences(x)), Map.class
    );

    /** Returns all the anagrams of a given word. */
    public static Seq<String> wordAnagrams(String word) {
        return dictionaryByOccurrences.getOrElse(wordOccurrences(word), List.empty());
    }

    /**
     * Returns the list of all subsets of the occurrence list.
     *  This includes the occurrence itself, i.e. `List(('k', 1), ('o', 1))`
     *  is a subset of `List(('k', 1), ('o', 1))`.
     *  It also include the empty subset `List()`.
     *
     *  Example: the subsets of the occurrence list `List(('a', 2), ('b', 2))` are:
     *
     *    List(
     *      List(),
     *      List(('a', 1)),
     *      List(('a', 2)),
     *      List(('b', 1)),
     *      List(('a', 1), ('b', 1)),
     *      List(('a', 2), ('b', 1)),
     *      List(('b', 2)),
     *      List(('a', 1), ('b', 2)),
     *      List(('a', 2), ('b', 2))
     *    )
     *
     *  Note that the order of the occurrence list subsets does not matter -- the subsets
     *  in the example above could have been displayed in some other order.
     */
    public static Seq<Seq<Tuple2<Character, Integer>>> combinations(Seq<Tuple2<Character, Integer>> occurrences) {
        if (occurrences.isEmpty()) return List.of(List.empty());
        return combinations(occurrences.tail()).appendAll(
                For(combinations(occurrences.tail()), subset ->
                        For(List.rangeClosed(1, occurrences.head()._2))
                                .yield(x -> subset.prepend(Tuple.of(occurrences.head()._1, x)))
                )
        );
    }

    /**
     * Subtracts occurrence list `y` from occurrence list `x`.
     *
     *  The precondition is that the occurrence list `y` is a subset of
     *  the occurrence list `x` -- any character appearing in `y` must
     *  appear in `x`, and its frequency in `y` must be smaller or equal
     *  than its frequency in `x`.
     *
     *  Note: the resulting value is an occurrence - meaning it is sorted
     *  and has no zero-entries.
     */
    public static Seq<Tuple2<Character, Integer>> subtract(Seq<Tuple2<Character, Integer>> x,
                                                           Seq<Tuple2<Character, Integer>> y) {
        return x.map(i ->
                Match(y.find(t -> t._1.equals(i._1))).of(
                        Case($None(), i),
                        Case($Some($()), t -> Tuple.of(i._1, i._2 - t._2))
                ))
                .filter(t -> t._2 > 0).toList();
    }

    /**
     * Returns a list of all anagram sentences of the given sentence.
     *
     *  An anagram of a sentence is formed by taking the occurrences of all the characters of
     *  all the words in the sentence, and producing all possible combinations of words with those characters,
     *  such that the words have to be from the dictionary.
     *
     *  The number of words in the sentence and its anagrams does not have to correspond.
     *  For example, the sentence `List("I", "love", "you")` is an anagram of the sentence `List("You", "olive")`.
     *
     *  Also, two sentences with the same words but in a different order are considered two different anagrams.
     *  For example, sentences `List("You", "olive")` and `List("olive", "you")` are different anagrams of
     *  `List("I", "love", "you")`.
     *
     *  Here is a full example of a sentence `List("Yes", "man")` and its anagrams for our dictionary:
     *
     *    List(
     *      List(en, as, my),
     *      List(en, my, as),
     *      List(man, yes),
     *      List(men, say),
     *      List(as, en, my),
     *      List(as, my, en),
     *      List(sane, my),
     *      List(Sean, my),
     *      List(my, en, as),
     *      List(my, as, en),
     *      List(my, sane),
     *      List(my, Sean),
     *      List(say, men),
     *      List(yes, man)
     *    )
     *
     *  The different sentences do not have to be output in the order shown above - any order is fine as long as
     *  all the anagrams are there. Every returned word has to exist in the dictionary.
     *
     *  Note: in case that the words of the sentence are in the dictionary, then the sentence is the anagram of itself,
     *  so it has to be returned in this list.
     *
     *  Note: There is only one anagram of an empty sentence.
     */
    public static Seq<Seq<String>> sentenceAnagrams(Seq<String> sentence) {
        return anagramsRec(sentenceOccurrences(sentence)).toList();
    }

    private static Iterator<Seq<String>> anagramsRec(Seq<Tuple2<Character, Integer>> occurrences) {
        if (occurrences.isEmpty()) return Iterator.of(List.empty());
        return For(combinations(occurrences), subset ->
                For(dictionaryByOccurrences.getOrElse(subset, List.empty()), anagram ->
                    For(anagramsRec(subtract(occurrences, subset)))
                            .yield(next -> next.prepend(anagram))
                )
        );
    }
}
