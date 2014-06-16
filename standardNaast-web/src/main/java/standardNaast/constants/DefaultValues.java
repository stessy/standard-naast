/* Copyright 2012 BuyWay-Services */
package standardNaast.constants;

import java.math.BigDecimal;
import java.util.Date;
import org.joda.time.DateTime;

/**
 * Description : Default value constants.
 *
 * @author BuyWay-Services: DWW<BR> Created on 4 oct. 2011
 */
public interface DefaultValues {

    /**
     * The PHONE_NUMBER_LENGTH.
     */
    public static final int PHONE_NUMBER_LENGTH = 15;
    /**
     * The POSTAL_CODE_LENGTH.
     */
    public static final int POSTAL_CODE_LENGTH = 6;
    /**
     * The CITY_MAX_LENGTH.
     */
    public static final int CITY_MAX_LENGTH = 60;
    /**
     * The NAME_MAX_LENGTH.
     */
    public static final int NAME_MAX_LENGTH = 28;
    /**
     * The AGE_FOR_MAJORITY.
     */
    public static final int AGE_FOR_MAJORITY = 18;
    /**
     * The OFFER_VALIDITY_DAYS.
     */
    public static final int OFFER_VALIDITY_DAYS = 20;
    /**
     * The default charset in Phenix.
     */
    public static final String CHARSET = "UTF-8";

    /**
     * Description : Commonly used default dates.
     *
     * @author BuyWay-Services: DWW<BR> Created on 4 oct. 2011
     */
    public interface Epoch {

        /**
         * The EPOCH_START_DATE.
         */
        public static final Date START_DATE = new Date(0);
        /**
         * The EPOCH_START_DATETIME.
         */
        public static final DateTime START_DATETIME = new DateTime(0);
    }

    /**
     * Description : Commonly used default amounts.
     *
     * @author BuyWay-Services: DWW<BR> Created on 4 oct. 2011
     */
    public interface Amount {

        /**
         * The DMA step for rounding.
         */
        public static final BigDecimal DMA_STEP = BigDecimal.valueOf(500);
        /**
         * The ZERO.
         */
        public static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2);
        /**
         * The BigDecimal.HUNDRED.
         */
        public static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
        /**
         * The PRECISION.
         */
        public static final int PRECISION = 10;
        /**
         * The SCALE.
         */
        public static final int SCALE = 2;
        /**
         * The MIN_VALUE.
         */
        public static final String MIN_VALUE = "0.00";
        /**
         * The DIGITS_INTEGER.
         */
        public static final int DIGITS_INTEGER = Amount.PRECISION - Amount.SCALE;
    }

    /**
     * Description : Commonly used default labels.
     *
     * @author BuyWay-Services: DWW<BR> Created on 14 mars 2012
     */
    public interface Label {

        /**
         * the french label for unknown.
         */
        public static final String UNKNOWN_FRENCH = "inconnu";
        /**
         * the dutch label for unknown.
         */
        public static final String UNKNOWN_DUTCH = "ongekend";
    }

    /**
     * Description : Commonly used default literals.
     *
     * @author BuyWay-Services: DWW<BR> Created on 4 oct. 2011
     */
    public interface Literals {

        /**
         * The POINT.
         */
        public static final String POINT = ".";
        /**
         * The DOT.
         */
        public static final String DOT = DefaultValues.Literals.POINT;
        /**
         * The COMMA.
         */
        public static final String COMMA = ",";
        /**
         * The SEMICOLON.
         */
        public static final String SEMICOLON = ";";
        /**
         * The DASH.
         */
        public static final String DASH = "-";
        /**
         * The UNDERSCORE.
         */
        public static final String UNDERSCORE = "_";
        /**
         * The SLASH.
         */
        public static final String SLASH = "/";
        /**
         * The BLANK_SPACE.
         */
        public static final String BLANK_SPACE = " ";
        /**
         * The PERCENT.
         */
        public static final String PERCENT = "%";
        /**
         * The AMPERSAND.
         */
        public static final String AMPERSAND = "&";
        /**
         * The QUESTION_MARK.
         */
        public static final String QUESTION_MARK = "?";
        /**
         * The COLON.
         */
        public static final String COLON = ":";
        /**
         * The DOLLAR.
         */
        public static final String DOLLAR = "$";
        /**
         * The EURO.
         */
        public static final String EURO = "€";
        /**
         * The NOT_AVAILABLE.
         */
        public static final String NOT_AVAILABLE = "N/A";
        /**
         * The HIDDEN_PASSWORD.
         */
        public static final String HIDDEN_PASSWORD = "****";
        /**
         * The NULL_CARD_NUMBER.
         */
        public static final String CARD_NUMBER = "0000000000000000";
        /**
         * The NULL_AUTHORIZATION_NUMBER.
         */
        public static final String AUTHORIZATION_NUMBER = "00000000";
        /**
         * The EMPTY_SPACE.
         */
        public static final String EMPTY_SPACE = " ";
    }
}
