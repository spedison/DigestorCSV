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
            case "utf8", "utf-8" -> StandardCharsets.UTF_8;
            case "utf16", "utf-16", "utf-16be", "utf-16le" -> StandardCharsets.UTF_16;
            case "ascii" -> StandardCharsets.US_ASCII;
            case "8859", "iso8859", "iso-8859", "iso-8859-1", "latim1" -> StandardCharsets.ISO_8859_1;
            default -> Charset.defaultCharset();
        };
    }

    /***
     * É usada para converter uma String digitada no configuração do Separador e o converte em uma String para separar.
     * @param strSeparator
     * @return
     */
    public static String strParaExpressaoRegular(String strSeparator) {
        return switch (strSeparator) {
            case "\\b" -> "[\b]";
            case "\\t" -> "[\t]";
            case "\\f" -> "[\f]";
            case "\\n" -> "[\n]";
            case "\\r" -> "[\r]";
            case "'" -> "[\\']";
            case "\"" -> "[\\\"]";
            case "\\" -> "[\\\\]";
            case " " -> "[ ]";
            case "." -> "[.]";
            case "," -> "[,]";
            case ";" -> "[;]";
            case "|" -> "[|]";
            default -> strSeparator;
        };
    }

    public static String chParaUsuario(Character chSeparator) {
        return switch (chSeparator) {
            case '\b' -> "\\b";
            case '\t' -> "\\t";
            case '\f' -> "\\f";
            case '\n' -> "\\n";
            case '\r' -> "\\r";
            default -> "%c".formatted(chSeparator);
        };
    }

}
