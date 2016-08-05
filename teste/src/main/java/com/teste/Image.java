package com.teste;

import java.awt.image.BufferedImage;
import java.lang.Object;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;



public class Image {

	private BufferedImage image;
	private String name;
	private String directory;
	private String parent;
	
	public Image(String inputPath)
	{
		readImage(inputPath);
	}
	
	private void readImage(String inputPath)
	{
		//Creating type File to read image
		File archive = new File(inputPath);
		
		//Read file as BufferedImage type
		if(archive.exists() && !archive.isDirectory())
		{
			try 
			{
				this.image = ImageIO.read(archive);
				setName(archive.getName());
				setDirectory(archive.getPath());
				setDirectoryWithoutArchiveName(archive.getParent());
				System.out.println("Image " + archive.getName()+ " successfully read!");
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.err.println("Error: The archive does not exist or is a directory!");
		}
	}
	
	public void writeImage(String outputPath, String formatName)
	{
		//Creating type File to write image
		File archive = new File(outputPath);
		
		if(!archive.exists())
		{
			//Writing image to specified outputPath
			try {
				ImageIO.write(this.image, formatName, archive);
				System.out.println("Image successfully written!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
			System.out.println("Image already exists!");
	}
	
	public boolean hasQRCode()
	{
		
		String qrCode = QRCode();
		if(qrCode == null)
		{	
			return false;
		}
		else return true;
		
		
	}
	
	public String QRCode()
	{
		//ADD CODE HERE
		Result qrCodeResult = null;
		
		BinaryBitmap binaryBitmap;
		try {
			binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(new FileInputStream(getDirectory())))));
			qrCodeResult = new MultiFormatReader().decode(binaryBitmap);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotFoundException e) {
			//Caso não seja encontrado QrCode na imagem, retornar null
			return null;
		}
		return qrCodeResult.toString();
	}

	public String getName() {
		return name;
	}
	
	public String getNameWithoutExtension()
	{
		return this.name.substring(0, name.lastIndexOf('.'));
	}

	private void setName(String name) {
		this.name = name;
	}

	public String getDirectory() {
		return directory;
	}
	
	public String getDirectoryWithoutArchiveName()
	{
		return parent;
	}
	
	private void setDirectoryWithoutArchiveName(String path)
	{
		this.parent = path;
	}

	private void setDirectory(String directory) {
		this.directory = directory;
	}
	
	
}
