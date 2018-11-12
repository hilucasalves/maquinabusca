package com.maquinadebusca.app.model.service;

import com.maquinadebusca.app.model.Documento;
import com.maquinadebusca.app.model.Link;
import java.util.LinkedList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.maquinadebusca.app.model.repository.DocumentoRepository;
import com.maquinadebusca.app.model.repository.LinkRepository;
import java.time.LocalDateTime;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

@Service
public class ColetorService {

  @Autowired
  private DocumentoRepository dr;

  @Autowired
  private LinkRepository lr;

  public Long contarLinkPorIntervaloDeIdentificacao (Long id1, Long id2) {
    return lr.countLinkByIdRange (id1, id2);
  }

  public List<Link> pesquisarLinkPorIntervaloDeIdentificacao (Long id1, Long id2) {
    return lr.findLinkByIdRange (id1, id2);
  }

  public String buscarPagina () {
    Slice<Link> pagina = null;
    Pageable pageable = PageRequest.of (0, 3, Sort.by (Sort.Direction.DESC, "url"));

    while (true) {
      pagina = lr.getPage (pageable);
      int numeroDaPagina = pagina.getNumber ();
      int numeroDeElementosNaPagina = pagina.getNumberOfElements ();
      int tamanhoDaPagina = pagina.getSize ();
      System.out.println ("\n\nPágina: " + numeroDaPagina + "   Número de Elementos: " + numeroDeElementosNaPagina + "   Tamaho da Página: " + tamanhoDaPagina);
      List<Link> links = pagina.getContent ();
      links.forEach (System.out::println);
      if (!pagina.hasNext ()) {
        break;
      }
      pageable = pagina.nextPageable ();
    }
    return "{\"resposta\": \"Ok\"}";
  }

  public boolean removerLink (Long id) {
    boolean resp = false;
    try {
      lr.deleteById (id);
      resp = true;
    } catch (Exception e) {
      System.out.println ("\n>>> Não foi possível remover o link informado no banco de dados.\n");
      e.printStackTrace ();
    }
    return resp;
  }

  public Link removerLink (Link link) {
    try {
      lr.delete (link);
    } catch (Exception e) {
      link = null;
      System.out.println ("\n>>> Não foi possível remover o link informado no banco de dados.\n");
      e.printStackTrace ();
    }
    return link;
  }

  public Link salvarLink (Link link) {
    Link l = null;
    try {
      l = lr.save (link);
    } catch (Exception e) {
      System.out.println ("\n>>> Não foi possível salvar o link informado no banco de dados.\n");
      e.printStackTrace ();
    }
    return l;
  }

  public Link atualizarLink (Link link) {
    Link l = null;
    try {
      l = lr.save (link);
    } catch (Exception e) {
      System.out.println ("\n>>> Não foi possível atualizar o link informado no banco de dados.\n");
      e.printStackTrace ();
    }
    return l;
  }

  public List<Documento> executar () {
    List<Documento> documentos = new LinkedList ();
    List<String> sementes = new LinkedList ();

    try {
      sementes.add ("https://www.youtube.com/");
      sementes.add ("https://www.facebook.com/");
      sementes.add ("https://www.twitter.com/");

      for (String url : sementes) {
        documentos.add (this.coletar (url));
      }
    } catch (Exception e) {
      System.out.println ("\n\n\n Erro ao executar o serviço de coleta! \n\n\n");
      e.printStackTrace ();
    }
    return documentos;
  }

  public Documento coletar (String urlDocumento) {
    Documento documento = new Documento ();

    try {
      Link link = new Link ();
      Document d = Jsoup.connect (urlDocumento).get ();
      Elements urls = d.select ("a[href]");

      documento.setUrl (urlDocumento);
      documento.setTexto (d.html ());
      documento.setVisao (d.text ());

      link.setUrl (urlDocumento);
      link.setUltimaColeta (LocalDateTime.now ());
      link.addDocumento (documento);
      documento.addLink (link);
      int i = 0;
      for (Element url : urls) {
        i++;
        String u = url.attr ("abs:href");
        if ((!u.equals ("")) && (u != null)) {
          link = lr.findByUrl (u);
          if (link == null) {
            link = new Link ();
            link.setUrl (u);
            link.setUltimaColeta (null);
          }
          link.addDocumento (documento);
          documento.addLink (link);
        }
      }
      System.out.println ("Número de links coletados: " + i);
      System.out.println ("Tamanho da lista links: " + documento.getLinks ().size ());
      //Salvar o documento no banco de dados.
      documento = dr.save (documento);
    } catch (Exception e) {
      System.out.println ("\n\n\n Erro ao coletar a página! \n\n\n");
      e.printStackTrace ();
    }
    return documento;
  }

  public List<Documento> getDocumento () {
    Iterable<Documento> documentos = dr.findAll ();
    List<Documento> resposta = new LinkedList ();
    for (Documento documento : documentos) {
      resposta.add (documento);
    }
    return resposta;
  }

  public Documento getDocumento (long id) {
    Documento documento = dr.findById (id);
    return documento;
  }

  public List<Link> getLink () {
    Iterable<Link> links = lr.findAll ();
    List<Link> resposta = new LinkedList ();
    for (Link link : links) {
      resposta.add (link);
    }
    return resposta;
  }

  public Link getLink (long id) {
    Link link = lr.findById (id);
    return link;
  }

  public List<Link> encontrarLinkUrl (String url) {
    return lr.findByUrlIgnoreCaseContaining (url);
  }

  public List<Link> listarEmOrdemAlfabetica () {
    return lr.getInLexicalOrder ();
  }
}

/*
        Hashtable historico = new Hashtable();

    public List adicionaUrl() {
        List<Documento2> documentos = new LinkedList();

        try {
            List<URL> urlsSementes = new LinkedList();

            urlsSementes.add(new URL("https://globoesporte.globo.com/futebol/times/cruzeiro"));
            urlsSementes.add(new URL("https://www.oi.com.br/"));
            
            while (!urlsSementes.isEmpty()) {
                
                URL url = (URL) urlsSementes.remove(0);
                
                if (this.protocoloDeExclusao(url) == true) {
                    documentos.add(this.metodoColetor(url));
                } else {
                    urlsSementes.add(url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return documentos;
    }

    public Documento2 coletar(URL url) {
        Documento2 d = new Documento2();

        try {
            Document doc = Jsoup.connect(url.toString()).get();
            Elements links = doc.select("a[href]");

            d.setUrl(url);
            d.setTexto(doc.html());
            d.setVisao(doc.text());

            List<String> urls = new LinkedList();
            for (Element link : links) {
                if ((!link.attr("abs:href").equals("") && (link.attr("abs:href") != null))) {
                    urls.add(link.attr("abs:href"));
                }
            }
            d.setUrls(urls);
        } catch (Exception e) {
            System.out.println("Erro ao realizar a coleta do documento.");
            e.printStackTrace();
        }
        return d;
    }

    public Documento2 metodoColetor(URL url) {
        Documento2 d = new Documento2();

        try {
            Document doc = Jsoup.connect(url.toString()).get();
            Elements links = doc.select("a[href]");
            d.setUrl(url);
            d.setHost(url.getHost());
            d.setTexto(doc.html());
            d.setVisao(doc.text());
            d.setDataColeta();
            List<String> urls = new LinkedList();

            for (Element link : links) {
                if ((!link.attr("abs:href").equals("") && (link.attr("abs:href") != null))) {
                    urls.add(link.attr("abs:href"));
                }
            }

            d.setUrls(urls);
        } catch (Exception e) {
            System.out.println("Erro ao coletar a página.");
            e.printStackTrace();
        }
        return d;

    }

    public boolean protocoloDeExclusao(URL url) {

        boolean resp = false;
        String host = url.getHost();

        Long tempoAnterior = (Long) this.historico.get(host);
        protocoloDePermissao(url);
        if (tempoAnterior == null) {
            resp = true;
            this.historico.put(host, Instant.now().toEpochMilli());
            protocoloDePermissao(url);
        } else {
            Long tempoCorrente = Instant.now().toEpochMilli();

            if ((tempoCorrente - tempoAnterior) >= 10000) {
                resp = true;
                this.historico.put(host, Instant.now().toEpochMilli());
                protocoloDePermissao(url);
            } else {
                resp = false;
                long sec = tempoCorrente - tempoAnterior;
            }
        }
        return resp;
    }

    public void protocoloDePermissao(URL url) {
        StringBuilder pagina = new StringBuilder();
        try {
            String host = "https://" + url.getHost() + "/robots.txt";
            URL urlRobotsTxt = new URL(host);
            URLConnection url_connection = urlRobotsTxt.openConnection();
            InputStream is = url_connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader buffer = new BufferedReader(reader);
            String linha;
            while ((linha = buffer.readLine()) != null) {
                if (linha.toLowerCase().contains("disallow")) {
                    String[] partes = linha.split(": ");
                    if (partes.length > 1) {
                        System.out.println("https://" + url.getHost() + partes[1]);
                    }
                }
            }
        } catch (IOException e) {
            pagina.append("Erro: não foi possível coletar a página.");
        }
    }
 */
