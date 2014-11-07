# Info

Simple [UpdateRequestProcessor][solr-urp] and factory for [Apache Solr][solr].

Each added/updated document passed this processor will receive last commited index version (generation in solr terms).
Field should be _indexer_, _stored_ and _single-valued_.

[solr]: http://lucene.apache.org/
[solr-urp]: https://github.com/apache/lucene-solr/blob/trunk/solr/core/src/java/org/apache/solr/update/processor/UpdateRequestProcessor.java

# Usage

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

