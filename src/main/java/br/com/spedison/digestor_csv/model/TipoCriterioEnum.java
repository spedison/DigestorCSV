package br.com.spedison.digestor_csv.model;

import lombok.Getter;

@Getter
public enum TipoCriterioEnum {

    VAZIO("Selecione"),
    TXT_IGUAL("é texto e igual"),
    TXT_DIFERENTE("é texto e diferente"),
    TXT_CONTEM("é texto e contém"),
    TXT_NAO_CONTEM("é texto e não contém"),
    TXT_INICIA("é texto e inicia com"),
    TXT_NAO_INICIA("é texto e não inicia com"),
    TXT_TERMINA("é texto e termina por"),
    TXT_NAO_TERMINA("é texto e não termina por"),
    NUM_ENTRE("é número entre"),
    NUM_MAIOR("é número maior que"),
    NUM_MENOR("é número Menor que"),
    NUM_IGUAL("é número igual a");

    private String texto;

    TipoCriterioEnum(String texto) {
        this.texto = texto;
    }
}
