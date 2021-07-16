package com.amdocs.aia.il.deployer.logs;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.APPLICATION_BUNDLE_NAME;
import static com.amdocs.aia.il.deployer.database.ConfigurationConstants.RESOURCE_BUNDLE_PATH;


/**
 * This class is used in order to support messages logging.
 * bundle and opusBundle member variables hold PropertyResourceBundle based on ResourceBundle_LOCALE.properties. Found in resources folder.
 * LOCALE is alias for the locale the machine runs on. For example for ENGLISH locale the file name is ResourceBundle_en_US.properties.
 * The file is built from keys and values. The key represents the message key and the value represents the real message. In English locale it is
 * English message, in French locale it is French message etc.
 * Below is an example of ResourceBundle_en_US.properties file:
 * my.hello=At {1} on {1}, there was {2} on planet {0}
 * my.goodbye=Bye
 * my.question=Do you speak English?
 * TESTING_WARN=At {}, there was {} on number {}.
 * NULL_OR_EMPTY_SEPERATOR=There is a null or empty separator  < + {} + >. Using the default separator ','
 * <p>
 * log method gets as input message key and returns the message as per the definitions in ResourceBundle_en_US.properties file.
 *
 * @author LIORHAL
 */

public class LogMsg {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogMsg.class);
    private static LogMsg logMsgInstance;

    private ResourceBundle bundle;
    private ResourceBundle appBundle;

    protected LogMsg() {
        bundle = null;
    }

    private void init() {
        Locale defaultLocale = Locale.getDefault();

        try {
            bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_PATH, defaultLocale);
            appBundle = ResourceBundle.getBundle(APPLICATION_BUNDLE_NAME, defaultLocale);
        } catch (MissingResourceException ex) {
            LOGGER.error("Caught MissingResourceException:{} ", ex);
        }
    }


    public static String getMessage(String messageKey, Object... params) {
        if (logMsgInstance == null) {
            logMsgInstance = new LogMsg();
            logMsgInstance.init();
        }
        if (params != null && params.length > 0) {
            return logMsgInstance.getMsgParams(messageKey, params);
        }
        return logMsgInstance.getMsg(messageKey);
    }


    private String getMsg(String messageKey) {
        if (null != appBundle) {
            try {
                return appBundle.getString(messageKey);
            } catch (MissingResourceException | ClassCastException ex) {
                LOGGER.error("Caught Exception:{} ", ex);
                return messageKey;
            }
        } else {

            if (null != bundle) {
                try {
                    return bundle.getString(messageKey);
                } catch (MissingResourceException exx) {
                    LOGGER.error("Caught MissingResourceException:{} ", exx);
                    return messageKey;
                }
            } else {
                LOGGER.info("resource bundle member was not initialized, message can not be fetched {} ", messageKey);
                return messageKey;
            }
        }
    }


    private String getMsgParams(String key, Object... params) {
        try {
            return MessageFormat.format(getMsg(key), params);
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Caught IllegalArgumentException: {}", ex);
            return key;
        }

    }
}


