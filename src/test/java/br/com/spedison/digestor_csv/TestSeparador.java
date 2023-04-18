package br.com.spedison.digestor_csv;

import org.apache.commons.lang3.stream.Streams;

import java.io.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TestSeparador {

    static class ItemContagem {
        public Character character;
        public int contagem;

        public ItemContagem(Character character) {
            this.character = character;
            this.contagem = 1;
        }

        public boolean isChar(Character ch) {
            return this.character.equals(ch);
        }

        public void inc() {
            this.contagem++;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemContagem that = (ItemContagem) o;
            return contagem == that.contagem && Objects.equals(character, that.character);
        }

        @Override
        public int hashCode() {
            return Objects.hash(character, contagem);
        }
    }

    static class MapaItemContagem extends HashMap<Character, ItemContagem> {
        public MapaItemContagem() {
        }

        public MapaItemContagem(String linha) {
            super();
            processaLinha(linha);
        }

        public MapaItemContagem conta(Character ch) {
            ItemContagem ic = this.putIfAbsent(ch, new ItemContagem(ch));
            if (ic != null)
                ic.inc();
            return this;
        }

        public void processaLinha(String linha) {
            Character[] charsOut = new Character[linha.length()];
            char[] charsIn = linha.toCharArray();
            IntStream
                    .range(0, linha.length())
                    .parallel()
                    .forEach(pos -> charsOut[pos] = charsIn[pos]);
            Arrays.stream(charsOut).forEach(this::conta);
        }


        public MapaItemContagem contagensIguais(MapaItemContagem outroMapa) {
            MapaItemContagem ret = new MapaItemContagem();
            this.forEach((c, v) -> { // Roda os elementos desta lista
                if (outroMapa.containsKey(c)) { // Se tiver na outra ? Executa
                    if (this.get(c).equals(v)) { // Os itens são iguais ?
                        ret.put(c, v); // Adiciona na saida
                    }
                }
            });
            return ret;
        }
    }

    static class DefineSeparador {

        private List<MapaItemContagem> geraMapaInical(String[] linhas) {
            return Arrays
                    .stream(linhas)
                    .map(MapaItemContagem::new)
                    .toList();
        }

        private List<MapaItemContagem> possiveisSeparadores(List<MapaItemContagem> mapa) {

            List<MapaItemContagem> resultado = new LinkedList<>();
            int limite = mapa.size() % 2 == 0 ? (mapa.size() / 2) - 1 : mapa.size() / 2;
            for (int pos = 0; pos <= limite; pos++) {
                MapaItemContagem i = mapa.get(pos).contagensIguais(mapa.get(mapa.size() - 1 - pos));
                if (i.size() > 0)
                    resultado.add(i);
            }
            if (resultado.size() <= 1)
                return resultado;
            else
                return possiveisSeparadores(resultado);
        }

        public Character separadoresUsados(String[] linhas) {

            List<MapaItemContagem> processa = geraMapaInical(linhas);
            List<MapaItemContagem> resultado = possiveisSeparadores(processa);
            if (resultado.isEmpty())
                return null;

            Comparator<ItemContagem> comparador = (ItemContagem a, ItemContagem b) -> {
                Integer retComp = Integer.compare(b.contagem, a.contagem);
                if (retComp == 0) {
                    if (Character.isAlphabetic(a.character) || Character.isDigit(a.character))
                        return 1;
                    return -1;
                }
                return retComp;
            };

            var ret = resultado
                    .get(0)
                    .entrySet()
                    .stream()
                    .map(Map.Entry::getValue)
                    .sorted(comparador)
                    .limit(1)
                    .map(k -> k.character)
                    .max(Character::compareTo)
                    .orElse(Character.valueOf(';'));

            return ret;
        }
    }

    public static void main(String[] args) {

        String linhaDoArquivo = """
                No,year,month,day,hour,pm2.5,DEWP,TEMP,PRES,cbwd,Iws,Is,Ir
                1,2010,1,1,0,NA,-21,-11,1021,NW,1.79,0,0
                2,2010,1,1,1,NA,-21,-12,1020,NW,4.92,0,0
                3,2010,1,1,2,NA,-21,-11,1019,NW,6.71,0,0
                4,2010,1,1,3,NA,-21,-14,1019,NW,9.84,0,0                
                                """;

        DefineSeparador def = new DefineSeparador();
        var k = def.separadoresUsados(linhaDoArquivo.split("[\n]"));
        if (k != null)
            System.out.println(k);
            //Arrays.stream(k).forEach(System.out::println);

        // return;
/*
        String fileName = args[0];
        int numLinesToCompare = 7; // altere para o número desejado de linhas a serem comparadas
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            Map<Character, Integer>[] histograms = new HashMap[numLinesToCompare];
            for (int i = 0; i < numLinesToCompare; i++) {
                histograms[i] = new HashMap<>();
            }
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null && lineNum < numLinesToCompare) {
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    if (c != '\r' && c != '\n') {
                        if (histograms[lineNum].containsKey(c)) {
                            histograms[lineNum].put(c, histograms[lineNum].get(c) + 1);
                        } else {
                            histograms[lineNum].put(c, 1);
                        }
                    }
                }
                lineNum++;
            }

            if (!histograms[0].keySet().equals(histograms[1].keySet())) {
                System.out.println("CSV files have different sets of characters. Cannot compare frequencies.");
                return;
            }

            Map<Character, Integer> combinedHistogram = new HashMap<>();
            for (char c : histograms[0].keySet()) {
                int total = 0;
                for (int i = 0; i < numLinesToCompare; i++) {
                    total += histograms[i].get(c);
                }
                combinedHistogram.put(c, total);
            }

            if (!combinedHistogram.containsKey(' ')) {
                combinedHistogram.put(' ', 0);
            }
            List<Character> possibleSeparators = new ArrayList<>();
            if (!combinedHistogram.isEmpty()) {
                int maxCount = Collections.max(combinedHistogram.values());
                for (Map.Entry<Character, Integer> entry : combinedHistogram.entrySet()) {
                    if (entry.getValue() == maxCount) {
                        possibleSeparators.add(entry.getKey());
                    }
                }
            }

            if (possibleSeparators.isEmpty()) {
                System.out.println("No separator found in CSV file");
            } else if (possibleSeparators.size() == 1) {
                char separator = possibleSeparators.get(0);
                System.out.println("Separator used in CSV file is: " + separator);
            } else {
                System.out.println("Possible separators in CSV file are: " + possibleSeparators);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } */
    }
}
