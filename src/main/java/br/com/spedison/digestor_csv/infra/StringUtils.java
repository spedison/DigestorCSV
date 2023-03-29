package br.com.spedison.digestor_csv.infra;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;

public class StringUtils {
    public static String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public static Charset getCharset(String nome) {
        return switch (nome.trim().toLowerCase()) {
            case "utf8" -> StandardCharsets.UTF_8;
            case "utf16" -> StandardCharsets.UTF_16;
            case "ascii" -> StandardCharsets.US_ASCII;
            case "8859","latim1" -> StandardCharsets.ISO_8859_1;
            default -> Charset.defaultCharset();
        };
    }
}
