---
layout: model
title: Mapping Drug Brand Names with Corresponding National Drug Codes
author: John Snow Labs
name: drug_brandname_ndc_mapper
date: 2022-05-11
tags: [chunk_mapper, en, licensed, ndc]
task: Chunk Mapping
language: en
edition: Spark NLP for Healthcare 3.5.1
spark_version: 3.0
supported: true
article_header:
  type: cover
use_language_switcher: "Python-Scala-Java"
---

## Description

This pretrained model maps drug brand names to corresponding National Drug Codes (NDC). Product NDCs for each strength are returned in result and metadata.

## Predicted Entities



{:.btn-box}
<button class="button button-orange" disabled>Live Demo</button>
<button class="button button-orange" disabled>Open in Colab</button>
[Download](https://s3.amazonaws.com/auxdata.johnsnowlabs.com/clinical/models/drug_brandname_ndc_mapper_en_3.5.1_3.0_1652259542096.zip){:.button.button-orange.button-orange-trans.arr.button-icon}

## How to use



<div class="tabs-box" markdown="1">
{% include programmingLanguageSelectScalaPythonNLU.html %}
```python
document_assembler = DocumentAssembler()\
      .setInputCol("text")\
      .setOutputCol("chunk")

chunkerMapper = ChunkMapperModel.pretrained("drug_brandname_ndc_mapper", "en", "clinical/models")\
      .setInputCols(["chunk"])\
      .setOutputCol("ndc")\
      .setRel("Strength_NDC") 

pipeline = Pipeline().setStages([document_assembler,
                                 chunkerMapper])  

model = pipeline.fit(spark.createDataFrame([['']]).toDF('text')) 

lp = LightPipeline(model)

res = lp.fullAnnotate(["zytiga", "zyvana", "ZYVOX", "ZYTIGA"])
```
```scala
val document_assembler = DocumentAssembler()\
      .setInputCol("text")\
      .setOutputCol("chunk")

val chunkerMapper = ChunkMapperModel.pretrained("drug_brandname_ndc_mapper", "en", "clinical/models")\
      .setInputCols(["chunk"])\
      .setOutputCol("ndc")\
      .setRel("Strength_NDC") 

val pipeline = Pipeline().setStages(Array(document_assembler,
                                 chunkerMapper))

 val text_data = Seq("zytiga", "zyvana", "ZYVOX", "ZYTIGA").toDF("text")
 val res = pipeline.fit(text_data).transform(text_data)
```
</div>

## Results

```bash
|---:|:------------|:-------------------------|:----------------------------------------------------------|
|    | Brandname   | Strenth_NDC              | Other_NDSs                                                |
|---:|:------------|:-------------------------|:----------------------------------------------------------|
|  0 | zytiga      | 500 mg/1 | 57894-195     | ['250 mg/1 | 57894-150']                                  |
|  1 | zyvana      | 527 mg/1 | 69336-405     | ['']                                                      |
|  2 | ZYVOX       | 600 mg/300mL | 0009-4992 | ['600 mg/300mL | 66298-7807', '600 mg/300mL | 0009-7807'] |
|  3 | ZYTIGA      | 500 mg/1 | 57894-195     | ['250 mg/1 | 57894-150']                                  |
|---:|:------------|:-------------------------|:----------------------------------------------------------|
```

{:.model-param}
## Model Information

{:.table-model}
|---|---|
|Model Name:|drug_brandname_ndc_mapper|
|Compatibility:|Spark NLP for Healthcare 3.5.1+|
|License:|Licensed|
|Edition:|Official|
|Input Labels:|[chunk]|
|Output Labels:|[mappings]|
|Language:|en|
|Size:|3.0 MB|
