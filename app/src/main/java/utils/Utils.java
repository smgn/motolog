package utils;

import android.text.TextUtils;

public class Utils {
    public static String formatDate(String date) {
        String[] splitArray = date.split(" ");

        if (splitArray.length == 3) {
            return TextUtils.join("-", splitArray);
        } else {
            return date; // should never happen
        }
    }
}
