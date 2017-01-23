package uk.ac.cam.cl.ajb327.exercises;

import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.IExercise2;
import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.Sentiment;
import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.Tokenizer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class Exercise2 implements IExercise2 {

	public Map<Sentiment, Double> calculateClassProbabilities(Map<Path, Sentiment> trainingSet) throws IOException {

		double positiveCount = 0;
		double negativeCount = 0;

		for (Path review : trainingSet.keySet()) {
			if (trainingSet.get(review) == Sentiment.POSITIVE) positiveCount++;
			else negativeCount++;
		}

		Map<Sentiment, Double> classProbabilities = new HashMap<>();
		classProbabilities.put(Sentiment.POSITIVE, positiveCount / (positiveCount + negativeCount));
		classProbabilities.put(Sentiment.NEGATIVE, negativeCount / (positiveCount + negativeCount));

		return classProbabilities;
	}

	@SuppressWarnings("Duplicates")
	public Map<String, Map<Sentiment, Double>> calculateUnsmoothedLogProbs(Map<Path, Sentiment> trainingSet) throws IOException {

		int positiveWords = 0;
		int negativeWords = 0;

		HashMap<String, Map<Sentiment, Double>> allWords = new HashMap<>();

		for (Path review : trainingSet.keySet()) {
			Sentiment reviewSentiment = trainingSet.get(review);
			List<String> tokens = Tokenizer.tokenize(review);
			for (String token : tokens) {
				if (reviewSentiment == Sentiment.POSITIVE) positiveWords++;
				else negativeWords++;
				Double instanceCount = 1.0;
				if (allWords.containsKey(token)) {
					instanceCount += allWords.get(token).get(reviewSentiment);
				}
				else {
					allWords.put(token, new HashMap<>());
					allWords.get(token).put(Sentiment.POSITIVE, 0.0);
					allWords.get(token).put(Sentiment.NEGATIVE, 0.0);
				}
				allWords.get(token).put(reviewSentiment, instanceCount);
			}
		}

		for (String word : allWords.keySet()) {
			allWords.get(word).put(Sentiment.POSITIVE, Math.log(allWords.get(word).get(Sentiment.POSITIVE) / positiveWords));
			allWords.get(word).put(Sentiment.NEGATIVE, Math.log(allWords.get(word).get(Sentiment.NEGATIVE) / negativeWords));
		}

		return allWords;
	}

	@SuppressWarnings("Duplicates")
	public Map<String, Map<Sentiment, Double>> calculateSmoothedLogProbs(Map<Path, Sentiment> trainingSet) throws IOException {

		int positiveWords = 0;
		int negativeWords = 0;

		HashMap<String, Map<Sentiment, Double>> allWords = new HashMap<>();

		for (Path review : trainingSet.keySet()) {
			Sentiment reviewSentiment = trainingSet.get(review);
			List<String> tokens = Tokenizer.tokenize(review);
			for (String token : tokens) {
				if (reviewSentiment == Sentiment.POSITIVE) positiveWords++;
				else negativeWords++;

				Double instanceCount = 1.0;
				if (allWords.containsKey(token)) {
					instanceCount += allWords.get(token).get(reviewSentiment);
				}
				else {
					allWords.put(token, new HashMap<>());
					allWords.get(token).put(Sentiment.POSITIVE, 1.0);
					allWords.get(token).put(Sentiment.NEGATIVE, 1.0);
					instanceCount++;
					positiveWords++;
					negativeWords++;
				}
				allWords.get(token).put(reviewSentiment, instanceCount);
			}
		}

		for (String word : allWords.keySet()) {
			allWords.get(word).put(Sentiment.POSITIVE, Math.log(allWords.get(word).get(Sentiment.POSITIVE) / positiveWords));
			allWords.get(word).put(Sentiment.NEGATIVE, Math.log(allWords.get(word).get(Sentiment.NEGATIVE) / negativeWords));
		}

		return allWords;
	}

	public Map<Path, Sentiment> naiveBayes(Set<Path> testSet, Map<String, Map<Sentiment, Double>> tokenLogProbs,
										   Map<Sentiment, Double> classProbabilities) throws IOException {

		HashMap<Path, Sentiment> allReviews = new HashMap<>();

		for (Path review : testSet) {
			double positiveSum = 0;
			double negativeSum = 0;
			List<String> tokens = Tokenizer.tokenize(review);
			for (String token : tokens) {
				if (tokenLogProbs.containsKey(token)) {
					positiveSum += tokenLogProbs.get(token).get(Sentiment.POSITIVE);
					negativeSum += tokenLogProbs.get(token).get(Sentiment.NEGATIVE);
				}
			}
			if (positiveSum + Math.log(classProbabilities.get(Sentiment.POSITIVE)) >= negativeSum + Math.log(classProbabilities.get(Sentiment.NEGATIVE))) {
				allReviews.put(review, Sentiment.POSITIVE);
			}
			else allReviews.put(review, Sentiment.NEGATIVE);
		}

		return allReviews;
	}

}