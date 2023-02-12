package br.com.spedison.digestor_csv.repository;

import br.com.spedison.digestor_csv.model.ConfiguracaoVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfiguracaoRepository extends JpaRepository<ConfiguracaoVO, String> {
    List<ConfiguracaoVO> findAllByHabilitadoOrderByOrdem(Boolean habilidado);

    @Query(value = "select count(c) from ConfiguracaoVO c where c.habilitado = true", nativeQuery = false)
    Long contaHabilitados();

    @Modifying
    @Query(value = "UPDATE ConfiguracaoVO c SET c.valor = :valor WHERE c.nome = :nome", nativeQuery = false)
    void updateValor(String nome, String valor);

    @Query("select c.valor from ConfiguracaoVO c where c.habilitado = true and c.nome in :nomes")
    List<String> getValores(String ... nomes);
}
