
* About using the TreeTagger

- There are several Linguistic annotation pipelines (LAPs) that runs 
  TreeTagger to get annotations. However, due to the license
  incompatibility, TreeTagger is not shipped with Excitment project. 

To use TreeTagger binaries and models, each user should do the
following.  

  1) Read and agree on the TreeTagger license. 
  2) Download and install TreeTagger binary and models. 

This README file explains how this can be done easily for the end
users. 

---
---
---

* Installation steps 

Here we provide the script for the users who wants to install Tree
Tagger. The script will help you to do step 2) of the above. Since
EXCITEMENT access TreeTagger with DKPro library, you have to install
it in a specific way. 

0) Prerequisite: You need "Ant" make tool. If it is not installed in
your system (try > "ant" in your command line), you can get it from
Ant homepage (http://ant.apache.org/). You need ant version 1.8.0 or
later.

1) Move to the directory /lap/src/scripts/treetagger/
It has a README (this document) and an Ant build script
("build.xml"). --- this is a script provided by DKPro. (Thanks! DKPro.) 

2) Run ant script in the directory, like followings 
 > ant local-maven 
This command will download and wrap the binary and models as Maven
modules, and install it on your local Maven repository (on your
computer, only). It will take some time. 
If you face some error, like "MD5SUM mismatch", please see the last
section of this document. 

3) Edit /lap/pom.xml to use the newly generated TreeTagger binaries
and models. 
The pom file already have the dependency written, but as a commented
section. Find; 

  <!-- TreeTagger related dependencies -->

; and uncomment the affected artifacts. They are four maven
artifacts. Tree tagger binary itself, German, English and Italian
models. It would look like the followings 
===== 
    	<dependency>
     		<groupId>de.tudarmstadt.ukp.dkpro.core</groupId>
     		<artifactId>de.tudarmstadt.ukp.dkpro.core.treetagger-bin</artifactId>
     		<version>LATEST</version>
    	</dependency>
    	<dependency>
     		<groupId>de.tudarmstadt.ukp.dkpro.core</groupId>
     		<artifactId>de.tudarmstadt.ukp.dkpro.core.treetagger-model-de</artifactId>
     		<version>LATEST</version>
    	</dependency>
    	<dependency>
     		<groupId>de.tudarmstadt.ukp.dkpro.core</groupId>
     		<artifactId>de.tudarmstadt.ukp.dkpro.core.treetagger-model-en</artifactId>
     		<version>LATEST</version>
    	</dependency>
    	<dependency>
     		<groupId>de.tudarmstadt.ukp.dkpro.core</groupId>
     		<artifactId>de.tudarmstadt.ukp.dkpro.core.treetagger-model-it</artifactId>
     		<version>LATEST</version>
    	</dependency>
===== 

4) Check that TreeTagger is actually working
Once you installed the artifacts and added the dependency in POM, it
is now time to test: open up Eclipse (or any tool), and run the unit
test class of "TreeTaggerEnTest". (In LAP module, src/test/java,
package eu.excitementproject.eop.lap.dkpro).  

Make sure that the class is not "skipped", but finishing Okay. The
test code is designed to skip the test, if tree tagger binaries/models
are not found, and generated an exception. If it shows actual
processing: now TreeTagger is installed and working correctly. 

---

* If an MD5 checksum error stops building of Ant Script 

TreeTagger binaries and models are often updated to a newer version,
and stored in the same URL. Thus, MD5 checkshum fails on such
case. Current ANT script is updated in June, 2013. 

In such a case, you can have to choices: 

1) update MD5 sum in the ant script: check the url is correct, and if
you trust the updated binary, just update MD5 sum as the actual file
in the website shows. 

2) notify the responsible person and get an updated version: contact
noh@cl.uni-heidelberg.de (Tae-Gil Noh) for an updated ant script. 

---
---
---

#1 TreeTagger homepage 
http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/
#2 DKPro homepage 
http://code.google.com/p/dkpro-core-asl/
