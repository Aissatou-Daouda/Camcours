package com.camcours;

import androidx.core.content.FileProvider;

public class PdfContentProvider extends FileProvider {
    public PdfContentProvider() {
        super(R.xml.file_paths);
    }
}
