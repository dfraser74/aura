/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.auraframework.impl.java.renderer;

import org.auraframework.annotations.Annotations.ServiceComponent;
import org.auraframework.def.DefDescriptor;
import org.auraframework.def.RendererDef;
import org.auraframework.impl.DefinitionAccessImpl;
import org.auraframework.impl.java.JavaSourceImpl;
import org.auraframework.system.AuraContext;
import org.auraframework.system.DefinitionFactory;
import org.auraframework.throwable.quickfix.QuickFixException;

@ServiceComponent
public class JavaRendererDefFactory implements DefinitionFactory<JavaSourceImpl<RendererDef>, RendererDef> {
    @Override
    public RendererDef getDefinition(DefDescriptor<RendererDef> descriptor,
            JavaSourceImpl<RendererDef> source) throws QuickFixException {
        JavaRendererDef.Builder builder = new JavaRendererDef.Builder();
        Class<?> rendererClass = source.getJavaClass();

        builder.setDescriptor(descriptor);
        builder.setLocation(rendererClass.getCanonicalName(), -1);
        builder.setRendererClass(rendererClass);
        builder.setAccess(new DefinitionAccessImpl(AuraContext.Access.PUBLIC));
        return builder.build();
    }

    @Override
    public Class<?> getSourceInterface() {
        return JavaSourceImpl.class;
    }

    @Override
    public Class<RendererDef> getDefinitionClass() {
        return RendererDef.class;
    }

    @Override
    public String getMimeType() {
        return JavaSourceImpl.JAVA_MIME_TYPE;
    }
}
