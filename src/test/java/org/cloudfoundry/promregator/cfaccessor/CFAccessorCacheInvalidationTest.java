package org.cloudfoundry.promregator.cfaccessor;

import org.cloudfoundry.promregator.JUnitTestUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CFAccessorCacheSpringApplication.class)
@TestPropertySource(locations="../default.properties")
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class CFAccessorCacheInvalidationTest {
	
	/*
	 * Warning! Do not try to merge with CFAccessorCacheTest
	 * These tests here require that we have a clean counting state in
	 * Mockito, which is hard to achieve, if we have the tests merged.
	 * If they are separate, this is quite trivial...
	 */

	@Autowired
	private CFAccessor parentMock;
	
	@Autowired
	private CFAccessorCache subject;
	
	@Before
	public void invalidateCaches() {
		this.subject.invalidateCacheApplications();
		this.subject.invalidateCacheSpace();
		this.subject.invalidateCacheOrg();
	}
	
	@AfterClass
	public static void runCleanup() {
		JUnitTestUtils.cleanUpAll();
	}
	
	@Test
	public void testInvalidateCacheApplications() {
		subject.retrieveApplicationId("dummy1", "dummy2", "dummy3");
		Mockito.verify(this.parentMock, Mockito.times(1)).retrieveApplicationId("dummy1", "dummy2", "dummy3");
		
		subject.invalidateCacheApplications();

		subject.retrieveApplicationId("dummy1", "dummy2", "dummy3");
		Mockito.verify(this.parentMock, Mockito.times(2)).retrieveApplicationId("dummy1", "dummy2", "dummy3");
	}

	@Test
	public void testInvalidateCacheSpace() {
		subject.retrieveSpaceId("dummy1", "dummy2");
		Mockito.verify(this.parentMock, Mockito.times(1)).retrieveSpaceId("dummy1", "dummy2");
		
		subject.invalidateCacheSpace();
		
		subject.retrieveSpaceId("dummy1", "dummy2");
		Mockito.verify(this.parentMock, Mockito.times(2)).retrieveSpaceId("dummy1", "dummy2");
	}

	@Test
	public void testInvalidateCacheOrg() {
		subject.retrieveOrgId("dummy");
		Mockito.verify(this.parentMock, Mockito.times(1)).retrieveOrgId("dummy");
		
		subject.invalidateCacheOrg();
		
		subject.retrieveOrgId("dummy");
		Mockito.verify(this.parentMock, Mockito.times(2)).retrieveOrgId("dummy");
	}

}
