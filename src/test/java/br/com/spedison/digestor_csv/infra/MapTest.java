package br.com.spedison.digestor_csv.infra;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MapTest {
    @Test
    public void testMapInsert() {
        Map<String, Integer> map = new HashMap<>();

        map.put("Teste1", 10);
        map.put("Teste2", 20);
        map.put("Teste3", 30);
        map.put("Teste4", 40);

        var l = map.putIfAbsent("Teste1", 120);

        System.out.printf("%n%d encontrado%n", l);
        System.out.println(map.toString());

        /*var a = map.putIfAbsent("Teste1", 11);
        System.out.println("Teste 1 = " + a);
        System.out.println(map.toString());

        a = map.putIfAbsent("Teste 20", 11);
        System.out.println("Teste 20 = " + a);
        System.out.println(map.toString()); */

    }
}
