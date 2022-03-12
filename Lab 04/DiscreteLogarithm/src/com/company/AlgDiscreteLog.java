package com.company;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AlgDiscreteLog {
    abstract BigInteger exec(final BigInteger p, final BigInteger a,
                       final BigInteger r, final BigInteger b,
                       final Function<BigInteger, BigInteger> f);

    protected final Logger logger;
    public AlgDiscreteLog(Logger logger) {
        this.logger = logger;
    }
}

class PollardAlgDiscreteLog extends AlgDiscreteLog {

    public PollardAlgDiscreteLog(Logger logger) {
        super(logger);
    }

    @Override
    public BigInteger exec(final BigInteger p, final BigInteger a,
                     final BigInteger r, final BigInteger b,
                     final Function<BigInteger, BigInteger> f) {
        if (!a.modPow(r, p).equals(BigInteger.ONE)) {
            throw new IllegalArgumentException();
        }

        final BigInteger halfP = p.divide(BigInteger.TWO);

        final BigInteger u = new BigInteger("2");
        final BigInteger v = new BigInteger("2");

        BigInteger c = a.modPow(u, p).multiply(b.modPow(v, p)).mod(p);
        BigInteger d = new BigInteger(String.valueOf(c));

        BigInteger logC = new BigInteger(String.valueOf(u));
        BigInteger logD = new BigInteger(String.valueOf(u));
        BigInteger logCX = new BigInteger(String.valueOf(v));
        BigInteger logDX = new BigInteger(String.valueOf(v));

        for (int i = 0; ; ++i) {
            logger.log(Level.INFO,
                    String.format("c=%s, d=%s, LOGaC=%s+%sx, LOGaD=%s+%sx",
                            c, d, logC.toString(), logCX.toString(),
                            logD.toString(), logDX.toString())
            );

            if (halfP.compareTo(c) > 0) {
                logC = logC.add(BigInteger.ONE);
            } else {
                logCX = logCX.add(BigInteger.ONE);
            }

            if (halfP.compareTo(d) > 0) {
                logD = logD.add(BigInteger.ONE);
            } else {
                logDX = logDX.add(BigInteger.ONE);
            }

            if (halfP.compareTo(f.apply(d)) > 0) {
                logD = logD.add(BigInteger.ONE);
            } else {
                logDX = logDX.add(BigInteger.ONE);
            }

            c = f.apply(c);
            d = f.apply(f.apply(d));

            if (c.mod(p).equals(d.mod(p))) {
                logger.log(Level.INFO,
                        String.format("c=%s, d=%s, LOGaC=%s+%sx, LOGaD=%s+%sx",
                                c, d, logC.toString(), logCX.toString(),
                                logD.toString(), logDX.toString())
                );

                break;
            }
        }


        // Solve the compare
        BigInteger compA = logCX.subtract(logDX).mod(r);
        BigInteger compB = logD.subtract(logC).mod(r);

        logger.log(Level.INFO, String.format("[COMPARE] %sx=%s (mod %s)",
                compA.toString(), compB.toString(), r.toString()));

        while (compB.mod(compA).compareTo(BigInteger.ZERO) != 0) {
            compB = compB.add(r);
        }

        return compB.divide(compA);
    }
}

class GelfondAlgDiscreteLog extends AlgDiscreteLog {

    public GelfondAlgDiscreteLog(Logger logger) {
        super(logger);
    }

    @Override
    BigInteger exec(BigInteger p, BigInteger a,
              BigInteger r, BigInteger b,
              Function<BigInteger, BigInteger> f) {

//        if (!a.modPow(r, p).equals(BigInteger.ONE)) {
//            throw new IllegalArgumentException();
//        }

        final BigInteger m = r.sqrt().add(BigInteger.ONE);

        logger.log(Level.INFO, "s=" + m);

        BigInteger e = BigInteger.ONE;

        Map <BigInteger, BigInteger> map = new HashMap<>();

        for (BigInteger i = BigInteger.ZERO; m.compareTo(i) > 0; i = i.add(BigInteger.ONE)) {
            map.put(e, i);

            logger.log(Level.INFO, "key=" + e + ", value=" + i);

            e = e.multiply(a).mod(p);
        }

        final BigInteger factor = a.modPow(p.subtract(m).subtract(BigInteger.ONE), p);
        e = b;

        for (BigInteger i = BigInteger.ZERO; m.compareTo(i) > 0; i = i.add(BigInteger.ONE)) {
            BigInteger value = map.get(e);
            e = e.multiply(factor).mod(p);

            if (value != null) {
                return m.multiply(i).add(value);
            }
        }

        return null;
    }
}

class DecompositionBaseAlgDiscreteLog extends AlgDiscreteLog {

    public DecompositionBaseAlgDiscreteLog(Logger logger) {
        super(logger);
    }

    private ArrayList<BigInteger> base;

    private void initBase(int size) {
        base.add(BigInteger.valueOf(5));
        logger.log(Level.INFO, String.format("[base] %s", BigInteger.valueOf(5)));

        for (int i = 1; i < size; ++i) {
            BigInteger t = base.get(i - 1);

            do {
                t = t.add(BigInteger.TWO);
            } while (!isPrime(t));

            logger.log(Level.INFO, String.format("[base] %s", t));

            base.add(t);
        }
    }

    private boolean isPrime(BigInteger num) {
        for (BigInteger i = BigInteger.TWO;
             num.divide(BigInteger.TWO).compareTo(i) > 0;
             i = i.add(BigInteger.ONE)) {

            BigInteger temp = num.mod(i);
            if (BigInteger.ZERO.equals(temp)) {
                return false;
            }
        }

        return true;
    }

    @Override
    BigInteger exec(BigInteger p, BigInteger a,
                    BigInteger r, BigInteger b,
                    Function<BigInteger, BigInteger> f) {

        int lnP = (int) Math.log(Double.parseDouble(p.toString()));
        int baseLen = (int) Math.sqrt(Math.exp(2 * Math.sqrt(lnP * Math.log(lnP))));

        logger.log(Level.INFO, String.format("[base size] %s", baseLen));

        base = new ArrayList<>(baseLen);
        initBase(baseLen);

        for (int i = 0; i < base.size(); ++i) {
            BigInteger u, bi;

            do {
                u = BigInteger.valueOf((long) (Math.random() * 100));
                bi = a.modPow(u, p);
            } while (!isSmooth(bi));

            logger.log(Level.INFO, String.format("u=%s, a^u=%s", u, bi));
        }

        BigInteger v = BigInteger.ZERO;
        do {
            v = v.add(BigInteger.ONE);
        } while (!isSmooth(b.modPow(v, p)));

        logger.log(Level.INFO, String.format("v=%s", v));

        return new BigInteger("0");
    }

    private boolean isSmooth(BigInteger n) {
        for (BigInteger p : base) {
            while (!p.equals(BigInteger.valueOf(-1)) && n.mod(p).equals(BigInteger.ZERO)) {
                n = n.divide(p);
            }
        }

        return BigInteger.ONE.equals(n);
    }
}
