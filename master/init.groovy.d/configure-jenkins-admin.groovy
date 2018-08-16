import jenkins.model.*
import hudson.security.*

Jenkins j = Jenkins.getInstance()
Boolean save = false

if (j.getSecurityRealm().getClass() == SecurityRealm.None) {
    println("Hudson Private Security Realm enabled")
    j.setSecurityRealm(new HudsonPrivateSecurityRealm(false))
    save = true
}

if (j.getAuthorizationStrategy().getClass() == AuthorizationStrategy.Unsecured) {
    println("Project Matrix Authorization Strategy enabled")
    j.setAuthorizationStrategy(new ProjectMatrixAuthorizationStrategy())
    save = true
}

def adminUsername = System.getenv('JENKINS_ADMIN_USERNAME') ?: 'admin'
def adminPassword = System.getenv('JENKINS_ADMIN_PASSWORD') ?: 'password'
if (j.getSecurityRealm().metaClass.respondsTo(j.getSecurityRealm(), 'createAccount', String, String)) {
  	def currentUsers = j.getSecurityRealm().getAllUsers().collect { it.getId() }
    if (!(adminUsername in currentUsers)) {
        println("Created administrative user ${adminUsername}")
        def user = j.getSecurityRealm().createAccount(adminUsername, adminPassword)
        user.save
    }
}
if (!(j.getAuthorizationStrategy().hasPermission(adminUsername, Jenkins.ADMINISTER))) {
    j.getAuthorizationStrategy().add(Jenkins.ADMINISTER, adminUsername)
    save = true
}

//save configuration to disk
if(save) {
    j.save()
}
else {
    println 'Nothing changed.  Jenkins Global Security settings already configured.'
}
