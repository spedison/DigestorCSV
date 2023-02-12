package br.com.spedison.digestor_csv.infra;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Utils {

    public static Charset getCharset(String nome) {
        return switch (nome.toLowerCase()) {
            case "utf8" -> StandardCharsets.UTF_8;
            case "utf16" -> StandardCharsets.UTF_16;
            case "ascii" -> StandardCharsets.US_ASCII;
            case "8859","latim1" -> StandardCharsets.ISO_8859_1;
            default -> Charset.defaultCharset();
        };
    }

    public static BufferedWriter abreArquivoEscrita(String dir, String nomeArquivo, Charset charset) throws IOException {
        File fArquivo = new File(dir, nomeArquivo);
        FileWriter fw = new FileWriter(fArquivo, charset);   //reads the file
        return new BufferedWriter(fw);  //creates a buffering character input stream
    }
    public static BufferedWriter abreArquivoEscrita(String nomeArquivo, Charset charset) throws IOException {
        File fArquivo = new File(nomeArquivo);
        FileWriter fw = new FileWriter(fArquivo, charset);   //reads the file
        return new BufferedWriter(fw);  //creates a buffering character input stream
    }
    public static BufferedReader abreArquivoLeitura(String arquivoEntrada, Charset charset) throws IOException {
        File fArquivoEntrada = new File(arquivoEntrada);
        FileReader fr = new FileReader(fArquivoEntrada, charset);   //reads the file
        return new BufferedReader(fr);  //creates a buffering character input stream
    }

    /***
     * Separa o nome do arquivo em 2 partes : antes do ponto da extenso e a extensao
     * @param nome - Nome do arquivo completo.
     * @return - retorna as 2 Strings. A primeira eh o nome do arquivo a segunda eh a extensao.
     */
    public static String[] separaNomeExtensaoArquivo(String nome) {
        final int posPonto = nome.lastIndexOf('.');
        final String ext = (posPonto > 0 && posPonto < nome.length() - 1) ? nome.substring(posPonto + 1) : "";
        if (posPonto >= 0 && posPonto < nome.length()) {
            nome = nome.substring(0, posPonto);
        }
        return new String[]{nome, ext};
    }
}
