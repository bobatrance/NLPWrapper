package com.bobatrance.NLPWrapper;

import java.util.HashSet;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class MiscHelper {
    
    // exports the file paths to a txt file
    public static void ExportFileList(HashSet<File> files) {
        File exportTxt = new File("FileList.txt");
        try {    
            if(!exportTxt.createNewFile()) {
                exportTxt.delete();
                exportTxt.createNewFile();
            }
            for (File file : files) {
                FileUtils.writeStringToFile(exportTxt, file.getAbsolutePath() + "\n", "UTF-8", true);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


}
