package br.com.spedison.digestor_csv.infra;

import java.util.LinkedList;

public class ListFileProcessamento extends LinkedList<FileProcessamento> {
    public ListFileProcessamento() {
        super();
    }

    @Override
    public boolean add(FileProcessamento fileProcessamento) {
        var ret = super.add(fileProcessamento);
        fileProcessamento.setNumeroArquivoProcessamento(size()-1);
        return ret;
    }
}
