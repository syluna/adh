package fr.adh.client;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class I18n {

    private static final Logger LOGGER = LoggerFactory.getLogger(I18n.class);

    private static final String LANGUAGE_FILE = "Interface/language";

    @Getter(value = AccessLevel.PRIVATE)
    private static final I18n instance = new I18n();

    private ResourceBundle resourceBundle;

    public static void initialize() {
        try {
            final Locale locale = Locale.getDefault() == null ? Locale.ENGLISH : Locale.getDefault();
            LOGGER.info("Loading language for locale [{}]", locale);
            getInstance().resourceBundle = ResourceBundle.getBundle(LANGUAGE_FILE, locale);
        } catch (final MissingResourceException mre) {
            if (!Locale.ENGLISH.equals(Locale.getDefault())) {
                Locale.setDefault(Locale.ENGLISH);
                LOGGER.warn("Missing to find resource file try to load [{}] as default.", Locale.getDefault());
                initialize();
            } else {
                LOGGER.error("Missing resource file [{}_{}.properties] : [{}].", LANGUAGE_FILE,
                        Locale.getDefault().getLanguage(), mre.getMessage());
            }
        }
    }

    public static final String get(final String key) {
        return getInstance().resourceBundle == null ? key : getInstance().resourceBundle.getString(key);
    }

}
