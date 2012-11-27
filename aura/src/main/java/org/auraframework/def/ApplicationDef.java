/*
 * Copyright (C) 2012 salesforce.com, inc.
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
package org.auraframework.def;

import java.util.Set;

import org.auraframework.system.AuraContext.Access;
import org.auraframework.throwable.quickfix.QuickFixException;


/**
 */
public interface ApplicationDef extends BaseComponentDef {
    @Override
    DefDescriptor<ApplicationDef> getDescriptor();
    @Override
    DefDescriptor<ApplicationDef> getExtendsDescriptor();

    Set<String> getPreloads();

    DefDescriptor<LayoutsDef> getLayoutsDefDescriptor();

    Access getAccess();

    DefDescriptor<EventDef> getLocationChangeEventDescriptor() throws QuickFixException;

    DefDescriptor<SecurityProviderDef> getSecurityProviderDefDescriptor() throws QuickFixException;

    Boolean isAppcacheEnabled() throws QuickFixException;

    Boolean isOnePageApp() throws QuickFixException;
    
}
