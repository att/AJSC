/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc.util;

import groovy.util.logging.Slf4j

import org.springframework.beans.BeansException
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationEvent
import org.springframework.stereotype.Component

@Slf4j
@Component('AJSCShutdownBean')
class AJSCShutdownBean implements BeanFactoryPostProcessor  {
	BeanFactory beanFactory = null;

	// Thread setup for shutdown ;-)
	static class LocalThread extends Thread {
		AJSCShutdownBean contained
		int threadCount = 0
		Object threadCountMutex = new Object()
		public LocalThread(AJSCShutdownBean bean) {
			super()
			int currentThreadNum = -1
			synchronized(threadCountMutex) {
				currentThreadNum = ++threadCount
			}
			log.debug("thread name before it is renamed: ${this.name}")
			this.name = "ApplicationShutdownThread-${currentThreadNum}"
			contained = bean
			log.info("Started shutdown thread ${this.name}")
		}
	}
	@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactoryArg) throws BeansException {
		log.info('postProcessBeanFactory method called... hashCode: 0x' + Integer.toHexString(beanFactoryArg.hashCode()))
		this.beanFactory = beanFactoryArg
		Runtime.runtime.addShutdownHook(new AJSCShutdownBean.LocalThread(this) {
					public void run() {
						log.debug('Received a shutdown message from the VM!')
						log.info('Received a shutdown message from the VM!')
						this.contained.shutdownAJSC()
					}
				})
	}
	private void shutdownAJSC() throws Exception {
		if (beanFactory != null) {
			def routeMgmtService = beanFactory.getBean('routeMgmtService')
			if (!routeMgmtService) {
				log.warn('Couldn\'t locate the routeMgmtService... Can\'t call stop method of static member computeService')
				return
			}
			def computeService = routeMgmtService.computeService
			if (!computeService) {
				log.warn('Couldn\'t get the static member computeService from the routeMgmtService... Can\'t stop the computeService')
				return
			}
			log.info('Stopping all contexts registered with the ComputeService...')
			routeMgmtService.computeService.ctxMap.each { key, ctx ->
				log.info("Stopping Camel context for: ${key}. There are currently ${ctx.inflightRepository?.size()} route exchanges in flight.")
				ctx.stop()
				log.info("Camel context stopped. Closing Spring context for: ${key}")
				ctx.applicationContext.close()
				log.info("Spring context closed for ${key}")
			}
		}
	}
}

