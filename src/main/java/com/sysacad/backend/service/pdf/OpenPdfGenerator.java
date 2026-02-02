package com.sysacad.backend.service.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sysacad.backend.dto.alumno.AlumnoCertificadoDTO;
import org.springframework.stereotype.Component;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.TextStyle;
import java.util.Locale;

@Component
public class OpenPdfGenerator implements IPdfGenerator {

    @Override
    public byte[] generarCertificado(AlumnoCertificadoDTO datos) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);

            document.open();

            // A. ENCABEZADO
            agregarEncabezado(document);

            // B. CUERPO
            agregarCuerpo(document, datos);

            // C. PIE DE PAGINA
            agregarPieDePagina(document, datos);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF del certificado: " + e.getMessage(), e);
        }
    }

    private void agregarEncabezado(Document document) throws DocumentException, IOException {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1, 1}); // 50% - 50%

        // Logo Izquierdo (Ministerio)
        Image logoMin = cargarImagen("static/img/logo_ministerio.png"); // Intencional: static folder
        // Si el usuario puso en src/main/resources/img, Spring carga con "img/..."
        if (logoMin == null) logoMin = cargarImagen("img/logo_ministerio.png");
        
        PdfPCell cellLeft = new PdfPCell();
        cellLeft.setBorder(Rectangle.NO_BORDER);
        cellLeft.setHorizontalAlignment(Element.ALIGN_LEFT);
        if (logoMin != null) {
            logoMin.scaleToFit(150, 60);
            cellLeft.addElement(logoMin);
        }
        cellLeft.addElement(new Paragraph("República Argentina", FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD)));
        cellLeft.addElement(new Paragraph("Ministerio de Capital Humano", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));

        // Logo Derecho (UTN)
         Image logoUtn = cargarImagen("static/img/logo_utn.png");
         if (logoUtn == null) logoUtn = cargarImagen("img/logo_utn.png");

        PdfPCell cellRight = new PdfPCell();
        cellRight.setBorder(Rectangle.NO_BORDER);
        cellRight.setHorizontalAlignment(Element.ALIGN_RIGHT);
        if (logoUtn != null) {
             logoUtn.scaleToFit(150, 60);
             // Alineación de imagen en celda requiere trucos o wrapping, simple addElement lo pone abajo
             // Usamos tabla anidada o setAlignment en image
             logoUtn.setAlignment(Image.RIGHT);
            cellRight.addElement(logoUtn);
        }
        Paragraph pUtn = new Paragraph("Universidad Tecnológica Nacional", FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD));
        pUtn.setAlignment(Element.ALIGN_RIGHT);
        cellRight.addElement(pUtn);
        
        Paragraph pFac = new Paragraph("Facultad Regional Rosario", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9));
        pFac.setAlignment(Element.ALIGN_RIGHT);
        cellRight.addElement(pFac);

        headerTable.addCell(cellLeft);
        headerTable.addCell(cellRight);
        
        document.add(headerTable);
        document.add(new Paragraph("\n\n")); // Espacio
    }

    private void agregarCuerpo(Document document, AlumnoCertificadoDTO datos) throws DocumentException {
        Font fontCuerpo = FontFactory.getFont(FontFactory.TIMES, 12);
        
        String texto = String.format(
            "Por la presente se hace constar que %s - DNI: %s - LEGAJO N° %s, es alumno Regular de %s que se dicta en la %s de la Universidad Tecnológica Nacional.",
            datos.nombreCompleto(),
            datos.dni(),
            datos.legajo(),
            datos.carrera(),
            datos.facultad()
        );

        Paragraph p = new Paragraph(texto, fontCuerpo);
        p.setAlignment(Element.ALIGN_JUSTIFIED);
        p.setLeading(20f); // Interlineado
        p.setFirstLineIndent(30f);
        
        document.add(p);
        document.add(new Paragraph("\n"));
    }

    private void agregarPieDePagina(Document document, AlumnoCertificadoDTO datos) throws DocumentException {
        Font fontPie = FontFactory.getFont(FontFactory.TIMES, 12);

        String mes = datos.fechaEmision().getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        String texto = String.format(
            "A solicitud del interesado y a los fines de ser presentado ante quien corresponda, se le extiende el presente certificado, sin enmiendas ni raspaduras, en Rosario el %d de %s de %d.-",
            datos.fechaEmision().getDayOfMonth(),
            mes,
            datos.fechaEmision().getYear()
        );

        Paragraph p = new Paragraph(texto, fontPie);
        p.setAlignment(Element.ALIGN_JUSTIFIED);
        p.setLeading(20f);
        document.add(p);
        
        document.add(new Paragraph("\n\n\n\n"));
        
        Paragraph direccion = new Paragraph("E ZEBALLOS 1341 - ROSARIO", FontFactory.getFont(FontFactory.TIMES_ITALIC, 10));
        direccion.setAlignment(Element.ALIGN_CENTER);
        document.add(direccion);
    }
    
    private Image cargarImagen(String path) {
        try {
            // Intenta cargar desde classpath
            ClassPathResource res = new ClassPathResource(path);
            if (res.exists()) {
                return Image.getInstance(res.getURL());
            }
            return null;
        } catch (Exception e) {
             // Log error silently or return null
             return null;
        }
    }
}
