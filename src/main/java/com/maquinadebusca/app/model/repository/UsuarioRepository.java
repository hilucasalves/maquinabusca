package com.maquinadebusca.app.model.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.maquinadebusca.app.model.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Override
    List<Usuario> findAll();

    Usuario findById(long id);

    Usuario findByLogin(String login);

    @Override
    Usuario save(Usuario usuario);

    @Override
    void delete(Usuario usuario);

    @Override
    void deleteById(Long id);

    List<Usuario> findByLoginIgnoreCaseContaining(String login);

    @Query(value = "SELECT * FROM usuario ORDER BY nome", nativeQuery = true)
    List<Usuario> getInLexicalOrder();

    @Query(value = "SELECT * FROM usuario", nativeQuery = true)
    public Slice<Usuario> getPage(Pageable pageable);

    @Query(value = "SELECT * FROM usuario WHERE id between ?1 and ?2", nativeQuery = true)
    List<Usuario> findUsuarioByIdRange(Long id1, Long id2);
}
