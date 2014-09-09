/* Copyright 2012 BuyWay-Services */
package standardNaast.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

/**
 * Description : Utility class for Exceptions.
 *
 * @author BuyWay-Services: DWW<BR> Created on Aug 27, 2012
 */
public class ExceptionUtils {

    /**
     * The UNKNOWN_HOST.
     */
    public static final String UNKNOWN_HOST = "UNKNOWN_HOST";

    /**
     * @return The generated String representing the timestamp and server name.
     */
    public static String getTimestampAndServerName() {
        String hostname = ExceptionUtils.UNKNOWN_HOST;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
            int index = hostname.indexOf(".");
            if (index != -1) {
                hostname = StringUtils.substring(hostname, 0, index);
            }
        } catch (UnknownHostException unknownHost) {
        }
        return MessageFormat
                .format("{0}-{1}", DateUtils.formatDate(new Date(), "yyyy-MM-dd-HH.mm.ss.SSS000"), hostname);
    }
}
