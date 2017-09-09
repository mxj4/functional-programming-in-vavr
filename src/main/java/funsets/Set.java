package funsets;

import io.vavr.Function1;
import io.vavr.collection.Stream;

import java.util.function.Predicate;

/**
 * 2. Purely Functional Sets.
 *
 * We represent a set by its characteristic function, i.e.
 * its `contains` predicate.
 */
interface Set extends Function1<Integer, Boolean> {
    /**
     * Indicates whether a set contains a given element.
     */
    default Boolean contains(Integer elem) {
        return this.apply(elem);
    }

    /**
     * Returns the set of the one given element.
     */
    static Set singletonSet(Integer elem) {
        return x -> elem.equals(x);
    }

    /**
     * Returns the union of the two given sets,
     * the sets of all elements that are in either `this` or `t`.
     */
    default Set union(Set t) {
        return x -> this.contains(x) || t.contains(x);
    }

    /**
     * Returns the intersection of the two given sets,
     * the set of all elements that are both in `this` or `t`.
     */
    default Set intersect(Set t) {
        return x -> this.contains(x) && t.contains(x);
    }

    /**
     * Returns the difference of the two given sets,
     * the set of all elements of `this` that are not in `t`.
     */
    default Set diff(Set t) {
        return x -> this.contains(x) && !t.contains(x);
    }

    /**
     * Returns the subset of `this` for which `p` holds.
     */
    default Set filter(Predicate<Integer> p) {
        return x -> this.contains(x) && p.test(x);
    }

    /**
     * The bounds for `forall` and `exists` are +/- 1000.
     */
    int bound = 1000;

    /**
     * Returns whether all bounded integers within `this` satisfy `p`.
     */
    default Boolean forall(Predicate<Integer> p) {
        return iter(-bound, p);
    }

    /**
     * Helper function for `forall`
     */
    default Boolean iter(Integer a, Predicate<Integer> p) {
        if (a > bound) return true;
        else if (this.contains(a)) return p.test(a) && iter(a + 1, p);
        else return iter(a + 1, p);
    }

    /**
     * Returns whether there exists a bounded integer within `this`
     * that satisfies `p`.
     */
    default Boolean exists(Predicate<Integer> p) {
        return !forall(p.negate());
    }

    /**
     * Returns a set transformed by applying `f` to each element of `this`.
     */
    default Set map(Function1<Integer, Integer> f) {
        return y -> this.exists(x -> y.equals(f.apply(x)));
    }

    /**
     * Displays the contents of this set
     */
    default String asString() {
        return Stream.rangeClosed(-bound, bound).filter(x -> this.contains(x)).mkString("{", ",", "}");
    }

    /**
     * Prints the contents of a set on the console.
     */
    default void printSet() {
        System.out.println(this.asString());
    }
}