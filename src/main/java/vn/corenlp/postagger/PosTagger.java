package vn.corenlp.postagger;

import marmot.morph.MorphTagger;
import marmot.morph.Sentence;
import marmot.morph.Word;

import marmot.util.FileUtils;
import org.apache.log4j.Logger;
import vn.corenlp.wordsegmenter.Vocabulary;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PosTagger {
    private static PosTagger posTagger = null;
    private MorphTagger tagger;
    public final static Logger LOGGER = Logger.getLogger(PosTagger.class);
    public PosTagger() throws IOException {
        LOGGER.info("Loading POS Tagging model");
        String modelPath = "models/postagger/vi-tagger";

        ClassLoader classLoader = Vocabulary.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(modelPath);

        if (inputStream == null) {
            throw new IOException("PosTagger: " + modelPath + " is not found!");
        }
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        try {
            tagger = (MorphTagger) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static PosTagger initialize() throws IOException {
        if(posTagger == null) {
            posTagger = new PosTagger();
        }
        return posTagger;
    }

    public List<vn.pipeline.Word> tagSentence(String sentence) throws IOException {
        List<vn.pipeline.Word> output = new ArrayList<>();
        String line = sentence.trim();
        if (line.length() == 0) {
            return output;
        }
        String[] tokenstrs = line.split(" ");
        LinkedList tokens = new LinkedList();

        for(int i = 0; i < tokenstrs.length; ++i) {
            if (!tokenstrs[i].isEmpty()) {
                Word word = new Word(tokenstrs[i]);
                tokens.add(word);
            }
        }

        Sentence marmotSentence = new Sentence(tokens);
        Object lemma_tags = tagger.tagWithLemma(marmotSentence);
        for(int i = 0; i < marmotSentence.size(); ++i) {
            List<String> token_lemma_tags = (List)((List)lemma_tags).get(i);
            vn.pipeline.Word word = new vn.pipeline.Word((i + 1), marmotSentence.getWord(i).getWordForm(), (String)token_lemma_tags.get(1));
            output.add(word);

        }
        return output;
    }


}
