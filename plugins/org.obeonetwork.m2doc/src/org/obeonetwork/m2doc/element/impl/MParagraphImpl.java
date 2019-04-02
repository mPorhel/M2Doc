/*******************************************************************************
 *  Copyright (c) 2018 Obeo. 
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *   
 *   Contributors:
 *       Obeo - initial API and implementation
 *  
 *******************************************************************************/
package org.obeonetwork.m2doc.element.impl;

import org.obeonetwork.m2doc.element.MElement;
import org.obeonetwork.m2doc.element.MParagraph;

/**
 * A paragraph that can be inserted in a word document.
 * 
 * @author <a href="mailto:yvan.lussaud@obeo.fr">Yvan Lussaud</a>
 */
public class MParagraphImpl extends AbstractMElementContainer implements MParagraph {
    /**
     * The style name.
     */
    private String styleName;

    /**
     * Constructor.
     * 
     * @param contents
     *            the contents
     * @param styleName
     *            the style name
     */
    public MParagraphImpl(MElement contents, String styleName) {
        super(contents);
        this.styleName = styleName;
    }

    @Override
    public String getStyleName() {
        return styleName;
    }

    @Override
    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

}
