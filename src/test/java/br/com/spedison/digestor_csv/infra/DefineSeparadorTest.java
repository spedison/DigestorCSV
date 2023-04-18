package br.com.spedison.digestor_csv.infra;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

class DefineSeparadorTest {

    @Test
    void separadoresUsadosVirgula() {

        String linhaDoArquivo = """
                No,year,month,day,hour,pm2.5,DEWP,TEMP,PRES,cbwd,Iws,Is,Ir
                1,2010,1,1,0,NA,-21,-11,1021,NW,1.79,0,0
                2,2010,1,1,1,NA,-21,-12,1020,NW,4.92,0,0
                3,2010,1,1,2,NA,-21,-11,1019,NW,6.71,0,0
                4,2010,1,1,3,NA,-21,-14,1019,NW,9.84,0,0                
                                """;

        DefineSeparador def = new DefineSeparador();
        var k = def.separadoresUsados(linhaDoArquivo.split("[\n]"));

        Assert.assertTrue(k.contains(','));

    }

    @Test
    void separadoresUsadosPontoEVirgula() {

        String linhaDoArquivo = """
                No;year;month;day;hour;pm2.5;DEWP;TEMP;PRES;cbwd;Iws;Is;Ir
                1;2010;1;1;0;NA;-21;-11;1021;NW;1.79;0;0
                2;2010;1;1;1;NA;-21;-12;1020;NW;4.92;0;0
                3;2010;1;1;2;NA;-21;-11;1019;NW;6.71;0;0
                4;2010;1;1;3;NA;-21;-14;1019;NW;9.84;0;0                
                                """;

        DefineSeparador def = new DefineSeparador();
        var k = def.separadoresUsados(linhaDoArquivo.split("[\n]"));

        Assert.assertTrue(k.contains(';'));

    }

    @Test
    void separadoresUsadosEspaco() {

        String linhaDoArquivo = """
                No year month day hour pm2.5 DEWP TEMP PRES cbwd Iws Is Ir
                1 2010 1 1 0 NA -21 -11 1021 NW 1.79 0 0
                2 2010 1 1 1 NA -21 -12 1020 NW 4.92 0 0
                3 2010 1 1 2 NA -21 -11 1019 NW 6.71 0 0
                4 2010 1 1 3 NA -21 -14 1019 NW 9.84 0 0                
                                """;

        DefineSeparador def = new DefineSeparador();
        var k = def.separadoresUsados(linhaDoArquivo.split("[\n]"));

        Assert.assertTrue(k.contains(' '));

    }

    @Test
    void separadoresUsadosEspaco2() {

        String linhaDoArquivo = """
                No year month day hour pm2.5 DEWP TEMP PRES cbwd Iws Is Ir
                1 2010 1 1 0 NA -21 -11 1021 NW 1.79 0 0
                2 2010 1 1 1 NA -21 -12 1020 NW 4.92 0 0
                3 2010 1 1 2 NA -21 -11 1019 NW 6.71 0 0
                4a2010 1 1 3 NA -21 -14 1019 NW 9.84 0,0                
                                """;

        DefineSeparador def = new DefineSeparador();
        var k = def.separadoresUsados(linhaDoArquivo.split("[\n]"));

        Assert.assertTrue(k.contains(' '));

    }

    @Test
    void separadoresUsadosA() {

        String linhaDoArquivo = """
                aaaaaaaaaaaaaaaaNoayearamonthadayahourapm2.5aDEWPaTEMPaPRESacbwdaIwsaIsaIr
                aaaaaaaaaaaaaaaa1a2010a1a1a0aNAa-21a-11a1021aNWa1.79a0a0
                aaaaaaaaaaaaaaaa2a2010a1a1a1aNAa-21a-12a1020aNWa4.92a0a0
                aaaaaaaaaaaaaaaa3a2010a1a1a2aNAa-21a-11a1019aNWa6.71a0a0
                aaaaaaaaaaaaaaaa4a2010a1a1a3aNAa-21a-14a1019aNWa9.84a0a0                
                                                """;

        DefineSeparador def = new DefineSeparador();
        var k = def.separadoresUsados(linhaDoArquivo.split("[\n]"));
        Assert.assertTrue(k.contains('.'));
    }

    @Test
    void separadoresUsadosCovid() {

        String linhaDoArquivo =
        """
                Secretaria;Número do Processo;Modalidade de Contratação;Contratada / Conveniada;CPF / CNPJ / CGC;Descrição Processo;Finalidade/Item;Quantidade Item;Valor Unitário;Empenho;Nota de Empenho;Data da Movimentação;Tipo de Pagamento;Número de Pagamento;Valor NE;Valor NL;Valor NP;Valor OB;Código Fonte Recurso;Código Nome Fonte Detalhada;Local Entrega
                Saúde;2020/16884;DISPENSA DE LICITACAO;HICHENS HARRISON CAPITAL PARTNER LLC;EIM number 831426803;AQUISIÇÃO DE EQUIPAMENTO;RESPIRADOR E OUTROS POR IMPORTAÇÃO;1;165.247.500,00;242.247.500,00;2020NE00561;14/04/2020;NE;;165.247.500,00;0,00;0,00;0,00;1;001001133 - REC.TESOSURO-COVID19;SAO PAULO
                Saúde;2020/16884;DISPENSA DE LICITACAO;HICHENS HARRISON CAPITAL PARTNER LLC;EIM number 831426803;AQUISIÇÃO DE EQUIPAMENTO;RESPIRADOR E OUTROS POR IMPORTAÇÃO;0;0,00;242.247.500,00;2020NE00561;14/04/2020;NL;2020NL03027;0,00;165.247.500,00;0,00;0,00;1;001001133 - REC.TESOSURO-COVID19;SAO PAULO
                Saúde;2020/16884;DISPENSA DE LICITACAO;HICHENS HARRISON CAPITAL PARTNER LLC;EIM number 831426803;AQUISIÇÃO DE EQUIPAMENTO;RESPIRADOR E OUTROS POR IMPORTAÇÃO;0;0,00;242.247.500,00;2020NE00561;14/04/2020;OB;2020OB00118;0,00;0,00;0,00;165.247.500,00;1;001001133 - REC.TESOSURO-COVID19;SAO PAULO
                Saúde;2020/16884;DISPENSA DE LICITACAO;HICHENS HARRISON CAPITAL PARTNER LLC;EIM number 831426803;AQUISIÇÃO DE EQUIPAMENTO;RESPIRADOR E OUTROS POR IMPORTAÇÃO;1;385.577.500,00;242.247.500,00;2020NE00584;23/04/2020;NE;;385.577.500,00;0,00;0,00;0,00;1;001001133 - REC.TESOSURO-COVID19;SAO PAULO
                Saúde;2020/16884;DISPENSA DE LICITACAO;HICHENS HARRISON CAPITAL PARTNER LLC;EIM number 831426803;AQUISIÇÃO DE EQUIPAMENTO;RESPIRADOR E OUTROS POR IMPORTAÇÃO;0;0,00;242.247.500,00;2020NE00584;23/04/2020;NL;2020NL03424;0,00;77.000.000,00;0,00;0,00;1;001001133 - REC.TESOSURO-COVID19;SAO PAULO
                Saúde;2020/16884;DISPENSA DE LICITACAO;HICHENS HARRISON CAPITAL PARTNER LLC;EIM number 831426803;AQUISIÇÃO DE EQUIPAMENTO;RESPIRADOR E OUTROS POR IMPORTAÇÃO;0;0,00;242.247.500,00;2020NE00584;23/04/2020;OB;2020OB00126;0,00;0,00;0,00;77.000.000,00;1;001001133 - REC.TESOSURO-COVID19;SAO PAULO                                
        """;

        DefineSeparador def = new DefineSeparador();
        var k = def.separadoresUsados(linhaDoArquivo.split("[\n]"));

        Assert.assertTrue(k.contains(';'));
    }

    @Test
    void separadoresUsadosCovidTab() {

        String linhaDoArquivo =
        """
                Secretaria	Número do Processo	Modalidade de Contratação	Contratada / Conveniada	CPF / CNPJ / CGC	Descrição Processo	Finalidade/Item	Quantidade Item	Valor Unitário	Empenho	Nota de Empenho	Data da Movimentação	Tipo de Pagamento	Número de Pagamento	Valor NE	Valor NL	Valor NP	Valor OB	Código Fonte Recurso	Código Nome Fonte Detalhada	Local Entrega
                Saúde	2020/16884	DISPENSA DE LICITACAO	HICHENS HARRISON CAPITAL PARTNER LLC	EIM number 831426803	AQUISIÇÃO DE EQUIPAMENTO	RESPIRADOR E OUTROS POR IMPORTAÇÃO	1	165.247.500,00	242.247.500,00	2020NE00561	14/04/2020	NE		165.247.500,00	0,00	0,00	0,00	1	001001133 - REC.TESOSURO-COVID19	SAO PAULO
                Saúde	2020/16884	DISPENSA DE LICITACAO	HICHENS HARRISON CAPITAL PARTNER LLC	EIM number 831426803	AQUISIÇÃO DE EQUIPAMENTO	RESPIRADOR E OUTROS POR IMPORTAÇÃO	0	0,00	242.247.500,00	2020NE00561	14/04/2020	NL	2020NL03027	0,00	165.247.500,00	0,00	0,00	1	001001133 - REC.TESOSURO-COVID19	SAO PAULO
                Saúde	2020/16884	DISPENSA DE LICITACAO	HICHENS HARRISON CAPITAL PARTNER LLC	EIM number 831426803	AQUISIÇÃO DE EQUIPAMENTO	RESPIRADOR E OUTROS POR IMPORTAÇÃO	0	0,00	242.247.500,00	2020NE00561	14/04/2020	OB	2020OB00118	0,00	0,00	0,00	165.247.500,00	1	001001133 - REC.TESOSURO-COVID19	SAO PAULO
                Saúde	2020/16884	DISPENSA DE LICITACAO	HICHENS HARRISON CAPITAL PARTNER LLC	EIM number 831426803	AQUISIÇÃO DE EQUIPAMENTO	RESPIRADOR E OUTROS POR IMPORTAÇÃO	1	385.577.500,00	242.247.500,00	2020NE00584	23/04/2020	NE		385.577.500,00	0,00	0,00	0,00	1	001001133 - REC.TESOSURO-COVID19	SAO PAULO
                Saúde	2020/16884	DISPENSA DE LICITACAO	HICHENS HARRISON CAPITAL PARTNER LLC	EIM number 831426803	AQUISIÇÃO DE EQUIPAMENTO	RESPIRADOR E OUTROS POR IMPORTAÇÃO	0	0,00	242.247.500,00	2020NE00584	23/04/2020	NL	2020NL03424	0,00	77.000.000,00	0,00	0,00	1	001001133 - REC.TESOSURO-COVID19	SAO PAULO
                Saúde	2020/16884	DISPENSA DE LICITACAO	HICHENS HARRISON CAPITAL PARTNER LLC	EIM number 831426803	AQUISIÇÃO DE EQUIPAMENTO	RESPIRADOR E OUTROS POR IMPORTAÇÃO	0	0,00	242.247.500,00	2020NE00584	23/04/2020	OB	2020OB00126	0,00	0,00	0,00	77.000.000,00	1	001001133 - REC.TESOSURO-COVID19	SAO PAULO                                
        """;

        DefineSeparador def = new DefineSeparador();
        var k = def.separadoresUsados(linhaDoArquivo.split("[\n]"));
        Assert.assertTrue(k.contains('\t'));

        k.stream().map("[%c]"::formatted).forEach(System.out::println);

    }


}