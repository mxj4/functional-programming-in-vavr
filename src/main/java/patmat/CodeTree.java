package patmat;

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.CharSeq;
import io.vavr.collection.List;
import io.vavr.collection.Seq;

import java.util.function.Predicate;

import static io.vavr.API.*;
import static io.vavr.Patterns.$Cons;
import static io.vavr.Patterns.$Nil;
import static io.vavr.Patterns.$Tuple2;
import static patmat.CodeTreePatterns.*;

/**
 * Assignment 4: Huffman coding
 *
 * A huffman code is represented by a binary tree.
 *
 * Every `Leaf` node of the tree represents one character of the alphabet that the tree can encode.
 * The weight of a `Leaf` is the frequency of appearance of the character.
 *
 * The branches of the huffman tree, the `Fork` nodes, represent a set containing all the characters
 * present in the leaves below it. The weight of a `Fork` node is the sum of the weights of these
 * leaves.
 */
public interface CodeTree {
    final class Fork implements CodeTree {
        public CodeTree left;
        public CodeTree right;
        public Seq<Character> characters;
        public Integer weight;

        public Fork(CodeTree left, CodeTree right, Seq<Character> characters, Integer weight) {
            this.left = left;
            this.right = right;
            this.characters = characters;
            this.weight = weight;
        }

        public boolean equals(Object other) {
            if (this == other) return true;
            if (other instanceof Fork) {
                Fork o = (Fork) other;
                return this.left.equals(o.left) &&
                        this.right.equals(o.right) &&
                        this.characters.eq(o.characters) &&
                        this.weight.equals(o.weight);
            }
            return false;
        }
    }

    final class Leaf implements CodeTree {
        public Character character;
        public Integer weight;

        public Leaf(Character character, Integer weight) {
            this.character = character;
            this.weight = weight;
        }

        public boolean equals(Object other) {
            if (this == other) return true;
            if (other instanceof Leaf) {
                Leaf o = (Leaf) other;
                return this.character.equals(o.character) &&
                        this.weight.equals(o.weight);
            }
            return false;
        }
    }

    // Part 1: Basics

    default Integer weight() {
        return Match(this).of(
                Case($Fork($(), $(), $(), $()),
                        (l, r, c, weight) -> weight),
                Case($Leaf($(), $()),
                        (c, weight) -> weight)
        );
    }

    default Seq<Character> characters() {
        return Match(this).of(
                Case($Fork($(), $(), $(), $()),
                        (l, r, characters, w) -> characters),
                Case($Leaf($(), $()),
                        (character, w) -> CharSeq.of(character))
        );
    }

    static Fork makeCodeTree(CodeTree left, CodeTree right) {
        return new Fork(
                left, right,
                left.characters().appendAll(right.characters()),
                left.weight() + right.weight()
        );
    }

    // Part 2: Generating Huffman trees

    /**
     * In this assignment, we are working with lists of characters. This function allows
     * you to easily create a character list from a given string.
     */
    static Seq<Character> string2Chars(String str) {
        return CharSeq.of(str);
    }

    /**
     * This function computes for each unique character in the list `chars` the number of
     * times it occurs. For example, the invocation
     *
     *   times(List('a', 'b', 'a'))
     *
     * should return the following (the order of the resulting list is not important):
     *
     *   List(('a', 2), ('b', 1))
     *
     * The type `List[(Char, Int)]` denotes a list of pairs, where each pair consists of a
     * character and an integer. Pairs can be constructed easily using parentheses:
     *
     *   val pair: (Char, Int) = ('c', 1)
     *
     * In order to access the two elements of a pair, you can use the accessors `_1` and `_2`:
     *
     *   val theChar = pair._1
     *   val theInt  = pair._2
     *
     * Another way to deconstruct a pair is using pattern matching:
     *
     *   pair match {
     *     case (theChar, theInt) =>
     *       println("character is: "+ theChar)
     *       println("integer is  : "+ theInt)
     *   }
     */
    static Seq<Tuple2<Character, Integer>> times(Seq<Character> characters) {
        return characters.groupBy(x -> x).map(x -> Tuple.of(x._1, x._2.size()));
    }

    /**
     * Returns a list of `Leaf` nodes for a given frequency table `freqs`.
     *
     * The returned list should be ordered by ascending weights (i.e. the
     * head of the list should have the smallest weight), where the weight
     * of a leaf is the frequency of the character.
     */
    static Seq<Leaf> makeOrderedLeafList(Seq<Tuple2<Character, Integer>> freqs) {
        return freqs.sortBy(x -> x._2).map(x -> new Leaf(x._1, x._2));
    }

    /**
     * Checks whether the list `trees` contains only one single code tree.
     */
    static Boolean singleton(Seq<CodeTree> trees) {
        return trees.size() == 1;
    }

    /**
     * The parameter `trees` of this function is a list of code trees ordered
     * by ascending weights.
     *
     * This function takes the first two elements of the list `trees` and combines
     * them into a single `Fork` node. This node is then added back into the
     * remaining elements of `trees` at a position such that the ordering by weights
     * is preserved.
     *
     * If `trees` is a list of less than two elements, that list should be returned
     * unchanged.
     */
    static Seq<CodeTree> combine(Seq<CodeTree> trees) {
        if (trees .size() < 2) return trees;
        return trees.drop(2).append(makeCodeTree(trees.get(0), trees.get(1))).sortBy(x -> x.weight());
    }

    /**
     * This function will be called in the following way:
     *
     *   until(singleton, combine)(trees)
     *
     * where `trees` is of type `List[CodeTree]`, `singleton` and `combine` refer to
     * the two functions defined above.
     *
     * In such an invocation, `until` should call the two functions until the list of
     * code trees contains only one single tree, and then return that singleton list.
     *
     * Hint: before writing the implementation,
     *  - start by defining the parameter types such that the above example invocation
     *    is valid. The parameter types of `until` should match the argument types of
     *    the example invocation. Also define the return type of the `until` function.
     *  - try to find sensible parameter names for `xxx`, `yyy` and `zzz`.
     */
    static Function1<Seq<CodeTree>, Seq<CodeTree>> until(
            Predicate<Seq<CodeTree>> condition, Function1<Seq<CodeTree>, Seq<CodeTree>> action
    ) {
        return trees -> {
            if (condition.test(trees)) return trees;
            return until(condition, action).apply(action.apply(trees));
        };
    }

    /**
     * This function creates a code tree which is optimal to encode the text `characters`.
     *
     * The parameter `characters` is an arbitrary text. This function extracts the character
     * frequencies from that text and creates a code tree based on them.
     */
    static CodeTree createCodeTree(Seq<Character> characters) {
        return until(x -> singleton(x), x -> combine(x))
                .apply(makeOrderedLeafList(times(characters)).map(x -> x))
                .single();
    }

    // Part 3: Decoding

    // a bit is an Integer

    /**
     * This function decodes the bit sequence `bits` using the code tree `tree` and returns
     * the resulting list of characters.
     */
    default Seq<Character> decode(Seq<Integer> bits) {
        return decodeRec(this, bits, CharSeq.of());
    }

    default Seq<Character> decodeRec(CodeTree subTree, Seq<Integer> remaining, Seq<Character> acc) {
        return Match(Tuple.of(subTree, remaining)).of(
                Case($Tuple2($Leaf($(), $()), $(rest -> rest.isEmpty())),
                        (leaf, rest) -> acc.append(leaf.character)),
                Case($Tuple2($Leaf($(), $()), $(rest -> !rest.isEmpty())),
                        (leaf, rest) -> decodeRec(this, rest, acc.append(leaf.character))),
                Case($Tuple2($Fork($(), $(), $(), $()), $(rest -> rest.head() == 0)),
                        (fork, rest) -> decodeRec(fork.left, rest.tail(), acc)),
                Case($Tuple2($Fork($(), $(), $(), $()), $(rest -> rest.head() == 1)),
                        (fork, rest) -> decodeRec(fork.right, rest.tail(), acc))
        );
    }

    /**
     * A Huffman coding tree for the French language.
     * Generated from the data given at
     *   http://fr.wikipedia.org/wiki/Fr%C3%A9quence_d%27apparition_des_lettres_en_fran%C3%A7ais
     */
    CodeTree frenchCode = new Fork(new Fork(new Fork(new Leaf('s', 121895), new Fork(new Leaf('d', 56269), new Fork(new Fork(new Fork(new Leaf('x', 5928), new Leaf('j', 8351), CharSeq.of('x', 'j'), 14279), new Leaf('f', 16351), CharSeq.of('x', 'j', 'f'), 30630), new Fork(new Fork(new Fork(new Fork(new Leaf('z', 2093), new Fork(new Leaf('k', 745), new Leaf('w', 1747), CharSeq.of('k', 'w'), 2492), CharSeq.of('z', 'k', 'w'), 4585), new Leaf('y', 4725), CharSeq.of('z', 'k', 'w', 'y'), 9310), new Leaf('h', 11298), CharSeq.of('z', 'k', 'w', 'y', 'h'), 20608), new Leaf('q', 20889), CharSeq.of('z', 'k', 'w', 'y', 'h', 'q'), 41497), CharSeq.of('x', 'j', 'f', 'z', 'k', 'w', 'y', 'h', 'q'), 72127), CharSeq.of('d', 'x', 'j', 'f', 'z', 'k', 'w', 'y', 'h', 'q'), 128396), CharSeq.of('s', 'd', 'x', 'j', 'f', 'z', 'k', 'w', 'y', 'h', 'q'), 250291), new Fork(new Fork(new Leaf('o', 82762), new Leaf('l', 83668), CharSeq.of('o', 'l'), 166430), new Fork(new Fork(new Leaf('m', 45521), new Leaf('p', 46335), CharSeq.of('m', 'p'), 91856), new Leaf('u', 96785), CharSeq.of('m', 'p', 'u'), 188641), CharSeq.of('o', 'l', 'm', 'p', 'u'), 355071), CharSeq.of('s', 'd', 'x', 'j', 'f', 'z', 'k', 'w', 'y', 'h', 'q', 'o', 'l', 'm', 'p', 'u'), 605362), new Fork(new Fork(new Fork(new Leaf('r', 100500), new Fork(new Leaf('c', 50003), new Fork(new Leaf('v', 24975), new Fork(new Leaf('g', 13288), new Leaf('b', 13822), CharSeq.of('g', 'b'), 27110), CharSeq.of('v', 'g', 'b'), 52085), CharSeq.of('c', 'v', 'g', 'b'), 102088), CharSeq.of('r', 'c', 'v', 'g', 'b'), 202588), new Fork(new Leaf('n', 108812), new Leaf('t', 111103), CharSeq.of('n', 't'), 219915), CharSeq.of('r', 'c', 'v', 'g', 'b', 'n', 't'), 422503), new Fork(new Leaf('e', 225947), new Fork(new Leaf('i', 115465), new Leaf('a', 117110), CharSeq.of('i', 'a'), 232575), CharSeq.of('e', 'i', 'a'), 458522), CharSeq.of('r', 'c', 'v', 'g', 'b', 'n', 't', 'e', 'i', 'a'), 881025), CharSeq.of('s', 'd', 'x', 'j', 'f', 'z', 'k', 'w', 'y', 'h', 'q', 'o', 'l', 'm', 'p', 'u', 'r', 'c', 'v', 'g', 'b', 'n', 't', 'e', 'i', 'a'), 1486387);

    /**
     * What does the secret message say? Can you decode it?
     * For the decoding use the `frenchCode' Huffman tree defined above.
     */
    Seq<Integer> secret = List.of(0, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1);

    /**
     * Write a function that returns the decoded secret
     */
    static Seq<Character> decodedSecret() {
        return frenchCode.decode(secret);
    }

    // Part 4a: Encoding using Huffman tree

    /**
     * This function encodes `text` using the code tree `tree`
     * into a sequence of bits.
     */
    default Function1<Seq<Character>, Seq<Integer>> encode() {
        return characters -> characters.flatMap(x -> encodeRec(this, x, List.of()));
    }

    static Seq<Integer> encodeRec(CodeTree tree, Character character, Seq<Integer> acc) {
        return Match(tree).of(
                Case($Leaf($(), $()), (leaf, rest) -> acc),
                Case($Fork($(left -> left.characters().contains(character)), $(), $(), $()),
                        (left, r, c, w) -> encodeRec(left, character, acc.append(0))),
                Case($Fork($(), $(right -> right.characters().contains(character)), $(), $()),
                        (l, right, c, w) -> encodeRec(right, character, acc.append(1)))
        );
    }

    // Part 4b: Encoding using code table

    // CodeTable is a Seq<Tuple2<Character, Seq<Integer>>>

    /**
     * This function returns the bit sequence that represents the character `character` in
     * the code table `table`.
     */
    static Function1<Character, Seq<Integer>> codeBits(Seq<Tuple2<Character, Seq<Integer>>> table) {
        return character -> Match(table).of(
                Case($Nil(), List::empty),
                Case($Cons($Tuple2($(c -> c.equals(character)), $()), $()),
                        (tuple, rest) -> tuple._2),
                Case($Cons($(), $()), (tuple, rest) -> codeBits(rest).apply(character))
        );
    }

    /**
     * Given a code tree, create a code table which contains, for every character in the
     * code tree, the sequence of bits representing that character.
     *
     * Hint: think of a recursive solution: every sub-tree of the code tree `tree` is itself
     * a valid code tree that can be represented as a code table. Using the code tables of the
     * sub-trees, think of how to build the code table for the entire tree.
     */
    default Seq<Tuple2<Character, Seq<Integer>>> convert() {
        return convertRec(this, List.of());
    }

    static Seq<Tuple2<Character, Seq<Integer>>> convertRec(
            CodeTree tree,
            Seq<Integer> code
    ) {
        return Match(tree).of(
                Case($Leaf($(), $()),
                        (character, w) -> List.of((Tuple.of(character, code)))),
                Case($Fork($(), $(), $(), $()),
                        (left, right, c, w) -> mergeCodeTables(
                                convertRec(left, code.append(0)),
                                convertRec(right, code.append(1))
                        ))
        );
    }

    /**
     * This function takes two code tables and merges them into one. Depending on how you
     * use it in the `convert` method above, this merge method might also do some transformations
     * on the two parameter code tables.
     */
    static Seq<Tuple2<Character, Seq<Integer>>> mergeCodeTables(Seq<Tuple2<Character, Seq<Integer>>> a, Seq<Tuple2<Character, Seq<Integer>>>b) {
        return a.appendAll(b);
    }

    /**
     * This function encodes `text` according to the code tree `tree`.
     *
     * To speed up the encoding process, it first converts the code tree to a code table
     * and then uses it to perform the actual encoding.
     */
    default Function1<Seq<Character>, Seq<Integer>> quickEncode() {
        return characters -> characters.flatMap(codeBits(convert()));
    }

}
