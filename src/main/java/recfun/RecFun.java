package recfun;

import io.vavr.collection.*;

// Recursion
public class RecFun {
    // ex1
    public static Integer pascal(Integer c, Integer r) {
        if (r == 0 || c == 0 || c == r) return 1;
        return pascal(c-1, r-1) + pascal(c, r-1);
    }

    // ex2
    public static Boolean balance(CharSeq chars) {
        return balanceRec(chars, 0) == 0;
    }

    private static Integer balanceRec(CharSeq chars, Integer acc) {
        if (chars.isEmpty() || acc < 0) return acc;
        if (chars.head() == '(') return balanceRec(chars.tail(), acc + 1);
        if (chars.head() == ')') return balanceRec(chars.tail(), acc -1);
        return balanceRec(chars.tail(), acc);
    }

    // ex3
    public static Integer countChange(Integer money, Seq<Integer> coins) {
        if (money == 0) return 1;
        if (coins.isEmpty()) return 0;
        if (money < 0) return 0;
        return countChange(money, coins.tail()) +
                countChange(money - coins.head(), coins);
    }
}
