package br.com.spedison.digestor_csv.infra;

import java.io.*;
import java.nio.charset.Charset;

public class FileUtils {
    /***
     * Ajusta o texto para que ele possa ser utilizado como nome de arquivo.
     * @param nome - Nome utilizado para formar um arquivo.
     * @return - Nome ajustado.
     */
    public static String ajustaNome(String nome) {
        nome = nome
                .replaceAll("[ ]", "_")
                .replaceAll("[/]", "-")
                .replaceAll("[\\\\]", "-")
                .replaceAll("[|]", "_")
                .replaceAll("[!]", "_")
                .replaceAll("[@]", "_")
                .replaceAll("[*]", "_")
                .replaceAll("[\"']", "")
                .replaceAll("[?]", "_")
                .trim();
        return StringUtils.removerAcentos(nome);
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
