package org.test;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.drools.core.RuleBaseConfiguration;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.KnowledgeBaseFactory;

public class Drools6FromProblemTest {

	private static final String RULE_FILENAME = "defaultKBase/TestFromRules.drl";

	@Test
	public void testReteoo() throws InterruptedException {
		KieSession ksession = initSession(false);
		runTest(ksession);
		ksession.dispose();
	}

	@Test
	public void testPhreak() throws InterruptedException {
		KieSession ksession = initSession(true);
		runTest(ksession);
		ksession.dispose();
	}
	
	private void runTest(KieSession ksession) {
		for (int i = 0; i < 4; i++) {
			ksession.insert(new SimpleFact("id" + i));
		}
		ksession.fireAllRules();
		assertEquals("all events should be in WM", 4, ksession.getFactCount());
		Logger.getLogger(getClass()).debug("End of first insert");

		ksession.insert(new SimpleFact("last"));
		ksession.fireAllRules();
		assertEquals("only one event should be still in WM", 1, ksession.getFactCount());
	}


	protected KieSession initSession(boolean phreakMode) {
		KieServices ks = KieServices.Factory.get();
		KieRepository kr = ks.getRepository();
		KieFileSystem kfs = ks.newKieFileSystem();
		Resource res = ks.getResources().newClassPathResource(RULE_FILENAME);
		kfs.write(res);

		KieBuilder kb = ks.newKieBuilder(kfs);
		kb.buildAll();
		if (kb.getResults().hasMessages()) {
			System.err.println("!!! Problem: " + kb.getResults().toString());
		}

		KieContainer kContainer = ks.newKieContainer(kr.getDefaultReleaseId());

		RuleBaseConfiguration kBaseConf = new RuleBaseConfiguration();
		kBaseConf.setEventProcessingMode(EventProcessingOption.STREAM);
		kBaseConf.setPhreakEnabled(phreakMode);
		KieBase kbase = kContainer.newKieBase(kBaseConf);

		KieSessionConfiguration ksessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
		KieSession ksession = kbase.newKieSession(ksessionConfig, null);

		MyAgendaListener agendaListener = new MyAgendaListener();
		ksession.addEventListener(agendaListener);

		return ksession;
	}
}

