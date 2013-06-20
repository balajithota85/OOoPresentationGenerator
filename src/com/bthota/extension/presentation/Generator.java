/* ==========================================================
 * Generator.java
 * https://github.com/balajithota85/OOoPresentationGenerator
 * ==========================================================
 * Copyright 2013 balaji thota.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================================================== */

package com.bthota.extension.presentation;

import com.sun.star.awt.Point;
import com.sun.star.awt.Size;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.drawing.XDrawPage;
import com.sun.star.drawing.XDrawPages;
import com.sun.star.drawing.XDrawPagesSupplier;
import com.sun.star.drawing.XShape;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.text.XText;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import java.io.File;

public class Generator {
	public static final String DESKTOP_PACKAGE = "com.sun.star.frame.Desktop";
	public static final String NEW_IMPRESS_FILE = "private:factory/simpress";
	public static final String BLANK_FILE = "_blank";
	public static final String TITLE_TEXT_SHAPE_PACKAGE = "com.sun.star.presentation.TitleTextShape";
	public static final String SUB_TITLE_TEXT_SHAPE_PACKAGE = "com.sun.star.presentation.SubtitleTextShape";
	public static final String GRAPHIC_OBJECT_SHAPE_PACKAGE = "com.sun.star.drawing.GraphicObjectShape";
	public static final String SAMPLE_TITLE = "Title of Presentation";
	public static final String SAMPLE_SUB_TITLE = "This is a sample\n\n\n\n\t\t\t\t\tBy,\n\t\t\t\t\t\t\t\t  Balaji Thota";
	public static final String SAMPLE_INPUT_DIRECTORY = "/home/bthota/Desktop/sample";
	public static final String SAMPLE_OUTPUT_FILE = "/home/bthota/sample.odp";
	
	public static void main(String[] args) {
		try {
			
			String inputDirectory = System.getProperty("inputDirectory");
			String outFileWithPath = System.getProperty("outFileWithPath");;
			String titleOfPresentation = System.getProperty("titleOfPresentation");;
			String subTitleOfPresentation = System.getProperty("subTitleOfPresentation");;
			
			if(inputDirectory == null || inputDirectory.equals("") || outFileWithPath == null || outFileWithPath.equals("")){
				System.out.println("came to null condition");
				return;
			}
			
			titleOfPresentation = (titleOfPresentation == null || titleOfPresentation.equals("")) ? 
					SAMPLE_TITLE : titleOfPresentation;
			
			subTitleOfPresentation = (subTitleOfPresentation == null || subTitleOfPresentation.equals("")) ? 
					SAMPLE_SUB_TITLE : subTitleOfPresentation;
			
			XComponentContext context = Bootstrap.bootstrap();
			XMultiComponentFactory serviceManager = context.getServiceManager();
			Object desktop = serviceManager.createInstanceWithContext(
					DESKTOP_PACKAGE, 
					context);
			XComponentLoader loader = (XComponentLoader) UnoRuntime.queryInterface(
					XComponentLoader.class, 
					desktop);

			PropertyValue[] properties = new PropertyValue[1];
			properties[0] = new PropertyValue();
			properties[0].Name = "Hidden";
			properties[0].Value = new Boolean(true);

			Object document = loader.loadComponentFromURL(
					NEW_IMPRESS_FILE, 
					BLANK_FILE, 
					0, 
					properties);
			
			XMultiServiceFactory serviceFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(
					XMultiServiceFactory.class, 
					document);

			XDrawPagesSupplier pageSupplier = (XDrawPagesSupplier) UnoRuntime.queryInterface(
					XDrawPagesSupplier.class, 
					document);
			
			XDrawPages pages = pageSupplier.getDrawPages();

			for (int i = pages.getCount() - 1; i > 0; --i) {
				pages.remove((XDrawPage) UnoRuntime.queryInterface(
						XDrawPage.class, 
						pages.getByIndex(i)
						));
			}

			if (pages.getCount() == 1) {
				XDrawPage firstPage = (XDrawPage) UnoRuntime.queryInterface(
						XDrawPage.class, 
						pages.getByIndex(0));

				if (firstPage.getCount() != 2) {
					for (int i = firstPage.getCount() - 1; i >= 0; --i) {
						firstPage.remove((XShape) UnoRuntime.queryInterface(
								XShape.class, 
								firstPage.getByIndex(i)));
					}
					XShape titleShape = (XShape) UnoRuntime.queryInterface(
									XShape.class,
									serviceFactory.createInstance(TITLE_TEXT_SHAPE_PACKAGE)
									);
					titleShape.setSize(new Size(25199, 3506));
					titleShape.setPosition(new Point(1400, 837));

					XShape subTitleShape = (XShape) UnoRuntime.queryInterface(
									XShape.class,
									serviceFactory.createInstance(SUB_TITLE_TEXT_SHAPE_PACKAGE)
									);
					subTitleShape.setSize(new Size(24639, 12179));
					subTitleShape.setPosition(new Point(1400, 4914));

					firstPage.add(titleShape);
					firstPage.add(subTitleShape);
				}

				XText title = (XText) UnoRuntime.queryInterface(XText.class,
						firstPage.getByIndex(0));
				XText subTitle = (XText) UnoRuntime.queryInterface(XText.class,
						firstPage.getByIndex(1));
				title.setString(titleOfPresentation);
				subTitle.setString(subTitleOfPresentation);
			}

			File[] files = new File(inputDirectory).listFiles();
			
			for (File file : files) {
				if (file.isFile()) {
					XDrawPage currentPage = pages.insertNewByIndex(pages.getCount());
					XShape currentTitleShape = (XShape) UnoRuntime.queryInterface(
									XShape.class,
									serviceFactory.createInstance(TITLE_TEXT_SHAPE_PACKAGE)
									);
					currentTitleShape.setSize(new Size(25199, 3506));
					currentTitleShape.setPosition(new Point(1400, 837));

					currentPage.add(currentTitleShape);

					XText currentTitle = (XText) UnoRuntime.queryInterface(
							XText.class, 
							currentPage.getByIndex(0)
							);
					currentTitle.setString(file.getName());

					XShape imageShape = (XShape) UnoRuntime.queryInterface(
									XShape.class,
									serviceFactory.createInstance(GRAPHIC_OBJECT_SHAPE_PACKAGE)
									);

					imageShape.setSize(new Size(24639, 12179));
					imageShape.setPosition(new Point(1400, 4914));

					XPropertySet xPropertySet = (XPropertySet) UnoRuntime.queryInterface(
							XPropertySet.class, 
							imageShape);

					xPropertySet.setPropertyValue("GraphicURL", "file://"+ file.getAbsolutePath());
					currentPage.add(imageShape);
				}
			}

			XStorable store = (XStorable) UnoRuntime.queryInterface(XStorable.class, document);
			store.storeAsURL("file://"+outFileWithPath, null);
		} catch (BootstrapException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}