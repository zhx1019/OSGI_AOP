/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package com.codeaffine.osgi.services.aop.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.codeaffine.osgi.services.aop.JoinPoint;

class ProxyInvocationHandler implements InvocationHandler {
    private final Object service;
    private JoinPointImpl<?> joinPoint;

    ProxyInvocationHandler( Object service ) {
      this.service = service;
    }

    @Override
    public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
      if( joinPoint != null ) {
        joinPoint.excuteBefore( method, args );
      }
      Object result = null;
      try {
        result = method.invoke( service, args );
        if( joinPoint != null ) {
          joinPoint.excuteAfter( method, args );
        }        
      } catch( Throwable error ) {
        joinPoint.executeOnError( method, args, error );
        
        if( error instanceof RuntimeException ) {
          throw ( RuntimeException )error;
        } else if( error instanceof InvocationTargetException ) {
          InvocationTargetException ite = ( InvocationTargetException )error;
          if( ite.getCause() != null ) {
            throw ite.getCause();
          } 
          throw error;
        } else {
          throw error;
        }
      }
      return result;
    }

    public void setJoinPoint( JoinPoint<?> joinPoint ) {
      this.joinPoint = ( JoinPointImpl<?> )joinPoint;
    }
  }