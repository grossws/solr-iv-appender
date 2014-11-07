package org.anenerbe.solr;

import org.apache.solr.common.util.NamedList;
import org.apache.solr.handler.RequestHandlerBase;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import java.net.MalformedURLException;
import java.net.URL;

public class IndexVersionHandler extends RequestHandlerBase {
  @Override
  public void init(NamedList args) {
    super.init(args);
    httpCaching = false;
  }

  @Override
  public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse resp)
      throws Exception {
    synchronized (this) {
      long generation = req.getCore().getDeletionPolicy().getLatestCommit().getGeneration();
      resp.add("iv", generation);
    }
  }

  //region=mbean
  private static final String REPO_URL = "https://github.com/grossws/solr-iv-appender";
  private static final URL[] DOC_URLS;

  static {
    URL[] docUrls;
    try {
      docUrls = new URL[]{new URL(REPO_URL)};
    } catch (MalformedURLException e) {
      // do nothing if docs URL can't be parsed
      docUrls = null;
    }

    DOC_URLS = docUrls;
  }

  @Override
  public String getDescription() {
    return "returns last index version (generation)";
  }

  @Override
  public String getSource() {
    return REPO_URL;
  }

  @Override
  public URL[] getDocs() {
    return DOC_URLS;
  }
  //endregion
}
