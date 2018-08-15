import jenkins.model.*
import hudson.util.Secret

def env = System.getenv()

Jenkins j = Jenkins.getInstance()
Boolean save = false

def anchore = j.getDescriptorByType(com.anchore.jenkins.plugins.anchore.AnchoreBuilder.DescriptorImpl)

Boolean debug = env['ANCHORE_DEBUG'] ?: false
String engine_mode = env['ANCHORE_MODE'] ?: 'anchoreengine'
String engine_url = env['ANCHORE_URL'] ?: 'http://anchore-engine:8228/v1'
String engine_user = env['ANCHORE_USER'] ?: 'admin'
String engine_pass = env['ANCHORE_PASS'] ?: 'foobar'
Boolean engine_verify = env['ANCHORE_VERIFY'] ?: true

if(anchore.debug != debug) {
    println "Updating Anchore Debug to: ${debug}"
    anchore.debug = debug
    save = true
}
if(anchore.enginemode != engine_mode) {
    println("Updating Anchore Engine Mode to ${engine_mode}")
    anchore.enginemode = engine_mode
    save = true
}
if(anchore.engineurl != engine_url) {
    println("Updating Anchore Engine URL to ${engine_url}")
    anchore.engineurl = engine_url
    save = true
}
if(anchore.engineuser != engine_user) {
    println("Updating Anchore Engine User to ${engine_user}")
    anchore.engineuser = engine_user
    save = true
}
if(anchore.enginepass.toString() != engine_pass) {
    println("Updating Anchore Engine Password")
    anchore.enginepass = Secret.fromString(engine_pass)
    save = true
}
if(anchore.engineverify != engine_verify) {
    println("Updating Anchore Engine Verify to ${engine_verify}")
    anchore.engineverify = engine_verify
    save = true
}

//save configuration to disk
if(save) {
    anchore.save()
}
else {
    println 'Nothing changed.  Anchore settings already configured.'
}
