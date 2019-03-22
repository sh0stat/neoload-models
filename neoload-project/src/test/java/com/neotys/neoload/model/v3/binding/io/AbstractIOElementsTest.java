package com.neotys.neoload.model.v3.binding.io;


import com.neotys.neoload.model.v3.project.Project;
import com.neotys.neoload.model.v3.validation.groups.NeoLoad;
import com.neotys.neoload.model.v3.validation.validator.Validation;
import com.neotys.neoload.model.v3.validation.validator.Validator;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.Assert.*;


abstract class AbstractIOElementsTest {

	private static final Validator VALIDATOR = new Validator();

	protected void read(final String fileName, final Project expectedProject) throws IOException {
		assertNotNull(expectedProject);
		
		read(fileName, "yaml", expectedProject);
		read(fileName, "json", expectedProject);
	}

	protected void read(final String fileName, final String extension, final Project expectedProject) throws IOException {
		final ClassLoader classLoader = getClass().getClassLoader();
		final File file = new File(Objects.requireNonNull(classLoader.getResource(fileName + "." + extension)).getFile());
		
		final IO mapper1 = new IO();
		final Project actualProject1 = mapper1.read(file, Project.class);
		final Validation validation1 = VALIDATOR.validate(actualProject1, NeoLoad.class);
		if (!validation1.isValid()) {
			fail(validation1.getMessage().get());
		}
		assertEquals(expectedProject, actualProject1);
		
		final IO mapper2 = new IO();
		final Project actualProject2 = mapper2.read(new String(Files.readAllBytes(Paths.get(file.toURI()))), Project.class);
		final Validation validation2 = VALIDATOR.validate(actualProject2, NeoLoad.class);
		if (!validation2.isValid()) {
			fail(validation2.getMessage().get());
		}
		assertEquals(expectedProject, actualProject2);
	}
	
	protected void write(final String fileName, final Project expectedProject) throws IOException {
		assertNotNull(expectedProject);
		
		write(fileName, "yaml", expectedProject);
		write(fileName, "json", expectedProject);
	}

	protected void write(final String fileName, final String extension, final Project expectedProject) throws IOException {
		final ClassLoader classLoader = getClass().getClassLoader();
		final File file = new File(Objects.requireNonNull(classLoader.getResource(fileName + "." + extension)).getFile());
		
		final IO mapper = new IO();

		String expectedContent = new String(Files.readAllBytes(Paths.get(file.toURI()))); 
		expectedContent = expectedContent.trim();
		expectedContent = expectedContent.replace("\r\n", "\n");
		
		final File actualFile = File.createTempFile(fileName + "-", "." + extension);
		mapper.write(actualFile, expectedProject);
		String actualContent = new String(Files.readAllBytes(Paths.get(actualFile.toURI())));
		assertNotNull(actualContent);
		actualContent = actualContent.trim();
		actualContent = actualContent.replace("\r\n", "\n");
		assertEquals(expectedContent, actualContent);			
		
		final Project actualProject = mapper.read(actualFile, Project.class);
		assertNotNull(actualProject);
		assertEquals(expectedProject, actualProject);		
	}	
}
