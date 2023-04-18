package br.com.spedison.digestor_csv.infra;

import org.apache.tika.Tika;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FileUtils {

    static private final Tika tika = new Tika();

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
                .replaceAll("[?]", "_")
                .replaceAll("[!]", "_")
                .replaceAll("[@]", "_")
                .replaceAll("[*]", "_")
                .replaceAll("[\"']", "")
                .trim();
        return StringUtils.removerAcentos(nome);
    }

    public static String getTipo(String fileName) {
        return tika.detect(fileName);
    }

    public static String getTipoComEncoding(String nomeDoArquivo) {
        String mimetype = tika.detect(nomeDoArquivo);
        if (mimetype.startsWith("text/")) {
            mimetype += (" - " + getEncodingArquivoTexto(nomeDoArquivo));
        }
        return mimetype;
    }

//    static class HistogramaLinha{
//        int linha;
//        int [] contagem;
//
//        public HistogramaLinha() {
//            linha = 0;
//            contagem = new int[Math.pow(2,16)];
//        }
//    }

    public static String getSeparadores(String nomeArquivo, Charset encoding) {

        try (FileReader fr = new FileReader(nomeArquivo)) {
            BufferedReader br = new BufferedReader(fr);

            return "";
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getEncodingArquivoTexto(String nomeDoArquivo) {

        byte[] bytes = new byte[16 * 1024];
        int length;

        try (InputStream inputStream = new FileInputStream(nomeDoArquivo)) {
            length = inputStream.read(bytes);
        } catch (IOException ioe) {
            return "";
        }

        boolean isAscii = true;
        for (int i = 0; i < length; i++) {
            int b = bytes[i] & 0xFF;
            if (b > 0x7F) {
                isAscii = false;
                break;
            }
        }
        if (isAscii) {
            return "ascii";
        }

        boolean isIso8859_1 = true;
        for (int i = 0; i < length; i++) {
            int b = bytes[i] & 0xFF;
            if (b > 0x7F && b < 0xA0) {
                isIso8859_1 = false;
                break;
            }
        }
        if (isIso8859_1) {
            return "ISO-8859-1";
        }

        int b0 = bytes[0] & 0xFF;
        int b1 = bytes[1] & 0xFF;
        int b2 = bytes[2] & 0xFF;
        int b3 = bytes[3] & 0xFF;

        if (b0 == 0x00 && b1 == 0x00 && b2 == 0xFE && b3 == 0xFF) {
            return "UTF-32BE";
        }

        if (b0 == 0xFF && b1 == 0xFE && b2 == 0x00 && b3 == 0x00) {
            return "UTF-32LE";
        }

        if (b0 == 0xFE && b1 == 0xFF) {
            return "UTF-16BE";
        }

        if (b0 == 0xFF && b1 == 0xFE) {
            return "UTF-16LE";
        }

        try {
            new String(bytes, 0, length, "UTF-8");
            return "UTF-8";
        } catch (java.io.UnsupportedEncodingException e) {
            return "";
        }

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
        if (posPonto >= 0) {
            nome = nome.substring(0, posPonto);
        }
        return new String[]{nome, ext};
    }

    public static FilenameFilter getFiltroArquivo(String ext) {
        return (File dirBase, String nome) -> {
            File ff = new File(dirBase.toString(), nome);
            if (ff.exists() && ff.isFile()) {
                return (
                        nome.trim().toLowerCase().endsWith(ext.toLowerCase().trim())
                                || ext.trim().equals("*")
                                || ext.trim().equals("*.*")
                );
            }
            return false;
        };
    }

    public static FilenameFilter getFiltroArquivoNaoVazio(String ext) {
        return (File dirBase, String nome) ->
             getFiltroArquivo(ext).accept(dirBase, nome) &&
                    (Paths.get(dirBase.toString(), nome).toFile().length() > 0) ;
    }
}
