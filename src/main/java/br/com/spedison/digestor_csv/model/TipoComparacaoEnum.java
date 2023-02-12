package br.com.spedison.digestor_csv.model;

import lombok.Getter;

@Getter
public enum TipoComparacaoEnum {
    TXT_IGUAL("é texto e igual"),
    TXT_CONTEM("é texto e contém"),
    TXT_NAO_CONTEM("é texto e não contém"),
    TXT_INICIA("é texto e inicia com"),
    TXT_TERMINA("é texto e termina por"),
    NUM_ENTRE("é número entre"),
    NUM_MAIOR("é número maior que"),
    NUM_MENOR("é número Menor que"),
    NUM_IGUAL("é número igual a");

    private String texto;

    TipoComparacaoEnum(String texto) {
        this.texto = texto;
    }
}
