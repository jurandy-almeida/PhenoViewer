package phenoviewer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class MainOp {
	public static void main(String[] args) throws IOException {
		ArrayList<File> imageList = new ArrayList<File>();

		//inicio do modo bruteforce de hoje
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_242_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_243_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_244_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_245_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_246_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_247_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_248_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_249_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_250_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_251_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_252_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_253_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_254_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_255_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_256_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_257_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_258_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_259_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_260_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_261_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_262_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_263_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_264_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_265_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_266_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_267_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_268_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_269_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_270_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_271_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_272_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_273_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_274_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_275_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_276_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_277_12.jpg"));
		imageList.add(new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\images\\2011_278_12.jpg"));
		//fim do modo bruteforce de hoje

		File mask = new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\masks\\angiosperma.bmp");

		VisualRhythm teste = new VisualRhythm(imageList, mask);
		ExcGreen teste2 = new ExcGreen(imageList, mask);
		AvgRgb teste3 = new AvgRgb(imageList, mask);

		BufferedImage ritmovisual = teste.process();
		File outputfile = new File("C:\\Users\\Jo�oPedro\\workspace\\Imagens\\masks\\ritm.jpg");
	    ImageIO.write(ritmovisual, "jpg", outputfile);

	    ArrayList<Float> printex = teste2.process();
	    ArrayList<ColorRGB> printrgb = teste3.process();

	    for(int i = 0;i<printex.size();i++)
	    	System.out.println(printex.get(i).toString());
	    for(int i = 0;i<printrgb.size();i++)
	    	System.out.println(printrgb.get(i).toString());

		System.out.println("pronto");
		}
	}
