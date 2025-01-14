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
package org.obeonetwork.m2doc.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.eclipse.acceleo.query.ast.Call;
import org.eclipse.acceleo.query.ast.TypeLiteral;
import org.eclipse.acceleo.query.parser.AstValidator;
import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult;
import org.eclipse.acceleo.query.runtime.IReadOnlyQueryEnvironment;
import org.eclipse.acceleo.query.runtime.IService;
import org.eclipse.acceleo.query.runtime.IValidationResult;
import org.eclipse.acceleo.query.runtime.impl.AbstractService;
import org.eclipse.acceleo.query.runtime.impl.ValidationServices;
import org.eclipse.acceleo.query.validation.type.ClassType;
import org.eclipse.acceleo.query.validation.type.IType;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.obeonetwork.m2doc.generator.BookmarkManager;
import org.obeonetwork.m2doc.generator.GenerationResult;
import org.obeonetwork.m2doc.generator.M2DocEvaluator;
import org.obeonetwork.m2doc.generator.UserContentManager;
import org.obeonetwork.m2doc.template.Parameter;
import org.obeonetwork.m2doc.template.Template;

/**
 * Implementation of an {@link org.eclipse.acceleo.query.runtime.IService IService} for
 * {@link Template}.
 * 
 * @author <a href="mailto:yvan.lussaud@obeo.fr">Yvan Lussaud</a>
 */
@SuppressWarnings("restriction")
public class M2DocTemplateService extends AbstractService implements IService {

    /**
     * The maximum depth of a recursive call.
     */
    private static final int MAX_DEPTH = 256;

    /**
     * The {@link Template} to call.
     */
    private Template template;

    /**
     * The {@link BookmarkManager} for evaluation.
     */
    private BookmarkManager bookmarkManager;

    /**
     * The {@link UserContentManager} for evaluation.
     */
    private UserContentManager userContentManager;

    /**
     * The {@link IReadOnlyQueryEnvironment} for evaluation.
     */
    private IReadOnlyQueryEnvironment queryEnvironment;

    /**
     * The {@link Monitor} for evaluation.
     */
    private Monitor monitor;

    /**
     * The {@link #getShortSignature() short signature}.
     */
    private String shortSignature;

    /**
     * The number of parameters.
     */
    private final int numberOfParameters;

    /**
     * The {@link Set} of returned {@link IType}.
     */
    private final Set<IType> returnTypes;

    /**
     * The {@link List} of parameter {@link IType}.
     */
    private List<IType> parameterTypes;

    /**
     * {@link Exception} during initialization.
     */
    private Exception exception;

    /**
     * The serilized {@link XWPFDocument} where the template comes from.
     */
    private final byte[] serializedDocument;

    /**
     * The {@link XWPFDocument} used for generation.
     */
    private XWPFDocument[] documents = new XWPFDocument[MAX_DEPTH];

    /**
     * Tells if we are already in this template call (recursion).
     */
    private int callDepth = -1;

    /**
     * Constructor. For validation only.
     * 
     * @param template
     *            the {@link Template} to call
     * @param queryEnvironment
     *            the {@link IReadOnlyQueryEnvironment} for validation
     */
    public M2DocTemplateService(Template template, IReadOnlyQueryEnvironment queryEnvironment) {
        this(template, null, null, null, queryEnvironment, null);
    }

    /**
     * Constructor. For evaluation.
     * 
     * @param template
     *            the {@link Template} to call
     * @param serializedDocument
     *            the serialized output {@link XWPFDocument}
     * @param bookmarkManager
     *            the {@link BookmarkManager} for evaluation
     * @param userContentManager
     *            the {@link UserContentManager} for evaluation
     * @param queryEnvironment
     *            the {@link IReadOnlyQueryEnvironment} for evaluation
     * @param monitor
     *            the {@link Monitor} for evaluation
     */
    public M2DocTemplateService(Template template, byte[] serializedDocument, BookmarkManager bookmarkManager,
            UserContentManager userContentManager, IReadOnlyQueryEnvironment queryEnvironment, Monitor monitor) {
        this.template = template;
        this.bookmarkManager = bookmarkManager;
        this.userContentManager = userContentManager;
        this.queryEnvironment = queryEnvironment;
        this.monitor = monitor;
        this.shortSignature = computeShortSignature(template);
        this.numberOfParameters = template.getParameters().size();
        this.serializedDocument = serializedDocument;

        this.returnTypes = new HashSet<>();
        returnTypes.add(new ClassType(queryEnvironment, GenerationResult.class));

        this.parameterTypes = new ArrayList<>();
        final AstValidator aqlValidator = new AstValidator(new ValidationServices(queryEnvironment));
        for (Parameter parameter : template.getParameters()) {
            final AstResult type = parameter.getType();
            final IValidationResult validatationResult = aqlValidator.validate(null, type);
            Set<IType> possibleTypes = validatationResult.getPossibleTypes(type.getAst());
            parameterTypes.add(possibleTypes.iterator().next());
        }

        if (serializedDocument != null) {
            try {
                documents[0] = deserializeDocument(serializedDocument);
            } catch (IOException e) {
                exception = e;
            } catch (InvalidFormatException e) {
                exception = e;
            }
        }
    }

    /**
     * Computes the {@link #getShortSignature() short signature}.
     * 
     * @param t
     *            the {@link Template}
     * @return the {@link #getShortSignature() short signature}
     */
    private String computeShortSignature(Template t) {
        final Object[] argumentTypes = new Object[t.getParameters().size()];

        int index = 0;
        for (Parameter parameter : t.getParameters()) {
            argumentTypes[index++] = ((TypeLiteral) parameter.getType().getAst()).getValue();
        }

        return serviceShortSignature(argumentTypes);
    }

    @Override
    public String getName() {
        return template.getName();
    }

    @Override
    public String getShortSignature() {
        return shortSignature;
    }

    @Override
    public String getLongSignature() {
        return EcoreUtil.getURI(template).toString() + " (" + getShortSignature() + ")";
    }

    @Override
    public List<IType> getParameterTypes(IReadOnlyQueryEnvironment env) {
        return parameterTypes;
    }

    @Override
    public int getNumberOfParameters() {
        return numberOfParameters;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public Set<IType> getType(Call call, ValidationServices services, IValidationResult validationResult,
            IReadOnlyQueryEnvironment env, List<IType> argTypes) {
        return returnTypes;
    }

    @Override
    protected GenerationResult internalInvoke(Object[] arguments) throws Exception {
        if (exception != null) {
            throw new IllegalStateException("Initialization problem: " + getShortSignature(), exception);
        }

        final Map<String, Object> variables = new HashMap<>();
        int index = 0;
        for (Parameter parameter : template.getParameters()) {
            variables.put(parameter.getName(), arguments[index++]);
        }
        final M2DocEvaluator evaluator = new M2DocEvaluator(bookmarkManager, userContentManager, queryEnvironment,
                monitor);
        callDepth++;
        try {
            if (documents[callDepth] == null) {
                documents[callDepth] = deserializeDocument(serializedDocument);
            }
            return evaluator.generate(template, variables, documents[callDepth]);
        } finally {
            callDepth--;
        }
    }

    /**
     * Deserializes the {@link XWPFDocument}.
     * 
     * @param document
     *            the serialized document
     * @return the deserialized {@link XWPFDocument}
     * @throws IOException
     *             if the document can't be deserialized
     * @throws InvalidFormatException
     *             if the serialized document is invalid.
     */
    private XWPFDocument deserializeDocument(byte[] document) throws IOException, InvalidFormatException {
        final XWPFDocument res;

        try (InputStream is = new ByteArrayInputStream(document); OPCPackage oPackage = OPCPackage.open(is)) {
            res = new XWPFDocument(oPackage);
        }

        return res;
    }

}
