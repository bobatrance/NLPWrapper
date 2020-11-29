package com.bobatrance.NLPWrapper;

import java.io.FileNotFoundException;
// import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.FileUtils;

import com.bobatrance.NLPWrapper.EnumModel.SegmentModel;
import com.bobatrance.NLPWrapper.EnumModel.TaggerModel;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

// use this to run the commands to the NLP API

public class NLPHelper {

    static final Charset enc = Charset.forName("UTF-8");
    static final String segmentData = ".\\segmenter\\";
    static final String taggerData = ".\\tagger\\";
    static String outputFilePath = "";

    public static enum NLPProcess {
        SEGMENT,
        POSTAG
    }

    public static void runProcessing(HashSet<File> filesToProcess, SegmentModel segModel, TaggerModel tagModel) {
        if(filesToProcess.size() == 0) {
            // TODO alert no files selected
        }
        else if(filesToProcess.size() >= 1)
        {
            segmentHelper(filesToProcess, segModel);
            taggerHelper(filesToProcess, tagModel);
        }
        else 
        {
            System.out.println("Encountered problem processing files from GUI");
        }
    }

    private static void segmentHelper(HashSet<File> filesToProcess, SegmentModel segModel) {
        // string args looks like
        // edu.stanford.nlp.ie.crf.CRFClassifier
        // -sighanCorporaDict .\data
        // -textFile test.txt
        // -inputEncoding UTF-8
        // -sighanPostProcessing true
        // -keepAllWhitespaces false
        // -loadClassifier .\data\pku.gz
        // -serDictionary .\data\dict-chris6.ser.gz

        Properties props = new Properties();
        props.setProperty("inputEncoding", enc.toString());
        props.setProperty("sighanPostProcessing", "true");
        props.setProperty("keepAllWhitespaces", "false");
        CRFClassifier classifier = new CRFClassifier(props);

        FileInputStream fis = null;
        GZIPInputStream gis = null;
        ObjectInputStream ois = null;
        try {
            switch (segModel) {
                case CTB:
                    fis = new FileInputStream(segmentData + "data\\ctb.gz");
                    break;
                case PKU:
                    fis = new FileInputStream(segmentData + "data\\pku.gz");
                    break;
            }
            gis = new GZIPInputStream(fis);
            ois = new ObjectInputStream(gis);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Properties classifierProps = new Properties();
        classifierProps.setProperty("serDictionary", segmentData + "data\\dict-chris6.ser.gz");
        classifierProps.setProperty("sighanCorporaDict", segmentData + "data\\");
        try {
            classifier.loadClassifier(ois, classifierProps);
            //if(filesToProcess.size() == 1) {
            for(File file : filesToProcess) {
                createOutputFile(file, NLPProcess.SEGMENT);
                // hack to capture the classifier answer
                PrintStream stdout = System.out;
                System.setOut(new PrintStream(outputFilePath));
                classifier.classifyAndWriteAnswers(file.getCanonicalPath());
                System.setOut(stdout);
            }

        } catch (ClassCastException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void taggerHelper(HashSet<File> filesToProcess, TaggerModel tagModel) {
        
        MaxentTagger tagger = null;
        switch (tagModel) {
            case DIST:
                tagger = new MaxentTagger(taggerData + "models\\chinese-distsim.tagger");
                break;
            case NODIST:
                tagger = new MaxentTagger(taggerData + "models\\chinese-nodistsim.tagger");
                break;
    
        }
        for (File file : filesToProcess) {
            createOutputFile(file, NLPProcess.POSTAG);
            List<List<HasWord>> sentences = null;
            try {
                //Java8 doesn't support this
                //FileReader fr = new FileReader(file.getAbsoluteFile().getParent() + "\\" + FilenameUtils.getBaseName(file.getName()) + "-segmented.txt", enc);
                InputStreamReader fr = new InputStreamReader(new FileInputStream(file.getAbsoluteFile().getParent() + "\\" + FilenameUtils.getBaseName(file.getName()) + "-segmented.txt"), enc.toString());
                sentences = MaxentTagger.tokenizeText(new BufferedReader(fr));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            for (List<HasWord> sentence : sentences) {
                List<TaggedWord> taggedSentence = tagger.tagSentence(sentence);
                try {
                    FileUtils.writeStringToFile(new File(outputFilePath), SentenceUtils.listToString(taggedSentence, false), enc, true);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
    }

    private static void createOutputFile(File file, NLPProcess process){
        final String fileExtension = ".txt";
        String filePath = file.getAbsoluteFile().getParent();
        String fileName = FilenameUtils.getBaseName(file.getName());
        switch (process) {
            case SEGMENT:
                fileName = filePath + "\\" + fileName + "-segmented" + fileExtension;
                break;
            case POSTAG:
                fileName = filePath + "\\" + fileName + "-tagged" + fileExtension;
                break;
        }
        try{
            File outputFile = new File(fileName);
            FileUtils.forceMkdirParent(outputFile);
            if(!outputFile.createNewFile()) {
                outputFile.delete();
                outputFile.createNewFile();
            }
            outputFilePath = fileName;
        } 
        catch (IOException e) {
            System.out.println("There was a problem creating the output file.");
            e.printStackTrace();
        }
    }

}
