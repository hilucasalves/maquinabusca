package com.maquinadebusca.app.model.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.maquinadebusca.app.model.Link;
import java.time.LocalDateTime;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

public interface LinkRepository extends JpaRepository<Link, Long> {

  @Override
  List<Link> findAll ();

  Link findById (long id);

  Link findByUrl (String url);

  @Override
  Link save (Link link);

  @Override
  void delete (Link link);

  @Override
  void deleteById (Long id);

  List<Link> findByUrlIgnoreCaseContaining (String url);

  @Query (value = "SELECT * FROM link ORDER BY url", nativeQuery = true)
  List<Link> getInLexicalOrder ();

  @Query (value = "SELECT * FROM link", nativeQuery = true)
  public Slice<Link> getPage (Pageable pageable);

  @Query (value = "SELECT * FROM link WHERE id between ?1 and ?2", nativeQuery = true)
  List<Link> findLinkByIdRange (Long id1, Long id2);

  @Query (value = "SELECT COUNT(*) FROM Link WHERE id between :identificador1 and  :identificador2", nativeQuery = true)
  Long countLinkByIdRange (@Param ("identificador1") Long id1, @Param ("identificador2") Long id2);

  @Transactional
  @Modifying
  @Query (value = "UPDATE link l SET l.ultimaColeta = :data WHERE l.url LIKE CONCAT ('%',:host,'%')", nativeQuery = true)
  int updateLastCrawlingDate (@Param ("data") LocalDateTime ultimaColeta, @Param ("host") String nomeHost);
}

//  @Query (value = "SELECT * FROM link", 
//                 countQuery  = "SELECT COUNT(*) FROM Link", 
//                 nativeQuery = true)
//  public Slice<Link> getPage (Pageable pageable);
