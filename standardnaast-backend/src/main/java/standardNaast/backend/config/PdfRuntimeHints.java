package standardNaast.backend.config;

import com.itextpdf.text.ImgCCITT;
import com.itextpdf.text.ImgRaw;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class PdfRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.resources().registerPattern("pdf/**");
        hints.resources().registerPattern("com/itextpdf/text/pdf/fonts/**");
        hints.resources().registerPattern("com/itextpdf/text/l10n/error/**");
        // iText clones PNG images reflectively when adding cells to the report table.
        hints.reflection().registerType(ImgRaw.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
        hints.reflection().registerType(ImgCCITT.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
    }
}
