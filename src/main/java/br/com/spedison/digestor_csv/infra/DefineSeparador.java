package br.com.spedison.digestor_csv.infra;

import java.util.*;
import java.util.stream.IntStream;

public class DefineSeparador {

    static class ItemContagem {
        private Character character;
        private int contagem;

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

        public int getContagem() {
            return contagem;
        }

        public Character getCharacter() {
            return character;
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

        public boolean isLetra() {
            return Character.isAlphabetic(character) || Character.isDigit(character);
        }
        public boolean notIsLetra() {
            return ! isLetra();
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

    public List<Character> separadoresUsados(String[] linhas) {

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

        return resultado
                .get(0)
                .entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .filter(ItemContagem::notIsLetra)
                .sorted(comparador)
                .limit(3)
                .map(ItemContagem::getCharacter)
                .toList();
    }
}
