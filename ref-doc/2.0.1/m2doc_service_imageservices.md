---
layout: article
title: ImageServices
 subtitle: 2.0.1
 relativePath: ../..
---

<!--
/********************************************************************************
** Copyright (c) 2015 Obeo.
** All rights reserved. This program and the accompanying materials
** are made available under the terms of the Eclipse Public License v1.0
** which accompanies this distribution, and is available at
** http://www.eclipse.org/legal/epl-v10.html
**
** Contributors:
**    Stephane Begaudeau (Obeo) - initial API and implementation
*********************************************************************************/
-->

# ImageServices

Services available for Images. See [document examples](https://github.com/ObeoNetwork/M2Doc/tree/2.0.1/tests/org.obeonetwork.m2doc.tests/resources/imageServices).

* TOC
{:toc}

## String.asImage(String) : org.obeonetwork.m2doc.element.MImage

Convert a String representing an URI to an Image and serialize it in the given format.

### Parameter

* **uri**: The Image uri, it can be relative to the template

### Example

* 'image.png'.asImage('jpg')
  * insert the image 'image.jpg'

## String.asImage() : org.obeonetwork.m2doc.element.MImage

Convert a String representing an URI to an Image.

### Parameter

* **uri**: The Image uri, it can be relative to the template

### Example

* 'image.png'.asImage()
  * insert the image 'image.png'

## org.obeonetwork.m2doc.element.MImage.fit(Integer, Integer) : org.obeonetwork.m2doc.element.MImage

Fits the Image in the given the given rectangle width and height.

### Parameter

* **image**: The Image
* **width**: The width to fit
* **height**: The height to fit

### Example

* myImage.fit(200, 300)
  * will fit the image in a rectangle (width=200, height=300)

## org.obeonetwork.m2doc.element.MImage.getHeight() : Integer

Gets the height of the image.

### Parameter

* **image**: The Image

### Example

* myImage.getHeight()
  * 300

## org.obeonetwork.m2doc.element.MImage.getWidth() : Integer

Gets the width of the image.

### Parameter

* **image**: The Image

### Example

* myImage.getWidth()
  * 300

## org.obeonetwork.m2doc.element.MImage.setConserveRatio(Boolean) : org.obeonetwork.m2doc.element.MImage

Sets the conserve ratio of the image.

### Parameter

* **image**: The Image
* **conserve**: A Boolean

### Example

* myImage.setConserveRatio(false)
  * set the conserve ratio to false

## org.obeonetwork.m2doc.element.MImage.setHeight(Integer) : org.obeonetwork.m2doc.element.MImage

Sets the height of the image.

### Parameter

* **image**: The Image
* **height**: The height

### Example

* myImage.setHeight(300)
  * set the height to 300

## org.obeonetwork.m2doc.element.MImage.setWidth(Integer) : org.obeonetwork.m2doc.element.MImage

Sets the width of the image.

### Parameter

* **image**: The Image
* **width**: The width

### Example

* myImage.setWidth(300)
  * set the witdh to 300


