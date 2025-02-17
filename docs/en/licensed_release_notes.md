---
layout: docs
header: true
seotitle: Spark NLP for Healthcare | John Snow Labs
title: Spark NLP for Healthcare Release Notes
permalink: /docs/en/licensed_release_notes
key: docs-licensed-release-notes
modify_date: 2021-07-14
show_nav: true
sidebar:
    nav: sparknlp-healthcare
---

## 3.5.2

#### Highlights

+ `TFGraphBuilder` annotator to create graphs for training NER, Assertion, Relation Extraction, and Generic Classifier models
+ Default TF graphs added for `AssertionDLApproach` to let users train models without custom graphs
+ New functionalities in `ContextualParserApproach`
+ Printing the list of clinical pretrained models and pipelines with one-liner
+ New clinical models
  - Clinical NER model (`ner_biomedical_bc2gm`)
  - Clinical `ChunkMapper` models (`abbreviation_mapper`, `rxnorm_ndc_mapper`, `drug_brandname_ndc_mapper`, `rxnorm_action_treatment_mapper`)
+ Bug fixes
+ New and updated notebooks
+ List of recently updated or added models

#### `TFGraphBuilder` annotator to create graphs for Training NER, Assertion, Relation Extraction, and Generic Classifier Models

We have a new annotator used to create graphs in the model training pipeline. `TFGraphBuilder` inspects the data and creates the proper graph if a suitable version of TensorFlow (<= 2.7 ) is available. The graph is stored in the defined folder and loaded by the approach.

You can use this builder with `MedicalNerApproach`, `RelationExtractionApproach`, `AssertionDLApproach`, and `GenericClassifierApproach`

*Example:*

```python
graph_folder_path = "./medical_graphs"

med_ner_graph_builder = TFGraphBuilder()\
    .setModelName("ner_dl")\
    .setInputCols(["sentence", "token", "embeddings"]) \
    .setLabelColumn("label")\
    .setGraphFile("auto")\
    .setHiddenUnitsNumber(20)\
    .setGraphFolder(graph_folder_path)

med_ner = MedicalNerApproach() \
    ...
    .setGraphFolder(graph_folder)

medner_pipeline = Pipeline()([
    ...,
    med_ner_graph_builder,
    med_ner    
    ])
```

For more examples, please check [TFGraph Builder Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/17.Graph_builder_for_DL_models.ipynb).

#### Default TF graphs added for `AssertionDLApproach` to let users train models without custom graphs

We added default TF graphs for the `AssertionDLApproach` to let users train assertion models without specifying any custom TF graph.

**Default Graph Features:**
+ Feature Sizes: 100, 200, 768
+ Number of Classes: 2, 4, 8

#### New Functionalities in `ContextualParserApproach`

+ Added `.setOptionalContextRules` parameter that allows to output regex matches regardless of context match (prefix, suffix configuration).
+ Allows sending a JSON string of the configuration file to `setJsonPath` parameter.

**Confidence Value Scenarios:**

1. When there is regex match only, the confidence value will be 0.5.
2. When there are regex and prefix matches together, the confidence value will be > 0.5 depending on the distance between target token and the prefix.
3. When there are regex and suffix matches together, the confidence value will be > 0.5 depending on the distance between target token and the suffix.
4. When there are regex, prefix, and suffix matches all together, the confidence value will be > than the other scenarios.

*Example*:

```python
jsonString = {
    "entity": "CarId",
    "ruleScope": "sentence",
    "completeMatchRegex": "false",
    "regex": "\\d+",
    "prefix": ["red"],
    "contextLength": 100
}

with open("jsonString.json", "w") as f:
    json.dump(jsonString, f)

contextual_parser = ContextualParserApproach()\
    .setInputCols(["sentence", "token"])\
    .setOutputCol("entity")\
    .setJsonPath("jsonString.json")\
    .setCaseSensitive(True)\
    .setOptionalContextRules(True)
```

#### Printing the List of Clinical Pretrained Models and Pipelines with One-Liner

  Now we can check what the clinical model names are of a specific annotator and the names of clinical pretrained pipelines in a language.

  + **Listing Clinical Model Names:**

*Example*:

```python
from sparknlp_jsl.pretrained import InternalResourceDownloader

InternalResourceDownloader.showPrivateModels("AssertionDLModel")
```

*Results*:

```bash
+-----------------------------------+------+---------+
| Model                             | lang | version |
+-----------------------------------+------+---------+
| assertion_ml                      |  en  | 2.0.2   |
| assertion_dl                      |  en  | 2.0.2   |
| assertion_dl_healthcare           |  en  | 2.7.2   |
| assertion_dl_biobert              |  en  | 2.7.2   |
| assertion_dl                      |  en  | 2.7.2   |
| assertion_dl_radiology            |  en  | 2.7.4   |
| assertion_jsl_large               |  en  | 3.1.2   |
| assertion_jsl                     |  en  | 3.1.2   |
| assertion_dl_scope_L10R10         |  en  | 3.4.2   |
| assertion_dl_biobert_scope_L10R10 |  en  | 3.4.2   |
+-----------------------------------+------+---------+
```

+ **Listing Clinical Pretrained Pipelines:**

```python
from sparknlp_jsl.pretrained import InternalResourceDownloader

InternalResourceDownloader.showPrivatePipelines("en")
```

```bash
+--------------------------------------------------------+------+---------+
| Pipeline                                               | lang | version |
+--------------------------------------------------------+------+---------+
| clinical_analysis                                      |  en  | 2.4.0   |
| clinical_ner_assertion                                 |  en  | 2.4.0   |
| clinical_deidentification                              |  en  | 2.4.0   |
| clinical_analysis                                      |  en  | 2.4.0   |
| explain_clinical_doc_ade                               |  en  | 2.7.3   |
| icd10cm_snomed_mapping                                 |  en  | 2.7.5   |
| recognize_entities_posology                            |  en  | 3.0.0   |
| explain_clinical_doc_carp                              |  en  | 3.0.0   |
| recognize_entities_posology                            |  en  | 3.0.0   |
| explain_clinical_doc_ade                               |  en  | 3.0.0   |
| explain_clinical_doc_era                               |  en  | 3.0.0   |
| icd10cm_snomed_mapping                                 |  en  | 3.0.2   |
| snomed_icd10cm_mapping                                 |  en  | 3.0.2   |
| icd10cm_umls_mapping                                   |  en  | 3.0.2   |
| snomed_umls_mapping                                    |  en  | 3.0.2   |
| ...                                                    |  ... | ...     |
+--------------------------------------------------------+------+---------+
```

#### New `ner_biomedical_bc2gm` NER Model

This model has been trained to extract genes/proteins from a medical text.

See [Model Card](https://nlp.johnsnowlabs.com/2022/05/10/ner_biomedical_bc2gm_en_3_0.html) for more details.

*Example* :

```python
...
ner = MedicalNerModel.pretrained("ner_biomedical_bc2gm", "en", "clinical/models")\
    .setInputCols(["sentence", "token", "embeddings"]) \
    .setOutputCol("ner")
...

text = spark.createDataFrame([["Immunohistochemical staining was positive for S-100 in all 9 cases stained, positive for HMB-45 in 9 (90%) of 10, and negative for cytokeratin in all 9 cases in which myxoid melanoma remained in the block after previous sections."]]).toDF("text")

result = model.transform(text)
```
*Results* :

```
+-----------+------------+
|chunk      |ner_label   |
+-----------+------------+
|S-100      |GENE_PROTEIN|
|HMB-45     |GENE_PROTEIN|
|cytokeratin|GENE_PROTEIN|
+-----------+------------+
```

#### New Clinical `ChunkMapper` Models

We have 4 new `ChunkMapper` models and a new [Chunk Mapping Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/26.Chunk_Mapping.ipynb) for showing their examples.

+ `drug_brandname_ndc_mapper`: This model maps drug brand names to corresponding National Drug Codes (NDC). Product NDCs for each strength are returned in result and metadata.

See [Model Card](https://nlp.johnsnowlabs.com/2022/05/11/drug_brandname_ndc_mapper_en_3_0.html) for more details.

*Example* :

```python
document_assembler = DocumentAssembler()\
      .setInputCol("text")\
      .setOutputCol("chunk")

chunkerMapper = ChunkMapperModel.pretrained("drug_brandname_ndc_mapper", "en", "clinical/models")\
      .setInputCols(["chunk"])\
      .setOutputCol("ndc")\
      .setRel("Strength_NDC")

model = PipelineModel(stages=[document_assembler,
                                 chunkerMapper])  

light_model = LightPipeline(model)
res = light_model.fullAnnotate(["zytiga", "ZYVOX", "ZYTIGA"])
```

*Results* :
```bash
+-------------+--------------------------+-----------------------------------------------------------+
| Brandname   | Strenth_NDC              | Other_NDSs                                                |
+-------------+--------------------------+-----------------------------------------------------------+
| zytiga      | 500 mg/1 | 57894-195     | ['250 mg/1 | 57894-150']                                  |
| ZYVOX       | 600 mg/300mL | 0009-4992 | ['600 mg/300mL | 66298-7807', '600 mg/300mL | 0009-7807'] |
| ZYTIGA      | 500 mg/1 | 57894-195     | ['250 mg/1 | 57894-150']                                  |
+-------------+--------------------------+-----------------------------------------------------------+

```

+ `abbreviation_mapper`: This model maps abbreviations and acronyms of medical regulatory activities with their definitions.

See [Model Card](https://nlp.johnsnowlabs.com/2022/05/11/abbreviation_mapper_en_3_0.html) for details.

*Example:*

```bash
input = ["""Gravid with estimated fetal weight of 6-6/12 pounds.
            LABORATORY DATA: Laboratory tests include a CBC which is normal. 
            HIV: Negative. One-Hour Glucose: 117. Group B strep has not been done as yet."""]
           
>> output:
+------------+----------------------------+
|Abbreviation|Definition                  |
+------------+----------------------------+
|CBC         |complete blood count        |
|HIV         |human immunodeficiency virus|
+------------+----------------------------+
```

+ `rxnorm_action_treatment_mapper`: RxNorm and RxNorm Extension codes with their corresponding action and treatment. Action refers to the function of the drug in various body systems; treatment refers to which disease the drug is used to treat.

See [Model Card](https://nlp.johnsnowlabs.com/2022/05/08/rxnorm_action_treatment_mapper_en_3_0.html) for more details.

*Example:*

```bash
input = ['Sinequan 150 MG', 'Zonalon 50 mg']
           
>> output:
+---------------+------------+---------------+
|chunk          |rxnorm_code |Action         |
+---------------+------------+---------------+
|Sinequan 150 MG|1000067     |Antidepressant |
|Zonalon 50 mg  |103971      |Analgesic      |
+---------------+------------+---------------+
```

+ `rxnorm_ndc_mapper`: This pretrained model maps RxNorm and RxNorm Extension codes with corresponding National Drug Codes (NDC).

See [Model Card](https://nlp.johnsnowlabs.com/2022/05/09/rxnorm_ndc_mapper_en_3_0.html) for more details.

*Example:*

```bash
input = ['doxepin hydrochloride 50 MG/ML', 'macadamia nut 100 MG/ML']
           
>> output:
+------------------------------+------------+------------+
|chunk                         |rxnorm_code |Product NDC |
+------------------------------+------------+------------+
|doxepin hydrochloride 50 MG/ML|1000091     |00378-8117  |
|macadamia nut 100 MG/ML       |212433      |00064-2120  |
+------------------------------+------------+------------+
```

#### Bug Fixes

We fixed some issues in `DrugNormalizer`, `DateNormalizer` and `ContextualParserApproach` annotators.

+ **`DateNormalizer`** : We fixed some relative date issues and also `DateNormalizer` takes account the Leap years now.
+ **`DrugNormalizer`** : Fixed some formats.
+ **`ContextualParserApproach`** :
  - Computing the right distance for prefix.
  - Extracting the right content for suffix.
  - Handling special characters in prefix and suffix.


#### New and Updated Notebooks
  - We prepared [Spark NLP for Healthcare 3hr Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/1hr_workshop/SparkNLP_for_Healthcare_3h_Notebook.ipynb) to cover mostly used components of Spark NLP in ODSC East 2022-3 hours hands-on workshop on 'Modular Approach to Solve Problems at Scale in Healthcare NLP'. You can also find its Databricks version [here](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/databricks/python/healthcare_tutorials/SparkNLP_for_Healthcare_3h_Notebook.ipynb).
  - New [Chunk Mapping Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/26.Chunk_Mapping.ipynb) for showing the examples of Chunk Mapper models.
  - [Updated healthcare tutorial notebooks for Databricks](https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/tutorials/Certification_Trainings/Healthcare/databricks_notebooks) with `sparknlp_jsl` v3.5.1
  - We have a new [Databricks healthcare tutorials folder](https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/databricks/python/healthcare_tutorials) in which you can find all Spark NLP for Healthcare Databricks tutorial notebooks. 
  - [Updated Graph Builder Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/17.Graph_builder_for_DL_models.ipynb) by adding the examples of new `TFGraphBuilder` annotator.

#### List of Recently Updated or Added Models

- `sbiobertresolve_rxnorm_action_treatment`
- `ner_biomedical_bc2gm`
- `abbreviation_mapper`
- `rxnorm_ndc_mapper`
- `drug_brandname_ndc_mapper`
- `sbiobertresolve_cpt_procedures_measurements_augmented`
- `sbiobertresolve_icd10cm_slim_billable_hcc`
- `sbiobertresolve_icd10cm_slim_normalized`

**For all Spark NLP for healthcare models, please check : [Models Hub Page](https://nlp.johnsnowlabs.com/models?edition=Spark+NLP+for+Healthcare)**


## 3.5.1
We are glad to announce that 3.5.1 version of Spark NLP for Healthcare has been released!

#### Highlights
- **Deidentification**:
  - New **Portuguese** **Deidentification** NER models and pretrained pipeline. This is the 6th supported language for deidentification (English, German, Spanish, Italian, French and Portuguese).
- **New pretrained models and pipelines**:
  - New **RxNorm** Sentence Entity Resolver model to map and extract pharmaceutical actions (e.g. analgesic, hypoglycemic) as well as treatments (e.g. backache, diabetes) along with the RxNorm code resolved (`sbiobertresolve_rxnorm_action_treatment`)
  - New **RCT** classification models and pretrained pipelines to classify the sections within the abstracts of scientific articles regarding randomized clinical trials (RCT). (`rct_binary_classifier_use`, `rct_binary_classifier_biobert`, `bert_sequence_classifier_binary_rct_biobert`, `rct_binary_classifier_use_pipeline`, `rct_binary_classifier_biobert_pipeline`, `bert_sequence_classifier_binary_rct_biobert_pipeline`)
- **New features**:
  - Add `getClasses()` attribute for `MedicalBertForTokenClassifier` and `MedicalBertForSequenceClassification` to find out the entity classes of the models
  - Download the AnnotatorModels from the healthcare library using the Healthcare version instead of the open source version (the pretrained models were used to be dependent on open source Spark NLP version before)
  - New functionality to download and extract clinical models from S3 via direct zip url.
- **Core improvements**:
  - Fixing the confidence scores in `MedicalNerModel` when `setIncludeAllConfidenceScores` is true
  - Graph_builder `relation_extraction` model file name extension problem with `auto` parameter.

- **List of recently updated or added models**

#### Portuguese Deidentification Models

This is the 6th supported language for deidentification (English, German, Spanish, Italian, French and Portuguese). This version includes two Portuguese deidentification models to mask or obfuscate Protected Health Information in the Portuguese language. The models are the following:

- `ner_deid_generic`:  extracts `Name`, `Profession`, `Age`, `Date`, `Contact` (Telephone numbers, Email addresses), `Location` (Address, City, Postal code, Hospital Name, Organization), `ID` (Social Security numbers, Medical record numbers) and `Sex` entities.

   See [Model Hub Page](https://nlp.johnsnowlabs.com/2022/04/13/ner_deid_generic_pt_3_0.html) for details.

- `ner_deid_subentity`: `Patient` (name), `Hospital` (name), `Date`, `Organization`, `City`, `ID`, `Street`, `Sex`, `Email`, `ZIP`, `Profession`, `Phone`, `Country`, `Doctor` (name) and `Age`

  See [Model Hub Page](https://nlp.johnsnowlabs.com/2022/04/13/ner_deid_subentity_pt_3_0.html) for details.

You will use the `w2v_cc_300d` Portuguese Embeddings with these models. The pipeline should look as follows:
```python
...
word_embeddings = WordEmbeddingsModel.pretrained("w2v_cc_300d", "pt")\
    .setInputCols(["sentence","token"])\
    .setOutputCol("embeddings")

ner_subentity = MedicalNerModel.pretrained("ner_deid_subentity", "pt", "clinical/models")\    
    .setInputCols(["sentence","token","embeddings"])\
    .setOutputCol("ner_deid_subentity")

ner_converter_subentity = NerConverter()\
    .setInputCols(["sentence","token","ner_deid_subentity"])\
    .setOutputCol("ner_chunk_subentity")

ner_generic = MedicalNerModel.pretrained("ner_deid_generic", "pt", "clinical/models")\    
    .setInputCols(["sentence","token","embeddings"])\
    .setOutputCol("ner_deid_generic")

ner_converter_generic = NerConverter()\
    .setInputCols(["sentence","token","ner_deid_generic"])\
    .setOutputCol("ner_chunk_generic")

nlpPipeline = Pipeline(stages=[
      documentAssembler,
      sentencerDL,
      tokenizer,
      word_embeddings,
      ner_subentity,
      ner_converter_subentity,
      ner_generic,
      ner_converter_generic,
      ])

text = """Detalhes do paciente.
Nome do paciente:  Pedro Gonçalves
NHC: 2569870.
Endereço: Rua Das Flores 23.
Código Postal: 21754-987.
Dados de cuidados.
Data de nascimento: 10/10/1963.
Idade: 53 anos
Data de admissão: 17/06/2016.
Doutora: Maria Santos"""

data = spark.createDataFrame([[text]]).toDF("text")
results = nlpPipeline.fit(data).transform(data)

```

Results:
```
+-----------------+-------------------------------------+
|chunk            |ner_generic_label|ner_subentity_label|
+-----------------+-------------------------------------+
|Pedro Gonçalves  |      NAME       |      PATIENT      |
|2569870          |      ID         |      ID           |
|Rua Das Flores 23|      LOCATION   |      STREET       |
|21754-987        |      LOCATION   |      ZIP          |
|10/10/1963       |      DATE       |      DATE         |
|53               |      AGE        |      AGE          |
|17/06/2016       |      DATE       |      DATE         |
|Maria Santos     |      NAME       |      DOCTOR       |
+-----------------+-------------------------------------+
```

We also include a Clinical Deidentification Pipeline for Portuguese that uses `ner_deid_subentity` NER model and also several `ContextualParsers` for rule based contextual Named Entity Recognition tasks. It's available to be used as follows:

```python
from sparknlp.pretrained import PretrainedPipeline

deid_pipeline = PretrainedPipeline("clinical_deidentification", "pt", "clinical/models")
```

The pretrained pipeline comes with Deidentification and Obfuscation capabilities as shows the following example:

```
text = """RELAÇÃO HOSPITALAR
NOME: Pedro Gonçalves
NHC: MVANSK92F09W408A
ENDEREÇO: Rua Burcardo 7
CÓDIGO POSTAL: 80139
DATA DE NASCIMENTO: 03/03/1946
IDADE: 70 anos
SEXO: Homens
E-MAIL: pgon21@tim.pt
DATA DE ADMISSÃO: 12/12/2016
DOUTORA: Eva Andrade
RELATO CLÍNICO: 70 anos, aposentado, sem alergia a medicamentos conhecida, com a seguinte história: ex-acidente de trabalho com fratura de vértebras e costelas; operado de doença de Dupuytren na mão direita e ponte ílio-femoral esquerda; diabetes tipo II, hipercolesterolemia e hiperuricemia; alcoolismo ativo, fuma 20 cigarros/dia.
Ele foi encaminhado a nós por apresentar hematúria macroscópica pós-evacuação em uma ocasião e microhematúria persistente posteriormente, com evacuação normal.
O exame físico mostrou bom estado geral, com abdome e genitais normais; o toque retal foi compatível com adenoma de próstata grau I/IV.
A urinálise mostrou 4 hemácias/campo e 0-5 leucócitos/campo; o resto do sedimento era normal.
O hemograma é normal; a bioquímica mostrou uma glicemia de 169 mg/dl e triglicerídeos 456 mg/dl; função hepática e renal são normais. PSA de 1,16 ng/ml.

DIRIGIDA A: Dr. Eva Andrade - Centro Hospitalar do Medio Ave - Avenida Dos Aliados, 56
E-MAIL: evandrade@poste.pt
"""

result = deid_pipeline.annotate(text)
```

Results:
```
|    | Sentence                       | Masked                     | Masked with Chars              | Masked with Fixed Chars   | Obfuscated                        |
|---:|:-------------------------------|:---------------------------|:-------------------------------|:--------------------------|:----------------------------------|
|  0 | RELAÇÃO HOSPITALAR             | RELAÇÃO HOSPITALAR         | RELAÇÃO HOSPITALAR             | RELAÇÃO HOSPITALAR        | RELAÇÃO HOSPITALAR                |
|    | NOME: Pedro Gonçalves          | NOME: <DOCTOR>             | NOME: [*************]          | NOME: ****                | NOME: Isabel Magalhães            |
|  1 | NHC: MVANSK92F09W408A          | NHC: <ID>                  | NHC: [**************]          | NHC: ****                 | NHC: 124 445 311                  |
|  2 | ENDEREÇO: Rua Burcardo 7       | ENDEREÇO: <STREET>         | ENDEREÇO: [************]       | ENDEREÇO: ****            | ENDEREÇO: Rua de Santa María, 100 |
|  3 | CÓDIGO POSTAL: 80139           | CÓDIGO POSTAL: <ZIP>       | CÓDIGO POSTAL: [***]           | CÓDIGO POSTAL: ****       | CÓDIGO POSTAL: 1000-306           |
|    | DATA DE NASCIMENTO: 03/03/1946 | DATA DE NASCIMENTO: <DATE> | DATA DE NASCIMENTO: [********] | DATA DE NASCIMENTO: ****  | DATA DE NASCIMENTO: 04/04/1946    |
|  4 | IDADE: 70 anos                 | IDADE: <AGE> anos          | IDADE: ** anos                 | IDADE: **** anos          | IDADE: 46 anos                    |
|  5 | SEXO: Homens                   | SEXO: <SEX>                | SEXO: [****]                   | SEXO: ****                | SEXO: Mulher                      |
|  6 | E-MAIL: pgon21@tim.pt          | E-MAIL: <EMAIL>            | E-MAIL: [***********]          | E-MAIL: ****              | E-MAIL: eric.shannon@geegle.com   |
|    | DATA DE ADMISSÃO: 12/12/2016   | DATA DE ADMISSÃO: <DATE>   | DATA DE ADMISSÃO: [********]   | DATA DE ADMISSÃO: ****    | DATA DE ADMISSÃO: 23/12/2016      |
|  7 | DOUTORA: Eva Andrade           | DOUTORA: <DOCTOR>          | DOUTORA: [*********]           | DOUTORA: ****             | DOUTORA: Isabel Magalhães         |
```

 See [Model Hub Page](https://nlp.johnsnowlabs.com/2022/04/14/clinical_deidentification_pt_3_0.html) for details.


Check Spark NLP Portuguese capabilities in [4.7.Clinical_Deidentification_in_Portuguese.ipynb notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/4.7.Clinical_Deidentification_in_Portuguese.ipynb) we have prepared for you.

#### New RxNorm Sentence Entity Resolver Model (`sbiobertresolve_rxnorm_action_treatment`)

We are releasing `sbiobertresolve_rxnorm_action_treatment` model that maps clinical entities and concepts (like drugs/ingredients) to RxNorm codes using `sbiobert_base_cased_mli` Sentence Bert Embeddings. This resolver model maps and extracts pharmaceutical actions (e.g analgesic, hypoglycemic) as well as treatments (e.g backache, diabetes) along with the RxNorm code resolved. Actions and treatments of the drugs are returned in `all_k_aux_labels` column.

 See [Model Card](https://nlp.johnsnowlabs.com/2022/04/25/sbiobertresolve_rxnorm_action_treatment_en_2_4.html) for details.

*Example* :

```python
documentAssembler = DocumentAssembler()\
      .setInputCol("text")\
      .setOutputCol("ner_chunk")

sbert_embedder = BertSentenceEmbeddings.pretrained('sbiobert_base_cased_mli', 'en','clinical/models')\
      .setInputCols(["ner_chunk"])\
      .setOutputCol("sentence_embeddings")

rxnorm_resolver = SentenceEntityResolverModel.pretrained("sbiobertresolve_rxnorm_action_treatment", "en", "clinical/models") \
      .setInputCols(["ner_chunk", "sentence_embeddings"]) \
      .setOutputCol("rxnorm_code")\
      .setDistanceFunction("EUCLIDEAN")

pipelineModel = PipelineModel(
    stages = [
        documentAssembler,
        sbert_embedder,
        rxnorm_resolver])

lp_model = LightPipeline(pipelineModel)

text = ["Zita 200 mg", "coumadin 5 mg", 'avandia 4 mg']

result= lp_model.annotate(text)

```
Results* :
```
|    | ner_chunk     |   rxnorm_code | action                                  | treatment                          |
|---:|:--------------|--------------:|:----------------------------------------|------------------------------------|
|  0 | Zita 200 mg   |        104080 | ['Analgesic', 'Antacid', 'Antipyretic'] | ['Backache', 'Pain', 'Sore Throat']|
|  1 | coumadin 5 mg |        855333 | ['Anticoagulant']                       | ['Cerebrovascular Accident']       |
|  2 | avandia 4 mg  |        261242 | ['Drugs Used In Diabets','Hypoglycemic']| ['Diabetes Mellitus', ...]         |                                                                                              |
```

#### New RCT Classification Models and Pretrained Pipelines

We are releasing new **Randomized Clinical Trial (RCT)** classification models and pretrained pipelines that can classify the sections within the abstracts of scientific articles regarding randomized clinical trials (RCT).

+ Classification Models:
	+ `rct_binary_classifier_use` ([Models Hub page](https://nlp.johnsnowlabs.com/2022/04/24/rct_binary_classifier_use_en_3_0.html))
	+ `rct_binary_classifier_biobert` ([Models Hub page](https://nlp.johnsnowlabs.com/2022/04/25/rct_binary_classifier_biobert_en_3_0.html))
	+ `bert_sequence_classifier_binary_rct_biobert` ([Models Hub page](https://nlp.johnsnowlabs.com/2022/04/25/bert_sequence_classifier_binary_rct_biobert_en_3_0.html))

+ Pretrained Pipelines:
	+ `rct_binary_classifier_use_pipeline` ([Models Hub page](https://nlp.johnsnowlabs.com/2022/04/25/rct_binary_classifier_use_pipeline_en_3_0.html))
	+ `rct_binary_classifier_biobert_pipeline` ([Models Hub page](https://nlp.johnsnowlabs.com/2022/04/25/rct_binary_classifier_biobert_pipeline_en_3_0.html))
	+ `bert_sequence_classifier_binary_rct_biobert_pipeline` ([Models Hub page](https://nlp.johnsnowlabs.com/2022/04/25/bert_sequence_classifier_binary_rct_biobert_pipeline_en_3_0.html))

 *Classification Model Example* :

```python
...
use = UniversalSentenceEncoder.pretrained()\
        .setInputCols("document")\
        .setOutputCol("sentence_embeddings")

classifier_dl = ClassifierDLModel.pretrained('rct_binary_classifier_use', 'en', 'clinical/models')\
        .setInputCols(["sentence_embeddings"])\
        .setOutputCol("class")

use_clf_pipeline = Pipeline(
    stages = [
        document_assembler,
        use,
        classifier_dl
    ])

sample_text = """Abstract:Based on the American Society of Anesthesiologists' Practice Guidelines for Sedation and Analgesia by Non-Anesthesiologists (ASA-SED), a sedation training course aimed at improving medical safety was developed by the Japanese Association for Medical Simulation in 2011. This study evaluated the effect of debriefing on participants' perceptions of the essential points of the ASA-SED. A total of 38 novice doctors participated in the sedation training course during the research period. Of these doctors, 18 participated in the debriefing group, and 20 participated in non-debriefing group. Scoring of participants' guideline perceptions was conducted using an evaluation sheet (nine items, 16 points) created based on the ASA-SED. The debriefing group showed a greater perception of the ASA-SED, as reflected in the significantly higher scores on the evaluation sheet (median, 16 points) than the control group (median, 13 points; p < 0.05). No significant differences were identified before or during sedation, but the difference after sedation was significant (p < 0.05). Debriefing after sedation training courses may contribute to better perception of the ASA-SED, and may lead to enhanced attitudes toward medical safety during sedation and analgesia. """

result = use_clf_pipeline.transform(spark.createDataFrame([[sample_text]]).toDF("text"))

```

*Results* :
```
>> class: True
```

*Pretrained Pipeline Example* :

```python
from sparknlp.pretrained import PretrainedPipeline

pipeline = PretrainedPipeline("rct_binary_classifier_use_pipeline", "en", "clinical/models")
```

```
text = """Abstract:Based on the American Society of Anesthesiologists' Practice Guidelines for Sedation and Analgesia by Non-Anesthesiologists (ASA-SED), a sedation training course aimed at improving medical safety was developed by the Japanese Association for Medical Simulation in 2011. This study evaluated the effect of debriefing on participants' perceptions of the essential points of the ASA-SED. A total of 38 novice doctors participated in the sedation training course during the research period. Of these doctors, 18 participated in the debriefing group, and 20 participated in non-debriefing group. Scoring of participants' guideline perceptions was conducted using an evaluation sheet (nine items, 16 points) created based on the ASA-SED. The debriefing group showed a greater perception of the ASA-SED, as reflected in the significantly higher scores on the evaluation sheet (median, 16 points) than the control group (median, 13 points; p < 0.05). No significant differences were identified before or during sedation, but the difference after sedation was significant (p < 0.05). Debriefing after sedation training courses may contribute to better perception of the ASA-SED, and may lead to enhanced attitudes toward medical safety during sedation and analgesia. """

result = pipeline.annotate(text)
```

*Results* :

```
>> class: True
```

#### New Features
##### Add `getClasses()` attribute to `MedicalBertForTokenClassifier` and `MedicalBertForSequenceClassification`
Now you can use `getClasses()` method for checking the entity labels of  `MedicalBertForTokenClassifier` and `MedicalBertForSequenceClassification` like `MedicalNerModel`.

  ```python
  tokenClassifier = MedicalBertForTokenClassifier.pretrained("bert_token_classifier_ner_ade", "en", "clinical/models")\
  	.setInputCols("token", "document")\
  	.setOutputCol("ner")\
  	.setCaseSensitive(True)\
  	.setMaxSentenceLength(512)

  tokenClassifier.getClasses()
  ```

  ```bash
  ['B-DRUG', 'I-ADE', 'I-DRUG', 'O', 'B-ADE']
  ```

##### Download the AnnotatorModels from the healthcare library using the Healthcare version instead of the open source version

Now we download the private models using the Healthcare version instead of the open source version (the pretrained models were used to be dependent on open source Spark NLP version before).

##### New functionality to download and extract clinical models from S3 via direct link.
Now, you can download clinical models from S3 via direct link directly by `downloadModelDirectly` method. See the [Models Hub Page](https://nlp.johnsnowlabs.com/models) to find out the download url of each model.

  ```python
  from sparknlp.pretrained import ResourceDownloader

  #The first argument is the path to the zip file and the second one is the folder.
  ResourceDownloader.downloadModelDirectly("clinical/models/assertion_dl_en_2.0.2_2.4_1556655581078.zip", "clinical/models")  
  ```

#### Core improvements:

##### Fix `MedicalNerModel` confidence scores when `setIncludeAllConfidenceScores` is `True`

A mismatch problem between the tag with the highest confidence score and the predicted tag in `MedicalNerModel` is resolved.

##### Graph_builder `relation_extraction` model file name extension problem with `auto` param

A naming problem which occurs while generating a graph for Relation Extraction via graph builder was resolved. Now, the TF graph is generated with the correct extension (`.pb`).

#### List of Recently Updated or Added Models

- ner_deid_generic_pt
- ner_deid_subentity_pt
- clinical_deidentification_pt
- sbiobertresolve_rxnorm_action_treatment
- rct_binary_classifier_use 
- rct_binary_classifier_biobert 
- bert_sequence_classifier_binary_rct_biobert 
- rct_binary_classifier_use_pipeline 
- rct_binary_classifier_biobert_pipeline 
- bert_sequence_classifier_binary_rct_biobert_pipeline 
- sbiobertresolve_ndc


## 3.5.0
We are glad to announce that Spark NLP Healthcare 3.5.0 has been released!

#### Highlights
+ **Zero-shot Relation Extraction** to extract relations between clinical entities with no training dataset
+ **Deidentification**:
  - New **French** **Deidentification** NER models and pipeline
  - New **Italian** **Deidentification** NER models and pipeline
  - Check our reference table for **French and Italian deidentification metrics**
  - Added **French support to the "fake" generation of data** (aka data obfuscation) in the Deidentification annotator
  - **Deidentification** **benchmark**: Spark NLP vs Cloud Providers (AWS, Azure, GCP)
+ **Graph generation**:
  - **ChunkMapperApproach** to augment NER chunks extracted by Spark NLP with a custom **graph-like dictionary of relationships**
+ **New Relation Extraction features**:
  - Configuration of **case sensitivity** in the name of the **relations** in **Relation Extraction Models**
+ **Models and Demos**:
  - We have reached **600 clinical models and pipelines**, what sums up to **5000+ overall models** in [Models Hub](https://nlp.johnsnowlabs.com/models)!
  - Check our new [live demos](https://nlp.johnsnowlabs.com/demos) including [multilanguage deidentification](https://demo.johnsnowlabs.com/healthcare/DEID_PHI_TEXT_MULTI/) to anonymize clinical notes in 5 different languages
+ Generate Dataframes to **train Assertion Status models** using **JSON Files** exported **from Annotation Lab** (ALAB)
+ Guide about how to scale **from PoC to Production** using Spark NLP for Healthcare in our new Medium Article, available [here](https://medium.com/spark-nlp/deploying-spark-nlp-for-healthcare-from-zero-to-hero-88949b0c866d)
+ **Core improvements**:
  - **Contextual Parser** (our Rule-based NER annotator) is now **much more performant**!
  - **Bug fixing and compatibility additions** affecting and improving some behaviours of _AssertionDL, BertSentenceChunkEmbeddings, AssertionFilterer and EntityRulerApproach_
+ **New notebooks: zero-shot relation extraction and Deidentification benchmark vs Cloud Providers**

#### Zero-shot Relation Extraction to extract relations between clinical entities with no training dataset
This release includes a zero-shot relation extraction model that leverages `BertForSequenceClassificaiton` to return, based on a predefined set of relation candidates (including no-relation / O), which one has the higher probability to be linking two entities.

The dataset will be a csv which contains the following columns: `sentence`, `chunk1`, `firstCharEnt1`, `lastCharEnt1`, `label1`, `chunk2`, `firstCharEnt2`, `lastCharEnt2`, `label2`, `rel`.

For example, let's take a look at this dataset (columns `chunk1`, `rel`, `chunk2` and `sentence`):

```
+----------------------------------------------+-------+-------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| chunk1                                       | rel   | chunk2                              | sentence                                                                                                                                                                       |
|----------------------------------------------+-------+-------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| light-headedness                             | PIP   | diaphoresis                         | She states this light-headedness is often associated with shortness of breath and diaphoresis occasionally with nausea .                                                       |
| respiratory rate                             | O     | saturation                          | VITAL SIGNS - Temp 98.8 , pulse 60 , BP 150/94 , respiratory rate 18 , and saturation 96% on room air .                                                                        |
| lotions                                      | TrNAP | incisions                           | No lotions , creams or powders to incisions .                                                                                                                                  |
| abdominal ultrasound                         | TeRP  | gallbladder sludge                  | Abdominal ultrasound on 2/23/00 - This study revealed gallbladder sludge but no cholelithiasis .                                                                               |
| ir placement of a drainage catheter          | TrAP  | his abdominopelvic fluid collection | At that time he was made NPO with IVF , placed on Ampicillin / Levofloxacin / Flagyl and underwent IR placement of a drainage catheter for his abdominopelvic fluid collection |
+----------------------------------------------+-------+-------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
```

The relation types (TeRP, TrAP, PIP, TrNAP, etc...) are described [here](https://www.i2b2.org/NLP/Relations/assets/Relation%20Annotation%20Guideline.pdf)

Let's take a look at the first sentence!

`She states this light-headedness is often associated with shortness of breath and diaphoresis occasionally with nausea`

As we see in the table, the sentences includes a `PIP` relationship (`Medical problem indicates medical problem`), meaning that in that sentence, chunk1 (`light-headedness`) *indicates* chunk2 (`diaphoresis`).

We set a list of candidates tags (`[PIP, TrAP, TrNAP, TrWP, O]`) and candidate sentences (`[light-headedness caused diaphoresis, light-headedness was administered for diaphoresis, light-headedness was not given for diaphoresis, light-headedness worsened diaphoresis]`), meaning that:

- `PIP` is expressed by `light-headedness caused diaphoresis`
- `TrAP` is expressed by `light-headedness was administered for diaphoresis`
- `TrNAP` is expressed by `light-headedness was not given for diaphoresis`
- `TrWP` is expressed by `light-headedness worsened diaphoresis`
- or something generic, like `O` is expressed by `light-headedness and diaphoresis`...

We will get that the biggest probability of is `PIP`, since it's phrase `light-headedness caused diaphoresis` is the most similar relationship expressing the meaning in the original sentence (`light-headnedness is often associated with ... and diaphoresis`)

The example code is the following:
```
...
re_ner_chunk_filter = sparknlp_jsl.annotator.RENerChunksFilter() \
    .setRelationPairs(["problem-test","problem-treatment"]) \
    .setMaxSyntacticDistance(4)\
    .setDocLevelRelations(False)\
    .setInputCols(["ner_chunks", "dependencies"]) \
    .setOutputCol("re_ner_chunks")

# The relations are defined by a map- keys are relation label, values are lists of predicated statements. The variables in curly brackets are NER entities, there could be more than one, e.g. "{{TREATMENT, DRUG}} improves {{PROBLEM}}"
re_model = sparknlp_jsl.annotator.ZeroShotRelationExtractionModel \
    .pretrained("re_zeroshot_biobert", "en", "clinical/models")\
    .setRelationalCategories({
        "CURE": ["{{TREATMENT}} cures {{PROBLEM}}."],
        "IMPROVE": ["{{TREATMENT}} improves {{PROBLEM}}.", "{{TREATMENT}} cures {{PROBLEM}}."],
        "REVEAL": ["{{TEST}} reveals {{PROBLEM}}."]})\
    .setMultiLabel(False)\
    .setInputCols(["re_ner_chunks", "sentences"]) \
    .setOutputCol("relations")

pipeline = sparknlp.base.Pipeline() \
    .setStages([documenter, tokenizer, sentencer, words_embedder, pos_tagger, ner_tagger, ner_converter,
                dependency_parser, re_ner_chunk_filter, re_model])

data = spark.createDataFrame(
    [["Paracetamol can alleviate headache or sickness. An MRI test can be used to find cancer."]]
).toDF("text")

model = pipeline.fit(data)
results = model.transform(data)

results\
    .selectExpr("explode(relations) as relation")\
    .show(truncate=False)    
```

Results:
```
+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
|relation                                                                                                                                                                                                                                                                                                                                                              |
+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
|{category, 534, 613, REVEAL, {entity1_begin -> 48, relation -> REVEAL, hypothesis -> An MRI test reveals cancer., confidence -> 0.9760039, nli_prediction -> entail, entity1 -> TEST, syntactic_distance -> 4, chunk2 -> cancer, entity2_end -> 85, entity1_end -> 58, entity2_begin -> 80, entity2 -> PROBLEM, chunk1 -> An MRI test, sentence -> 1}, []}            |
|{category, 267, 357, IMPROVE, {entity1_begin -> 0, relation -> IMPROVE, hypothesis -> Paracetamol improves sickness., confidence -> 0.98819494, nli_prediction -> entail, entity1 -> TREATMENT, syntactic_distance -> 3, chunk2 -> sickness, entity2_end -> 45, entity1_end -> 10, entity2_begin -> 38, entity2 -> PROBLEM, chunk1 -> Paracetamol, sentence -> 0}, []}|
|{category, 0, 90, IMPROVE, {entity1_begin -> 0, relation -> IMPROVE, hypothesis -> Paracetamol improves headache., confidence -> 0.9929625, nli_prediction -> entail, entity1 -> TREATMENT, syntactic_distance -> 2, chunk2 -> headache, entity2_end -> 33, entity1_end -> 10, entity2_begin -> 26, entity2 -> PROBLEM, chunk1 -> Paracetamol, sentence -> 0}, []}    |
+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
```

Take a look at the example notebook [here](https://colab.research.google.com/github/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/10.3.ZeroShot_Clinical_Relation_Extraction.ipynb).

Stay tuned for the **few-shot** Annotator to be release soon!


#### New French Deidentification NER models and pipeline
We trained two new NER models to find PHI data (protected health information) that may need to be deidentified in **French**. `ner_deid_generic` and `ner_deid_subentity` models are trained with in-house annotations.
+ `ner_deid_generic` : Detects 7 PHI entities in French (`DATE`, `NAME`, `LOCATION`, `PROFESSION`, `CONTACT`, `AGE`, `ID`).
+ `ner_deid_subentity` : Detects 15 PHI sub-entities in French (`PATIENT`, `HOSPITAL`, `DATE`, `ORGANIZATION`, `E-MAIL`, `USERNAME`, `ZIP`, `MEDICALRECORD`, `PROFESSION`, `PHONE`, `DOCTOR`, `AGE`, `STREET`, `CITY`, `COUNTRY`).
*Example* :
```bash
...
embeddings = WordEmbeddingsModel.pretrained("w2v_cc_300d", "fr")\
    .setInputCols(["sentence", "token"])\
	   .setOutputCol("embeddings")
deid_ner = MedicalNerModel.pretrained("ner_deid_generic", "fr", "clinical/models")\
    .setInputCols(["sentence", "token", "embeddings"])\
    .setOutputCol("ner")
deid_sub_entity_ner = MedicalNerModel.pretrained("ner_deid_subentity", "fr", "clinical/models")\
    .setInputCols(["sentence", "token", "embeddings"])\
    .setOutputCol("ner_sub_entity")
...
text = """J'ai vu en consultation Michel Martinez (49 ans) adressé au Centre Hospitalier De Plaisir pour un diabète mal contrôlé avec des symptômes datant de Mars 2015."""
result = model.transform(spark.createDataFrame([[text]], ["text"]))
```
*Results* :

```bash
| chunk              		| ner_deid_generic_chunk | ner_deid_subentity_chunk |
|-------------------------------|------------------------|--------------------------|
| Michel Martinez    		| NAME                   | PATIENT                  |
| 49 ans             		| AGE                    | AGE                      |
| Centre Hospitalier De Plaisir | LOCATION        	 | HOSPITAL                 |
| Mars 2015          		| DATE                   | DATE                     |
```

We also developed a clinical deidentification pretrained pipeline that can be used to deidentify PHI information from **French** medical texts. The PHI information will be masked and obfuscated in the resulting text. The pipeline can mask and obfuscate the following entities: `DATE`, `AGE`, `SEX`, `PROFESSION`, `ORGANIZATION`, `PHONE`, `E-MAIL`, `ZIP`, `STREET`, `CITY`, `COUNTRY`, `PATIENT`, `DOCTOR`, `HOSPITAL`, `MEDICALRECORD`, `SSN`, `IDNUM`, `ACCOUNT`, `PLATE`, `USERNAME`, `URL`, and `IPADDR`.

```bash
from sparknlp.pretrained import PretrainedPipeline
deid_pipeline = PretrainedPipeline("clinical_deidentification", "fr", "clinical/models")
text = """PRENOM : Jean NOM : Dubois NUMÉRO DE SÉCURITÉ SOCIALE : 1780160471058 ADRESSE : 18 Avenue Matabiau VILLE : Grenoble CODE POSTAL : 38000"""
result = deid_pipeline.annotate(text)
```
*Results*:

```bash
Masked with entity labels
------------------------------
PRENOM : <PATIENT> NOM : <PATIENT> NUMÉRO DE SÉCURITÉ SOCIALE : <SSN>  ADRESSE : <STREET> VILLE : <CITY> CODE POSTAL : <ZIP>
Masked with chars
------------------------------
PRENOM : [**] NOM : [****] NUMÉRO DE SÉCURITÉ SOCIALE : [***********]  ADRESSE : [****************] VILLE : [******] CODE POSTAL : [***]
Masked with fixed length chars
------------------------------
PRENOM : **** NOM : **** NUMÉRO DE SÉCURITÉ SOCIALE : ****  ADRESSE : **** VILLE : **** CODE POSTAL : ****
Obfuscated
------------------------------
PRENOM : Mme Olivier NOM : Mme Traore NUMÉRO DE SÉCURITÉ SOCIALE : 164033818514436  ADRESSE : 731, boulevard de Legrand VILLE : Sainte Antoine CODE POSTAL : 37443
```


#### New Italian Deidentification NER models and pipeline

We trained two new NER models to find PHI data (protected health information) that may need to be deidentified in **Italian**. `ner_deid_generic` and `ner_deid_subentity` models are trained with in-house annotations.
+ `ner_deid_generic` : Detects 8 PHI entities in Italian (`DATE`, `NAME`, `LOCATION`, `PROFESSION`, `CONTACT`, `AGE`, `ID`, `SEX`).
+ `ner_deid_subentity` : Detects 19 PHI sub-entities in Italian (`DATE`, `AGE`, `SEX`, `PROFESSION`, `ORGANIZATION`, `PHONE`, `EMAIL`, `ZIP`, `STREET`, `CITY`, `COUNTRY`, `PATIENT`, `DOCTOR`, `HOSPITAL`, `MEDICALRECORD`, `SSN`, `IDNUM`, `USERNAME`, `URL`).
*Example* :
```bash
...
embeddings = WordEmbeddingsModel.pretrained("w2v_cc_300d", "it")\
    .setInputCols(["sentence", "token"])\
	   .setOutputCol("embeddings")
deid_ner = MedicalNerModel.pretrained("ner_deid_generic", "it", "clinical/models")\
    .setInputCols(["sentence", "token", "embeddings"])\
    .setOutputCol("ner")
deid_sub_entity_ner = MedicalNerModel.pretrained("ner_deid_subentity", "it", "clinical/models")\
    .setInputCols(["sentence", "token", "embeddings"])\
    .setOutputCol("ner_sub_entity")
...
text = """Ho visto Gastone Montanariello (49 anni) riferito all' Ospedale San Camillo per diabete mal controllato con sintomi risalenti a marzo 2015."""
result = model.transform(spark.createDataFrame([[text]], ["text"]))
```
*Results* :

```bash
| chunk                | ner_deid_generic_chunk | ner_deid_subentity_chunk |
|----------------------|------------------------|--------------------------|
| Gastone Montanariello| NAME                   | PATIENT                  |
| 49                   | AGE                    | AGE                      |
| Ospedale San Camillo | LOCATION               | HOSPITAL                 |
| marzo 2015           | DATE                   | DATE                     |
```

We also developed a clinical deidentification pretrained pipeline that can be used to deidentify PHI information from **Italian** medical texts. The PHI information will be masked and obfuscated in the resulting text. The pipeline can mask and obfuscate the following entities: `DATE`, `AGE`, `SEX`, `PROFESSION`, `ORGANIZATION`, `PHONE`, `E-MAIL`, `ZIP`, `STREET`, `CITY`, `COUNTRY`, `PATIENT`, `DOCTOR`, `HOSPITAL`, `MEDICALRECORD`, `SSN`, `IDNUM`, `ACCOUNT`, `PLATE`, `USERNAME`, `URL`, and `IPADDR`.

```bash
from sparknlp.pretrained import PretrainedPipeline
deid_pipeline = PretrainedPipeline("clinical_deidentification", "it", "clinical/models")
sample_text = """NOME: Stefano Montanariello CODICE FISCALE: YXYGXN51C61Y662I INDIRIZZO: Viale Burcardo 7 CODICE POSTALE: 80139"""
result = deid_pipeline.annotate(sample_text)
```
*Results*:

```bash
Masked with entity labels
------------------------------
NOME: <PATIENT> CODICE FISCALE: <SSN> INDIRIZZO: <STREET> CODICE POSTALE: <ZIP>

Masked with chars
------------------------------
NOME: [*******************] CODICE FISCALE: [**************] INDIRIZZO: [**************] CODICE POSTALE: [***]

Masked with fixed length chars
------------------------------
NOME: **** CODICE FISCALE: **** INDIRIZZO: **** CODICE POSTALE: ****

Obfuscated
------------------------------
NOME: Stefania Gregori CODICE FISCALE: UIWSUS86M04J604B INDIRIZZO: Viale Orlando 808 CODICE POSTALE: 53581
```

#### Check our reference table for **French and Italian deidentification metrics**
Please find this reference table with metrics comparing F1 score for the available entities in French and Italian clinical pipelines:
```
|Entity Label |Italian|French|
|-------------|-------|------|
|PATIENT      |0.9069 |0.9382|
|DOCTOR       |0.9171 |0.9912|
|HOSPITAL     |0.8968 |0.9375|
|DATE         |0.9835 |0.9849|
|AGE          |0.9832 |0.8575|
|PROFESSION   |0.8864 |0.8147|
|ORGANIZATION |0.7385 |0.7697|
|STREET       |0.9754 |0.8986|
|CITY         |0.9678 |0.8643|
|COUNTRY      |0.9262 |0.8983|
|PHONE        |0.9815 |0.9785|
|USERNAME     |0.9091 |0.9239|
|ZIP          |0.9867 |1.0   |
|E-MAIL       |1      |1.0   |
|MEDICALRECORD|0.8085 |0.939 |
|SSN          |0.9286 |N/A   |
|URL          |1      |N/A   |
|SEX          |0.9697 |N/A   |
|IDNUM        |0.9576 |N/A   |
```



#### Added French support in Deidentification Annotator for data obfuscation
Our `Deidentificator` annotator is now able to obfuscate entities (coming from a deid NER model) with fake data in French language. Example:

Example code:
```
...
embeddings = WordEmbeddingsModel.pretrained("w2v_cc_300d", "fr").setInputCols(["sentence", "token"]).setOutputCol("word_embeddings")

clinical_ner = MedicalNerModel.pretrained("ner_deid_subentity", "fr", "clinical/models").setInputCols(["sentence","token", "word_embeddings"]).setOutputCol("ner")

ner_converter = NerConverter().setInputCols(["sentence", "token", "ner"]).setOutputCol("ner_chunk")

de_identification = DeIdentification() \
    .setInputCols(["ner_chunk", "token", "sentence"]) \
    .setOutputCol("dei") \
    .setMode("obfuscate") \
    .setObfuscateDate(True) \
    .setRefSep("#") \
    .setDateTag("DATE") \
    .setLanguage("fr") \
    .setObfuscateRefSource('faker')

pipeline = Pipeline() \
    .setStages([
    documentAssembler,
    sentenceDetector,
    tokenizer,
    embeddings,
    clinical_ner,
    ner_converter,
    de_identification
])
sentences = [
["""J'ai vu en consultation Michel Martinez (49 ans) adressé au Centre Hospitalier De Plaisir pour un diabète mal contrôlé avec des symptômes datant"""]
]

my_input_df = spark.createDataFrame(sentences).toDF("text")
output = pipeline.fit(my_input_df).transform(my_input_df)
...
```

Entities detected:
```
+------------+----------+
|token       |entity    |
+------------+----------+
|J'ai        |O         |
|vu          |O         |
|en          |O         |
|consultation|O         |
|Michel      |B-PATIENT |
|Martinez    |I-PATIENT |
|(           |O         |
|49          |B-AGE     |
|ans         |O         |
|)           |O         |
|adressé     |O         |
|au          |O         |
|Centre      |B-HOSPITAL|
|Hospitalier |I-HOSPITAL|
|De          |I-HOSPITAL|
|Plaisir     |I-HOSPITAL|
|pour        |O         |
|un          |O         |
|diabète     |O         |
|mal         |O         |
+------------+----------+
```

Obfuscated sentence:
```
+--------------------------------------------------------------------------------------------------------------------------------------------------------+
|result                                                                                                                                                  |
+--------------------------------------------------------------------------------------------------------------------------------------------------------+
|[J'ai vu en consultation Sacrispeyre Ligniez (86 ans) adressé au Centre Hospitalier Pierre Futin pour un diabète mal contrôlé avec des symptômes datant]|
+--------------------------------------------------------------------------------------------------------------------------------------------------------+
```


#### Deidentification benchmark: Spark NLP vs Cloud Providers (AWS, Azure, GCP)
We have published a new notebook with a benchmark and the reproduceable code, comparing Spark NLP for Healthcare Deidentification capabilities of one of our English pipelines (`clinical_deidentification_glove_augmented`) versus:
- AWS Comprehend Medical
- Azure Cognitive Services
- GCP Data Loss Prevention

The notebook is available [here](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/4.3.Clinical_Deidentification_SparkNLP_vs_Cloud_Providers_Comparison.ipynb), and the results are the following:

```
        SPARK NLP   AWS    AZURE      GCP
AGE          1      0.96    0.93      0.9
DATE         1      0.99    0.9       0.96
DOCTOR      0.98    0.96    0.7       0.6
HOSPITAL    0.92    0.89    0.72      0.72
LOCATION    0.9     0.81    0.87      0.73
PATIENT     0.96    0.95    0.78      0.48
PHONE        1       1      0.8       0.97
ID          0.93    0.93     -          -
```

#### ChunkMapperApproach: mapping extracted entities to an ontology (Json dictionary) with relations
We have released a new annotator, called **ChunkMapperApproach**(), that receives a **ner_chunk** and a Json with a mapping of NER entities and relations, and returns the **ner_chunk** augmented with the relations from the Json ontology.


Example of a small ontology with relations:


Giving the map with entities and relationships stored in mapper.json, we will use an NER to detect entities in a text and, in case any of them is found, the **ChunkMapper** will augment the output with the relationships from this dictionary:

```
{"mappings": [{
             "key": "metformin",
             "relations": [{
                   "key": "action",
                   "values" : ["hypoglycemic", "Drugs Used In Diabets"]
                   },{
                   "key": "treatment",
                   "values" : ["diabetes", "t2dm"]
                   }]
           }]
```

```
text = ["""The patient was prescribed 1 unit of Advil for 5 days after meals. The patient was also
given 1 unit of Metformin daily.
He was seen by the endocrinology service and she was discharged on 40 units of insulin glargine at night ,
12 units of insulin lispro with meals , and metformin 1000 mg two times a day."""]
...
nerconverter = NerConverterInternal()\
  .setInputCols("sentence", "token", "ner")\
  .setOutputCol("ner_chunk")

chunkerMapper = ChunkMapperApproach() \
  .setInputCols("ner_chunk")\
  .setOutputCol("relations")\
  .setDictionary("mapper.json")\
  .setRel("action")

pipeline = Pipeline().setStages([document_assembler,sentence_detector,tokenizer, ner, nerconverter, chunkerMapper])

res = pipeline.fit(test_data).transform(test_data)

res.select(F.explode('ner_chunk.result').alias("chunks")).show(truncate=False)
```

Entities:
```
+----------------+
|chunks          |
+----------------+
|Metformin       |
|insulin glargine|
|insulin lispro  |
|metformin       |
|mg              |
|times           |
+----------------+
```

Checking the relations:
```
...
pd_df = res.select(F.explode('relations').alias('res')).select('res.result', 'res.metadata').toPandas()
...
```

Results:
```
Entity:					metformin
Main relation:				hypoglycemic
Other relations (included in metadata):	Drugs Used In Diabets
```


#### Configuration of case sensitivity in the name of the relations in Relation Extraction Models
We have added a new parameter, called 'relationPairsCaseSensitive', which affects the way `setRelationPairs` works. If `relationPairsCaseSensitive` is True, then the pairs of entities in the dataset should match the pairs in setRelationPairs in their specific case (case sensitive). By default it's set to False, meaning that the match of those relation names is case insensitive.

Before 3.5.0, `.setRelationPairs(["dosage-drug"])` would not return relations if it was trained with a relation called `DOSAGE-DRUG` (different casing). Now, setting `.setRelationPairs(["dosage-drug"])`and `relationPairsCaseSensitive(False)` or just leaving it by default, it will return any `dosage-drug` or `DOSAGE-DRUG` relationship.

Example of usage in Python:
```
...
reModel = RelationExtractionModel()\
    .pretrained("posology_re")\
    .setInputCols(["embeddings", "pos_tags", "ner_chunks", "dependencies"])\
    .setMaxSyntacticDistance(4)\
    .setRelationPairs(["dosage-drug"]) \
    .setRelationPairsCaseSensitive(False) \
    .setOutputCol("relations_case_insensitive")
...
```

This will return relations named dosage-drug, DOSAGE-DRUG, etc.


#### We have reached the milestone of 600 clinical models (and 5000+ models overall) ! 🥳
This release added to Spark NLP Models Hub 100+ pretrained clinical pipelines, available to use as one-liners, including some of the most used NER models, namely:

+ `ner_deid_generic_pipeline_de`: German deidentification pipeline with aggregated (generic) labels
+ `ner_deid_subentity_pipeline_de`: German deidentification pipeline with specific (subentity) labels
+ `ner_clinical_biobert_pipeline_en`: A pretrained pipeline based on `ner_clinical_biobert` to carry out NER on BioBERT embeddings
+ `ner_abbreviation_clinical_pipeline_en`: A pretrained pipeline based on `ner_abbreviation_clinical` that detects medical acronyms and abbreviations
+ `ner_ade_biobert_pipeline_en`: A pretrained pipeline based on `ner_ade_biobert` to carry out Adverse Drug Events NER recognition using BioBERT embeddings
+ `ner_ade_clinical_pipeline_en`: Similar to the previous one, but using `clinical_embeddings`
+ `ner_radiology_pipeline_en`: A pretrained pipeline to detect Radiology entities (coming from `ner_radiology_wip` model)
+ `ner_events_clinical_pipeline_en`: A pretrained pipeline to extract Clinical Events related entities (leveraging `ner_events_clinical`)
+ `ner_anatomy_biobert_pipeline_en`: A pretrained pipeline to extract Anamoty entities (from `ner_anamoty_biobert`)
+ ...100 more

Here is how you can use any of the pipelines with one line of code:

```
from sparknlp.pretrained import PretrainedPipeline

pipeline = PretrainedPipeline("explain_clinical_doc_medication", "en", "clinical/models")

result = pipeline.fullAnnotate("""The patient is a 30-year-old female with a long history of insulin dependent diabetes, type 2. She received a course of Bactrim for 14 days for UTI.  She was prescribed 5000 units of Fragmin  subcutaneously daily, and along with Lantus 40 units subcutaneously at bedtime.""")[0]
```

Results:
```
+----+----------------+------------+
|    | chunks         | entities   |
|---:|:---------------|:-----------|
|  0 | insulin        | DRUG       |
|  1 | Bactrim        | DRUG       |
|  2 | for 14 days    | DURATION   |
|  3 | 5000 units     | DOSAGE     |
|  4 | Fragmin        | DRUG       |
|  5 | subcutaneously | ROUTE      |
|  6 | daily          | FREQUENCY  |
|  7 | Lantus         | DRUG       |
|  8 | 40 units       | DOSAGE     |
|  9 | subcutaneously | ROUTE      |
| 10 | at bedtime     | FREQUENCY  |
+----+----------------+------------+
+----+----------+------------+-------------+
|    | chunks   | entities   | assertion   |
|---:|:---------|:-----------|:------------|
|  0 | insulin  | DRUG       | Present     |
|  1 | Bactrim  | DRUG       | Past        |
|  2 | Fragmin  | DRUG       | Planned     |
|  3 | Lantus   | DRUG       | Planned     |
+----+----------+------------+-------------+
+----------------+-----------+------------+-----------+----------------+
| relation       | entity1   | chunk1     | entity2   | chunk2         |
|:---------------|:----------|:-----------|:----------|:---------------|
| DRUG-DURATION  | DRUG      | Bactrim    | DURATION  | for 14 days    |
| DOSAGE-DRUG    | DOSAGE    | 5000 units | DRUG      | Fragmin        |
| DRUG-ROUTE     | DRUG      | Fragmin    | ROUTE     | subcutaneously |
| DRUG-FREQUENCY | DRUG      | Fragmin    | FREQUENCY | daily          |
| DRUG-DOSAGE    | DRUG      | Lantus     | DOSAGE    | 40 units       |
| DRUG-ROUTE     | DRUG      | Lantus     | ROUTE     | subcutaneously |
| DRUG-FREQUENCY | DRUG      | Lantus     | FREQUENCY | at bedtime     |
+----------------+-----------+------------+-----------+----------------+
```

We have updated our [11.Pretrained_Clinical_Pipelines.ipynb](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/11.Pretrained_Clinical_Pipelines.ipynb) notebook to properly show this addition. Don't forget to check it out!

All of our scalable, production-ready Spark NLP Clinical Models and Pipelines can be found in our Models Hub

Finally, we have added two new **entityMapper** models: **drug_ontology** and **section_mapper**

For all Spark NLP for healthcare models, please check our [Models Hub webpage](https://nlp.johnsnowlabs.com/models?edition=Spark+NLP+for+Healthcare)


#### Have you checked our demo page?
New several demos were created, available at https://nlp.johnsnowlabs.com/demos

In this release we feature the **Multilingual deidentification**, showcasing how to deidentify clinical texts in English, Spanish, German, French and Italian. This demo is available [here](https://demo.johnsnowlabs.com/healthcare/DEID_PHI_TEXT_MULTI)

**For the rest of the demos, please visit [Models Hub Demos Page](https://nlp.johnsnowlabs.com/demos)**

#### Generate Dataframes to train Assertion Status Models using JSON files exported from Annotation Lab (ALAB)
Now we can generate a dataframe that can be used to train an `AssertionDLModel` by using the output of `AnnotationToolJsonReader.generatePlainAssertionTrainSet()`. The dataframe contains all the columns that you need for training.

*Example* :

```python
filename = "../json_import.json"
reader = AnnotationToolJsonReader(assertion_labels = ['AsPresent', 'AsAbsent', 'AsConditional', 'AsHypothetical', 'AsFamily', 'AsPossible', 'AsElse'])
df =  reader.readDataset(spark, filename)
reader.generatePlainAssertionTrainSet(df).show(truncate=False)
```

*Results* :

```
+-------+--------------------------------------------+-----+---+-----------+---------+
|task_id|sentence                                    |begin|end|ner        |assertion|
+-------+--------------------------------------------+-----+---+-----------+---------+
|1      |Patient has a headache for the last 2 weeks |2    |3  |a headache |AsPresent|
+-------+--------------------------------------------+-----+---+-----------+---------+
```

#### Understand how to scale from a PoC to Production using Spark NLP for Healthcare in our new Medium Article, available here

We receive many questions about how Spark work distribution is carried out, what specially becomes important before making the leap from a PoC to a big scalable, production-ready cluster.

This article helps you understand:
- How many different ways to create a cluster are available, as well as their advantages and disadvantages;
- How to scale all of them;
- How to take advantage of autoscalability and autotermination policy in Cloud Providers;
- Which are the steps to take depending on your infrastructure, to make the leap to production;

If you need further assistance, please reach our Support team at [support@johnsnowlabs.com](mailto:support@johnsnowlabs.com)


#### Contextual Parser (our Rule-based NER annotator) is now much more performant!
Contextual Parser has been improved in terms of performance. These are the metrics comparing 3.4.2 and 3.5.0

```
4 cores and 30 GB RAM
=====================
	10 MB	20 MB	30MB	50MB		
3.4.2	349	786	982	1633		
3.5.0   142	243	352	556		

8 cores and 60 GB RAM
=====================
	10 MB	20 MB	30MB	50MB
3.4.2	197	373	554	876
3.5.0   79	136	197	294
```

#### We have reached the milestone of 600 clinical demos!
During this release, we included:
- More than 100+ recently created clinical models and pipelines, including NER, NER+RE, NER+Assertion+RE, etc.
- Added two new `entityMapper` models: `drug_action_treatment_mapper` and `normalized_section_header_mapper`

**For all Spark NLP for healthcare models, please check : [Models Hub Page](https://nlp.johnsnowlabs.com/models?edition=Spark+NLP+for+Healthcare)**



#### Bug fixing and compatibility additions
This is the list of fixed issues and bugs, as well as one compatibility addition between **EntityRuler** and **AssertionFiltered**:

+ **Error in AssertionDLApproach and AssertionLogRegApproach**: an error was being triggered wthen the dataset contained long (64bits) instead of 32 bits integers for the start / end columns. Now this bug is fixed.
+ **Error in BertSentenceChunkEmbeddings**: loading a model after downloading it with pretrained() was triggering an error. Now you can load any model after downloading it with `pretrained()`.
+ Adding **setIncludeConfidence** to AssertionDL Python version, where it was missing. Now, it's included in both Python and Scala, as described [here](https://nlp.johnsnowlabs.com/licensed/api/com/johnsnowlabs/nlp/annotators/assertion/dl/AssertionDLModel.html#setIncludeConfidence(value:Boolean):AssertionDLModel.this.type)
+ **Making EntityRuler and AssertionFiltered compatible**: AssertionFilterer annotator that is being used to filter the entities based on entity labels now can be used by EntityRulerApproach, a rule based entity extractor:

```
Path("test_file.jsonl").write_text(json.dumps({"id":"cough","label":"COUGH","patterns":["cough","coughing"]}))
...
entityRuler = EntityRulerApproach()\
    .setInputCols(["sentence", "token"])\
    .setOutputCol("ner_chunk")\
    .setPatternsResource("test_file.jsonl", ReadAs.TEXT, {"format": "jsonl"})

clinical_assertion = AssertionDLModel.pretrained("assertion_dl", "en", "clinical/models") \
    .setInputCols(["sentence", "ner_chunk", "embeddings"]) \
    .setOutputCol("assertion")

assertion_filterer = AssertionFilterer()\
    .setInputCols("sentence","ner_chunk","assertion")\
    .setOutputCol("assertion_filtered")\
    .setWhiteList(["present"])\

...

empty_data = spark.createDataFrame([[""]]).toDF("text")
ruler_model = rulerPipeline.fit(empty_data)

text = "I have a cough but no fatigue or chills."

ruler_light_model = LightPipeline(ruler_model).fullAnnotate(text)[0]['assertion_filtered']
```

Result:
```
Annotation(chunk, 9, 13, cough, {'entity': 'COUGH', 'id': 'cough', 'sentence': '0'})]
```


#### **New notebooks: zero-shot relation extraction and Deidentification benchmark (Spark NLP and Cloud Providers)**
Check these recently notebooks created by our Healthcare team and available in our [Spark NLP Workshop git repo](https://github.com/JohnSnowLabs/spark-nlp-workshop/), where you can find many more.
- Zero-shot Relation Extraction, available [here](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/10.3.ZeroShot_Clinical_Relation_Extraction.ipynb).
- Deidentification benchmark (SparkNLP and Cloud Providers), available [here](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/4.3.Clinical_Deidentification_SparkNLP_vs_Cloud_Providers_Comparison.ipynb)


## 3.4.2
We are glad to announce that Spark NLP Healthcare 3.4.2 has been released!

#### Highlights

 + New RCT Classifier, NER models and pipeline (Deidentification)
 + Setting the scope window (target area) dynamically in Assertion Status detection models
 + Reading JSON files (exported from ALAB) from HDFS with `AnnotationJsonReader`
 + Allow users to write Tensorflow graphs to HDFS
 + Serving Spark NLP on APIs
 + Updated documentation on installing Spark NLP for Healthcare in AWS EMR (Jupyter, Livy, Yarn, Hadoop)
 + New series of notebooks to reproduce the academic papers published by our colleagues
 + PySpark tutorial notebooks to let non-Spark users get started with Apache Spark ecosystem in Python
 + New & updated notebooks
 + List of recently updated or added models

#### New RCT Classifier, NER Models and Pipeline (Deidentification)

We are releasing a new `bert_sequence_classifier_rct_biobert` model, four new Spanish deidentification NER models (`ner_deid_generic_augmented`, `ner_deid_subentity_augmented`, `ner_deid_generic_roberta_augmented`, `ner_deid_subentity_roberta_augmented`) and a pipeline (`clinical_deidentification_augmented`).

 + `bert_sequence_classifier_rct_biobert`: This model can classify the sections within abstract of scientific articles regarding randomized clinical trials (RCT) (`BACKGROUND`, `CONCLUSIONS`, `METHODS`, `OBJECTIVE`, `RESULTS`).

*Example* :

```python
...
sequenceClassifier_model = MedicalBertForSequenceClassification.pretrained("bert_sequence_classifier_rct_biobert", "en", "clinical/models")\
  .setInputCols(["document",'token'])\
  .setOutputCol("class")
...

sample_text = "Previous attempts to prevent all the unwanted postoperative responses to major surgery with an epidural hydrophilic opioid , morphine , have not succeeded . The authors ' hypothesis was that the lipophilic opioid fentanyl , infused epidurally close to the spinal-cord opioid receptors corresponding to the dermatome of the surgical incision , gives equal pain relief but attenuates postoperative hormonal and metabolic responses more effectively than does systemic fentanyl ."

result = sequence_clf_model.transform(spark.createDataFrame([[sample_text]]).toDF("text"))

>> class: 'BACKGROUND'
```


+ `ner_deid_generic_augmented`, `ner_deid_subentity_augmented`, `ner_deid_generic_roberta_augmented`, `ner_deid_subentity_roberta_augmented` models and `clinical_deidentification_augmented` pipeline : You can use either `sciwi-embeddings` (300 dimensions) or the Roberta Clinical Embeddings (infix `_roberta_`) with these NER models. These models and pipeline are different to their non-augmented versions in the following:

  - They are trained with more data, now including an in-house annotated deidentification dataset;
  - New `SEX` tag is available for all of them. This tag is now included in the NER and has been improved with more rules in the ContextualParsers of the pipeline, resulting in having a bigger recall to detect the sex of the patient.
  - New `STREET`, `CITY` and `COUNTRY` entities are added to subentity versions.

For more details and examples, please check [Clinical Deidentification in Spanish notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/4.2.Clinical_Deidentification_in_Spanish.ipynb).

*Example* :

```python
...
embeddings = WordEmbeddingsModel.pretrained("embeddings_sciwiki_300d","es","clinical/models")\
    .setInputCols(["sentence", "token"])\
    .setOutputCol("embeddings")

deid_ner = MedicalNerModel.pretrained("ner_deid_generic_augmented", "es", "clinical/models")\
    .setInputCols(["sentence", "token", "embeddings"])\
    .setOutputCol("ner")

deid_sub_entity_ner = MedicalNerModel.pretrained("ner_deid_subentity_augmented", "es", "clinical/models")\
    .setInputCols(["sentence", "token", "embeddings"])\
    .setOutputCol("ner_sub_entity")
...
```

*Results* :
```bash
chunk                    entity_subentity    entity_generic
-----------------------  ------------------  ----------------
Antonio Miguel Martínez  PATIENT             NAME
un varón                 SEX                 SEX
35                       AGE                 AGE
auxiliar de enfermería   PROFESSION          PROFESSION
Cadiz                    CITY                LOCATION
España                   COUNTRY             LOCATION
Clinica San Carlos       HOSPITAL            LOCATION
```

#### Setting the Scope Window (Target Area) Dynamically in Assertion Status Detection Models

This parameter allows you to train the Assertion Status Models to focus on specific context windows when resolving the status of a NER chunk. The window is in format `[X,Y]` being `X` the number of tokens to consider on the left of the chunk, and `Y` the max number of tokens to consider on the right. Let's take a look at what different windows mean:

- By default, the window is `[-1,-1]` which means that the Assertion Status will look at all of the tokens in the sentence/document (up to a maximum of tokens set in `setMaxSentLen()`).
- `[0,0]` means "don't pay attention to any token except the ner_chunk", what basically is not considering any context for the Assertion resolution.
- `[9,15]` is what empirically seems to be the best baseline, meaning that we look up to 9 tokens on the left and 15 on the right of the ner chunk to understand the context and resolve the status.

Check this [scope window tuning assertion status detection notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/2.1.Scope_window_tuning_assertion_status_detection.ipynb) that illustrates the effect of the different windows and how to properly **fine-tune** your AssertionDLModels to get the best of them.

*Example* :

```python
assertion_status = AssertionDLApproach() \
          .setGraphFolder("assertion_dl/") \
          .setInputCols("sentence", "chunk", "embeddings") \
          .setOutputCol("assertion") \
          ...
          ...
          .setScopeWindow([9, 15])     # NEW! Scope Window!
```

#### Reading JSON Files (Exported from ALAB) From HDFS with `AnnotationJsonReader`

Now we can read the dataframe from a HDFS that we read the files from in our cluster.

*Example* :

```python
filename = "hdfs:///user/livy/import.json"
reader = AnnotationToolJsonReader(assertion_labels = ['AsPresent', 'AsAbsent', 'AsConditional', 'AsHypothetical', 'Family', 'AsPossible', 'AsElse'])
df = reader.readDataset(spark, filename)
```

#### Allow Users Write Tensorflow Graphs to HDFS

Now we can save custom Tensorflow graphs to the HDFS that mainly being used in a cluster environment.

```python
tf_graph.build("ner_dl", build_params={"embeddings_dim": 200, "nchars": 128, "ntags": 12, "is_medical": 1}, model_location="hdfs:///user/livy", model_filename="auto")
```

#### Serving Spark NLP on APIs

Two new notebooks and a series of blog posts / Medium articles have been created to guide Spark NLP users to serve Spark NLP on a RestAPI.

* The notebooks can be found [here](https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/tutorials/RestAPI).
* The articles can be found in the Technical Documentation of Spark NLP, available [here](https://nlp.johnsnowlabs.com/docs/en/quickstart) and also in Medium:
	* [Serving Spark NLP via API (1/3): Microsoft’s Synapse ML](https://medium.com/@jjmcarrascosa/serving-spark-nlp-via-api-1-3-microsoft-synapse-ml-2c77a3f61f9d)
	* [Serving Spark NLP via API (2/3): FastAPI and LightPipelines](https://medium.com/@jjmcarrascosa/serving-spark-nlp-via-api-2-3-fastapi-and-lightpipelines-218d1980c9fc)
	* [Serving Spark NLP via API (3/3): Databricks Jobs and MLFlow Serve APIs](https://medium.com/@jjmcarrascosa/serving-spark-nlp-via-api-3-3-databricks-and-mlflow-serve-apis-4ef113e7fac4)

The difference between both approaches are the following:
+ `SynapseML` is a Microsoft Azure Open Source library used to carry out ML at scale. In this case, we use the Spark Serving feature, that leverages Spark Streaming and adds a web server with a Load Balancer, allowing concurrent processing of Spark NLP calls. Best approach if you look for scalability with Load Balancing.
+ `FastAPI` + `LightPipelines`: A solution to run Spark NLP using a FastAPI webserver. It uses LightPipelines, what means having a very good performance but not leveraging Spark Clusters. Also, no Load Balancer is available in the suggestion, but you can create your own. Best approach if you look for performance.
+ `Databricks` and `MLFlow`: Using MLFlow Serve or Databricks Jobs APIs to serve for inference Spark NLP pipelines from within Databricks. Best approach if you look for scalability within Databricks.


#### Updated Documentation on Installing Spark NLP For Healthcare in AWS EMR (Jupyter, Livy, Yarn, Hadoop)

Ready-to-go Spark NLP for Healthcare environment in AWS EMR. Full instructions are [here](https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/platforms/emr).

#### New Series of Notebooks to Reproduce the Academic Papers Published by Our Colleagues

You can find all these notebooks [here](https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/tutorials/academic)

#### PySpark Tutorial Notebooks to Let Non-Spark Users to Get Started with Apache Spark Ecosystem in Python

John Snow Labs has created a series of 8 notebooks to go over PySpark from zero to hero. Notebooks cover PySpark essentials, DataFrame creation, querying, importing data from different formats, functions / udfs, Spark MLLib examples (regression, classification, clustering) and Spark NLP best practises (usage of parquet, repartition, coalesce, custom annotators, etc).

You can find all these notebooks [here](https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/tutorials/PySpark).

#### New & Updated Notebooks

+ `Series of academic notebooks` : A new series of academic paper notebooks, available [here](https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/tutorials/academic)
+ `Clinical_Deidentification_in_Spanish.ipynb`: A notebook showcasing Clinical Deidentification in Spanish, available [here](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/4.2.Clinical_Deidentification_in_Spanish.ipynb).
+ `Clinical_Deidentification_Comparison.ipynb`: A new series of comparisons between different Deidentification libraries. So far, it contains Spark NLP for Healthcare and ScrubaDub with Spacy Transformers. Available [here](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/4.3.Clinical_Deidentification_Comparison.ipynb).
+ `Scope_window_tuning_assertion_status_detection.ipynb`: How to finetune Assertion Status using the Scope Window. Available [here](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/2.1.Scope_window_tuning_assertion_status_detection.ipynb)
+ `Clinical_Longformer_vs_BertSentence_&_USE.ipynb`: A Comparison of how Clinical Longformer embeddings, averaged by the Sentence Embeddings annotator, performs compared to BioBert and UniversalSentenceEncoding. Link [here](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/clinical_text_classification/3.Clinical_Longformer_vs_BertSentence_%26_USE.ipynb).
+ `Serving_SparkNLP_with_Synapse.ipynb`: Serving SparkNLP for production purposes using Synapse ML. Available [here](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/RestAPI/Serving_SparkNLP_with_Synapse.ipynb)
+ `Serving_SparkNLP_with_FastAPI_and_LP.ipynb`: Serving SparkNLP for production purposes using FastAPI, RestAPI and LightPipelines. Available [here](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/RestAPI/Serving_SparkNLP_with_FastAPI_and_LP.ipynb)
+ `Series of PySpark tutorial notebooks`: Available [here](https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/tutorials/PySpark)

#### List of Recently Updated or Added Models

+ `sbiobertresolve_hcpcs`
+ `bert_sequence_classifier_rct_biobert`
+ `ner_deid_generic_augmented_es`
+ `ner_deid_subentity_augmented_es`
+ `ner_deid_generic_roberta_augmented_es`
+ `ner_deid_subentity_roberta_augmented_es`
+ `clinical_deidentification_augmented_es`

**For all Spark NLP for healthcare models, please check : [Models Hub Page](https://nlp.johnsnowlabs.com/models?edition=Spark+NLP+for+Healthcare)**

## 3.4.1

We are glad to announce that Spark NLP Healthcare 3.4.1 has been released!

#### Highlights

+ Brand new Spanish deidentification NER models
+ Brand new Spanish deidentification pretrained pipeline
+ New clinical NER model to detect supplements
+ New RxNorm sentence entity resolver model
+ New `EntityChunkEmbeddings` annotator
+ New `MedicalBertForSequenceClassification` annotator
+ New `MedicalDistilBertForSequenceClassification` annotator
+ New `MedicalDistilBertForSequenceClassification` and `MedicalBertForSequenceClassification` models
+ Redesign of the `ContextualParserApproach` annotator
+ `getClasses` method in `RelationExtractionModel` and `RelationExtractionDLModel` annotators
+ Label customization feature for `RelationExtractionModel` and `RelationExtractionDL` models
+ `useBestModel` parameter in `MedicalNerApproach` annotator
+ Early stopping feature in `MedicalNerApproach` annotator
+ Multi-Language support for faker and regex lists of `Deidentification` annotator
+ Spark 3.2.0 compatibility for the entire library
+ Saving visualization feature in `spark-nlp-display` library
+ Deploying a custom Spark NLP image (for opensource, healthcare, and Spark OCR) to an enterprise version of Kubernetes: OpenShift
+ New speed benchmarks table on databricks
+ New & Updated Notebooks
+ List of recently updated or added models

#### Brand New Spanish Deidentification NER Models

We trained two new NER models to find PHI data (protected health information) that may need to be deidentified in **Spanish**. `ner_deid_generic` and `ner_deid_subentity` models are trained with in-house annotations. Both also are available for using Roberta Spanish Clinical Embeddings and sciwiki 300d.

+ `ner_deid_generic` : Detects 7 PHI entities in Spanish (`DATE`, `NAME`, `LOCATION`, `PROFESSION`, `CONTACT`, `AGE`, `ID`).

+ `ner_deid_subentity` : Detects 13 PHI sub-entities in Spanish (`PATIENT`, `HOSPITAL`, `DATE`, `ORGANIZATION`, `E-MAIL`, `USERNAME`, `LOCATION`, `ZIP`, `MEDICALRECORD`, `PROFESSION`, `PHONE`, `DOCTOR`, `AGE`).

*Example* :

```bash
...
embeddings = WordEmbeddingsModel.pretrained("embeddings_sciwiki_300d","es","clinical/models")\
    .setInputCols(["sentence", "token"])\
    .setOutputCol("embeddings")

deid_ner = MedicalNerModel.pretrained("ner_deid_generic", "es", "clinical/models")\
    .setInputCols(["sentence", "token", "embeddings"])\
    .setOutputCol("ner")

deid_sub_entity_ner = MedicalNerModel.pretrained("ner_deid_subentity", "es", "clinical/models")\
    .setInputCols(["sentence", "token", "embeddings"])\
    .setOutputCol("ner_sub_entity")
...

text = """Antonio Pérez Juan, nacido en Cadiz, España. Aún no estaba vacunado, se infectó con Covid-19 el dia 14/03/2020
y tuvo que ir al Hospital. Fue tratado con anticuerpos monoclonales en la Clinica San Carlos.."""
result = model.transform(spark.createDataFrame([[text]], ["text"]))
```

*Results* :

```bash
| chunk              | ner_deid_generic_chunk | ner_deid_subentity_chunk |
|--------------------|------------------------|--------------------------|
| Antonio Pérez Juan | NAME                   | PATIENT                  |
| Cádiz              | LOCATION               | LOCATION                 |
| España             | LOCATION               | LOCATION                 |
| 14/03/2022         | DATE                   | DATE                     |
| Clínica San Carlos | LOCATION               | HOSPITAL                 |
```

#### Brand New Spanish Deidentification Pretrained Pipeline

We developed a clinical deidentification pretrained pipeline that can be used to deidentify PHI information from **Spanish** medical texts. The PHI information will be masked and obfuscated in the resulting text. The pipeline can mask, fake or obfuscate the following entities: `AGE`, `DATE`, `PROFESSION`, `E-MAIL`, `USERNAME`, `LOCATION`, `DOCTOR`, `HOSPITAL`, `PATIENT`, `URL`, `IP`, `MEDICALRECORD`, `IDNUM`, `ORGANIZATION`, `PHONE`, `ZIP`, `ACCOUNT`, `SSN`, `PLATE`, `SEX` and `IPADDR`.

```bash
from sparknlp.pretrained import PretrainedPipeline
deid_pipeline = PretrainedPipeline("clinical_deidentification", "es", "clinical/models")

sample_text = """Datos del paciente. Nombre:  Jose . Apellidos: Aranda Martinez. NHC: 2748903. NASS: 26 37482910."""

result = deid_pipe.annotate(text)

print("\n".join(result['masked']))
print("\n".join(result['masked_with_chars']))
print("\n".join(result['masked_fixed_length_chars']))
print("\n".join(result['obfuscated']))
```
*Results*:

```bash
Masked with entity labels
------------------------------
Datos del paciente. Nombre:  <PATIENT> . Apellidos: <PATIENT>. NHC: <SSN>. NASS: <SSN> <SSN>

Masked with chars
------------------------------
Datos del paciente. Nombre:  [**] . Apellidos: [*************]. NHC: [*****]. NASS: [**] [******]

Masked with fixed length chars
------------------------------
Datos del paciente. Nombre:  **** . Apellidos: ****. NHC: ****. NASS: **** ****

Obfuscated
------------------------------
Datos del paciente. Nombre:  Sr. Lerma . Apellidos: Aristides Gonzalez Gelabert. NHC: BBBBBBBBQR648597. NASS: 041010000011 RZRM020101906017 04.
```

#### New Clinical NER Model to Detect Supplements

We are releasing `ner_supplement_clinical` model that can extract benefits of using drugs for certain conditions. It can label detected entities as `CONDITION` and `BENEFIT`. Also this model is trained on the dataset that is released by Spacy in their HealthSea product. Here is the benchmark comparison of both versions:

|Entity|Spark NLP| Spacy-HealthSea|
|-|-|-|
|BENEFIT|0.8729641|0.8330684|
|CONDITION|0.8339274|0.8333333|

*Example* :

```bash
...
clinical_ner = MedicalNerModel.pretrained("ner_supplement_clinical", "en", "clinical/models") \
      .setInputCols(["sentence", "token", "embeddings"]) \
      .setOutputCol("ner_tags")
...

results = ner_model.transform(spark.createDataFrame([["Excellent!. The state of health improves, nervousness disappears, and night sleep improves. It also promotes hair and nail growth."]], ["text"]))
```

*Results* :

```bash
+------------------------+---------------+
| chunk                  | ner_label     |
+------------------------+---------------+
| nervousness            | CONDITION     |
| night sleep improves   | BENEFIT       |
| hair                   | BENEFIT       |
| nail                   | BENEFIT       |
+------------------------+---------------+
```

#### New RxNorm Sentence Entity Resolver Model

`sbiobertresolve_rxnorm_augmented_re` : This model maps clinical entities and concepts (like drugs/ingredients) to RxNorm codes without specifying the relations between the entities (relations are calculated on the fly inside the annotator) using sbiobert_base_cased_mli Sentence Bert Embeddings (EntityChunkEmbeddings).

*Example* :

```python
...
rxnorm_resolver = SentenceEntityResolverModel\
      .pretrained("sbiobertresolve_rxnorm_augmented_re", "en", "clinical/models")\
      .setInputCols(["entity_chunk_embeddings"])\
      .setOutputCol("rxnorm_code")\
      .setDistanceFunction("EUCLIDEAN")
...
```

#### New `EntityChunkEmbeddings` Annotator

We have a new `EntityChunkEmbeddings` annotator to compute a weighted average vector representing entity-related vectors. The model's input usually consists of chunks of recognized named entities produced by MedicalNerModel. We can specify relations between the entities by the `setTargetEntities()` parameter, and the internal Relation Extraction model finds related entities and creates a chunk. Embedding for the chunk is calculated according to the weights specified in the `setEntityWeights()` parameter.

For instance, the chunk `warfarin sodium 5 MG Oral Tablet` has `DRUG`, `STRENGTH`, `ROUTE`, and `FORM` entity types. Since DRUG label is the most prominent label for resolver models, now we can assign weight to prioritize DRUG label (i.e `{"DRUG": 0.8, "STRENGTH": 0.2, "ROUTE": 0.2, "FORM": 0.2}` as shown below). In other words, embeddings of these labels are multipled by the assigned weights such as `DRUG` by `0.8`.

For more details and examples, please check [Sentence Entity Resolvers with EntityChunkEmbeddings Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/3.2.Sentence_Entity_Resolvers_with_EntityChunkEmbeddings.ipynb) in the Spark NLP workshop repo.

*Example* :

```python
...

drug_chunk_embeddings = EntityChunkEmbeddings()\
    .pretrained("sbiobert_base_cased_mli","en","clinical/models")\
    .setInputCols(["ner_chunks", "dependencies"])\
    .setOutputCol("drug_chunk_embeddings")\
    .setMaxSyntacticDistance(3)\
    .setTargetEntities({"DRUG": ["STRENGTH", "ROUTE", "FORM"]})\
    .setEntityWeights({"DRUG": 0.8, "STRENGTH": 0.2, "ROUTE": 0.2, "FORM": 0.2})

rxnorm_resolver = SentenceEntityResolverModel\
    .pretrained("sbiobertresolve_rxnorm_augmented_re", "en", "clinical/models")\
    .setInputCols(["drug_chunk_embeddings"])\
    .setOutputCol("rxnorm_code")\
    .setDistanceFunction("EUCLIDEAN")

rxnorm_weighted_pipeline_re = Pipeline(
    stages = [
        documenter,
        sentence_detector,
        tokenizer,
        embeddings,
        posology_ner_model,
        ner_converter,
        pos_tager,
        dependency_parser,
        drug_chunk_embeddings,
        rxnorm_resolver])

sampleText = ["The patient was given metformin 500 mg, 2.5 mg of coumadin and then ibuprofen.",
              "The patient was given metformin 400 mg, coumadin 5 mg, coumadin, amlodipine 10 MG"]

data_df = spark.createDataFrame(sample_df)
results = rxnorm_weighted_pipeline_re.fit(data_df).transform(data_df)
```

The internal relation extraction creates the chunks here, and the embedding is computed according to the weights.

*Results* :
```bash
+-----+----------------+--------------------------+--------------------------------------------------+
|index|           chunk|rxnorm_code_weighted_08_re|                                      Concept_Name|
+-----+----------------+--------------------------+--------------------------------------------------+
|    0|metformin 500 mg|                    860974|metformin hydrochloride 500 MG:::metformin 500 ...|
|    0| 2.5 mg coumadin|                    855313|warfarin sodium 2.5 MG [Coumadin]:::warfarin so...|
|    0|       ibuprofen|                   1747293|ibuprofen Injection:::ibuprofen Pill:::ibuprofe...|
|    1|metformin 400 mg|                    332809|metformin 400 MG:::metformin 250 MG Oral Tablet...|
|    1|   coumadin 5 mg|                    855333|warfarin sodium 5 MG [Coumadin]:::warfarin sodi...|
|    1|        coumadin|                    202421|Coumadin:::warfarin sodium 2 MG/ML Injectable S...|
|    1|amlodipine 10 MG|                    308135|amlodipine 10 MG Oral Tablet:::amlodipine 10 MG...|
+-----+----------------+--------------------------+--------------------------------------------------+
```

#### New `MedicalBertForSequenceClassification` Annotator

We developed a new annotator called `MedicalBertForSequenceClassification`. It can load BERT Models with sequence classification/regression head on top (a linear layer on top of the pooled output) e.g. for multi-class document classification tasks.

#### New `MedicalDistilBertForSequenceClassification` Annotator

We developed a new annotator called `MedicalDistilBertForSequenceClassification`. It can load DistilBERT Models with sequence classification/regression head on top (a linear layer on top of the pooled output) e.g. for multi-class document classification tasks.

#### New `MedicalDistilBertForSequenceClassification` and `MedicalBertForSequenceClassification` Models

We are releasing a new `MedicalDistilBertForSequenceClassification` model and three new `MedicalBertForSequenceClassification` models.

- `bert_sequence_classifier_ade_biobert`: a classifier for detecting if a sentence is talking about a possible ADE (`TRUE`, `FALSE`)

- `bert_sequence_classifier_gender_biobert`: a classifier for detecting the gender of the main subject of the sentence (`MALE`, `FEMALE`, `UNKNOWN`)

- `bert_sequence_classifier_pico_biobert`: a classifier for detecting the class of a sentence according to PICO framework (`CONCLUSIONS`, `DESIGN_SETTING`,`INTERVENTION`, `PARTICIPANTS`, `FINDINGS`, `MEASUREMENTS`, `AIMS`)

*Example* :

```python
...
sequenceClassifier = MedicalBertForSequenceClassification.pretrained("bert_sequence_classifier_pico", "en", "clinical/models")\
    .setInputCols(["document","token"])\
    .setOutputCol("class")
...

sample_text = "To compare the results of recording enamel opacities using the TF and modified DDE indices."

result = sequence_clf_model.transform(spark.createDataFrame([[sample_text]]).toDF("text"))
```

*Results* :

```
+-------------------------------------------------------------------------------------------+-----+
|text                                                                                       |label|
+-------------------------------------------------------------------------------------------+-----+
|To compare the results of recording enamel opacities using the TF and modified DDE indices.|AIMS |
+-------------------------------------------------------------------------------------------+-----+
```


+ `distilbert_sequence_classifier_ade` : This model is a DistilBertForSequenceClassification model for classifying clinical texts whether they contain ADE (`TRUE`, `FALSE`).

*Example* :

```python
...
sequenceClassifier = MedicalDistilBertForSequenceClassification\
      .pretrained('distilbert_sequence_classifier_ade', 'en', 'clinical/models') \
      .setInputCols(['token', 'document']) \
      .setOutputCol('class')
...

sample_text = "I felt a bit drowsy and had blurred vision after taking Aspirin."

result = sequence_clf_model.transform(spark.createDataFrame([[sample_text]]).toDF("text"))
```

*Results* :

```
+----------------------------------------------------------------+-----+
|text                                                            |label|
+----------------------------------------------------------------+-----+
|I felt a bit drowsy and had blurred vision after taking Aspirin.| True|
+----------------------------------------------------------------+-----+
```


#### Redesign of the `ContextualParserApproach` Annotator

- We've dropped the annotator's `contextMatch` parameter and removed the need for a `context` field when feeding a JSON configuration file to the annotator. Context information can now be fully defined using the `prefix`, `suffix` and `contextLength` fields in the JSON configuration file.
- We've also fixed issues with the `contextException` field in the JSON configuration file - it was mismatching values in documents with several sentences and ignoring exceptions situated to the right of a word/token.
- The `ruleScope` field in the JSON configuration file can now be set to `document` instead of `sentence`. This allows you to match multi-word entities like "New York" or "Salt Lake City". You can do this by setting `"ruleScope" : "document"` in the JSON configuration file and feeding a dictionary (csv or tsv) to the annotator with its `setDictionary` parameter. These changes also mean that we've dropped the `updateTokenizer` parameter since the new capabilities of `ruleScope` improve the user experience for matching multi-word entities.
- You can now feed in a dictionary in your chosen format - either vertical or horizontal. You can set that with the following parameter: `setDictionary("dictionary.csv", options={"orientation":"vertical"})`
- Lastly, there was an improvement made to the confidence value calculation process to better measure successful hits.

For more explanation and examples, please check this [Contextual Parser medium article](https://medium.com/spark-nlp/contextual-parser-increased-flexibility-extracting-entities-in-spark-nlp-123ed58672f0) and [Contextual Parser Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/1.2.Contextual_Parser_Rule_Based_NER.ipynb).

#### `getClasses` Method in `RelationExtractionModel` and `RelationExtractionDLModel` Annotators

Now you can use `getClasses()` method for checking the relation labels of RE models (RelationExtractionModel and RelationExtractionDLModel) like MedicalNerModel().

*Example* :
```python
clinical_re_Model = RelationExtractionModel()\
    .pretrained("re_temporal_events_clinical", "en", 'clinical/models')\
    .setInputCols(["embeddings", "pos_tags", "ner_chunks", "dependencies"])\
    .setOutputCol("relations")\

clinical_re_Model.getClasses()
```

*Output* :
```output
['OVERLAP', 'BEFORE', 'AFTER']
```

####  Label Customization Feature for `RelationExtractionModel` and `RelationExtractionDL` Models

We are releasing label customization feature for Relation Extraction and Relation Extraction DL models by using `.setCustomLabels()` parameter.

*Example* :

```python
...
reModel = RelationExtractionModel.pretrained("re_ade_clinical", "en", 'clinical/models')\
    .setInputCols(["embeddings", "pos_tags", "ner_chunks", "dependencies"])\
    .setOutputCol("relations")\
    .setMaxSyntacticDistance(10)\
    .setRelationPairs(["drug-ade, ade-drug"])\
    .setCustomLabels({"1": "is_related", "0": "not_related"})

redl_model = RelationExtractionDLModel.pretrained('redl_ade_biobert', 'en', "clinical/models") \
    .setPredictionThreshold(0.5)\
    .setInputCols(["re_ner_chunks", "sentences"]) \
    .setOutputCol("relations")\
    .setCustomLabels({"1": "is_related", "0": "not_related"})
...

sample_text = "I experienced fatigue and muscle cramps after taking Lipitor but no more adverse after passing Zocor."
result = model.transform(spark.createDataFrame([[sample_text]]).toDF('text'))
```

*Results* :

```
+-----------+-------+-------------+-------+-------+----------+
|   relation|entity1|       chunk1|entity2| chunk2|confidence|
+-----------+-------+-------------+-------+-------+----------+
| is_related|    ADE|      fatigue|   DRUG|Lipitor| 0.9999825|
|not_related|    ADE|      fatigue|   DRUG|  Zocor| 0.9960077|
| is_related|    ADE|muscle cramps|   DRUG|Lipitor|       1.0|
|not_related|    ADE|muscle cramps|   DRUG|  Zocor|   0.94971|
+-----------+-------+-------------+-------+-------+----------+
```


#### `useBestModel` Parameter in `MedicalNerApproach` Annotator

Introducing `useBestModel` param in MedicalNerApproach annotator. This param preserves and restores the model that has achieved the best performance at the end of the training. The priority is metrics from testDataset (micro F1), metrics from validationSplit (micro F1), and if none is set it will keep track of loss during the training.

*Example* :
```python
med_ner = MedicalNerApproach()\
    .setInputCols(["sentence", "token", "embeddings"])\
    .setLabelColumn("label")\
    .setOutputCol("ner")\
    ...
    ...
    .setUseBestModel(True)\
```

#### Early Stopping Feature in `MedicalNerApproach` Annotator

Introducing `earlyStopping` feature for MedicalNerApproach(). You can stop training at the point when the perforfmance on test/validation dataset starts to degrage. Two params are added to MedicalNerApproach() in order to use this feature:

+ `earlyStoppingCriterion` : (float) This is used set the minimal improvement of the test metric to terminate training. The metric monitored is the same as the metrics used in `useBestModel` (macro F1 when using test/validation set, loss otherwise). Default is 0 which means no early stopping is applied.

+ `earlyStoppingPatience`: (int), the number of epoch without improvement which will be tolerated. Default is 0, which means that early stopping will occur at the first time when performance in the current epoch is no better than in the previous epoch.

*Example* :

```python
med_ner = MedicalNerApproach()\
    .setInputCols(["sentence", "token", "embeddings"])\
    .setLabelColumn("label")\
    .setOutputCol("ner")\
    ...
    ...
    .setTestDataset(test_data_parquet_path)\
    .setEarlyStoppingCriterion(0.01)\
    .setEarlyStoppingPatience(3)\
```

#### Multi-Language Support for Faker and Regex Lists of `Deidentification` Annotator

We have a new `.setLanguage()` parameter in order to use internal Faker and Regex list for multi-language texts. When you are working with German and Spanish texts for a Deidentification, you can set this parameter to `de` for German and `es` for Spanish. Default value of this parameter is `en`.

*Example* :

```python
deid_obfuscated = DeIdentification()\
      .setInputCols(["sentence", "token", "ner_chunk"]) \
      .setOutputCol("obfuscated") \
      .setMode("obfuscate")\
      .setLanguage('de')\
      .setObfuscateRefSource("faker")\
```

#### Spark 3.2.0 Compatibility for the Entire Library

Now we can use the [Spark 3.2.0](https://spark.apache.org/docs/3.2.0/) version for Spark NLP for Healthcare by setting `spark32=True` in `sparknlp_jsl.start()` function.

```bash
! pip install --ignore-installed -q pyspark==3.2.0
```

```bash
import sparknlp_jsl

spark = sparknlp_jsl.start(SECRET, spark32=True)
```

#### Saving Visualization Feature in `spark-nlp-display` Library

We have a new `save_path` parameter in `spark-nlp-display` library for saving any visualization results in Spark NLP.

*Example* :

```bash
from sparknlp_display import NerVisualizer

visualiser = NerVisualizer()

visualiser.display(light_result[0], label_col='ner_chunk', document_col='document', save_path="display_result.html")
```

#### Deploying a Custom Spark NLP Image (for opensource, healthcare, and Spark OCR) to an Enterprise Version of Kubernetes: OpenShift

Spark NLP for opensource, healthcare, and SPARK OCR is now available for Openshift - enterprise version of Kubernetes. For deployment, please refer to:

Github Link: https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/platforms/openshift

Youtube: https://www.youtube.com/watch?v=FBes-6ylFrM&ab_channel=JohnSnowLabs

#### New Speed Benchmarks Table on Databricks

We prepared a speed benchmark table by running a clinical BERT For Token Classification model pipeline on various number of repartitioning and writing the results to parquet or delta formats. You can find the details here : [Clinical Bert For Token Classification Benchmark Experiment](https://nlp.johnsnowlabs.com/docs/en/benchmark#clinical-bert-for-token-classification-benchmark-experiment).

#### New & Updated Notebooks

+ We have updated our existing workshop notebooks with v3.4.0 by adding new features and functionalities.
+ You can find the workshop notebooks updated with previous versions in the branches named with the relevant version.
+ We have updated the [ContextualParser Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/1.2.Contextual_Parser_Rule_Based_NER.ipynb) with the new updates in this version.
+ We have a new [Sentence Entity Resolvers with EntityChunkEmbeddings Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/3.2.Sentence_Entity_Resolvers_with_EntityChunkEmbeddings.ipynb) for the new `EntityChunkEmbeddings` annotator.

**To see more, please check : [Spark NLP Healthcare Workshop Repo](https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/tutorials/Certification_Trainings/Healthcare)**

#### List of Recently Updated or Added Models

- `bert_sequence_classifier_ade_en`
- `bert_sequence_classifier_gender_biobert_en`
- `bert_sequence_classifier_pico_biobert_en`
- `distilbert_sequence_classifier_ade_en`
- `bert_token_classifier_ner_supplement_en`
- `deid_pipeline_es`
- `ner_deid_generic_es`
- `ner_deid_generic_roberta_es`
- `ner_deid_subentity_es`
- `ner_deid_subentity_roberta_es`
- `ner_nature_nero_clinical_en`
- `ner_supplement_clinical_en`
- `sbiobertresolve_clinical_abbreviation_acronym_en`
- `sbiobertresolve_rxnorm_augmented_re`

**For all Spark NLP for healthcare models, please check : [Models Hub Page](https://nlp.johnsnowlabs.com/models?edition=Spark+NLP+for+Healthcare)**

## 3.4.0

We are glad to announce that Spark NLP Healthcare 3.4.0 has been released!
This is a massive release: new features, new models, academic papers, and more!

#### Highlights

+ New German Deidentification NER Models
+ New German Deidentification Pretrained Pipeline
+ New Clinical NER Models
+ New AnnotationMerger Annotator
+ New MedicalBertForTokenClassifier Annotator
+ New BERT-Based Clinical NER Models
+ New Clinical Relation Extraction Models
+ New LOINC, SNOMED, UMLS and Clinical Abbreviation Entity Resolver Models
+ New ICD10 to ICD9 Code Mapping Pretrained Pipeline
+ New Clinical Sentence Embedding Models
+ Printing Validation and Test Logs for MedicalNerApproach and AssertionDLApproach
+ Filter Only the Regex Entities Feature in Deidentification Annotator
+ Add `.setMaskingPolicy` Parameter in Deidentification Annotator
+ Add `.cache_folder` Parameter in `UpdateModels.updateCacheModels()`
+ S3 Access Credentials No Longer Shipped Along Licenses
+ Enhanced Security for the Library and log4shell Update
+ New Peer-Reviewed Conference Paper on Clinical Relation Extraction
+ New Peer-Reviewed Conference Paper on Adverse Drug Events Extraction
+ New and Updated Notebooks

#### New German Deidentification NER Models

We trained two new NER models to find PHI data (protected health information) that may need to be deidentified in **German**.
`ner_deid_generic` and `ner_deid_subentity` models are trained with in-house annotations.

+ `ner_deid_generic` : Detects 7 PHI entities in German (`DATE`, `NAME`, `LOCATION`, `PROFESSION`, `CONTACT`, `AGE`, `ID`).

+ `ner_deid_subentity` : Detects 12 PHI sub-entities in German (`PATIENT`, `HOSPITAL`, `DATE`, `ORGANIZATION`, `CITY`, `STREET`, `USERNAME`, `PROFESSION`, `PHONE`, `COUNTRY`, `DOCTOR`, `AGE`).

*Example* :

```bash
...

embeddings = WordEmbeddingsModel.pretrained("w2v_cc_300d","de","clinical/models")\
    .setInputCols(["sentence", "token"])\
    .setOutputCol("embeddings")

deid_ner = MedicalNerModel.pretrained("ner_deid_generic", "de", "clinical/models")\
  .setInputCols(["sentence", "token", "embeddings"])\
  .setOutputCol("ner")

deid_sub_entity_ner = MedicalNerModel.pretrained("ner_deid_subentity", "de", "clinical/models")\
  .setInputCols(["sentence", "token", "embeddings"])\
  .setOutputCol("ner_sub_entity")
...

text = """Michael Berger wird am Morgen des 12 Dezember 2018 ins St. Elisabeth-Krankenhaus
in Bad Kissingen eingeliefert. Herr Berger ist 76 Jahre alt und hat zu viel Wasser in den Beinen."""

result = model.transform(spark.createDataFrame([[text]], ["text"]))
```

*Results* :

```bash
+-------------------------+----------------------+-------------------------+
|chunk                    |ner_deid_generic_chunk|ner_deid_subentity_chunk |
+-------------------------+----------------------+-------------------------+
|Michael Berger           |NAME                  |PATIENT                  |
|12 Dezember 2018         |DATE                  |DATE                     |
|St. Elisabeth-Krankenhaus|LOCATION              |HOSPITAL                 |
|Bad Kissingen            |LOCATION              |CITY                     |
|Berger                   |NAME                  |PATIENT                  |
|76                       |AGE                   |AGE                      |
+-------------------------+----------------------+-------------------------+
```
#### New German Deidentification Pretrained Pipeline

We developed a clinical deidentification pretrained pipeline that can be used to deidentify PHI information from **German** medical texts. The PHI information will be masked and obfuscated in the resulting text. The pipeline can mask and obfuscate `PATIENT`, `HOSPITAL`, `DATE`, `ORGANIZATION`, `CITY`, `STREET`, `USERNAME`, `PROFESSION`, `PHONE`, `COUNTRY`, `DOCTOR`, `AGE`, `CONTACT`, `ID`, `PHONE`, `ZIP`, `ACCOUNT`, `SSN`, `DLN`, `PLATE` entities.

*Example* :

```bash
...
from sparknlp.pretrained import PretrainedPipeline

deid_pipeline = PretrainedPipeline("clinical_deidentification", "de", "clinical/models")

text = """Zusammenfassung : Michael Berger wird am Morgen des 12 Dezember 2018 ins St.Elisabeth Krankenhaus in Bad Kissingen eingeliefert.
Herr Michael Berger ist 76 Jahre alt und hat zu viel Wasser in den Beinen.

Persönliche Daten :
ID-Nummer: T0110053F
Platte A-BC124
Kontonummer: DE89370400440532013000
SSN : 13110587M565
Lizenznummer: B072RRE2I55
Adresse : St.Johann-Straße 13 19300"""

result = deid_pipe.annotate(text)

print("\n".join(result['masked']))
print("\n".join(result['obfuscated']))
print("\n".join(result['masked_with_chars']))
print("\n".join(result['masked_fixed_length_chars']))

```
*Results* :

```bash
Zusammenfassung : <PATIENT> wird am Morgen des <DATE> ins <HOSPITAL> eingeliefert.
Herr <PATIENT> ist <AGE> Jahre alt und hat zu viel Wasser in den Beinen.
Persönliche Daten :
ID-Nummer: <ID>
Platte <PLATE>
Kontonummer: <ACCOUNT>
SSN : <SSN>
Lizenznummer: <DLN>
Adresse : <STREET> <ZIP>

Zusammenfassung : Herrmann Kallert wird am Morgen des 11-26-1977 ins International Neuroscience eingeliefert.
Herr Herrmann Kallert ist 79 Jahre alt und hat zu viel Wasser in den Beinen.
Persönliche Daten :
ID-Nummer: 136704D357
Platte QA348G
Kontonummer: 192837465738
SSN : 1310011981M454
Lizenznummer: XX123456
Adresse : Klingelhöferring 31206

Zusammenfassung : **** wird am Morgen des **** ins **** eingeliefert.
Herr **** ist **** Jahre alt und hat zu viel Wasser in den Beinen.
Persönliche Daten :
ID-Nummer: ****
Platte ****
Kontonummer: ****
SSN : ****
Lizenznummer: ****
Adresse : **** ****

Zusammenfassung : [************] wird am Morgen des [**************] ins [**********************] eingeliefert.
Herr [************] ist ** Jahre alt und hat zu viel Wasser in den Beinen.
Persönliche Daten :
ID-Nummer: [*******]
Platte [*****]
Kontonummer: [********************]
SSN : [**********]
Lizenznummer: [*********]
Adresse : [*****************] [***]
```

#### New Clinical NER Models

We have two new clinical NER models.

+ `ner_abbreviation_clinical` : This model is trained to extract clinical abbreviations and acronyms in texts and labels these entities as `ABBR`.

*Example* :

```bash
...
clinical_ner = MedicalNerModel.pretrained("ner_abbreviation_clinical", "en", "clinical/models")\
  .setInputCols(["sentence", "token", "embeddings"])\
  .setOutputCol("ner")
...

results = ner_model.transform(spark.createDataFrame([["Gravid with estimated fetal weight of 6-6/12 pounds. LOWER EXTREMITIES: No edema. LABORATORY DATA: Laboratory tests include a CBC which is normal. Blood Type: AB positive. Rubella: Immune. VDRL: Nonreactive. Hepatitis C surface antigen: Negative. HIV: Negative. One-Hour Glucose: 117. Group B strep has not been done as yet."]], ["text"]))
```

*Results* :

```bash
+-----+---------+
|chunk|ner_label|
+-----+---------+
|CBC  |ABBR     |
|AB   |ABBR     |
|VDRL |ABBR     |
|HIV  |ABBR     |
+-----+---------+
```

+ `ner_drugprot_clinical` : This model detects chemical compounds/drugs and genes/proteins in medical text and research articles. Here are the labels it can detect : `GENE`, `CHEMICAL`, `GENE_AND_CHEMICAL`.

*Example* :

```bash
...
clinical_ner = MedicalNerModel.pretrained("ner_drugprot_clinical", "en", "clinical/models")\
  .setInputCols(["sentence", "token", "embeddings"])\
  .setOutputCol("ner")
...

results = ner_model.transform(spark.createDataFrame([["Anabolic effects of clenbuterol on skeletal muscle are mediated by beta 2-adrenoceptor activation"]], ["text"]))
```

*Results* :

```bash
|    | chunk                | ner_label         |
|---:|:---------------------|:------------------|
|  0 | clenbuterol          | CHEMICAL          |
|  1 | beta 2-adrenoceptor  | GENE              |

```

#### New AnnotationMerger Annotator

A new annotator: `AnnotationMerger`. Besides NERs, now we will be able to merge results of **Relation Extraction models** and **Assertion models** as well. Therefore, it can merge results of Relation Extraction models, NER models, and Assertion Status models.

*Example-1* :

```bash
...
annotation_merger = AnnotationMerger()\
    .setInputCols("ade_relations", "pos_relations", "events_relations")\
    .setInputType("category")\
    .setOutputCol("all_relations")
...

results = ann_merger_model.transform(spark.createDataFrame([["The patient was prescribed 1 unit of naproxen for 5 days after meals for chronic low back pain. The patient was also given 1 unit of oxaprozin daily for rheumatoid arthritis presented with tense bullae and cutaneous fragility on the face and the back of the hands."]], ["text"]))
```

*Results-1* :

```bash
|    | all_relations   | all_relations_entity1   | all_relations_chunk1   | all_relations_entity2   | all_relations_chunk2                                      |
|---:|:----------------|:------------------------|:-----------------------|:------------------------|:----------------------------------------------------------|
|  0 | 1               | DRUG                    | oxaprozin              | ADE                     | tense bullae                                              |
|  1 | 1               | DRUG                    | oxaprozin              | ADE                     | cutaneous fragility on the face and the back of the hands |
|  2 | DOSAGE-DRUG     | DOSAGE                  | 1 unit                 | DRUG                    | naproxen                                                  |
|  3 | DRUG-DURATION   | DRUG                    | naproxen               | DURATION                | for 5 days                                                |
|  4 | DOSAGE-DRUG     | DOSAGE                  | 1 unit                 | DRUG                    | oxaprozin                                                 |
|  5 | DRUG-FREQUENCY  | DRUG                    | oxaprozin              | FREQUENCY               | daily                                                     |
|  6 | OVERLAP         | TREATMENT               | naproxen               | DURATION                | 5 days                                                    |
|  7 | OVERLAP         | TREATMENT               | oxaprozin              | FREQUENCY               | daily                                                     |
|  8 | BEFORE          | TREATMENT               | oxaprozin              | PROBLEM                 | rheumatoid arthritis                                      |
|  9 | AFTER           | TREATMENT               | oxaprozin              | OCCURRENCE              | presented                                                 |
| 10 | OVERLAP         | FREQUENCY               | daily                  | PROBLEM                 | rheumatoid arthritis                                      |
| 11 | OVERLAP         | FREQUENCY               | daily                  | PROBLEM                 | tense bullae                                              |
| 12 | OVERLAP         | FREQUENCY               | daily                  | PROBLEM                 | cutaneous fragility on the face                           |
| 13 | BEFORE          | PROBLEM                 | rheumatoid arthritis   | OCCURRENCE              | presented                                                 |
| 14 | OVERLAP         | PROBLEM                 | rheumatoid arthritis   | PROBLEM                 | tense bullae                                              |
| 15 | OVERLAP         | PROBLEM                 | rheumatoid arthritis   | PROBLEM                 | cutaneous fragility on the face                           |
| 16 | BEFORE          | OCCURRENCE              | presented              | PROBLEM                 | tense bullae                                              |
| 17 | BEFORE          | OCCURRENCE              | presented              | PROBLEM                 | cutaneous fragility on the face                           |
| 18 | OVERLAP         | PROBLEM                 | tense bullae           | PROBLEM                 | cutaneous fragility on the face                           |
```

*Example-2* :

```bash
...
ner_annotation_merger = AnnotationMerger()\
    .setInputCols("ner_chunk", "radiology_ner_chunk", "jsl_ner_chunk")\
    .setInputType("chunk")\
    .setOutputCol("all_ners")

assertion_annotation_merger = AnnotationMerger()\
    .setInputCols("clinical_assertion", "radiology_assertion", "jsl_assertion")\
    .setInputType("assertion")\
    .setOutputCol("all_assertions")
...

results = ann_merger_model.transform(spark.createDataFrame([["The patient was prescribed 1 unit of naproxen for 5 days after meals for chronic low back pain. The patient was also given 1 unit of oxaprozin daily for rheumatoid arthritis presented with tense bullae and cutaneous fragility on the face and the back of the hands."]], ["text"]))
```

*Results-2* :

```bash
|    | ners                            | all_assertions   |
|---:|:--------------------------------|:-----------------|
|  0 | naproxen                        | present          |
|  1 | chronic low back pain           | present          |
|  2 | oxaprozin                       | present          |
|  3 | rheumatoid arthritis            | present          |
|  4 | tense bullae                    | present          |
|  5 | cutaneous fragility on the face | present          |
|  6 | low back                        | Confirmed        |
|  7 | pain                            | Confirmed        |
|  8 | rheumatoid arthritis            | Confirmed        |
|  9 | tense bullae                    | Confirmed        |
| 10 | cutaneous                       | Confirmed        |
| 11 | fragility                       | Confirmed        |
| 12 | face                            | Confirmed        |
| 13 | back                            | Confirmed        |
| 14 | hands                           | Confirmed        |
| 15 | 1 unit                          | Present          |
| 16 | naproxen                        | Past             |
| 17 | for 5 days                      | Past             |
| 18 | chronic                         | Someoneelse      |
| 19 | low                             | Past             |
| 20 | back pain                       | Present          |
| 21 | 1 unit                          | Past             |
| 22 | oxaprozin                       | Past             |
| 23 | daily                           | Past             |
| 24 | rheumatoid arthritis            | Present          |
| 25 | tense                           | Present          |
| 26 | bullae                          | Present          |
| 27 | cutaneous fragility             | Present          |
| 28 | face                            | Someoneelse      |
| 29 | back of the hands               | Present          |
```

#### New MedicalBertForTokenClassifier Annotator

We developed a new annotator called MedicalBertForTokenClassifier that can load BERT-Based clinical token classifier models head on top (a linear layer on top of the hidden-states output) e.g. for Named-Entity-Recognition (NER) tasks.


#### New BERT-Based Clinical NER Models

Here are the MedicalBertForTokenClassifier Models we have in the library at the moment:

+ `bert_token_classifier_ner_ade`
+ `bert_token_classifier_ner_anatomy`
+ `bert_token_classifier_ner_bionlp`
+ `bert_token_classifier_ner_cellular`
+ `bert_token_classifier_ner_chemprot`
+ `bert_token_classifier_ner_chemicals`
+ `bert_token_classifier_ner_jsl_slim`
+ `bert_token_classifier_ner_jsl`
+ `bert_token_classifier_ner_deid`
+ `bert_token_classifier_ner_drugs`
+ `bert_token_classifier_ner_clinical`
+ `bert_token_classifier_ner_bacteria`

In addition, we are releasing a new BERT-Based clinical NER model named `bert_token_classifier_drug_development_trials`. It is a `MedicalBertForTokenClassification` NER model to identify concepts related to drug development including `Trial Groups` , `End Points` , `Hazard Ratio`, and other entities in free text. It can detect the following entities: `Patient_Count`, `Duration`, `End_Point`, `Value`, `Trial_Group`, `Hazard_Ratio`, `Total_Patients`

*Example* :

```bash
...
tokenClassifier= MedicalBertForTokenClassifier.pretrained("bert_token_classifier_drug_development_trials", "en", "clinical/models")\
  .setInputCols("token", "document")\
  .setOutputCol("ner")\
  .setCaseSensitive(True)
...

results = ner_model.transform(spark.createDataFrame([["In June 2003, the median overall survival with and without topotecan were 4.0 and 3.6 months, respectively. The best complete response ( CR ) , partial response ( PR ) , stable disease and progressive disease were observed in 23, 63, 55 and 33 patients, respectively, with topotecan, and 11, 61, 66 and 32 patients, respectively, without topotecan."]], ["text"]))

```

*Results* :

```bash
|    | chunk             | entity        |
|---:|:------------------|:--------------|
|  0 | median            | Duration      |
|  1 | overall survival  | End_Point     |
|  2 | with              | Trial_Group   |
|  3 | without topotecan | Trial_Group   |
|  4 | 4.0               | Value         |
|  5 | 3.6 months        | Value         |
|  6 | 23                | Patient_Count |
|  7 | 63                | Patient_Count |
|  8 | 55                | Patient_Count |
|  9 | 33 patients       | Patient_Count |
| 10 | topotecan         | Trial_Group   |
| 11 | 11                | Patient_Count |
| 12 | 61                | Patient_Count |
| 13 | 66                | Patient_Count |
| 14 | 32 patients       | Patient_Count |
| 15 | without topotecan | Trial_Group   |
```

#### New Clinical Relation Extraction Models

We have two new clinical Relation Extraction models for detecting interactions between drugs and proteins. These models work hand-in-hand with the new `ner_drugprot_clinical` NER model and detect following relations between entities: `INHIBITOR`, `DIRECT-REGULATOR`, `SUBSTRATE`, `ACTIVATOR`, `INDIRECT-UPREGULATOR`, `INDIRECT-DOWNREGULATOR`, `ANTAGONIST`, `PRODUCT-OF`, `PART-OF`, `AGONIST`.

+ `redl_drugprot_biobert` : This model was trained using BERT and performs with higher accuracy.

+ `re_drugprot_clinical` : This model was trained using `RelationExtractionApproach()`.

*Example* :

```bash
...
drugprot_ner_tagger = MedicalNerModel.pretrained("ner_drugprot_clinical", "en", "clinical/models")\
    .setInputCols("sentences", "tokens", "embeddings")\
    .setOutputCol("ner_tags")   
...

drugprot_re_biobert = RelationExtractionDLModel()\
    .pretrained('redl_drugprot_biobert', "en", "clinical/models")\
    .setPredictionThreshold(0.9)\
    .setInputCols(["re_ner_chunks", "sentences"])\
    .setOutputCol("relations")

drugprot_re_clinical = RelationExtractionModel()\
    .pretrained("re_drugprot_clinical", "en", 'clinical/models')\
    .setInputCols(["embeddings", "pos_tags", "ner_chunks", "dependencies"])\
    .setOutputCol("relations")\
    .setMaxSyntacticDistance(4)\
    .setPredictionThreshold(0.9)\
    .setRelationPairs(['CHEMICAL-GENE'])
...

sample_text = "Lipid specific activation of the murine P4-ATPase Atp8a1 (ATPase II). The asymmetric transbilayer distribution of phosphatidylserine (PS) in the mammalian plasma membrane and secretory vesicles is maintained, in part, by an ATP-dependent transporter. This aminophospholipid "flippase" selectively transports PS to the cytosolic leaflet of the bilayer and is sensitive to vanadate, Ca(2+), and modification by sulfhydryl reagents. Although the flippase has not been positively identified, a subfamily of P-type ATPases has been proposed to function as transporters of amphipaths, including PS and other phospholipids. A candidate PS flippase ATP8A1 (ATPase II), originally isolated from bovine secretory vesicles, is a member of this subfamily based on sequence homology to the founding member of the subfamily, the yeast protein Drs2, which has been linked to ribosomal assembly, the formation of Golgi-coated vesicles, and the maintenance of PS asymmetry."
result = re_model.transform(spark.createDataFrame([[sample_text]]).toDF("text"))
```

*Results* :

```bash
+---------+--------+-------------+-----------+--------------------+-------+-------------+-----------+--------------------+----------+
| relation| entity1|entity1_begin|entity1_end|              chunk1|entity2|entity2_begin|entity2_end|              chunk2|confidence|
+---------+--------+-------------+-----------+--------------------+-------+-------------+-----------+--------------------+----------+
|SUBSTRATE|CHEMICAL|          308|        310|                  PS|   GENE|          275|        283|            flippase|  0.998399|
|ACTIVATOR|CHEMICAL|         1563|       1578|     sn-1,2-glycerol|   GENE|         1479|       1509|plasma membrane P...|  0.999304|
|ACTIVATOR|CHEMICAL|         1563|       1578|     sn-1,2-glycerol|   GENE|         1511|       1517|              Atp8a1|  0.979057|
+---------+--------+-------------+-----------+--------------------+-------+-------------+-----------+--------------------+----------+
```

#### New LOINC, SNOMED, UMLS and Clinical Abbreviation Entity Resolver Models

We have five new Sentence Entity Resolver models.

+ `sbiobertresolve_clinical_abbreviation_acronym` : This model maps clinical abbreviations and acronyms to their meanings using `sbiobert_base_cased_mli` Sentence Bert Embeddings. It is a part of ongoing research we have been running in-house, and trained with a limited dataset. We’ll be updating & enriching the model in the upcoming releases.

*Example* :

```bash
...
abbr_resolver = SentenceEntityResolverModel.pretraind("sbiobertresolve_clinical_abbreviation_acronym", "en", "clinical/models")\
  .setInputCols(["merged_chunk", "sentence_embeddings"])\
  .setOutputCol("abbr_meaning")\
  .setDistanceFunction("EUCLIDEAN")
...

sample_text = "HISTORY OF PRESENT ILLNESS: The patient three weeks ago was seen at another clinic for upper respiratory infection-type symptoms. She was diagnosed with a viral infection and had used OTC medications including Tylenol, Sudafed, and Nyquil."
results = abb_model.transform(spark.createDataFrame([[sample_text]]).toDF('text'))
```

*Results* :

```bash
|   sent_id | ner_chunk   | entity   | abbr_meaning     | all_k_results                                                                      | all_k_resolutions          |
|----------:|:------------|:---------|:-----------------|:-----------------------------------------------------------------------------------|:---------------------------|
|         0 | OTC         | ABBR     | over the counter | ['over the counter', 'ornithine transcarbamoylase', 'enteric-coated', 'thyroxine'] | ['OTC', 'OTC', 'EC', 'T4'] |

```

+ `sbiobertresolve_umls_drug_substance` : This model maps clinical entities to UMLS CUI codes. It is trained on `2021AB` UMLS dataset. The complete dataset has 127 different categories, and this model is trained on the `Clinical Drug`, `Pharmacologic Substance`, `Antibiotic`, `Hazardous or Poisonous Substance` categories using `sbiobert_base_cased_mli` embeddings.

*Example* :

```bash
...
umls_resolver = SentenceEntityResolverModel.pretrained("sbiobertresolve_umls_drug_substance","en", "clinical/models")\
  .setInputCols(["ner_chunk", "sbert_embeddings"])\
  .setOutputCol("resolution")\
  .setDistanceFunction("EUCLIDEAN")
...

results = model.fullAnnotate(['Dilaudid', 'Hydromorphone', 'Exalgo', 'Palladone', 'Hydrogen peroxide 30 mg', 'Neosporin Cream', 'Magnesium hydroxide 100mg/1ml', 'Metformin 1000 mg'])
```

*Results* :

```bash
|    | chunk                         | code     | code_description           | all_k_code_desc                                              | all_k_codes                                                                                                                                                                             |
|---:|:------------------------------|:---------|:---------------------------|:-------------------------------------------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|  0 | Dilaudid                      | C0728755 | dilaudid                   | ['C0728755', 'C0719907', 'C1448344', 'C0305924', 'C1569295'] | ['dilaudid', 'Dilaudid HP', 'Disthelm', 'Dilaudid Injection', 'Distaph']                                                                                                                |
|  1 | Hydromorphone                 | C0012306 | HYDROMORPHONE              | ['C0012306', 'C0700533', 'C1646274', 'C1170495', 'C0498841'] | ['HYDROMORPHONE', 'Hydromorphone HCl', 'Phl-HYDROmorphone', 'PMS HYDROmorphone', 'Hydromorphone injection']                                                                             |
|  2 | Exalgo                        | C2746500 | Exalgo                     | ['C2746500', 'C0604734', 'C1707065', 'C0070591', 'C3660437'] | ['Exalgo', 'exaltolide', 'Exelgyn', 'Extacol', 'exserohilone']                                                                                                                          |
|  3 | Palladone                     | C0730726 | palladone                  | ['C0730726', 'C0594402', 'C1655349', 'C0069952', 'C2742475'] | ['palladone', 'Palladone-SR', 'Palladone IR', 'palladiazo', 'palladia']                                                                                                                 |
|  4 | Hydrogen peroxide 30 mg       | C1126248 | hydrogen peroxide 30 MG/ML | ['C1126248', 'C0304655', 'C1605252', 'C0304656', 'C1154260'] | ['hydrogen peroxide 30 MG/ML', 'Hydrogen peroxide solution 30%', 'hydrogen peroxide 30 MG/ML [Proxacol]', 'Hydrogen peroxide 30 mg/mL cutaneous solution', 'benzoyl peroxide 30 MG/ML'] |
|  5 | Neosporin Cream               | C0132149 | Neosporin Cream            | ['C0132149', 'C0306959', 'C4722788', 'C0704071', 'C0698988'] | ['Neosporin Cream', 'Neosporin Ointment', 'Neomycin Sulfate Cream', 'Neosporin Topical Ointment', 'Naseptin cream']                                                                     |
|  6 | Magnesium hydroxide 100mg/1ml | C1134402 | magnesium hydroxide 100 MG | ['C1134402', 'C1126785', 'C4317023', 'C4051486', 'C4047137'] | ['magnesium hydroxide 100 MG', 'magnesium hydroxide 100 MG/ML', 'Magnesium sulphate 100mg/mL injection', 'magnesium sulfate 100 MG', 'magnesium sulfate 100 MG/ML']                     |
|  7 | Metformin 1000 mg             | C0987664 | metformin 1000 MG          | ['C0987664', 'C2719784', 'C0978482', 'C2719786', 'C4282269'] | ['metformin 1000 MG', 'metFORMIN hydrochloride 1000 MG', 'METFORMIN HCL 1000MG TAB', 'metFORMIN hydrochloride 1000 MG [Fortamet]', 'METFORMIN HCL 1000MG SA TAB']                       |

```

+ `sbiobertresolve_loinc_cased` : This model maps extracted clinical NER entities to LOINC codes using `sbiobert_base_cased_mli` Sentence Bert Embeddings. It is trained with augmented **cased** concept names since sbiobert model is cased.

*Example* :

```bash
...
loinc_resolver = SentenceEntityResolverModel.pretrained("sbiobertresolve_loinc_cased", "en", "clinical/models")\
  .setInputCols(["ner_chunk", "sbert_embeddings"])\
  .setOutputCol("resolution")\
  .setDistanceFunction("EUCLIDEAN")
...

sample_text= """The patient is a 22-year-old female with a history of obesity. She has a BMI of 33.5 kg/m2, aspartate aminotransferase 64, and alanine aminotransferase 126. Her hemoglobin is 8.2%."""
result = model.transform(spark.createDataFrame([[sample_text]], ["text"]))
```

*Results* :

```bash
+-------------------------------------+------+-----------+----------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
|                            ner_chunk|entity| resolution|                                           all_codes|                                                                                                                                                                                             resolutions|
+-------------------------------------+------+-----------+----------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
|                                  BMI|  Test|  LP35925-4|[LP35925-4, 59574-4, BDYCRC, 73964-9, 59574-4,...   |[Body mass index (BMI), Body mass index, Body circumference, Body muscle mass, Body mass index (BMI) [Percentile], ...                                                                                  |
|           aspartate aminotransferase|  Test|    14409-7|[14409-7, 1916-6, 16325-3, 16324-6, 43822-6, 308... |[Aspartate aminotransferase, Aspartate aminotransferase/Alanine aminotransferase, Alanine aminotransferase/Aspartate aminotransferase, Alanine aminotransferase, Aspartate aminotransferase [Prese...   |
|             alanine aminotransferase|  Test|    16324-6|[16324-6, 16325-3, 14409-7, 1916-6, 59245-1, 30...  |[Alanine aminotransferase, Alanine aminotransferase/Aspartate aminotransferase, Aspartate aminotransferase, Aspartate aminotransferase/Alanine aminotransferase, Alanine glyoxylate aminotransfer,...   |
|                           hemoglobin|  Test|    14775-1|[14775-1, 16931-8, 12710-0, 29220-1, 15082-1, 72... |[Hemoglobin, Hematocrit/Hemoglobin, Hemoglobin pattern, Haptoglobin, Methemoglobin, Oxyhemoglobin, Hemoglobin test status, Verdohemoglobin, Hemoglobin A, Hemoglobin distribution width, Myoglobin,...  |
+-------------------------------------+------+-----------+----------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
```

+ `sbluebertresolve_loinc_uncased` : This model maps extracted clinical NER entities to LOINC codes using `sbluebert_base_uncased_mli` Sentence Bert Embeddings. It trained on the augmented version of the **uncased (lowercased)** dataset which is used in previous LOINC resolver models.

*Example* :

```bash
...
loinc_resolver = SentenceEntityResolverModel.pretrained("sbluebertresolve_loinc_uncased", "en", "clinical/models")\
  .setInputCols(["jsl_ner_chunk", "sbert_embeddings"])\
  .setOutputCol("resolution")\
  .setDistanceFunction("EUCLIDEAN")
...

sample_text= """The patient is a 22-year-old female with a history of obesity. She has a BMI of 33.5 kg/m2, aspartate aminotransferase 64, and alanine aminotransferase 126. Her hgba1c is 8.2%."""
result = model.transform(spark.createDataFrame([[sample_text]], ["text"]))
```
*Results* :

```bash
+-------------------------------------+------+-----------+----------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
|                            ner_chunk|entity| resolution|                                           all_codes|                                                                                                                                                                                             resolutions|
+-------------------------------------+------+-----------+----------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
|                                  BMI|  Test|    39156-5|[39156-5, LP35925-4, BDYCRC, 73964-9, 59574-4,...]  |[Body mass index, Body mass index (BMI), Body circumference, Body muscle mass, Body mass index (BMI) [Percentile], ...]                                                                                 |
|           aspartate aminotransferase|  Test|    14409-7|['14409-7', '16325-3', '1916-6', '16324-6',...]     |['Aspartate aminotransferase', 'Alanine aminotransferase/Aspartate aminotransferase', 'Aspartate aminotransferase/Alanine aminotransferase', 'Alanine aminotransferase', ...]                           |
|             alanine aminotransferase|  Test|    16324-6|['16324-6', '1916-6', '16325-3', '59245-1',...]     |['Alanine aminotransferase', 'Aspartate aminotransferase/Alanine aminotransferase', 'Alanine aminotransferase/Aspartate aminotransferase', 'Alanine glyoxylate aminotransferase',...]                   |
|                               hgba1c|  Test|    41995-2|['41995-2', 'LP35944-5', 'LP19717-5', '43150-2',...]|['Hemoglobin A1c', 'HbA1c measurement device', 'HBA1 gene', 'HbA1c measurement device panel', ...]                                                                                                      |
+-------------------------------------+------+-----------+------------------------------------------------------------------------------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
```

+ `sbiobertresolve_snomed_drug` : This model maps detected drug entities to SNOMED codes using `sbiobert_base_cased_mli` Sentence Bert Embeddings.

*Example* :

```bash
...
snomed_resolver = SentenceEntityResolverModel.pretrained("sbiobertresolve_snomed_drug", "en", "clinical/models") \
    .setInputCols(["ner_chunk", "sentence_embeddings"]) \
    .setOutputCol("snomed_code")\
    .setDistanceFunction("EUCLIDEAN")
...

sample_text = "She is given Fragmin 5000 units subcutaneously daily, OxyContin 30 mg p.o. q.12 h., folic acid 1 mg daily, levothyroxine 0.1 mg p.o. daily, Avandia 4 mg daily, aspirin 81 mg daily, Neurontin 400 mg p.o. t.i.d., magnesium citrate 1 bottle p.o. p.r.n., sliding scale coverage insulin."
results = model.transform(spark.createDataFrame([[sample_text]]).toDF('text'))
```

*Results* :

```bash
+-----------------+------+-----------------+-----------------+------------------------------------------------------------+------------------------------------------------------------+
|        ner_chunk|entity|      snomed_code|    resolved_text|                                               all_k_results|                                           all_k_resolutions|
+-----------------+------+-----------------+-----------------+------------------------------------------------------------+------------------------------------------------------------+
|          Fragmin|  DRUG| 9487801000001106|          Fragmin|9487801000001106:::130752006:::28999000:::953500100000110...|Fragmin:::Fragilysin:::Fusarin:::Femulen:::Fumonisin:::Fr...|
|        OxyContin|  DRUG| 9296001000001100|        OxyCONTIN|9296001000001100:::373470001:::230091000001108:::55452001...|OxyCONTIN:::Oxychlorosene:::Oxyargin:::oxyCODONE:::Oxymor...|
|       folic acid|  DRUG|         63718003|       Folic acid|63718003:::6247001:::226316008:::432165000:::438451000124...|Folic acid:::Folic acid-containing product:::Folic acid s...|
|    levothyroxine|  DRUG|10071011000001106|    Levothyroxine|10071011000001106:::710809001:::768532006:::126202002:::7...|Levothyroxine:::Levothyroxine (substance):::Levothyroxine...|
|          Avandia|  DRUG| 9217601000001109|          avandia|9217601000001109:::9217501000001105:::12226401000001108::...|avandia:::avandamet:::Anatera:::Intanza:::Avamys:::Aragam...|
|          aspirin|  DRUG|        387458008|          Aspirin|387458008:::7947003:::5145711000001107:::426365001:::4125...|Aspirin:::Aspirin-containing product:::Aspirin powder:::A...|
|        Neurontin|  DRUG| 9461401000001102|        neurontin|9461401000001102:::130694004:::86822004:::952840100000110...|neurontin:::Neurolysin:::Neurine (substance):::Nebilet:::...|
|magnesium citrate|  DRUG|         12495006|Magnesium citrate|12495006:::387401007:::21691008:::15531411000001106:::408...|Magnesium citrate:::Magnesium carbonate:::Magnesium trisi...|
|          insulin|  DRUG|         67866001|          Insulin|67866001:::325072002:::414515005:::39487003:::411530000::...|Insulin:::Insulin aspart:::Insulin detemir:::Insulin-cont...|
+-----------------+------+-----------------+-----------------+------------------------------------------------------------+------------------------------------------------------------+

```

#### New ICD10 to ICD9 Code Mapping Pretrained Pipeline

We are releasing new `icd10_icd9_mapping` pretrained pipeline. This pretrained pipeline maps ICD10 codes to ICD9 codes without using any text data. You’ll just feed a comma or white space-delimited ICD10 codes and it will return the corresponding ICD9 codes as a list.

*Example* :

```bash
from sparknlp.pretrained import PretrainedPipeline
pipeline = PretrainedPipeline("icd10_icd9_mapping", "en", "clinical/models")
pipeline.annotate('E669 R630 J988')
```
*Results* :

```bash
{'document': ['E669 R630 J988'],
'icd10': ['E669', 'R630', 'J988'],
'icd9': ['27800', '7830', '5198']}

Code Descriptions:

|    | ICD10                | Details                               |
|---:|:---------------------|:--------------------------------------|
|  0 | E669                 | Obesity                               |
|  1 | R630                 | Anorexia                              |
|  2 | J988                 | Other specified respiratory disorders |

|    | ICD9                 | Details                               |
|---:|:---------------------|:--------------------------------------|
|  0 | 27800                | Obesity                               |
|  1 | 7830                 | Anorexia                              |
|  2 | 5198                 | Other diseases of respiratory system  |

```

#### New Clinical Sentence Embedding Models

We have two new clinical Sentence Embedding models.

+ `sbiobert_jsl_rxnorm_cased` : This model maps sentences & documents to a 768 dimensional dense vector space by using average pooling on top of BioBert model. It's also fine-tuned on RxNorm dataset to help generalization over medication-related datasets.

*Example* :

```bash
...
sentence_embeddings = BertSentenceEmbeddings.pretrained("sbiobert_jsl_rxnorm_cased", "en", "clinical/models")\
  .setInputCols(["sentence"])\
  .setOutputCol("sbioert_embeddings")
...
```

+ `sbert_jsl_medium_rxnorm_uncased` : This model maps sentences & documents to a 512-dimensional dense vector space by using average pooling on top of BERT model. It's also fine-tuned on the RxNorm dataset to help generalization over medication-related datasets.

*Example* :

```bash
...
sentence_embeddings = BertSentenceEmbeddings.pretrained("sbert_jsl_medium_rxnorm_uncased", "en", "clinical/models")\
  .setInputCols(["sentence"])\
  .setOutputCol("sbert_embeddings")
...
```

#### Printing Validation and Test Logs in MedicalNerApproach and AssertionDLApproach

Now we can check validation loss and test loss for each epoch in the logs created during trainings of MedicalNerApproach and AssertionDLApproach.


```bash
Epoch 15/15 started, lr: 9.345794E-4, dataset size: 1330


Epoch 15/15 - 56.65s - loss: 37.58828 - avg training loss: 1.7899181 - batches: 21
Quality on validation dataset (20.0%), validation examples = 266
time to finish evaluation: 8.11s
Total validation loss: 15.1930	Avg validation loss: 2.5322
label	 tp	 fp	 fn	 prec	 rec	 f1
I-Disease	 707	 72	 121	 0.9075738	 0.8538647	 0.8799004
B-Disease	 657	 81	 60	 0.8902439	 0.916318	 0.90309274
tp: 1364 fp: 153 fn: 181 labels: 2
Macro-average	 prec: 0.89890885, rec: 0.88509136, f1: 0.8919466
Micro-average	 prec: 0.89914304, rec: 0.8828479, f1: 0.89092094
Quality on test dataset:
time to finish evaluation: 9.11s
Total test loss: 17.7705	Avg test loss: 1.6155
label	 tp	 fp	 fn	 prec	 rec	 f1
I-Disease	 663	 113	 126	 0.85438144	 0.8403042	 0.8472843
B-Disease	 631	 122	 77	 0.8379814	 0.8912429	 0.86379194
tp: 1294 fp: 235 fn: 203 labels: 2
Macro-average	 prec: 0.8461814, rec: 0.86577356, f1: 0.85586536
Micro-average	 prec: 0.8463048, rec: 0.86439544, f1: 0.8552544
```

#### Filter Only the Regex Entities Feature in Deidentification Annotator

The `setBlackList()` method will be able to filter just the detected Regex Entities. Before this change we filtered the chunks and the regex entities.

#### Add `.setMaskingPolicy` Parameter in Deidentification Annotator

Now we can have three modes to mask the entities in the Deidentification annotator.
You can select the modes using the `.setMaskingPolicy("entity_labels")`.

The methods are the followings:
  1. "entity_labels": Mask with the entity type of that chunk. (default)
  2. "same_length_chars": Mask the deid entities with same length of asterix (`*`) with brackets (`[`,`]`) on both end.
  3. "fixed_length_chars": Mask the deid entities with a fixed length of asterix (`*`). The length is setting up using the `setFixedMaskLength(4)` method.


Given the following sentence `John Snow is a good guy.` the result will be:

  1. "entity_labels": `<NAME> is a good guy.`
  2. "same_length_chars": `[*******] is a good guy.`
  3. "fixed_length_chars": `**** is a good guy.`

*Example*
```bash
Masked with entity labels
------------------------------
DATE <DATE>, <DOCTOR>,  The driver's license <DLN>.

Masked with chars
------------------------------
DATE [**********], [***********],  The driver's license [*********].

Masked with fixed length chars
------------------------------
DATE ****, ****,  The driver's license ****.

Obfuscated
------------------------------
DATE 07-04-1981, Dr Vivian Irving,  The driver's license K272344712994.
```

#### Add `.cache_folder` Parameter in `UpdateModels.updateCacheModels()`

This parameter lets user to define custom local paths for the folder on which pretrained models are saved (rather than using default cached_pretrained folder).

This cache_folder must be a path ("hdfs:..","file:...").

```bash
UpdateModels.updateCacheModels("file:/home/jsl/cache_pretrained_2")
```

```bash
UpdateModels.updateModels("12/01/2021","file:/home/jsl/cache_pretrained_2")
```

The cache folder used by default is the folder loaded in the spark configuration ` spark.jsl.settings.pretrained.cache_folder`.The default value for that property is `~/cache_pretrained`


#### S3 Access Credentials No Longer Shipped Along Licenses

S3 access credentials are no longer being shipped with licenses. Going forward, we'll use temporal S3 access credentials which will be periodically refreshed. All this will happen automatically and will be transparent to the user.
Still, for those users who would need to perform manual tasks involving access to S3, there's a mechanism to get access to the set of credentials being used by the library at any given time.

```bash
from sparknlp_jsl import get_credentials
get_credentials(spark)
```

#### Enhanced Security for the Library and log4shell Update

On top of periodical security checks on the library code, 3rd party dependencies were analyzed, and some dependencies reported as containing vulnerabilities were replaced by more secure options.
Also, the library was analyzed in the context of the recently discovered threat(CVE-2021-45105) on the log4j library. Spark NLP for Healthcare does not depend on the log4j library by itself, but the library gets loaded through some of its dependencies.
It's worth noting that the version of log4j dependency that will be in the classpath when running Spark NLP for Healthcare is 1.x, which would make the system vulnerable to CVE-2021-4104, instead of CVE-2021-45105. CVE-2021-4104 is related to the JMSAppender.
Spark NLP for Healthcare does not provide any log4j configuration, so it's up to the user to follow the recommendation of avoiding the use of the JMSAppender.


#### New Peer-Reviewed Conference Paper on Clinical Relation Extraction

We publish a new peer-reviewed conference paper titled [Deeper Clinical Document Understanding Using Relation Extraction](https://arxiv.org/pdf/2112.13259.pdf) explaining the applications of Relation Extraction in a text mining framework comprising of Named Entity Recognition (NER) and Relation Extraction (RE) models. The paper is accepted to SDU (Scientific Document Understanding) workshop at AAAI-2022 conference and claims new SOTA scores on 5 out of 7 Biomedical & Clinical Relation Extraction (RE) tasks.

|Dataset|FCNN|BioBERT|Curr-SOTA|
|-|-|-|-|
|i2b2-Temporal|68.7|**73.6**|72.41|
|i2b2-Clinical|60.4|**69.1**|67.97|
|DDI|69.2|72.1|**84.1**|
|CPI|65.8|74.3|**88.9**|
|PGR|81.2|**87.9**|79.4|
|ADE Corpus|89.2|**90.0**|83.7|
|Posology|87.8|**96.7**|96.1|

*Macro-averaged F1 scores of both RE models on public datasets. FCNN refers to the Speed-Optimized FCNN architecture, while BioBERT refers to the AccuracyOptimized BioBERT architecture. The SOTA metrics are obtained from (Guan et al. 2020), (Ningthoujam et al. 2019), (Asada, Miwa, and Sasaki 2020), (Phan et al. 2021), (Sousa
and Couto 2020), (Crone 2020), and (Yang et al. 2021) respectively.*


#### New Peer-Reviewed Conference Paper on Adverse Drug Events Extraction

We publish a new peer-reviewed conference paper titled [Mining Adverse Drug Reactions from Unstructured Mediums at Scale](https://arxiv.org/pdf/2201.01405.pdf) proposing an end-to-end Adverse Drug Event mining solution using Classification, NER, and Relation Extraction Models. The paper is accepted to W3PHIAI (INTERNATIONAL WORKSHOP ON HEALTH INTELLIGENCE) workshop at AAAI-2022 conference, and claims new SOTA scores on 1 benchmark dataset for Classification, 3 benchmark datasets for NER, and 1 benchmark dataset for Relation Extraction.

|Task | Dataset | Spark NLP | Curr-SOTA |
|-|-|-|-|
|Classification|ADE|85.96|**87.0**|
|Classification|CADEC|**86.69**|81.5|
|Entity Recognition|ADE|**91.75**|91.3|
|Entity Recognition|CADEC|**78.36**|71.9|
|Entity Recognition|SMM4H|**76.73**|67.81|
|Relation Extraction|ADE|**90.0**|83.7|

*All F1 scores are Macro-averaged*

#### New and Updated Notebooks

+ We have two new Notebooks:
  - [Chunk Sentence Splitter Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/18.Chunk_Sentence_Splitter.ipynb) that involves usage of `ChunkSentenceSplitter` annotator.
  - [Clinical Relation Extraction Spark NLP Paper Reproduce Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/10.3.Clinical_RE_SparkNLP_Paper_Reproduce.ipynb) that can be used for reproducing the results in  [Deeper Clinical Document Understanding Using Relation Extraction](https://arxiv.org/pdf/2112.13259.pdf) paper.

+ We have updated our existing notebooks by adding new features and functionalities. Here are updated notebooks:
  - [Clinical Named Entity Recognition Model](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/1.Clinical_Named_Entity_Recognition_Model.ipynb)
  - [Clinical Entity Resolver Models](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/3.Clinical_Entity_Resolvers.ipynb)
  - [Clinical DeIdentification](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/4.Clinical_DeIdentification.ipynb)
  - [Clinical NER Chunk Merger](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/7.Clinical_NER_Chunk_Merger.ipynb)
  - [Pretrained Clinical Pipelines](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/11.Pretrained_Clinical_Pipelines.ipynb)
  - [Healthcare Code Mapping](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/11.1.Healthcare_Code_Mapping.ipynb)
  - [Improved Entity Resolvers in Spark NLP with sBert](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/24.Improved_Entity_Resolvers_in_SparkNLP_with_sBert.ipynb)


**To see more, please check : [Spark NLP Healthcare Workshop Repo](https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/tutorials/Certification_Trainings/Healthcare)**



## 3.3.4
We are glad to announce that Spark NLP Healthcare 3.3.4 has been released!



#### Highlights

+ New Clinical NER Models
+ New NER Model Finder Pretrained Pipeline
+ New Relation Extraction Model
+ New LOINC, MeSH, NDC and SNOMED Entity Resolver Models
+ Updated RxNorm Sentence Entity Resolver Model
+ New Shift Days Feature in StructuredDeid Deidentification Module
+ New Multiple Chunks Merge Ability in ChunkMergeApproach
+ New setBlackList Feature in ChunkMergeApproach
+ New setBlackList Feature in NerConverterInternal
+ New setLabelCasing Feature in MedicalNerModel
+ New Update Models Functionality
+ New and Updated Notebooks

#### New Clinical NER Models

We have three new clinical NER models.

+ `ner_deid_subentity_augmented_i2b2` : This model annotates text to find protected health information(PHI) that may need to be removed. It is trained with 2014 i2b2 dataset (no augmentation applied) and can detect `MEDICALRECORD`, `ORGANIZATION`, `DOCTOR`, `USERNAME`, `PROFESSION`, `HEALTHPLAN`, `URL`, `CITY`, `DATE`, `LOCATION-OTHER`, `STATE`, `PATIENT`, `DEVICE`, `COUNTRY`, `ZIP`, `PHONE`, `HOSPITAL`, `EMAIL`, `IDNUM`, `SREET`, `BIOID`, `FAX`, `AGE` entities.

*Example* :

```bash
...
deid_ner = MedicalNerModel.pretrained("ner_deid_subentity_augmented_i2b2", "en", "clinical/models") \
      .setInputCols(["sentence", "token", "embeddings"]) \
      .setOutputCol("ner")
...

results = ner_model.transform(spark.createDataFrame([["A. Record date : 2093-01-13, David Hale, M.D., Name : Hendrickson, Ora MR. # 7194334 Date : 01/13/93 PCP : Oliveira, 25 years old, Record date : 1-11-2000. Cocke County Baptist Hospital. 0295 Keats Street. Phone +1 (302) 786-5227. Patient's complaints first surfaced when he started working for Brothers Coal-Mine."]], ["text"]))
```

*Results* :

```bash
+-----------------------------+-------------+
|chunk                        |ner_label    |
+-----------------------------+-------------+
|2093-01-13                   |DATE         |
|David Hale                   |DOCTOR       |
|Hendrickson, Ora             |PATIENT      |
|7194334                      |MEDICALRECORD|
|01/13/93                     |DATE         |
|Oliveira                     |DOCTOR       |
|25                           |AGE          |
|1-11-2000                    |DATE         |
|Cocke County Baptist Hospital|HOSPITAL     |
|0295 Keats Street            |STREET       |
|(302) 786-5227               |PHONE        |
|Brothers Coal-Mine Corp      |ORGANIZATION |
+-----------------------------+-------------+
```

+ `ner_biomarker` : This model is trained to extract biomarkers, therapies, oncological, and other general concepts from text. Following are the entities it can detect: `Oncogenes`, `Tumor_Finding`, `UnspecificTherapy`, `Ethnicity`, `Age`, `ResponseToTreatment`, `Biomarker`, `HormonalTherapy`, `Staging`, `Drug`, `CancerDx`, `Radiotherapy`, `CancerSurgery`, `TargetedTherapy`, `PerformanceStatus`, `CancerModifier`, `Radiological_Test_Result`, `Biomarker_Measurement`, `Metastasis`, `Radiological_Test`, `Chemotherapy`, `Test`, `Dosage`, `Test_Result`, `Immunotherapy`, `Date`, `Gender`, `Prognostic_Biomarkers`, `Duration`, `Predictive_Biomarkers`

*Example* :

```bash
...
clinical_ner = MedicalNerModel.pretrained("ner_biomarker", "en", "clinical/models") \
  .setInputCols(["sentence", "token", "embeddings"]) \
  .setOutputCol("ner")
...

results = ner_model.transform(spark.createDataFrame([["Here , we report the first case of an intraductal tubulopapillary neoplasm of the pancreas with clear cell morphology . Immunohistochemistry revealed positivity for Pan-CK , CK7 , CK8/18 , MUC1 , MUC6 , carbonic anhydrase IX , CD10 , EMA , β-catenin and e-cadherin ."]], ["text"]))
```

*Results* :

```bash
|    | ner_chunk                | entity                |   confidence |
|---:|:-------------------------|:----------------------|-------------:|
|  0 | intraductal              | CancerModifier        |     0.9934   |
|  1 | tubulopapillary          | CancerModifier        |     0.6403   |
|  2 | neoplasm of the pancreas | CancerDx              |     0.758825 |
|  3 | clear cell               | CancerModifier        |     0.9633   |
|  4 | Immunohistochemistry     | Test                  |     0.9534   |
|  5 | positivity               | Biomarker_Measurement |     0.8795   |
|  6 | Pan-CK                   | Biomarker             |     0.9975   |
|  7 | CK7                      | Biomarker             |     0.9975   |
|  8 | CK8/18                   | Biomarker             |     0.9987   |
|  9 | MUC1                     | Biomarker             |     0.9967   |
| 10 | MUC6                     | Biomarker             |     0.9972   |
| 11 | carbonic anhydrase IX    | Biomarker             |     0.937567 |
| 12 | CD10                     | Biomarker             |     0.9974   |
| 13 | EMA                      | Biomarker             |     0.9899   |
| 14 | β-catenin                | Biomarker             |     0.8059   |
| 15 | e-cadherin               | Biomarker             |     0.9806   |
```

+ `ner_nihss` : NER model that can identify entities according to NIHSS guidelines for clinical stroke assessment to evaluate neurological status in acute stroke patients. Here are the labels it can detect : `11_ExtinctionInattention`, `6b_RightLeg`, `1c_LOCCommands`, `10_Dysarthria`, `NIHSS`, `5_Motor`, `8_Sensory`, `4_FacialPalsy`, `6_Motor`, `2_BestGaze`, `Measurement`, `6a_LeftLeg`, `5b_RightArm`, `5a_LeftArm`, `1b_LOCQuestions`, `3_Visual`, `9_BestLanguage`, `7_LimbAtaxia`, `1a_LOC` .

*Example* :

```bash
...
clinical_ner = MedicalNerModel.pretrained("ner_nihss", "en", "clinical/models") \
  .setInputCols(["sentence", "token", "embeddings"]) \
  .setOutputCol("ner")
...

results = ner_model.transform(spark.createDataFrame([["Abdomen , soft , nontender . NIH stroke scale on presentation was 23 to 24 for , one for consciousness , two for month and year and two for eye / grip , one to two for gaze , two for face , eight for motor , one for limited ataxia , one to two for sensory , three for best language and two for attention . On the neurologic examination the patient was intermittently"]], ["text"]))
```  

*Results* :

```bash
|    | chunk              | entity                   |
|---:|:-------------------|:-------------------------|
|  0 | NIH stroke scale   | NIHSS                    |
|  1 | 23 to 24           | Measurement              |
|  2 | one                | Measurement              |
|  3 | consciousness      | 1a_LOC                   |
|  4 | two                | Measurement              |
|  5 | month and year and | 1b_LOCQuestions          |
|  6 | two                | Measurement              |
|  7 | eye / grip         | 1c_LOCCommands           |
|  8 | one to             | Measurement              |
|  9 | two                | Measurement              |
| 10 | gaze               | 2_BestGaze               |
| 11 | two                | Measurement              |
| 12 | face               | 4_FacialPalsy            |
| 13 | eight              | Measurement              |
| 14 | one                | Measurement              |
| 15 | limited            | 7_LimbAtaxia             |
| 16 | ataxia             | 7_LimbAtaxia             |
| 17 | one to two         | Measurement              |
| 18 | sensory            | 8_Sensory                |
| 19 | three              | Measurement              |
| 20 | best language      | 9_BestLanguage           |
| 21 | two                | Measurement              |
| 22 | attention          | 11_ExtinctionInattention |
```

#### New NER Model Finder Pretrained Pipeline

We are releasing new `ner_model_finder` pretrained pipeline trained with bert embeddings that can be used to find the most appropriate NER model given the entity name.

*Example* :

```bash
from sparknlp.pretrained import PretrainedPipeline
finder_pipeline = PretrainedPipeline("ner_model_finder", "en", "clinical/models")

result = finder_pipeline.fullAnnotate("psychology")
```

*Results* :

|entity|top models|all models|resolutions|
|-|-|-|-|
|psychology|['ner_medmentions_coarse', 'jsl_rd_ner_wip_greedy_clinical', 'ner_jsl_enriched', 'ner_jsl', 'jsl_ner_wip_modifier_clinical', 'ner_jsl_greedy']  |['ner_medmentions_coarse', 'jsl_rd_ner_wip_greedy_clinical', 'ner_jsl_enriched', 'ner_jsl', 'jsl_ner_wip_modifier_clinical', 'ner_jsl_greedy']:::['jsl_rd_ner_wip_greedy_clinical', 'ner_jsl_enriched', 'ner_jsl_slim', 'ner_jsl', 'jsl_ner_wip_modifier_clinical,...|psychological condition:::clinical department::: ... |

#### New Relation Extraction Model

We are releasing new `redl_nihss_biobert ` relation extraction model that can relate scale items and their measurements according to NIHSS guidelines.

*Example* :

```bash
...
re_model = RelationExtractionDLModel()\
    .pretrained('redl_nihss_biobert', 'en', "clinical/models") \
    .setPredictionThreshold(0.5)\
    .setInputCols(["re_ner_chunks", "sentences"]) \
    .setOutputCol("relations")
...

sample_text = "There , her initial NIHSS score was 4 , as recorded by the ED physicians . This included 2 for weakness in her left leg and 2 for what they felt was subtle ataxia in her left arm and leg ."
result = re_model.transform(spark.createDataFrame([[sample_text]]).toDF("text"))
```

*Results* :

```bash
| chunk1                                | entity1      |   entity1_begin |   entity1_end | entity2     |   chunk2 |   entity2_begin |   entity2_end | relation   |
|:--------------------------------------|:-------------|----------------:|--------------:|:------------|---------:|----------------:|--------------:|:-----------|
| initial NIHSS score                   | NIHSS        |              12 |            30 | Measurement |        4 |              36 |            36 | Has_Value  |
| left leg                              | 6a_LeftLeg   |             111 |           118 | Measurement |        2 |              89 |            89 | Has_Value  |
| subtle ataxia in her left arm and leg | 7_LimbAtaxia |             149 |           185 | Measurement |        2 |             124 |           124 | Has_Value  |
| left leg                              | 6a_LeftLeg   |             111 |           118 | Measurement |        4 |              36 |            36 | 0          |
| initial NIHSS score                   | NIHSS        |              12 |            30 | Measurement |        2 |             124 |           124 | 0          |
| subtle ataxia in her left arm and leg | 7_LimbAtaxia |             149 |           185 | Measurement |        4 |              36 |            36 | 0          |
| subtle ataxia in her left arm and leg | 7_LimbAtaxia |             149 |           185 | Measurement |        2 |              89 |            89 | 0          |
```

#### New LOINC, MeSH, NDC and SNOMED Entity Resolver Models

We have four new Sentence Entity Resolver Models.

+ `sbiobertresolve_mesh` : This model maps clinical entities to Medical Subject Heading (MeSH) codes using `sbiobert_base_cased_mli` Sentence Bert Embeddings.

*Example* :

```bash
...
mesh_resolver = SentenceEntityResolverModel.pretrained("sbiobertresolve_mesh", "en", "clinical/models") \
      .setInputCols(["ner_chunk", "sentence_embeddings"]) \
      .setOutputCol("mesh_code")\
      .setDistanceFunction("EUCLIDEAN")\
      .setCaseSensitive(False)

...

sample_text = """She was admitted to the hospital with chest pain and found to have bilateral pleural effusion, the right greater than the left. We reviewed the pathology obtained from the pericardectomy in March 2006, which was diagnostic of mesothelioma. At this time, chest tube placement for drainage of the fluid occurred and thoracoscopy with fluid biopsies, which were performed, which revealed malignant mesothelioma."""
result = resolver_model.transform(spark.createDataFrame([[sample_text]]).toDF("text"))
```

*Results* :

```bash
+--------------------------+---------+----------+----------------------------------------------------------------------------------------------------+----------------------------------------------------------------------------------------------------+----------------------------------------------------------------------------------------------------+
|                 ner_chunk|   entity| mesh_code|                                                                                           all_codes|                                                                                         resolutions|                                                                                           distances|
+--------------------------+---------+----------+----------------------------------------------------------------------------------------------------+----------------------------------------------------------------------------------------------------+----------------------------------------------------------------------------------------------------+
|                chest pain|  PROBLEM|   D002637|D002637:::D059350:::D019547:::D020069:::D015746:::D000072716:::D005157:::D059265:::D001416:::D048...|Chest Pain:::Chronic Pain:::Neck Pain:::Shoulder Pain:::Abdominal Pain:::Cancer Pain:::Facial Pai...|0.0000:::0.0577:::0.0587:::0.0601:::0.0658:::0.0704:::0.0712:::0.0741:::0.0766:::0.0778:::0.0794:...|
|bilateral pleural effusion|  PROBLEM|   D010996|D010996:::D010490:::D011654:::D016724:::D010995:::D016066:::D011001:::D007819:::D035422:::D004653...|Pleural Effusion:::Pericardial Effusion:::Pulmonary Edema:::Empyema, Pleural:::Pleural Diseases::...|0.0309:::0.1010:::0.1115:::0.1213:::0.1218:::0.1398:::0.1425:::0.1401:::0.1451:::0.1464:::0.1464:...|
|             the pathology|     TEST|   D010336|D010336:::D010335:::D001004:::D020969:::C001675:::C536472:::D004194:::D003951:::D013631:::C535329...|Pathology:::Pathologic Processes:::Anus Diseases:::Disease Attributes:::malformins:::Upington dis...|0.0788:::0.0977:::0.1364:::0.1396:::0.1419:::0.1459:::0.1418:::0.1393:::0.1514:::0.1541:::0.1491:...|
|        the pericardectomy|TREATMENT|   D010492|D010492:::D011670:::D018700:::D020884:::D011672:::D005927:::D064727:::D002431:::C000678968:::D011...|Pericardiectomy:::Pulpectomy:::Pleurodesis:::Colpotomy:::Pulpotomy:::Glossectomy:::Posterior Caps...|0.1098:::0.1448:::0.1801:::0.1852:::0.1871:::0.1923:::0.1901:::0.2023:::0.2075:::0.2010:::0.1996:...|
|              mesothelioma|  PROBLEM|D000086002|D000086002:::C535700:::D009208:::D032902:::D018301:::D018199:::C562740:::C000686536:::D018276:::D...|Mesothelioma, Malignant:::Malignant mesenchymal tumor:::Myoepithelioma:::Ganoderma:::Neoplasms, M...|0.0813:::0.1515:::0.1599:::0.1810:::0.1864:::0.1881:::0.1907:::0.1938:::0.1924:::0.1876:::0.2040:...|
|      chest tube placement|TREATMENT|   D015505|D015505:::D019616:::D013896:::D012124:::D013906:::D013510:::D020708:::D035423:::D013903:::D000066...|Chest Tubes:::Thoracic Surgical Procedures:::Thoracic Diseases:::Respiratory Care Units:::Thoraco...|0.0557:::0.1473:::0.1598:::0.1604:::0.1725:::0.1651:::0.1795:::0.1760:::0.1804:::0.1846:::0.1883:...|
|     drainage of the fluid|TREATMENT|   D004322|D004322:::D018495:::C045413:::D021061:::D045268:::D018508:::D005441:::D015633:::D014906:::D001834...|Drainage:::Fluid Shifts:::Bonain's liquid:::Liquid Ventilation:::Flowmeters:::Water Purification:...|0.1141:::0.1403:::0.1582:::0.1549:::0.1586:::0.1626:::0.1599:::0.1655:::0.1667:::0.1656:::0.1741:...|
|              thoracoscopy|TREATMENT|   D013906|D013906:::D020708:::D035423:::D013905:::D035441:::D013897:::D001468:::D000069258:::D013909:::D013...|Thoracoscopy:::Thoracoscopes:::Thoracic Cavity:::Thoracoplasty:::Thoracic Wall:::Thoracic Duct:::...|0.0000:::0.0359:::0.0744:::0.1007:::0.1070:::0.1143:::0.1186:::0.1257:::0.1228:::0.1356:::0.1354:...|
|            fluid biopsies|     TEST|D000073890|D000073890:::D010533:::D020420:::D011677:::D017817:::D001706:::D005441:::D005751:::D013582:::D000...|Liquid Biopsy:::Peritoneal Lavage:::Cyst Fluid:::Punctures:::Nasal Lavage Fluid:::Biopsy:::Fluids...|0.1408:::0.1612:::0.1763:::0.1744:::0.1744:::0.1810:::0.1744:::0.1828:::0.1896:::0.1909:::0.1950:...|
|    malignant mesothelioma|  PROBLEM|D000086002|D000086002:::C535700:::C562740:::D009236:::D007890:::D012515:::D009208:::C009823:::C000683999:::C...|Mesothelioma, Malignant:::Malignant mesenchymal tumor:::Hemangiopericytoma, Malignant:::Myxosarco...|0.0737:::0.1106:::0.1658:::0.1627:::0.1660:::0.1639:::0.1728:::0.1676:::0.1791:::0.1843:::0.1849:...|
+-------+--------------------------+---------+----------+----------------------------------------------------------------------------------------------------+----------------------------------------------------------------------------------------------------+----------------------------------------------------------------------------------------------------+
```

+ `sbiobertresolve_ndc` : This model maps clinical entities and concepts (like drugs/ingredients) to [National Drug Codes](https://www.fda.gov/drugs/drug-approvals-and-databases/national-drug-code-directory) using `sbiobert_base_cased_mli` Sentence Bert Embeddings. Also, if a drug has more than one NDC code, it returns all available codes in the all_k_aux_label column separated by `|` symbol.

*Example* :

```bash
...
ndc_resolver = SentenceEntityResolverModel.pretrained("sbiobertresolve_ndc", "en", "clinical/models") \
      .setInputCols(["ner_chunk", "sentence_embeddings"]) \
      .setOutputCol("ndc_code")\
      .setDistanceFunction("EUCLIDEAN")\
      .setCaseSensitive(False)
...

sample_text = """The patient was transferred secondary to inability and continue of her diabetes, the sacral decubitus, left foot pressure wound, and associated complications of diabetes.
She is given aspirin 81 mg, folic acid 1 g daily, insulin glargine 100 UNT/ML injection and metformin 500 mg p.o. p.r.n."""
result = resolver_model.transform(spark.createDataFrame([[sample_text]]).toDF("text"))
```

*Results* :

```bash
+-------------------------------------+------+-----------+------------------------------------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
|                            ner_chunk|entity|   ndc_code|                                                                   description|                                                                                                                                                                                               all_codes|                                                                                                                                                                                         all_resolutions|                                                                                                                                                                                         other ndc codes|
+-------------------------------------+------+-----------+------------------------------------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
|                        aspirin 81 mg|  DRUG|73089008114|                               aspirin 81 mg/81mg, 81 mg in 1 carton , capsule|[73089008114, 71872708704, 71872715401, 68210101500, 69536028110, 63548086706, 71679001000, 68196090051, 00113400500, 69536018112, 73089008112, 63981056362, 63739043402, 63548086705, 00113046708, 7...|[aspirin 81 mg/81mg, 81 mg in 1 carton , capsule, aspirin 81 mg 81 mg/1, 4 blister pack in 1 bag , tablet, aspirin 81 mg/1, 1 blister pack in 1 bag , tablet, coated, aspirin 81 mg/1, 1 bag in 1 dru...|         [-, -, -, -, -, -, -, -, -, -, -, 63940060962, -, -, -, -, -, -, -, -, 70000042002|00363021879|41250027408|36800046708|59779027408|49035027408|71476010131|81522046708|30142046708, -, -, -, -]|
|                       folic acid 1 g|  DRUG|43744015101|                                   folic acid 1 g/g, 1 g in 1 package , powder|[43744015101, 63238340000, 66326050555, 51552041802, 51552041805, 63238340001, 81919000204, 51552041804, 66326050556, 51552106301, 51927003300, 71092997701, 51927296300, 51552146602, 61281900002, 6...|[folic acid 1 g/g, 1 g in 1 package , powder, folic acid 1 kg/kg, 1 kg in 1 bottle , powder, folic acid 1 kg/kg, 1 kg in 1 drum , powder, folic acid 1 g/g, 5 g in 1 container , powder, folic acid 1...|                                                                                               [-, -, -, -, -, -, -, -, -, -, -, 51552139201, -, -, -, 81919000203, -, 81919000201, -, -, -, -, -, -, -]|
|insulin glargine 100 UNT/ML injection|  DRUG|00088502101|insulin glargine 100 [iu]/ml, 1 vial, glass in 1 package , injection, solution|[00088502101, 00088222033, 49502019580, 00002771563, 00169320111, 00088250033, 70518139000, 00169266211, 50090127600, 50090407400, 00002771559, 00002772899, 70518225200, 70518138800, 00024592410, 0...|[insulin glargine 100 [iu]/ml, 1 vial, glass in 1 package , injection, solution, insulin glargine 100 [iu]/ml, 1 vial, glass in 1 carton , injection, solution, insulin glargine 100 [iu]/ml, 1 vial ...|[-, -, -, 00088221900, -, -, 50090139800|00088502005, -, 70518146200|00169368712, 00169368512|73070020011, 00088221905|49502019675|50090406800, -, 73070010011|00169750111|50090495500, 66733077301|0...|
|                     metformin 500 mg|  DRUG|70010006315|               metformin hydrochloride 500 mg/500mg, 500 mg in 1 drum , tablet|[70010006315, 62207041613, 71052050750, 62207049147, 71052091050, 25000010197, 25000013498, 25000010198, 71052063005, 51662139201, 70010049118, 70882012456, 71052011005, 71052065905, 71052050850, 1...|[metformin hydrochloride 500 mg/500mg, 500 mg in 1 drum , tablet, metformin hcl 500 mg/kg, 50 kg in 1 drum , powder, 5-fluorouracil 500 g/500g, 500 g in 1 container , powder, metformin er 500 mg 50...|                                                                                             [-, -, -, 70010049105, -, -, -, -, -, -, -, -, -, -, -, 71800000801|42571036007, -, -, -, -, -, -, -, -, -]|
+-------------------------------------+------+-----------+------------------------------------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
```

+ `sbiobertresolve_loinc_augmented` : This model maps extracted clinical NER entities to LOINC codes using `sbiobert_base_cased_mli` Sentence Bert Embeddings. It is trained on the augmented version of the dataset which is used in previous LOINC resolver models.

*Example* :

```bash
...
loinc_resolver = SentenceEntityResolverModel.pretrained("sbiobertresolve_loinc_augmented","en", "clinical/models") \
     .setInputCols(["ner_chunk", "sentence_embeddings"]) \
     .setOutputCol("loinc_code")\
     .setDistanceFunction("EUCLIDEAN")\
     .setCaseSensitive(False)
...

sample_text="""The patient is a 22-year-old female with a history of obesity. She has a Body mass index (BMI) of 33.5 kg/m2, aspartate aminotransferase 64, and alanine aminotransferase 126. Her hgba1c is 8.2%."""
result = resolver_model.transform(spark.createDataFrame([[sample_text]]).toDF("text"))
```

*Results* :

```bash
+--------------------------+-----+---+------+----------+--------------------------------------------------+--------------------------------------------------+
|                     chunk|begin|end|entity|Loinc_Code|                                         all_codes|                                       resolutions|
+--------------------------+-----+---+------+----------+--------------------------------------------------+--------------------------------------------------+
|           Body mass index|   74| 88|  Test| LP35925-4|LP35925-4:::BDYCRC:::LP172732-2:::39156-5:::LP7...|body mass index:::body circumference:::body mus...|
|aspartate aminotransferase|  111|136|  Test| LP15426-7|LP15426-7:::14409-7:::LP307348-5:::LP15333-5:::...|aspartate aminotransferase::: aspartate transam...|
|  alanine aminotransferase|  146|169|  Test| LP15333-5|LP15333-5:::LP307326-1:::16324-6:::LP307348-5::...|alanine aminotransferase:::alanine aminotransfe...|
|                    hgba1c|  180|185|  Test|   17855-8|17855-8:::4547-6:::55139-0:::72518-4:::45190-6:...| hba1c::: hgb a1::: hb1::: hcds1::: hhc1::: htr...|
+--------------------------+-----+---+------+----------+--------------------------------------------------+--------------------------------------------------+
```

+ `sbiobertresolve_clinical_snomed_procedures_measurements` : This model maps medical entities to SNOMED codes using `sent_biobert_clinical_base_cased` Sentence Bert Embeddings. The corpus of this model includes `Procedures` and `Measurement` domains.

*Example* :

```bash
...
snomed_resolver = SentenceEntityResolverModel.pretrained("sbiobertresolve_clinical_snomed_procedures_measurements", "en", "clinical/models") \
      .setInputCols(["ner_chunk", "sbert_embeddings"]) \
      .setOutputCol("snomed_code")
...

light_model = LightPipeline(resolver_model)
result = light_model.fullAnnotate(['coronary calcium score', 'heart surgery', 'ct scan', 'bp value'])

```

*Results* :

```bash
|    | chunk                  |      code | code_description              | all_k_codes                                                                     | all_k_resolutions                                                                                                                                               |
|---:|:-----------------------|----------:|:------------------------------|:--------------------------------------------------------------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------|
|  0 | coronary calcium score | 450360000 | Coronary artery calcium score | ['450360000', '450734004', '1086491000000104', '1086481000000101', '762241007'] | ['Coronary artery calcium score', 'Coronary artery calcium score', 'Dundee Coronary Risk Disk score', 'Dundee Coronary Risk rank', 'Dundee Coronary Risk Disk'] |
|  1 | heart surgery          |   2598006 | Open heart surgery            | ['2598006', '64915003', '119766003', '34068001', '233004008']                   | ['Open heart surgery', 'Operation on heart', 'Heart reconstruction', 'Heart valve replacement', 'Coronary sinus operation']                                     |
|  2 | ct scan                | 303653007 | CT of head                    | ['303653007', '431864000', '363023007', '418272005', '241577003']               | ['CT of head', 'CT guided injection', 'CT of site', 'CT angiography', 'CT of spine']                                                                            |
|  3 | bp value               |  75367002 | Blood pressure                | ['75367002', '6797001', '723232008', '46973005', '427732000']                   | ['Blood pressure', 'Mean blood pressure', 'Average blood pressure', 'Blood pressure taking', 'Speed of blood pressure response']                                |
```

#### Updated RxNorm Sentence Entity Resolver Model

We have updated `sbiobertresolve_rxnorm_augmented` model training on an augmented version of the dataset used in previous versions of the model.

#### New Shift Days Feature in StructuredDeid Deidentification Module

 Now we can shift n days in the structured deidentification when the column is a Date.

 *Example* :

 ```pyhton
 df = spark.createDataFrame([
            ["Juan García", "13/02/1977", "711 Nulla St.", "140", "673 431234"],
            ["Will Smith", "23/02/1977", "1 Green Avenue.", "140", "+23 (673) 431234"],
            ["Pedro Ximénez", "11/04/1900", "Calle del Libertador, 7", "100", "912 345623"]
        ]).toDF("NAME", "DOB", "ADDRESS", "SBP", "TEL")

 obfuscator = StructuredDeidentification(spark=spark, columns={"NAME": "ID", "DOB": "DATE"},
                                                      columnsSeed={"NAME": 23, "DOB": 23},
                                                      obfuscateRefSource="faker",
                                                      days=5
                                         )

result = obfuscator.obfuscateColumns(self.df)
result.show(truncate=False)                                             
```

*Results* :

```bash
+----------+------------+-----------------------+---+----------------+
|NAME      |DOB         |ADDRESS                |SBP|TEL             |
+----------+------------+-----------------------+---+----------------+
|[T1825511]|[18/02/1977]|711 Nulla St.          |140|673 431234      |
|[G6835267]|[28/02/1977]|1 Green Avenue.        |140|+23 (673) 431234|
|[S2371443]|[16/04/1900]|Calle del Libertador, 7|100|912 345623      |
+----------+------------+-----------------------+---+----------------+
```

#### New Multiple Chunks Merge Ability in ChunkMergeApproach

Updated ChunkMergeApproach to admit N input cols (`.setInputCols("ner_chunk","ner_chunk_1","ner_chunk_2")`). The input columns must be chunk columns.

*Example* :

```python
...
deid_ner = MedicalNerModel.pretrained("ner_deid_large", "en", "clinical/models") \
            .setInputCols(["sentence", "token", "embeddings"]) \
            .setOutputCol("ner")

ner_converter = NerConverter() \
            .setInputCols(["sentence", "token", "ner"]) \
            .setOutputCol("ner_chunk") \
            .setWhiteList(['DATE', 'AGE', 'NAME', 'PROFESSION', 'ID'])

medical_ner = MedicalNerModel.pretrained("ner_events_clinical", "en", "clinical/models") \
            .setInputCols(["sentence", "token", "embeddings"]) \
            .setOutputCol("ner2")

ner_converter_2 = NerConverter() \
            .setInputCols(["sentence", "token", "ner2"]) \
            .setOutputCol("ner_chunk_2")

ssn_parser = ContextualParserApproach() \
            .setInputCols(["sentence", "token"]) \
            .setOutputCol("entity_ssn") \
            .setJsonPath("../../src/test/resources/ssn.json") \
            .setCaseSensitive(False) \
            .setContextMatch(False)

chunk_merge = ChunkMergeApproach() \
            .setInputCols("entity_ssn","ner_chunk","ner_chunk_2") \
            .setOutputCol("deid_merged_chunk") \
            .setChunkPrecedence("field")      
...
```

#### New setBlackList Feature in ChunkMergeApproach

Now we can filter out the entities in the ChunkMergeApproach using a black list `.setBlackList(["NAME","ID"])`. The entities specified in the blackList will be excluded from the final entity list.

*Example* :

```python
chunk_merge = ChunkMergeApproach() \
            .setInputCols("entity_ssn","ner_chunk") \
            .setOutputCol("deid_merged_chunk") \
            .setBlackList(["NAME","ID"])
```

#### New setBlackList Feature in NerConverterInternal

Now we can filter out the entities in the NerConverterInternal using a black list `.setBlackList(["Drug","Treatment"])`. The entities specified in the blackList will be excluded from the final entity list.

*Example* :

```python
ner = MedicalNerModel.pretrained("ner_jsl_slim", "en", "clinical/models")\
        .setInputCols("sentence", "token","embeddings")\
        .setOutputCol("ner")

converter = NerConverterInternal()\
        .setInputCols("sentence","token","ner")\
        .setOutputCol("entities")\
        .setBlackList(["Drug","Treatment"])
```

#### New setLabelCasing Feature in MedicalNerModel

Now we can decide if we want to return the tags in upper or lower case with `setLabelCasing()`. That method convert the I-tags and B-tags in lower or upper case during the inference. The values will be 'lower' for lower case and 'upper' for upper case.


*Example* :

```python
...
ner_tagger = MedicalNerModel() \
            .pretrained("ner_clinical", "en", "clinical/models") \
            .setInputCols(["sentences", "tokens", "embeddings"]) \
            .setOutputCol("ner_tags") \
            .setLabelCasing("lower")
...

results = LightPipeline(pipelineModel).annotate("A 28-year-old female with a history of gestational diabetes mellitus diagnosed eight years prior to presentation and subsequent type two diabetes mellitus ")
results["ner_tags"]
```

*Results* :

```bash
['O', 'O', 'O', 'O', 'O', 'O', 'O', 'B-problem', 'I-problem', 'I-problem', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'B-problem', 'I-problem', 'I-problem', 'I-problem', 'I-problem']
```


#### New Update Models Functionality

We developed a new utility function called `UpdateModels` that allows you to refresh your `cache_pretrained` folder without running any annotator or manually checking. It has two methods;

+ `UpdateModels.updateCacheModels()` : This method lets you update all the models existing in the `cache_pretrained` folder. It downloads the latest version of all the models existing in the `cache_pretrained`.

*Example* :

```bash
# Models in /cache_pretrained
ls ~/cache_pretrained
>> ner_clinical_large_en_3.0.0_2.3_1617206114650/

# Update models in /cache_pretrained
from sparknlp_jsl.updateModels import UpdateModels
UpdateModels.updateCacheModels()
```

*Results* :

```bash
# Updated models in /cache_pretrained
ls ~/cache_pretrained
>> ner_clinical_large_en_3.0.0_2.3_1617206114650/
   ner_clinical_large_en_3.0.0_3.0_1617206114650/
```


+ `UpdateModels.updateModels("11/24/2021")` : This method lets you download all the new models uploaded to the Models Hub starting from a cut-off date (i.e. the last sync update).

*Example* :

```bash
# Models in /cache_pretrained
ls ~/cache_pretrained
>> ner_clinical_large_en_3.0.0_2.3_1617206114650/
   ner_clinical_large_en_3.0.0_3.0_1617206114650/

# Update models in /cache_pretrained according to date
from sparknlp_jsl.updateModels import UpdateModels
UpdateModels.updateModels("11/24/2021")

```

*Results* :

```bash
# Updated models in /cache_pretrained
ls ~/cache_pretrained
>>ner_clinical_large_en_3.0.0_2.3_1617206114650/
  ner_clinical_large_en_3.0.0_3.0_1617206114650/
  ner_model_finder_en_3.3.2_2.4_1637761259895/
  sbertresolve_ner_model_finder_en_3.3.2_2.4_1637764208798/
```


#### New and Updated Notebooks

+ We have a new [Connect to Annotation Lab via API Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Annotation_Lab/AL_API_import_export_pre_annotate.ipynb) you can find how to;

     - upload pre-annotations to ALAB
     - import a project form ALAB and convert to CoNLL file
     - upload tasks without pre-annotations

+ We have updated [Clinical Relation Extraction Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/10.Clinical_Relation_Extraction.ipynb) by adding a Relation Extraction Model-NER Model-Relation Pairs table that can be used to get the most optimal results when using these models.


**To see more, please check : [Spark NLP Healthcare Workshop Repo](https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/tutorials/Certification_Trainings/Healthcare)**


## 3.3.2
We are glad to announce that Spark NLP Healthcare 3.3.2 has been released!.



#### Highlights

+ New Clinical NER Models and Spanish NER Model
+ New BERT-Based Clinical NER Models
+ Updated Clinical NER Model
+ New NER Model Class Distribution Feature
+ New RxNorm Sentence Entity Resolver Model
+ New Spanish SNOMED Sentence Entity Resolver Model
+ New Clinical Question vs Statement BertForSequenceClassification model
+ New Sentence Entity Resolver Fine-Tune Features (Overwriting and Drop Code)
+ Updated ICD10CM Entity Resolver Models
+ Updated NER Profiling Pretrained Pipelines
+ New ChunkSentenceSplitter Annotator
+ Updated Spark NLP For Healthcare Notebooks and New Notebooks

#### New Clinical NER Models (including a new Spanish one)

We are releasing three new clinical NER models trained by MedicalNerApproach().

+ `roberta_ner_diag_proc` : This models leverages Spanish Roberta Biomedical Embeddings (`roberta_base_biomedical`) to extract two entities, Diagnosis and Procedures (`DIAGNOSTICO`, `PROCEDIMIENTO`). It's a renewed version of `ner_diag_proc_es`, available [here](https://nlp.johnsnowlabs.com/2020/07/08/ner_diag_proc_es.html), that was trained with `embeddings_scielowiki_300d` embeddings instead.

*Example* :

```bash
...
embeddings =  RoBertaEmbeddings.pretrained("roberta_base_biomedical", "es")\
    .setInputCols(["sentence", "token"])\
    .setOutputCol("embeddings")

ner = MedicalNerModel.pretrained("roberta_ner_diag_proc", "es", "clinical/models")\
    .setInputCols(["sentence", "token", "embeddings"])\
    .setOutputCol("ner")\

ner_converter = NerConverter() \
    .setInputCols(['sentence', 'token', 'ner']) \
    .setOutputCol('ner_chunk')

pipeline = Pipeline(stages = [
    documentAssembler,
    sentenceDetector,
    tokenizer,
    embeddings,
    ner,
    ner_converter])

empty = spark.createDataFrame([['']]).toDF("text")

p_model = pipeline.fit(empty)

test_sentence = 'Mujer de 28 años con antecedentes de diabetes mellitus gestacional diagnosticada ocho años antes de la presentación y posterior diabetes mellitus tipo dos (DM2), un episodio previo de pancreatitis inducida por HTG tres años antes de la presentación, asociado con una hepatitis aguda, y obesidad con un índice de masa corporal (IMC) de 33,5 kg / m2, que se presentó con antecedentes de una semana de poliuria, polidipsia, falta de apetito y vómitos. Dos semanas antes de la presentación, fue tratada con un ciclo de cinco días de amoxicilina por una infección del tracto respiratorio. Estaba tomando metformina, glipizida y dapagliflozina para la DM2 y atorvastatina y gemfibrozil para la HTG. Había estado tomando dapagliflozina durante seis meses en el momento de la presentación. El examen físico al momento de la presentación fue significativo para la mucosa oral seca; significativamente, su examen abdominal fue benigno sin dolor a la palpación, protección o rigidez. Los hallazgos de laboratorio pertinentes al ingreso fueron: glucosa sérica 111 mg / dl, bicarbonato 18 mmol / l, anión gap 20, creatinina 0,4 mg / dl, triglicéridos 508 mg / dl, colesterol total 122 mg / dl, hemoglobina glucosilada (HbA1c) 10%. y pH venoso 7,27. La lipasa sérica fue normal a 43 U / L. Los niveles séricos de acetona no pudieron evaluarse ya que las muestras de sangre se mantuvieron hemolizadas debido a una lipemia significativa. La paciente ingresó inicialmente por cetosis por inanición, ya que refirió una ingesta oral deficiente durante los tres días previos a la admisión. Sin embargo, la química sérica obtenida seis horas después de la presentación reveló que su glucosa era de 186 mg / dL, la brecha aniónica todavía estaba elevada a 21, el bicarbonato sérico era de 16 mmol / L, el nivel de triglicéridos alcanzó un máximo de 2050 mg / dL y la lipasa fue de 52 U / L. Se obtuvo el nivel de β-hidroxibutirato y se encontró que estaba elevado a 5,29 mmol / L; la muestra original se centrifugó y la capa de quilomicrones se eliminó antes del análisis debido a la interferencia de la turbidez causada por la lipemia nuevamente. El paciente fue tratado con un goteo de insulina para euDKA y HTG con una reducción de la brecha aniónica a 13 y triglicéridos a 1400 mg / dL, dentro de las 24 horas. Se pensó que su euDKA fue precipitada por su infección del tracto respiratorio en el contexto del uso del inhibidor de SGLT2. La paciente fue atendida por el servicio de endocrinología y fue dada de alta con 40 unidades de insulina glargina por la noche, 12 unidades de insulina lispro con las comidas y metformina 1000 mg dos veces al día. Se determinó que todos los inhibidores de SGLT2 deben suspenderse indefinidamente. Tuvo un seguimiento estrecho con endocrinología post alta.'
res = p_model.transform(spark.createDataFrame(pd.DataFrame({'text': [test_sentence]})))
```

*Results* :
```bash
+---------------------------------+------------+
|                             text|ner_label  |
+---------------------------------+------------+
|    diabetes mellitus gestacional|DIAGNOSTICO|
|       diabetes mellitus tipo dos|DIAGNOSTICO|
|                              DM2|DIAGNOSTICO|
|    pancreatitis inducida por HTG|DIAGNOSTICO|
|                  hepatitis aguda|DIAGNOSTICO|
|                         obesidad|DIAGNOSTICO|
|          índice de masa corporal|DIAGNOSTICO|
|                              IMC|DIAGNOSTICO|
|                         poliuria|DIAGNOSTICO|
|                       polidipsia|DIAGNOSTICO|
|                          vómitos|DIAGNOSTICO|
|infección del tracto respiratorio|DIAGNOSTICO|
|                              DM2|DIAGNOSTICO|
|                              HTG|DIAGNOSTICO|
|                            dolor|DIAGNOSTICO|
|                          rigidez|DIAGNOSTICO|
|                          cetosis|DIAGNOSTICO|
|infección del tracto respiratorio|DIAGNOSTICO|
+---------------------------------+-----------+
```

+ `ner_covid_trials` : This model is trained to extract covid-specific medical entities in clinical trials. It supports the following entities ranging from virus type to trial design: `Stage`, `Severity`, `Virus`, `Trial_Design`, `Trial_Phase`, `N_Patients`, `Institution`, `Statistical_Indicator`, `Section_Header`, `Cell_Type`, `Cellular_component`, `Viral_components`, `Physiological_reaction`, `Biological_molecules`, `Admission_Discharge`, `Age`, `BMI`, `Cerebrovascular_Disease`, `Date`, `Death_Entity`, `Diabetes`, `Disease_Syndrome_Disorder`, `Dosage`, `Drug_Ingredient`, `Employment`, `Frequency`, `Gender`, `Heart_Disease`, `Hypertension`, `Obesity`, `Pulse`, `Race_Ethnicity`, `Respiration`, `Route`, `Smoking`, `Time`, `Total_Cholesterol`, `Treatment`, `VS_Finding`, `Vaccine` .

*Example* :
```bash
...
covid_ner = MedicalNerModel.pretrained('ner_covid_trials', 'en', 'clinical/models') \
      .setInputCols(["sentence", "token", "embeddings"]) \
      .setOutputCol("ner")    
...

results = covid_model.transform(spark.createDataFrame(pd.DataFrame({"text": ["""In December 2019 , a group of patients with the acute respiratory disease was detected in Wuhan , Hubei Province of China . A month later , a new beta-coronavirus was identified as the cause of the 2019 coronavirus infection . SARS-CoV-2 is a coronavirus that belongs to the group of β-coronaviruses of the subgenus Coronaviridae . The SARS-CoV-2 is the third known zoonotic coronavirus disease after severe acute respiratory syndrome ( SARS ) and Middle Eastern respiratory syndrome ( MERS ). The diagnosis of SARS-CoV-2 recommended by the WHO , CDC is the collection of a sample from the upper respiratory tract ( nasal and oropharyngeal exudate ) or from the lower respiratory tract such as expectoration of endotracheal aspirate and bronchioloalveolar lavage and its analysis using the test of real-time polymerase chain reaction ( qRT-PCR )."""]})))
```

*Results* :

```bash

|    | chunk                               |   begin |   end | entity                    |
|---:|:------------------------------------|--------:|------:|:--------------------------|
|  0 | December 2019                       |       3 |    15 | Date                      |
|  1 | acute respiratory disease           |      48 |    72 | Disease_Syndrome_Disorder |
|  2 | beta-coronavirus                    |     146 |   161 | Virus                     |
|  3 | 2019 coronavirus infection          |     198 |   223 | Disease_Syndrome_Disorder |
|  4 | SARS-CoV-2                          |     227 |   236 | Virus                     |
|  5 | coronavirus                         |     243 |   253 | Virus                     |
|  6 | β-coronaviruses                     |     284 |   298 | Virus                     |
|  7 | subgenus Coronaviridae              |     307 |   328 | Virus                     |
|  8 | SARS-CoV-2                          |     336 |   345 | Virus                     |
|  9 | zoonotic coronavirus disease        |     366 |   393 | Disease_Syndrome_Disorder |
| 10 | severe acute respiratory syndrome   |     401 |   433 | Disease_Syndrome_Disorder |
| 11 | SARS                                |     437 |   440 | Disease_Syndrome_Disorder |
| 12 | Middle Eastern respiratory syndrome |     448 |   482 | Disease_Syndrome_Disorder |
| 13 | MERS                                |     486 |   489 | Disease_Syndrome_Disorder |
| 14 | SARS-CoV-2                          |     511 |   520 | Virus                     |
| 15 | WHO                                 |     541 |   543 | Institution               |
| 16 | CDC                                 |     547 |   549 | Institution               |
```

+ `ner_chemd_clinical` : This model extract the names of chemical compounds and drugs in medical texts. The entities that can be detected are as follows : `SYSTEMATIC`, `IDENTIFIERS`, `FORMULA`, `TRIVIAL`, `ABBREVIATION`, `FAMILY`, `MULTIPLE` . For reference [click here](https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4331685/) .

*Example* :
```bash
...
chemd_ner = MedicalNerModel.pretrained('ner_chemd', 'en', 'clinical/models') \
      .setInputCols(["sentence", "token", "embeddings"]) \
      .setOutputCol("ner")    
...

results = chemd_model.transform(spark.createDataFrame(pd.DataFrame({"text": ["""Isolation, Structure Elucidation, and Iron-Binding Properties of Lystabactins, Siderophores Isolated from a Marine Pseudoalteromonas sp. The marine bacterium Pseudoalteromonas sp. S2B, isolated from the Gulf of Mexico after the Deepwater Horizon oil spill, was found to produce lystabactins A, B, and C (1-3), three new siderophores. The structures were elucidated through mass spectrometry, amino acid analysis, and NMR. The lystabactins are composed of serine (Ser), asparagine (Asn), two formylated/hydroxylated ornithines (FOHOrn), dihydroxy benzoic acid (Dhb), and a very unusual nonproteinogenic amino acid, 4,8-diamino-3-hydroxyoctanoic acid (LySta). The iron-binding properties of the compounds were investigated through a spectrophotometric competition."""]})))
```

*Results* :

```bash
+----------------------------------+------------+
|chunk                             |ner_label   |
+----------------------------------+------------+
|Lystabactins                      |FAMILY      |
|lystabactins A, B, and C          |MULTIPLE    |
|amino acid                        |FAMILY      |
|lystabactins                      |FAMILY      |
|serine                            |TRIVIAL     |
|Ser                               |FORMULA     |
|asparagine                        |TRIVIAL     |
|Asn                               |FORMULA     |
|formylated/hydroxylated ornithines|FAMILY      |
|FOHOrn                            |FORMULA     |
|dihydroxy benzoic acid            |SYSTEMATIC  |
|amino acid                        |FAMILY      |
|4,8-diamino-3-hydroxyoctanoic acid|SYSTEMATIC  |
|LySta                             |ABBREVIATION|
+----------------------------------+------------+
```

#### New BERT-Based Clinical NER Models

We have two new BERT-Based token classifier NER models.

+ `bert_token_classifier_ner_bionlp` : This model is BERT-based version of `ner_bionlp` model and can detect biological and genetics terms in cancer-related texts. (`Amino_acid`, `Anatomical_system`, `Cancer`, `Cell`, `Cellular_component`, `Developing_anatomical_Structure`, `Gene_or_gene_product`, `Immaterial_anatomical_entity`, `Multi-tissue_structure`, `Organ`, `Organism`, `Organism_subdivision`, `Simple_chemical`, `Tissue`)

*Example* :

```python
...
tokenClassifier = BertForTokenClassification.pretrained("bert_token_classifier_ner_bionlp", "en", "clinical/models")\
    .setInputCols("token", "document")\
    .setOutputCol("ner")\
    .setCaseSensitive(True)
...

test_sentence = """Both the erbA IRES and the erbA/myb virus constructs transformed erythroid cells after infection of bone marrow or blastoderm cultures. The erbA/myb IRES virus exhibited a 5-10-fold higher transformed colony forming efficiency than the erbA IRES virus in the blastoderm assay."""
result = p_model.transform(spark.createDataFrame(pd.DataFrame({'text': [test_sentence]})))
```

*Results* :

```bash
+-------------------+----------------------+
|chunk              |ner_label             |
+-------------------+----------------------+
|erbA IRES          |Organism              |
|erbA/myb virus     |Organism              |
|erythroid cells    |Cell                  |
|bone marrow        |Multi-tissue_structure|
|blastoderm cultures|Cell                  |
|erbA/myb IRES virus|Organism              |
|erbA IRES virus    |Organism              |
|blastoderm         |Cell                  |
+-------------------+----------------------+
```

+ `bert_token_classifier_ner_cellular` : This model is BERT-based version of `ner_cellular` model and can detect molecular biology-related terms (`DNA`, `Cell_type`, `Cell_line`, `RNA`, `Protein`) in medical texts.

*Metrics* :

```bash
              precision    recall  f1-score   support

       B-DNA       0.87      0.77      0.82      1056
       B-RNA       0.85      0.79      0.82       118
 B-cell_line       0.66      0.70      0.68       500
 B-cell_type       0.87      0.75      0.81      1921
   B-protein       0.90      0.85      0.88      5067
       I-DNA       0.93      0.86      0.90      1789
       I-RNA       0.92      0.84      0.88       187
 I-cell_line       0.67      0.76      0.71       989
 I-cell_type       0.92      0.76      0.84      2991
   I-protein       0.94      0.80      0.87      4774

    accuracy                           0.80     19392
   macro avg       0.76      0.81      0.78     19392
weighted avg       0.89      0.80      0.85     19392
```

*Example* :

```python
...

tokenClassifier = BertForTokenClassification.pretrained("bert_token_classifier_ner_cellular", "en", "clinical/models")
.setInputCols("token", "document")
.setOutputCol("ner")
.setCaseSensitive(True)

...

test_sentence = """Detection of various other intracellular signaling proteins is also described. Genetic characterization of transactivation of the human T-cell leukemia virus type 1 promoter: Binding of Tax to Tax-responsive element 1 is mediated by the cyclic AMP-responsive members of the CREB/ATF family of transcription factors. To achieve a better understanding of the mechanism of transactivation by Tax of human T-cell leukemia virus type 1 Tax-responsive element 1 (TRE-1), we developed a genetic approach with Saccharomyces cerevisiae. We constructed a yeast reporter strain containing the lacZ gene under the control of the CYC1 promoter associated with three copies of TRE-1. Expression of either the cyclic AMP response element-binding protein (CREB) or CREB fused to the GAL4 activation domain (GAD) in this strain did not modify the expression of the reporter gene. Tax alone was also inactive."""

result = p_model.transform(spark.createDataFrame(pd.DataFrame({'text': [test_sentence]})))
```

*Results* :

```bash
+-------------------------------------------+---------+
|chunk                                      |ner_label|
+-------------------------------------------+---------+
|intracellular signaling proteins           |protein  |
|human T-cell leukemia virus type 1 promoter|DNA      |
|Tax                                        |protein  |
|Tax-responsive element 1                   |DNA      |
|cyclic AMP-responsive members              |protein  |
|CREB/ATF family                            |protein  |
|transcription factors                      |protein  |
|Tax                                        |protein  |
|human T-cell leukemia virus type 1         |DNA      |
|Tax-responsive element 1                   |DNA      |
|TRE-1                                      |DNA      |
|lacZ gene                                  |DNA      |
|CYC1 promoter                              |DNA      |
|TRE-1                                      |DNA      |
|cyclic AMP response element-binding protein|protein  |
|CREB                                       |protein  |
|CREB                                       |protein  |
|GAL4 activation domain                     |protein  |
|GAD                                        |protein  |
|reporter gene                              |DNA      |
|Tax                                        |protein  |
+-------------------------------------------+---------+
```

#### Updated Clinical NER Model

We have updated `ner_jsl_enriched` model by enriching the training data using clinical trials data to make it more robust. This model is capable of predicting up to `87` different entities and is based on `ner_jsl` model. Here are the entities this model can detect;

`Social_History_Header`, `Oncology_Therapy`, `Blood_Pressure`, `Respiration`, `Performance_Status`, `Family_History_Header`, `Dosage`, `Clinical_Dept`, `Diet`, `Procedure`, `HDL`, `Weight`, `Admission_Discharge`, `LDL`, `Kidney_Disease`, `Oncological`, `Route`, `Imaging_Technique`, `Puerperium`, `Overweight`, `Temperature`, `Diabetes`, `Vaccine`, `Age`, `Test_Result`, `Employment`, `Time`, `Obesity`, `EKG_Findings`, `Pregnancy`, `Communicable_Disease`, `BMI`, `Strength`, `Tumor_Finding`, `Section_Header`, `RelativeDate`, `ImagingFindings`, `Death_Entity`, `Date`, `Cerebrovascular_Disease`, `Treatment`, `Labour_Delivery`, `Pregnancy_Delivery_Puerperium`, `Direction`, `Internal_organ_or_component`, `Psychological_Condition`, `Form`, `Medical_Device`, `Test`, `Symptom`, `Disease_Syndrome_Disorder`, `Staging`, `Birth_Entity`, `Hyperlipidemia`, `O2_Saturation`, `Frequency`, `External_body_part_or_region`, `Drug_Ingredient`, `Vital_Signs_Header`, `Substance_Quantity`, `Race_Ethnicity`, `VS_Finding`, `Injury_or_Poisoning`, `Medical_History_Header`, `Alcohol`, `Triglycerides`, `Total_Cholesterol`, `Sexually_Active_or_Sexual_Orientation`, `Female_Reproductive_Status`, `Relationship_Status`, `Drug_BrandName`, `RelativeTime`, `Duration`, `Hypertension`, `Metastasis`, `Gender`, `Oxygen_Therapy`, `Pulse`, `Heart_Disease`, `Modifier`, `Allergen`, `Smoking`, `Substance`, `Cancer_Modifier`, `Fetus_NewBorn`, `Height` .

*Example* :

```bash
...  
clinical_ner = MedicalNerModel.pretrained("ner_jsl_enriched", "en", "clinical/models") \
    .setInputCols(["sentence", "token", "embeddings"]) \
    .setOutputCol("ner")
...

results = model.transform(spark.createDataFrame([["The patient is a 21-day-old Caucasian male here for 2 days of congestion - mom has been suctioning yellow discharge from the patient's nares, plus she has noticed some mild problems with his breathing while feeding (but negative for any perioral cyanosis or retractions). One day ago, mom also noticed a tactile temperature and gave the patient Tylenol. Baby also has had some decreased p.o. intake. His normal breast-feeding is down from 20 minutes q.2h. to 5 to 10 minutes secondary to his respiratory congestion. He sleeps well, but has been more tired and has been fussy over the past 2 days. The parents noticed no improvement with albuterol treatments given in the ER. His urine output has also decreased; normally he has 8 to 10 wet and 5 dirty diapers per 24 hours, now he has down to 4 wet diapers per 24 hours. Mom denies any diarrhea. His bowel movements are yellow colored and soft in nature."]], ["text"]))
```
*Results* :

```bash
|    | chunk                                     |   begin |   end | entity                       |
|---:|:------------------------------------------|--------:|------:|:-----------------------------|
|  0 | 21-day-old                                |      17 |    26 | Age                          |
|  1 | Caucasian                                 |      28 |    36 | Race_Ethnicity               |
|  2 | male                                      |      38 |    41 | Gender                       |
|  3 | 2 days                                    |      52 |    57 | Duration                     |
|  4 | congestion                                |      62 |    71 | Symptom                      |
|  5 | mom                                       |      75 |    77 | Gender                       |
|  6 | suctioning yellow discharge               |      88 |   114 | Symptom                      |
|  7 | nares                                     |     135 |   139 | External_body_part_or_region |
|  8 | she                                       |     147 |   149 | Gender                       |
|  9 | mild                                      |     168 |   171 | Modifier                     |
| 10 | problems with his breathing while feeding |     173 |   213 | Symptom                      |
| 11 | perioral cyanosis                         |     237 |   253 | Symptom                      |
| 12 | retractions                               |     258 |   268 | Symptom                      |
| 13 | One day ago                               |     272 |   282 | RelativeDate                 |
| 14 | mom                                       |     285 |   287 | Gender                       |
| 15 | tactile temperature                       |     304 |   322 | Symptom                      |
| 16 | Tylenol                                   |     345 |   351 | Drug_BrandName               |
| 17 | Baby                                      |     354 |   357 | Age                          |
| 18 | decreased p.o. intake                     |     377 |   397 | Symptom                      |
| 19 | His                                       |     400 |   402 | Gender                       |
| 20 | q.2h                                      |     450 |   453 | Frequency                    |
| 21 | 5 to 10 minutes                           |     459 |   473 | Duration                     |
| 22 | his                                       |     488 |   490 | Gender                       |
| 23 | respiratory congestion                    |     492 |   513 | Symptom                      |
| 24 | He                                        |     516 |   517 | Gender                       |
| 25 | tired                                     |     550 |   554 | Symptom                      |
| 26 | fussy                                     |     569 |   573 | Symptom                      |
| 27 | over the past 2 days                      |     575 |   594 | RelativeDate                 |
| 28 | albuterol                                 |     637 |   645 | Drug_Ingredient              |
| 29 | ER                                        |     671 |   672 | Clinical_Dept                |
| 30 | His                                       |     675 |   677 | Gender                       |
| 31 | urine output has also decreased           |     679 |   709 | Symptom                      |
| 32 | he                                        |     721 |   722 | Gender                       |
| 33 | per 24 hours                              |     760 |   771 | Frequency                    |
| 34 | he                                        |     778 |   779 | Gender                       |
| 35 | per 24 hours                              |     807 |   818 | Frequency                    |
| 36 | Mom                                       |     821 |   823 | Gender                       |
| 37 | diarrhea                                  |     836 |   843 | Symptom                      |
| 38 | His                                       |     846 |   848 | Gender                       |
| 39 | bowel                                     |     850 |   854 | Internal_organ_or_component  |
```

#### New NER Model Class Distribution Feature

+ `getTrainingClassDistribution` : This parameter returns the distribution of labels used when training the NER model.

*Example*:

```bash
ner_model.getTrainingClassDistribution()
>> {'B-Disease': 2536, 'O': 31659, 'I-Disease': 2960}
```

#### New RxNorm Sentence Entity Resolver Model

+ `sbiobertresolve_rxnorm_augmented` : This model maps clinical entities and concepts (like drugs/ingredients) to RxNorm codes using sbiobert_base_cased_mli Sentence Bert Embeddings. It trained on the augmented version of the dataset which is used in previous RxNorm resolver models. Additionally, this model returns concept classes of the drugs in all_k_aux_labels column.

#### New Spanish SNOMED Sentence Entity Resolver Model

+ `robertaresolve_snomed` : This models leverages Spanish Roberta Biomedical Embeddings (`roberta_base_biomedical`) at sentence-level to map ner chunks into Spanish SNOMED codes.

*Example* :

```bash

documentAssembler = DocumentAssembler()\
    .setInputCol("text")\
    .setOutputCol("document")

sentenceDetector = SentenceDetectorDLModel.pretrained() \
    .setInputCols(["document"]) \
    .setOutputCol("sentence")

tokenizer = Tokenizer()\
    .setInputCols("sentence")\
    .setOutputCol("token")

word_embeddings = RoBertaEmbeddings.pretrained("roberta_base_biomedical", "es")\
    .setInputCols(["sentence", "token"])\
    .setOutputCol("roberta_embeddings")

ner = MedicalNerModel.pretrained("roberta_ner_diag_proc","es","clinical/models")\
    .setInputCols("sentence","token","roberta_embeddings")\
    .setOutputCol("ner")

ner_converter = NerConverter() \
    .setInputCols(["sentence", "token", "ner"]) \
    .setOutputCol("ner_chunk")

c2doc = Chunk2Doc() \
    .setInputCols(["ner_chunk"]) \
    .setOutputCol("ner_chunk_doc")

chunk_embeddings = SentenceEmbeddings() \
    .setInputCols(["ner_chunk_doc", "roberta_embeddings"]) \
    .setOutputCol("chunk_embeddings") \
    .setPoolingStrategy("AVERAGE")

er = SentenceEntityResolverModel.pretrained("robertaresolve_snomed", "es", "clinical/models")\
    .setInputCols(["ner_chunk_doc", "chunk_embeddings"]) \
    .setOutputCol("snomed_code") \
    .setDistanceFunction("EUCLIDEAN")

snomed_training_pipeline = Pipeline(stages = [
    documentAssembler,
    sentenceDetector,
    tokenizer,
    word_embeddings,
    ner,
    ner_converter,
    c2doc,
    chunk_embeddings,
    er])

empty = spark.createDataFrame([['']]).toDF("text")

p_model = snomed_pipeline .fit(empty)

test_sentence = 'Mujer de 28 años con antecedentes de diabetes mellitus gestacional diagnosticada ocho años antes de la presentación y posterior diabetes mellitus tipo dos (DM2), un episodio previo de pancreatitis inducida por HTG tres años antes de la presentación, asociado con una hepatitis aguda, y obesidad con un índice de masa corporal (IMC) de 33,5 kg / m2, que se presentó con antecedentes de una semana de poliuria, polidipsia, falta de apetito y vómitos. Dos semanas antes de la presentación, fue tratada con un ciclo de cinco días de amoxicilina por una infección del tracto respiratorio. Estaba tomando metformina, glipizida y dapagliflozina para la DM2 y atorvastatina y gemfibrozil para la HTG. Había estado tomando dapagliflozina durante seis meses en el momento de la presentación. El examen físico al momento de la presentación fue significativo para la mucosa oral seca; significativamente, su examen abdominal fue benigno sin dolor a la palpación, protección o rigidez. Los hallazgos de laboratorio pertinentes al ingreso fueron: glucosa sérica 111 mg / dl, bicarbonato 18 mmol / l, anión gap 20, creatinina 0,4 mg / dl, triglicéridos 508 mg / dl, colesterol total 122 mg / dl, hemoglobina glucosilada (HbA1c) 10%. y pH venoso 7,27. La lipasa sérica fue normal a 43 U / L. Los niveles séricos de acetona no pudieron evaluarse ya que las muestras de sangre se mantuvieron hemolizadas debido a una lipemia significativa. La paciente ingresó inicialmente por cetosis por inanición, ya que refirió una ingesta oral deficiente durante los tres días previos a la admisión. Sin embargo, la química sérica obtenida seis horas después de la presentación reveló que su glucosa era de 186 mg / dL, la brecha aniónica todavía estaba elevada a 21, el bicarbonato sérico era de 16 mmol / L, el nivel de triglicéridos alcanzó un máximo de 2050 mg / dL y la lipasa fue de 52 U / L. Se obtuvo el nivel de β-hidroxibutirato y se encontró que estaba elevado a 5,29 mmol / L; la muestra original se centrifugó y la capa de quilomicrones se eliminó antes del análisis debido a la interferencia de la turbidez causada por la lipemia nuevamente. El paciente fue tratado con un goteo de insulina para euDKA y HTG con una reducción de la brecha aniónica a 13 y triglicéridos a 1400 mg / dL, dentro de las 24 horas. Se pensó que su euDKA fue precipitada por su infección del tracto respiratorio en el contexto del uso del inhibidor de SGLT2. La paciente fue atendida por el servicio de endocrinología y fue dada de alta con 40 unidades de insulina glargina por la noche, 12 unidades de insulina lispro con las comidas y metformina 1000 mg dos veces al día. Se determinó que todos los inhibidores de SGLT2 deben suspenderse indefinidamente. Tuvo un seguimiento estrecho con endocrinología post alta.'

res = p_model.transform(spark.createDataFrame(pd.DataFrame({'text': [test_sentence]})))
```

*Results* :

```bash
+----+-------------------------------+-------------+--------------+
|    | ner_chunk                     | entity      |   snomed_code|
|----+-------------------------------+-------------+--------------|
|  0 | diabetes mellitus gestacional | DIAGNOSTICO |     11687002 |
|  1 | diabetes mellitus tipo dos (  | DIAGNOSTICO |     44054006 |
|  2 | pancreatitis                  | DIAGNOSTICO |     75694006 |
|  3 | HTG                           | DIAGNOSTICO |    266569009 |
|  4 | hepatitis aguda               | DIAGNOSTICO |     37871000 |
|  5 | obesidad                      | DIAGNOSTICO |      5476005 |
|  6 | índice de masa corporal       | DIAGNOSTICO |    162859006 |
|  7 | poliuria                      | DIAGNOSTICO |     56574000 |
|  8 | polidipsia                    | DIAGNOSTICO |     17173007 |
|  9 | falta de apetito              | DIAGNOSTICO |     49233005 |
| 10 | vómitos                       | DIAGNOSTICO |    422400008 |
| 11 | infección                     | DIAGNOSTICO |     40733004 |
| 12 | HTG                           | DIAGNOSTICO |    266569009 |
| 13 | dolor                         | DIAGNOSTICO |     22253000 |
| 14 | rigidez                       | DIAGNOSTICO |    271587009 |
| 15 | cetosis                       | DIAGNOSTICO |      2538008 |
| 16 | infección                     | DIAGNOSTICO |     40733004 |
+----+-------------------------------+-------------+--------------+
```

#### New Clinical Question vs Statement BertForSequenceClassification model

+ `bert_sequence_classifier_question_statement_clinical` : This model classifies sentences into one of these two classes: question (interrogative sentence) or statement (declarative sentence) and trained with BertForSequenceClassification. This model is at first trained on SQuAD and SPAADIA dataset and then fine tuned on the clinical visit documents and MIMIC-III dataset annotated in-house. Using this model, you can find the question statements and exclude & utilize in the downstream tasks such as NER and relation extraction models.

*Example* :

```bash
documentAssembler = DocumentAssembler()\
    .setInputCol("text")\
    .setOutputCol("document")

sentenceDetector = SentenceDetectorDLModel.pretrained() \
    .setInputCols(["document"]) \
    .setOutputCol("sentence")

tokenizer = Tokenizer()\
    .setInputCols("sentence")\
    .setOutputCol("token")

seq = BertForSequenceClassification.pretrained('bert_sequence_classifier_question_statement_clinical', 'en', 'clinical/models')\
  .setInputCols(["token", "sentence"])\
  .setOutputCol("label")\
  .setCaseSensitive(True)

pipeline = Pipeline(stages = [
    documentAssembler,
    sentenceDetector,
    tokenizer,
    seq])

test_sentences = ["""Hello I am going to be having a baby throughand have just received my medical results before I have my tubes tested. I had the tests on day 23 of my cycle. My progresterone level is 10. What does this mean? What does progesterone level of 10 indicate?
Your progesterone report is perfectly normal. We expect this result on day 23rd of the cycle.So there's nothing to worry as it's perfectly alright"""]

res = p_model.transform(spark.createDataFrame(pd.DataFrame({'text': test_sentences})))
```

*Results* :

```bash
+--------------------------------------------------------------------------------------------------------------------+---------+
|sentence                                                                                                            |label    |
+--------------------------------------------------------------------------------------------------------------------+---------+
|Hello I am going to be having a baby throughand have just received my medical results before I have my tubes tested.|statement|
|I had the tests on day 23 of my cycle.                                                                              |statement|
|My progresterone level is 10.                                                                                       |statement|
|What does this mean?                                                                                                |question |
|What does progesterone level of 10 indicate?                                                                        |question |
|Your progesterone report is perfectly normal. We expect this result on day 23rd of the cycle.                       |statement|
|So there's nothing to worry as it's perfectly alright                                                               |statement|
+--------------------------------------------------------------------------------------------------------------------+---------
```

*Metrics* :
```bash
              precision    recall  f1-score   support

    question       0.97      0.94      0.96       243
   statement       0.98      0.99      0.99       729

    accuracy                           0.98       972
   macro avg       0.98      0.97      0.97       972
weighted avg       0.98      0.98      0.98       972
```

#### New Sentence Entity Resolver Fine-Tune Features (Overwriting and Drop Code)

+ `.setOverwriteExistingCode()` : This parameter provides overwriting codes over the existing codes if in pretrained Sentence Entity Resolver Model. For example, you want to add a new term to a pretrained resolver model, and if the code of term already exists in the pretrained model, when you `.setOverwriteExistingCode(True)`, it removes all the same codes and their descriptions from the model, then you will have just the new term with its code in the fine-tuned model.

+ `.setDropCodesList()` : This parameter drops list of codes from a pretrained Sentence Entity Resolver Model.

For more examples, please check [Fine-Tuning Sentence Entity Resolver Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/13.1.Finetuning_Sentence_Entity_Resolver_Model.ipynb)

#### Updated ICD10CM Entity Resolver Models

We have updated `sbiobertresolve_icd10cm_augmented` model with [ICD10CM 2022 Dataset](https://www.cdc.gov/nchs/icd/icd10cm.htm) and `sbiobertresolve_icd10cm_augmented_billable_hcc` model by dropping invalid codes.

#### Updated NER Profiling Pretrained Pipelines

We have updated `ner_profiling_clinical` and `ner_profiling_biobert` pretrained pipelines by adding new clinical NER models and NER model outputs to the previous versions. In this way, you can see all the NER labels of tokens. For examples, please check [NER Profiling Pretrained Pipeline Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/11.2.Pretrained_NER_Profiling_Pipelines.ipynb).

#### New ChunkSentenceSplitter Annotator

+ We are releasing `ChunkSentenceSplitter` annotator that splits documents or sentences by chunks provided. Splitted parts can be named with the splitting chunks. By using this annotator, you can do some some tasks like splitting clinical documents according into sections in accordance with CDA (Clinical Document Architecture).

*Example* :

```python
...
ner_converter = NerConverter() \
      .setInputCols(["document", "token", "ner"]) \
      .setOutputCol("ner_chunk")\
      .setWhiteList(["Header"])

chunkSentenceSplitter = ChunkSentenceSplitter()\
    .setInputCols("ner_chunk","document")\
    .setOutputCol("paragraphs")\
    .setGroupBySentences(True) \
    .setDefaultEntity("Intro") \
    .setInsertChunk(False)        
...

text = ["""INTRODUCTION: Right pleural effusion and suspected malignant mesothelioma.
PREOPERATIVE DIAGNOSIS:  Right pleural effusion and suspected malignant mesothelioma.
POSTOPERATIVE DIAGNOSIS: Right pleural effusion, suspected malignant mesothelioma.
PROCEDURE:  Right VATS pleurodesis and pleural biopsy."""]

results = pipeline_model.transform(df)
```

*Results* :

```bash
+----------------------------------------------------------------------+------+
|                                                                result|entity|
+----------------------------------------------------------------------+------+
|INTRODUCTION: Right pleural effusion and suspected malignant mesoth...|Header|
|PREOPERATIVE DIAGNOSIS:  Right pleural effusion and suspected malig...|Header|
|POSTOPERATIVE DIAGNOSIS: Right pleural effusion, suspected malignan...|Header|
|                 PROCEDURE:  Right VATS pleurodesis and pleural biopsy|Header|
+----------------------------------------------------------------------+------+
```

- By using `.setInsertChunk()` parameter you can remove the chunk from splitted parts.

*Example* :

```python
chunkSentenceSplitter = ChunkSentenceSplitter()\
    .setInputCols("ner_chunk","document")\
    .setOutputCol("paragraphs")\
    .setGroupBySentences(True) \
    .setDefaultEntity("Intro") \
    .setInsertChunk(False)

paragraphs = chunkSentenceSplitter.transform(results)

df = paragraphs.selectExpr("explode(paragraphs) as result")\
               .selectExpr("result.result",
                           "result.metadata.entity",
                           "result.metadata.splitter_chunk")

```

*Results* :

```bash
+--------------------------------------------------+------+------------------------+
|                                            result|entity|          splitter_chunk|
+--------------------------------------------------+------+------------------------+
| Right pleural effusion and suspected malignant...|Header|           INTRODUCTION:|
|  Right pleural effusion and suspected malignan...|Header| PREOPERATIVE DIAGNOSIS:|
| Right pleural effusion, suspected malignant me...|Header|POSTOPERATIVE DIAGNOSIS:|
|         Right VATS pleurodesis and pleural biopsy|Header|              PROCEDURE:|
+--------------------------------------------------+------+------------------------+
```


#### Updated Spark NLP For Healthcare Notebooks

- [NER Profiling Pretrained Pipeline Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/11.2.Pretrained_NER_Profiling_Pipelines.ipynb) .
- [Fine-Tuning Sentence Entity Resolver Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/13.1.Finetuning_Sentence_Entity_Resolver_Model.ipynb)


**To see more, please check : [Spark NLP Healthcare Workshop Repo](https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/tutorials/Certification_Trainings/Healthcare)**


## 3.3.1
We are glad to announce that Spark NLP Healthcare 3.3.1 has been released!.

#### Highlights
+ New ChunkKeyPhraseExtraction Annotator
+ New BERT-Based NER Models
+ New UMLS Sentence Entity Resolver Models
+ Updated RxNorm Entity Resolver Model (Dropping Invalid Codes)
+ New showVersion() Method in Compatibility Class
+ New Docker Images for Spark NLP for Healthcare and Spark OCR
+ New and Updated Deidentification() Parameters
+ New Python API Documentation
+ Updated Spark NLP For Healthcare Notebooks and New Notebooks

#### New ChunkKeyPhraseExtraction Annotator

We are releasing `ChunkKeyPhraseExtraction` annotator that leverages Sentence BERT embeddings to select keywords and key phrases that are most similar to a document. This annotator can be fed by either the output of NER model, NGramGenerator or YAKE, and could be used to generate similarity scores for each NER chunk that is coming out of any (clinical) NER model. That is, you can now sort your clinical entities by the importance of them with respect to document or sentence that they live in. Additionally, you can also use this new annotator to grab new clinical chunks that are missed by a pretrained NER model as well as summarizing the whole document into a few important sentences or phrases.

You can find more examples in [ChunkKeyPhraseExtraction notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/9.Chunk_Key_Phrase_Extraction.ipynb)

*Example* :

```bash
...
ngram_ner_key_phrase_extractor = ChunkKeyPhraseExtraction.pretrained("sbert_jsl_medium_uncased ", "en", "clinical/models")\
    .setTopN(5) \
    .setDivergence(0.4)\
    .setInputCols(["sentences", "merged_chunks"])\
    .setOutputCol("key_phrases")
...

text = "A 28-year-old female with a history of gestational diabetes mellitus diagnosed eight years prior to presentation and subsequent type two diabetes mellitus ( T2DM ), one prior episode of HTG-induced pancreatitis three years prior to presentation , associated with an acute hepatitis , and obesity with a body mass index ( BMI ) of 33.5 kg/m2 , presented with a one-week history of polyuria , polydipsia , poor appetite , and vomiting . Two weeks prior to presentation, she was treated with a five-day course of amoxicillin for a respiratory tract infection. She was on metformin , glipizide , and dapagliflozin for T2DM and atorvastatin and gemfibrozil for HTG. She had been on dapagliflozin for six months at the time of presentation . Physical examination on presentation was significant for dry oral mucosa ; significantly, her abdominal examination was benign with no tenderness , guarding , or rigidity . Pertinent laboratory findings on admission were: serum glucose 111 mg/dl , bicarbonate 18 mmol/l , anion gap 20 , creatinine 0.4 mg/dL , triglycerides 508 mg/dL , total cholesterol 122 mg/dL , glycated hemoglobin ( HbA1c ) 10% , and venous pH 7.27. Serum lipase was normal at 43 U/L . Serum acetone levels could not be assessed as blood samples kept hemolyzing due to significant lipemia ."


textDF = spark.createDataFrame([[text]]).toDF("text")
ngram_ner_results =  ngram_ner_pipeline.transform(textDF)
```

*Results* :

```bash
+--------------------------+------+-------------------+-------------------+--------+
|key_phrase                |source|DocumentSimilarity |MMRScore           |sentence|
+--------------------------+------+-------------------+-------------------+--------+
|type two diabetes mellitus|NER   |0.7639750686118073 |0.4583850593816694 |0       |
|HTG-induced pancreatitis  |ngrams|0.66933222897749   |0.10416352343367463|0       |
|vomiting                  |ngrams|0.5824238088130589 |0.14864183399720493|0       |
|history polyuria          |ngrams|0.46337313737310987|0.0959500325843913 |0       |
|28-year-old female        |ngrams|0.31692529374916967|0.10043002919664669|0       |
+--------------------------+------+-------------------+-------------------+--------+
```

#### New BERT-Based NER Models

We have two new BERT-Based token classifier NER models.

+ `bert_token_classifier_ner_chemicals` : This model is BERT-based version of `ner_chemicals` model and can detect chemical compounds (`CHEM`) in the medical texts.

*Metrics* :

```bash
              precision    recall  f1-score   support
      B-CHEM       0.94      0.92      0.93     30731
      I-CHEM       0.95      0.93      0.94     31270
    accuracy                           0.99     62001
   macro avg       0.96      0.95      0.96     62001
weighted avg       0.99      0.93      0.96     62001
```


*Example* :

```python
...
tokenClassifier = BertForTokenClassification.pretrained("bert_token_classifier_ner_chemicals", "en", "clinical/models")\
    .setInputCols("token", "document")\
    .setOutputCol("ner")\
    .setCaseSensitive(True)
...

test_sentence = """The results have shown that the product p - choloroaniline is not a significant factor in chlorhexidine - digluconate associated erosive cystitis. A high percentage of kanamycin - colistin and povidone - iodine irrigations were associated with erosive cystitis."""
result = p_model.transform(spark.createDataFrame([[test_sentence]]).toDF("text"))
```

*Results* :

```bash
+---------------------------+---------+
|chunk                      |ner_label|
+---------------------------+---------+
|p - choloroaniline         |CHEM     |
|chlorhexidine - digluconate|CHEM     |
|kanamycin                  |CHEM     |
|colistin                   |CHEM     |
|povidone - iodine          |CHEM     |
+---------------------------+---------+
```

+ `bert_token_classifier_ner_chemprot` : This model is BERT-based version of `ner_chemprot_clinical` model and can detect chemical compounds and genes (`CHEMICAL`, `GENE-Y`, `GENE-N`) in the medical texts.

*Metrics* :

```bash
              precision    recall  f1-score   support
  B-CHEMICAL       0.80      0.79      0.80      8649
    B-GENE-N       0.53      0.56      0.54      2752
    B-GENE-Y       0.71      0.73      0.72      5490
  I-CHEMICAL       0.82      0.79      0.81      1313
    I-GENE-N       0.62      0.62      0.62      1993
    I-GENE-Y       0.75      0.72      0.74      2420
    accuracy                           0.96     22617
   macro avg       0.75      0.74      0.75     22617
weighted avg       0.83      0.73      0.78     22617
```

*Example* :

```bash
...
tokenClassifier = BertForTokenClassification.pretrained("bert_token_classifier_ner_chemprot", "en", "clinical/models")\
    .setInputCols("token", "document")\
    .setOutputCol("ner")\
    .setCaseSensitive(True)
...

test_sentence = "Keratinocyte growth factor and acidic fibroblast growth factor are mitogens for primary cultures of mammary epithelium."
result = p_model.transform(spark.createDataFrame([[test_sentence]]).toDF("text"))
```

*Results* :

```bash
+-------------------------------+---------+
|chunk                          |ner_label|
+-------------------------------+---------+
|Keratinocyte growth factor     |GENE-Y   |
|acidic fibroblast growth factor|GENE-Y   |
+-------------------------------+---------+
```


#### New UMLS Sentence Entity Resolver Models

We are releasing two new UMLS Sentence Entity Resolver models trained on 2021AB UMLS dataset and map clinical entities to UMLS CUI codes.

+ `sbiobertresolve_umls_disease_syndrome` : This model is trained on the `Disease` or `Syndrome` category using `sbiobert_base_cased_mli` embeddings.

*Example* :

```bash
...
resolver = SentenceEntityResolverModel.pretrained("sbiobertresolve_umls_disease_syndrome","en", "clinical/models") \
     .setInputCols(["ner_chunk", "sbert_embeddings"]) \
     .setOutputCol("resolution")\
     .setDistanceFunction("EUCLIDEAN")
...

data = spark.createDataFrame([["""A 28-year-old female with a history of gestational diabetes mellitus diagnosed eight years prior to presentation and subsequent type two diabetes mellitus (T2DM), one prior episode of HTG-induced pancreatitis three years prior to presentation, associated with an acute hepatitis, and obesity with a body mass index (BMI) of 33.5 kg/m2, presented with a one-week history of polyuria, polydipsia, poor appetite, and vomiting."""]]).toDF("text")
results = model.fit(data).transform(data)

```

*Results* :

```bash
|    | chunk                                 | code     | code_description                      | all_k_codes                                                  | all_k_codes_desc                                                                                                                                                                                         |
|---:|:--------------------------------------|:---------|:--------------------------------------|:-------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|  0 | gestational diabetes mellitus         | C0085207 | gestational diabetes mellitus         | ['C0085207', 'C0032969', 'C2063017', 'C1283034', 'C0271663'] | ['gestational diabetes mellitus', 'pregnancy diabetes mellitus', 'pregnancy complicated by diabetes mellitus', 'maternal diabetes mellitus', 'gestational diabetes mellitus, a2']                        |
|  1 | subsequent type two diabetes mellitus | C0348921 | pre-existing type 2 diabetes mellitus | ['C0348921', 'C1719939', 'C0011860', 'C0877302', 'C0271640'] | ['pre-existing type 2 diabetes mellitus', 'disorder associated with type 2 diabetes mellitus', 'diabetes mellitus, type 2', 'insulin-requiring type 2 diabetes mellitus', 'secondary diabetes mellitus'] |
|  2 | HTG-induced pancreatitis              | C0376670 | alcohol-induced pancreatitis          | ['C0376670', 'C1868971', 'C4302243', 'C0267940', 'C2350449'] | ['alcohol-induced pancreatitis', 'toxic pancreatitis', 'igg4-related pancreatitis', 'hemorrhage pancreatitis', 'graft pancreatitis']                                                                     |
|  3 | an acute hepatitis                    | C0019159 | acute hepatitis                       | ['C0019159', 'C0276434', 'C0267797', 'C1386146', 'C2063407'] | ['acute hepatitis a', 'acute hepatitis a', 'acute hepatitis', 'acute infectious hepatitis', 'acute hepatitis e']                                                                                         |
|  4 | obesity                               | C0028754 | obesity                               | ['C0028754', 'C0342940', 'C0342942', 'C0857116', 'C1561826'] | ['obesity', 'abdominal obesity', 'generalized obesity', 'obesity gross', 'overweight and obesity']                                                                                                       |
|  5 | polyuria                              | C0018965 | hematuria                             | ['C0018965', 'C0151582', 'C3888890', 'C0268556', 'C2936921'] | ['hematuria', 'uricosuria', 'polyuria-polydipsia syndrome', 'saccharopinuria', 'saccharopinuria']                                                                                                        |
|  6 | polydipsia                            | C0268813 | primary polydipsia                    | ['C0268813', 'C0030508', 'C3888890', 'C0393777', 'C0206085'] | ['primary polydipsia', 'parasomnia', 'polyuria-polydipsia syndrome', 'hypnogenic paroxysmal dystonias', 'periodic hypersomnias']                                                                         |
|  7 | poor appetite                         | C0003123 | lack of appetite                      | ['C0003123', 'C0011168', 'C0162429', 'C1282895', 'C0039338'] | ['lack of appetite', 'poor swallowing', 'poor nutrition', 'neurologic unpleasant taste', 'taste dis']                                                                                                    |
|  8 | vomiting                              | C0152164 | periodic vomiting                     | ['C0152164', 'C0267172', 'C0152517', 'C0011119', 'C0152227'] | ['periodic vomiting', 'habit vomiting', 'viral vomiting', 'choking', 'tearing']                                                                                                                          |
```

+ `sbiobertresolve_umls_clinical_drugs` : This model is trained on the `Clinical Drug` category using `sbiobert_base_cased_mli` embeddings.

*Example* :

```bash
...
resolver = SentenceEntityResolverModel.pretrained("sbiobertresolve_umls_clinical_drugs","en", "clinical/models") \
     .setInputCols(["ner_chunk", "sbert_embeddings"]) \
     .setOutputCol("resolution")\
     .setDistanceFunction("EUCLIDEAN")
...

data = spark.createDataFrame([["""She was immediately given hydrogen peroxide 30 mg to treat the infection on her leg, and has been advised Neosporin Cream for 5 days. She has a history of taking magnesium hydroxide 100mg/1ml and metformin 1000 mg."""]]).toDF("text")
results = model.fit(data).transform(data)

```

*Results* :

```bash
|    | chunk                         | code     | code_description           | all_k_codes                                                  | all_k_codes_desc                                                                                                                                                                        |
|---:|:------------------------------|:---------|:---------------------------|:-------------------------------------------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|  0 | hydrogen peroxide 30 mg       | C1126248 | hydrogen peroxide 30 mg/ml | ['C1126248', 'C0304655', 'C1605252', 'C0304656', 'C1154260'] | ['hydrogen peroxide 30 mg/ml', 'hydrogen peroxide solution 30%', 'hydrogen peroxide 30 mg/ml [proxacol]', 'hydrogen peroxide 30 mg/ml cutaneous solution', 'benzoyl peroxide 30 mg/ml'] |
|  1 | Neosporin Cream               | C0132149 | neosporin cream            | ['C0132149', 'C0358174', 'C0357999', 'C0307085', 'C0698810'] | ['neosporin cream', 'nystan cream', 'nystadermal cream', 'nupercainal cream', 'nystaform cream']                                                                                        |
|  2 | magnesium hydroxide 100mg/1ml | C1134402 | magnesium hydroxide 100 mg | ['C1134402', 'C1126785', 'C4317023', 'C4051486', 'C4047137'] | ['magnesium hydroxide 100 mg', 'magnesium hydroxide 100 mg/ml', 'magnesium sulphate 100mg/ml injection', 'magnesium sulfate 100 mg', 'magnesium sulfate 100 mg/ml']                     |
|  3 | metformin 1000 mg             | C0987664 | metformin 1000 mg          | ['C0987664', 'C2719784', 'C0978482', 'C2719786', 'C4282269'] | ['metformin 1000 mg', 'metformin hydrochloride 1000 mg', 'metformin hcl 1000mg tab', 'metformin hydrochloride 1000 mg [fortamet]', 'metformin hcl 1000mg sa tab']                       |

```

#### Updated RxNorm Entity Resolver Model (Dropping Invalid Codes)

`sbiobertresolve_rxnorm` model was updated by dropping invalid codes using 02 August 2021 RxNorm dataset.

#### New showVersion() Method in Compatibility Class

We added the `.showVersion()` method in our Compatibility class that shows the name of the models and the version in a pretty way.

```python
compatibility = Compatibility()
compatibility.showVersion('sentence_detector_dl_healthcare')
```
After the execution you will see the following table,

```bash
+---------------------------------+------+---------+
| Pipeline/Model                  | lang | version |
+---------------------------------+------+---------+
| sentence_detector_dl_healthcare |  en  | 2.6.0   |
| sentence_detector_dl_healthcare |  en  | 2.7.0   |
| sentence_detector_dl_healthcare |  en  | 3.2.0   |
+---------------------------------+------+---------+
```

#### New Docker Images for Spark NLP for Healthcare and Spark OCR

We are releasing new Docker Images for Spark NLP for Healthcare and Spark OCR containing a jupyter environment. Users having a valid license can run the image on their local system, and connect to pre-configured jupyter instance without installing the library on their local system.

**Spark NLP for Healthcare Docker Image**

For running Spark NLP for Healthcare inside a container:

- Instructions: [Spark NLP for Healthcare Docker Image](https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/jupyter/docker_image_nlp_hc)

- Video Instructions: [Youtube Video](https://www.youtube.com/watch?v=tgN0GZGMVJk)

**Spark NLP for Healthcare & OCR Docker Image**

For users who want to run Spark OCR and then feed the output of OCR pipeline to healthcare modules to process further:

- Instructions: [Spark NLP for Healthcare & OCR Docker Image](https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/jupyter/docker_image_ocr)

#### New and Updated Deidentification() Parameters

*New Parameter* :
+ `setBlackList()` : List of entities **ignored** for masking or obfuscation.The default values are: `SSN`, `PASSPORT`, `DLN`, `NPI`, `C_CARD`, `IBAN`, `DEA`.

*Updated Parameter* :

+ `.setObfuscateRefSource()` : It was set `faker` as default.

#### New Python API Documentation

We have new Spark NLP for Healthcare [Python API Documentation](https://nlp.johnsnowlabs.com/licensed/api/python/) . This page contains information how to use the library with Python examples.

#### Updated Spark NLP For Healthcare Notebooks and New Notebooks

- New [BertForTokenClassification NER Model Training with Transformers Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/1.6.BertForTokenClassification_NER_SparkNLP_with_Transformers.ipynb) for showing how to train a BertForTokenClassification NER model with transformers and then import into Spark NLP.

- New [ChunkKeyPhraseExtraction notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/9.Chunk_Key_Phrase_Extraction.ipynb) for showing how to get chunk key phrases using `ChunkKeyPhraseExtraction`.

- Updated all [Spark NLP For Healthcare Notebooks](https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/tutorials/Certification_Trainings/Healthcare) with v3.3.0 by adding the new features.



**To see more, please check : [Spark NLP Healthcare Workshop Repo](https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/tutorials/Certification_Trainings/Healthcare)**


## 3.3.0
We are glad to announce that Spark NLP Healthcare 3.3.0 has been released!.

#### Highlights
+ NER Finder Pretrained Pipelines to Run Run 48 different Clinical NER and 21 Different Biobert Models At Once Over the Input Text
+ 3 New Sentence Entity Resolver Models (3-char ICD10CM, RxNorm_NDC, HCPCS)
+ Updated UMLS Entity Resolvers (Dropping Invalid Codes)
+ 5 New Clinical NER Models (Trained By BertForTokenClassification Approach)
+ Radiology NER Model Trained On cheXpert Dataset
+ New Speed Benchmarks on Databricks
+ NerConverterInternal Fixes
+ Simplified Setup and Recommended Use of start() Function
+ NER Evaluation Metrics Fix
+ New Notebooks (Including How to Use SparkNLP with Neo4J)

#### NER Finder Pretrained Pipelines to Run Run 48 different Clinical NER and 21 Different Biobert Models At Once Over the Input Text

We are releasing two new NER Pretrained Pipelines that can be used to explore all the available pretrained NER models at once. You can check [NER Profiling Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/11.2.Pretrained_NER_Profiling_Pipelines.ipynb) to see how to use these pretrained pipelines.

- `ner_profiling_clinical` : When you run this pipeline over your text, you will end up with the predictions coming out of each of the 48 pretrained clinical NER models trained with `embeddings_clinical`.

|Clinical NER Model List|
|-|
|ner_ade_clinical|
|ner_posology_greedy|
|ner_risk_factors|
|jsl_ner_wip_clinical|
|ner_human_phenotype_gene_clinical|
|jsl_ner_wip_greedy_clinical|
|ner_cellular|
|ner_cancer_genetics|
|jsl_ner_wip_modifier_clinical|
|ner_drugs_greedy|
|ner_deid_sd_large|
|ner_diseases|
|nerdl_tumour_demo|
|ner_deid_subentity_augmented|
|ner_jsl_enriched|
|ner_genetic_variants|
|ner_bionlp|
|ner_measurements_clinical|
|ner_diseases_large|
|ner_radiology|
|ner_deid_augmented|
|ner_anatomy|
|ner_chemprot_clinical|
|ner_posology_experimental|
|ner_drugs|
|ner_deid_sd|
|ner_posology_large|
|ner_deid_large|
|ner_posology|
|ner_deidentify_dl|
|ner_deid_enriched|
|ner_bacterial_species|
|ner_drugs_large|
|ner_clinical_large|
|jsl_rd_ner_wip_greedy_clinical|
|ner_medmentions_coarse|
|ner_radiology_wip_clinical|
|ner_clinical|
|ner_chemicals|
|ner_deid_synthetic|
|ner_events_clinical|
|ner_posology_small|
|ner_anatomy_coarse|
|ner_human_phenotype_go_clinical|
|ner_jsl_slim|
|ner_jsl|
|ner_jsl_greedy|
|ner_events_admission_clinical|


- `ner_profiling_biobert` : When you run this pipeline over your text, you will end up with the predictions coming out of each of the 21 pretrained clinical NER models trained with `biobert_pubmed_base_cased`.

|BioBert NER Model List|
|-|
|ner_cellular_biobert|
|ner_diseases_biobert|
|ner_events_biobert|
|ner_bionlp_biobert|
|ner_jsl_greedy_biobert|
|ner_jsl_biobert|
|ner_anatomy_biobert|
|ner_jsl_enriched_biobert|
|ner_human_phenotype_go_biobert|
|ner_deid_biobert|
|ner_deid_enriched_biobert|
|ner_clinical_biobert|
|ner_anatomy_coarse_biobert|
|ner_human_phenotype_gene_biobert|
|ner_posology_large_biobert|
|jsl_rd_ner_wip_greedy_biobert|
|ner_posology_biobert|
|jsl_ner_wip_greedy_biobert|
|ner_chemprot_biobert|
|ner_ade_biobert|
|ner_risk_factors_biobert|

You can also check [Models Hub](https://nlp.johnsnowlabs.com/models) page for more information about all these NER models and more.

*Example* :

```
from sparknlp.pretrained import PretrainedPipeline
ner_profiling_pipeline = PretrainedPipeline('ner_profiling_biobert', 'en', 'clinical/models')

result = ner_profiling_pipeline.annotate("A 28-year-old female with a history of gestational diabetes mellitus diagnosed eight years prior to presentation and subsequent type two diabetes mellitus ( T2DM ), one prior episode of HTG-induced pancreatitis three years prior to presentation , associated with an acute hepatitis , and obesity with a body mass index ( BMI ) of 33.5 kg/m2 , presented with a one-week history of polyuria , polydipsia , poor appetite , and vomiting .")
```

*Results* :

```bash
sentence :  ['A 28-year-old female with a history of gestational diabetes mellitus diagnosed eight years prior to presentation and subsequent type two diabetes mellitus ( T2DM ), one prior episode of HTG-induced pancreatitis three years prior to presentation , associated with an acute hepatitis , and obesity with a body mass index ( BMI ) of 33.5 kg/m2 , presented with a one-week history of polyuria , polydipsia , poor appetite , and vomiting .']
token :  ['A', '28-year-old', 'female', 'with', 'a', 'history', 'of', 'gestational', 'diabetes', 'mellitus', 'diagnosed', 'eight', 'years', 'prior', 'to', 'presentation', 'and', 'subsequent', 'type', 'two', 'diabetes', 'mellitus', '(', 'T2DM', '),', 'one', 'prior', 'episode', 'of', 'HTG-induced', 'pancreatitis', 'three', 'years', 'prior', 'to', 'presentation', ',', 'associated', 'with', 'an', 'acute', 'hepatitis', ',', 'and', 'obesity', 'with', 'a', 'body', 'mass', 'index', '(', 'BMI', ')', 'of', '33.5', 'kg/m2', ',', 'presented', 'with', 'a', 'one-week', 'history', 'of', 'polyuria', ',', 'polydipsia', ',', 'poor', 'appetite', ',', 'and', 'vomiting', '.']
ner_cellular_biobert_chunks :  []
ner_diseases_biobert_chunks :  ['gestational diabetes mellitus', 'type two diabetes mellitus', 'T2DM', 'HTG-induced pancreatitis', 'hepatitis', 'obesity', 'polyuria', 'polydipsia', 'poor appetite', 'vomiting']
ner_events_biobert_chunks :  ['gestational diabetes mellitus', 'eight years', 'presentation', 'type two diabetes mellitus ( T2DM', 'HTG-induced pancreatitis', 'three years', 'presentation', 'an acute hepatitis', 'obesity', 'a body mass index', 'BMI', 'presented', 'a one-week', 'polyuria', 'polydipsia', 'poor appetite', 'vomiting']
ner_bionlp_biobert_chunks :  []
ner_jsl_greedy_biobert_chunks :  ['28-year-old', 'female', 'gestational diabetes mellitus', 'eight years prior', 'type two diabetes mellitus', 'T2DM', 'HTG-induced pancreatitis', 'three years prior', 'acute hepatitis', 'obesity', 'body mass index', 'BMI ) of 33.5 kg/m2', 'one-week', 'polyuria', 'polydipsia', 'poor appetite', 'vomiting']
ner_jsl_biobert_chunks :  ['28-year-old', 'female', 'gestational diabetes mellitus', 'eight years prior', 'type two diabetes mellitus', 'T2DM', 'HTG-induced pancreatitis', 'three years prior', 'acute', 'hepatitis', 'obesity', 'body mass index', 'BMI ) of 33.5 kg/m2', 'one-week', 'polyuria', 'polydipsia', 'poor appetite', 'vomiting']
ner_anatomy_biobert_chunks :  ['body']
ner_jsl_enriched_biobert_chunks :  ['28-year-old', 'female', 'gestational diabetes mellitus', 'type two diabetes mellitus', 'T2DM', 'HTG-induced pancreatitis', 'acute', 'hepatitis', 'obesity', 'polyuria', 'polydipsia', 'poor appetite', 'vomiting']
ner_human_phenotype_go_biobert_chunks :  ['obesity', 'polyuria', 'polydipsia']
ner_deid_biobert_chunks :  ['eight years', 'three years']
ner_deid_enriched_biobert_chunks :  []
ner_clinical_biobert_chunks :  ['gestational diabetes mellitus', 'subsequent type two diabetes mellitus ( T2DM', 'HTG-induced pancreatitis', 'an acute hepatitis', 'obesity', 'a body mass index ( BMI )', 'polyuria', 'polydipsia', 'poor appetite', 'vomiting']
ner_anatomy_coarse_biobert_chunks :  ['body']
ner_human_phenotype_gene_biobert_chunks :  ['obesity', 'mass', 'polyuria', 'polydipsia', 'vomiting']
ner_posology_large_biobert_chunks :  []
jsl_rd_ner_wip_greedy_biobert_chunks :  ['gestational diabetes mellitus', 'type two diabetes mellitus', 'T2DM', 'HTG-induced pancreatitis', 'acute hepatitis', 'obesity', 'body mass index', '33.5', 'kg/m2', 'polyuria', 'polydipsia', 'poor appetite', 'vomiting']
ner_posology_biobert_chunks :  []
jsl_ner_wip_greedy_biobert_chunks :  ['28-year-old', 'female', 'gestational diabetes mellitus', 'eight years prior', 'type two diabetes mellitus', 'T2DM', 'HTG-induced pancreatitis', 'three years prior', 'acute hepatitis', 'obesity', 'body mass index', 'BMI ) of 33.5 kg/m2', 'one-week', 'polyuria', 'polydipsia', 'poor appetite', 'vomiting']
ner_chemprot_biobert_chunks :  []
ner_ade_biobert_chunks :  ['pancreatitis', 'acute hepatitis', 'polyuria', 'polydipsia', 'poor appetite', 'vomiting']
ner_risk_factors_biobert_chunks :  ['diabetes mellitus', 'subsequent type two diabetes mellitus', 'obesity']
```

#### 3 New Sentence Entity Resolver Models (3-char ICD10CM, RxNorm_NDC, HCPCS)

+ `sbiobertresolve_hcpcs` : This model maps extracted medical entities to [Healthcare Common Procedure Coding System (HCPCS)](https://www.nlm.nih.gov/research/umls/sourcereleasedocs/current/HCPCS/index.html#:~:text=The%20Healthcare%20Common%20Procedure%20Coding,%2C%20supplies%2C%20products%20and%20services.)
 codes using `sbiobert_base_cased_mli` sentence embeddings. It also returns the domain information of the codes in the `all_k_aux_labels` parameter in the metadata of the result.

*Example* :

```bash
documentAssembler = DocumentAssembler()\
      .setInputCol("text")\
      .setOutputCol("ner_chunk")
sbert_embedder = BertSentenceEmbeddings.pretrained('sbiobert_base_cased_mli', 'en','clinical/models')\
      .setInputCols(["ner_chunk"])\
      .setOutputCol("sentence_embeddings")

hcpcs_resolver = SentenceEntityResolverModel.pretrained("sbiobertresolve_hcpcs", "en", "clinical/models") \
      .setInputCols(["ner_chunk", "sentence_embeddings"]) \
      .setOutputCol("hcpcs_code")\
      .setDistanceFunction("EUCLIDEAN")
hcpcs_pipelineModel = PipelineModel(
    stages = [
        documentAssembler,
        sbert_embedder,
        hcpcs_resolver])

res = hcpcs_pipelineModel.transform(spark.createDataFrame([["Breast prosthesis, mastectomy bra, with integrated breast prosthesis form, unilateral, any size, any type"]]).toDF("text"))
```

*Results* :

|ner_chunk|hcpcs_code|all_codes|all_resolutions|domain|
|-|-|-|-|-|
| Breast prosthesis, mastectomy bra, with integrated breast prosthesis form, unilateral, any size, any type  | L8001  |[L8001, L8002, L8000, L8033, L8032, ...]   |'Breast prosthesis, mastectomy bra, with integrated breast prosthesis form, unilateral, any size, any type', 'Breast prosthesis, mastectomy bra, with integrated breast prosthesis form, bilateral, any size, any type', 'Breast prosthesis, mastectomy bra, without integrated breast prosthesis form, any size, any type', 'Nipple prosthesis, custom fabricated, reusable, any material, any type, each', ...  | Device, Device, Device, Device, Device, ...  |


+ `sbiobertresolve_icd10cm_generalised` : This model maps medical entities to 3 digit ICD10CM codes (according to ICD10 code structure the first three characters represent general type of the injury or disease). Difference in results (compared with `sbiobertresolve_icd10cm`) can be observed in the example below.

*Example* :

```bash
documentAssembler = DocumentAssembler()\
      .setInputCol("text")\
      .setOutputCol("ner_chunk")
sbert_embedder = BertSentenceEmbeddings.pretrained('sbiobert_base_cased_mli', 'en','clinical/models')\
      .setInputCols(["ner_chunk"])\
      .setOutputCol("sentence_embeddings")

icd_resolver = SentenceEntityResolverModel.pretrained("sbiobertresolve_icd10cm_generalised", "en", "clinical/models") \
      .setInputCols(["ner_chunk", "sentence_embeddings"]) \
      .setOutputCol("icd_code")\
      .setDistanceFunction("EUCLIDEAN")

icd_pipelineModel = PipelineModel(
    stages = [
        documentAssembler,
        sbert_embedder,
        icd_resolver])

res = icd_pipelineModel.transform(spark.createDataFrame([["82 - year-old male with a history of hypertension , chronic renal insufficiency , COPD , and gastritis"]]).toDF("text"))
```

*Results* :
```bash
|    | chunk                       | entity  | code_3char | code_desc_3char                               | code_full | code_full_description                     |  distance | all_k_resolutions_3char                                                    | all_k_codes_3char                              |
|---:|:----------------------------|:--------|:-----------|:----------------------------------------------|:----------|:------------------------------------------|----------:|:---------------------------------------------------------------------------|:-----------------------------------------------|
|  0 | hypertension                | SYMPTOM | I10        | hypertension                                  | I150      | Renovascular hypertension                 |    0      | [hypertension, hypertension (high blood pressure), h/o: hypertension, ...] | [I10, I15, Z86, Z82, I11, R03, Z87, E27]       |
|  1 | chronic renal insufficiency | SYMPTOM | N18        | chronic renal impairment                      | N186      | End stage renal disease                   |    0.014  | [chronic renal impairment, renal insufficiency, renal failure, anaemi ...] | [N18, P96, N19, D63, N28, Z87, N17, N25, R94]  |
|  2 | COPD                        | SYMPTOM | J44        | chronic obstructive lung disease (disorder)   | I2781     | Cor pulmonale (chronic)                   |    0.1197 | [chronic obstructive lung disease (disorder), chronic obstructive pul ...] | [J44, Z76, J81, J96, R06, I27, Z87]            |
|  3 | gastritis                   | SYMPTOM | K29        | gastritis                                     | K5281     | Eosinophilic gastritis or gastroenteritis |    0      | gastritis:::bacterial gastritis:::parasitic gastritis                      | [K29, B96, K93]                                |
```

+ `sbiobertresolve_rxnorm_ndc` : This model maps `DRUG` entities to rxnorm codes and their [National Drug Codes (NDC)](https://www.drugs.com/ndc.html#:~:text=The%20NDC%2C%20or%20National%20Drug,and%20the%20commercial%20package%20size.)
 using `sbiobert_base_cased_mli` sentence embeddings. You can find all NDC codes of drugs seperated by `|` in the `all_k_aux_labels` parameter of the metadata.

*Example* :

```bash
documentAssembler = DocumentAssembler()\
      .setInputCol("text")\
      .setOutputCol("ner_chunk")

sbert_embedder = BertSentenceEmbeddings.pretrained('sbiobert_base_cased_mli', 'en','clinical/models')\
      .setInputCols(["ner_chunk"])\
      .setOutputCol("sentence_embeddings")

rxnorm_ndc_resolver = SentenceEntityResolverModel.pretrained("sbiobertresolve_rxnorm_ndc", "en", "clinical/models") \
      .setInputCols(["ner_chunk", "sentence_embeddings"]) \
      .setOutputCol("rxnorm_code")\
      .setDistanceFunction("EUCLIDEAN")

rxnorm_ndc_pipelineModel = PipelineModel(
    stages = [
        documentAssembler,
        sbert_embedder,
        rxnorm_ndc_resolver])

res = rxnorm_ndc_pipelineModel.transform(spark.createDataFrame([["activated charcoal 30000 mg powder for oral suspension"]]).toDF("text"))
```

*Results* :

|chunk|rxnorm_code|all_codes|resolutions|all_k_aux_labels|all_distances|
|-|-|-|-|-|-|
|activated charcoal 30000 mg powder for oral suspension|1440919|1440919, 808917, 1088194, 1191772, 808921,...|activated charcoal 30000 MG Powder for Oral Suspension, Activated Charcoal 30000 MG Powder for Oral Suspension, wheat dextrin 3000 MG Powder for Oral Solution [Benefiber], cellulose 3000 MG Oral Powder [Unifiber], fosfomycin 3000 MG Powder for Oral Solution [Monurol] ...|69784030828, 00395052791, 08679001362\|86790016280\|00067004490, 46017004408\|68220004416, 00456430001,...|0.0000, 0.0000, 0.1128, 0.1148, 0.1201,...|

#### Updated UMLS Entity Resolvers (Dropping Invalid Codes)

UMLS model `sbiobertresolve_umls_findings` and `sbiobertresolve_umls_major_concepts` were updated by dropping the invalid codes using the [latest UMLS release](
https://www.nlm.nih.gov/pubs/techbull/mj21/mj21_umls_2021aa_release.html) done May 2021.  

#### 5 New Clinical NER Models (Trained By BertForTokenClassification Approach)

We are releasing four new BERT-based NER models.

+ `bert_token_classifier_ner_ade` : This model is BERT-Based version of `ner_ade_clinical` model and performs 5% better. It can detect drugs and adverse reactions of drugs in reviews, tweets, and medical texts using `DRUG` and `ADE` labels.

*Example* :

```bash
...
tokenClassifier = BertForTokenClassification.pretrained("bert_token_classifier_ner_ade", "en", "clinical/models")\
    .setInputCols("token", "document")\
    .setOutputCol("ner")\
    .setCaseSensitive(True)

ner_converter = NerConverter()\
        .setInputCols(["document","token","ner"])\
        .setOutputCol("ner_chunk")

pipeline =  Pipeline(stages=[documentAssembler, tokenizer, tokenClassifier, ner_converter])
p_model = pipeline.fit(spark.createDataFrame(pd.DataFrame({'text': ['']})))

test_sentence = """Been taking Lipitor for 15 years , have experienced severe fatigue a lot!!! . Doctor moved me to voltaren 2 months ago , so far , have only experienced cramps"""
result = p_model.transform(spark.createDataFrame(pd.DataFrame({'text': [test_sentence]})))
```

*Results* :

```bash
+--------------+---------+
|chunk         |ner_label|
+--------------+---------+
|Lipitor       |DRUG     |
|severe fatigue|ADE      |
|voltaren      |DRUG     |
|cramps        |ADE      |
+--------------+---------+
```

+ `bert_token_classifier_ner_jsl_slim` : This model is BERT-Based version of `ner_jsl_slim` model and 2% better than the legacy NER model (MedicalNerModel) that is based on BiLSTM-CNN-Char architecture. It can detect `Death_Entity`, `Medical_Device`, `Vital_Sign`, `Alergen`, `Drug`, `Clinical_Dept`, `Lifestyle`, `Symptom`, `Body_Part`, `Physical_Measurement`, `Admission_Discharge`, `Date_Time`, `Age`, `Birth_Entity`, `Header`, `Oncological`, `Substance_Quantity`, `Test_Result`, `Test`, `Procedure`, `Treatment`, `Disease_Syndrome_Disorder`, `Pregnancy_Newborn`, `Demographics` entities.

*Example* :

```bash
...
tokenClassifier = BertForTokenClassification.pretrained("bert_token_classifier_ner_jsl_slim", "en", "clinical/models")\
    .setInputCols("token", "document")\
    .setOutputCol("ner")\
    .setCaseSensitive(True)

ner_converter = NerConverter()\
    .setInputCols(["sentence","token","ner"])\
    .setOutputCol("ner_chunk")

pipeline = Pipeline(stages=[documentAssembler, sentence_detector, tokenizer, tokenClassifier, ner_converter])
p_model = pipeline.fit(spark.createDataFrame(pd.DataFrame({'text': ['']})))

test_sentence = """HISTORY: 30-year-old female presents for digital bilateral mammography secondary to a soft tissue lump palpated by the patient in the upper right shoulder. The patient has a family history of breast cancer within her mother at age 58. Patient denies personal history of breast cancer."""
result = p_model.transform(spark.createDataFrame(pd.DataFrame({'text': [test_sentence]})))
```

*Results* :

```bash
+----------------+------------+
|chunk           |ner_label   |
+----------------+------------+
|HISTORY:        |Header      |
|30-year-old     |Age         |
|female          |Demographics|
|mammography     |Test        |
|soft tissue lump|Symptom     |
|shoulder        |Body_Part   |
|breast cancer   |Oncological |
|her mother      |Demographics|
|age 58          |Age         |
|breast cancer   |Oncological |
+----------------+------------+
```

+ `bert_token_classifier_ner_drugs` : This model is BERT-based version of `ner_drugs` model and detects drug chemicals. This new model is 3% better than the legacy NER model (MedicalNerModel) that is based on BiLSTM-CNN-Char architecture.


*Example* :

```bash
...
tokenClassifier = BertForTokenClassification.pretrained("bert_token_classifier_ner_drugs", "en", "clinical/models")\
  .setInputCols("token", "sentence")\
  .setOutputCol("ner")\
  .setCaseSensitive(True)

ner_converter = NerConverter()\
        .setInputCols(["sentence","token","ner"])\
        .setOutputCol("ner_chunk")

pipeline =  Pipeline(stages=[documentAssembler, sentenceDetector, tokenizer, tokenClassifier, ner_converter])
model = pipeline.fit(spark.createDataFrame(pd.DataFrame({'text': ['']})))

test_sentence = """The human KCNJ9 (Kir 3.3, GIRK3) is a member of the G-protein-activated inwardly rectifying potassium (GIRK) channel family. Here we describe the genomicorganization of the KCNJ9 locus on chromosome 1q21-23 as a candidate gene forType II diabetes mellitus in the Pima Indian population. The gene spansapproximately 7.6 kb and contains one noncoding and two coding exons separated byapproximately 2.2 and approximately 2.6 kb introns, respectively. We identified14 single nucleotide polymorphisms (SNPs), including one that predicts aVal366Ala substitution, and an 8 base-pair (bp) insertion/deletion. Ourexpression studies revealed the presence of the transcript in various humantissues including pancreas, and two major insulin-responsive tissues: fat andskeletal muscle. The characterization of the KCNJ9 gene should facilitate furtherstudies on the function of the KCNJ9 protein and allow evaluation of thepotential role of the locus in Type II diabetes.BACKGROUND: At present, it is one of the most important issues for the treatment of breast cancer to develop the standard therapy for patients previously treated with anthracyclines and taxanes. With the objective of determining the usefulnessof vinorelbine monotherapy in patients with advanced or recurrent breast cancerafter standard therapy, we evaluated the efficacy and safety of vinorelbine inpatients previously treated with anthracyclines and taxanes."""
result = model.transform(spark.createDataFrame(pd.DataFrame({'text': [test_sentence]})))
```
*Results* :

```bash
+--------------+---------+
|chunk         |ner_label|
+--------------+---------+
|potassium     |DrugChem |
|nucleotide    |DrugChem |
|anthracyclines|DrugChem |
|taxanes       |DrugChem |
|vinorelbine   |DrugChem |
|vinorelbine   |DrugChem |
|anthracyclines|DrugChem |
|taxanes       |DrugChem |
+--------------+---------+
```

+ `bert_token_classifier_ner_anatomy` : This model is BERT-Based version of `ner_anatomy` model and 3% better. It can detect `Anatomical_system`, `Cell`, `Cellular_component`, `Developing_anatomical_structure`, `Immaterial_anatomical_entity`, `Multi-tissue_structure`, `Organ`, `Organism_subdivision`, `Organism_substance`, `Pathological_formation`, `Tissue` entities.

*Example* :

```bash
...
tokenClassifier = BertForTokenClassification.pretrained("bert_token_classifier_ner_anatomy", "en", "clinical/models")\
    .setInputCols("token", "sentence")\
    .setOutputCol("ner")\
    .setCaseSensitive(True)

ner_converter = NerConverter()\
    .setInputCols(["sentence","token","ner"])\
    .setOutputCol("ner_chunk")

pipeline =  Pipeline(stages=[documentAssembler, sentenceDetector, tokenizer, tokenClassifier, ner_converter])
pp_model = pipeline.fit(spark.createDataFrame(pd.DataFrame({'text': ['']})))

test_sentence = """This is an 11-year-old female who comes in for two different things. 1. She was seen by the allergist. No allergies present, so she stopped her Allegra, but she is still real congested and does a lot of snorting. They do not notice a lot of snoring at night though, but she seems to be always like that. 2. On her right great toe, she has got some redness and erythema. Her skin is kind of peeling a little bit, but it has been like that for about a week and a half now.\nGeneral: Well-developed female, in no acute distress, afebrile.\nHEENT: Sclerae and conjunctivae clear. Extraocular muscles intact. TMs clear. Nares patent. A little bit of swelling of the turbinates on the left. Oropharynx is essentially clear. Mucous membranes are moist.\nNeck: No lymphadenopathy.\nChest: Clear.\nAbdomen: Positive bowel sounds and soft.\nDermatologic: She has got redness along her right great toe, but no bleeding or oozing. Some dryness of her skin. Her toenails themselves are very short and even on her left foot and her left great toe the toenails are very short."""
result = pp_model.transform(spark.createDataFrame(pd.DataFrame({'text': [test_sentence]})))
```

*Results* :

```bash
+-------------------+----------------------+
|chunk              |ner_label             |
+-------------------+----------------------+
|great toe          |Multi-tissue_structure|
|skin               |Organ                 |
|conjunctivae       |Multi-tissue_structure|
|Extraocular muscles|Multi-tissue_structure|
|Nares              |Multi-tissue_structure|
|turbinates         |Multi-tissue_structure|
|Oropharynx         |Multi-tissue_structure|
|Mucous membranes   |Tissue                |
|Neck               |Organism_subdivision  |
|bowel              |Organ                 |
|great toe          |Multi-tissue_structure|
|skin               |Organ                 |
|toenails           |Organism_subdivision  |
|foot               |Organism_subdivision  |
|great toe          |Multi-tissue_structure|
|toenails           |Organism_subdivision  |
+-------------------+----------------------+
```

+ `bert_token_classifier_ner_bacteria` : This model is BERT-Based version of `ner_bacterial_species` model and detects different types of species of bacteria in clinical texts using `SPECIES` label.

*Example* :

```bash
...
tokenClassifier = BertForTokenClassification.pretrained("bert_token_classifier_ner_bacteria", "en", "clinical/models")\
    .setInputCols("token", "document")\
    .setOutputCol("ner")\
    .setCaseSensitive(True)

ner_converter = NerConverter()\
    .setInputCols(["document","token","ner"])\
    .setOutputCol("ner_chunk")

pipeline =  Pipeline(stages=[documentAssembler, tokenizer, tokenClassifier, ner_converter])
p_model = pipeline.fit(spark.createDataFrame(pd.DataFrame({'text': ['']})))

test_sentence = """Based on these genetic and phenotypic properties, we propose that strain SMSP (T) represents \
a novel species of the genus Methanoregula, for which we propose the name Methanoregula formicica \
sp. nov., with the type strain SMSP (T) (= NBRC 105244 (T) = DSM 22288 (T))."""
result = p_model.transform(spark.createDataFrame(pd.DataFrame({'text': [test_sentence]})))
```

*Results* :

```bash
+-----------------------+---------+
|chunk                  |ner_label|
+-----------------------+---------+
|SMSP (T)               |SPECIES  |
|Methanoregula formicica|SPECIES  |
|SMSP (T)               |SPECIES  |
+-----------------------+---------+
```

#### Radiology NER Model Trained On cheXpert Dataset

+ Ner NER model `ner_chexpert` trained on Radiology Chest reports to extract anatomical sites and observation entities. The model achieves 92.8% and 77.4% micro and macro f1 scores on the cheXpert dataset.

*Example* :

```bash
...
embeddings_clinical = WordEmbeddingsModel.pretrained("embeddings_clinical", "en", "clinical/models")  .setInputCols(["sentence", "token"])  .setOutputCol("embeddings")
clinical_ner = MedicalNerModel.pretrained("ner_chexpert", "en", "clinical/models")   .setInputCols(["sentence", "token", "embeddings"])   .setOutputCol("ner")
...
nlpPipeline = Pipeline(stages=[document_assembler, sentence_detector, tokenizer, embeddings_clinical, clinical_ner, ner_converter])
model = nlpPipeline.fit(spark.createDataFrame([[""]]).toDF("text"))
EXAMPLE_TEXT = """FINAL REPORT HISTORY : Chest tube leak , to assess for pneumothorax .
FINDINGS : In comparison with study of ___ , the endotracheal tube and Swan - Ganz catheter have been removed . The left chest tube remains in place and there is no evidence of pneumothorax. Mild atelectatic changes are seen at the left base."""
results = model.transform(spark.createDataFrame([[EXAMPLE_TEXT]]).toDF("text"))
```

*Results* :

```bash
|    | chunk                    | label   |
|---:|:-------------------------|:--------|
|  0 | endotracheal tube        | OBS     |
|  1 | Swan - Ganz catheter     | OBS     |
|  2 | left chest               | ANAT    |
|  3 | tube                     | OBS     |
|  4 | in place                 | OBS     |
|  5 | pneumothorax             | OBS     |
|  6 | Mild atelectatic changes | OBS     |
|  7 | left base                | ANAT    |
```

#### New Speed Benchmarks on Databricks

We prepared a speed benchmark table by running a NER pipeline on various number of cluster configurations (worker number, driver node, specs etc) and also writing the results to parquet or delta formats. You can find all the details of these tries in here : [Speed Benchmark Table](https://nlp.johnsnowlabs.com/docs/en/benchmark)

#### NerConverterInternal Fixes
Now NerConverterInternal can deal with tags that have some dash (`-`) charachter like B-GENE-N and B-GENE-Y.


#### Simplified Setup and Recommended Use of start() Function
Starting with this release, we are shipping AWS credentials inside Spark NLP Healthcare's license. This removes the requirement of setting the `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY` environment variables.
To use this feature, you just need to make sure that you always call the start() function at the beginning of your program,

```python
from sparknlp_jsl import start
spark = start()
```

```scala
import com.johnsnowlabs.util.start
val spark = start()
```

If for some reason you don't want to use this mechanism, the keys will continue to be shipped separately, and the environment variables will continue to work as they did in the past.

#### Ner Evaluation Metrics Fix

Bug fixed in the `NerDLMetrics` package. Previously, the `full_chunk` option was using greedy approach to merge chunks for a strict evaluation, which has been fixed to merge chunks using IOB scheme to get accurate entities boundaries and metrics. Also, the `tag` option has been fixed to get metrics that align with the default NER logs.

#### New Notebooks

- [Clinical Relation Extraction Knowledge Graph with Neo4j Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/10.2.Clinical_RE_Knowledge_Graph_with_Neo4j.ipynb)
- [NER Profiling Pretrained Pipelines Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/11.2.Pretrained_NER_Profiling_Pipelines.ipynb)
- New Databricks [Detecting Adverse Drug Events From Conversational Texts](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/databricks/python/healthcare_case_studies/Detecting%20Adverse%20Drug%20Events%20From%20Conversational%20Texts.ipynb) case study notebook.

**To see more, please check :** [Spark NLP Healthcare Workshop Repo](https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/tutorials/Certification_Trainings/Healthcare)


## 3.2.3
We are glad to announce that Spark NLP Healthcare 3.2.3 has been released!.

#### Highlights
+ New BERT-Based Deidentification NER Model
+ New Sentence Entity Resolver Models For German Language
+ New Spell Checker Model For Drugs
+ Allow To Use Disambiguator Pretrained Model
+ Allow To Use Seeds in StructuredDeidentification
+ Added Compatibility with Tensorflow 1.15 For Graph Generation.
+ New Setup Videos

#### New BERT-Based Deidentification NER Model

We have a new `bert_token_classifier_ner_deid` model that is BERT-based version of `ner_deid_subentity_augmented` and annotates text to find protected health information that may need to be de-identified. It can detect 23 different entities (`MEDICALRECORD`, `ORGANIZATION`, `DOCTOR`, `USERNAME`, `PROFESSION`, `HEALTHPLAN`, `URL`, `CITY`, `DATE`, `LOCATION-OTHER`, `STATE`, `PATIENT`, `DEVICE`, `COUNTRY`, `ZIP`, `PHONE`, `HOSPITAL`, `EMAIL`, `IDNUM`, `SREET`, `BIOID`, `FAX`, `AGE`).

*Example*:

```bash
documentAssembler = DocumentAssembler()\
  .setInputCol("text")\
  .setOutputCol("document")

tokenizer = Tokenizer()\
  .setInputCols("document")\
  .setOutputCol("token")

tokenClassifier = BertForTokenClassification.pretrained("bert_token_classifier_ner_deid", "en")\
  .setInputCols("token", "document")\
  .setOutputCol("ner")\
  .setCaseSensitive(True)

ner_converter = NerConverter()\
  .setInputCols(["document","token","ner"])\
  .setOutputCol("ner_chunk")

pipeline =  Pipeline(stages=[documentAssembler, tokenizer, tokenClassifier, ner_converter])
p_model = pipeline.fit(spark.createDataFrame(pd.DataFrame({'text': ['']})))

text = """A. Record date : 2093-01-13, David Hale, M.D. Name : Hendrickson, Ora MR. # 7194334. PCP : Oliveira, non-smoking. Cocke County Baptist Hospital. 0295 Keats Street. Phone +1 (302) 786-5227. Patient's complaints first surfaced when he started working for Brothers Coal-Mine."""
result = p_model.transform(spark.createDataFrame(pd.DataFrame({'text': [text]})))
```

*Results*:

```bash
+-----------------------------+-------------+
|chunk                        |ner_label    |
+-----------------------------+-------------+
|2093-01-13                   |DATE         |
|David Hale                   |DOCTOR       |
|Hendrickson, Ora             |PATIENT      |
|7194334                      |MEDICALRECORD|
|Oliveira                     |PATIENT      |
|Cocke County Baptist Hospital|HOSPITAL     |
|0295 Keats Street            |STREET       |
|302) 786-5227                |PHONE        |
|Brothers Coal-Mine           |ORGANIZATION |
+-----------------------------+-------------+
```

#### New Sentence Entity Resolver Models For German Language

We are releasing two new Sentence Entity Resolver Models for German language that use `sent_bert_base_cased` (de) embeddings.

+ `sbertresolve_icd10gm` : This model maps extracted medical entities to ICD10-GM codes for the German language.

*Example*:

```bash
documentAssembler = DocumentAssembler()\
    .setInputCol("text")\
    .setOutputCol("ner_chunk")

sbert_embedder = BertSentenceEmbeddings.pretrained("sent_bert_base_cased", "de")\
    .setInputCols(["ner_chunk"])\
    .setOutputCol("sbert_embeddings")

icd10gm_resolver = SentenceEntityResolverModel.pretrained("sbertresolve_icd10gm", "de", "clinical/models")\
    .setInputCols(["ner_chunk", "sbert_embeddings"])\
    .setOutputCol("icd10gm_code")

icd10gm_pipelineModel = PipelineModel( stages = [documentAssembler, sbert_embedder, icd10gm_resolver])

icd_lp = LightPipeline(icd10gm_pipelineModel)
icd_lp.fullAnnotate("Dyspnoe")
```

*Results* :

|chunk|code|resolutions|all_codes|all_distances|
|-|-|-|-|-|
| Dyspnoe | C671 | Dyspnoe, Schlafapnoe, Dysphonie, Frühsyphilis, Hyperzementose, Hypertrichose, ...  | [R06.0, G47.3, R49.0, A51, K03.4, L68, ...] | [0.0000, 2.5602, 3.0529, 3.3310, 3.4645, 3.7148, ...] |

+ `sbertresolve_snomed` : This model maps extracted medical entities to SNOMED codes for the German language.

*Example*:

```bash
documentAssembler = DocumentAssembler()\
    .setInputCol("text")\
    .setOutputCol("ner_chunk")

sbert_embedder = BertSentenceEmbeddings.pretrained("sent_bert_base_cased", "de")\
    .setInputCols(["ner_chunk"])\
    .setOutputCol("sbert_embeddings")

snomed_resolver = SentenceEntityResolverModel.pretrained("sbertresolve_snomed", "de", "clinical/models")\
    .setInputCols(["ner_chunk", "sbert_embeddings"])\
    .setOutputCol("snomed_code")

snomed_pipelineModel = PipelineModel( stages = [ documentAssembler, sbert_embedder, snomed_resolver])

snomed_lp = LightPipeline(snomed_pipelineModel)
snomed_lp.fullAnnotate("Bronchialkarzinom ")
```

*Results* :

|chunk|code|resolutions|all_codes|all_distances|
|-|-|-|-|-|
| Bronchialkarzinom  | 22628 | Bronchialkarzinom, Bronchuskarzinom, Rektumkarzinom, Klavikulakarzinom, Lippenkarzinom, Urothelkarzinom, ...  | [22628, 111139, 18116, 107569, 18830, 22909, ...] | [0.0000, 0.0073, 0.0090, 0.0098, 0.0098, 0.0102, ...] |

#### New Spell Checker Model For Drugs

We are releasing new `spellcheck_drug_norvig` model that detects and corrects spelling errors of drugs in a text based on the Norvig's approach.

*Example* :

```bash
documentAssembler = DocumentAssembler()\
    .setInputCol("text")\
    .setOutputCol("document")

tokenizer = Tokenizer()
    .setInputCols("document")\
    .setOutputCol("token")

spell = NorvigSweetingModel.pretrained("spellcheck_drug_norvig", "en", "clinical/models")\
    .setInputCols("token")
    .setOutputCol("spell")\

pipeline = Pipeline( stages = [documentAssembler,
tokenizer, spell])

model = pipeline.fit(spark.createDataFrame([['']]).toDF('text'))
lp = LightPipeline(model)

lp.annotate("You have to take Neutrcare and colfosrinum and a bit of Fluorometholne & Ribotril")
```

*Results* :

```bash
Original text  : You have to take Neutrcare and colfosrinum and a bit of fluorometholne & Ribotril
Corrected text : You have to take Neutracare and colforsinum and a bit of fluorometholone & Rivotril

```

#### Allow to use Disambiguator pretrained model.

Now we can use the NerDisambiguatorModel as a pretrained model to disambiguate person entities.

```python
 text = "The show also had a contestant named Brad Pitt" \
        + "who later defeated Christina Aguilera on the way to become Female Vocalist Champion in the 1989 edition of Star Search in the United States. "
 data = SparkContextForTest.spark.createDataFrame([
     [text]]) \
     .toDF("text").cache()
 da = DocumentAssembler().setInputCol("text").setOutputCol("document")

 sd = SentenceDetector().setInputCols("document").setOutputCol("sentence")

 tk = Tokenizer().setInputCols("sentence").setOutputCol("token")

 emb = WordEmbeddingsModel.pretrained().setOutputCol("embs")

 semb = SentenceEmbeddings().setInputCols("sentence", "embs").setOutputCol("sentence_embeddings")

 ner = NerDLModel.pretrained().setInputCols("sentence", "token", "embs").setOutputCol("ner")

 nc = NerConverter().setInputCols("sentence", "token", "ner").setOutputCol("ner_chunk").setWhiteList(["PER"])

 NerDisambiguatorModel.pretrained().setInputCols("ner_chunk", "sentence_embeddings").setOutputCol("disambiguation")

 pl = Pipeline().setStages([da, sd, tk, emb, semb, ner, nc, disambiguator])

 data = pl.fit(data).transform(data)
 data.select("disambiguation").show(10, False)

```

```bash
+-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
|disambiguation                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
+-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
|[[disambiguation, 65, 82, http://en.wikipedia.org/?curid=144171, http://en.wikipedia.org/?curid=6636454, [chunk -> Christina Aguilera, titles -> christina aguilera ::::: christina aguilar, links -> http://en.wikipedia.org/?curid=144171 ::::: http://en.wikipedia.org/?curid=6636454, beginInText -> 65, scores -> 0.9764155197864447, 0.9727793647472524, categories -> Musicians, Singers, Actors, Businesspeople, Musicians, Singers, ids -> 144171, 6636454, endInText -> 82], []]]|
+-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+

----------------------

```
#### Allow to use seeds in StructuredDeidentification

Now, we can use a seed for a specific column. The seed is used to randomly select the entities used during obfuscation mode. By providing the same seed, you can replicate the same mapping multiple times.

```python
df = spark.createDataFrame([
            ["12", "12", "Juan García"],
            ["24", "56", "Will Smith"],
            ["56", "32", "Pedro Ximénez"]
        ]).toDF("ID1", "ID2", "NAME")

obfuscator = StructuredDeidentification(spark=spark, columns={"ID1": "ID", "ID2": "ID", "NAME": "PATIENT"},
                                                columnsSeed={"ID1": 23, "ID2": 23},
                                                obfuscateRefSource="faker")
result = obfuscator.obfuscateColumns(df)
result.show(truncate=False)      
```

```bash
+----------+----------+----------------+
|ID1       |ID2       |NAME            |
+----------+----------+----------------+
|[D3379888]|[D3379888]|[Raina Cleaves] |
|[R8448971]|[M8851891]|[Jennell Barre] |
|[M8851891]|[L5448098]|[Norene Salines]|
+----------+----------+----------------+

Here, you can see that as we have provided the same seed `23` for columns `ID1`, and `ID2`, the number `12` which is appears twice in the first row is mapped to the same randomly generated id `D3379888` each time.
```
#### Added compatibility with Tensorflow 1.15 for graph generation
Some users reported problems while using graphs generated by Tensorflow 2.x. We provide compatibility with Tensorflow 1.15 in the `tf_graph_1x` module, that can be used like this,

```
from sparknlp_jsl.training import tf_graph_1x

```

In next releases, we will provide full support for graph generation using Tensorflow 2.x.

#### New Setup Videos

Now we have videos showing how to setup Spark NLP, Spark NLP for Healthcare and Spark OCR on UBUNTU.

+ [How to Setup Spark NLP on UBUNTU](https://www.youtube.com/watch?v=ZnFENM-yNfQ)
+ [How to Setup Spark NLP for HEALTHCARE on UBUNTU](https://www.youtube.com/watch?v=yKnF-_oz0GE)
+ [How to Setup Spark OCR on UBUNTU](https://www.youtube.com/watch?v=cmt4WIcL0nI)

**To see more, please check**: [Spark NLP Healthcare Workshop Repo](https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/tutorials/Certification_Trainings/Healthcare)


## 3.2.2
We are glad to announce that Spark NLP Healthcare 3.2.2 has been released!.

#### Highlights
+ New NER Model For Detecting Drugs, Posology, and Administration Cycles
+ New Sentence Entity Resolver Models
+ New Router Annotator To Use Multiple Resolvers Optimally In the Same Pipeline
+ Re-Augmented Deidentification NER Model

#### New NER Model For Detecting Drugs, Posology, and Administration Cycles

We are releasing a new NER posology model `ner_posology_experimental`. This model is based on the original `ner_posology_large` model, but trained with additional clinical trials data to detect experimental drugs, experiment cycles, cycle counts, and cycles numbers. Supported Entities: `Administration`, `Cyclenumber`, `Strength`, `Cycleday`, `Duration`, `Cyclecount`, `Route`, `Form`, `Frequency`, `Cyclelength`, `Drug`, `Dosage`

*Example*:

```bash
...
word_embeddings = WordEmbeddingsModel.pretrained("embeddings_clinical", "en", "clinical/models")\
   .setInputCols(["sentence", "token"])\
   .setOutputCol("embeddings")
clinical_ner = MedicalNerModel.pretrained("ner_posology_experimental", "en", "clinical/models") \
   .setInputCols(["sentence", "token", "embeddings"]) \
   .setOutputCol("ner")
...
nlp_pipeline = Pipeline(stages=[document_assembler, sentence_detector, tokenizer, word_embeddings, clinical_ner, ner_converter])
model = nlp_pipeline.fit(spark.createDataFrame([[""]]).toDF("text"))
results = model.transform(spark.createDataFrame([["Y-90 Humanized Anti-Tac: 10 mCi (if a bone marrow transplant was part of the patient's previous therapy) or 15 mCi of yttrium labeled anti-TAC; followed by calcium trisodium Inj (Ca DTPA)..\n\nCalcium-DTPA: Ca-DTPA will be administered intravenously on Days 1-3 to clear the radioactive agent from the body."]]).toDF("text"))
```

*Results*:
```bash
|    | chunk                    |   begin |   end | entity   |
|---:|:-------------------------|--------:|------:|:---------|
|  0 | Y-90 Humanized Anti-Tac  |       0 |    22 | Drug     |
|  1 | 10 mCi                   |      25 |    30 | Dosage   |
|  2 | 15 mCi                   |     108 |   113 | Dosage   |
|  3 | yttrium labeled anti-TAC |     118 |   141 | Drug     |
|  4 | calcium trisodium Inj    |     156 |   176 | Drug     |
|  5 | Calcium-DTPA             |     191 |   202 | Drug     |
|  6 | Ca-DTPA                  |     205 |   211 | Drug     |
|  7 | intravenously            |     234 |   246 | Route    |
|  8 | Days 1-3                 |     251 |   258 | Cycleday |
```

#### New Sentence Entity Resolver Models

We have two new sentence entity resolver models trained with using `sbert_jsl_medium_uncased` embeddings.

+ `sbertresolve_rxnorm_disposition` : This model maps medication entities (like drugs/ingredients) to RxNorm codes and their dispositions using `sbert_jsl_medium_uncased` Sentence Bert Embeddings. If you look for a faster inference with just drug names (excluding dosage and strength), this version of RxNorm model would be a better alternative. In the result, look for the aux_label parameter in the metadata to get dispositions divided by `|`.

*Example*:
```bash
documentAssembler = DocumentAssembler()\
      .setInputCol("text")\
      .setOutputCol("ner_chunk")

sbert_embedder = BertSentenceEmbeddings.pretrained('sbert_jsl_medium_uncased', 'en','clinical/models')\
      .setInputCols(["ner_chunk"])\
      .setOutputCol("sbert_embeddings")

rxnorm_resolver = SentenceEntityResolverModel.pretrained("sbertresolve_rxnorm_disposition", "en", "clinical/models") \
      .setInputCols(["ner_chunk", "sbert_embeddings"]) \
      .setOutputCol("rxnorm_code")\
      .setDistanceFunction("EUCLIDEAN")

rxnorm_pipelineModel = PipelineModel(
    stages = [
        documentAssembler,
        sbert_embedder,
        rxnorm_resolver])

rxnorm_lp = LightPipeline(rxnorm_pipelineModel)
rxnorm_lp = LightPipeline(pipelineModel) result = rxnorm_lp.fullAnnotate("alizapride 25 mg/ml")
```
*Result*:

```bash
|    | chunks             | code   | resolutions                                                                                                                                                                            | all_codes                                                       | all_k_aux_labels                                                                                            | all_distances                                                 |
|---:|:-------------------|:-------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:----------------------------------------------------------------|:------------------------------------------------------------------------------------------------------------|:--------------------------------------------------------------|
|  0 |alizapride 25 mg/ml | 330948 | [alizapride 25 mg/ml, alizapride 50 mg, alizapride 25 mg/ml oral solution, adalimumab 50 mg/ml, adalimumab 100 mg/ml [humira], adalimumab 50 mg/ml [humira], alirocumab 150 mg/ml, ...]| [330948, 330949, 249531, 358817, 1726845, 576023, 1659153, ...] | [Dopamine receptor antagonist, Dopamine receptor antagonist, Dopamine receptor antagonist, -, -, -, -, ...] | [0.0000, 0.0936, 0.1166, 0.1525, 0.1584, 0.1567, 0.1631, ...] |
```

+ `sbertresolve_snomed_conditions` : This model maps clinical entities (domain: Conditions) to Snomed codes using `sbert_jsl_medium_uncased` Sentence Bert Embeddings.

*Example*:

```bash
documentAssembler = DocumentAssembler()\
      .setInputCol("text")\
      .setOutputCol("ner_chunk")

sbert_embedder = BertSentenceEmbeddings.pretrained('sbert_jsl_medium_uncased', 'en','clinical/models')\
      .setInputCols(["ner_chunk"])\
      .setOutputCol("sbert_embeddings")

snomed_resolver = SentenceEntityResolverModel.pretrained("sbertresolve_snomed_conditions", "en", "clinical/models") \
      .setInputCols(["ner_chunk", "sbert_embeddings"]) \
      .setOutputCol("snomed_code")\
      .setDistanceFunction("EUCLIDEAN")

snomed_pipelineModel = PipelineModel(
    stages = [
        documentAssembler,
        sbert_embedder,
        snomed_resolver
        ])

snomed_lp = LightPipeline(snomed_pipelineModel)
result = snomed_lp.fullAnnotate("schizophrenia")
```
*Result*:

```bash
|    | chunks        | code     | resolutions                                                                                                              | all_codes                                                            | all_distances                                        |
|---:|:--------------|:---------|:-------------------------------------------------------------------------------------------------------------------------|:---------------------------------------------------------------------|:-----------------------------------------------------|
|  0 | schizophrenia | 58214004 | [schizophrenia, chronic schizophrenia, borderline schizophrenia, schizophrenia, catatonic, subchronic schizophrenia, ...]| [58214004, 83746006, 274952002, 191542003, 191529003, 16990005, ...] | 0.0000, 0.0774, 0.0838, 0.0927, 0.0970, 0.0970, ...] |
```

#### New Router Annotator To Use Multiple Resolvers Optimally In the Same Pipeline

Normally, when we need to use more than one sentence entity resolver models in the same pipeline, we used to hit `BertSentenceEmbeddings` annotator more than once given the number of different resolver models in the same pipeline. Now we are introducing a solution with the help of `Router` annotator that could allow us to feed all the NER chunks to `BertSentenceEmbeddings` at once and then route the output of Sentence Embeddings to different resolver models needed.

You can find an example of how to use this annotator in the updated [3.Clinical_Entity_Resolvers.ipynb Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/3.Clinical_Entity_Resolvers.ipynb)

*Example*:

```bash
...
# to get PROBLEM entitis
clinical_ner = MedicalNerModel().pretrained("ner_clinical", "en", "clinical/models") \
        .setInputCols(["sentence", "token", "word_embeddings"]) \
        .setOutputCol("clinical_ner")

clinical_ner_chunk = NerConverter()\
        .setInputCols("sentence","token","clinical_ner")\
        .setOutputCol("clinical_ner_chunk")\
        .setWhiteList(["PROBLEM"])

# to get DRUG entities
posology_ner = MedicalNerModel().pretrained("ner_posology", "en", "clinical/models") \
        .setInputCols(["sentence", "token", "word_embeddings"]) \
        .setOutputCol("posology_ner")

posology_ner_chunk = NerConverter()\
        .setInputCols("sentence","token","posology_ner")\
        .setOutputCol("posology_ner_chunk")\
        .setWhiteList(["DRUG"])

# merge the chunks into a single ner_chunk
chunk_merger = ChunkMergeApproach()\
        .setInputCols("clinical_ner_chunk","posology_ner_chunk")\
        .setOutputCol("final_ner_chunk")\
        .setMergeOverlapping(False)


# convert chunks to doc to get sentence embeddings of them
chunk2doc = Chunk2Doc().setInputCols("final_ner_chunk").setOutputCol("final_chunk_doc")


sbiobert_embeddings = BertSentenceEmbeddings.pretrained("sbiobert_base_cased_mli","en","clinical/models")\
        .setInputCols(["final_chunk_doc"])\
        .setOutputCol("sbert_embeddings")

# filter PROBLEM entity embeddings
router_sentence_icd10 = Router() \
        .setInputCols("sbert_embeddings") \
        .setFilterFieldsElements(["PROBLEM"]) \
        .setOutputCol("problem_embeddings")

# filter DRUG entity embeddings
router_sentence_rxnorm = Router() \
        .setInputCols("sbert_embeddings") \
        .setFilterFieldsElements(["DRUG"]) \
        .setOutputCol("drug_embeddings")

# use problem_embeddings only
icd_resolver = SentenceEntityResolverModel.pretrained("sbiobertresolve_icd10cm_slim_billable_hcc","en", "clinical/models") \
        .setInputCols(["clinical_ner_chunk", "problem_embeddings"]) \
        .setOutputCol("icd10cm_code")\
        .setDistanceFunction("EUCLIDEAN")


# use drug_embeddings only
rxnorm_resolver = SentenceEntityResolverModel.pretrained("sbiobertresolve_rxnorm","en", "clinical/models") \
        .setInputCols(["posology_ner_chunk", "drug_embeddings"]) \
        .setOutputCol("rxnorm_code")\
        .setDistanceFunction("EUCLIDEAN")


pipeline = Pipeline(stages=[
    documentAssembler,
    sentenceDetector,
    tokenizer,
    word_embeddings,
    clinical_ner,
    clinical_ner_chunk,
    posology_ner,
    posology_ner_chunk,
    chunk_merger,
    chunk2doc,
    sbiobert_embeddings,
    router_sentence_icd10,
    router_sentence_rxnorm,
    icd_resolver,
    rxnorm_resolver
])

```


#### Re-Augmented Deidentification NER Model

We re-augmented `ner_deid_subentity_augmented` deidentification NER model improving the previous metrics by 2%.

*Example*:

```bash
...
deid_ner = MedicalNerModel.pretrained("ner_deid_subentity_augmented", "en", "clinical/models") \
      .setInputCols(["sentence", "token", "embeddings"]) \
      .setOutputCol("ner")
...
nlpPipeline = Pipeline(stages=[document_assembler, sentence_detector, tokenizer, word_embeddings, deid_ner, ner_converter])
model = nlpPipeline.fit(spark.createDataFrame([[""]]).toDF("text"))

results = model.transform(spark.createDataFrame(pd.DataFrame({"text": ["""A. Record date : 2093-01-13, David Hale, M.D., Name : Hendrickson, Ora MR. # 7194334 Date : 01/13/93 PCP : Oliveira, 25 -year-old, Record date : 1-11-2000. Cocke County Baptist Hospital. 0295 Keats Street. Phone +1 (302) 786-5227."""]})))
```

*Results*:

```bash
+-----------------------------+-------------+
|chunk                        |ner_label    |
+-----------------------------+-------------+
|2093-01-13                   |DATE         |
|David Hale                   |DOCTOR       |
|Hendrickson, Ora             |PATIENT      |
|7194334                      |MEDICALRECORD|
|01/13/93                     |DATE         |
|Oliveira                     |DOCTOR       |
|25-year-old                  |AGE          |
|1-11-2000                    |DATE         |
|Cocke County Baptist Hospital|HOSPITAL     |
|0295 Keats Street.           |STREET       |
|(302) 786-5227               |PHONE        |
|Brothers Coal-Mine           |ORGANIZATION |
+-----------------------------+-------------+
```


**To see more, please check:** [Spark NLP Healthcare Workshop Repo](https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/tutorials/Certification_Trainings/Healthcare)


## 3.2.1
We are glad to announce that Spark NLP Healthcare 3.2.1 has been released!.

#### Highlights

+ Deprecated ChunkEntityResolver.
+ New BERT-Based NER Models
+ HCC module added support for versions v22 and v23.
+ Updated Notebooks for resolvers and graph builders.
+ New TF Graph Builder.

#### New BERT-Based NER Models

We have two new BERT-based token classifier NER models. These models are the first clinical NER models that use the BertForTokenCLassification approach that was introduced in Spark NLP 3.2.0.

+ `bert_token_classifier_ner_clinical`: This model is BERT-based version of `ner_clinical` model. This new model is 4% better than the legacy NER model (MedicalNerModel) that is based on BiLSTM-CNN-Char architecture.

*Metrics*:

```
              precision    recall  f1-score   support

     PROBLEM       0.88      0.92      0.90     30276
        TEST       0.91      0.86      0.88     17237
   TREATMENT       0.87      0.88      0.88     17298
           O       0.97      0.97      0.97    202438

    accuracy                           0.95    267249
   macro avg       0.91      0.91      0.91    267249
weighted avg       0.95      0.95      0.95    267249

```

*Example*:

```bash
documentAssembler = DocumentAssembler()\
  .setInputCol("text")\
  .setOutputCol("document")

sentenceDetector = SentenceDetectorDLModel.pretrained("sentence_detector_dl_healthcare","en","clinical/models")\
       .setInputCols(["document"])\
       .setOutputCol("sentence")

tokenizer = Tokenizer()\
       .setInputCols("sentence")\
       .setOutputCol("token")

tokenClassifier = BertForTokenClassification.pretrained("bert_token_classifier_ner_clinical", "en", "clinical/models")\
       .setInputCols("token", "sentence")\
       .setOutputCol("ner")\
       .setCaseSensitive(True)

ner_converter = NerConverter()\
        .setInputCols(["sentence","token","ner"])\
        .setOutputCol("ner_chunk")

pipeline =  Pipeline(stages=[
       documentAssembler,
       sentenceDetector,
       tokenizer,
       tokenClassifier,
       ner_converter
  ])

p_model = pipeline.fit(spark.createDataFrame([[""]]).toDF("text"))

text = 'A 28-year-old female with a history of gestational diabetes mellitus diagnosed eight years prior to presentation and subsequent type two diabetes mellitus ( T2DM ), one prior episode of HTG-induced pancreatitis three years prior to presentation , associated with an acute hepatitis , and obesity with a body mass index ( BMI ) of 33.5 kg/m2 , presented with a one-week history of polyuria , polydipsia , poor appetite , and vomiting . Two weeks prior to presentation , she was treated with a five-day course of amoxicillin for a respiratory tract infection . She was on metformin , glipizide , and dapagliflozin for T2DM and atorvastatin and gemfibrozil for HTG . She had been on dapagliflozin for six months at the time of presentation . Physical examination on presentation was significant for dry oral mucosa ; significantly , her abdominal examination was benign with no tenderness , guarding , or rigidity . Pertinent laboratory findings on admission were : serum glucose 111 mg/dl , bicarbonate 18 mmol/l , anion gap 20 , creatinine 0.4 mg/dL , triglycerides 508 mg/dL , total cholesterol 122 mg/dL , glycated hemoglobin ( HbA1c ) 10% , and venous pH 7.27 . Serum lipase was normal at 43 U/L . Serum acetone levels could not be assessed as blood samples kept hemolyzing due to significant lipemia . The patient was initially admitted for starvation ketosis , as she reported poor oral intake for three days prior to admission . However , serum chemistry obtained six hours after presentation revealed her glucose was 186 mg/dL , the anion gap was still elevated at 21 , serum bicarbonate was 16 mmol/L , triglyceride level peaked at 2050 mg/dL , and lipase was 52 U/L . The β-hydroxybutyrate level was obtained and found to be elevated at 5.29 mmol/L - the original sample was centrifuged and the chylomicron layer removed prior to analysis due to interference from turbidity caused by lipemia again . The patient was treated with an insulin drip for euDKA and HTG with a reduction in the anion gap to 13 and triglycerides to 1400 mg/dL , within 24 hours . Her euDKA was thought to be precipitated by her respiratory tract infection in the setting of SGLT2 inhibitor use . The patient was seen by the endocrinology service and she was discharged on 40 units of insulin glargine at night , 12 units of insulin lispro with meals , and metformin 1000 mg two times a day . It was determined that all SGLT2 inhibitors should be discontinued indefinitely . She had close follow-up with endocrinology post discharge .'

res = p_model.transform(spark.createDataFrame([[text]]).toDF("text")).collect()

res[0]['label']
```

+ `bert_token_classifier_ner_jsl`: This model is BERT-based version of `ner_jsl` model. This new model is better than the legacy NER model (MedicalNerModel) that is based on BiLSTM-CNN-Char architecture.

*Metrics*:

```
                                    precision    recall  f1-score   support

                    Admission_Discharge       0.84      0.97      0.90       415
                                    Age       0.96      0.96      0.96      2434
                                Alcohol       0.75      0.83      0.79       145
                               Allergen       0.33      0.16      0.22        25
                                    BMI       1.00      0.77      0.87        26
                           Birth_Entity       1.00      0.17      0.29        12
                         Blood_Pressure       0.86      0.88      0.87       597
                Cerebrovascular_Disease       0.74      0.77      0.75       266
                          Clinical_Dept       0.90      0.92      0.91      2385
                   Communicable_Disease       0.70      0.59      0.64        85
                                   Date       0.95      0.98      0.96      1438
                           Death_Entity       0.83      0.83      0.83        59
                               Diabetes       0.95      0.95      0.95       350
                                   Diet       0.60      0.49      0.54       229
                              Direction       0.88      0.90      0.89      6187
              Disease_Syndrome_Disorder       0.90      0.89      0.89     13236
                                 Dosage       0.57      0.49      0.53       263
                                   Drug       0.91      0.93      0.92     15926
                               Duration       0.82      0.85      0.83      1218
                           EKG_Findings       0.64      0.70      0.67       325
                             Employment       0.79      0.85      0.82       539
           External_body_part_or_region       0.84      0.84      0.84      4805
                  Family_History_Header       1.00      1.00      1.00       889
                          Fetus_NewBorn       0.57      0.56      0.56       341
                                   Form       0.53      0.43      0.48        81
                              Frequency       0.87      0.90      0.88      1718
                                 Gender       0.98      0.98      0.98      5666
                                    HDL       0.60      1.00      0.75         6
                          Heart_Disease       0.88      0.88      0.88      2295
                                 Height       0.89      0.96      0.92       134
                         Hyperlipidemia       1.00      0.95      0.97       194
                           Hypertension       0.95      0.98      0.97       566
                        ImagingFindings       0.66      0.64      0.65       601
                      Imaging_Technique       0.62      0.67      0.64       108
                    Injury_or_Poisoning       0.85      0.83      0.84      1680
            Internal_organ_or_component       0.90      0.91      0.90     21318
                         Kidney_Disease       0.89      0.89      0.89       446
                                    LDL       0.88      0.97      0.92        37
                        Labour_Delivery       0.82      0.71      0.76       306
                         Medical_Device       0.89      0.93      0.91     12852
                 Medical_History_Header       0.96      0.97      0.96      1013
                               Modifier       0.68      0.60      0.64      1398
                          O2_Saturation       0.84      0.82      0.83       199
                                Obesity       0.96      0.98      0.97       130
                            Oncological       0.88      0.96      0.92      1635
                             Overweight       0.80      0.80      0.80        10
                         Oxygen_Therapy       0.91      0.92      0.92       231
                              Pregnancy       0.81      0.83      0.82       439
                              Procedure       0.91      0.91      0.91     14410
                Psychological_Condition       0.81      0.81      0.81       354
                                  Pulse       0.85      0.95      0.89       389
                         Race_Ethnicity       1.00      1.00      1.00       163
                    Relationship_Status       0.93      0.91      0.92        57
                           RelativeDate       0.83      0.86      0.84      1562
                           RelativeTime       0.74      0.79      0.77       431
                            Respiration       0.99      0.95      0.97       221
                                  Route       0.68      0.69      0.69       597
                         Section_Header       0.97      0.98      0.98     28580
  Sexually_Active_or_Sexual_Orientation       1.00      0.64      0.78        14
                                Smoking       0.83      0.90      0.86       225
                  Social_History_Header       0.95      0.99      0.97       825
                               Strength       0.71      0.55      0.62       227
                              Substance       0.85      0.81      0.83       193
                     Substance_Quantity       0.00      0.00      0.00        28
                                Symptom       0.84      0.86      0.85     23092
                            Temperature       0.94      0.97      0.96       410
                                   Test       0.84      0.88      0.86      9050
                            Test_Result       0.84      0.84      0.84      2766
                                   Time       0.90      0.81      0.86       140
                      Total_Cholesterol       0.69      0.95      0.80        73
                              Treatment       0.73      0.72      0.73       506
                          Triglycerides       0.83      0.80      0.81        30
                             VS_Finding       0.76      0.77      0.76       588
                                Vaccine       0.70      0.84      0.76        92
                     Vital_Signs_Header       0.95      0.98      0.97      2223
                                 Weight       0.88      0.89      0.88       306
                                      O       0.97      0.96      0.97    253164

                               accuracy                           0.94    445974
                              macro avg       0.82      0.82      0.81    445974
                           weighted avg       0.94      0.94      0.94    445974
```

*Example*:

```
documentAssembler = DocumentAssembler()\
       .setInputCol("text")\
       .setOutputCol("document")

sentenceDetector = SentenceDetectorDLModel.pretrained("sentence_detector_dl_healthcare","en","clinical/models")\
       .setInputCols(["document"])\
       .setOutputCol("sentence")

tokenizer = Tokenizer()\
       .setInputCols("sentence")\
       .setOutputCol("token")

tokenClassifier = BertForTokenClassification.pretrained("bert_token_classifier_ner_jsl", "en", "clinical/models")\
       .setInputCols("token", "sentence")\
       .setOutputCol("ner")\
       .setCaseSensitive(True)

ner_converter = NerConverter()\
        .setInputCols(["sentence","token","ner"])\
        .setOutputCol("ner_chunk")

pipeline =  Pipeline(stages=[
       documentAssembler,
       sentenceDetector,
       tokenizer,
       tokenClassifier,
       ner_converter
  ])

p_model = pipeline.fit(spark.createDataFrame([[""]]).toDF("text"))

text = 'A 28-year-old female with a history of gestational diabetes mellitus diagnosed eight years prior to presentation and subsequent type two diabetes mellitus ( T2DM ), one prior episode of HTG-induced pancreatitis three years prior to presentation , associated with an acute hepatitis , and obesity with a body mass index ( BMI ) of 33.5 kg/m2 , presented with a one-week history of polyuria , polydipsia , poor appetite , and vomiting . Two weeks prior to presentation , she was treated with a five-day course of amoxicillin for a respiratory tract infection . She was on metformin , glipizide , and dapagliflozin for T2DM and atorvastatin and gemfibrozil for HTG . She had been on dapagliflozin for six months at the time of presentation . Physical examination on presentation was significant for dry oral mucosa ; significantly , her abdominal examination was benign with no tenderness , guarding , or rigidity . Pertinent laboratory findings on admission were : serum glucose 111 mg/dl , bicarbonate 18 mmol/l , anion gap 20 , creatinine 0.4 mg/dL , triglycerides 508 mg/dL , total cholesterol 122 mg/dL , glycated hemoglobin ( HbA1c ) 10% , and venous pH 7.27 . Serum lipase was normal at 43 U/L . Serum acetone levels could not be assessed as blood samples kept hemolyzing due to significant lipemia . The patient was initially admitted for starvation ketosis , as she reported poor oral intake for three days prior to admission . However , serum chemistry obtained six hours after presentation revealed her glucose was 186 mg/dL , the anion gap was still elevated at 21 , serum bicarbonate was 16 mmol/L , triglyceride level peaked at 2050 mg/dL , and lipase was 52 U/L . The β-hydroxybutyrate level was obtained and found to be elevated at 5.29 mmol/L - the original sample was centrifuged and the chylomicron layer removed prior to analysis due to interference from turbidity caused by lipemia again . The patient was treated with an insulin drip for euDKA and HTG with a reduction in the anion gap to 13 and triglycerides to 1400 mg/dL , within 24 hours . Her euDKA was thought to be precipitated by her respiratory tract infection in the setting of SGLT2 inhibitor use . The patient was seen by the endocrinology service and she was discharged on 40 units of insulin glargine at night , 12 units of insulin lispro with meals , and metformin 1000 mg two times a day . It was determined that all SGLT2 inhibitors should be discontinued indefinitely . She had close follow-up with endocrinology post discharge .'

res = p_model.transform(spark.createDataFrame([[text]]).toDF("text")).collect()

res[0]['label']
```


#### HCC module added support for versions v22 and v23

Now we can use the version 22 and the version 23 for the new HCC module to calculate CMS-HCC Risk Adjustment score.

Added the following parameters `elig`, `orec` and `medicaid` on the profiles functions. These parameters may not be stored in clinical notes, and may require to be imported from other sources.

```
elig : The eligibility segment of the patient.
       Allowed values are as follows:
       - "CFA": Community Full Benefit Dual Aged
       - "CFD": Community Full Benefit Dual Disabled
       - "CNA": Community NonDual Aged
       - "CND": Community NonDual Disabled
       - "CPA": Community Partial Benefit Dual Aged
       - "CPD": Community Partial Benefit Dual Disabled
       - "INS": Long Term Institutional
       - "NE": New Enrollee
       - "SNPNE": SNP NE

orec: Original reason for entitlement code.
      - "0": Old age and survivor's insurance
      - "1": Disability insurance benefits
      - "2": End-stage renal disease
      - "3": Both DIB and ESRD

medicaid: If the patient is in Medicaid or not.

```

Required parameters should be stored in Spark dataframe.

```python

df.show(truncate=False)

+---------------+------------------------------+---+------+-----------+----+--------+
|hcc_profileV24 |icd10_code                    |age|gender|eligibility|orec|medicaid|
+---------------+------------------------------+---+------+-----------+----+--------+
|{"hcc_lst":[...|[E1169, I5030, I509, E852]    |64 |F     |CFA        |0   |true    |
|{"hcc_lst":[...|[G629, D469, D6181]           |77 |M     |CND        |1   |false   |
|{"hcc_lst":[...|[D473, D473, D473, M069, C969]|16 |F     |CPA        |3   |true    |
+---------------+------------------------------+---+------+-----------+----+--------+

The content of the hcc_profileV24 column is a JSON-parsable string, like in the following example,
{
    "hcc_lst": [
        "HCC18",
        "HCC85_gDiabetesMellit",
        "HCC85",
        "HCC23",
        "D3"
    ],
    "details": {
        "CNA_HCC18": 0.302,
        "CNA_HCC85": 0.331,
        "CNA_HCC23": 0.194,
        "CNA_D3": 0.0,
        "CNA_HCC85_gDiabetesMellit": 0.0
    },
    "hcc_map": {
        "E1169": [
            "HCC18"
        ],
        "I5030": [
            "HCC85"
        ],
        "I509": [
            "HCC85"
        ],
        "E852": [
            "HCC23"
        ]
    },
    "risk_score": 0.827,
    "parameters": {
        "elig": "CNA",
        "age": 56,
        "sex": "F",
        "origds": false,
        "disabled": false,
        "medicaid": false
    }
}


```
We can import different CMS-HCC model versions as seperate functions and use them in the same program.

```python

from sparknlp_jsl.functions import profile,profileV22,profileV23

df = df.withColumn("hcc_profileV24", profile(df.icd10_code,
                                          df.age,
                                          df.gender,
                                          df.eligibility,
                                          df.orec,
                                          df.medicaid
                                          ))

df.withColumn("hcc_profileV22", profileV22(df.codes, df.age, df.sex,df.elig,df.orec,df.medicaid))
df.withColumn("hcc_profileV23", profileV23(df.codes, df.age, df.sex,df.elig,df.orec,df.medicaid))

```


```python
df.show(truncate=False)

+----------+------------------------------+---+------+-----------+----+--------+
|risk_score|icd10_code                    |age|gender|eligibility|orec|medicaid|
+----------+------------------------------+---+------+-----------+----+--------+
|0.922     |[E1169, I5030, I509, E852]    |64 |F     |CFA        |0   |true    |
|3.566     |[G629, D469, D6181]           |77 |M     |CND        |1   |false   |
|1.181     |[D473, D473, D473, M069, C969]|16 |F     |CPA        |3   |true    |
+----------+------------------------------+---+------+-----------+----+--------+
```

#### Updated Notebooks for resolvers and graph builders

+ We have updated the resolver notebooks on spark-nlp-workshop repo with new `BertSentenceChunkEmbeddings` annotator. This annotator lets users aggregate sentence embeddings and ner chunk embeddings to get more specific and accurate resolution codes. It works by averaging context and chunk embeddings to get contextual information. Input to this annotator is the context (sentence) and ner chunks, while the output is embedding for each chunk that can be fed to the resolver model. The `setChunkWeight` parameter can be used to control the influence of surrounding context. Example below shows the comparison of old vs new approach.


|text|ner_chunk|entity|icd10_code|all_codes|resolutions|icd10_code_SCE|all_codes_SCE|resolutions_SCE|
|-|-|-|-|-|-|-|-|-|
|Two weeks prior to presentation, she was treated with a five-day course of amoxicillin for a respiratory tract infection.|a respiratory tract infection|PROBLEM|J988|[J988, J069, A499, J22, J209,...]|[respiratory tract infection, upper respiratory tract infection, bacterial respiratory infection, acute respiratory infection, bronchial infection,...]|Z870|[Z870, Z8709, J470, J988, A499,...|[history of acute lower respiratory tract infection (situation), history of acute lower respiratory tract infection, bronchiectasis with acute lower respiratory infection, rti - respiratory tract infection, bacterial respiratory infection,...|

Here are the updated resolver notebooks:

> - [3.Clinical_Entity_Resolvers.ipynb](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/3.Clinical_Entity_Resolvers.ipynb)
> - [24.Improved_Entity_Resolvers_in_SparkNLP_with_sBert.ipynb](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/24.Improved_Entity_Resolvers_in_SparkNLP_with_sBert.ipynb)

You can also check for more examples of this annotator: [24.1.Improved_Entity_Resolution_with_SentenceChunkEmbeddings.ipynb](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/24.1.Improved_Entity_Resolution_with_SentenceChunkEmbeddings.ipynb)

+ We have updated TF Graph builder notebook to show how to create TF graphs with TF2.x.

> Here is the updated notebook: [17.Graph_builder_for_DL_models.ipynb](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/17.Graph_builder_for_DL_models.ipynb)  

**To see more, please check: [Spark NLP Healthcare Workshop Repo](https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/tutorials/Certification_Trainings/Healthcare)**


#### New TF Graph Builder

TF graph builder to create graphs and train DL models for licensed annotators (MedicalNer, Relation Extraction, Assertion and Generic Classifier) is made compatible with TF2.x.

To see how to create TF Graphs, you can check here: [17.Graph_builder_for_DL_models.ipynb](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/17.Graph_builder_for_DL_models.ipynb)  

## 3.2.0
We are glad to announce that Spark NLP Healthcare 3.2.0 has been released!.

#### Highlights

+ New Sentence Boundary Detection Model for Healthcare text
+ New Assertion Status Models
+ New Sentence Entity Resolver Model
+ Finetuning Sentence Entity Resolvers with Your Data
+ New Clinical NER Models
+ New CMS-HCC risk-adjustment score calculation module
+ New Embedding generation module for entity resolution


##### New Sentence Boundary Detection Model for Healthcare text

We are releasing an updated Sentence Boundary detection model to identify complex sentences containing multiple measurements, and punctuations. This model is trained on an in-house dataset.

*Example*:

*Python:*
```bash
...
documenter = DocumentAssembler()\
  .setInputCol("text")\
  .setOutputCol("document")

sentencerDL = SentenceDetectorDLModel
  .pretrained("sentence_detector_dl_healthcare","en","clinical/models")
  .setInputCols(["document"])
  .setOutputCol("sentences")

text = """He was given boluses of MS04 with some effect.he has since been placed on a PCA . He takes 80 mg. of ativan at home ativan for anxiety,
with 20 meq kcl po, 30 mmol K-phos iv and 2 gms mag so4 iv.
Size: Prostate gland measures 10x1.1x 4.9 cm (LS x AP x TS). Estimated volume is
51.9 ml. and is mildly enlarged in size.Normal delineation pattern of the prostate gland is preserved.
"""

sd_model = LightPipeline(PipelineModel(stages=[documenter, sentencerDL]))

result = sd_model.fullAnnotate(text)
```

*Results*:

```
| s.no | sentences                                                      |
|-----:|:---------------------------------------------------------------|
|    0 | He was given boluses of MS04 with some effect.                 |
|    1 | he has since been placed on a PCA .                            |
|    2 | He takes 80 mg. of ativan at home ativan for anxiety,          |
|      | with 20 meq kcl po, 30 mmol K-phos iv and 2 gms mag so4 iv.    |
|    3 | Size: Prostate gland measures 10x1.1x 4.9 cm (LS x AP x TS).   |
|    4 | Estimated volume is                                            |
|      | 51.9 ml. and is mildly enlarged in size.                       |
|    5 | Normal delineation pattern of the prostate gland is preserved. |

```

##### New Assertion Status Models

We are releasing two new Assertion Status Models based on the BiLSTM architecture. Apart from what we released in other assertion models, an in-house annotations on a curated dataset (6K clinical notes) is used to augment the base assertion dataset (2010 i2b2/VA).

+ `assertion_jsl`: This model can classify the assertions made on given medical concepts as being `Present`, `Absent`, `Possible`, `Planned`, `Someoneelse`, `Past`, `Family`, `None`, `Hypotetical`.

+ `assertion_jsl_large`: This model can classify the assertions made on given medical concepts as being `present`, `absent`, `possible`, `planned`, `someoneelse`, `past`.

*assertion_dl vs assertion_jsl*:

|chunks|entities|assertion_dl|assertion_jsl|
|-|-|-|-|
|Mesothelioma|PROBLEM|present|Present|
|CVA|PROBLEM|absent|Absent|
|cancer|PROBLEM|associated_with_someone_else|Family|
|her INR|TEST|present|Planned|
|Amiodarone|TREATMENT|hypothetical|Hypothetical|
|lymphadenopathy|PROBLEM|absent|Absent|
|stage III disease|PROBLEM|possible|Possible|
|IV piggyback|TREATMENT|conditional|Past|


*Example*:

*Python:*
```bash
...
clinical_assertion = AssertionDLModel.pretrained("assertion_jsl", "en", "clinical/models") \
    .setInputCols(["sentence", "ner_chunk", "embeddings"]) \
    .setOutputCol("assertion")

nlpPipeline = Pipeline(stages=[documentAssembler, sentenceDetector, tokenizer, word_embeddings, clinical_ner, ner_converter, clinical_assertion])
model = nlpPipeline.fit(spark.createDataFrame([[""]]).toDF("text"))

result = model.transform(spark.createDataFrame([["The patient is a 41-year-old and has a nonproductive cough that started last week. She has had right-sided chest pain radiating to her back with fever starting today. She has no nausea. She has a history of pericarditis and pericardectomy in May 2006 and developed cough with right-sided chest pain, and went to an urgent care center and Chest x-ray revealed right-sided pleural effusion. In family history, her father has a colon cancer history."]], ["text"])
```

*Results*:

```
+-------------------+-----+---+-------------------------+-------+---------+
|chunk              |begin|end|ner_label                |sent_id|assertion|
+-------------------+-----+---+-------------------------+-------+---------+
|nonproductive cough|35   |53 |Symptom                  |0      |Present  |
|last week          |68   |76 |RelativeDate             |0      |Past     |
|chest pain         |103  |112|Symptom                  |1      |Present  |
|fever              |141  |145|VS_Finding               |1      |Present  |
|today              |156  |160|RelativeDate             |1      |Present  |
|nausea             |174  |179|Symptom                  |2      |Absent   |
|pericarditis       |203  |214|Disease_Syndrome_Disorder|3      |Past     |
|pericardectomy     |220  |233|Procedure                |3      |Past     |
|May 2006           |238  |245|Date                     |3      |Past     |
|cough              |261  |265|Symptom                  |3      |Past     |
|chest pain         |284  |293|Symptom                  |3      |Past     |
|Chest x-ray        |334  |344|Test                     |3      |Past     |
|pleural effusion   |367  |382|Disease_Syndrome_Disorder|3      |Past     |
|colon cancer       |421  |432|Oncological              |4      |Family   |
+-------------------+-----+---+-------------------------+-------+---------+
```

#### New Sentence Entity Resolver Model

We are releasing `sbiobertresolve_rxnorm_disposition` model that maps medication entities (like drugs/ingredients) to RxNorm codes and their dispositions using `sbiobert_base_cased_mli` Sentence Bert Embeddings. In the result, look for the aux_label parameter in the metadata to get dispositions that were divided by `|`.

*Example*:

*Python*:
```bash

documentAssembler = DocumentAssembler()\
      .setInputCol("text")\
      .setOutputCol("ner_chunk")

sbert_embedder = BertSentenceEmbeddings.pretrained('sbiobert_base_cased_mli', 'en','clinical/models')\
      .setInputCols(["ner_chunk"])\
      .setOutputCol("sbert_embeddings")

rxnorm_resolver = SentenceEntityResolverModel.pretrained("sbiobertresolve_rxnorm_disposition", "en", "clinical/models") \
      .setInputCols(["ner_chunk", "sbert_embeddings"]) \
      .setOutputCol("rxnorm_code")\
      .setDistanceFunction("EUCLIDEAN")

pipelineModel = PipelineModel(
    stages = [
        documentAssembler,
        sbert_embedder,
        rxnorm_resolver
    ])

rxnorm_lp = LightPipeline(pipelineModel)

result = rxnorm_lp.fullAnnotate("belimumab 80 mg/ml injectable solution")
```

*Results*:

```
|    | chunks                                | code    | resolutions                                                                                                                                                                                 | all_codes                                         | all_k_aux_labels                                                                            | all_distances                                 |
|---:|:--------------------------------------|:--------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:--------------------------------------------------|:--------------------------------------------------------------------------------------------|:----------------------------------------------|
|  0 |belimumab 80 mg/ml injectable solution | 1092440 | [belimumab 80 mg/ml injectable solution, belimumab 80 mg/ml injectable solution [benlysta], ifosfamide 80 mg/ml injectable solution, belimumab 80 mg/ml [benlysta], belimumab 80 mg/ml, ...]| [1092440, 1092444, 107034, 1092442, 1092438, ...] | [Immunomodulator, Immunomodulator, Alkylating agent, Immunomodulator, Immunomodulator, ...] | [0.0000, 0.0145, 0.0479, 0.0619, 0.0636, ...] |
```

#### Finetuning Sentence Entity Resolvers with Your Data

Instead of starting from scratch when training a new Sentence Entity Resolver model, you can train a new model by adding your new data to the pretrained model.

There's a new method `setPretrainedModelPath(path)`, which allows you to point the training process to an existing model, and allows you to initialize your model with the data from the pretrained model.

When both the new data and the pretrained model contain the same code, you will see both of the results at the top.

Here is a sample notebook : [Finetuning Sentence Entity Resolver Model Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/13.1.Finetuning_Sentence_Entity_Resolver_Model.ipynb)

*Example:*

In the example below, we changed the code of `sepsis` to `X1234` and re-retrain the main ICD10-CM model with this new dataset. So we want to see the `X1234` code as a result in the all_codes.

*Python:*
```bash
...
bertExtractor = SentenceEntityResolverApproach()\
  .setNeighbours(50)\
  .setThreshold(1000)\
  .setInputCols("sentence_embeddings")\
  .setNormalizedCol("description_normalized")\   # concept_name
  .setLabelCol("code")\     # concept_code
  .setOutputCol("recognized_code")\
  .setDistanceFunction("EUCLIDEAN")\
  .setCaseSensitive(False)\         
  .setUseAuxLabel(True)\         # if exist  
  .setPretrainedModelPath("path_to_a_pretrained_model")


new_model = bertExtractor.fit("new_dataset")
new_model.save("models/new_resolver_model")  # save and use later
```

```
...
resolver_model = SentenceEntityResolverModel.load("models/new_resolver_model") \
      .setInputCols(["ner_chunk", "sentence_embeddings"]) \
      .setOutputCol("output_code")

pipelineModel = PipelineModel(
    stages = [
        documentAssembler,
        sentence_embedder,
        resolver_model])

light_model = LightPipeline(pipelineModel)
light_model.fullAnnotate("sepsis")
```

*Main Model Results*:

|chunks |begin |end |code |all_codes |resolutions |all_k_aux_labels |all_distances|
|-|-|-|-|-|-|-|-|
|sepsis   |0   |5   |A4189 |[A4189, L419, A419, A267, E771, ...]   | [sepsis [Other specified sepsis], parapsoriasis [Parapsoriasis, unspecified], postprocedural sepsis [Sepsis, unspecified organism], erysipelothrix sepsis [Erysipelothrix sepsis], fucosidosis [Defects in glycoprotein degradation], ... ]| [1\|1\|2, 1\|1\|2, 1\|1\|2, 1\|1\|2, 1\|1\|23, ...]  |[0.0000, 0.2079, 0.2256, 0.2359, 0.2399,...]   |

*Re-Trained Model Results*:

|chunks |begin |end |code |all_codes |resolutions |all_k_aux_labels |all_distances|
|-|-|-|-|-|-|-|-|
|sepsis   |0   |5   |X1234 |[X1234, A4189, A419, L419, A267, ...]   | [sepsis [Sepsis, new resolution], sepsis [Other specified sepsis], SEPSIS [Sepsis, unspecified organism], parapsoriasis [Parapsoriasis, unspecified], erysipelothrix sepsis [Erysipelothrix sepsis], ... ]| [1\|1\|74, 1\|1\|2, 1\|1\|2, 1\|1\|2, 1\|1\|2, ...]  |[0.0000, 0.0000, 0.0000, 0.2079, 0.2359, ...]   |

#### New Clinical NER Models

+ `ner_jsl_slim`: This model is trained based on `ner_jsl` model with more generalized entities.

  (`Death_Entity`, `Medical_Device`, `Vital_Sign`, `Alergen`, `Drug`, `Clinical_Dept`, `Lifestyle`, `Symptom`, `Body_Part`, `Physical_Measurement`, `Admission_Discharge`, `Date_Time`, `Age`, `Birth_Entity`, `Header`, `Oncological`, `Substance_Quantity`, `Test_Result`, `Test`, `Procedure`, `Treatment`, `Disease_Syndrome_Disorder`, `Pregnancy_Newborn`, `Demographics`)

*ner_jsl vs ner_jsl_slim*:

|chunks|ner_jsl|ner_jsl_slim|
|-|-|-|
|Description:|Section_Header|Header|
|atrial fibrillation|Heart_Disease|Disease_Syndrome_Disorder|
|August 24, 2007|Date|Date_Time|
|transpleural fluoroscopy|Procedure|Test|
|last week|RelativeDate|Date_Time|
|She|Gender|Demographics|
|fever|VS_Finding|Vital_Sign|
|PAST MEDICAL HISTORY:|Medical_History_Header|Header|
|Pericardial window|Internal_organ_or_component|Body_Part|
|FAMILY HISTORY:|Family_History_Header|Header|
|CVA|Cerebrovascular_Disease|Disease_Syndrome_Disorder|
|diabetes|Diabetes|Disease_Syndrome_Disorder|
|married|Relationship_Status|Demographics|
|alcohol|Alcohol|Lifestyle|
|illicit drug|Substance|Lifestyle|
|Coumadin|Drug_BrandName|Drug|
|Blood pressure 123/95|Blood_Pressure|Vital_Sign|
|heart rate 83|Pulse|Vital_Sign|
|anticoagulated|Drug_Ingredient|Drug|


*Example*:

*Python*:

```bash
...
embeddings_clinical = WordEmbeddingsModel().pretrained('embeddings_clinical', 'en', 'clinical/models') \
    .setInputCols(['sentence', 'token']) \
    .setOutputCol('embeddings')

clinical_ner = MedicalNerModel.pretrained("ner_jsl_slim", "en", "clinical/models") \
  .setInputCols(["sentence", "token", "embeddings"]) \
  .setOutputCol("ner")
...

nlpPipeline = Pipeline(stages=[document_assembler, sentence_detector, tokenizer, embeddings_clinical,  clinical_ner, ner_converter])
model = nlpPipeline.fit(spark.createDataFrame([[""]]).toDF("text"))

results = model.transform(spark.createDataFrame([["HISTORY: 30-year-old female presents for digital bilateral mammography secondary to a soft tissue lump palpated by the patient in the upper right shoulder. The patient has a family history of breast cancer within her mother at age 58. Patient denies personal history of breast cancer."]], ["text"]))
```

*Results*:
```bash
|    | chunk            | entity       |
|---:|:-----------------|:-------------|
|  0 | HISTORY:         | Header       |
|  1 | 30-year-old      | Age          |
|  2 | female           | Demographics |
|  3 | mammography      | Test         |
|  4 | soft tissue lump | Symptom      |
|  5 | shoulder         | Body_Part    |
|  6 | breast cancer    | Oncological  |
|  7 | her mother       | Demographics |
|  8 | age 58           | Age          |
|  9 | breast cancer    | Oncological  |
```

+ `ner_jsl_biobert` : This model is the BioBert version of `ner_jsl` model and trained with `biobert_pubmed_base_cased` embeddings.

+ `ner_jsl_greedy_biobert` : This model is the BioBert version of `ner_jsl_greedy` models and trained with `biobert_pubmed_base_cased` embeddings.

*Example*:

*Python*:

```bash
...
embeddings_clinical = BertEmbeddings.pretrained('biobert_pubmed_base_cased') \
    .setInputCols(['sentence', 'token']) \
    .setOutputCol('embeddings')
clinical_ner = MedicalNerModel.pretrained("ner_jsl_greedy_biobert", "en", "clinical/models") \
  .setInputCols(["sentence", "token", "embeddings"]) \
  .setOutputCol("ner")
...
nlpPipeline = Pipeline(stages=[document_assembler, sentence_detector, tokenizer, embeddings_clinical,  clinical_ner, ner_converter])
model = nlpPipeline.fit(spark.createDataFrame([[""]]).toDF("text"))
results = model.transform(spark.createDataFrame([["The patient is a 21-day-old Caucasian male here for 2 days of congestion - mom has been suctioning yellow discharge from the patient's nares, plus she has noticed some mild problems with his breathing while feeding (but negative for any perioral cyanosis or retractions). One day ago, mom also noticed a tactile temperature and gave the patient Tylenol. Baby also has had some decreased p.o. intake. His normal breast-feeding is down from 20 minutes q.2h. to 5 to 10 minutes secondary to his respiratory congestion. He sleeps well, but has been more tired and has been fussy over the past 2 days. The parents noticed no improvement with albuterol treatments given in the ER. His urine output has also decreased; normally he has 8 to 10 wet and 5 dirty diapers per 24 hours, now he has down to 4 wet diapers per 24 hours. Mom denies any diarrhea. His bowel movements are yellow colored and soft in nature."]], ["text"]))
```

*Results*:

```bash
|    | chunk                                          | entity                       |
|---:|:-----------------------------------------------|:-----------------------------|
|  0 | 21-day-old                                     | Age                          |
|  1 | Caucasian                                      | Race_Ethnicity               |
|  2 | male                                           | Gender                       |
|  3 | for 2 days                                     | Duration                     |
|  4 | congestion                                     | Symptom                      |
|  5 | mom                                            | Gender                       |
|  6 | suctioning yellow discharge                    | Symptom                      |
|  7 | nares                                          | External_body_part_or_region |
|  8 | she                                            | Gender                       |
|  9 | mild problems with his breathing while feeding | Symptom                      |
| 10 | perioral cyanosis                              | Symptom                      |
| 11 | retractions                                    | Symptom                      |
| 12 | One day ago                                    | RelativeDate                 |
| 13 | mom                                            | Gender                       |
| 14 | tactile temperature                            | Symptom                      |
| 15 | Tylenol                                        | Drug                         |
| 16 | Baby                                           | Age                          |
| 17 | decreased p.o. intake                          | Symptom                      |
| 18 | His                                            | Gender                       |
| 19 | breast-feeding                                 | External_body_part_or_region |
| 20 | q.2h                                           | Frequency                    |
| 21 | to 5 to 10 minutes                             | Duration                     |
| 22 | his                                            | Gender                       |
| 23 | respiratory congestion                         | Symptom                      |
| 24 | He                                             | Gender                       |
| 25 | tired                                          | Symptom                      |
| 26 | fussy                                          | Symptom                      |
| 27 | over the past 2 days                           | RelativeDate                 |
| 28 | albuterol                                      | Drug                         |
| 29 | ER                                             | Clinical_Dept                |
| 30 | His                                            | Gender                       |
| 31 | urine output has also decreased                | Symptom                      |
| 32 | he                                             | Gender                       |
| 33 | per 24 hours                                   | Frequency                    |
| 34 | he                                             | Gender                       |
| 35 | per 24 hours                                   | Frequency                    |
| 36 | Mom                                            | Gender                       |
| 37 | diarrhea                                       | Symptom                      |
| 38 | His                                            | Gender                       |
| 39 | bowel                                          | Internal_organ_or_component  |
```

#### New CMS-HCC risk-adjustment score calculation module

We are releasing a new module to calculate medical risk adjusment score by using the Centers for Medicare & Medicaid Service (CMS) risk adjustment model. The main input to this model are ICD codes of the diseases. After getting ICD codes of diseases by Spark NLP Healthcare ICD resolvers, risk score can be calculated by this module in spark environment.
Current supported version for the model is CMS-HCC V24.

The model needs following parameters in order to calculate the risk score:
- ICD Codes
- Age
- Gender
- The eligibility segment of the patient
- Original reason for entitlement
- If the patient is in Medicaid or not
- If the patient is disabled or not

*Example*:

*Python:*
```
sample_patients.show()
```
*Results*:
```
+----------+------------------------------+---+------+
|Patient_ID|ICD_codes                     |Age|Gender|
+----------+------------------------------+---+------+
|101       |[E1169, I5030, I509, E852]    |64 |F     |
|102       |[G629, D469, D6181]           |77 |M     |
|103       |[D473, D473, D473, M069, C969]|16 |F     |
+----------+------------------------------+---+------+
```


*Python:*
```
from sparknlp_jsl.functions import profile
df = df.withColumn("hcc_profile", profile(df.ICD_codes, df.Age, df.Gender))

df = df.withColumn("hcc_profile", F.from_json(F.col("hcc_profile"), schema))
df= df.withColumn("risk_score", df.hcc_profile.getItem("risk_score"))\
      .withColumn("hcc_lst", df.hcc_profile.getItem("hcc_map"))\
      .withColumn("parameters", df.hcc_profile.getItem("parameters"))\
      .withColumn("details", df.hcc_profile.getItem("details"))\

df.select('Patient_ID', 'risk_score','ICD_codes', 'Age', 'Gender').show(truncate=False )

df.show(truncate=100, vertical=True)

```

*Results*:
{% raw %}
```

+----------+----------+------------------------------+---+------+
|Patient_ID|risk_score|ICD_codes                     |Age|Gender|
+----------+----------+------------------------------+---+------+
|101       |0.827     |[E1169, I5030, I509, E852]    |64 |F     |
|102       |1.845     |[G629, D469, D6181]           |77 |M     |
|103       |1.288     |[D473, D473, D473, M069, C969]|16 |F     |
+----------+----------+------------------------------+---+------+

RECORD 0-------------------------------------------------------------------------------------------------------------------
 Patient_ID          | 101                                                                                                  
 ICD_codes           | [E1169, I5030, I509, E852]                                                                           
 Age                 | 64                                                                                                   
 Gender              | F                                                                                                    
 Eligibility_Segment | CNA                                                                                                  
 OREC                | 0                                                                                                    
 Medicaid            | false                                                                                                 
 Disabled            | false                                                                                                
 hcc_profile         | {{"CNA_HCC18":0.302,"CNA_HCC85":0.331,"CNA_HCC23":0.194,"CNA_D3":0.0,"CNA_HCC85_gDiabetesMellit":...
 risk_score          | 0.827                                                                                                
 hcc_lst             | {"E1169":["HCC18"],"I5030":["HCC85"],"I509":["HCC85"],"E852":["HCC23"]}                              
 parameters          | {"elig":"CNA","age":64,"sex":"F","origds":'0',"disabled":false,"medicaid":false}                   
 details             | {"CNA_HCC18":0.302,"CNA_HCC85":0.331,"CNA_HCC23":0.194,"CNA_D3":0.0,"CNA_HCC85_gDiabetesMellit":0.0}
-RECORD 1-------------------------------------------------------------------------------------------------------------------
 Patient_ID          | 102                                                                                                  
 ICD_codes           | [G629, D469, D6181]                                                                                  
 Age                 | 77                                                                                                   
 Gender              | M                                                                                                    
 Eligibility_Segment | CNA                                                                                                  
 OREC                | 0                                                                                                    
 Medicaid            | false                                                                                                 
 Disabled            | false                                                                                                 
 hcc_profile         | {{"CNA_M75_79":0.473,"CNA_D1":0.0,"CNA_HCC46":1.372}, ["D1","HCC46"], {"D469":["HCC46"]}, {"elig"...
 risk_score          | 1.845                                                                                                
 hcc_lst             | {"D469":["HCC46"]}                                                                                   
 parameters          | {"elig":"CNA","age":77,"sex":"M","origds":'0',"disabled":false,"medicaid":false}                   
 details             | {"CNA_M75_79":0.473,"CNA_D1":0.0,"CNA_HCC46":1.372}                                                  
-RECORD 2-------------------------------------------------------------------------------------------------------------------
 Patient_ID          | 103                                                                                                  
 ICD_codes           | [D473, D473, D473, M069, C969]                                                                       
 Age                 | 16                                                                                                   
 Gender              | F                                                                                                    
 Eligibility_Segment | CNA                                                                                                  
 OREC                | 0                                                                                                    
 Medicaid            | false                                                                                                
 Disabled            | false                                                                                                
 hcc_profile         | {{"CNA_HCC10":0.675,"CNA_HCC40":0.421,"CNA_HCC48":0.192,"CNA_D3":0.0}, ["HCC10","HCC40","HCC48","...
 risk_score          | 1.288                                                                                                
 hcc_lst             | {"D473":["HCC48"],"M069":["HCC40"],"C969":["HCC10"]}                                                 
 parameters          | {"elig":"CNA","age":16,"sex":"F","origds":'0',"disabled":false,"medicaid":false}                   
 details             | {"CNA_HCC10":0.675,"CNA_HCC40":0.421,"CNA_HCC48":0.192,"CNA_D3":0.0}
```
{% endraw %}

Here is a sample notebook : [Calculating Medicare Risk Adjustment Score](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/3.1.Calculate_Medicare_Risk_Adjustment_Score.ipynb)

#### New Embedding generation module for entity resolution

We are releasing a new annotator `BertSentenceChunkEmbeddings` to let users aggregate sentence embeddings and ner chunk embeddings to get more specific and accurate resolution codes. It works by averaging context and chunk embeddings to get contextual information. This is specially helpful when ner chunks do not have additional information (like body parts or severity) as explained in the example below. Input to this annotator is the context (sentence) and ner chunks, while the output is embedding for each chunk that can be fed to the resolver model. The `setChunkWeight` parameter can be used to control the influence of surrounding context. Example below shows the comparison of old vs new approach.

Sample Notebook: [Improved_Entity_Resolution_with_SentenceChunkEmbeddings](https://colab.research.google.com/github/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/24.1.Improved_Entity_Resolution_with_SentenceChunkEmbeddings.ipynb)

*Example*:

*Python:*
```
...
sentence_chunk_embeddings = BertSentenceChunkEmbeddings\
    .pretrained("sbiobert_base_cased_mli", "en", "clinical/models")\
    .setInputCols(["sentences", "ner_chunk"])\
    .setOutputCol("sentence_chunk_embeddings")\
    .setChunkWeight(0.5)

resolver = SentenceEntityResolverModel.pretrained('sbiobertresolve_icd10cm', 'en', 'clinical/models')\
            .setInputCols(["ner_chunk", "sentence_chunk_embeddings"]) \
              .setOutputCol("resolution")

text = """A 20 year old female patient badly tripped while going down stairs. She complains of right leg pain.
Her x-ray showed right hip fracture. Hair line fractures also seen on the left knee joint.
She also suffered from trauma and slight injury on the head.

OTHER CONDITIONS: She was also recently diagnosed with diabetes, which is of type 2.
"""

nlpPipeline = Pipeline(stages=[document_assembler, sentence_detector, tokenizer, embeddings_clinical,  clinical_ner, ner_converter, sentence_chunk_embeddings, resolver])
model = nlpPipeline.fit(spark.createDataFrame([[""]]).toDF("text"))
results = model.transform(spark.createDataFrame([[text]], ["text"]))
```

*Results*:
```
|    | chunk               | entity              | code_with_old_approach | resolutions_with_old_approach                              | code_with_new_approach | resolutions_with_new_approach                                                                 |
|---:|:--------------------|:--------------------|:-----------------------|:-----------------------------------------------------------|:-----------------------|:----------------------------------------------------------------------------------------------|
|  0 | leg pain            | Symptom             | R1033                  | Periumbilical pain                                         | M79661                 | Pain in right lower leg                                                                       |
|  1 | hip fracture        | Injury_or_Poisoning | M84459S                | Pathological fracture, hip, unspecified, sequela           | M84451S                | Pathological fracture, right femur, sequela                                                   |
|  2 | Hair line fractures | Injury_or_Poisoning | S070XXS                | Crushing injury of face, sequela                           | S92592P                | Other fracture of left lesser toe(s), subsequent encounter for fracture with malunion         |
|  3 | trauma              | Injury_or_Poisoning | T794XXS                | Traumatic shock, sequela                                   | S0083XS                | Contusion of other part of head, sequela                                                      |
|  4 | slight injury       | Injury_or_Poisoning | B03                    | Smallpox                                                   | S0080XD                | Unspecified superficial injury of other part of head, subsequent encounter                    |
|  5 | diabetes            | Diabetes            | E118                   | Type 2 diabetes mellitus with unspecified complications    | E1169                  | Type 2 diabetes mellitus with other specified complication                                    |
```

**To see more, please check :** [Spark NLP Healthcare Workshop Repo](https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/tutorials/Certification_Trainings/Healthcare)


## 3.1.3
We are glad to announce that Spark NLP for Healthcare 3.1.3 has been released!.
This release comes with new features, new models, bug fixes, and examples.

#### Highlights
+ New Relation Extraction model and a Pretrained pipeline for extracting and linking ADEs
+ New Entity Resolver model for SNOMED codes
+ ChunkConverter Annotator
+ BugFix: getAnchorDateMonth method in DateNormalizer.
+ BugFix: character map in MedicalNerModel fine-tuning.

##### New Relation Extraction model and a Pretrained pipeline for extracting and linking ADEs

We are releasing a new Relation Extraction Model for ADEs. This model is trained using Bert Word embeddings (`biobert_pubmed_base_cased`), and is capable of linking ADEs and Drugs.

*Example*:

```python
re_model = RelationExtractionModel()\
        .pretrained("re_ade_biobert", "en", 'clinical/models')\
        .setInputCols(["embeddings", "pos_tags", "ner_chunks", "dependencies"])\
        .setOutputCol("relations")\
        .setMaxSyntacticDistance(3)\ #default: 0
        .setPredictionThreshold(0.5)\ #default: 0.5
        .setRelationPairs(["ade-drug", "drug-ade"]) # Possible relation pairs. Default: All Relations.

nlp_pipeline = Pipeline(stages=[documenter, sentencer, tokenizer, words_embedder, pos_tagger, ner_tagger, ner_chunker, dependency_parser, re_model])

light_pipeline = LightPipeline(nlp_pipeline.fit(spark.createDataFrame([['']]).toDF("text")))

text ="""Been taking Lipitor for 15 years , have experienced sever fatigue a lot!!! . Doctor moved me to voltaren 2 months ago , so far , have only experienced cramps"""

annotations = light_pipeline.fullAnnotate(text)
```

We also have a new pipeline comprising of all models related to ADE(Adversal Drug Event) as part of this release. This pipeline includes classification, NER, assertion and relation extraction models. Users can now use this pipeline to get classification result, ADE and Drug entities, assertion status for ADE entities, and relations between ADE and Drug entities.

Example:

```python
    pretrained_ade_pipeline = PretrainedPipeline('explain_clinical_doc_ade', 'en', 'clinical/models')

    result = pretrained_ade_pipeline.fullAnnotate("""Been taking Lipitor for 15 years , have experienced sever fatigue a lot!!! . Doctor moved me to voltaren 2 months ago , so far , have only experienced cramps""")[0]
```

*Results*:

```bash
Class: True

NER_Assertion:
|    | chunk                   | entitiy    | assertion   |
|----|-------------------------|------------|-------------|
| 0  | Lipitor                 | DRUG       | -           |
| 1  | sever fatigue           | ADE        | Conditional |
| 2  | voltaren                | DRUG       | -           |
| 3  | cramps                  | ADE        | Conditional |

Relations:
|    | chunk1                        | entitiy1   | chunk2      | entity2 | relation |
|----|-------------------------------|------------|-------------|---------|----------|
| 0  | sever fatigue                 | ADE        | Lipitor     | DRUG    |        1 |
| 1  | cramps                        | ADE        | Lipitor     | DRUG    |        0 |
| 2  | sever fatigue                 | ADE        | voltaren    | DRUG    |        0 |
| 3  | cramps                        | ADE        | voltaren    | DRUG    |        1 |

```
##### New Entity Resolver model for SNOMED codes

We are releasing a new SentenceEntityResolver model for SNOMED codes. This model also includes AUX SNOMED concepts and can find codes for Morph Abnormality, Procedure, Substance, Physical Object, and Body Structure entities. In the metadata, the `all_k_aux_labels` can be divided to get further information: `ground truth`, `concept`, and `aux` . In the example shared below the ground truth is `Atherosclerosis`, concept is `Observation`, and aux is `Morph Abnormality`.

*Example*:

```python
snomed_resolver = SentenceEntityResolverModel.pretrained("sbiobertresolve_snomed_findings_aux_concepts", "en", "clinical/models") \
     .setInputCols(["ner_chunk", "sbert_embeddings"]) \
     .setOutputCol("snomed_code")\
     .setDistanceFunction("EUCLIDEAN")

snomed_pipelineModel = PipelineModel(
    stages = [
        documentAssembler,
        sbert_embedder,
        snomed_resolver])

snomed_lp = LightPipeline(snomed_pipelineModel)
result = snomed_lp.fullAnnotate("atherosclerosis")

```

*Results*:

```bash
|    | chunks          | code     | resolutions                                                                                                                                                                                                                                                                                                                                                                                                                    | all_codes                                                                                                                                                                                          | all_k_aux_labels                                      | all_distances                                                                                                                                   |
|---:|:----------------|:---------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:------------------------------------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------|
|  0 | atherosclerosis | 38716007 | [atherosclerosis, atherosclerosis, atherosclerosis, atherosclerosis, atherosclerosis, atherosclerosis, atherosclerosis artery, coronary atherosclerosis, coronary atherosclerosis, coronary atherosclerosis, coronary atherosclerosis, coronary atherosclerosis, arteriosclerosis, carotid atherosclerosis, cardiovascular arteriosclerosis, aortic atherosclerosis, aortic atherosclerosis, atherosclerotic ischemic disease] | [38716007, 155382007, 155414001, 195251000, 266318005, 194848007, 441574008, 443502000, 41702007, 266231003, 155316000, 194841001, 28960008, 300920004, 39468009, 155415000, 195252007, 129573006] | 'Atherosclerosis', 'Observation', 'Morph Abnormality' | [0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0280, 0.0451, 0.0451, 0.0451, 0.0451, 0.0451, 0.0462, 0.0477, 0.0466, 0.0490, 0.0490, 0.0485 |
```


##### ChunkConverter Annotator

Allows to use RegexMather chunks as NER chunks and feed the output to the downstream annotators like RE or Deidentification.

``` Python
        document_assembler = DocumentAssembler().setInputCol('text').setOutputCol('document')

        sentence_detector = SentenceDetector().setInputCols(["document"]).setOutputCol("sentence")

        regex_matcher = RegexMatcher()\
            .setInputCols("sentence")\
            .setOutputCol("regex")\
            .setExternalRules(path="../src/test/resources/regex-matcher/rules.txt",delimiter=",")

        chunkConverter = ChunkConverter().setInputCols("regex").setOutputCol("chunk")


```


## 3.1.2
We are glad to announce that Spark NLP for Healthcare 3.1.2 has been released!.
This release comes with new features, new models, bug fixes, and examples.

#### Highlights
+ Support for Fine-tuning of Ner models.
+ More builtin(pre-defined) graphs for MedicalNerApproach.
+ Date Normalizer.
+ New Relation Extraction Models for ADE.
+ Bug Fixes.
+ Support for user-defined Custom Transformer.
+ Java Workshop Examples.
+ Deprecated Compatibility class in Python.


##### Support for Fine Tuning of Ner models

Users can now resume training/fine-tune existing(already trained) Spark NLP MedicalNer models on new data. Users can simply provide the path to any existing MedicalNer model and train it further on the new dataset:

```
ner_tagger = MedicalNerApproach().setPretrainedModelPath("/path/to/trained/medicalnermodel")
```

If the new dataset contains new tags/labels/entities, users can choose to override existing tags with the new ones. The default behaviour is to reset the list of existing tags and generate a new list from the new dataset. It is also possible to preserve the existing tags by setting the 'overrideExistingTags' parameter:

```
ner_tagger = MedicalNerApproach()\
  .setPretrainedModelPath("/path/to/trained/medicalnermodel")\
  .setOverrideExistingTags(False)
```

Setting overrideExistingTags to false is intended to be used when resuming trainig on the same, or very similar dataset (i.e. with the same tags or with just a few different ones).

If tags overriding is disabled, and new tags are found in the training set, then the approach will try to allocate them to unused output nodes, if any. It is also possible to override specific tags of the old model by mapping them to new tags:


```bash
ner_tagger = MedicalNerApproach()\
  .setPretrainedModelPath("/path/to/trained/medicalnermodel")\
  .setOverrideExistingTags(False)\
  .setTagsMapping("B-PER,B-VIP", "I-PER,I-VIP")
```

In this case, the new tags `B-VIP` and `I-VIP` will replace the already trained tags 'B-PER' and 'I-PER'. Unmapped old tags will remain in use and unmapped new tags will be allocated to new outpout nodes, if any.

Jupyter Notebook: [Finetuning Medical NER Model Notebook] (https://colab.research.google.com/github/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/1.5.Resume_MedicalNer_Model_Training.ipynb)

##### More builtin graphs for MedicalNerApproach

Seventy new TensorFlow graphs have been added to the library of available graphs which are used to train MedicalNer models. The graph with the optimal set of parameters is automatically chosen by MedicalNerApproach.


##### DateNormalizer

New annotator that normalize dates to the format YYYY/MM/DD.
This annotator identifies dates in chunk annotations, and transform these dates to the format YYYY/MM/DD.
Both the input and output formats for the annotator are `chunk`.

Example:


```bash
	sentences = [
		    ['08/02/2018'],
		    ['11/2018'],
		    ['11/01/2018'],
		    ['12Mar2021'],
		    ['Jan 30, 2018'],
		    ['13.04.1999'],
		    ['3April 2020'],
		    ['next monday'],
		    ['today'],
		    ['next week'],
	]
	df = spark.createDataFrame(sentences).toDF("text")
	document_assembler = DocumentAssembler().setInputCol('text').setOutputCol('document')
	chunksDF = document_assembler.transform(df)
	aa = map_annotations_col(chunksDF.select("document"),
				    lambda x: [Annotation('chunk', a.begin, a.end, a.result, a.metadata, a.embeddings) for a in x], "document",
				    "chunk_date", "chunk")
	dateNormalizer = DateNormalizer().setInputCols('chunk_date').setOutputCol('date').setAnchorDateYear(2021).setAnchorDateMonth(2).setAnchorDateDay(27)
	dateDf = dateNormalizer.transform(aa)
	dateDf.select("date.result","text").show()
```

```bash

+-----------+----------+
|text        |  date    |
+-----------+----------+
|08/02/2018  |2018/08/02|
|11/2018     |2018/11/DD|
|11/01/2018  |2018/11/01|
|12Mar2021   |2021/03/12|
|Jan 30, 2018|2018/01/30|
|13.04.1999  |1999/04/13|
|3April 2020 |2020/04/03|
|next Monday |2021/06/19|
|today       |2021/06/12|
|next week   |2021/06/19|
+-----------+----------+
```

##### New Relation Extraction Models for ADE

We are releasing new Relation Extraction models for ADE (Adverse Drug Event). This model is available in both `RelationExtraction` and Bert based `RelationExtractionDL` versions, and is capabale of linking drugs with ADE mentions.

Example

```bash
    ade_re_model = new RelationExtractionModel().pretrained('ner_ade_clinical', 'en', 'clinical/models')\
                                     .setInputCols(["embeddings", "pos_tags", "ner_chunk", "dependencies"])\
                                     .setOutputCol("relations")\
                                     .setPredictionThreshold(0.5)\
                                     .setRelationPairs(['ade-drug', 'drug-ade'])
    pipeline = Pipeline(stages=[documenter, sentencer, tokenizer, pos_tagger, words_embedder, ner_tagger, ner_converter,
                                                   dependency_parser, re_ner_chunk_filter, re_model])
    text ="""A 30 year old female presented with tense bullae due to excessive use of naproxin, and leg cramps relating to oxaprozin."""

    p_model = pipeline.fit(spark.createDataFrame([[text]]).toDF("text"))

    result = p_model.transform(data)

```

Results
```bash
|    | chunk1        | entity1    |  chunk2       | entity2   |    result  |
|---:|:--------------|:-----------|:--------------|:----------|-----------:|
|  0 | tense bullae  | ADE        | naproxin      | DRUG      |          1 |
|  1 | tense bullae  | ADE        | oxaprozin     | DRUG      |          0 |
|  2 | naproxin      | DRUG       | leg cramps    | ADE       |          0 |
|  3 | leg cramps    | ADE        | oxaprozin     | DRUG      |          1 |

```

Benchmarking
Model: `re_ade_clinical`
```bash

              precision    recall  f1-score   support
           0       0.85      0.89      0.87      1670
           1       0.88      0.84      0.86      1673
   micro avg       0.87      0.87      0.87      3343
   macro avg       0.87      0.87      0.87      3343
weighted avg       0.87      0.87      0.87      3343
```

Model: `redl_ade_biobert`
```bash
Relation           Recall Precision        F1   Support
0                   0.894     0.946     0.919      1011
1                   0.963     0.926     0.944      1389
Avg.                0.928     0.936     0.932
Weighted Avg.       0.934     0.934     0.933
```

##### Bug Fixes
+ RelationExtractionDLModel had an issue(BufferOverflowException) on versions 3.1.0 and 3.1.1, which is fixed with this release.
+ Some pretrained RelationExtractionDLModels got outdated after release 3.0.3, new updated models were created, tested and made available to be used with versions 3.0.3, and later.
+ Some SentenceEntityResolverModels which did not work with Spark 2.4/2.3 were fixed.

##### Support for user-defined Custom Transformer.
Utility classes to define custom transformers in python are included in this release. This allows users to define functions in Python to manipulate Spark-NLP annotations. This new Transformers can be added to pipelines like any of the other models you're already familiar with.
Example how to use the custom transformer.

```python
        def myFunction(annotations):
            # lower case the content of the annotations
            return [a.copy(a.result.lower()) for a in annotations]

        custom_transformer = CustomTransformer(f=myFunction).setInputCol("ner_chunk").setOutputCol("custom")
        outputDf = custom_transformer.transform(outdf).select("custom").toPandas()
```

##### Java Workshop Examples

Add Java examples in the workshop repository.
https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/java/healthcare

##### Deprecated Compatibility class in Python

Due to active release cycle, we are adding & training new pretrained models at each release and it might be tricky to maintain the backward compatibility or keep up with the latest models and versions, especially for the users using our models locally in air-gapped networks.

We are releasing a new utility class to help you check your local & existing models with the latest version of everything we have up to date. You will not need to specify your AWS credentials from now on. This new class is now replacing the previous Compatibility class written in Python and CompatibilityBeta class written in Scala.

```        
from sparknlp_jsl.compatibility import Compatibility

compatibility = Compatibility(spark)

print(compatibility.findVersion('sentence_detector_dl_healthcare'))
```

Output
```
[{'name': 'sentence_detector_dl_healthcare', 'sparkVersion': '2.4', 'version': '2.6.0', 'language': 'en', 'date': '2020-09-13T14:44:42.565', 'readyToUse': 'true'}, {'name': 'sentence_detector_dl_healthcare', 'sparkVersion': '2.4', 'version': '2.7.0', 'language': 'en', 'date': '2021-03-16T08:42:34.391', 'readyToUse': 'true'}]
```


## 3.1.1
We are glad to announce that Spark NLP for Healthcare 3.1.1 has been released!

#### Highlights
+ MedicalNerModel new parameter `includeAllConfidenceScores`.
+ MedicalNerModel new parameter `inferenceBatchSize`.
+ New Resolver Models
+ Updated Resolver Models
+ Getting Started with Spark NLP for Healthcare Notebook in Databricks

#### MedicalNer new parameter `includeAllConfidenceScores`
You can now customize whether you will require confidence score for every token(both entities and non-entities) at the output of the MedicalNerModel, or just for the tokens recognized as entities.

#### MedicalNerModel new parameter `inferenceBatchSize`
You can now control the batch size used during inference as a separate parameter from the one you used during training of the model. This can be useful in the situation in which the hardware on which you run inference has different capacity. For example, when you have lower available memory during inference, you can reduce the batch size.

#### New Resolver Models
We trained three new sentence entity resolver models.

+ `sbertresolve_snomed_bodyStructure_med` and `sbiobertresolve_snomed_bodyStructure` models map extracted medical (anatomical structures) entities to Snomed codes (body structure version).

     + `sbertresolve_snomed_bodyStructure_med` : Trained  with using `sbert_jsl_medium_uncased` embeddings.
     + `sbiobertresolve_snomed_bodyStructure`  : Trained with using `sbiobert_base_cased_mli` embeddings.

*Example* :
```
documentAssembler = DocumentAssembler()\
      .setInputCol("text")\
      .setOutputCol("ner_chunk")
jsl_sbert_embedder = BertSentenceEmbeddings.pretrained('sbert_jsl_medium_uncased','en','clinical/models')\
      .setInputCols(["ner_chunk"])\
      .setOutputCol("sbert_embeddings")
snomed_resolver = SentenceEntityResolverModel.pretrained("sbertresolve_snomed_bodyStructure_med, "en", "clinical/models) \
      .setInputCols(["ner_chunk", "sbert_embeddings"]) \
      .setOutputCol("snomed_code")
snomed_pipelineModel = PipelineModel(
    stages = [
        documentAssembler,
        jsl_sbert_embedder,
        snomed_resolver])
snomed_lp = LightPipeline(snomed_pipelineModel)
result = snomed_lp.fullAnnotate("Amputation stump")
```

*Result*:
```bash
|    | chunks           | code     | resolutions                                                                                                                                                                                                                                  | all_codes                                                                                       | all_distances                                                               |
|---:|:-----------------|:---------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:------------------------------------------------------------------------------------------------|:----------------------------------------------------------------------------|
|  0 | amputation stump | 38033009 | [Amputation stump, Amputation stump of upper limb, Amputation stump of left upper limb, Amputation stump of lower limb, Amputation stump of left lower limb, Amputation stump of right upper limb, Amputation stump of right lower limb, ...]| ['38033009', '771359009', '771364008', '771358001', '771367001', '771365009', '771368006', ...] | ['0.0000', '0.0773', '0.0858', '0.0863', '0.0905', '0.0911', '0.0972', ...] |
```
 +  `sbiobertresolve_icdo_augmented` : This model maps extracted medical entities to ICD-O codes using sBioBert sentence embeddings. This model is augmented using the site information coming from ICD10 and synonyms coming from SNOMED vocabularies. It is trained with a dataset that is 20x larger than the previous version of ICDO resolver. Given the oncological entity found in the text (via NER models like ner_jsl), it returns top terms and resolutions along with the corresponding ICD-10 codes to present more granularity with respect to body parts mentioned. It also returns the original histological behavioral codes and descriptions in the aux metadata.

*Example*:

```
...
chunk2doc = Chunk2Doc().setInputCols("ner_chunk").setOutputCol("ner_chunk_doc")

sbert_embedder = BertSentenceEmbeddings\
     .pretrained("sbiobert_base_cased_mli","en","clinical/models")\
     .setInputCols(["ner_chunk_doc"])\
     .setOutputCol("sbert_embeddings")

icdo_resolver = SentenceEntityResolverModel.pretrained("sbiobertresolve_icdo_augmented","en", "clinical/models") \
     .setInputCols(["ner_chunk", "sbert_embeddings"]) \
     .setOutputCol("resolution")\
     .setDistanceFunction("EUCLIDEAN")
nlpPipeline = Pipeline(stages=[document_assembler, sentence_detector, tokenizer, word_embeddings, clinical_ner, ner_converter, chunk2doc, sbert_embedder, icdo_resolver])
empty_data = spark.createDataFrame([[""]]).toDF("text")
model = nlpPipeline.fit(empty_data)
results = model.transform(spark.createDataFrame([["The patient is a very pleasant 61-year-old female with a strong family history of colon polyps. The patient reports her first polyps noted at the age of 50. We reviewed the pathology obtained from the pericardectomy in March 2006, which was diagnostic of mesothelioma. She also has history of several malignancies in the family. Her father died of a brain tumor at the age of 81. Her sister died at the age of 65 breast cancer. She has two maternal aunts with history of lung cancer both of whom were smoker. Also a paternal grandmother who was diagnosed with leukemia at 86 and a paternal grandfather who had B-cell lymphoma."]]).toDF("text"))
```

*Result*:

```
+--------------------+-----+---+-----------+-------------+-------------------------+-------------------------+
|               chunk|begin|end|     entity|         code|        all_k_resolutions|              all_k_codes|
+--------------------+-----+---+-----------+-------------+-------------------------+-------------------------+
|        mesothelioma|  255|266|Oncological|9971/3||C38.3|malignant mediastinal ...|9971/3||C38.3:::8854/3...|
|several malignancies|  293|312|Oncological|8894/3||C39.8|overlapping malignant ...|8894/3||C39.8:::8070/2...|
|         brain tumor|  350|360|Oncological|9562/0||C71.9|cancer of the brain:::...|9562/0||C71.9:::9070/3...|
|       breast cancer|  413|425|Oncological|9691/3||C50.9|carcinoma of breast:::...|9691/3||C50.9:::8070/2...|
|         lung cancer|  471|481|Oncological|8814/3||C34.9|malignant tumour of lu...|8814/3||C34.9:::8550/3...|
|            leukemia|  560|567|Oncological|9670/3||C80.9|anemia in neoplastic d...|9670/3||C80.9:::9714/3...|
|     B-cell lymphoma|  610|624|Oncological|9818/3||C77.9|secondary malignant ne...|9818/3||C77.9:::9655/3...|
+--------------------+-----+---+-----------+-------------+-------------------------+-------------------------+
```


#### Updated Resolver Models
We updated `sbiobertresolve_snomed_findings` and `sbiobertresolve_cpt_procedures_augmented` resolver models to reflect the latest changes in the official terminologies.

#### Getting Started with Spark NLP for Healthcare Notebook in Databricks
We prepared a new notebook for those who want to get started with Spark NLP for Healthcare in Databricks : [Getting Started with Spark NLP for Healthcare Notebook](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/databricks/python/healthcare_case_studies/Get_Started_Spark_NLP_for_Healthcare.ipynb)

## 3.1.0
We are glad to announce that Spark NLP for Healthcare 3.1.0 has been released!
#### Highlights
+ Improved load time & memory consumption for SentenceResolver models.
+ New JSL Bert Models.
+ JSL SBert Model Speed Benchmark.
+ New ICD10CM resolver models.
+ New Deidentification NER models.
+ New column returned in DeidentificationModel
+ New Reidentification feature
+ New Deidentification Pretrained Pipelines
+ Chunk filtering based on confidence
+ Extended regex dictionary fuctionallity in Deidentification
+ Enhanced RelationExtractionDL Model to create and identify relations between entities across the entire document
+ MedicalNerApproach can now accept a graph file directly.
+ MedicalNerApproach can now accept a user-defined name for log file.
+ More improvements in Scaladocs.
+ Bug fixes in Deidentification module.
+ New notebooks.



#### Sentence Resolver Models load time improvement
Sentence resolver models now have faster load times, with a speedup of about 6X when compared to previous versions.
Also, the load process now is more  memory friendly meaning that the maximum memory required during load time is smaller, reducing the chances of OOM exceptions, and thus relaxing hardware requirements.

#### New JSL SBert Models
We trained new sBert models in TF2 and fined tuned on MedNLI, NLI and UMLS datasets with various parameters to cover common NLP tasks in medical domain. You can find the details in the following table.

+ `sbiobert_jsl_cased`
+ `sbiobert_jsl_umls_cased`
+ `sbert_jsl_medium_uncased`
+ `sbert_jsl_medium_umls_uncased`
+ `sbert_jsl_mini_uncased`
+ `sbert_jsl_mini_umls_uncased`
+ `sbert_jsl_tiny_uncased`
+ `sbert_jsl_tiny_umls_uncased`

#### JSL SBert Model Speed Benchmark

| JSL SBert Model| Base Model | Is Cased | Train Datasets | Inference speed (100 rows) |
|-|-|-|-|-|
| sbiobert_jsl_cased | biobert_v1.1_pubmed | Cased | medNLI, allNLI| 274,53 |
| sbiobert_jsl_umls_cased | biobert_v1.1_pubmed | Cased | medNLI, allNLI, umls | 274,52 |
| sbert_jsl_medium_uncased | uncased_L-8_H-512_A-8 | Uncased | medNLI, allNLI| 80,40 |
| sbert_jsl_medium_umls_uncased | uncased_L-8_H-512_A-8 | Uncased | medNLI, allNLI, umls | 78,35 |
| sbert_jsl_mini_uncased | uncased_L-4_H-256_A-4 | Uncased | medNLI, allNLI| 10,68 |
| sbert_jsl_mini_umls_uncased | uncased_L-4_H-256_A-4 | Uncased | medNLI, allNLI, umls | 10,29 |
| sbert_jsl_tiny_uncased | uncased_L-2_H-128_A-2 | Uncased | medNLI, allNLI| 4,54 |
| sbert_jsl_tiny_umls_uncased | uncased_L-2_H-128_A-2 | Uncased | medNLI, allNL, umls | 4,54 |

#### New ICD10CM resolver models:
These models map clinical entities and concepts to ICD10 CM codes using sentence bert embeddings. They also return the official resolution text within the brackets inside the metadata. Both models are augmented with synonyms, and previous augmentations are flexed according to cosine distances to unnormalized terms (ground truths).

+ `sbiobertresolve_icd10cm_slim_billable_hcc`: Trained with classic sbiobert mli. (`sbiobert_base_cased_mli`)

Models Hub Page : https://nlp.johnsnowlabs.com/2021/05/25/sbiobertresolve_icd10cm_slim_billable_hcc_en.html

+ `sbertresolve_icd10cm_slim_billable_hcc_med`: Trained with new jsl sbert(`sbert_jsl_medium_uncased`)

Models Hub Page : https://nlp.johnsnowlabs.com/2021/05/25/sbertresolve_icd10cm_slim_billable_hcc_med_en.html


*Example*: 'bladder cancer'
+ `sbiobertresolve_icd10cm_augmented_billable_hcc`

| chunks | code | all_codes | resolutions |all_distances | 100x Loop(sec) |
|-|-|-|-|-|-|
| bladder cancer | C679 | [C679, Z126, D090, D494, C7911] | [bladder cancer, suspected bladder cancer, cancer in situ of urinary bladder, tumor of bladder neck, malignant tumour of bladder neck] | [0.0000, 0.0904, 0.0978, 0.1080, 0.1281] | 26,9 |

+ ` sbiobertresolve_icd10cm_slim_billable_hcc`

| chunks | code | all_codes | resolutions |all_distances | 100x Loop(sec) |
| - | - | - | - | - | - |
| bladder cancer | D090 | [D090, D494, C7911, C680, C679] | [cancer in situ of urinary bladder [Carcinoma in situ of bladder], tumor of bladder neck [Neoplasm of unspecified behavior of bladder], malignant tumour of bladder neck [Secondary malignant neoplasm of bladder], carcinoma of urethra [Malignant neoplasm of urethra], malignant tumor of urinary bladder [Malignant neoplasm of bladder, unspecified]] | [0.0978, 0.1080, 0.1281, 0.1314, 0.1284] | 20,9 |

+ `sbertresolve_icd10cm_slim_billable_hcc_med`

| chunks | code | all_codes | resolutions |all_distances | 100x Loop(sec) |
| - | - | - | - | - | - |
| bladder cancer | C671 | [C671, C679, C61, C672, C673] | [bladder cancer, dome [Malignant neoplasm of dome of bladder], cancer of the urinary bladder [Malignant neoplasm of bladder, unspecified], prostate cancer [Malignant neoplasm of prostate], cancer of the urinary bladder] | [0.0894, 0.1051, 0.1184, 0.1180, 0.1200] | 12,8 |

#### New Deidentification NER Models

We trained four new NER models to find PHI data (protected health information) that may need to be deidentified. `ner_deid_generic_augmented` and `ner_deid_subentity_augmented` models are trained with a combination of 2014 i2b2 Deid dataset and in-house annotations as well as some augmented version of them. Compared to the same test set coming from 2014 i2b2 Deid dataset, we achieved a better accuracy and generalisation on some entity labels as summarised in the following tables. We also trained the same models with `glove_100d` embeddings to get more memory friendly versions.    

+ `ner_deid_generic_augmented`  : Detects PHI 7 entities (`DATE`, `NAME`, `LOCATION`, `PROFESSION`, `CONTACT`, `AGE`, `ID`).

Models Hub Page : https://nlp.johnsnowlabs.com/2021/06/01/ner_deid_generic_augmented_en.html

| entity| ner_deid_large (v3.0.3 and before)|ner_deid_generic_augmented (v3.1.0)|
|--|:--:|:--:|
|   CONTACT| 0.8695|0.9592 |
|      NAME| 0.9452|0.9648 |
|      DATE| 0.9778|0.9855 |
|  LOCATION| 0.8755|0.923 |

+ `ner_deid_subentity_augmented`: Detects PHI 23 entities (`MEDICALRECORD`, `ORGANIZATION`, `DOCTOR`, `USERNAME`, `PROFESSION`, `HEALTHPLAN`, `URL`, `CITY`, `DATE`, `LOCATION-OTHER`, `STATE`, `PATIENT`, `DEVICE`, `COUNTRY`, `ZIP`, `PHONE`, `HOSPITAL`, `EMAIL`, `IDNUM`, `SREET`, `BIOID`, `FAX`, `AGE`)

Models Hub Page : https://nlp.johnsnowlabs.com/2021/06/01/ner_deid_subentity_augmented_en.html

|entity| ner_deid_enriched (v3.0.3 and before)| ner_deid_subentity_augmented (v3.1.0)|
|-------------|:------:|:--------:|
|     HOSPITAL| 0.8519| 0.8983 |
|         DATE| 0.9766| 0.9854 |
|         CITY| 0.7493| 0.8075 |
|       STREET| 0.8902| 0.9772 |
|          ZIP| 0.8| 0.9504 |
|        PHONE| 0.8615| 0.9502 |
|       DOCTOR| 0.9191| 0.9347 |
|          AGE| 0.9416| 0.9469 |

+ `ner_deid_generic_glove`: Small version of `ner_deid_generic_augmented` and detects 7 entities.
+ `ner_deid_subentity_glove`: Small version of `ner_deid_subentity_augmented` and detects 23 entities.

*Example*:

Scala
```scala
...
val deid_ner = MedicalNerModel.pretrained("ner_deid_subentity_augmented", "en", "clinical/models") \
      .setInputCols(Array("sentence", "token", "embeddings")) \
      .setOutputCol("ner")
...
val nlpPipeline = new Pipeline().setStages(Array(document_assembler, sentence_detector, tokenizer, word_embeddings, deid_ner, ner_converter))
model = nlpPipeline.fit(spark.createDataFrame([[""]]).toDF("text"))

val result = pipeline.fit(Seq.empty["A. Record date : 2093-01-13, David Hale, M.D., Name : Hendrickson, Ora MR. # 7194334 Date : 01/13/93 PCP : Oliveira, 25 -year-old, Record date : 1-11-2000. Cocke County Baptist Hospital. 0295 Keats Street. Phone +1 (302) 786-5227."].toDS.toDF("text")).transform(data)
```

Python
```bash
...
deid_ner = MedicalNerModel.pretrained("ner_deid_subentity_augmented", "en", "clinical/models") \
      .setInputCols(["sentence", "token", "embeddings"]) \
      .setOutputCol("ner")
...
nlpPipeline = Pipeline(stages=[document_assembler, sentence_detector, tokenizer, word_embeddings, deid_ner, ner_converter])
model = nlpPipeline.fit(spark.createDataFrame([[""]]).toDF("text"))

results = model.transform(spark.createDataFrame(pd.DataFrame({"text": ["""A. Record date : 2093-01-13, David Hale, M.D., Name : Hendrickson, Ora MR. # 7194334 Date : 01/13/93 PCP : Oliveira, 25 -year-old, Record date : 1-11-2000. Cocke County Baptist Hospital. 0295 Keats Street. Phone +1 (302) 786-5227."""]})))
```
*Results*:
```bash
+-----------------------------+-------------+
|chunk                        |ner_label    |
+-----------------------------+-------------+
|2093-01-13                   |DATE         |
|David Hale                   |DOCTOR       |
|Hendrickson, Ora             |PATIENT      |
|7194334                      |MEDICALRECORD|
|01/13/93                     |DATE         |
|Oliveira                     |DOCTOR       |
|25-year-old                  |AGE          |
|1-11-2000                    |DATE         |
|Cocke County Baptist Hospital|HOSPITAL     |
|0295 Keats Street.           |STREET       |
|(302) 786-5227               |PHONE        |
|Brothers Coal-Mine           |ORGANIZATION |
+-----------------------------+-------------+
```


#### New column returned in DeidentificationModel

DeidentificationModel now can return a new column to save the mappings between the mask/obfuscated entities and original entities.
This column is optional and you can set it up with the `.setReturnEntityMappings(True)` method. The default value is False.
Also, the name for the column can be changed using the following method; `.setMappingsColumn("newAlternativeName")`
The new column will produce annotations with the following structure,
```
Annotation(
  type: chunk,
  begin: 17,
  end: 25,
  result: 47,
    metadata:{
        originalChunk -> 01/13/93  //Original text of the chunk
        chunk -> 0  // The number of the chunk in the sentence
        beginOriginalChunk -> 95 // Start index of the original chunk
        endOriginalChunk -> 102  // End index of the original chunk
        entity -> AGE // Entity of the chunk
        sentence -> 2 // Number of the sentence
    }
)
```

#### New Reidentification feature

With the new ReidetificationModel, the user can go back to the original sentences using the mappings columns and the deidentification sentences.

*Example:*

Scala

```scala
val redeidentification = new ReIdentification()
     .setInputCols(Array("mappings", "deid_chunks"))
     .setOutputCol("original")
```

Python

```scala
reDeidentification = ReIdentification()
     .setInputCols(["mappings","deid_chunks"])
     .setOutputCol("original")
```

#### New Deidentification Pretrained Pipelines
We developed a `clinical_deidentification` pretrained pipeline that can be used to deidentify PHI information from medical texts. The PHI information will be masked and obfuscated in the resulting text. The pipeline can mask and obfuscate `AGE`, `CONTACT`, `DATE`, `ID`, `LOCATION`, `NAME`, `PROFESSION`, `CITY`, `COUNTRY`, `DOCTOR`, `HOSPITAL`, `IDNUM`, `MEDICALRECORD`, `ORGANIZATION`, `PATIENT`, `PHONE`, `PROFESSION`,  `STREET`, `USERNAME`, `ZIP`, `ACCOUNT`, `LICENSE`, `VIN`, `SSN`, `DLN`, `PLATE`, `IPADDR` entities.

Models Hub Page : [clinical_deidentification](https://nlp.johnsnowlabs.com/2021/05/27/clinical_deidentification_en.html)

There is also a lightweight version of the same pipeline trained with memory efficient `glove_100d`embeddings.
Here are the model names:
- `clinical_deidentification`
- `clinical_deidentification_glove`

*Example:*

Python:
```bash
from sparknlp.pretrained import PretrainedPipeline
deid_pipeline = PretrainedPipeline("clinical_deidentification", "en", "clinical/models")

deid_pipeline.annotate("Record date : 2093-01-13, David Hale, M.D. IP: 203.120.223.13. The driver's license no:A334455B. the SSN:324598674 and e-mail: hale@gmail.com. Name : Hendrickson, Ora MR. # 719435 Date : 01/13/93. PCP : Oliveira, 25 years-old. Record date : 2079-11-09, Patient's VIN : 1HGBH41JXMN109286.")
```
Scala:
```scala
import com.johnsnowlabs.nlp.pretrained.PretrainedPipeline
val deid_pipeline = PretrainedPipeline("clinical_deidentification","en","clinical/models")

val result = deid_pipeline.annotate("Record date : 2093-01-13, David Hale, M.D. IP: 203.120.223.13. The driver's license no:A334455B. the SSN:324598674 and e-mail: hale@gmail.com. Name : Hendrickson, Ora MR. # 719435 Date : 01/13/93. PCP : Oliveira, 25 years-old. Record date : 2079-11-09, Patient's VIN : 1HGBH41JXMN109286.")
```
*Result*:
```bash
{'sentence': ['Record date : 2093-01-13, David Hale, M.D.',
   'IP: 203.120.223.13.',
   'The driver's license no:A334455B.',
   'the SSN:324598674 and e-mail: hale@gmail.com.',
   'Name : Hendrickson, Ora MR. # 719435 Date : 01/13/93.',
   'PCP : Oliveira, 25 years-old.',
   'Record date : 2079-11-09, Patient's VIN : 1HGBH41JXMN109286.'],
'masked': ['Record date : <DATE>, <DOCTOR>, M.D.',
   'IP: <IPADDR>.',
   'The driver's license <DLN>.',
   'the <SSN> and e-mail: <EMAIL>.',
   'Name : <PATIENT> MR. # <MEDICALRECORD> Date : <DATE>.',
   'PCP : <DOCTOR>, <AGE> years-old.',
   'Record date : <DATE>, Patient's VIN : <VIN>.'],
'obfuscated': ['Record date : 2093-01-18, Dr Alveria Eden, M.D.',
   'IP: 001.001.001.001.',
   'The driver's license K783518004444.',
   'the SSN-400-50-8849 and e-mail: Merilynn@hotmail.com.',
   'Name : Charls Danger MR. # J3366417 Date : 01-18-1974.',
   'PCP : Dr Sina Sewer, 55 years-old.',
   'Record date : 2079-11-23, Patient's VIN : 6ffff55gggg666777.'],
'ner_chunk': ['2093-01-13',
   'David Hale',
   'no:A334455B',
   'SSN:324598674',
   'Hendrickson, Ora',
   '719435',
   '01/13/93',
   'Oliveira',
   '25',
   '2079-11-09',
   '1HGBH41JXMN109286']}
```

#### Chunk filtering based on confidence

We added a new annotator ChunkFiltererApproach that allows to load a csv with both entities and confidence thresholds.
This annotator will produce a ChunkFilterer model.

You can load the dictionary with the following property `setEntitiesConfidenceResource()`.

An example dictionary is:

```CSV
TREATMENT,0.7
```

With that dictionary, the user can filter the chunks corresponding to treatment entities which have confidence lower than 0.7.

Example:

We have a ner_chunk column and sentence column with the following data:

Ner_chunk
```
|[{chunk, 141, 163, the genomicorganization, {entity -> TREATMENT, sentence -> 0, chunk -> 0, confidence -> 0.57785}, []}, {chunk, 209, 267, a candidate gene forType II
           diabetes mellitus, {entity -> PROBLEM, sentence -> 0, chunk -> 1, confidence -> 0.6614286}, []}, {chunk, 394, 408, byapproximately, {entity -> TREATMENT, sentence -> 1, chunk -> 2, confidence -> 0.7705}, []}, {chunk, 478, 508, single nucleotide polymorphisms, {entity -> TREATMENT, sentence -> 2, chunk -> 3, confidence -> 0.7204666}, []}, {chunk, 559, 581, aVal366Ala substitution, {entity -> TREATMENT, sentence -> 2, chunk -> 4, confidence -> 0.61505}, []}, {chunk, 588, 601, an 8 base-pair, {entity -> TREATMENT, sentence -> 2, chunk -> 5, confidence -> 0.29226667}, []}, {chunk, 608, 625, insertion/deletion, {entity -> PROBLEM, sentence -> 3, chunk -> 6, confidence -> 0.9841}, []}]|
+-------
```
Sentence
```
[{document, 0, 298, The human KCNJ9 (Kir 3.3, GIRK3) is a member of the G-protein-activated inwardly rectifying potassium (GIRK) channel family.Here we describe the genomicorganization of the KCNJ9 locus on chromosome 1q21-23 as a candidate gene forType II
             diabetes mellitus in the Pima Indian population., {sentence -> 0}, []}, {document, 300, 460, The gene spansapproximately 7.6 kb and contains one noncoding and two coding exons ,separated byapproximately 2.2 and approximately 2.6 kb introns, respectively., {sentence -> 1}, []}, {document, 462, 601, We identified14 single nucleotide polymorphisms (SNPs),
             including one that predicts aVal366Ala substitution, and an 8 base-pair, {sentence -> 2}, []}, {document, 603, 626, (bp) insertion/deletion., {sentence -> 3}, []}]
```

We can filter the entities using the following annotator:

```python
        chunker_filter = ChunkFiltererApproach().setInputCols("sentence", "ner_chunk") \
            .setOutputCol("filtered") \
            .setCriteria("regex") \
            .setRegex([".*"]) \         
            .setEntitiesConfidenceResource("entities_confidence.csv")

```
Where entities-confidence.csv has the following data:

```csv
TREATMENT,0.7
PROBLEM,0.9
```
We can use that chunk_filter:

```
chunker_filter.fit(data).transform(data)
```
Producing the following entities:

```
|[{chunk, 394, 408, byapproximately, {entity -> TREATMENT, sentence -> 1, chunk -> 2, confidence -> 0.7705}, []}, {chunk, 478, 508, single nucleotide polymorphisms, {entity -> TREATMENT, sentence -> 2, chunk -> 3, confidence -> 0.7204666}, []}, {chunk, 608, 625, insertion/deletion, {entity -> PROBLEM, sentence -> 3, chunk -> 6, confidence -> 0.9841}, []}]|

```
As you can see, only the treatment entities with confidence score of more than 0.7, and the problem entities with confidence score of more than 0.9 have been kept in the output.


#### Extended regex dictionary fuctionallity in Deidentification

The RegexPatternsDictionary can now use a regex that spawns the 2 previous token and the 2 next tokens.
That feature is implemented using regex groups.

Examples:

Given the sentence `The patient with ssn 123123123` we can use the following regex to capture the entitty `ssn (\d{9})`
Given the sentence `The patient has 12 years` we can use the following regex to capture the entitty `(\d{2}) years`

#### Enhanced RelationExtractionDL Model to create and identify relations between entities across the entire document

A new option has been added to `RENerChunksFilter` to support pairing entities from different sentences using `.setDocLevelRelations(True)`, to pass to the Relation Extraction Model. The RelationExtractionDL Model has also been updated to process document-level relations.

How to use:

```python
        re_dl_chunks = RENerChunksFilter() \
            .setInputCols(["ner_chunks", "dependencies"])\
            .setDocLevelRelations(True)\
            .setMaxSyntacticDistance(7)\
            .setOutputCol("redl_ner_chunks")  
```

Examples:

Given a document containing multiple sentences: `John somkes cigrettes. He also consumes alcohol.`, now we can generate relation pairs across sentences and relate `alcohol` with `John` .

#### Set NER graph explicitely in MedicalNerApproach
Now MedicalNerApproach can receives the path to the graph directly. When a graph location is provided through this method, previous graph search behavior is disabled.
```
MedicalNerApproach.setGraphFile(graphFilePath)
```

#### MedicalNerApproach can now accept a user-defined name for log file.
Now MedicalNerApproach can accept a user-defined name for the log file. If not such a name is provided, the conventional naming will take place.
```
MedicalNerApproach.setLogPrefix("oncology_ner")
```
This will result in `oncology_ner_20210605_141701.log` filename being used, in which the `20210605_141701` is a timestamp.

#### New Notebooks

+ A [new notebook](https://colab.research.google.com/github/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/1.4.Biomedical_NER_SparkNLP_paper_reproduce.ipynb)  to reproduce our peer-reviewed NER paper (https://arxiv.org/abs/2011.06315)
+ New databricks [case study notebooks](https://github.com/JohnSnowLabs/spark-nlp-workshop/tree/master/databricks/python/healthcare_case_studies). In these notebooks, we showed the examples of how to work with oncology notes dataset and OCR on databricks for both DBr and community edition versions.


### 3.0.3

We are glad to announce that Spark NLP for Healthcare 3.0.3 has been released!

#### Highlights

+ Five new entity resolution models to cover UMLS, HPO and LIONC terminologies.
+ New feature for random displacement of dates on deidentification model.
+ Five new pretrained pipelines to map terminologies across each other (from UMLS to ICD10, from RxNorm to MeSH etc.)
+ AnnotationToolReader support for Spark 2.3. The tool that helps model training on Spark-NLP to leverage data annotated using JSL Annotation Tool now has support for Spark 2.3.
+ Updated documentation (Scaladocs) covering more APIs, and examples.

#### Five new resolver models:

+ `sbiobertresolve_umls_major_concepts`: This model returns CUI (concept unique identifier) codes for Clinical Findings, Medical Devices, Anatomical Structures and Injuries & Poisoning terms.            
+ `sbiobertresolve_umls_findings`: This model returns CUI (concept unique identifier) codes for 200K concepts from clinical findings.
+ `sbiobertresolve_loinc`: Map clinical NER entities to LOINC codes using `sbiobert`.
+ `sbluebertresolve_loinc`: Map clinical NER entities to LOINC codes using `sbluebert`.
+ `sbiobertresolve_HPO`: This model returns Human Phenotype Ontology (HPO) codes for phenotypic abnormalities encountered in human diseases. It also returns associated codes from the following vocabularies for each HPO code:

		* MeSH (Medical Subject Headings)
		* SNOMED
		* UMLS (Unified Medical Language System )
		* ORPHA (international reference resource for information on rare diseases and orphan drugs)
		* OMIM (Online Mendelian Inheritance in Man)

  *Related Notebook*: [Resolver Models](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/24.Improved_Entity_Resolvers_in_SparkNLP_with_sBert.ipynb)

#### New feature on Deidentification Module
+ isRandomDateDisplacement(True): Be able to apply a random displacement on obfuscation dates. The randomness is based on the seed.
+ Fix random dates when the format is not correct. Now you can repeat an execution using a seed for dates. Random dates will be based on the seed.

#### Five new healthcare code mapping pipelines:

+ `icd10cm_umls_mapping`: This pretrained pipeline maps ICD10CM codes to UMLS codes without using any text data. You’ll just feed white space-delimited ICD10CM codes and it will return the corresponding UMLS codes as a list. If there is no mapping, the original code is returned with no mapping.

      {'icd10cm': ['M89.50', 'R82.2', 'R09.01'],
          'umls': ['C4721411', 'C0159076', 'C0004044']}

+ `mesh_umls_mapping`: This pretrained pipeline maps MeSH codes to UMLS codes without using any text data. You’ll just feed white space-delimited MeSH codes and it will return the corresponding UMLS codes as a list. If there is no mapping, the original code is returned with no mapping.

      {'mesh': ['C028491', 'D019326', 'C579867'],
         'umls': ['C0970275', 'C0886627', 'C3696376']}

+ `rxnorm_umls_mapping`: This pretrained pipeline maps RxNorm codes to UMLS codes without using any text data. You’ll just feed white space-delimited RxNorm codes and it will return the corresponding UMLS codes as a list. If there is no mapping, the original code is returned with no mapping.

      {'rxnorm': ['1161611', '315677', '343663'],
         'umls': ['C3215948', 'C0984912', 'C1146501']}

+ `rxnorm_mesh_mapping`: This pretrained pipeline maps RxNorm codes to MeSH codes without using any text data. You’ll just feed white space-delimited RxNorm codes and it will return the corresponding MeSH codes as a list. If there is no mapping, the original code is returned with no mapping.

      {'rxnorm': ['1191', '6809', '47613'],
         'mesh': ['D001241', 'D008687', 'D019355']}

+ `snomed_umls_mapping`: This pretrained pipeline maps SNOMED codes to UMLS codes without using any text data. You’ll just feed white space-delimited SNOMED codes and it will return the corresponding UMLS codes as a list. If there is no mapping, the original code is returned with no mapping.

      {'snomed': ['733187009', '449433008', '51264003'],
         'umls': ['C4546029', 'C3164619', 'C0271267']}

  *Related Notebook*: [Healthcare Code Mapping](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Healthcare/11.1.Healthcare_Code_Mapping.ipynb)


### 3.0.2

We are very excited to announce that **Spark NLP for Healthcare 3.0.2** has been released! This release includes bug fixes and some compatibility improvements.

#### Highlights

* Dictionaries for Obfuscator were augmented with more than 10K names.
* Improved support for spark 2.3 and spark 2.4.
* Bug fixes in `DrugNormalizer`.

#### New Features
Provide confidence scores for all available tags in `MedicalNerModel`,

##### MedicalNerModel before 3.0.2
```
[[named_entity, 0, 9, B-PROBLEM, [word -> Pneumonia, confidence -> 0.9998], []]
```
##### Now in Spark NLP for Healthcare 3.0.2
```
[[named_entity, 0, 9, B-PROBLEM, [B-PROBLEM -> 0.9998, I-TREATMENT -> 0.0, I-PROBLEM -> 0.0, I-TEST -> 0.0, B-TREATMENT -> 1.0E-4, word -> Pneumonia, B-TEST -> 0.0], []]
```

### 3.0.1

We are very excited to announce that **Spark NLP for Healthcare 3.0.1** has been released!

#### Highlights:

* Fixed problem in Assertion Status internal tokenization (reported in Spark-NLP #2470).
* Fixes in the internal implementation of DeIdentificationModel/Obfuscator.
* Being able to disable the use of regexes in the Deidentification process
* Other minor bug fixes & general improvements.

#### DeIdentificationModel Annotator

##### New `seed` parameter.
Now we have the possibility of using a seed to guide the process of obfuscating entities and returning the same result across different executions. To make that possible a new method setSeed(seed:Int) was introduced.

**Example:**
Return obfuscated documents in a repeatable manner based on the same seed.
##### Scala
```scala
deIdentification = DeIdentification()
      .setInputCols(Array("ner_chunk", "token", "sentence"))
      .setOutputCol("dei")
      .setMode("obfuscate")
      .setObfuscateRefSource("faker")
      .setSeed(10)
      .setIgnoreRegex(true)
```
##### Python
```python
de_identification = DeIdentification() \
            .setInputCols(["ner_chunk", "token", "sentence"]) \
            .setOutputCol("dei") \
            .setMode("obfuscate") \
            .setObfuscateRefSource("faker") \
            .setSeed(10) \
            .setIgnoreRegex(True)

```

This seed controls how the obfuscated values are picked from a set of obfuscation candidates. Fixing the seed allows the process to be replicated.

**Example:**

Given the following input to the deidentification:
```
"David Hale was in Cocke County Baptist Hospital. David Hale"
```

If the annotator is set up with a seed of 10:
##### Scala
```
val deIdentification = new DeIdentification()
      .setInputCols(Array("ner_chunk", "token", "sentence"))
      .setOutputCol("dei")
      .setMode("obfuscate")
      .setObfuscateRefSource("faker")
      .setSeed(10)
      .setIgnoreRegex(true)
```
##### Python
```python
de_identification = DeIdentification() \
            .setInputCols(["ner_chunk", "token", "sentence"]) \
            .setOutputCol("dei") \
            .setMode("obfuscate") \
            .setObfuscateRefSource("faker") \
            .setSeed(10) \
            .setIgnoreRegex(True)

```

The result will be the following for any execution,

```
"Brendan Kitten was in New Megan.Brendan Kitten"
```
Now if we set up a seed of 32,
##### Scala

```scala
val deIdentification = new DeIdentification()
      .setInputCols(Array("ner_chunk", "token", "sentence"))
      .setOutputCol("dei")
      .setMode("obfuscate")
      .setObfuscateRefSource("faker")
      .setSeed(32)
      .setIgnoreRegex(true)
```
##### Python
```python
de_identification = DeIdentification() \
            .setInputCols(["ner_chunk", "token", "sentence"]) \
            .setOutputCol("dei") \
            .setMode("obfuscate") \
            .setObfuscateRefSource("faker") \
            .setSeed(10) \
            .setIgnoreRegex(True)
```

The result will be the following for any execution,

```
"Louise Pear was in Lake Edward.Louise Pear"
```

##### New `ignoreRegex` parameter.
You can now choose to completely disable the use of regexes in the deidentification process by setting the setIgnoreRegex param to True.
**Example:**
##### Scala

```scala
DeIdentificationModel.setIgnoreRegex(true)
```
##### Python
```python
DeIdentificationModel().setIgnoreRegex(True)
```

The default value for this param is `False` meaning that regexes will be used by default.

##### New supported entities for Deidentification & Obfuscation:

We added new entities to the default supported regexes:

* `SSN - Social security number.`
* `PASSPORT - Passport id.`
* `DLN - Department of Labor Number.`
* `NPI - National Provider Identifier.`
* `C_CARD - The id number for credits card.`
* `IBAN - International Bank Account Number.`
* `DEA - DEA Registration Number, which is an identifier assigned to a health care provider by the United States Drug Enforcement Administration.`

We also introduced new Obfuscator cases for these new entities.


### 3.0.0

We are very excited to announce that **Spark NLP for Healthcare 3.0.0** has been released! This has been one of the biggest releases we have ever done and we are so proud to share this with our customers.

#### Highlights:

Spark NLP for Healthcare 3.0.0 extends the support for Apache Spark 3.0.x and 3.1.x major releases on Scala 2.12 with both Hadoop 2.7. and 3.2. We now support all 4 major Apache Spark and PySpark releases of 2.3.x, 2.4.x, 3.0.x, and 3.1.x helping the customers to migrate from earlier Apache Spark versions to newer releases without being worried about Spark NLP support.

#### Highlights:
* Support for Apache Spark and PySpark 3.0.x on Scala 2.12
* Support for Apache Spark and PySpark 3.1.x on Scala 2.12
* Migrate to TensorFlow v2.3.1 with native support for Java to take advantage of many optimizations for CPU/GPU and new features/models introduced in TF v2.x
* A brand new `MedicalNerModel` annotator to train & load the licensed clinical NER models.
*  **Two times faster NER and Entity Resolution** due to new batch annotation technique.
* Welcoming 9x new Databricks runtimes to our Spark NLP family:
  * Databricks 7.3
  * Databricks 7.3 ML GPU
  * Databricks 7.4
  * Databricks 7.4 ML GPU
  * Databricks 7.5
  * Databricks 7.5 ML GPU
  * Databricks 7.6
  * Databricks 7.6 ML GPU
  * Databricks 8.0
  * Databricks 8.0 ML (there is no GPU in 8.0)
  * Databricks 8.1 Beta
* Welcoming 2x new EMR 6.x series to our Spark NLP family:
  * EMR 6.1.0 (Apache Spark 3.0.0 / Hadoop 3.2.1)
  * EMR 6.2.0 (Apache Spark 3.0.1 / Hadoop 3.2.1)
* Starting Spark NLP for Healthcare 3.0.0 the default packages  for CPU and GPU will be based on Apache Spark 3.x and Scala 2.12.

##### Deprecated

Text2SQL annotator is deprecated and will not be maintained going forward. We are working on a better and faster version of Text2SQL at the moment and will announce soon.

#### 1. MedicalNerModel Annotator

Starting Spark NLP for Healthcare 3.0.0, the licensed clinical and biomedical pretrained NER models will only work with this brand new annotator called `MedicalNerModel` and will not work with `NerDLModel` in open source version.

In order to make this happen, we retrained all the clinical NER models (more than 80) and uploaded to models hub.

Example:


    clinical_ner = MedicalNerModel.pretrained("ner_clinical", "en", "clinical/models") \
    .setInputCols(["sentence", "token", "embeddings"])\
    .setOutputCol("ner")

#### 2. Speed Improvements

A new batch annotation technique implemented in Spark NLP 3.0.0 for `NerDLModel`,`BertEmbeddings`, and `BertSentenceEmbeddings` annotators will be reflected in `MedicalNerModel` and it improves prediction/inferencing performance radically. From now on the `batchSize` for these annotators means the number of rows that can be fed into the models for prediction instead of sentences per row. You can control the throughput when you are on accelerated hardware such as GPU to fully utilise it. Here are the overall speed comparison:

Now, NER inference and Entity Resolution are **two times faster** on CPU and three times faster on GPU.


#### 3. JSL Clinical NER Model

We are releasing the richest clinical NER model ever, spanning over 80 entities. It has been under development for the last 6 months and we manually annotated more than 4000 clinical notes to cover such a high number of entities in a single model. It has 4 variants at the moment:

- `jsl_ner_wip_clinical`
- `jsl_ner_wip_greedy_clinical`
- `jsl_ner_wip_modifier_clinical`
- `jsl_rd_ner_wip_greedy_clinical`

##### Entities:

`Kidney_Disease`, `HDL`, `Diet`, `Test`, `Imaging_Technique`, `Triglycerides`, `Obesity`, `Duration`, `Weight`, `Social_History_Header`, `ImagingTest`, `Labour_Delivery`, `Disease_Syndrome_Disorder`, `Communicable_Disease`, `Overweight`, `Units`, `Smoking`, `Score`, `Substance_Quantity`, `Form`, `Race_Ethnicity`, `Modifier`, `Hyperlipidemia`, `ImagingFindings`, `Psychological_Condition`, `OtherFindings`, `Cerebrovascular_Disease`, `Date`, `Test_Result`, `VS_Finding`, `Employment`, `Death_Entity`, `Gender`, `Oncological`, `Heart_Disease`, `Medical_Device`, `Total_Cholesterol`, `ManualFix`, `Time`, `Route`, `Pulse`, `Admission_Discharge`, `RelativeDate`, `O2_Saturation`, `Frequency`, `RelativeTime`, `Hypertension`, `Alcohol`, `Allergen`, `Fetus_NewBorn`, `Birth_Entity`, `Age`, `Respiration`, `Medical_History_Header`, `Oxygen_Therapy`, `Section_Header`, `LDL`, `Treatment`, `Vital_Signs_Header`, `Direction`, `BMI`, `Pregnancy`, `Sexually_Active_or_Sexual_Orientation`, `Symptom`, `Clinical_Dept`, `Measurements`, `Height`, `Family_History_Header`, `Substance`, `Strength`, `Injury_or_Poisoning`, `Relationship_Status`, `Blood_Pressure`, `Drug`, `Temperature`, `EKG_Findings`, `Diabetes`, `BodyPart`, `Vaccine`, `Procedure`, `Dosage`


#### 4. JSL Clinical Assertion Model

We are releasing a brand new clinical assertion model, supporting 8 assertion statuses.

- `jsl_assertion_wip`


##### Assertion Labels :

`Present`, `Absent`, `Possible`, `Planned`, `Someoneelse`, `Past`, `Family`, `Hypotetical`

#### 5. Library Version Compatibility Table :

Spark NLP for Healthcare 3.0.0 is compatible with Spark NLP 3.0.1

#### 6. Pretrained Models Version Control (Beta):

Due to active release cycle, we are adding & training new pretrained models at each release and it might be tricky to maintain the backward compatibility or keep up with the latest models, especially for the users using our models locally in air-gapped networks.

We are releasing a new utility class to help you check your local & existing models with the latest version of everything we have up to date. You will not need to specify your AWS credentials from now on. This is the second version of the model checker we released with 2.7.6 and will replace that soon.

    from sparknlp_jsl.compatibility_beta import CompatibilityBeta

    compatibility = CompatibilityBeta(spark)

    print(compatibility.findVersion("ner_deid"))


#### 7. Updated Pretrained Models:

 (requires fresh `.pretraned()`)

None


<div class="prev_ver h3-box" markdown="1">

## Previos versions

</div>
<ul class="pagination">
    <li>
        <a href="licensed_release_notes_2">Versions 2.0.0</a>
    </li>
    <li>
        <strong>Versions 3.0.0</strong>
    </li>
</ul>