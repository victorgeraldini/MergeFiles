package com.teste;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	//List that will be used
    	List<Image> imageList = null;
    	
    	// File representing the folder that you select using a FileChooser
        final File dir = new File("C:\\Users\\luisb\\Desktop\\Images");

        // array of supported extensions
        final String[] EXTENSIONS = new String[]{
            "gif", "png", "bmp", "jpg", "jpeg", "JPG"
        };
        
        // filter to identify images based on their extensions
        final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

            public boolean accept(final File dir, final String name) {
                for (final String ext : EXTENSIONS) {
                    if (name.endsWith("." + ext)) {
                        return (true);
                    }
                }
                return (false);
            }
        };
        
        removeEqualQrCodeImage(dir, IMAGE_FILTER);
        
        
        
        
//        List<String> loteNames = hasFrontBackSubdirectory(dir);
//        
//        for(String lote : loteNames)
//        {
//        	createPDF(lote, IMAGE_FILTER);
//        }	
        
        
        
//        
//        //Run through the list, dividing each image to a directory according to the document it belongs
//        try {
//			divideImages(imageList, dir);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
     
        
    }
    
    //Method that creates a List with all the images of a given directory
    public static List<Image> readImages(File dir, FilenameFilter filter)
    {
    	List<Image> imageList = null;
    	
    	//Make sure dir is a directory
        if (dir.isDirectory()) 
        {
        	//If it is a valid directory, read all the image and include them in a list
        	imageList = new ArrayList<Image>();
        	
            for (final File file : dir.listFiles(filter)) 
            {
            	//Add element to list
            	Image image = new Image(file.getPath());
            	imageList.add(image);
            }
        }
        else
        {
        	//If not a directory, warn the user about it
        	System.err.println("ERRO: " + dir.getPath() + " is not a valid directory! ");
        }
        
		return imageList;
    }
    
    //Method that divides the images in a given List<Image> to diferent directories inside a root directory
    /*
     * TEM QUE CHECAR SE A VERIFICAÇÃO DE QRCODE É FEITA CORRETAMENTE
     */
    public static void divideImages(List<Image> imageList, File rootDirectory) throws IOException
    {
    	//make sure rootDirectory is valid
    	if(!rootDirectory.isDirectory())
    	{
    		System.err.println("ERRO: " + rootDirectory.getPath() + " is not a valid directory! ");
    	}
    	else
    	{
    		//Directory where images are currently being saved
    		File currentDirectory = rootDirectory;
    		
    		//New directory to be created
    		Path newDirectory;
    		
    		//For each image into the imageList
    		int iterator = 1;
    		int halfList = (imageList.size()/2) + 1; //Value of half the list plus 1
    		for(final Image image : imageList)
    		{
    			//See if the image has a QRCode (use method 'hasQRCode() in class Image)
    			if(image.hasQRCode())
    			{
    				
    				//If the image has a QRCode, check if the image position in the list is <= n/2 + 1
    				if(iterator < (imageList.size()/2) + 1)
    				{
	        			//If yes, create a new directory to insert the image
	        			newDirectory = Paths.get(rootDirectory.getPath() + File.separator + image.getNameWithoutExtension());
	        			Files.createDirectories(newDirectory);
    				}
    				else
    				{
    					//Otherwise, the currentDirectory will be the same directory of element of index (currentImageIndex - imageList.size()/2)
    					newDirectory = Paths.get(rootDirectory.getPath() + File.separator + imageList.get(iterator - halfList).getNameWithoutExtension());
//    					System.out.println("Imagem: " + image.getName());
//    					System.out.println("Diretorio: " + newDirectory.toString());
//    					System.out.println("Iterator - HalfList: " + (iterator-halfList));
    					
    				}
    				currentDirectory = newDirectory.toFile();
    			}	
    			
    			//Necessarily, the number of images will be even and the (n/2 + 1) element of the list will be
				//Of the same document of element (1)
    			
    			//Create "Front" and "Back" directories if necessary
    			if(iterator < halfList)
    			{
    				newDirectory = Paths.get(currentDirectory.getPath() + File.separator + "FRONT");
    				currentDirectory = newDirectory.toFile();
    				if(!currentDirectory.exists())
    				{
    					Files.createDirectories(newDirectory);
    				}
    			}
    			else
    			{
    				newDirectory = Paths.get(currentDirectory.getPath() + File.separator + "BACK");
    				currentDirectory = newDirectory.toFile();
    				if(!currentDirectory.exists())
    				{
    					Files.createDirectories(newDirectory);
    				}
    			}
    				image.writeImage((currentDirectory.getPath() + File.separator +  image.getNameWithoutExtension()), "jpg");
        			//System.out.println("A imagem " + image.getName() + " foi salva no diretório " + currentDirectory.getPath());
    			iterator++;
    		}
    	}
    }
    
    /*
     * Method that creates a PDF file with the images in a given directory in the format
     * c:/directory/front
     * c:/directory/back
     * The first page on the PDF will be the first image of /front. The second page, the first image
     * of /back. The third, second image of /front. The forth, second image of /back. And so on.
     * 
     * The PDF file is created at c:/directory.
     */
    
    public static void createPDF(String directory, FilenameFilter filter)
    {
    	//If directory does not exist OR is not a valid directory, return a error message
    	if(!new File(directory).exists() || !new File(directory).isDirectory())
    	{
    		System.err.println("ERROR: Directory " + directory+ " doesnt exists or is not valid!");
    		//Exit method
    		return;
    	}
    	
		// Reading front and back images
		List<Image> frontImages = readImages(new File(directory + File.separator + "FRONT"), filter);
		List<Image> backImages = readImages(new File(directory + File.separator + "BACK"), filter);
		
		List<Image> finalList = orderImages(frontImages, backImages); // Working

		// If final list isn't empty, create new pdf document with its elements
		if (finalList.isEmpty()) {
			System.err.println(
					"WARNING: Not possible to create PDF file for " + directory + " : No archives to be written.");
			return;
		}

		Document pdfDocument = new Document();
		try {
			PdfWriter.getInstance(pdfDocument, new FileOutputStream(directory + File.separator + "finalPDF"));
			pdfDocument.open();

			for (Image image : finalList) {
				com.itextpdf.text.Image newImage = com.itextpdf.text.Image.getInstance(image.getDirectory());
				pdfDocument.add(newImage);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		pdfDocument.close();
    }
    
    /*
     * Method to correctly order the images
     */
    
    public static List<Image> orderImages(List<Image> frontImages, List<Image> backImages)
    {
    	List<Image> finalList = new ArrayList<Image>();
    	
    	if(frontImages.isEmpty() || backImages.isEmpty())
    	{
    		return finalList;
    	}
    	
    	for(int i = 0; i < frontImages.size(); i++)
    	{
    		finalList.add(frontImages.get(i));
    		
    		if(i < backImages.size())
    		{
    			finalList.add(backImages.get(i));
    		}
    	}
    	
		return finalList;
	}
    
    /*
     * Method to list all subdirectories of a given directory that have subfolders named "FRONT" and "BACK"
     */
    public static List<String> hasFrontBackSubdirectory(File dir)
    {
    	//Get all subdirectories names and insert them into a list (these subdirectories will be the lote names) in case these subdirectories
    	//Have other subdirectories named "FRONT" and "BACK"
    	String[] loteNames = dir.list();
    	List<String> filteredLoteNames = new ArrayList<String>();
    	
    	for(String name : loteNames)
    	{
    		String lotePath = dir.toString() + File.separator + name;
    		
    		//Chek if it is a valid directory
    		if(new File(lotePath).isDirectory())
    		{
    			//Check if parents has child "FRONT" and "BACK"
    			if(new File(lotePath + File.separator + "FRONT").exists() && new File(lotePath + File.separator + "BACK").exists())
    			{
    				filteredLoteNames.add(lotePath);
    			}
    			else
    				System.err.println("WARNING: folder " + lotePath + " does not has subfolders named 'FRONT' and/or 'BACK'");
    		}
    	}
    	
    	return filteredLoteNames;
    }
    
    /*
     * Method to check if there are two or more images in the same folder with the same QRCode. In case there are, this method will remove one of the images.
     * 
     * ESTÁ DELETANDO APENAS UM ARQUIVO POR VEZ, MAS A COMPARAÇÃO DOS QRCODES É FEITA CORRETAMENTE
     */
    
    public static void removeEqualQrCodeImage(File dir, FilenameFilter filter)
    {
    	List<Image> directoryImages = readImages(dir, filter);
    	List<Image> imagesWithQrCode = new ArrayList<Image>();
    	Set<Image> imagesToBeDeleted = new HashSet<Image>();
    	
    	for(Image image : directoryImages)
    	{
    		if(image.hasQRCode())
    		{	
    			imagesWithQrCode.add(image);
    			//System.out.println("A imagem " + image.getName() + " possui QrCode.");
    		}
    	}
    	
    	if(imagesWithQrCode.size() > 1)
    	{
	    	for(int i = 0; i < (imagesWithQrCode.size() - 1); i++)
	    	{
	    		for(int j = (i+1); j < imagesWithQrCode.size(); j++)
	    		{
	    			//If the QRCode of both images are equal, the second one will have to be deleted
	    			String a = imagesWithQrCode.get(i).QRCode().toString();
	    			String b = imagesWithQrCode.get(j).QRCode().toString();
	    			
	    			if(a.equals(b))
	    			{
	    				if(!imagesToBeDeleted.contains(imagesWithQrCode.get(j)))
	    				{
		    				imagesToBeDeleted.add(imagesWithQrCode.get(j));
	    				}
	    			}
	    		}
	    	}
    	}
    	
    	//Delete the images in the set
    	
    	for (Image image : imagesToBeDeleted)
    	{
    			System.out.println(image.getDirectory());
    			File file = new File(image.getDirectory());
    			file.deleteOnExit();
    	}
    }
}
