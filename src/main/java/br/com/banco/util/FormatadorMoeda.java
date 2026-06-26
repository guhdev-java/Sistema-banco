package br.com.banco.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class FormatadorMoeda {
    private static final Locale LOCALE_BR = new Locale.Builder().setLanguage("pt").setRegion("BR").build();
    private static final NumberFormat INSTANCE = NumberFormat.getCurrencyInstance(LOCALE_BR);

    private FormatadorMoeda() {}

    public static String formatar(BigDecimal valor) {
        return INSTANCE.format(valor);
    }

    public static Locale getLocale() {
        return LOCALE_BR;
    }
}
