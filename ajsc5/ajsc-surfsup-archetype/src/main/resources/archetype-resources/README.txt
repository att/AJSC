INSTRUCTIONS TO UPDATE CONFIGURATIONS IN THIS PROJECT
=====================================================
To update build-time settings or properties, edit the pom.xml.

To update runtime settings or properties, you can edit defaults or 
make variables required during installation by editing the <variableDescriptors> 
section of the src/main/swm/descriptor.xml.

If you want to add a new variable or property that can be set in a template 
during installation, add a placeholder in the template file using the following 
format (double underscores at the start and end with your variable name):

	__MYVARNAME__
	
If you want to create a new template file, add a file in src/main/scripts or 
src/main/config but place "template." as the start of the file name.  All
files starting with "template." will be treated as templates at installation
time.  Variables will be replaced with actual values provided during installation
and the file will be renamed without the "template." prefix.