
# Camunda Webapp Translations

This Camunda Platform project provides translations for Camunda Tasklist, Cockpit Basic/Full, and Admin. Community translations in languages other than English are contributed and maintained by the broader Camunda community and are not officially supported or maintained by Camunda.
### Get Started

Just copy one or more  language files to a webapp and include them in the config file as described in the [User Guide](https://docs.camunda.org/manual/latest/webapps/tasklist/configuration/#localization).

A [Video Tutorial](https://blog.camunda.org/post/2014/12/internationalization-in-camunda-bpm/
) is also available.

### List of available translations

| Language             | Version | Webapp                                                                                                            | Contributor                                                                                                                          |
|----------------------|---------|-------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| German               | 7.15.0  | [Tasklist](/tasklist/de.json), [Cockpit](/cockpit/de.json), [Admin](/admin/de.json), [Welcome](/welcome/de.json)  | [camunda services GmbH](https://github.com/camunda), [Philipp Heuer](https://github.com/PhilippHeuer)                                |
| English              | 7.15.0  | [Tasklist](/tasklist/en.json), [Cockpit](/cockpit/en.json), [Admin](/admin/en.json), [Welcome](/welcome/en.json)  | [camunda services GmbH](https://github.com/camunda)                                                                                  |
| Czech                | 7.7.0   | [Tasklist](/tasklist/cs.json)                                                                                     | [Trask solutions, Tomas Mikes](https://github.com/mikibo)                                                                            |
| Russian              | 7.15.0  | [Tasklist](/tasklist/ru.json)                                                                                     | [Andrii Stesin](https://github.com/astesin), [Max Davliatshin](https://github.com/TitanUser), [Andrey Zhukov](https://github.com/zhukov-andrey)|
| Ukrainian            | 7.5.0   | [Tasklist](/tasklist/uk.json)                                                                                     | [Andrii Stesin](https://github.com/astesin)                                                                                          |
| Polish               | 7.5.0   | [Tasklist](/tasklist/pl.json)                                                                                     | [camunda services GmbH](https://github.com/camunda)                                                                                  |
| Dutch                | 7.5.0   | [Tasklist](/tasklist/nl.json)                                                                                     | [camunda services GmbH](https://github.com/camunda)                                                                                  |
| Spanish              | 7.11.0  | [Tasklist](/tasklist/es.json)                                                                                     | [Paolo Vásquez](https://github.com/paolovas), [Diego Gil](https://github.com/dags), [jlmori](https://github.com/jlmori)              |
| Portuguese(Brazilian)| 7.15.0  | [Tasklist](/tasklist/pt_BR.json), [Cockpit](/cockpit/pt-BR.json), [Admin](/admin/pt_BR.json), [Welcome](/welcome/pt_BR.json) | [Marcio Junior Vieira (Ambiente Livre)](https://www.ambientelivre.com.br), [Silas Araújo (Ambiente Livre)](https://www.ambientelivre.com.br), [GabrielAraujoSouza](https://github.com/GabrielAraujoSouza), [Luciano Antonichen de Campos](https://github.com/lucianoac), [Projeto Cade UnB](https://github.com/projeto-cade-unb) |
| Japanese             | 7.5.0   | [Tasklist](/tasklist/ja.json)                                                                                     | [Masaaki Inaba](https://github.com/mas178) ([Rational Works, Inc.](http://rational.works))                                           |
| Danish               | 7.6.0   | [Tasklist](/tasklist/da.json)                                                                                     | [Lasse Lindgård](https://github.com/lldata)                                                                                          |
| Italian              | 7.12.0  | [Tasklist](/tasklist/it.json)                                                                                     | [Giacomo Cappellini](https://github.com/arkanoid87)                                                                                  |
| Turkish              | 7.12.0  | [Tasklist](/tasklist/tr.json)                                                                                     | Mustafa Cem Ertem, [Elif T. Kuş](https://github.com/elifkus)                                                                         |
| Nepali               | 7.14.0  | [Tasklist](/tasklist/np.json)                                                                                     | [Ashish Maharjan](https://github.com/AshishMhrzn10)                                                                                  |
| French              | 7.16.0  | [Tasklist](/tasklist/fr.json), [Cockpit](/cockpit/fr.json), [Admin](/admin/fr.json), [Welcome](/welcome/fr.json) | [camunda services GmbH](https://github.com/camunda), [Pierre-Yves Monnet](https://github.com/pierre-yves-monnet)                                                                                  |

### Contribute!

Everybody is welcome to contribute! You can help by sending us a pull request with your translation. For further reference, please see our [contribution guide](https://github.com/camunda/camunda-bpm-platform/blob/master/CONTRIBUTING.md).

  * Website: http://www.camunda.org/
  * License: Apache License, Version 2.0  http://www.apache.org/licenses/LICENSE-2.0

### SynchroTranslation Tool

The project contains a tool, "org;camunda.webapptranslation.SynchroTranslation" to facilitate the translation.
This tool contains two functions:
* Detect the missing keys in dictionaries
* Complete dictionaries to help the translator.

The proposition made by the tool are not visible by the application because all propositions are prefixed by different keys, like 

#### Build and execute SynchroTranslation
The machine must have Maven + Java installed.
1. Fork  / Clone the repository
2. Go to the source of the project
3. Execute
```` 
> mvn install
````
6. Execute

````
> java  -jar target/SynchroTranslation.jar`
 Folder to study: /tmp/camunda-webapp-translation
 Reference language: en
 Detection: SYNTHETIC
 Completion: NO
 Report: STDOUT
Explore from rootFolder [D:\dev\intellij\py-camunda-webapp-translations]
Detect [/tmp/camunda-webapp-translation/admin]
Detect [/tmp/camunda-webapp-translation/cockpit]
Detect [/tmp/camunda-webapp-translation/tasklist]
Detect [/tmp/camunda-webapp-translation/welcome]````
````


#### Detection
By default, the tool does a Detection only. Detection reads all dictionaries and does not modify them.


1. First pass

All subdirectories  are read. When, in a folder, a reference dictionary is detected (`en.json`), then the other `*.json` are read.
   It gives the list of all expected languages. The folder is registered as a **"language folder"**

For example, at the end of this pass, we detected the language [*fr*, *de*, *bp*, *ru*, *pt-BR*].
List of **"language folder"** are [ *admin*, *cockpit*, *tasklist*, *welcome* ]

2. Second pass 

This is executed **"language folder"** per **"language folder"**, 

For each language detected: 

The language does not exist? It is marked as *Not exist*

Else, the dictionary is compared to the reference dictionary.  For example, the detection compares `admin/fr.json` and `admin/en.json`.
* if a key exists in the reference, this is a "**MISSING KEY**"
* if a key exists in the dictionary but not in the reference, this is a "**TOO MUCH KEY**"
* if a key exists in the dictionary and  the reference, but not the same type, this is a "**INCORRECT CLASSE**""

Example in the reference dictionary:
``` 
"weekdays": [
      "Sunday",
      "Monday",
      "Tuesday",
      "Wednesday",
      "Thursday",
      "Friday",
      "Saturday"
    ],
 ```
In a dictionary:
````
"weekdays":"Dimanche,Lundi,Mardi,Mercredi,Jeudi,Vendredi,Samedi",
```` 

Example of execution:
```
=================================== Detection ===================================
----- Folder D:\dev\intellij\py-camunda-webapp-translations\admin
[de]       ... OK
[np]       ... Not exist (559 missing keys)
[ru]       ... Not exist (559 missing keys)
[pt-BR]    ... OK
[en]       ... Referentiel
[it]       ... Not exist (559 missing keys)
[fr]       ... OK
[es]       ... Missing 40 keys
[cs]       ... Not exist (559 missing keys)
[zh-Hans]  ... Missing 18 keys
[uk]       ... Not exist (559 missing keys)
[ja]       ... Not exist (559 missing keys)
[pl]       ... Not exist (559 missing keys)
[da]       ... Not exist (559 missing keys)
[nl]       ... Not exist (559 missing keys)
[tr]       ... Not exist (559 missing keys)
----- Folder D:\dev\intellij\py-camunda-webapp-translations\cockpit
[de]       ... Missing 17 keys,Incorrect class 1 keys
[np]       ... Not exist (2371 missing keys)
[ru]       ... Not exist (2371 missing keys)
[pt-BR]    ... Missing 38 keys,Incorrect class 1 keys
[en]       ... Referentiel
[it]       ... Not exist (2371 missing keys)
[fr]       ... Not exist (2371 missing keys)
[es]       ... Missing 147 keys,Incorrect class 1 keys
[cs]       ... Not exist (2371 missing keys)
[zh-Hans]  ... Not exist (2371 missing keys)
[uk]       ... Not exist (2371 missing keys)
[ja]       ... Not exist (2371 missing keys)
[pl]       ... Not exist (2371 missing keys)
[da]       ... Not exist (2371 missing keys)
[nl]       ... Not exist (2371 missing keys)
[tr]       ... Not exist (2371 missing keys)
----- Folder D:\dev\intellij\py-camunda-webapp-translations\tasklist
[de]       ... Missing 4 keys
[np]       ... Missing 17 keys,Too much 24 keys
[ru]       ... Missing 4 keys
[pt-BR]    ... Missing 4 keys
[en]       ... Referentiel
[it]       ... Missing 20 keys
[fr]       ... Missing 131 keys,Too much 13 keys
[es]       ... Missing 22 keys
[cs]       ... Missing 118 keys,Too much 19 keys
[zh-Hans]  ... Missing 20 keys
[uk]       ... Missing 185 keys,Too much 13 keys
[ja]       ... Missing 123 keys,Too much 13 keys
[pl]       ... Missing 29 keys
[da]       ... Missing 122 keys,Too much 13 keys
[nl]       ... Missing 123 keys,Too much 13 keys
[tr]       ... Missing 20 keys
----- Folder D:\dev\intellij\py-camunda-webapp-translations\welcome
[de]       ... OK
[np]       ... Not exist (178 missing keys)
[ru]       ... Not exist (178 missing keys)
[pt-BR]    ... OK
[en]       ... Referentiel
[it]       ... Not exist (178 missing keys)
[fr]       ... OK
[es]       ... Missing 18 keys
[cs]       ... Not exist (178 missing keys)
[zh-Hans]  ... Missing 15 keys
[uk]       ... Not exist (178 missing keys)
[ja]       ... Not exist (178 missing keys)
[pl]       ... Not exist (178 missing keys)
[da]       ... Not exist (178 missing keys)
[nl]       ... Not exist (178 missing keys)
[tr]       ... Not exist (178 missing keys)
The end
```

 ##### Parameters
`-f < folder >` give a different root folder

`-d <NO|SYNTHETIC|FULL>` Default is *SYNTHETIC*. If the detection is *NO*, the function is not executed. With* FULL*, all keys are listed. 

`-r <STDOUT|LOGGER >` Default is *STDOUT*. With *LOGGER*, the result is sent to the Java Logger.

#### Completion
The completion removed all non "TOO MUCH" keys and added a key for each missing. It does not add the final key but a prefixed key.
For example, when the key `AUTH_DAY_CONTEXT_EVENING` is missing, completion adds a key `AUTH_DAY_CONTEXT_EVENING_ PLEASETRANSLATETHESENTENCE` key.

There are two situations:
Completion is not able to calculate a proposition, then a key prefixed by `_PLEASETRANSLATETHESENTENCE` is added.

Completion is able to calculate a proposition. Then two keys are added
* one prefixed by `_PLEASEVERIFYTHESENTENCE`
* a second one prefixed by `_PLEASEVERIFYTHESENTENCE_REFERENCE`
Then, it is possible to see the proposition and the original sentence.

**The reviewer has to check sentences and rename keys. Executing the software again will purge all prefixed keys.**

For example, after the execution, the result may be in the French dictionary
````
"ACTIVITY_INSTANCE_TOOLTIP_CANCELED_PLEASETRANSLATETHESENTENCE": "Canceled Activity Instance",
"ABORT_PLEASEVERIFYTHESENTENCE": "Annuler ##;## Interrompre ##;## Abandonner",
"ABORT_PLEASEVERIFYTHESENTENCE_REFERENCE": "Abort",
````

The different completion strategies are :
* Same key
 The translation may exist in another dictionary for the same language.
For example, in the Cockpit dictionary, the key ABORT is missing.
 In the taskList dictionary, the key ABORT exists with a translation:
 ```
 ABORT = 'annuler'
 ```
So we can propose 'annuler'.
When the same keys exist in different dictionaries but with different values,  all the possibilities are joined in the value, separate by `##;##` .
The reviewer has to keep one.
````
"ABORT_PLEASEVERIFYTHESENTENCE": "Annuler ##;## Interrompre ##;## Abandonner",
````
The value in the reference dictionary is added, too, to help the reviewer.
````
"ABORT_PLEASEVERIFYTHESENTENCE_REFERENCE": "Abort",
````
* Same translation

This strategy comes in second.
The idea is to search if the same content already exists in a different key.
 
For example, we search this key in the French cockpit dictionary:
```
  ==>  cockpit[fr]: 
  "BULK_OVERRIDE_SUCCESSFUL"
  ```
In the reference (English) dictionary, the value is
```
  ===>  cockpit[en]:  
  "BULK_OVERRIDE_SUCCESSFUL": "Successful",
  ```
We search if the same **CONTENT** (`Successful`) exists in the reference dictionary.
We found in the
```
===>  taskList[en] : "OPERATION_SUCCESSFUL": "Successful",
===>  taskList[en] : "REMOVED_OK": "Successful",
===>  taskList[en] : "CREATED_SUCCESS": "Successful",
  ```
So, now we can check if a translation exists for these keys. 
We detect (assuming the CREATED_SUCCESS does not exist in the French dictionary):
```
===>  taskList[fr] :  "OPERATION_SUCCESSFUL": "Avec succès",
===>  taskList[fr] :  "REMOVED_OK": "opération réussie",
  ```
   So, we have two propositions:
 
```
  "BULK_OVERRIDE_SUCCESSFUL": = "Avec succès  ##;## opération réussie"
```

* Google Translation

The Google Translation API is used to give a proposition. This requires providing an API Key.
To get a Google API Key, visit Google. It's possible to get an account with an initial three-month budget of $300, which is enough to translate millions of sentences.

Two parameters exist then:

`--googleAPIKey <GoogleKey>` to provide your key.

`--limiteGoogleAPIKey <Number>`  to limit the number of translations.

By default, the limitation is 100 translations. 

**Attention, each time you restart the software, all prefixed keys are purged. So, when you translate X sentences, you have to review it, remove the prefix, else the translation will ask again the same key.**

Example of execution:

```
=================================== Completion ===================================
----- Folder D:\dev\intellij\py-camunda-webapp-translations\admin
[fr]       ... Nothing done.
----- Folder D:\dev\intellij\py-camunda-webapp-translations\cockpit
[fr]       ... Add 2369 keys / proposition ( GoogleTranslation:1, SameKey:234, SameTranslation:454), Check in the dictionary keys ( _PLEASEVERIFYTHESENTENCE:689, _PLEASETRANSLATETHESENTENCE:1680, _PLEASEVERIFYTHESENTENCE_REFERENCE:689)
Dictionary wrote with success.
----- Folder D:\dev\intellij\py-camunda-webapp-translations\tasklist
[fr]       ... Add 131 keys / proposition ( SameKey:105, SameTranslation:9), Check in the dictionary keys ( _PLEASEVERIFYTHESENTENCE:114, _PLEASETRANSLATETHESENTENCE:17, _PLEASEVERIFYTHESENTENCE_REFERENCE:114)
Dictionary wrote with success.
----- Folder D:\dev\intellij\py-camunda-webapp-translations\welcome
[fr]       ... Nothing done.
GoogleTranslation: 1693 requested,  1 done in 2774 ms
The end
```

The reviewer needs to choose and remove the prefixed:
````
"ACTIVITY_INSTANCE_TOOLTIP_CANCELED": "Annuler les instances d'activitée",
"ABORT": "Annuler",
"ABORT_PLEASEVERIFYTHESENTENCE_REFERENCE": "Abort",
````
Just execute the software again to purge all the prefixed keys.

#### parameters

`-c <NO|KEYS|TRANSLATION`>`Default is NO. With KEYS, only the two first strategies are used. With TRANSLATION, keys and translation are used.

`--googleAPIKey <GoogleKey>` to provide your key.

`--limiteGoogleAPIKey <Number>`  to limit the number of translations.
