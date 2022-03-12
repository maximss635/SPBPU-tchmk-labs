package com.company;

import java.io.IOException;
import java.math.BigInteger;
import java.util.function.Function;
import java.util.logging.*;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private static final BigInteger a = new BigInteger("10");
    private static final BigInteger b = new BigInteger("83");

    private static final BigInteger p = new BigInteger("107");
    private static final BigInteger q = new BigInteger("53");

    private static final BigInteger halfP = p.divide(BigInteger.TWO);

    private static final Function<BigInteger, BigInteger> f =
            (BigInteger c) -> { return halfP.compareTo(c) > 0 ?
                    a.multiply(c).mod(p) : b.multiply(c).mod(p); };


    public static void main(String[] args) throws IOException {

        Handler handler = new ConsoleHandler();
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord logRecord) {
                if (logRecord.getLevel() == Level.INFO) {
                    return String.format("[%d] %s\n", logRecord.getSequenceNumber(), logRecord.getMessage());
                }
                else {
                    return String.format("[ANSWER] %s\n", logRecord.getMessage());
                }
            }
        });
        logger.addHandler(handler);


        try {
            AlgDiscreteLog algorithm = new DecompositionBaseAlgDiscreteLog(logger);

            BigInteger answer = algorithm.exec(p, a, q, b, f);

            logger.log(Level.INFO, "[RESULT] " + answer.toString());

        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Wrong arguments");
        }
    }


}
