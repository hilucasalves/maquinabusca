package com.maquinadebusca.app.model.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.maquinadebusca.app.model.Termo;

public interface TermoRepository extends JpaRepository<Termo, Long> {

  @Override
  List<Termo> findAll ();

  Termo findById (long id);

  @Override
  Termo save (Termo termo);

}
