/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc

import java.text.SimpleDateFormat
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import groovy.io.FileType
import java.io.File;


class AssemblyService {

	private static AssemblyService instance

	public static AssemblyService getInstance() {

		if(instance == null) {
			instance = new AssemblyService()
		}
		return instance
	}

	static void getBasedir(String basedir,String distFilesRoot){
		def BASE_DIR = basedir+File.separator+"target"+File.separator+"versioned-ajsc";

		def targetDir = basedir+File.separator+"target"

		getInstance().serviceZips(BASE_DIR,targetDir);
		//getInstance().copyEnvConfigurationEtc(basedir,distFilesRoot)
	}

	def serviceZips(baseDir,targetDir){
		def zipname;
		def namespaces = [];
		def dir = new File(baseDir);

		dir.eachFile FileType.DIRECTORIES, { namespaces << it.path }

		try{
			def ant = new AntBuilder()
			namespaces.each {
				zipname = new File(it).name;
				ant.zip(
						destfile: "${targetDir}"+File.separator+"${zipname}.zip",
						basedir: "${it}",
						level:9,
						)
			}
			propertyZips(baseDir,targetDir);
		}
		catch(Exception ex){
			println all
		}
	}

	def propertyZips(baseDir,targetDir){

		def namespaces = [];
		def dir = new File(baseDir);

		dir.eachFile FileType.DIRECTORIES, { namespaces << it.path }

		try{
			def zipname;
			def ant = new AntBuilder()
			def parent;
			namespaces.each {
				zipname = new File(it).name;
				ant.zip(
						destfile: "${targetDir}"+File.separator+"${zipname}_props.zip",
						basedir: "${it}",
						level:9,
						includes:"**"+File.separator+"props"+File.separator+"/*",
						)
			}
		}
		catch(Exception ex){
		}
	}
	
//	def copyEnvConfigurationEtc(String basedir,String distFilesRoot){
//		try{
//			def ant = new AntBuilder()
//			
//			if(System.getProperty("AJSC_ENV") !=null){
//				def ajscEnv = System.getProperty("AJSC_ENV")
//				if (ajscEnv.equalsIgnoreCase("DEV")){
//					ant.echo("AJSC_ENV is DEV. Copying /DEV properties to /etc directory")
//					ant.copy(todir:basedir+File.separator+"target"+File.separator+"swm"+File.separator+"package"+File.separator+"nix"+File.separator+"dist_files${distFilesRoot}"+File.separator+"etc", overwrite:true) {
//						fileset(dir:basedir+File.separator+"DEV")
//					}
//				}else if(ajscEnv.equalsIgnoreCase("QA")){
//					ant.echo("AJSC_ENV is QA. Copying /QA properties to /etc directory")
//					ant.copy(todir:basedir+File.separator+"target"+File.separator+"swm"+File.separator+"package"+File.separator+"nix"+File.separator+"dist_files${distFilesRoot}"+File.separator+"etc", overwrite:true) {
//						fileset(dir:basedir+File.separator+"QA")
//					}
//				}else if(ajscEnv.equalsIgnoreCase("PREPROD")){
//					ant.echo("AJSC_ENV is PREPROD. Copying /PREPROD properties to /etc directory")
//					ant.copy(todir:basedir+File.separator+"target"+File.separator+"swm"+File.separator+"package"+File.separator+"nix"+File.separator+"dist_files${distFilesRoot}"+File.separator+"etc", overwrite:true) {
//						fileset(dir:basedir+File.separator+"PREPROD")
//					}
//				}else if(ajscEnv.equalsIgnoreCase("PROD")){
//					ant.echo("AJSC_ENV is PROD. Copying /PROD properties to /etc directory")
//					ant.copy(todir:basedir+File.separator+"target"+File.separator+"swm"+File.separator+"package"+File.separator+"nix"+File.separator+"dist_files${distFilesRoot}"+File.separator+"etc", overwrite:true) {
//						fileset(dir:basedir+File.separator+"PROD")
//					}
//				}else{
//					ant.echo(message:"INVALID value given for AJSC_ENV:" + ajscEnv + " (Valid values: DEV,QA,PREPROD,PROD)", level:"error")
//				}
//			}else{
//				ant.echo(message:"AJSC_ENV not set. Using Default Setting: Configuration files have been copied from src/main/config to /etc directory")
//			}
//		}catch(e){
//			e.printStackTrace()
//		}
//	}
}