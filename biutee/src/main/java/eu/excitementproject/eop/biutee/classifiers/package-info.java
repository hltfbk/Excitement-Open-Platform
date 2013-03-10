/**
 * Package of main classes of classifiers used in BIUTEE.
 * 
 * <B>General instructions for writing a new classifier.</B>
 * <P>
 * So, first of all, a classifier implements the interface {@link eu.excitementproject.eop.biutee.classifiers.Classifier}.
 * Next, if the classifier should be trained somehow, it should implement {@link eu.excitementproject.eop.biutee.classifiers.TrainableClassifier},
 * which extends {@link eu.excitementproject.eop.biutee.classifiers.Classifier}.
 * <BR>
 * Usually, in BIUTEE we use a linear classifier (the learning model, described
 * in RANLP 2011 requires a linear classifier). Thus, usually you would like to
 * implement {@link eu.excitementproject.eop.biutee.classifiers.LinearClassifier}, or
 * actually {@link eu.excitementproject.eop.biutee.classifiers.LinearTrainableClassifier}.
 * <P>
 * Well, up until now you just had to write implementations for training and
 * classification algorithms. Now, you have to add capabilities of storing and
 * loading classifiers.
 * <BR>
 * To add storing capability, you need your classifier to implement
 * {@link eu.excitementproject.eop.biutee.classifiers.io.StorableClassifier}, so
 * actually you will end up with {@link eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier}.
 * So, the only method you have to add is {@link eu.excitementproject.eop.biutee.classifiers.io.StorableClassifier#store()}.
 * This method merely returns {@link eu.excitementproject.eop.biutee.classifiers.io.LearningModel}.
 * You will have to implement your own {@link eu.excitementproject.eop.biutee.classifiers.io.LearningModel},
 * and your classifier's store() method will return it.
 * <BR>
 * Good. So, now, just let me explain how to write this {@link eu.excitementproject.eop.biutee.classifiers.io.LearningModel}.
 * Let's start with the easy stuff. The description methods (getDescriptionOfTraining and setDescriptionOfTraining)
 * are quite free. Implement them in whatever way you want. They have no effect on the classifier.
 * The most significant method is {@link eu.excitementproject.eop.biutee.classifiers.io.LearningModel#getClassifierClassOfModel()}.
 * This method returns a string, which must be the fully-qualified-name of a class
 * that implements {@link eu.excitementproject.eop.biutee.classifiers.io.LoadableClassifier}.
 * You might ask "What is this class? Where do I take it from?", well, just a few
 * minutes. You will know everything.
 * <BR>
 * The third getter-and-setter is {@link eu.excitementproject.eop.biutee.classifiers.io.LearningModel#getNestedModel()}.
 * You can return null. It is used for classifiers that actually do not make the
 * classification, but propagate the classification to an "inner classifier". An
 * example is {@link eu.excitementproject.eop.biutee.classifiers.hypothesis_normalize.HypothesisNoramlizeLinearClassifier},
 * which actually makes some normalization on the given feature-vector that it has to
 * classify, and propagate it to an inner classifier.
 * So, if you write such a "classifier", which actually just manipulates the feature-vector
 * and uses an "inner classifier", your {@link eu.excitementproject.eop.biutee.classifiers.io.LearningModel#getNestedModel()}
 * will have to return the {@link eu.excitementproject.eop.biutee.classifiers.io.LearningModel}
 * of that "inner classifier".
 * <BR>
 * Usually, you will write your sub-class of {@link eu.excitementproject.eop.biutee.classifiers.io.LearningModel}
 * which additional fields, getters and setters. Note that {@link eu.excitementproject.eop.biutee.classifiers.io.LearningModel}
 * is actually stored in an XML file using JAXB. So, for each field, you must add public
 * getter and setter.
 * <P>
 * Good!
 * You are done with writing a classifier with store capability!
 * Now, let's proceed to a classifier with loading capability.
 * This means, a classifier that can be initialized by loading a {@link eu.excitementproject.eop.biutee.classifiers.io.LearningModel}.
 * So, what you have to do is to write a class that implements {@link eu.excitementproject.eop.biutee.classifiers.io.LoadableClassifier}.
 * Implement all the methods of {@link eu.excitementproject.eop.biutee.classifiers.Classifier},
 * and then implement the method {@link eu.excitementproject.eop.biutee.classifiers.io.LoadableClassifier#load(eu.excitementproject.eop.biutee.classifiers.io.LearningModel)}.
 * This method gets a {@link eu.excitementproject.eop.biutee.classifiers.io.LearningModel},
 * and uses that model to assign values to its fields (or do whatever you think about with
 * this model).
 * <BR>
 * You might wonder what can be done with {@link eu.excitementproject.eop.biutee.classifiers.io.LearningModel}.
 * Well, sorry. Really sorry, but it was unavoidable. You have to use casting.
 * Cast the {@link eu.excitementproject.eop.biutee.classifiers.io.LearningModel} to
 * the actual class that you have created above (which extends {@link eu.excitementproject.eop.biutee.classifiers.io.LearningModel}).
 * Then, with YOUR class, you can do whatever you want (in particular, take its parameters
 * and assign to internal fields of your classifier).
 * <BR>
 * Good. One last comment is that if you wrote a classifier that wraps an "inner classifier" (see above)
 * then you can create that "inner classifier" by
 * <code>LearningModelToClassifier.createForModel(model.getNestedModel());</code>
 * (see an example in {@link eu.excitementproject.eop.biutee.classifiers.hypothesis_normalize.HypothesisNoramlizeLinearClassifier}).
 * <P>
 * Well, that's it. Now you know how to write a classifier with load and store
 * capabilities!
 * 
 * 
 * 
 */
package eu.excitementproject.eop.biutee.classifiers;
