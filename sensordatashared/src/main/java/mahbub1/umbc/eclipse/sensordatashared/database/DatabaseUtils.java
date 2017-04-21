package mahbub1.umbc.eclipse.sensordatashared.database;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by mahbub on 2/3/17.
 */

public final class DatabaseUtils {
    public static final String AUTHORITY = "edu.eclipse.umbc";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd", Locale.US);
    /** The scheme part for this provider's URI. */
    private static final String SCHEME = "content://";


}
