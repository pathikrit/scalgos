public class TempBigPrecision {



    /*
     * Temporary solution to increase precision for +,-,* (not /) to 30 decimal digits (100 bits)
     * Do calculations in both double & longs
     * double (first 13 digits correct) and longs (last 18 digits correct because calculation is mod 2^64)
     * When the digits on the border of long and double are ..99999... or ...00000..., it may fail (e.g. 10^30 fails)
     */
    public static String operate(long a, long b, char operator) {
        double x;
        long y;

        switch(operator) {
            case '+': x = 1.0*a+b; y = a+b; break;
            case '-': x = 1.0*a-b; y = a-b; break;
            case '*': x = 1.0*a*b; y = a*b; break;
            default: throw new IllegalArgumentException("Operator must be + or - or * only (no division): " + operator);
        }

        final int L = 18;
        String sx = String.format("%.0f", x);
        if (sx.length() <= L)
            return "" + y;
        sx = sx.substring(0, sx.length()-L);
        long sl = 0;
        for (int i = 0; i < sx.length(); i++)
            sl = 10*sl + sx.charAt(i) - '0';
        for (int i = 0; i < L; i++)
            sl *= 10;
        return sx + String.format("%0" + L + "d", y - sl);
    }
}
