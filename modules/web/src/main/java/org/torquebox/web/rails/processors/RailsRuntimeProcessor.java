/*
 * Copyright 2008-2012 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.torquebox.web.rails.processors;

import java.io.File;

import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.logging.Logger;
import org.torquebox.core.app.RubyAppMetaData;
import org.torquebox.core.runtime.BundlerAwareRuntimePreparer;
import org.torquebox.core.runtime.RubyLoadPathMetaData;
import org.torquebox.core.runtime.RubyRuntimeMetaData;
import org.torquebox.core.runtime.RuntimeInitializer;
import org.torquebox.core.runtime.RuntimePreparer;
import org.torquebox.web.rack.RackMetaData;
import org.torquebox.web.rails.RailsMetaData;
import org.torquebox.web.rails.RailsRuntimeInitializer;

public class RailsRuntimeProcessor implements DeploymentUnitProcessor {

    @Override
    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        DeploymentUnit unit = phaseContext.getDeploymentUnit();
        RubyAppMetaData rubyAppMetaData = unit.getAttachment( RubyAppMetaData.ATTACHMENT_KEY );
        RackMetaData rackAppMetaData = unit.getAttachment( RackMetaData.ATTACHMENT_KEY );
        RailsMetaData railsAppMetaData = unit.getAttachment( RailsMetaData.ATTACHMENT_KEY );

        if ( rubyAppMetaData == null || rackAppMetaData == null || railsAppMetaData == null ) {
            return;
        }
        
        RubyRuntimeMetaData runtimeMetaData = unit.getAttachment(  RubyRuntimeMetaData.ATTACHMENT_KEY );
        
        if ( runtimeMetaData != null && runtimeMetaData.getRuntimeType() != null ) {
            return;
        }
        
        if (runtimeMetaData == null) {
            runtimeMetaData = new RubyRuntimeMetaData();
            unit.putAttachment(  RubyRuntimeMetaData.ATTACHMENT_KEY, runtimeMetaData );
        }
        
        runtimeMetaData.setBaseDir( rubyAppMetaData.getRoot() );
        runtimeMetaData.setEnvironment( rubyAppMetaData.getEnvironmentVariables() );
        runtimeMetaData.setRuntimeType( RubyRuntimeMetaData.RuntimeType.RAILS );
        runtimeMetaData.appendLoadPath( new RubyLoadPathMetaData( rubyAppMetaData.getRoot() ) );
        runtimeMetaData.appendLoadPath( new RubyLoadPathMetaData( new File( rubyAppMetaData.getRoot(), "lib" ) ) );
        
        RuntimeInitializer initializer = new RailsRuntimeInitializer( rubyAppMetaData, rackAppMetaData, railsAppMetaData );
        RuntimePreparer preparer = new BundlerAwareRuntimePreparer( rubyAppMetaData );
        runtimeMetaData.setRuntimeInitializer( initializer );
        runtimeMetaData.setRuntimePreparer( preparer );
    }

    @Override
    public void undeploy(DeploymentUnit unit) {

    }
    
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger( "org.torquebox.web.rails" );

}
