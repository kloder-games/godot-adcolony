def can_build(plat):
	return plat=="android"

def configure(env):
	if (env['platform'] == 'android'):
		env.android_add_maven_repository("url 'https://adcolony.bintray.com/AdColony'")
		env.android_add_dependency("compile 'com.adcolony:sdk:3.0.4'")
		env.android_add_java_dir("android")
		env.android_add_to_manifest("android/AndroidManifestChunk.xml")
