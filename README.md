# Info


Index version appender and querying extension for [Apache Solr][solr].

- [IndexVersionAppendingProcessorFactory](src/java/org/anenerbe/solr/IndexVersionAppendingProcessorFactory.java) (which is simple [UpdateRequestProcessor][solr-urp])

  Each added/updated document passed this processor will receive last committed index version (generation in solr terms).
  Field should be _indexer_, _stored_ and _single-valued_.

- [IndexVersionHandler](src/java/org/anenerbe/solr/IndexVersionHandler.java) (which is simple [SolrRequestHandler][solr-rh])

  Returns last committed index version in `iv` field


[solr]: http://lucene.apache.org/
[solr-urp]: https://github.com/apache/lucene-solr/blob/trunk/solr/core/src/java/org/apache/solr/update/processor/UpdateRequestProcessor.java
[solr-rh]: https://github.com/apache/lucene-solr/blob/trunk/solr/core/src/java/org/apache/solr/request/SolrRequestHandler.java

# Usage

## IndexVersionHandler

Add `org.anenerbe.solr.IndexVersionHandler` as handler to `solrconfig.xml`:

```xml
<config>
  <!-- ... -->

  <requestHandler name="/iv" class="org.anenerbe.solr.IndexVersionHandler">
    <lst name="defaults">
      <str name="wt">json</str>
    </lst>
  </requestHandler>

  <!-- ... -->
</config>
```

## IndexVersionAppendingProcessorFactory

Add some field to solr `schema.xml`:

```xml
<schema name="example" version="1.5">
  <!-- ... -->

  <field name="iv" type="long" indexed="true" stored="true"/>

  <!-- ... -->
</schema>
```

and `org.anenerbe.solr.IndexVersionAppendingProcessorFactory` to some `updateRequestProcessorChain` to `solrconfig.xml`:

```xml
<config>
  <!-- ... -->

  <updateRequestProcessorChain name="indexversion" default="true">
    <processor class="org.anenerbe.solr.IndexVersionAppendingProcessorFactory">
      <!-- should be appropriate field from `schema.xml` -->
      <!-- `iv` is default value -->
      <str name="fieldName">iv</str>
    </processor>
    <processor class="solr.LogUpdateProcessorFactory"/>
    <processor class="solr.RunUpdateProcessorFactory"/>
  </updateRequestProcessorChain>

  <!-- ... -->
</config>
```

# Licensing

Licensed under MIT License. See [LICENSE](LICENSE) file.

