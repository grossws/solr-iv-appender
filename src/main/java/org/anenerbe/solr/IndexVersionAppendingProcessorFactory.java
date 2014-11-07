package org.anenerbe.solr;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.processor.UpdateRequestProcessor;
import org.apache.solr.update.processor.UpdateRequestProcessorFactory;
import org.apache.solr.util.plugin.SolrCoreAware;

import java.io.IOException;

import static org.apache.solr.common.SolrException.ErrorCode.SERVER_ERROR;

public class IndexVersionAppendingProcessorFactory extends UpdateRequestProcessorFactory implements SolrCoreAware {
  protected SolrCore core;

  protected String fieldName;

  @Override
  public void init(NamedList args) {
    Object obj = args.remove("fieldName");
    if (null != obj) {
      fieldName = obj.toString();
    }

    if (StringUtils.isEmpty(fieldName)) {
      fieldName = "iv";
    }

    if (0 < args.size()) {
      throw new SolrException(SERVER_ERROR, "Unexpected init param(s): '" + args.getName(0) + "'");
    }
  }

  /**
   * Method is called after factory is fully initialized
   * See <a href="https://wiki.apache.org/solr/SolrPlugins#line-248">lifecycle</a>.
   *
   * @param core
   */
  @Override
  public void inform(SolrCore core)
      throws SolrException {
    this.core = core;
    checkVersionField(fieldName, core.getLatestSchema());
  }

  @Override
  public UpdateRequestProcessor getInstance(SolrQueryRequest req, SolrQueryResponse resp, UpdateRequestProcessor next) {
    return new IndexVersionAppendingProcessor(core, fieldName, next);
  }

  public static void checkVersionField(String fieldName, IndexSchema schema)
      throws SolrException {
    final String errPrefix = fieldName + " field must exist in schema, using indexed=\"true\", stored=\"true\" and multiValued=\"false\"";
    SchemaField sf = schema.getFieldOrNull(fieldName);

    if (null == sf) {
      throw new SolrException(SolrException.ErrorCode.SERVER_ERROR,
                              errPrefix + " (" + fieldName + " does not exist)");
    }
    if (!sf.indexed() && !sf.hasDocValues()) {
      throw new SolrException(SolrException.ErrorCode.SERVER_ERROR,
                              errPrefix + " (" + fieldName + " must be indexed)");
    }
    if (!sf.stored()) {
      throw new SolrException(SolrException.ErrorCode.SERVER_ERROR,
                              errPrefix + " (" + fieldName + " is not stored)");
    }
    if (sf.multiValued()) {
      throw new SolrException(SolrException.ErrorCode.SERVER_ERROR,
                              errPrefix + " (" + fieldName + " is multiValued)");
    }
  }

  static class IndexVersionAppendingProcessor extends UpdateRequestProcessor {
    private SolrCore core;
    private String fieldName;

    public IndexVersionAppendingProcessor(SolrCore core, String fieldName, UpdateRequestProcessor next) {
      super(next);
      this.core = core;
      this.fieldName = fieldName;
    }

    @Override
    public void processAdd(AddUpdateCommand cmd) throws IOException {
      long gen = core.getDeletionPolicy().getLatestCommit().getGeneration();
      cmd.getSolrInputDocument().setField(fieldName, gen);

      super.processAdd(cmd);
    }
  }
}

