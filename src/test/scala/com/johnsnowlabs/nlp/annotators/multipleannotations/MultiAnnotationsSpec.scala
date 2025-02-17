/*
 * Copyright 2017-2022 John Snow Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.johnsnowlabs.nlp.annotators.multipleannotations

import com.johnsnowlabs.nlp.{DocumentAssembler, LightPipeline, SparkAccessor}
import com.johnsnowlabs.tags.FastTest
import org.apache.spark.ml.Pipeline
import org.scalatest.flatspec.AnyFlatSpec
import com.johnsnowlabs.nlp.Annotation
import org.junit.Assert.assertEquals

class MultiAnnotationsSpec extends AnyFlatSpec {

  import SparkAccessor.spark.implicits._

  "An multiple anootator chunks" should "transform data " taggedAs FastTest in {
    val data =
      SparkAccessor.spark.sparkContext.parallelize(Seq("Example text")).toDS().toDF("text")

    val documentAssembler = new DocumentAssembler()
      .setInputCol("text")
      .setOutputCol("document")

    val documentAssembler2 = new DocumentAssembler()
      .setInputCol("text")
      .setOutputCol("document2")

    val documentAssembler3 = new DocumentAssembler()
      .setInputCol("text")
      .setOutputCol("document3")

    val multipleColumns = new MultiColumnApproach()
      .setInputCols("document", "document2", "document3")
      .setOutputCol("multiple_document")

    val pipeline = new Pipeline()
      .setStages(
        Array(documentAssembler, documentAssembler2, documentAssembler3, multipleColumns))

    val pipelineModel = pipeline.fit(data)

    val annotations =
      Annotation.collect(pipelineModel.transform(data), "multiple_document").flatten
    assertEquals(annotations.length, 3)

    val result = new LightPipeline(pipelineModel).annotate("My document")

    assertEquals(result("multiple_document").size, 3)

  }

}
