package com.gyjian;

import com.itextpdf.text.pdf.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Iterator;

@Data
public class PdfImageExtractor {
    String outputDirectory;

    /**
     * 从 pdf 里提取图片，并保存到指定的目录
     * @param pdfFileName pdf 文件名
     * @param outputDir 图片保存的目录名
     * @throws Exception
     */
    public void getImagesFromPDF(final String pdfFileName, String outputDir) throws Exception {
        setOutputDirectory(outputDir);

        final PdfReader pdf = new PdfReader(pdfFileName);

        final int numberOfPages = pdf.getNumberOfPages();

        for (int i = 1; i <= numberOfPages; i++) {
            final PdfDictionary page = pdf.getPageN(i);
            final PdfDictionary resource = (PdfDictionary) PdfReader.getPdfObject(page.get(PdfName.RESOURCES));
            final PdfDictionary xObject = (PdfDictionary) PdfReader.getPdfObject(resource.get(PdfName.XOBJECT));

            if (xObject != null) {
                for (final Iterator it = xObject.getKeys().iterator(); it.hasNext(); ) {
                    final PdfObject obj = xObject.get((PdfName) it.next());

                    if (obj.isIndirect()) {
                        final PdfDictionary tg = (PdfDictionary) PdfReader.getPdfObject(obj);
                        final PdfName type = (PdfName) PdfReader.getPdfObject(tg.get(PdfName.SUBTYPE));

                        if (PdfName.IMAGE.equals(type)) {
                            //pego os bytes do meu objeto
                            final int XrefIndex = ((PRIndirectReference) obj).getNumber();
                            final PdfObject pdfObj = pdf.getPdfObject(XrefIndex);
                            final PdfStream pdfStrem = (PdfStream) pdfObj;

                            // 把图片字节流保存为文件
                            saveImage(PdfReader.getStreamBytesRaw((PRStream) pdfStrem));
                        }
                    }
                }
            }
        }

        pdf.close();
    }

    private void saveImage(final byte[] image) throws Exception {
        final String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File( outputDirectory, fileName);
        file.getParentFile().mkdirs();

        final BufferedImage buff = ImageIO.read(new ByteArrayInputStream(image));
        if (buff != null) {
            ImageIO.write(buff, "jpg", file);
        }
    }

}
