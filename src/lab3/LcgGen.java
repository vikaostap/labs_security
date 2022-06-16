package lab3;

import java.math.BigInteger;

public class LcgGen {
    public long a;
    public long c;
    public int _last;


    public int nextNumber() {
        long next = a * _last + c;
        _last = (int)next;
        return _last;
    }

    public void crack(long x0, long x1, long x2) {
        BigInteger m = BigInteger.TWO.pow(32);

        BigInteger X0 = BigInteger.valueOf(x0);
        BigInteger X1 = BigInteger.valueOf(x1);
        BigInteger X2 = BigInteger.valueOf(x2);


        BigInteger d21 = X2.subtract(X1);

        BigInteger d10 = X1.subtract(X0);
        BigInteger d10Inverse = d10.modInverse(m);
        BigInteger A = d21.multiply(d10Inverse);
        while (A.compareTo(BigInteger.ZERO) < 0) {
            A = A.add(m);
        }
        A = A.mod(m);

        BigInteger C = X1.subtract((A.multiply(X0)));
        while (C.compareTo(BigInteger.ZERO) < 0) {
            C = C.add(m);
        }
        C = C.mod(m);

        // set params
        a = A.longValue();
        c = C.longValue();
        _last = (int)x2;
    }
}
