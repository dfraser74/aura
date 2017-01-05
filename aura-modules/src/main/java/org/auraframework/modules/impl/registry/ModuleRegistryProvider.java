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
package org.auraframework.modules.impl.registry;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.auraframework.adapter.ConfigAdapter;
import org.auraframework.adapter.RegistryAdapter;
import org.auraframework.annotations.Annotations.ServiceComponent;
import org.auraframework.def.DefDescriptor;
import org.auraframework.def.DefDescriptor.DefType;
import org.auraframework.impl.source.SourceFactory;
import org.auraframework.impl.source.file.FileSourceLoader;
import org.auraframework.impl.system.CompilingDefRegistry;
import org.auraframework.modules.source.ModuleLocationAdapter;
import org.auraframework.impl.parser.ParserFactory;
import org.auraframework.system.AuraContext.Authentication;
import org.auraframework.system.AuraContext.Mode;
import org.auraframework.system.DefRegistry;
import org.auraframework.system.SourceListener;
import org.auraframework.system.SourceLoader;
import org.auraframework.util.FileMonitor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Provides DefRegistry for Aura modules
 */
@ServiceComponent
public class ModuleRegistryProvider implements RegistryAdapter, SourceListener {

    private static final Set<String> PREFIXES = ImmutableSet.of(
            DefDescriptor.MARKUP_PREFIX
    );

    private static final Set<DefType> DEF_TYPES = EnumSet.of(
            DefType.MODULE
    );

    @Inject
    private FileMonitor fileMonitor;

    @Inject
    private ConfigAdapter configAdapter;

    @Inject
    private ParserFactory parserFactory;

    @Inject
    private List<ModuleLocationAdapter> locationAdapters;

    private Map<ModuleLocationAdapter, DefRegistry> locationMap = Maps.newConcurrentMap();

    @Override
    public DefRegistry[] getRegistries(Mode mode, Authentication access, Set<SourceLoader> extraLoaders) {
        List<DefRegistry> registries = Lists.newArrayList();
        List<SourceLoader> moduleLoaders = Lists.newArrayList();

        for(ModuleLocationAdapter location : locationAdapters) {
            File modules = location.getSourceDir();
            if (modules != null && modules.canRead() && modules.canExecute() && modules.isDirectory()) {
                FileSourceLoader fsl = new FileSourceLoader(modules, fileMonitor);
                moduleLoaders.add(fsl);
                CompilingDefRegistry defRegistry = new CompilingDefRegistry(fsl, PREFIXES, DEF_TYPES, parserFactory);
                registries.add(defRegistry);

                locationMap.putIfAbsent(location, defRegistry);
            }
        }

        if (moduleLoaders.size() > 0) {
            // ensure file based module namespaces are cached
            new SourceFactory(moduleLoaders, configAdapter);
        }

        return registries.toArray(new DefRegistry[registries.size()]);
    }

    @PostConstruct
    public void init() {
        if (fileMonitor != null) {
            fileMonitor.subscribeToChangeNotification(this);
        }
    }

    @Override
    public void onSourceChanged(DefDescriptor<?> source, SourceMonitorEvent event, String filePath) {
        synchronized (this) {
            if (filePath != null) {
                try {
                    File changedFile = new File(filePath);
                    String canonicalPath = changedFile.getCanonicalPath();
                    for (ModuleLocationAdapter mla : locationMap.keySet()) {
                        String moduleLocationPath = mla.getSourceDir().getCanonicalPath();
                        if (canonicalPath.startsWith(moduleLocationPath)) {
                            DefRegistry registry = locationMap.get(mla);
                            registry.reset();
                        }
                    }
                } catch (IOException ignored) {
                    // skip
                }
            }
        }
    }
}
