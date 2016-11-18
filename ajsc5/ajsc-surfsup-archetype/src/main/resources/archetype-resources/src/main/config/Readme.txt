All files located in this src/main/config directory will be copied into the eventual $AJSC_HOME/etc directory.
To utilize SWM Node Variable replacement on ANY file, simply create a template.filename of any file. Utilize 
variables surrounded by double underscores (example: __VARIABLE_NAME__). The template.filename will be copied and 
the variables will be replaced by variables set on the SOA Cloud Node you are installing the service to. 