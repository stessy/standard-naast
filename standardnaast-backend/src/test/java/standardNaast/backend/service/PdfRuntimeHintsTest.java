package standardNaast.backend.service;

import com.itextpdf.text.Image;
import com.itextpdf.text.ImgCCITT;
import com.itextpdf.text.ImgRaw;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.ClassPathResource;
import standardNaast.backend.config.PdfRuntimeHints;

import static org.assertj.core.api.Assertions.assertThat;

class PdfRuntimeHintsTest {

    @Test
    void includesReportAssetsAndITextFontAndDiagnosticResources() {
        RuntimeHints hints = new RuntimeHints();
        new PdfRuntimeHints().registerHints(hints, getClass().getClassLoader());

        for (String path : new String[]{
                "pdf/comicbd.ttf", "pdf/comic.ttf", "pdf/image_cotisations.PNG",
                "com/itextpdf/text/pdf/fonts/Helvetica-Bold.afm",
                "com/itextpdf/text/pdf/fonts/glyphlist.txt",
                "com/itextpdf/text/l10n/error/en.lng"}) {
            assertThat(new ClassPathResource(path).exists()).as(path).isTrue();
            assertThat(RuntimeHintsPredicates.resource().forResource(path).test(hints))
                    .as(path).isTrue();
        }
        assertThat(RuntimeHintsPredicates.resource().forResource("unrelated.txt").test(hints)).isFalse();
    }

    @Test
    void allowsITextToClonePngImages() throws Exception {
        RuntimeHints hints = new RuntimeHints();
        new PdfRuntimeHints().registerHints(hints, getClass().getClassLoader());
        for (Class<?> type : new Class<?>[]{ImgRaw.class, ImgCCITT.class}) {
            assertThat(RuntimeHintsPredicates.reflection()
                    .onConstructorInvocation(type.getDeclaredConstructor(Image.class)).test(hints)).isTrue();
        }
    }

    @Test
    void registersHintsOnPdfService() {
        assertThat(CotisationPdfReportServiceImpl.class.getAnnotation(ImportRuntimeHints.class).value())
                .contains(PdfRuntimeHints.class);
    }
}
