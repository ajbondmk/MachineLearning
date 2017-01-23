package uk.ac.cam.cl.ajb327.exercises;

import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.IExercise1;
import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.Sentiment;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.io.BufferedReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.Tokenizer;

public class Exercise1 implements IExercise1 {

	@SuppressWarnings("Duplicates")
	public Map<Path, Sentiment> simpleClassifier(Set<Path> testSet, Path lexiconFile) throws IOException {

		HashMap<String, Sentiment> lexiconSet = new HashMap<>();
		BufferedReader br = Files.newBufferedReader(lexiconFile);
		String line = br.readLine();
		while (line != null) {
			String word = line.substring(line.indexOf("word1=") + 6, line.indexOf(" pos1"));
			String polarity = line.substring(line.indexOf("priorpolarity=") + 14, line.indexOf("priorpolarity=") + 17);
			if (polarity.equals("pos")) lexiconSet.put(word, Sentiment.POSITIVE);
			else if (polarity.equals("neg")) lexiconSet.put(word, Sentiment.NEGATIVE);
			line = br.readLine();
		}

		HashMap<Path, Sentiment> classifications = new HashMap<>();
		for (Path review : testSet) {
			int positiveWords = 0;
			int negativeWords = 0;
			List<String> tokens = Tokenizer.tokenize(review);
			for (String token : tokens) {
				if (lexiconSet.containsKey(token)) {
					if (lexiconSet.get(token) == Sentiment.POSITIVE) positiveWords++;
					else negativeWords++;
				}
			}
			if (positiveWords >= negativeWords) {
				classifications.put(review, Sentiment.POSITIVE);
			} else {
				classifications.put(review, Sentiment.NEGATIVE);
			}
		}

		return classifications;
	}

	public double calculateAccuracy(Map<Path, Sentiment> trueSentiments, Map<Path, Sentiment> predictedSentiments) {
		double total = 0;
		double correct = 0;
		for (Map.Entry<Path, Sentiment> review : predictedSentiments.entrySet()) {
			if (trueSentiments.containsKey(review.getKey())) {
				if (trueSentiments.get(review.getKey()) == review.getValue()) correct++;
			}
			total++;
		}
		return (correct / total);
	}

	@SuppressWarnings("Duplicates")
	public Map<Path, Sentiment> improvedClassifier(Set<Path> testSet, Path lexiconFile) throws IOException {

		HashMap<String, Sentiment> lexiconSet = new HashMap<>();
		BufferedReader br = Files.newBufferedReader(lexiconFile);
		String line = br.readLine();
		while (line != null) {
			String word = line.substring(line.indexOf("word1=") + 6, line.indexOf(" pos1"));
			String polarity = line.substring(line.indexOf("priorpolarity=") + 14, line.indexOf("priorpolarity=") + 17);
			if (polarity.equals("pos")) lexiconSet.put(word, Sentiment.POSITIVE);
			else if (polarity.equals("neg")) lexiconSet.put(word, Sentiment.NEGATIVE);
			line = br.readLine();
		}

		HashMap<Path, Sentiment> classifications = new HashMap<>();
		for (Path review : testSet) {
			int positiveWords = 0;
			int negativeWords = 0;
			List<String> tokens = Tokenizer.tokenize(review);
			for (String token : tokens) {
				if (lexiconSet.containsKey(token)) {
					if (lexiconSet.get(token) == Sentiment.POSITIVE) positiveWords++;
					else negativeWords++;
				}
			}
			if (positiveWords * 0.75 >= negativeWords) {
				classifications.put(review, Sentiment.POSITIVE);
			} else {
				classifications.put(review, Sentiment.NEGATIVE);
			}
		}

		return classifications;
	}

}