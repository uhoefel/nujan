package eu.hoefel.nujan.netcdf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

import eu.hoefel.nujan.hdf.HdfException;

final class NhUtil {
    
    /**
     * Do not use: for internal testing only by Thdfa, GenData, and Tnetcdfa to
     * force character generation.
     */
    static final int DTYPE_TEST_CHAR = 8; // for internal test only

    public static long parseUtcTime(String stg) throws HdfException {
        long utcTime = 0;
        if (stg.equals("0")) {
            utcTime = 0;
        } else {
            SimpleDateFormat utcSdf = null;
            if (stg.length() == 10) {// yyyy-mm-dd
                utcSdf = new SimpleDateFormat("yyyy-MM-dd");
            } else if (stg.length() == 19) { // yyyy-MM-ddTHH:mm:ss
                utcSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            } else {
                throw new HdfException("invalid -utcModTime: \"" + stg + "\"");
            }

            utcSdf.setTimeZone(new SimpleTimeZone(0, "UTC"));
            Date dt = null;
            try {
                dt = utcSdf.parse(stg);
            } catch (ParseException exc) {
                throw new HdfException("invalid -utcModTime: \"" + stg + "\"");
            }
            utcTime = dt.getTime();
        }
        return utcTime;
    }
    
    /**
     * Formats a general Object by recursively examining it if it's an array. Calls
     * formatObjectSub to do the real work.
     */
    public static String formatObject(Object obj) {
        StringBuilder sbuf = new StringBuilder();
        if (obj == null)
            sbuf.append("  (null)");
        else {
            sbuf.append("  cls: " + obj.getClass().getName() + "\n");
            formatObjectSub(obj, 2, sbuf);
        }
        return sbuf.toString();
    }

    /**
     * Formats a general Object by recursively examining it if it's an array.
     */
    static void formatObjectSub(Object obj, int indent, StringBuilder sbuf) {
        if (obj == null)
            sbuf.append(String.format("%s(null)\n", mkIndent(indent)));
        else if (obj instanceof String str) {
            sbuf.append(String.format("%s(String) \"%s\"\n", mkIndent(indent), str));
        } else if (obj instanceof byte[] vals) {
            sbuf.append(mkIndent(indent) + "(bytes)");
            for (int i = 0; i < vals.length; i++) {
                sbuf.append("  " + vals[i]);
            }
            sbuf.append("\n");
        } else if (obj instanceof short[] vals) {
            sbuf.append(mkIndent(indent) + "(shorts)");
            for (int i = 0; i < vals.length; i++) {
                sbuf.append("  " + vals[i]);
            }
            sbuf.append("\n");
        } else if (obj instanceof int[] vals) {
            sbuf.append(mkIndent(indent) + "(ints)");
            for (int i = 0; i < vals.length; i++) {
                sbuf.append("  " + vals[i]);
            }
            sbuf.append("\n");
        } else if (obj instanceof long[] vals) {
            sbuf.append(mkIndent(indent) + "(longs)");
            for (int i = 0; i < vals.length; i++) {
                sbuf.append("  " + vals[i]);
            }
            sbuf.append("\n");
        } else if (obj instanceof float[] vals) {
            sbuf.append(mkIndent(indent) + "(floats)");
            for (int i = 0; i < vals.length; i++) {
                sbuf.append("  " + vals[i]);
            }
            sbuf.append("\n");
        } else if (obj instanceof double[] vals) {
            sbuf.append(mkIndent(indent) + "(doubles)");
            for (int i = 0; i < vals.length; i++) {
                sbuf.append("  " + vals[i]);
            }
            sbuf.append("\n");
        } else if (obj instanceof char[] vals) {
            sbuf.append(mkIndent(indent) + "(chars)");
            for (int i = 0; i < vals.length; i++) {
                sbuf.append("  " + vals[i]);
            }
            sbuf.append("\n");
        } else if (obj instanceof Object[] vals) {
            for (int i = 0; i < vals.length; i++) {
                sbuf.append(String.format("%s%d  cls: %s:\n", mkIndent(indent), i, vals[i].getClass().getName()));
                formatObjectSub(vals[i], indent + 1, sbuf);
            }
        } else {
            sbuf.append(String.format("%s(%s) %s\n", mkIndent(indent), obj.getClass().getName(), obj));
        }
    }

    /** Formats an array of ints. */
    public static String formatInts(int[] vals) {
        String res = "";
        if (vals == null)
            res = "(null)";
        else {
            res = "[";
            for (int i = 0; i < vals.length; i++) {
                if (i > 0) {
                    res += " ";
                }
                res += vals[i];
            }
            res += "]";
        }
        return res;
    }

    /** Returns an indentation string having length 2*indent. */
    static String mkIndent(int indent) {
        String res = "";
        for (int i = 0; i < indent; i++) {
            res += "  ";
        }
        return res;
    }

    public static String formatUtcTime(long tval) {
        SimpleDateFormat utcSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        utcSdf.setTimeZone(new SimpleTimeZone(0, "UTC"));
        return utcSdf.format(tval);
    }
}
