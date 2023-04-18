package br.com.spedison.digestor_csv.infra;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

class StringUtilsTest {

    @Test
    void removerAcentos() {
        var s = StringUtils.removerAcentos("ÉdisonDêOBonéParaçAda");
        Assert.assertEquals(s, "EdisonDeOBoneParacAda");
    }

    @Test
    void getCharset() {
        Charset s = StringUtils.getCharset("utf-8");
        Assert.assertEquals(s, StandardCharsets.UTF_8);

        s = StringUtils.getCharset("utf-16");
        Assert.assertEquals(s, StandardCharsets.UTF_16);

        s = StringUtils.getCharset("latim1");
        Assert.assertEquals(s, StandardCharsets.ISO_8859_1);

        s = StringUtils.getCharset("ascii");
        Assert.assertEquals(s, StandardCharsets.US_ASCII);
    }

    @Test
    void strParaExpressaoRegular() {
        var encodado = "\\b";
        var semEncoding = "[\b]";
        Assert.assertEquals(StringUtils.strParaExpressaoRegular(encodado), semEncoding);
        encodado = "\\t";
        semEncoding = "[\t]";
        Assert.assertEquals(StringUtils.strParaExpressaoRegular(encodado), semEncoding);
        encodado = "\\f";
        semEncoding = "[\f]";
        Assert.assertEquals(StringUtils.strParaExpressaoRegular(encodado), semEncoding);
        encodado = "\\n";
        semEncoding = "[\n]";
        Assert.assertEquals(StringUtils.strParaExpressaoRegular(encodado), semEncoding);
        encodado = "\\r";
        semEncoding = "[\r]";
        Assert.assertEquals(StringUtils.strParaExpressaoRegular(encodado), semEncoding);
        encodado = "'";
        semEncoding = "[\\']";
        Assert.assertEquals(StringUtils.strParaExpressaoRegular(encodado), semEncoding);
        encodado = "\"";
        semEncoding = "[\\\"]";
        Assert.assertEquals(StringUtils.strParaExpressaoRegular(encodado), semEncoding);
        encodado = "\\";
        semEncoding = "[\\\\]";
        Assert.assertEquals(StringUtils.strParaExpressaoRegular(encodado), semEncoding);
        encodado = "\\";
        semEncoding = "[\\\\]";
        Assert.assertEquals(StringUtils.strParaExpressaoRegular(encodado), semEncoding);
        encodado = " ";
        semEncoding = "[ ]";
        Assert.assertEquals(StringUtils.strParaExpressaoRegular(encodado), semEncoding);
    }

    @Test
    public void testaComRegExp() {
        String[] strTokens = {"Separando", "em", "partes"};

        String regExp = StringUtils.strParaExpressaoRegular("\\t");
        String str = "Separando\tem\tpartes";
        Assert.assertArrayEquals(str.split(regExp), strTokens);

        regExp = StringUtils.strParaExpressaoRegular(";");
        str = "Separando;em;partes";
        Assert.assertArrayEquals(str.split(regExp), strTokens);

        regExp = StringUtils.strParaExpressaoRegular("\\n");
        str = "Separando\nem\npartes";
        Assert.assertArrayEquals(str.split(regExp), strTokens);

        regExp = StringUtils.strParaExpressaoRegular("[;]{2}");
        str = "Separando;;em;;partes";
        Assert.assertArrayEquals(str.split(regExp), strTokens);
    }

}