---
layout: model
title: Japanese Bert Embeddings (from izumi-lab)
author: John Snow Labs
name: bert_embeddings_bert_small_japanese
date: 2022-04-11
tags: [bert, embeddings, ja, open_source]
task: Embeddings
language: ja
edition: Spark NLP 3.4.2
spark_version: 3.0
supported: true
article_header:
  type: cover
use_language_switcher: "Python-Scala-Java"
---

## Description

Pretrained Bert Embeddings model, uploaded to Hugging Face, adapted and imported into Spark NLP. `bert-small-japanese` is a Japanese model orginally trained by `izumi-lab`.

{:.btn-box}
<button class="button button-orange" disabled>Live Demo</button>
<button class="button button-orange" disabled>Open in Colab</button>
[Download](https://s3.amazonaws.com/auxdata.johnsnowlabs.com/public/models/bert_embeddings_bert_small_japanese_ja_3.4.2_3.0_1649674508136.zip){:.button.button-orange.button-orange-trans.arr.button-icon}

## How to use



<div class="tabs-box" markdown="1">
{% include programmingLanguageSelectScalaPythonNLU.html %}
```python
documentAssembler = DocumentAssembler() \
    .setInputCol("text") \
    .setOutputCol("document")

tokenizer = Tokenizer() \
    .setInputCols("document") \
    .setOutputCol("token")
  
embeddings = BertEmbeddings.pretrained("bert_embeddings_bert_small_japanese","ja") \
    .setInputCols(["document", "token"]) \
    .setOutputCol("embeddings")
    
pipeline = Pipeline(stages=[documentAssembler, tokenizer, embeddings])

data = spark.createDataFrame([["私はSpark NLPを愛しています"]]).toDF("text")

result = pipeline.fit(data).transform(data)
```
```scala
val documentAssembler = new DocumentAssembler() 
      .setInputCol("text") 
      .setOutputCol("document")
 
val tokenizer = new Tokenizer() 
    .setInputCols(Array("document"))
    .setOutputCol("token")

val embeddings = BertEmbeddings.pretrained("bert_embeddings_bert_small_japanese","ja") 
    .setInputCols(Array("document", "token")) 
    .setOutputCol("embeddings")

val pipeline = new Pipeline().setStages(Array(documentAssembler, tokenizer, embeddings))

val data = Seq("私はSpark NLPを愛しています").toDF("text")

val result = pipeline.fit(data).transform(data)
```
</div>

{:.model-param}
## Model Information

{:.table-model}
|---|---|
|Model Name:|bert_embeddings_bert_small_japanese|
|Compatibility:|Spark NLP 3.4.2+|
|License:|Open Source|
|Edition:|Official|
|Input Labels:|[sentence, token]|
|Output Labels:|[bert]|
|Language:|ja|
|Size:|68.3 MB|
|Case sensitive:|true|

## References

- https://huggingface.co/izumi-lab/bert-small-japanese
- https://github.com/google-research/bert
- https://github.com/retarfi/language-pretraining/tree/v1.0
- https://arxiv.org/abs/2003.10555
- https://arxiv.org/abs/2003.10555
- https://creativecommons.org/licenses/by-sa/4.0/