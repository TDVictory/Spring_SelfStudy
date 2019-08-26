package com.TDVictory.imports;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class MyImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        String[] Imports = {"com.TDVictory.imports.Blue","com.TDVictory.imports.Red"};
        return Imports;
    }
}
